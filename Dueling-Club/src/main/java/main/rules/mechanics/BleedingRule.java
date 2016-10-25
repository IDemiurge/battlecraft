package main.rules.mechanics;

import main.ability.effects.Effect.MOD;
import main.ability.effects.oneshot.common.ModifyCounterEffect;
import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.content.OBJ_TYPES;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.elements.conditions.ObjTypeComparison;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.MicroGame;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.rules.DC_RuleImpl;
import main.system.ConditionMaster;
import main.system.auxiliary.StringMaster;

/**
 * Add bleeding counters for every 1% below 25%. Lose 1% max endurance per turn
 * for each counter
 *
 * @author JustMe
 */

public class BleedingRule extends DC_RuleImpl {
    private static final Integer THRESHOLD = 20;
    private static final Integer MODIFIER = 10;

    public BleedingRule(MicroGame game) {
        super(game);
    }

    @Override
    public void apply(Ref ref) {
        super.apply(ref);
    }

    @Override
    public boolean check(Event event) {
        return super.check(event);
    }

    @Override
    public void initEffects() {
        // LIMIT BY MAX
        effects = new ModifyCounterEffect(STD_COUNTERS.Bleeding_Counter.getName(),
                MOD.MODIFY_BY_CONST,

                "{ACTIVE_PARAMS.BLEEDING_MOD}/100*"
                        + StringMaster.wrapInParenthesis(
                        // TODO formula?
                        THRESHOLD + "-" + "({TARGET_C_TOUGHNESS}*100/"
                                + "{TARGET_TOUGHNESS})*" + MODIFIER + "/100")

        );
    }

    @Override
    public void initConditions() {
        // DAMAGE TYPE CHECK? event_damage_type?
        conditions = new Conditions(ConditionMaster.getAliveCondition(KEYS.TARGET), ConditionMaster
                .getLivingCondition("target"), new NotCondition(new ObjTypeComparison(
                OBJ_TYPES.BF_OBJ, "target")), new NumericCondition("{TARGET_TOUGHNESS}*"
                + THRESHOLD + "/100"

                , "{TARGET_C_TOUGHNESS}"));

    }

    @Override
    public void initEventType() {
        this.event_type = STANDARD_EVENT_TYPE.UNIT_IS_DEALT_TOUGHNESS_DAMAGE;
    }

}