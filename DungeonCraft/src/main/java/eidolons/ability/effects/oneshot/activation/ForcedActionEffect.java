package eidolons.ability.effects.oneshot.activation;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class ForcedActionEffect extends MicroEffect implements OneshotEffect {

    private String actionName;
    private KEYS key;

    public ForcedActionEffect(String actionName) {
        this.actionName = actionName;
    }

    public ForcedActionEffect(String actionName, KEYS target) {
        this(actionName);
        this.key = target;
    }

    @Override
    public boolean applyThis() {

        Unit unit = (Unit) ref.getTargetObj();
        Ref REF = unit.getRef();
        if (key != null) {
            REF.setTarget(ref.getId(key));
        }

        Action action = new Action(unit.getAction(actionName), REF);
        unit.getGame().getAiManager().getAI(unit).getForcedActions()
         .add(action);
        return true;
    }
}
