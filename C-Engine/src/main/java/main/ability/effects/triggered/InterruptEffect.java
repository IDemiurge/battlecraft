package main.ability.effects.triggered;

import main.ability.Interruptable;
import main.ability.effects.MicroEffect;
import main.ability.effects.TriggeredEffect;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class InterruptEffect extends MicroEffect implements TriggeredEffect {

    public InterruptEffect() {

    }

    public InterruptEffect(String OBJ_REF) {
    }

    @Override
    public boolean applyThis() {
        // if (OBJ_REF == null) {
        // game.getState().setInterrupted(true);
        //
        // return true;
        // }
        if (ref.getEvent() == null) {
            return false;
        }
        Ref REF = ref.getEvent().getRef();
        Interruptable i = REF.getEffect();
        // if (OBJ_REF != null) {
        i = (Interruptable) REF.getObj(KEYS.ACTIVE);
        // }
        if (i == null) {
            return false;
        }
        i.setInterrupted(true);

        return true;
    }

}
