package main.game.ai.elements.actions;

import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_QuickItemAction;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;

/**
 * Created by JustMe on 3/3/2017.
 */
public class ActionFactory {
    public static DC_UnitAction getUnitAction(Unit unit, String name) {
        return unit.getAction(name, true);
    }

    public static Action newAction(DC_ActiveObj action, Ref ref) {
        if (action instanceof DC_QuickItemAction) {
            DC_QuickItemAction itemActiveObj = (DC_QuickItemAction) action;
            return new AiQuickItemAction(itemActiveObj.getItem(), ref);
        }
        // my change!

        return new Action(action, ref);
    }
}
