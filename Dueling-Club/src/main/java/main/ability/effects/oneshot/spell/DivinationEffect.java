package main.ability.effects.oneshot.spell;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.client.cc.logic.spells.DivinationMaster;
import main.entity.obj.unit.Unit;

public class DivinationEffect extends MicroEffect implements OneshotEffect {

    @Override
    public boolean applyThis() {
        Unit hero = (Unit) ref.getSourceObj();
        try {
            DivinationMaster.divine(hero);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }

        return true;
    }
}
