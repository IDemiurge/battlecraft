package main.game.battlecraft.logic.dungeon.building;

import main.content.values.parameters.G_PARAMS;
import main.data.XLinkedMap;
import main.data.xml.XML_Converter;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.BLOCK_TYPE;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.ZCoordinates;
import main.game.core.game.DC_Game;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;
import java.util.stream.Collectors;

public class MapBlock {
    int id;
    BLOCK_TYPE type;
    DequeImpl<Obj> objects;
    List<Coordinates> coordinates;
    Map<Coordinates, Obj> map = new HashMap<>(); //dirty hack with ZCoordinates - beyond redemption
    Map<MapBlock, List<Coordinates>> connectedBlocks = new HashMap<>();
    int spawningPriority;
    int treasureValue;
    private MapZone zone;
    private ROOM_TYPE roomType;
    private Coordinates keyCoordinate;
    private String xml;

    public MapBlock(int id, BLOCK_TYPE b, MapZone zone, DungeonPlan plan,
                    List<Coordinates> coordinates) {
        this.type = b;
        coordinates.removeIf(c-> coordinates.lastIndexOf(c)!= coordinates.indexOf(c));
        this.coordinates =//new LinkedHashSet<>
         (coordinates);
        this.zone = zone;
        this.id = id;
        if (zone != null) {
            zone.addBlock(this);
        }
        if (plan != null) {
            plan.addBlock(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MapBlock) {
            MapBlock mapBlock = (MapBlock) obj;
            if (!mapBlock.getCoordinates().equals(getCoordinates())) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void link(MapBlock lastBlock, Coordinates... coordinates) {
        List<Coordinates> list = new ListMaster<Coordinates>().getList(coordinates);
        connectedBlocks.put(lastBlock, list);
        LogMaster.log(1, this + " linked to " + lastBlock.toString() + " on "
         + list);
    }

    public String toString() {
        String string = getShortName();
        return string + "; Zone # " + zone.getI() + "; "
         + CoordinatesMaster.getBoundsFromCoordinates(coordinates);
    }

    public String getXml() {
        resetObjects();
        xml = "";
        xml += XML_Converter.openXmlFormatted(getShortName());
        xml += XML_Converter.wrapLeaf(DungeonBuilder.BLOCK_TYPE_NODE, type.getName());
        if (roomType != null) {
            xml += XML_Converter.wrapLeaf(DungeonBuilder.ROOM_TYPE_NODE, roomType.toString());

        }
        xml += XML_Converter.openXmlFormatted(DungeonBuilder.COORDINATES_NODE);
        for (Coordinates c : coordinates) {
            xml += c.toString() + ";";
        }
        xml += XML_Converter.closeXmlFormatted(DungeonBuilder.COORDINATES_NODE);

        xml += XML_Converter.openXmlFormatted(DungeonBuilder.OBJ_NODE);

        for (Coordinates c : map.keySet()) {
            Obj obj = map.get(c);
            String name = obj.getName();
            Integer chance = obj.getIntParam(G_PARAMS.CHANCE);
            if (chance > 0) {
                name += StringMaster.wrapInParenthesis(chance + "%");
            }

            xml += c + "=" + name + ",";
        }
        // ++ random objects
        xml += XML_Converter.closeXmlFormatted(DungeonBuilder.OBJ_NODE);
        xml += XML_Converter.closeXmlFormatted(getShortName());
        // objects?
        return xml;
    }

    public void resetObjects() {
        setObjects(getObjectsByCoordinates());
        map.clear();
        for (Coordinates c : getCoordinates()) {
            for (BattleFieldObject obj : DC_Game.game.getBfObjectsOnCoordinate(c)) {
                if (map.containsKey(c)) {
                    ZCoordinates coordinates = new ZCoordinates(c.x, c.y, new Random().nextInt());
                    map.put(coordinates, obj);
                } else {
                    map.put(c, obj);
                }
            }
            for (BattleFieldObject obj : DC_Game.game.getOverlayingObjects(c)) {
                if (map.containsKey(c)) {
                    ZCoordinates coordinates = new ZCoordinates(c.x, c.y, new Random().nextInt());
                    map.put(coordinates, obj);
                } else {
                    map.put(c, obj);
                }
            }
        }
    }

    public DequeImpl<Obj> getObjectsByCoordinates() {
        DequeImpl<Obj> objects =
         new DequeImpl<>();

        coordinates.stream().map(c ->
         DC_Game.game.getBfObjectsOnCoordinate(c)
        ).collect(Collectors.toList()).forEach(list -> objects.addAllCast(list));

        return objects;
    }

    public DequeImpl<Obj> getObjects() {
        if (objects == null) {
            objects = new DequeImpl<>();
        }
        return objects;
    }

    public void setObjects(DequeImpl<Obj> objects) {
        this.objects = objects;
    }

    public String getShortName() {
        String name = type.getName();
        if (roomType != null) {
            name = StringMaster.getWellFormattedString(roomType + "");
        }
        return name + "-" + id;
    }

    public BLOCK_TYPE getType() {
        return type;
    }

    public void setType(BLOCK_TYPE type2) {
        type = type2;
    }

    public int getSquare() {
        return coordinates.size();
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public Map<MapBlock, List<Coordinates>> getConnectedBlocks() {
        if (connectedBlocks == null) {
            connectedBlocks = new XLinkedMap();
        }
        return connectedBlocks;
    }

    public int getSpawningPriority() {
        return spawningPriority;
    }

    public void setSpawningPriority(int spawningPriority) {
        this.spawningPriority = spawningPriority;
    }

    public int getTreasureValue() {
        return treasureValue;
    }

    public void setTreasureValue(int treasureValue) {
        this.treasureValue = treasureValue;
    }

    public int getWidth() {
        return CoordinatesMaster.getRangeX(coordinates);
    }

    public int getHeight() {
        return CoordinatesMaster.getRangeY(coordinates);
    }

    // ai priority , ...
    public ROOM_TYPE getRoomType() {
        return roomType;
    }

    // if (block.isEntrance())
    // block.isTreasureRoom()
    public void setRoomType(ROOM_TYPE roomType) {
        this.roomType = roomType;
    }

    public void addObject(BattleFieldObject obj, Coordinates c) {

        if (map.containsKey(c)) {
            ZCoordinates coordinates = new ZCoordinates(c.x, c.y, new Random().nextInt());
            map.put(coordinates, obj);
        } else {
            getMap().put(c, obj);
        }
        getObjects().add(obj);
    }

    public boolean removeObject(DC_Obj obj, Coordinates c) {
        MapMaster.removeValue(getMap(), obj);
        return !getObjects().remove(obj);
    }

    public Coordinates getKeyCoordinate() {
        return keyCoordinate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MapZone getZone() {
        return zone;
    }

    public void setZone(MapZone zone) {
        this.zone = zone;

    }

    public void addCoordinate(Coordinates coordinate) {
        if (!this.coordinates.contains(coordinate)) {
            coordinates.add(coordinate);
        }
    }

    public Map<Coordinates, Obj> getMap() {
        return map;
    }

}
