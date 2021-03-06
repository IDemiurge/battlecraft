package main.elements.conditions;

import main.data.ability.AE_ConstrArgs;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.math.Formula;
import main.system.math.PositionMaster;

public class DistanceCondition extends NumericCondition {

    private String objRef;
    private String objRef2;
    int zDepth;
    private Formula distance;

    @AE_ConstrArgs(argNames = {"distance", "objRef", "objRef2"})
    public DistanceCondition(String distance, String objRef, String objRef2) {
        super(new Formula(distance), new Formula("[DISTANCE(" + objRef + "," + objRef2 + ")]-1"),
                false);
        this.setDistance(greater);
        this.objRef = objRef;
        this.objRef2 = objRef2;
    }

    @AE_ConstrArgs(argNames = {"distance", "objRef", "coordinates"})
    public DistanceCondition(Integer distance, String objRef, String coordinatesString) {
        super(distance + "", "[DIST(" + objRef + "," + coordinatesString + ")]");
        this.setDistance(greater);
    }

    public DistanceCondition(String distance, Boolean equal_less) {
        this(distance, KEYS.SOURCE.toString(), KEYS.MATCH.toString());
        if (equal_less)
            setEqual(equal);
        else {
            Formula buffer = getComparedValue();
            setComparedValue(getComparingValue());
            setComparingValue(buffer);
        }
    }

    public DistanceCondition(String distance) {
        this(distance, KEYS.SOURCE.toString(), KEYS.MATCH.toString());
    }

    @Override
    public boolean check(Ref ref) {
        if (objRef != null) {
            if (objRef2 != null) {
                return PositionMaster.getDistance(ref.getObj(objRef), ref.getObj(objRef2))
                         <= distance.getInt(ref);
            }
        }

        return super.check(ref);
    }

    public Formula getDistance() {
        return distance;
    }

    public void setDistance(Formula distance) {
        this.distance = distance;
    }

}
