package main.ability.conditions.req;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;

public class CostCondition extends MicroCondition {
    boolean spell;
    private String actionName;

    public CostCondition(String actionName, Boolean spell) {
        this.actionName = actionName;
        this.spell = spell;

    }

    public CostCondition(String actionName) {
        this(actionName, false);
    }

    @Override
    public boolean check(Ref ref) {
        Unit hero = (Unit) ref.getTargetObj();

        DC_ActiveObj action;

        if (spell) {
            action = hero.getSpell(actionName);
        } else {
            action = hero.getAction(actionName);
        }
        if (action == null) {
            return false;
        }

        boolean canBeActivated = action.canBeActivated(ref, true);
        if (!canBeActivated) {
            return false;
        }
        return canBeActivated;
    }

}
