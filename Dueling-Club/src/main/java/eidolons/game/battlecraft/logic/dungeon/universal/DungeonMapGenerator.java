package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.building.BuildHelper.BuildParameters;
import eidolons.game.battlecraft.logic.dungeon.location.building.DC_Map;
import eidolons.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapBlock;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapZone;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.DUNGEON_MAP_MODIFIER;
import main.content.enums.DungeonEnums.DUNGEON_MAP_TEMPLATE;
import main.content.enums.DungeonEnums.MAP_FILL_TEMPLATE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;
import main.system.util.Refactor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DungeonMapGenerator<E extends DungeonWrapper> extends DungeonHandler<E> {
    private static final String ENTRANCE_TYPE = "Dungeon Entrance";
    private static final boolean subLevelTest = false;
    DUNGEON_MAP_TEMPLATE template;
    DUNGEON_MAP_MODIFIER mod;
    Integer sizeMod = 100;
    // keep in mind that bf size may change...
    // some objects may require special placements?
    private Map<Coordinates, ObjType> objMap;
    private Map<MAP_ZONES, Integer> zones;
    private ArrayList<FACING_DIRECTION> usedSides;
    private DungeonPlan plan;
    private BuildParameters params;

    public DungeonMapGenerator(DungeonMaster<E> master) {
        this(master, null);
    }

    public DungeonMapGenerator(DungeonMaster<E> master, BuildParameters params) {
        super(master);
        this.params = params;
    }
    // ++ passable obj map?


	/*
     * approaching terrain types? perhaps by using objects - passable or not
	 * semi-random object placement separate the map into zones? at least don't					
	 * use the default spawning zones - sides and center
	 */


    public static String getCoordinatesForSide(FACING_DIRECTION side) {
        switch (side) {
            case EAST:
                return "8-0,8-1," + "8-2," + "8-3,8-4,8-5";
            case WEST:
                return "0-0,0-1," + "0-2," + "0-3,0-4,0-5";
            case NORTH:
                return "2-0,3-0," + "4-0," + "5-0,6-0";
            case SOUTH:
                return "2-5,3-5," + "4-5," + "5-5,6-5";
        }
        return null;
    }

    @Override
    public LocationBuilder getBuilder() {
        return (LocationBuilder) super.getBuilder();
    }

//    public DC_Map generateMap(Location dungeon) {
//        map = new DC_Map();
//
//
//        if (dungeon.getPlan() == null) {
//            try {
//                if (dungeon.checkProperty(PROPS.DUNGEON_PLAN)) {
//                    this.plan = getBuilder().setParam(params). loadDungeonMap(dungeon
//                            .getProperty(PROPS.DUNGEON_PLAN));
//                } else {
//                    this.plan = getBuilder().setParam(params).buildDungeonPlan(dungeon);
//                }
//                dungeon.setPlan(plan);
//            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
//            }
//        } else {
//            plan = dungeon.getPlan();
//        }
//        template = new EnumMaster<DUNGEON_MAP_TEMPLATE>().retrieveEnumConst(
//                DUNGEON_MAP_TEMPLATE.class, dungeon.getProperty(PROPS.DUNGEON_MAP_TEMPLATE));
//        mod = new EnumMaster<DUNGEON_MAP_MODIFIER>().retrieveEnumConst(DUNGEON_MAP_MODIFIER.class,
//                dungeon.getProperty(PROPS.DUNGEON_MAP_MODIFIER));
//
//		/*
//         * mod should have a preset initComps option as well.
//		 */
//
//        try {
////            initBfObjMap(dungeon);
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//            map = new DC_Map();
//        }
//        String background = dungeon.getProperty(PROPS.MAP_BACKGROUND);
//        if (!ImageManager.isImage(background)) {
//            if (mod != null) {
//                background = mod.getBackground();
//            } else if (template != null) {
//                background = template.getBackground();
//            }
//        }
//        if (!ImageManager.isImage(background)) {
//            background = ImageManager.DEFAULT_BACKGROUND;
//        }
//        map.setBackground(background);
//        return map;
//    }
//    private void initBfObjMap(Location dungeon) {
//        objMap = new HashMap<>();
//        map.setObjMap(objMap);
//        if (plan != null) {
//            // if (plan.isPreloaded()) addObjects();
//            if (!dungeon.isRandomized()) {
//                if (!plan.isLoaded()) {
//                    generateUndergroundObjects();
//                }
//                return;
//            }
//        }
//        for (String s : StringMaster.open(dungeon.getProperty(PROPS.MAP_PRESET_OBJECTS))) {
//            try {
//                ObjType objType = DataManager.getType(VariableManager.removeVarPart(s),
//                        DC_TYPE.BF_OBJ);
//                Coordinates coordinates = new Coordinates(VariableManager.getVarPart(s));
//                if (objType != null) {
//                    objMap.put(coordinates, objType);
//                }
//            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
//            }
//        }
//
//        if (template == null) {
//            return;
//        }
//        zones = new HashMap<>();
//        List<String> objects = StringMaster.openContainer(template.getObjects());
//        if (mod != null) {
//            objects.addAll(StringMaster.openContainer(mod.getObjects()));
//        }
//        // if (mod.getPresetObjects()!=null){ TODO
//        // StringMaster.openContainer(mod.getPresetObjects(),
//        // StringMaster.AND_SEPARATOR)
//        // .getOrCreate(index)
//        // for ( s s StringMaster.openContainer(mod.getPresetObjects) ){
//        // c = new Coordinates(s);TODO
//        //
//        // }
//        // objMap.put(c, obj);
//        // } TODO
//
//            sizeMod = getSizeMod(dungeon.getDungeon());
//
//        objects.addAll(StringMaster.openContainer(dungeon.getProperty(PROPS.MAP_OBJECTS)));
//        for (String s : objects) {
//            String objTypeName = VariableManager.removeVarPart(s);
//            Integer max = StringMaster.getInteger(StringMaster.cropParenthesises(VariableManager
//                    .getVarPart(s)));
//
//            if (sizeMod != null) {
//                max = max * sizeMod / 100;
//            }
//            int n = RandomWizard.getRandomIntBetween(max / 2, max, true);
//            placeObjects(objTypeName, n);
//        }
//    }

    private void generateUndergroundObjects() {
        // dungeonMap.getFillerType();
        HashSet<Coordinates> linkCells = new HashSet<>();
        for (MapBlock b : plan.getBlocks()) {
            for (List<Coordinates> list : b.getConnectedBlocks().values()) {
                // culdesac won't be linked, that's it
                linkCells.addAll(list);
                for (Coordinates c : list) {
                    checkLinkCellObj(c, b, new MapMaster<MapBlock, List<Coordinates>>()
                     .getKeyForValue(b.getConnectedBlocks(), list));
                }
            }
            // b.getZone().getFillerType();
            // doors/wall-spaces
            // wall_fill_width = 1;
            // how to ensure that walls don't form 2 layers?
            // perhaps it would be easier to build with walls in mind - skip one
            // cell here and there when necessary, then only bother about links
            if (b.getRoomType() != null) {
                b.getKeyCoordinate();
            }
        }
        for (MapZone z : plan.getZones()) {
            for (int i = z.getX1(); i < z.getX2(); i++) {
                coordinateLoop:
                for (int j = z.getY1(); j < z.getY2(); j++) {
                    Coordinates c = new Coordinates(i, j);
                    if (linkCells.contains(c)) {
                        continue;
                    } // builder.getHelper().getUsedCoordinates()
                    for (MapBlock b : z.getBlocks()) {
                        if (b.getCoordinates().contains(c)) {
                            continue coordinateLoop;
                        }
                    }
                    fill(c, z.getFillerType());

                }
            }
        }
        // addLightEmittingObjects();
        // DoorMaster.initDoors(dungeon);
        // TrapMaster.initTraps(dungeon);
        // ChestMaster.initTreasures(dungeon);
    }

    private void addLightEmittingObjects() {
        int preferredIlluminationLevel = 0;
        // for each block separately - some well-lit, others almost dark...
        // avoid lighting traps and treasures!
        // TODO space evenly across walls; add for special rooms;

    }

    private void checkLinkCellObj(Coordinates c, MapBlock b, MapBlock b2) {
        ROOM_TYPE roomType = b.getRoomType();
        if (roomType != null) {

        }

    }

    private void fill(Coordinates c, String fillerType) {
        ObjType type = DataManager.getType(fillerType, DC_TYPE.BF_OBJ);
        if (type == null) {
            MAP_FILL_TEMPLATE leTemplate = new EnumMaster<MAP_FILL_TEMPLATE>().retrieveEnumConst(
             MAP_FILL_TEMPLATE.class, fillerType);
            if (leTemplate != null) {
                int i = 0;
                for (Coordinates adj : c.getAdjacentCoordinates()) {
                    ObjType objType = objMap.get(adj);
                    if (objType != null) {
                        if ((leTemplate.getPeripheryObjects() + leTemplate.getCenterObjects())
                         .contains(objType.getName())) {
                            i++;
                        }
                    }
                }
                if (i >= c.getAdjacentCoordinates().size() * 2 / 5) {
                    type = RandomWizard.getObjTypeByWeight(leTemplate.getCenterObjects(),
                     DC_TYPE.BF_OBJ);
                    objMap.put(c, type);
                    return;
                }

                type = RandomWizard.getObjTypeByWeight(leTemplate.getPeripheryObjects(),
                 DC_TYPE.BF_OBJ);
            } else {
                // other random groups
                DUNGEON_MAP_TEMPLATE template = new EnumMaster<DUNGEON_MAP_TEMPLATE>()
                 .retrieveEnumConst(DUNGEON_MAP_TEMPLATE.class, fillerType);
                if (template != null) {
                    type = RandomWizard.getObjTypeByWeight(template.getObjects(), DC_TYPE.BF_OBJ);
                }
            }
        }
        objMap.put(c, type);
    }

    private Integer getSizeMod(Dungeon dungeon) {
        int mod = 100;
        mod = mod * dungeon.getCellsY() / GuiManager.getBF_CompDisplayedCellsY()
         * dungeon.getCellsX() / GuiManager.getBF_CompDisplayedCellsX();
        if (mod == 100) {
            return null;
        }
        return mod;
    }

    private void addEntranceObjects(Entrance entrance) {
        // TODO Auto-generated method stub

    }

    private void placeObjects(String objTypeName, int n) {
        ObjType objType = DataManager.getType(objTypeName, DC_TYPE.BF_OBJ);
        if (objType == null) {
            return;
        }
        // full random
        while (n > 0) {
            Coordinates c = null;
            MAP_ZONES zone = null;
            Loop.startLoop(1000);
            while (!Loop.loopEnded()) {

                c = getRandomCoordinate();
                zone = getZone(c);
                if (zone == null) {
                    continue;
                }
                if (!checkCoordinate(c)) {
                    continue;
                }
                if (!checkLimit(zone)) {
                    continue;
                }
                if (!rollZone(zone)) {
                    continue;
                }
                break;
            }
            n--;
            Integer integer = zones.get(zone);
            if (integer == null) {
                integer = 0;
            }
            zones.put(zone, integer + 1);

            objMap.put(c, objType);

        }
    }

    private Coordinates getRandomCoordinate() {
        return new Coordinates(RandomWizard.getRandomInt(getWidth()), RandomWizard
         .getRandomInt(getHeight()));
    }

    private int getHeight() {
        return getDungeon().getHeight();
    }

    private int getWidth() {
        return getDungeon().getWidth();
    }

    @Refactor
    @Override
    public E getDungeon() {
        return (E) getBuilder().getDungeon();
    }

    public boolean checkCoordinate(Coordinates c) {
        if (isBlockingCorner(c)) {
            return false;
        }
        return !objMap.containsKey(c);

    }

    private boolean isBlockingCorner(Coordinates c) {
        Boolean west_east = null;
        if (c.x + 1 - getWidth() >= -1) {
            west_east = false;
        }
        if (c.x == 0 || c.x == 1) {
            west_east = true;
        }
        if (west_east == null) {
            return false;
        }

        Boolean north_south = null;
        if (c.y + 1 - getHeight() >= -1) {
            north_south = false;
        }
        if (c.y == 0 || c.x == 1) {
            north_south = true;
        }
        if (north_south == null) {
            return false;
        }
        if ((c.x + c.y) % 2 == 0) //
        {
            return false;
        }
        Coordinates c2 = new Coordinates(c.x + (west_east ? 1 : -1), c.y + (north_south ? -1 : 1));
        if ((c2.x + c2.y) % 2 == 0) {
            return false;
        }
        return objMap.containsKey(c2);
    }

    private boolean rollZone(MAP_ZONES zone) {
        if (zone == MAP_ZONES.CENTER) {
            return false;
        }
        int chanceToPass = MathMaster.applyMod(100, zone.getChance_mod());
        if (zones.get(zone) != null) {
            if (zones.get(zone) > 0) {
                chanceToPass = chanceToPass * zone.getObj_limit() / (4 * zones.get(zone));
            }
        }
        return RandomWizard.chance(chanceToPass);
    }

    private boolean checkLimit(MAP_ZONES zone) {
        Integer integer = zones.get(zone);
        if (integer == null) {
            return true;
        }
        return integer >= zone.getObj_limit();
    }

    private MAP_ZONES getZone(Coordinates c) {
        for (MAP_ZONES z : MAP_ZONES.values()) {
            String coordinates = z.getCoordinates();
            coordinates = getExtendedBfCoordinates(z);
            if (ListMaster.toList(Coordinates.getCoordinates(coordinates)).contains(c)) {
                return z;
            }
        }
        // return MAP_ZONES.CENTER;
        return null;
    }

    private String getExtendedBfCoordinates(MAP_ZONES z) {
        // DC_Game.game.getCoordinates();
        // define center, then it's easy
        int sideWidth = Math.max(1, Math.round(sizeMod / 100));
        int centerWidth = Math.max(3, Math.round(sizeMod / 100));// height
        // separate?

        switch (z) {
            case CENTER:
                break;
            case LEFT_FREE_ZONE:
                break;
            case RIGHT_FREE_ZONE:
                break;
            case SEMI_CORNERS:
                break;
            case SIDE_EAST:
                break;
            case SIDE_NORTH:
                break;
            case SIDE_SOUTH:
                break;
            case SIDE_WEST:
                break;
        }
        return z.getCoordinates();
    }

    // randomization could be done via weight-string with these constants
    // as per map template or obj type
    public enum MAP_ZONES {
        // CORNERS(DC_Map.CORNERS, 2, 100),
        SEMI_CORNERS(DC_Map.SEMI_CORNERS, 2, 100),
        CENTER(DC_Map.CENTER_ZONE, 2, 50),
        LEFT_FREE_ZONE(DC_Map.WEST_FREE_ZONE, 3, 100),
        RIGHT_FREE_ZONE(DC_Map.EAST_FREE_ZONE, 3, 100),
        SIDE_SOUTH(FACING_DIRECTION.SOUTH, 2, 70),
        SIDE_NORTH(FACING_DIRECTION.NORTH, 2, 70),
        SIDE_EAST(FACING_DIRECTION.EAST, 1, 30),
        SIDE_WEST(FACING_DIRECTION.WEST, 1, 30),;
        private String coordinates;
        private int obj_limit;
        private int chance_mod;

        MAP_ZONES(FACING_DIRECTION side, int obj_limit, int chance_mod) {
            this(getCoordinatesForSide(side), obj_limit, chance_mod);
        }

        MAP_ZONES(String coordinates, int obj_limit, int chance_mod) {
            this.coordinates = coordinates;
            this.obj_limit = obj_limit;
            this.chance_mod = chance_mod;

        }

        public String getCoordinates() {
            return coordinates;
        }

        public int getObj_limit() {
            return obj_limit;
        }

        public int getChance_mod() {
            return chance_mod;
        }
    }

    // private void generateSublevelEntrances() {
    // usedSides = new ArrayList<FACING_DIRECTION>();
    //
    // for (Dungeon sublvl : dungeon.getSubLevels()) {
    // generateSublevelEntrance(sublvl);
    // }
    // }
    //
    // private void generateSublevelEntrance(Dungeon sublevel) {
    // String entranceData = sublevel.getEntranceData();
    // // if (DungeonLevelMaster.isSublevelTestOn())
    // // entranceData = DungeonLevelMaster.TEST_ENTRANCE_DATA;
    // for (String string : StringMaster.open(entranceData)) {
    // ObjType bfObjType = new ObjType(DataManager.getType(string
    // .split(StringMaster.ALT_PAIR_SEPARATOR)[0], OBJ_TYPES.BF_OBJ));
    // List<ENTRANCE_POINT_TEMPLATE> templates = new
    // EnumMaster<ENTRANCE_POINT_TEMPLATE>()
    // .getEnumList(ENTRANCE_POINT_TEMPLATE.class, string
    // .split(StringMaster.ALT_PAIR_SEPARATOR)[1], StringMaster.VAR_SEPARATOR);
    // // could be mutiple, use AND
    //
    // for (ENTRANCE_POINT_TEMPLATE t : templates) {
    // FACING_DIRECTION side = FacingMaster.getRandomFacing(usedSides
    // .toArray(new FACING_DIRECTION[usedSides.size()]));
    // if (side == null)
    // return;
    // usedSides.add(side);
    // Coordinates c = DungeonLevelMaster.getEntranceCoordinates(side, t,
    // dungeon);
    //
    // Entrance entrance = new Entrance(c.x, c.y, bfObjType, dungeon, sublevel);
    // if (dungeon.getEntrances().isEmpty())
    // dungeon.setMainEntrance(entrance);
    // addEntranceObjects(entrance);
    // dungeon.getEntrances().add(entrance);
    // }
    // }
    // }

}
