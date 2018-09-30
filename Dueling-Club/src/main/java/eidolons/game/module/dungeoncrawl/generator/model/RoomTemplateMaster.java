package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.PathUtils;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;

import java.awt.*;
import java.util.*;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 2/13/2018.
 * <p>
 * uses custom data format!
 * <p>
 * convert from existing levels?
 * <p>
 * how to use these?
 * <p>
 * perhaps the whole map will be modeled via this logic before real filling?
 */
public class RoomTemplateMaster {
    public static final boolean SINGLE_FILE_DATA = false;
    public static final FACING_DIRECTION DEFAULT_ENTRANCE_SIDE = FACING_DIRECTION.WEST;
    private static final String MODEL_SPLITTER = "=";
    private static final String EXIT_TEMPLATE_SEPARATOR = "><" + StringMaster.NEW_LINE;
    private static final String ROOM_TYPE_SEPARATOR = "<>" + StringMaster.NEW_LINE;
    private final LevelData data;
    Stack<List<RoomModel>> roomPoolStack = new Stack<>();
    private ROOM_TEMPLATE_GROUP[] groups;
    private String wrapType;
    private int wrapWidth;
    private Map<ROOM_TEMPLATE_GROUP, Set<RoomModel>> models = new LinkedHashMap<>();
    private Map<ROOM_TEMPLATE_GROUP, Map<ROOM_TYPE, Map<EXIT_TEMPLATE, List<RoomModel>>>> templateMap;
    private Map<ROOM_TEMPLATE_GROUP, Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>>> preloadedData;

    public RoomTemplateMaster(LevelData data, LevelModel model) {
        this.data = data;
        groups = this.data.getTemplateGroups();
        templateMap = new XLinkedMap<>();
        wrapType = this.data.getValue(LEVEL_VALUES.WRAP_CELL_TYPE);
        wrapWidth = this.data.getIntValue(LEVEL_VALUES.WRAP_ROOMS);

        preloadedData = loadMergedData(SINGLE_FILE_DATA);
        for (ROOM_TEMPLATE_GROUP group : groups) {

            templateMap.put(group, new HashMap<>());
            models.put(group, new HashSet<>());
            fillTemplateMap(group);
        }
    }

    private Map<ROOM_TEMPLATE_GROUP, Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>>>
    loadMergedData(boolean singleFile) {
        Map<ROOM_TEMPLATE_GROUP, Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>>> map = new HashMap<>();
        if (singleFile) {

        }
        for (ROOM_TEMPLATE_GROUP group : groups) {

            String data = singleFile ? "" : FileManager.readFile(
             getMergedPath(group));
            Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>> submap = createMap(group, data);
            map.put(group, submap);
        }
        for (ROOM_TEMPLATE_GROUP group : groups) {
            if (group.isMultiGroup()) {
                Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>> merged
                 = new HashMap<>();
                Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>> sub =
                 map.get(group.getMultiGroupOne());
                if (sub == null)
                    sub = createMap(group.getMultiGroupOne());
                merged.putAll(sub);

                sub = map.get(group.getMultiGroupTwo());
                if (sub == null)
                    sub = createMap(group.getMultiGroupTwo());
                merged.putAll(sub);
                map.put(group, merged);
            }
        }
        return map;
    }

    private Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>> createMap(ROOM_TEMPLATE_GROUP group) {
        String data = FileManager.readFile(
         getMergedPath(group));
        return createMap(group, data);
    }

    private Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>> createMap(ROOM_TEMPLATE_GROUP group
     , String data) {
        String[] byRoom = StringMaster.splitLines(data, false, ROOM_TYPE_SEPARATOR);
        Map<ROOM_TYPE, Map<EXIT_TEMPLATE, String>> submap = new HashMap<>();

        for (String roomData : byRoom) {
            roomData = StringMaster.trimNewlines(roomData);
            ROOM_TYPE roomType = new EnumMaster<ROOM_TYPE>().retrieveEnumConst(ROOM_TYPE.class,
             StringMaster.splitLines(roomData)[0].trim());
            if (roomType == null)
                continue;
            roomData = StringMaster.cropFirstSegment(roomData, StringMaster.NEW_LINE);

            String[] byExit = StringMaster.splitLines(roomData, false, EXIT_TEMPLATE_SEPARATOR);
            Map<EXIT_TEMPLATE, String> exitMap = new HashMap<>();
            submap.put(roomType, exitMap);

            for (EXIT_TEMPLATE exit : EXIT_TEMPLATE.values()) {
                exitMap.put(exit, "");
            }

            for (String part : byExit) {
                EXIT_TEMPLATE exit = new EnumMaster<EXIT_TEMPLATE>().retrieveEnumConst(EXIT_TEMPLATE.class,
                 StringMaster.splitLines(part)[0].trim());
                String text = StringMaster.cropFirstSegment(part, StringMaster.NEW_LINE);
                if (!StringMaster.contains(text, ROOM_CELL.FLOOR.getSymbol())) {
                    text = getRoomData(exit, group, roomType);
                }
                if (!text.isEmpty())
                    exitMap.put(exit,
                     text);
            }
        }
        return submap;
    }

    private String getMergedPath(ROOM_TEMPLATE_GROUP group) {
        return
         PathFinder.getRoomTemplatesFolder() + group + PathUtils.getPathSeparator()
          + group.name().toLowerCase() + " merged.txt";
    }

    private String getROOM_TYPE_SEPARATOR(ROOM_TYPE type) {
        return ROOM_TYPE_SEPARATOR + type.toString();
    }

    private String getEXIT_TEMPLATE_SEPARATOR(EXIT_TEMPLATE exitTemplate) {
        return
         EXIT_TEMPLATE_SEPARATOR + exitTemplate.toString();
    }

    private void fillTemplateMap(ROOM_TEMPLATE_GROUP group) {
        for (ROOM_TYPE sub : ROOM_TYPE.values()) {
            Map<EXIT_TEMPLATE, List<RoomModel>> map = new HashMap<>();
            List<RoomModel> common = sub == ROOM_TYPE.CORRIDOR || SINGLE_FILE_DATA
             ? new ArrayList<>()
             : loadModels(group, sub, null);
            for (EXIT_TEMPLATE exitTemplate : EXIT_TEMPLATE.values()) {
                List<RoomModel> roomModels = loadModels(group, sub, exitTemplate);
                roomModels.addAll(common);
                map.put(exitTemplate, roomModels);
                models.get(group).addAll(roomModels);
            }
            templateMap.get(group).put(sub, map);
        }
    }


    protected List<RoomModel> loadModels(ROOM_TEMPLATE_GROUP group,
                                         ROOM_TYPE type,
                                         EXIT_TEMPLATE exitTemplate) {
        List<RoomModel> list = new ArrayList<>();
        String text = getRoomData(exitTemplate, group, type);
        if (text != null) {
            if (!text.contains(MODEL_SPLITTER)) {
                return list;
            }
            for (String string : StringMaster.splitLines(text, false, MODEL_SPLITTER)) {
                if (string.isEmpty())
                    continue;
                list.add(createRoomModel(string, exitTemplate, type));
            }
        }
        //        }
        return list;
    }

    private String getRoomData(EXIT_TEMPLATE exitTemplate,
                               ROOM_TEMPLATE_GROUP group, ROOM_TYPE type
    ) {

        try {
            return
             processRoomData(preloadedData.get(group).get(type).get(exitTemplate));
        } catch (Exception e) {
            log(1, group + " has no " + type
             + exitTemplate);
        }

        return "";
    }

    private String processRoomData(String s) {
        return s.replace("0", "O");
    }

    private String getRoomPath(EXIT_TEMPLATE exitTemplate, ROOM_TEMPLATE_GROUP group, ROOM_TYPE type) {

        String path = PathFinder.getRoomTemplatesFolder() + group + PathUtils.getPathSeparator();
        if (exitTemplate != null)
            path += exitTemplate + PathUtils.getPathSeparator();
        //else => common templates
        return path + getRoomTypeFileName(type) + ".txt";
    }

    private String getRoomTypeFileName(ROOM_TYPE type) {
        if (type == ROOM_TYPE.THRONE_ROOM) {
            return "main";
        }
        if (type == ROOM_TYPE.CORRIDOR) {
            return "link";
        }
        return type.toString().split("_")[0];
    }


    private RoomModel createRoomModel(String data,
                                      EXIT_TEMPLATE exit,
                                      ROOM_TYPE template) {
        String[] array = StringMaster.splitLines(data.trim());
        if (array.length < 1) {
            log(1, "EMPTY ROOM FOR " + template + exit);
            return null;
        }
        int wrapWidth = getWrapWidthForRoomModel(exit, template);

        String[][] cells = new String[array.length + wrapWidth * 2][array[0].length() + wrapWidth * 2];
        //        boolean hor = array[0].length() + wrapWidth * 2 > array.length + wrapWidth * 2;
        //        if (hor)
        //            cells = new String[array.length + wrapWidth * 2][array[0].length() + wrapWidth * 2];

        int i = wrapWidth;
        for (String row : array) {
            if (cells.length <= i)
                continue;
            cells[i] = row.split(""); //TODO won't work?
            i++;
        }
        if (wrapWidth > 0)
            RoomModelTransformer.wrapInWalls(cells, wrapWidth, wrapType); //TODO could wrap in empty cells for outdoors!

        //        if (hor) {
        //                   TODO setRotations or leave it be!
        //            cells = ArrayMaster.rotateStringMatrixClockwise(cells);
        //        } else {
        try {
            String[][] fixedForRows = ArrayMaster.flip(cells, false, true);
            fixedForRows = ArrayMaster.rotateStringMatrixAnticlockwise(fixedForRows);
            cells = fixedForRows;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        //        }

        RoomModel model = new RoomModel(cells, template, exit);
        return model;
    }

    private int getWrapWidthForRoomModel(EXIT_TEMPLATE exit, ROOM_TYPE template) {
        if (wrapWidth > 1) {
            if (template == ROOM_TYPE.CORRIDOR
                //             || template == ROOM_TYPE.DEATH_ROOM
                //             || template == ROOM_TYPE.GUARD_ROOM
             )
                return 0;
        }
        return wrapWidth;
    }


    public RoomModel getNextRandomModel(ROOM_TYPE roomType, EXIT_TEMPLATE template,
                                        FACING_DIRECTION entrance, ROOM_TEMPLATE_GROUP templateGroup) {

        if (roomPoolStack.isEmpty())
            resetSizedRandomRoomPools(templateGroup);
        List<RoomModel> models = new ArrayList<>(roomPoolStack.pop());
        models.removeIf(model -> model.getType() != roomType);
        models.removeIf(model -> model.getExitTemplate() != template);

        if (models.isEmpty()) {
            if (roomPoolStack.isEmpty())
                return null;
            return getNextRandomModel(roomType, template, entrance, templateGroup);
        }

        RoomModel model = new RandomWizard<RoomModel>().getRandomListItem(models);

        while (model.getCells().length < 3 || model.getCells()[0].length < 3 && new Loop(20).continues()) {
            model = new RandomWizard<RoomModel>().getRandomListItem(models);
        }
        model = clone(model);

        checkFlipping(template, entrance, model);

        checkRotations(template, entrance, model);


        return model;
    }

    private void checkRotations(EXIT_TEMPLATE template, FACING_DIRECTION entrance, RoomModel model) {
        boolean random = data.isRandomRotation() && template != EXIT_TEMPLATE.FORK&& template != EXIT_TEMPLATE.ANGLE;
        FACING_DIRECTION requiredEntranceSide = DEFAULT_ENTRANCE_SIDE;

        //can specify required entrance side with 'e' symbol
        if (data.isPresetEntrancesAllowed())
            if (random) {
                for (String s : model.getCells()[model.getCells().length - 1]) {
                    if (s.equals(ROOM_CELL.ROOM_EXIT.getSymbol())) {
                        requiredEntranceSide = FACING_DIRECTION.EAST;
                    }
                }
                if (requiredEntranceSide == DEFAULT_ENTRANCE_SIDE) {
                    String[][] rotated = ArrayMaster.rotateStringMatrixClockwise(model.getCells());
                    for (String s : rotated[0]) {
                        if (s.equals(ROOM_CELL.ROOM_EXIT.getSymbol())) {
                            requiredEntranceSide = FACING_DIRECTION.SOUTH;
                        }
                    }
                    if (requiredEntranceSide == DEFAULT_ENTRANCE_SIDE) {
                        for (String s : rotated[rotated.length - 1]) {
                            if (s.equals(ROOM_CELL.ROOM_EXIT.getSymbol())) {
                                requiredEntranceSide = FACING_DIRECTION.NORTH;
                            }
                        }
                    }
                }
            }
        FACING_DIRECTION exit = entrance;
        //        if (entrance != null && entrance.isVertical() &&
        //         template == EXIT_TEMPLATE.THROUGH)
        //            exit = FacingMaster.rotate180(entrance);
        exit = entrance != null && entrance.isVertical() &&
         template == EXIT_TEMPLATE.ANGLE ? exit.flip()
         : exit;

        Boolean[] rotations =
         random ?
          RotationMaster.getRandomPossibleParentRotations(entrance, template)
          : RotationMaster.getRotations(
          exit, requiredEntranceSide);
        //        if (template==EXIT_TEMPLATE.ANGLE){
        //            if (rotations.length==3)
        //                TODO something cheesy there...
        //        }
        if (rotations != null) {
            model.setRotations(rotations);
        }

    }

    private void checkFlipping(EXIT_TEMPLATE template, FACING_DIRECTION entrance, RoomModel model) {
        if (model.getType() == ROOM_TYPE.ENTRANCE_ROOM ||
         model.getType() == ROOM_TYPE.EXIT_ROOM)
            return;
        if (template == EXIT_TEMPLATE.ANGLE)
            if (entrance != null && !entrance.isVertical())
                model.setFlip(false, true);
        if (template == EXIT_TEMPLATE.CROSSROAD) {
            model.setFlip(RandomWizard.random(), RandomWizard.random());
        }
        if (template == EXIT_TEMPLATE.FORK) {
            model.setFlip(false, RandomWizard.random());
        }
        if (template == EXIT_TEMPLATE.THROUGH) {
            model.setFlip(RandomWizard.random(), false);
        }
    }


    private RoomModel clone(RoomModel model) {
        return new RoomModel(ArrayMaster.cloneString2d(
         model.getCells()), model.getType(), model.getExitTemplate());
    }


    public String generate(int x, int y, float irregularity) {
        String result = "";
        int col = 0;
        int row = 0;
        while (row < y) {
            while (col < x) {
                result += GeneratorEnums.ROOM_CELL.WALL.getSymbol();
                col++;
            }
            result += StringMaster.NEW_LINE;
            row++;
        }


        return result;
    }

    public void resetSizedRandomRoomPools(ROOM_TEMPLATE_GROUP templateGroup) {
        roomPoolStack.clear();
        Map<Dimension, List<RoomModel>> pools = new LinkedHashMap<>();
        List<Dimension> dimensions = new ArrayList<>();
        for (RoomModel model : models.get(templateGroup)) {
            Dimension dimension = new Dimension(model.getWidth(), model.getHeight());
            MapMaster.addToListMap(pools, dimension, model);
            if (!dimensions.contains(dimension))
                dimensions.add(dimension);
        }
        Collections.sort(dimensions,
         new SortMaster<Dimension>().getSorterByExpression_((Dimension dim)
          -> (int) -(dim.getHeight() * dim.getWidth()
          * (RandomWizard.chance(data.getIntValue(LEVEL_VALUES.RANDOMIZED_SIZE_SORT_CHANCE))
          ? RandomWizard.getRandomIntBetween(0, 100) : 1))));

        for (Dimension dimension : dimensions) {
            roomPoolStack.add(pools.get(dimension));
        }
    }

    public RoomModel getRandomModelToSubstitute(int width, int height, EXIT_TEMPLATE template,
                                                ROOM_TYPE type, FACING_DIRECTION entrance, ROOM_TEMPLATE_GROUP templateGroup,
                                                List<FACING_DIRECTION> usedExits) {
        List<RoomModel> pool = new ArrayList<>(models.get(templateGroup));

        pool.removeIf(r -> {
            if (r.getExitTemplate() != template) {
                return true;
            }
            if (r.getType() != type) {
                return true;
            }
            if (r.getWidth() == width && r.getHeight() == height)
                return false;
            if (r.getWidth() == height && r.getHeight() == width)
                return false;
            return true;
        });

        RoomModel model = null;
        while (!pool.isEmpty()) {
            model = clone(pool.remove(RandomWizard.getRandomIndex(pool)));
            //for culdesac?

            checkRotations(template, entrance, model);
            //        checkFlipping(template, entrance, model);

            if (new ListMaster().compare(usedExits,
             Arrays.asList(RotationMaster.getRotatedExits(model.getRotations(),
              ExitMaster.getExits(template))))) {
                return model;
            }
        }

        return null;
    }
    //TODO multiple objects on cell?

}