package main.game.module.adventure.entity;

import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.MACRO_PARAMS;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroRef;
import main.game.module.adventure.gui.map.obj.MapObjComp;
import main.game.module.adventure.map.Area;
import main.game.module.adventure.map.Place.PLACE_VISIBILITY_STATUS;
import main.game.module.adventure.map.MacroCoordinates;

public abstract class MapObj extends MacroObj {

    protected Coordinates coordinates;
    protected Coordinates mapRenderPoint;
    protected PLACE_VISIBILITY_STATUS visibilityStatus;
    protected MapObjComp comp;
    protected Area area;

    public MapObj(ObjType type, MacroRef ref) {
        super(type, ref);
    }

    public MapObj(MacroGame game, ObjType type, MacroRef ref) {
        super(game, type, ref);
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        setParam(G_PARAMS.POS_X, coordinates.x);
        setParam(G_PARAMS.POS_Y, coordinates.y);
        int x = this.coordinates.x - getDefaultSize() / 2;
        int y = this.coordinates.y - getDefaultSize() / 2;
        setParam(MACRO_PARAMS.MAP_POS_X, x, true);
        setParam(MACRO_PARAMS.MAP_POS_Y, y, true);
        setMapRenderPoint(new MacroCoordinates(x, y));

    }

    public abstract int getDefaultSize();

    public Coordinates getDefaultMapRenderPoint() {
        return mapRenderPoint;
    }

    public void setMapRenderPoint(Coordinates mapRenderPoint) {
        this.mapRenderPoint = mapRenderPoint;
    }

}
