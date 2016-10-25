package main.rules.mechanics;

import main.content.PARAMS;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_WeaponObj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

public class RangeRule {

	/*
     *
	 * some weapons/units may receive bonus/penalty based on this... 
	 * 
	 * status? stdCondition? 
	 * 
	 */

    public static int getMod(boolean dmg_atk, boolean close_long, DC_HeroObj source,
                             DC_HeroObj target, DC_WeaponObj weapon, DC_ActiveObj action) {
        PARAMS param = null;
        if (close_long)
            param = dmg_atk ? PARAMS.CLOSE_QUARTERS_DAMAGE_MOD : PARAMS.CLOSE_QUARTERS_ATTACK_MOD;
        else
            param = dmg_atk ? PARAMS.LONG_REACH_DAMAGE_MOD : PARAMS.LONG_REACH_ATTACK_MOD;
        int mod = 100;
        MathMaster.applyModIfNotZero(mod, source.getIntParam(param));
        MathMaster.applyModIfNotZero(mod, weapon.getIntParam(param));
        MathMaster.applyModIfNotZero(mod, action.getIntParam(param));
        mod -= 100;
        return mod;

    }

    public static Boolean isCloseQuartersOrLongReach(DC_HeroObj target, DC_HeroObj source,
                                                     DC_WeaponObj weapon, DC_ActiveObj action) {
        int distance = PositionMaster.getDistance(source, target);

        if (action.isThrow()) {
            if (distance < 1)
                return true;
            if (distance > action.getIntParam(PARAMS.RANGE, true))
                return false;
            return null;
        }
        if (action.isRanged())
            if (distance < 2)
                return true;
        if (distance < 1)
            return true;

        if (distance > weapon.getIntParam(PARAMS.RANGE, true))
            return false;
        // if (distance> weapon.getIntParam(PARAMS.AUTO_ATTACK_RANGE))
        // return false; // ?
        if (distance == 1)
            switch (weapon.getWeaponSize()) {
                case SMALL:
                    return false;
                case TINY:
                    return false;
            }
        return null;
    }

    public static Boolean isCloseQuartersOrLongReach(Ref ref) {

        // // TODO [UPDATE]
        // if (PositionMaster.getDistance(attacked, attacker) < 2) {
        // String message = "";
        // int mod = ranged.getIntParam(PARAMS.RANGED_PENALTY_ATK);
        // atk_mod = 100 - mod;
        // message = "Ranged attack on adjacent unit applies attack penalty: " +
        // mod + "%";
        // mod = ranged.getIntParam(PARAMS.RANGED_PENALTY_DMG);
        // dmg_mod = 100 - mod;
        // if (!precalc)
        // if (mod != 0) {
        // message = "Ranged attack on adjacent unit applies damage penalty: " +
        // mod
        // + "%";
        // weapon.getGame().getLogManager().log(message);
        // }
        // } else {
        // int mod = ranged.getIntParam(PARAMS.MELEE_PENALTY_ATK);
        // atk_mod = atk_mod - mod;
        // mod = ranged.getIntParam(PARAMS.MELEE_PENALTY_DMG);
        // dmg_mod = dmg_mod - mod;
        // }
        return null;
    }

}
