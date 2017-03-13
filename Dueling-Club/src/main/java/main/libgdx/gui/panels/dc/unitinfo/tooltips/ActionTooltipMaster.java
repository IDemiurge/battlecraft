package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import main.content.DC_ValueManager;
import main.content.PARAMS;
import main.content.UNIT_INFO_PARAMS;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_WeaponObj;
import main.game.logic.combat.CriticalAttackRule;
import main.game.logic.combat.DefenseVsAttackRule;
import main.rules.combat.ForceRule;
import main.system.math.MathMaster;
import main.system.math.ModMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class ActionTooltipMaster {


    //    public int getRange3(DC_ActiveObj action){
//        return action.getIntParam(PARAMS.AUTO_ATTACK_RANGE)+1;
//    }
    private static String getDiceText(DC_ActiveObj action) {
        DC_WeaponObj weapon = action.getActiveWeapon();
        int dieSize = weapon.getMaterial().getModifier();
        Integer dice = weapon.getIntParam(PARAMS.DICE);
        return dice + "d" + dieSize;
    }

    private static String getDamageText(DC_ActiveObj action) {
//    new AttackCalculator()
        int damage = MathMaster.applyMod(
         action.getOwnerObj().getIntParam(action.isOffhand() ? PARAMS.OFF_HAND_DAMAGE : PARAMS.DAMAGE),
         action.getIntParam(PARAMS.DAMAGE_MOD));
        return damage + " + " + getDiceText(action);
    }

    public static String getStringForParameter(PARAMS p, DC_ActiveObj action) {
    if (p!= PARAMS.FORCE) {
        if (action.getIntParam(p) == 0) {
            return null; //don't show any text!
        }
        if (DC_ValueManager.isCentimalModParam(p))
            if (action.getIntParam(p) == 100) {
                return null;
            }
        if (p.getDefaultValue().equals(action.getParam(p))) {
            return null;
        }
    }
        switch (p) {
            case BASE_DAMAGE:
                return getDamageText(action);
            case DIE_SIZE:
                return getDiceText(action);
            case COUNTER_MOD:
                return "Counter";
            case INSTANT_ATTACK_MOD:
                return "Instant";
            case AOO_ATTACK_MOD:
                return "Opportunity";
            case ACCURACY:
                return getAccuracyDescription(action);
            case CRITICAL_MOD:
                return getCriticalDescription(action);
            case SNEAK_DAMAGE_MOD:
                return getSneakDescription(action);
            case FORCE_KNOCK_MOD:
                return getForcePushDescription(action);
            case FORCE_PUSH_MOD:
                return getForceKnockDescription(action);
            case FORCE_DAMAGE_MOD:
                return getForceDamageDescription(action);
            case FORCE_MAX_STRENGTH_MOD:
                return getForceMaxStrengthDescription(action);
            case BLEEDING_MOD:
                return getBleedDescription(action);
            case ARMOR_PENETRATION:
                return getArmorPenetrationDescription(action);
            case ARMOR_MOD:
                return getArmorModDescription(action);
            case IMPACT_AREA:
                return getAreaOfImpactDescription(action);}
        return null;
    }

    private static String getForceMaxStrengthDescription(DC_ActiveObj action) {
        return null;
    }

    private static String getForceDamageDescription(DC_ActiveObj action) {
        DAMAGE_TYPE type = DAMAGE_TYPE.BLUDGEONING;
        return "+ Inflicts " +
         action.getIntParam(PARAMS.FORCE_DAMAGE_MOD)+"% of Force as " +
         type.getName() +
         " damage";

    }

    private static String getArmorModDescription(DC_ActiveObj action) {
        return "";

    }
    private static String getAreaOfImpactDescription(DC_ActiveObj action) {
        Integer area = action.getIntParam(PARAMS.IMPACT_AREA);
        return "Impact Area " +
         area+": " +
         "Can fully ignore Armor with Cover<" +
         (100-area) +         "%";
    }

    private static String getArmorPenetrationDescription(DC_ActiveObj action) {
        return "";

    }

    public static  void test(DC_ActiveObj action) {
        test(action, UNIT_INFO_PARAMS.ACTION_TOOLTIP_PARAMS_TABLE);
        test(action, UNIT_INFO_PARAMS.ACTION_TOOLTIP_PARAMS_TEXT);
    }
        public static  void test(DC_ActiveObj action, PARAMS[] params) {
        for (PARAMS p : params){
            main.system.auxiliary.log.LogMaster.log(1, " "
             + getStringForParameter(p, action)
            );
        }
    }
    private static String getBleedDescription(DC_ActiveObj action) {

        return "Bleeding: Inflicts " + action.getIntParam(PARAMS.BLEEDING_MOD)+"% Bleed Counters";

    }

    private static String getSneakDescription(DC_ActiveObj action) {

        String damage = String.valueOf(
         ModMaster.getFinalModForAction(action, PARAMS.SNEAK_DAMAGE_MOD));
//    TODO really hide if default?
//    if (damage.equals(PARAMS.SNEAK_DAMAGE_MOD.getDefaultValue()))
//        {
//            damage = "";
//        }else {
        damage = damage + "% Damage, ";
//        }

        String attack = String.valueOf(
         ModMaster.getFinalModForAction(action, PARAMS.SNEAK_ATTACK_MOD)) +
         "% Attack, ";
        String penetration = String.valueOf(
         100 - ModMaster.getFinalModForAction(action, PARAMS.SNEAK_ARMOR_MOD)) +
         "% Armor Penetration, ";
        String defense = String.valueOf(
         ModMaster.getFinalModForAction(action, PARAMS.SNEAK_DEFENSE_MOD)) +
         "% Defense";
        return "Sneak: " +
         damage +
         attack +
         penetration +
         defense;
    }

    private static String getForcePushDescription(DC_ActiveObj action) {
        int weight_max = ForceRule.getMaxWeightPush(action);
        String roll_info = "";
        return "Push: targets with less than " + weight_max +
         "lb weight" + roll_info;
    }

    private static String getForceKnockDescription(DC_ActiveObj action) {
        int weight_max = ForceRule.getMaxWeightKnock(action);
        int weight_min = ForceRule.getMinWeightKnock(action);
        String roll_info = "";
        return "Knockdown: never rolled vs > " + weight_max +
         "lb, always win vs < " + weight_min + "lb (or Interrupt)" + roll_info;
    }


    private static String getCriticalDescription(DC_ActiveObj action) {
        int attack = action.getOwnerObj().getIntParam(action.isOffhand() ? PARAMS.OFF_HAND_ATTACK : PARAMS.ATTACK);
        int defense = action.getOwnerObj().getIntParam(PARAMS.DEFENSE); // last hit unit? 5*level? same as unit's?
        attack = MathMaster.applyMod(attack, action.getIntParam(PARAMS.ATTACK_MOD));
        int percentage = CriticalAttackRule.getCriticalDamagePercentage(action);
        int chance = CriticalAttackRule.getCriticalChance(attack, defense, action);
        if (chance <= 0) {
            defense = 0;
            chance = CriticalAttackRule.getCriticalChance(attack, defense, action);
            if (chance <= 0)
                return "Crit: Impossible";
        }

        return "Crit: has " +
         chance +
         " chance to deal" +
         percentage +
         " to targets with " +
         defense + " defense";
    }

    private static String getAccuracyDescription(DC_ActiveObj action) {
        int attack = action.getOwnerObj().getIntParam(action.isOffhand() ? PARAMS.OFF_HAND_ATTACK : PARAMS.ATTACK);
        int defense = 0;
        attack = MathMaster.applyMod(attack, action.getIntParam(PARAMS.ATTACK_MOD));
        int chance = DefenseVsAttackRule.getMissChance(attack, defense, action);
        if (chance <= 0) {
            defense = action.getOwnerObj().getIntParam(PARAMS.DEFENSE);
            chance = DefenseVsAttackRule.getMissChance(attack, defense, action);
            if (chance <= 0)
                return "Accuracy: Miss Impossible";
        }
        return "Accuracy: has " +
         chance +
         " chance to miss targets with " +
         defense + " defense";
    }

}
