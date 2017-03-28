package tests.logic.combat;

import main.entity.active.DC_ActiveObj;
import main.game.logic.combat.attack.Attack;
import main.game.logic.combat.attack.DC_AttackMaster;
import main.game.logic.combat.damage.DamageCalculator;
import org.junit.Test;
import tests.entity.TwoUnitsTest;

/**
 * Created by JustMe on 3/28/2017.
 */
public class AttackDamageTest extends TwoUnitsTest {

    @Test
    public void testDamageCalc() {

        DC_ActiveObj action = entity.getAttack().getSubActions().get(0);
        //setAveraged(true);
        action.activateOn(entity2);
        Attack attack = DC_AttackMaster.getAttackFromAction(action);
        int precalc = DamageCalculator.precalculateDamage(attack);
//        DamageFactory.getDamageFromAttack(attack);
        assertEqualAndLog(   action.getDamageDealt().getAmount() , precalc,
         action+ " dmg precalc",
         action+ " Damage Dealt");

    }
}
