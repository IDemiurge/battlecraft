package eidolons.entity.item;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.enums.entity.ItemEnums.ARMOR_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;

public class DC_ArmorObj extends DC_HeroSlotItem {

    private ARMOR_TYPE armorType;

    public DC_ArmorObj(ObjType armor_type, Player originalOwner, GenericGame game, Ref ref) {
        super(armor_type, originalOwner, game, ref, DC_ContentValsManager.getArmorModifyingParams());
    }

    @Override
    public void applyMods() {
        super.applyMods();

        // initParam(PARAMS.SPELL_AP_PENALTY);
        // initParam(PARAMS.SPELL_FOC_PENALTY);
        // initParam(PARAMS.MOVE_AP_PENALTY);
        // initParam(PARAMS.MOVE_STA_PENALTY);
        // int mod = getIntParam(PARAMS.ATTACK_MOD);
        // getHero().multiplyParamByPercent(PARAMS.OFFHAND_ATTACK_MOD, mod,
        // false);
        // getHero().multiplyParamByPercent(PARAMS.ATTACK_MOD, mod, false);
        // mod = getIntParam(PARAMS.DEFENSE_MOD);
        // getHero().multiplyParamByPercent(PARAMS.DEFENSE_MOD, mod, false);

    }

    @Override
    public void addSpecialEffect(SPECIAL_EFFECTS_CASE case_type, Effect effects) {
        super.addSpecialEffect(case_type, effects);
        hero.addSpecialEffect(case_type, effects);
    }

    @Override
    public void apply() {
        initHero();
        // modifyParamByPercent(PARAMS.ARMOR,
        // Math.round(DC_Formulas.ARMORER_ARMOR_MOD
        // * getHero().getIntParam(PARAMS.ARMORER_MASTERY))); [OUTDATED]

        // main.system.auxiliary.LogMaster.log(1, "Armor: "
        // + getIntParam(PARAMS.ARMOR));
        // modifyParamByPercent(PARAMS.DURABILITY_SELF_DAMAGE_MOD,
        // (100 - getHero().getIntParam(PARAMS.ARMORER_MASTERY)));
        super.apply();

    }

    @Override
    protected void applyPenaltyReductions() {
        int penalty_reduction = -getHero().getIntParam(PARAMS.STRENGTH);

        modifyParameter(PARAMS.DEFENSE_MOD, -penalty_reduction, 100, true);
        modifyParameter(PARAMS.ATTACK_MOD, -penalty_reduction, 100 // getType().getType().getParams();
         // //original
         , true);
        modifyParameter(PARAMS.MOVE_ATB_COST_MOD, penalty_reduction, 0, true);
        modifyParameter(PARAMS.MOVE_TOU_COST_MOD, penalty_reduction, 0, true);

        penalty_reduction = -getHero().getIntParam(PARAMS.WILLPOWER);
        modifyParameter(PARAMS.SPELL_TOU_COST_MOD, penalty_reduction, 0, true);
        modifyParameter(PARAMS.SPELL_ESS_COST_MOD, penalty_reduction, 0, true);

    }

    @Override
    public void init() {
        super.init(); // init actives and passives
        // add actives/passive to hero
        // init weapon type

    }

    @Override
    protected PARAMETER getDurabilityParam() {
        return PARAMS.ARMOR;
    }

    @Override
    public void setRef(Ref ref) {
        ref.setID(KEYS.ARMOR, getId());
        if (!equipped) {
            equipped(ref);
        }
//          super.setRef(ref);
    }

    @Override
    public void equipped(Ref ref) {
        super.equipped(ref);
        ref.setID(KEYS.ARMOR, getId());
        super.setRef(ref);
    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

    }

    public ARMOR_TYPE getArmorType() {
        if (armorType == null) {
            armorType = new EnumMaster<ARMOR_TYPE>().
             retrieveEnumConst(ARMOR_TYPE.class, getProperty(G_PROPS.ARMOR_TYPE));
        }
        return armorType;
    }

}
