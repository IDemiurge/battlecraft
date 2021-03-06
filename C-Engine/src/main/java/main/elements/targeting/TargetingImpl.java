package main.elements.targeting;

import main.elements.Filter;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.math.Formula;

public abstract class TargetingImpl implements Targeting
// , Component
{
    protected Filter<Obj> filter;
    protected Formula numberOfTargets;
    protected boolean friendlyFire;
    protected boolean modsAdded;
    protected Ref ref;

    // protected Conditions conditions;

    @Override
    public String toString() {

        return getClass().getSimpleName()
         // + " with " + filter.toString()
         ;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TargetingImpl) {
            if (obj instanceof SelectiveTargeting) {
                if (!(this instanceof SelectiveTargeting)) {
                    return false;
                }
            }
            if (obj instanceof AutoTargeting) {
                if (!(this instanceof AutoTargeting)) {
                    return false;
                }
            }
            if (obj instanceof FixedTargeting) {
                if (!(this instanceof FixedTargeting)) {
                    return false;
                }
            }


            return ((TargetingImpl) obj).getConditions().equals(getConditions());
        }
        return false;
    }

    @Override
    public Ref getRef() {
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
    }

    @Override
    public Filter<Obj> getFilter() {
        if (filter == null) {
            filter = new Filter<>(getRef(), new Conditions());
        }
        return filter;
    }

    public Conditions getConditions() {
        return getFilter().getConditions();
    }

    public void setConditions(Conditions conditions) {
        if (filter == null) {
            filter = new Filter<>(getRef(), conditions);
        } else {
            filter.setConditions(conditions);
        }
    }

    public boolean isModsAdded() {
        return modsAdded;
    }

    public void setModsAdded(boolean modsAdded) {
        this.modsAdded = modsAdded;
    }

}
