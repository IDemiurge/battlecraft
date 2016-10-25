package main.ability.targeting;

import main.elements.targeting.TargetingImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;
import main.game.battlefield.DirectionMaster;

import java.util.LinkedList;
import java.util.List;

public class CoordinateTargeting extends TargetingImpl {
    private static final KEYS DEFAULT_KEY = KEYS.SOURCE;
    private UNIT_DIRECTION unitDirection;
    private DIRECTION direction;
    private String key;

    public CoordinateTargeting(DIRECTION d) {
        this.direction = (d);
    }

    public CoordinateTargeting(String key, UNIT_DIRECTION d) {
        this.unitDirection = (d);
        this.key = (key);
    }

    public CoordinateTargeting(UNIT_DIRECTION d) {
        this(DEFAULT_KEY.toString(), d);
    }

    public boolean select(Ref ref) {
        DC_Obj obj = (DC_Obj) ref.getObj(key);
        DIRECTION used_direction = direction;
        if (unitDirection != null) {
            DC_HeroObj unit = (DC_HeroObj) obj;
            used_direction = DirectionMaster.getDirectionByFacing(unit.getFacing(), unitDirection);
        }
        Coordinates coordinate = obj.getCoordinates().getAdjacentCoordinate(used_direction);
        List<DC_HeroObj> objects = obj.getGame().getObjectsOnCoordinate(coordinate);
        if (objects.size() == 0)
            ref.setTarget(obj.getGame().getCellByCoordinate(coordinate).getId());
        else if (objects.size() == 1)
            ref.setTarget(objects.get(0).getId());
        else
            ref.setGroup(new GroupImpl(new LinkedList<Obj>(objects)));
        return true;
    }
}