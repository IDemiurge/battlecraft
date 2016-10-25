package main.system.net.data;

import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.net.data.MapData.MAP_VALUES;

import java.util.HashMap;
import java.util.Map;

public class MapData extends DataUnit<MAP_VALUES> {

    private String background;
    private Map<Coordinates, ObjType> objMap;

    public MapData(Map<Coordinates, ObjType> objMap, String background) {
        this.setObjMap(objMap);
        this.setBackground(background);

        setValue(MAP_VALUES.MAP_OBJECTS, createObjCoordinateString(objMap, false));
        setValue(MAP_VALUES.BF_BACKGROUND, background);
    }

    // format
    public MapData(String input) {
        super(input);
        String value = getValue(MAP_VALUES.MAP_OBJECTS);
        if (!StringMaster.isEmpty(value)) {
            this.setObjMap(buildObjCoordinateMapFromString(value));
            // inversion
            setValue(MAP_VALUES.MAP_OBJECTS, createObjCoordinateString(objMap, true));
            this.setObjMap(buildObjCoordinateMapFromString(getValue(MAP_VALUES.MAP_OBJECTS)));
        } else {
            setObjMap(new HashMap<Coordinates, ObjType>());
        }
        this.setBackground(getValue(MAP_VALUES.BF_BACKGROUND));
    }

    public Map<Coordinates, ObjType> getObjMap() {
        return objMap;
    }

    public void setObjMap(Map<Coordinates, ObjType> objMap) {
        this.objMap = objMap;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getMapName() {
        return getValue(MAP_VALUES.MAP_NAME);
    }

    public enum MAP_VALUES {
        MAP_OBJECTS, BF_BACKGROUND, MAP_NAME
    }

}