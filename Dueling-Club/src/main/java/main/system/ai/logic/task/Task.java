package main.system.ai.logic.task;

import main.entity.obj.DC_HeroObj;
import main.system.ai.UnitAI;
import main.system.ai.logic.goal.Goal.GOAL_TYPE;

public class Task {
    private GOAL_TYPE type;
    private Object arg;
    private DC_HeroObj unit;
    private UnitAI ai;
    private boolean blocked;
    private boolean forced;

    // a more concrete directive than Goal; from which Actions can be derived
    public Task(UnitAI ai, GOAL_TYPE type, Object arg) {
        this.ai = ai;
        this.unit = ai.getUnit();
        this.type = type;
        this.arg = arg;
    }

    public Task(boolean forced, UnitAI ai2, GOAL_TYPE goal, Object object) {
        this(ai2, goal, object);
        this.setForced(forced);
    }

    @Override
    public String toString() {
        return ai.getUnit().getName() + "'s " + type + " Task - " + (arg != null ? arg : "");

    }

    public UnitAI getAI() {
        return ai;
    }

    public DC_HeroObj getUnit() {
        return unit;
    }

    /**
     * @return the type
     */
    public synchronized GOAL_TYPE getType() {
        return type;
    }

    /**
     * @return the arg
     */
    public synchronized Object getArg() {
        return arg;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

}
