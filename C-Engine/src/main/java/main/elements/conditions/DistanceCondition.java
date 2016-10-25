package main.elements.conditions;

import main.data.ability.AE_ConstrArgs;
import main.entity.Ref.KEYS;
import main.system.math.Formula;

public class DistanceCondition extends NumericCondition {

    int zDepth;
    private Formula distance;

    @AE_ConstrArgs(argNames = {"distance", "objRef", "objRef2"})
    public DistanceCondition(String distance, String objRef, String objRef2) {
        super(new Formula(distance), new Formula("[DISTANCE(" + objRef + "," + objRef2 + ")]-1"),
                false);
        this.setDistance(greater);
    }

    public DistanceCondition(String distance) {
        this(distance, KEYS.SOURCE.toString(), KEYS.MATCH.toString());
    }

    @Override
    public boolean check() {

        return super.check();
    }

    public Formula getDistance() {
        return distance;
    }

    public void setDistance(Formula distance) {
        this.distance = distance;
    }

}