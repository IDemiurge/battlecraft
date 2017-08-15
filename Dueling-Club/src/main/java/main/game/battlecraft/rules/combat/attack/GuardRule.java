package main.game.battlecraft.rules.combat.attack;

import main.content.PARAMS;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.mode.STD_MODES;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.system.auxiliary.StringMaster;
import main.system.math.roll.RollMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 8/10/2017.
 */
public class GuardRule {

    public static BattleFieldObject checkTargetChanged(DC_ActiveObj action) {

        BattleFieldObject target = (BattleFieldObject) action.getTargetObj();
//         attack.getAttackedUnit();
        List<Unit> guards = new LinkedList<>();
        Collection<Unit> units = target.getGame().getUnitsForCoordinates(target.getCoordinates());
        for (Unit unit : units) {
            if (unit.isAlliedTo(target.getOwner())) {
                if (unit.getMode() == STD_MODES.GUARDING
                 || unit.checkStatus(STATUS.GUARDING))
                {
                    Ref ref= Ref.getCopy(action.getRef() );
                    ref.setTarget(target.getId());
                    if (!SneakRule.checkSneak(ref))
                        guards.add(unit);
                }
            }
        }
        //add special defenders - adjacent or in line
        //TODO sort
        for (Unit guard : guards) { if (guard==target) continue;
            if (action.isAttackAny()) {
                if (action.isRanged()) {
                    if (checkDefenderTakesMissile(action, guard))
                        return guard;
                    continue;
                }
                Attack attack = EffectFinder.getAttackFromAction(action);
                if (checkDefenderTakesAttack(attack, guard))
                    return guard;
            } else {
                if (checkDefenderTakesMissile(action, guard))
                    return guard;
            }
        }
        //what kind of animation would there be?
        return null ;
    }
    private static boolean checkDefenderTakesAttack(Attack attack, Unit guard) {
        Ref ref=   Ref.getCopy(attack.getRef());
        ref.setTarget(guard.getId());
        String success= StringMaster.getValueRef(KEYS.TARGET, PARAMS.INITIATIVE_MODIFIER)
         +"*(100+" +RollMaster.getVigilanceModifier(guard, attack.getAction())+
         ")/100/3 "   ;
        String fail=StringMaster.getValueRef(KEYS.SOURCE, PARAMS.INITIATIVE_MODIFIER)+
         "*(100+" +RollMaster.getDexterousModifier(guard, attack.getAction())+
         ")/100/"+attack.getAction().getIntParam(PARAMS.AP_COST);
        String log=" to defend "+attack.getAttackedUnit().getName() +
         " against " +attack.getAction().getName();
        boolean result = !RollMaster.roll(ROLL_TYPES.REACTION, success, fail, ref, log);
//        boolean result = RollMaster.roll(ROLL_TYPES.REACTION, ref);

        return result;
    }

    private static boolean checkDefenderTakesMissile(DC_ActiveObj action, Unit unit) {
        return false;
    }
}