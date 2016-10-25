package main.rules.mechanics;

import main.ability.ActiveAbility;
import main.ability.effects.Effects;
import main.ability.effects.ModeEffect;
import main.ability.effects.RemoveBuffEffect;
import main.ability.effects.oneshot.common.AddTriggerEffect;
import main.ability.effects.oneshot.special.CastSpellEffect;
import main.content.PROPS;
import main.content.enums.STD_MODES;
import main.content.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.RefCondition;
import main.elements.conditions.StringComparison;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_UnitAction;
import main.entity.obj.top.DC_ActiveObj;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster;

/**
 * original targeting costs cooldown block
 *
 * @author JustMe
 */
public class ChannelingRule {

    private static final String PATH = "effects\\channeling\\";

    public static boolean activateChanneing(DC_ActiveObj spell) {

        // ActiveAbility spell_ability = ActivesConstructor
        // .mergeActiveList(spell, TARGETING_MODE.SINGLE);

        boolean result = true;
        ModeEffect modeEffect = new ModeEffect(STD_MODES.CHANNELING);
        String string = STD_MODES.CHANNELING.toString();
        if (spell instanceof DC_UnitAction)
            if (spell.checkProperty(G_PROPS.CUSTOM_PROPS)) {
                modeEffect.getModPropEffect().setValue(
                        spell.getProperty(G_PROPS.CUSTOM_PROPS));
            }
        Ref REF = spell.getRef().getCopy();
        REF.setTarget(spell.getOwnerObj().getId());
        // modeEffect.getAddBuffEffect().setEffect(effect)

        Condition conditions = new Conditions(new RefCondition(
                KEYS.EVENT_SOURCE, KEYS.SOURCE), new StringComparison(
                "{SOURCE_MODE}", string, true));

        CastSpellEffect castEffect = new CastSpellEffect(spell);
        castEffect.setForceTargeting(true);

        AddTriggerEffect triggerEffect = new AddTriggerEffect(
                STANDARD_EVENT_TYPE.UNIT_TURN_STARTED, conditions,
                new ActiveAbility(null, new Effects(
                        new RemoveBuffEffect(string), castEffect)));
        // triggerEffect
        // .getTrigger()
        // .getAbilities()
        // .addEffect(new RemoveBuffEffect(
        // STD_MODES.CHANNELING.getBuffName()));
        // triggerEffect.getTrigger().setOneShot(true);
        // triggerEffect.getTrigger().setForceTargeting(false);
        modeEffect.setReinit(false);
        modeEffect.getAddBuffEffect().addEffect(triggerEffect);
        modeEffect.getAddBuffEffect().setDuration(2);
        result &= modeEffect.apply(REF);
        return result;

    }

    public static void playChannelingSound(DC_ActiveObj active, boolean female) {
        // TODO try reverse the sounds too, who knows ;)
        String prop = active.getProperty(PROPS.CHANNELING_SOUND);

        if (StringMaster.isEmpty(prop)) {
            prop = generateSoundForSpell(active);
        }
        if (female)
            prop = "W_" + prop;

        String basePath = PathFinder.getSoundPath() + PATH + prop;
        SoundMaster.playRandomSoundVariant(basePath, true); // TODO find files
        // refactor!
    }

    private static String generateSoundForSpell(DC_ActiveObj spell) {
        // Aspect / Spell_type /

        // if (spell.getAspect()){
        //
        // }
        //
        // if (spell.getSpellType()){
        //
        // }

        return null;
    }

    // Targeting targeting = new FixedTargeting(KEYS.TARGET);
    // spell_ability.setTargeting(targeting);
    // Effect ELSEeffect = new
    // RemoveBuffEffect(modeEffect.getBuffTypeName());
    // Effect IFeffect = new CostEffect(spell);
    // ActiveAbility cost_ability = new ActiveAbility(new FixedTargeting(
    // KEYS.SOURCE), new IfElseEffect(IFeffect, new CostCondition(
    // spell), ELSEeffect));
    // Abilities abilities = new Abilities();
    // abilities.add(spell_ability);
    // abilities.add(cost_ability);
    // public static Costs getChannelingCosts(DC_SpellObj spell) {
    // // TODO
    // List<Cost> list = new LinkedList<>();
    // Cost apCost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING, PARAMS.C_N_OF_ACTIONS);
    // list.add(apCost);
    //
    // if (spell.getIntParam(PARAMS.CHANNELING_ESS_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_ESS_COST, PARAMS.C_ESSENCE);
    // list.add(cost);
    // }
    //
    // if (spell.getIntParam(PARAMS.CHANNELING_FOC_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_FOC_COST, PARAMS.C_FOCUS);
    // list.add(cost);
    // }
    // if (spell.getIntParam(PARAMS.CHANNELING_STA_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_STA_COST, PARAMS.C_STAMINA);
    // list.add(cost);
    // }
    // if (spell.getIntParam(PARAMS.CHANNELING_END_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_END_COST, PARAMS.C_ENDURANCE);
    // list.add(cost);
    // }
    // return new Costs(list);
    // }

}