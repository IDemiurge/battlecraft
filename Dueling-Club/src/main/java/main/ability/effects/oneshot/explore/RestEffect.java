package main.ability.effects.oneshot.explore;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.attachment.AddBuffEffect;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.oneshot.mechanic.ModeEffect;
import main.content.PARAMS;
import main.content.mode.STD_MODES;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.system.math.Formula;

import java.util.List;

/**
 * Created by JustMe on 11/17/2017.
 */
public class RestEffect extends DC_Effect {


    @Override
    public boolean applyThis() {
        //whole party!
        /*
        add regen and wait()
        interrupt?
         remove buff?
         or rely on reset() ?

         block actions
         add shader
         */
        List<Unit> allies = getGame().getMetaMaster().getPartyManager().getParty().getMembers();
        for (Unit sub : allies) {
            Ref REF = sub.getRef().getTargetingRef(sub);
            new ModeEffect(STD_MODES.SLEEPING).apply(REF);

        }
        float time =120;
        Boolean result = getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().playerRests(time); //(Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.WAIT_COMPLETE);
        if (result) {
//            applyRested();
            for (Unit sub : allies) {
                Ref REF = sub.getRef().getTargetingRef(sub);
                 getRestedBuffEffect().apply(REF);
                new ModifyValueEffect(PARAMS.C_MORALE,
                 MOD.MODIFY_BY_PERCENT, "15").apply(REF);
            }
        }
        return true;
    }

    private Effect getRestedBuffEffect() {
        Effects effects = new Effects();
        effects.add(new ModifyValueEffect(PARAMS.STAMINA, MOD.MODIFY_BY_PERCENT, "25"));
        effects.add(new ModifyValueEffect(PARAMS.ESSENCE, MOD.MODIFY_BY_PERCENT, "25"));
        effects.add(new ModifyValueEffect(PARAMS.WILLPOWER, MOD.MODIFY_BY_PERCENT, "25"));

        effects.add(new ModifyValueEffect(PARAMS.AGILITY, MOD.MODIFY_BY_PERCENT, "25"));
        effects.add(new ModifyValueEffect(PARAMS.DEXTERITY, MOD.MODIFY_BY_PERCENT, "25"));
        effects.add(new ModifyValueEffect(PARAMS.SPELLPOWER, MOD.MODIFY_BY_PERCENT, "25"));
        effects.add(new ModifyValueEffect(PARAMS.STRENGTH, MOD.MODIFY_BY_PERCENT, "25"));
        return new AddBuffEffect("Rested", effects, new Formula("10"));
    }
}
