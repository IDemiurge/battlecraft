package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.xml.XML_Converter;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/20/2018.
 */
public class DungeonLevel extends LevelLayer<LevelZone> {
    TileMap tileMap;
    LevelModel model;
    SUBLEVEL_TYPE type;
    LOCATION_TYPE locationType;
    List<ObjAtCoordinate> objects=     new ArrayList<>() ;
    List<ObjAtCoordinate> units=     new ArrayList<>() ;
    private String directionMapData;
    private int powerLevel;
    private Map<String, DIRECTION> directionMap;
    private Map<String, FLIP> flipMap;

    public DungeonLevel(TileMap tileMap, LevelModel model, SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        this.tileMap = tileMap;
        this.model = model;
        this.type = type;
        this.locationType = locationType;
    }

    @Override
    public String toXml() {
        //TODO save original model map!
        tileMap= new TileMapper(model,model.getData()).joinTileMaps();
        String xml =XML_Converter.wrap("Map",  tileMap.toString());

        //props
//entrances
        List<Coordinates> entrances =
         tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().get(c) == ROOM_CELL.ENTRANCE).collect(Collectors.toList());
        tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().get(c) == ROOM_CELL.ROOM_EXIT).collect(Collectors.toList());
        xml += XML_Converter.wrap(LocationBuilder.ENTRANCE_NODE, ContainerUtils.constructStringContainer(entrances));

        List<Coordinates> exits =
         tileMap.getMap().keySet().stream().filter(c -> tileMap.getMap().get(c) == ROOM_CELL.ROOM_EXIT).collect(Collectors.toList());
        xml += XML_Converter.wrap(LocationBuilder.EXIT_NODE, ContainerUtils.constructStringContainer(exits));

        for (LevelZone levelZone : getSubParts()) {
            xml += levelZone.toXml();
        }
        xml = XML_Converter.wrap(LocationBuilder.ZONES_NODE, xml);
        xml = XML_Converter.wrap("Level", xml);
        return xml;
    }

    @Override
    public List<LevelZone> getSubParts() {
        return model.getZones();
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public LevelModel getModel() {
        return model;
    }

    public SUBLEVEL_TYPE getType() {
        return type;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
    }

    public List<ObjAtCoordinate> getObjects() {
        return objects;
    }

    public List<ObjAtCoordinate> getUnits() {
        return units;
    }

    public LevelBlock getBlockForCoordinate(Coordinates coordinates) {
        for (LevelZone zone : getSubParts()) {
            for (LevelBlock block : zone.getSubParts()) {
                if (block.getCoordinatesList().contains(coordinates))
                    return block;
            }
        }
        return null;
    }

    public void setDirectionMapData(String directionMapData) {
        this.directionMapData = directionMapData;
    }

    public String getDirectionMapData() {
        return directionMapData;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(int powerLevel) {
        this.powerLevel = powerLevel;
    }

    public void setDirectionMap(Map<String, DIRECTION> directionMap) {
        this.directionMap = directionMap;
    }

    public Map<String, DIRECTION> getDirectionMap() {
        return directionMap;
    }

    public void setFlipMap(Map<String, FLIP> flipMap) {
        this.flipMap = flipMap;
    }

    public Map<String, FLIP> getFlipMap() {
        return flipMap;
    }
}
