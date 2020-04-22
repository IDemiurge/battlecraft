package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.xml.XmlNodeMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.data.DataUnit;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static main.system.auxiliary.log.LogMaster.log;

public class FloorLoader extends DungeonHandler<Location> {
    //nodes
    public static final String TRANSITS = "TRANSITS";
    public static final String OVERLAY_DIRECTIONS = "OVERLAY_DIRECTIONS";
    public static final String ENCOUNTER_AI_GROUPS = "ENCOUNTER_AI_GROUPS";
    public static final String CUSTOM_AI_GROUPS = "CUSTOM_AI_GROUPS";
    public static final String ID_MAP = "ID_MAP";
    public static final String MODULES = "MODULES";
    public static final String OBJ_NODE_NEW = "OBJ_IDS";
    public static final String BORDERS = "BORDERS";
    public static final String DATA_MAPS = "DATA_MAPS";
    //subnodes
    public static final String TRANSIT_IDS = "TRANSIT_IDS";
    public static final String TRANSIT_ONE_END = "TRANSIT_ONE_END";
    public static final String DATA = "DATA";
    public static final String FACING = "FACING";
    public static final String LAYERS = "LAYERS";
    public static final String MISSING = "Missing";

    public static final String COORDINATE_DATA = "COORDINATE_DATA";
    public static final String COORDINATES_VOID = "coordinates_void";
    public static final String CUSTOM_TYPE_DATA = "CUSTOM_TYPE_DATA";
    public static final String ZONES = "Zones";
    public static final String MAIN_ENTRANCE = "MAIN_ENTRANCE";
    public static final String MAIN_EXIT = "MAIN_EXIT";
    public static final String SCRIPT_DATA = "SCRIPT_DATA";
    private String entranceData = "";

    public FloorLoader(DungeonMaster master) {
        super(master);
    }


    private static ObjType applyCustomData(ObjType type, Map<String, String> data) {
        ObjType newType = new ObjType(type);
        for (String val : data.keySet()) {
            newType.setValue(val, data.get(val));
        }
        return newType;
    }

    public void processModuleSubNode(Node node, Location location, Module module) {
        log(LOG_CHANNEL.BUILDING, "Module Sub Node: " + node.getNodeName());
        switch (node.getNodeName()) {
            case CUSTOM_TYPE_DATA:
                Map<Integer, ObjType> idTypeMap = module.getIdTypeMap();
                Map<Integer, Map<String, String>> customTypesData = MapMaster.createDataMap(node.getTextContent());
                for (Integer id : customTypesData.keySet()) {
                    Map<String, String> data = customTypesData.get(id);
                    ObjType type = idTypeMap.get(id);
                    if (data != null) {
                        type = applyCustomData(type, data);
                    }
                    idTypeMap.put(id, type);
                }
                break;
            case BORDERS:
                getObjInitializer().processBorderObjects(module, node);
                break;
            case OBJ_NODE_NEW:
                Map<Integer, BattleFieldObject> objectMap =
                        getObjInitializer().processObjects(module,
                                module.getIdTypeMap(),
                                new HashMap<>(), node);

                log(LOG_CHANNEL.BUILDING, "Objects created: " + objectMap.size());
                module.getObjIdMap().putAll(objectMap);
                break;
            case COORDINATES_VOID:
                for (String substring : ContainerUtils.openContainer(node.getTextContent())) {
                    module.getVoidCells().add(Coordinates.get(substring));
                }
                break;
            case LAYERS:
                processLayers(node);
                break;
            case OVERLAY_DIRECTIONS:
                getObjInitializer().initDirectionMap(node.getTextContent());
                //TODO
                break;
            case FACING:
                //TODO
                break;
            case TRANSIT_IDS:
                processTransitsNode(node.getTextContent());
                break;
            case MAIN_ENTRANCE:
                location.setEntranceData(node.getTextContent());
                break;
            case MAIN_EXIT:
                location.setExitData(node.getTextContent());
                break;
            case ID_MAP:
                module.getIdTypeMap().putAll(processIdTypesMap(node.getTextContent()));
                log(LOG_CHANNEL.BUILDING, module + " Id-Type Map built: " +
                        module.getIdTypeMap());
                break;
        }

    }

    public void processNode(Node node, Location location) {
        log(LOG_CHANNEL.BUILDING, "Xml node: " + node.getNodeName());
        switch (node.getNodeName()) {
            case DATA:
                FloorData data = new FloorData(location);
                data.setData(node.getTextContent());
                log(LOG_CHANNEL.BUILDING, "Floor data: " + data);
                data.apply();
                log(LOG_CHANNEL.BUILDING, "Location after data applies: " +
                        location);
                if (location.getWidth() > 0 && location.getHeight() > 0) {
                    getBuilder().initWidthAndHeight(location);
                }
                break;
            case MODULES:
                getStructureBuilder().build(node, location);
                checkModuleRemap(false, location);
                break;
            case DATA_MAPS:
                Map<DataMap, Map<Integer, String>> map = new LinkedHashMap<>();
                for (Node sub : XmlNodeMaster.getNodeList(node)) {
                    Map<Integer, String> submap = new LinkedHashMap<>();
                    for (Node idNode : XmlNodeMaster.getNodeList(node)) {
                        submap.put(Integer.valueOf(idNode.getNodeName()), sub.getTextContent());
                    }
                    map.put(DataMap.valueOf(sub.getNodeName()), submap);

                }
            case ENCOUNTER_AI_GROUPS:
                getMaster().getGame().getAiManager().getGroupHandler()
                        .initEncounterGroups(node.getTextContent());
                break;
            case CUSTOM_AI_GROUPS:
//             TODO    getMaster().getGame().getAiManager().getGroupHandler()
//                        .initCustomGroups(node.getTextContent());
                break;


        }

    }

    protected void checkModuleRemap(boolean b, Location location) {
    }

    protected void processTransitsNode(String textContent) {
        entranceData += textContent;
    }

    protected void initTransits(Location location) {
        for (String substring : ContainerUtils.openContainer(entranceData)) {
            Integer id = NumberUtils.getInteger(substring.split("->")[0]);
            Coordinates c = Coordinates.get(substring.split("->")[1]);
            processTransitPair(id, c, location);
        }
    }

    protected void processTransitPair(Integer id, Coordinates c, Location location) {
        Entrance e = (Entrance) master.getObjByOriginalModuleId (id);
        e.setTargetCoordinates(c);

        Module module = getMetaMaster().getModuleMaster().getModule(c);
        e.setTargetModule(module);
    }


    protected void processLayers(Node node) {
        game.getMetaMaster().getDungeonMaster().getLayerManager().initLayers(node);
    }

    private static Map<Integer, ObjType> processIdTypesMap(String textContent) {
        /*
        id=objType(x, y);
        OR just map ids to types, then proceed with the obj map as usual
         */
        Map<Integer, ObjType> idMap = new LinkedHashMap<>();
        textContent = textContent.trim();
        for (String content : ContainerUtils.openContainer(textContent)) {
            String typeName = content.split("=")[0];
            ObjType type = DataManager.getType(typeName, DC_TYPE.BF_OBJ);
            if (type == null) {
                type = DataManager.getType(typeName, DC_TYPE.UNITS);
            }
            String ids = content.split("=")[1];
            for (String substring : ContainerUtils.openContainer(ids, ",")) {
                Integer id = Integer.valueOf(substring);
                idMap.put(id, type);
            }
        }
        return idMap;
    }

    private Map<Coordinates, FACING_DIRECTION> createUnitFacingMap(String textContent) {
        Map<Coordinates, FACING_DIRECTION> map = new HashMap<>();

        DataUnit<FACING_DIRECTION> data = new DataUnit<>(textContent);
        for (String s : data.getValues().keySet()) {
            map.put(new Coordinates(s), FacingMaster.getFacing(data.getValues().get(s)));
        }
        return map;
    }

    public void start() {
        entranceData = "";
    }

    public void finish(Location location) {
        initTransits(location);
        location.initMainEntrance();
        //TODO may not be initialized??
        location.initMainExit();
    }
}