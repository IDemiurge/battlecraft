package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.*;

/**
 * Created by JustMe on 2/13/2018.
 * <p>
 * from this, it must be possible to create a real level with specific objects
 */
public class LevelModel {
    ROOM_CELL[][] cells; //enum?
    List<LevelZone> zones;
    Map<Room, LevelBlock> blocks;
    Map<Coordinates, Room> roomMap = new LinkedHashMap<>();
    LevelData data;
    private Set<Coordinates> occupiedCells = new LinkedHashSet<>();
    private Integer leftMost;
    private Integer rightMost;
    private Integer topMost;
    private Integer bottomMost;

    public LevelModel(LevelData data) {
        this.data = data;
    }



    @Override
    public String toString() {

        return "LevelModel: \n" + "; total rooms=" + roomMap.size() + "; occupied cells= " + occupiedCells
         + "; " +
         toASCII_Map();
        //                 new ArrayMaster<ROOM_CELL>().getCellsString(cells);

    }

    private void addCapes(Coordinates p, RoomModel roomModel) {
        int x = p.x;
        int x1 = p.x + roomModel.getWidth();
        int y = p.y;
        int y1 = p.y + roomModel.getHeight();
        if (leftMost == null || x < leftMost)
            leftMost = x;
        if (rightMost == null || x1 > rightMost)
            rightMost = x1;
        if (topMost == null || y < topMost)
            topMost = y;
        if (bottomMost == null || y1 > bottomMost)
            bottomMost = y1;

    }

    public int getCurrentWidth() {

        if (leftMost == null)
            return data.getX();
        if (rightMost == null)
            return data.getX();
        return Math.abs(leftMost - rightMost) + 1;
    }

    public int getCurrentHeight() {
        if (bottomMost == null)
            return data.getY();
        if (topMost == null)
            return data.getY();
        return Math.abs(topMost - bottomMost) + 1;
    }


    public void addRoom(  Room room) {
        Coordinates p = room.getCoordinates();

        roomMap.put(p, room);
        addOccupied(p, room);
        addCapes(p, room);
        setCells(new TileMapper(this, data).build(this));

        main.system.auxiliary.log.LogMaster.log(1, "Placed " + room + " at " +
         p.x +" " + p.y + "; " + toString());

    }


    private void addOccupied(Coordinates p, RoomModel roomModel) {
        occupied(false, p, roomModel);
    }
        private void occupied(boolean remove, Coordinates p, RoomModel roomModel) { //check wrap?
        for (int x = p.x ; x  < p.x + roomModel.getWidth(); x++) {
            for (int y = p.y ; y  < p.y + roomModel.getHeight(); y++) {
                Coordinates point = new AbstractCoordinates(x, y);
                if (remove)
                    occupiedCells.remove(point);
                else
                    occupiedCells.add(point);
            }
        }
    }
    public   void shearWallsFromSide(Room room, FACING_DIRECTION entrance) {
        remove(room );
        Coordinates newCoordinates = room.shearWallsFromSide(entrance);
        room.setCoordinates(newCoordinates);
        //TODO can lead to path blocking!!!
        addRoom(room);
    }

    private void remove(Room room) {
        roomMap.remove(room.getCoordinates());
        occupied(true, room.getCoordinates(), room);
    }

    public Set<Coordinates> getOccupiedCells() {
        return occupiedCells;
    }

    public ROOM_CELL[][] getCells() {
        return cells;
    }

    public void setCells(ROOM_CELL[][] cells) {
        this.cells = cells;
    }

    public Map<Coordinates, Room> getRoomMap() {
        return roomMap;
    }

    public Integer getLeftMost() {
        return leftMost;
    }

    public Integer getRightMost() {
        return rightMost;
    }

    public Integer getTopMost() {
        return topMost;
    }

    public Integer getBottomMost() {
        return bottomMost;
    }

    public LevelData getData() {
        return data;
    }

    public List<LevelZone> getZones() {
        return zones;
    }

    public Map<Room, LevelBlock> getBlocks() {
        return blocks;
    }

    public String toASCII_Map() {
        return toASCII_Map(false);
    }

    public String toASCII_Map(boolean nullToX) {

        ROOM_CELL[][] cells = getCells();
        String string = "\n";
        String columns = "\nX    ";
        String separator = "\n      ";

        for (int x = 0; x < getCurrentWidth(); x++) {
            columns += x + "|";
            if (x < 10) columns += " ";
            separator += "___";
        }
        for (int y = 0; y < getCurrentHeight(); y++) {
            if (y < 10)
                string += y + "  | ";
            else
                string += y + " | ";
            for (int x = 0; x < getCurrentWidth(); x++) {
                if (cells[x][y] == null) {
                    if (nullToX)
                        string += "  X";
                    else
                        string += "  -";
                } else
                    string += "  " + cells[x][y].getSymbol();
            }
            string += "\n";

        }
        separator += "\n";
        return columns + separator + string + separator + columns;
    }


        public LevelZone getZone(LevelGraphNode node) {
        return
         zones.toArray(new LevelZone[zones.size()])[node.getZoneIndex()];
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }
}
