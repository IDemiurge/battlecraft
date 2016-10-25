package main.ability.effects;

import main.content.CONTENT_CONSTS.DAMAGE_MODIFIER;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.DamageMaster;
import main.rules.ForceRule;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.DamageSprite;
import main.system.math.Formula;

public class DealDamageEffect extends DC_Effect {
    private DAMAGE_TYPE damage_type;
    private boolean magical = true;
    private DamageSprite damageSprite;
    private DAMAGE_MODIFIER damage_mod;

    // private int damage_dealt = 0;

    // damage type?!
    public DealDamageEffect(Formula formula) {
        super();
        this.formula = formula;
    }

    public DealDamageEffect(Formula formula, Boolean magical) {
        this(formula);
        this.magical = magical;
    }

    public DealDamageEffect(Formula formula, DAMAGE_TYPE damage_type) {
        this(formula);
        this.damage_type = damage_type;
    }

    public DealDamageEffect(Formula formula, String damage_type, DAMAGE_MODIFIER damage_mod) {
        this(formula, new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class,
                damage_type));
        this.damage_mod = damage_mod;
    }

    @Override
    public boolean applyThis() {
        if (ref.getTargetObj() == null)
            return false;
        if (!(ref.getTargetObj() instanceof DC_HeroObj))
            return true; // TODO if cell, apply damage to corpses?
        DC_HeroObj targetObj = (DC_HeroObj) ref.getTargetObj();
        int amount = formula.getAppendedByModifier(ref.getFormula()).getInt(ref);
        DC_ActiveObj active = (DC_ActiveObj) ref.getActive();
        boolean spell = active instanceof DC_SpellObj;

        initDamageType();

        amount = applyReductions(targetObj, amount, active, spell);

        main.system.auxiliary.LogMaster.log(LogMaster.COMBAT_DEBUG, "Effect is dealing damage: "
                + amount + " to " + ref.getTargetObj().toString());

        if (damage_mod != null)
            ref.setValue(KEYS.DAMAGE_SOURCE, damage_mod.toString());
        ref.setValue(KEYS.DAMAGE_TYPE, damage_type.getName());
        int damage = DamageMaster.dealDamageOfType(damage_type, targetObj, ref, amount);

        // if (active.getIntParam(PARAMS.FORCE) == 0) // ONLY MAIN SPELL'S
        // DAMAGE
        if (damage > 0)
            if (!ref.isPeriodic())
                if (!ref.isTriggered()) {
                    if (!active.isAttack())
                        ForceRule.applyForceEffects(active);

                }

        // if (damageSprite == null)
        // damageSprite = new DamageSprite(this);
        // damageSprite.animate(ref);

        return true;

        // if (magical) {
        //
        // DamageMaster
        // .spellDamage((DC_HeroObj ) ref.getTargetObj(), ref, amount);
        // } else {
        // DamageMaster
        // .attack_damage((DC_HeroObj ) ref.getTargetObj(), (DC_HeroObj ) ref
        // .getSourceObj(), amount);
        // }

        // return true;

    }

    private int applyReductions(DC_HeroObj targetObj, int amount, DC_ActiveObj active, boolean spell) {
        if (getGame().getArmorMaster().checkCanShieldBlock(active, targetObj)) {
            int shieldBlock = getGame().getArmorMaster().getShieldDamageBlocked(amount, targetObj,
                    (DC_HeroObj) ref.getSourceObj(), active, null, damage_type);
            // event?
            amount -= shieldBlock;
        }

        if (damage_mod != null)
            if (damage_mod == DAMAGE_MODIFIER.QUIET)
                ref.setQuiet(true);
            else
                ref.setQuiet(false);
        if (spell) {
            // DC_SpellObj spellObj = (DC_SpellObj) active;
            // getGame().getLogManager().newLogEntryNode(true,
            // ENTRY_TYPE.SPELL_DAMAGE, spellObj);
            // getGame().getLogManager().doneLogEntryNode(ENTRY_TYPE.SPELL_DAMAGE);
        }
        int blocked = getGame().getArmorMaster().getArmorDamageEffectBlocked(amount, damage_type,
                targetObj, active);

        amount -= blocked;
        return amount;
    }

    private void initDamageType() {
        if (damage_type == null)
            try {
                damage_type = ((DC_Obj) ref.getObj(KEYS.ACTIVE)).getDamageType();
            } catch (Exception e) {
            }
        if (damage_type == null)
            if (magical)
                damage_type = DAMAGE_TYPE.MAGICAL;
            else
                damage_type = DAMAGE_TYPE.PHYSICAL;
    }

    @Override
    public String toString() {
        String string = "DMG: " + formula;
        if (damage_type != null)
            string += StringMaster.wrapInParenthesis(damage_type.getName());
        return string;
    }

    public DAMAGE_TYPE getDamage_type() {
        return damage_type;
    }

    public boolean isMagical() {
        return magical;
    }

    public DamageSprite getDamageSprite() {
        return damageSprite;
    }

    public DAMAGE_MODIFIER getDamage_mod() {
        return damage_mod;
    }
}