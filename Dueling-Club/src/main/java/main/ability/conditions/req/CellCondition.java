package main.ability.conditions.req;

import main.elements.conditions.standard.OccupiedCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitModel;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.Coordinates.UNIT_DIRECTION;
import main.game.bf.DirectionMaster;

public class CellCondition extends OccupiedCondition {
    boolean free;
    private UNIT_DIRECTION direction;

    public CellCondition(Boolean free, String obj_ref, UNIT_DIRECTION direction) {
        super(obj_ref);
        this.direction = direction;
        this.free = free;
    }

    public CellCondition(UNIT_DIRECTION direction) {
        this(true, KEYS.SOURCE.toString(), direction);
    }

    public CellCondition(String obj_ref, UNIT_DIRECTION direction) {
        this(true, obj_ref, direction);
    }

    public CellCondition(Boolean b) {
        this(b, null, null);
    }

    @Override
    public boolean check(Ref ref) {
        return (free) != super.check(ref);
    }

    @Override
    protected Coordinates getCoordinates(Ref ref) {
        if (obj_ref == null) {
            obj_ref = KEYS.MATCH.toString();
        }
        if (direction == null) {
            return super.getCoordinates(ref);
        }
        Obj obj = ref.getObj(obj_ref);
        FACING_DIRECTION f = null;
        if (obj instanceof DC_UnitModel) {
            f = ((DC_UnitModel) obj).getFacing();
        }
        if (f == null) {
            return null;
        }

        return super.getCoordinates(ref).getAdjacentCoordinate(
                DirectionMaster.getDirectionByFacing(f, direction));
    }
}
// boolean result = (free) ? !super.preCheck() : super.preCheck();
// if (!result) return false;
//
// if (getCoordinates().isInvalid()) return false;
//
//

