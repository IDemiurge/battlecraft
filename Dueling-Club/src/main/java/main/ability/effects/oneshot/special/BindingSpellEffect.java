package main.ability.effects.oneshot.special;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.continuous.CustomTargetEffect;
import main.content.CONTENT_CONSTS.SPELL_TAGS;
import main.content.properties.G_PROPS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.game.event.Event;
import main.system.ConditionMaster;

public class BindingSpellEffect extends DC_Effect {
    Boolean shareOrRedirect;
    private Conditions conditions;
    private BIND_TYPE type;

    public BindingSpellEffect(BIND_TYPE type, Condition c) {
        this.conditions = new Conditions(c);
    }

    @Override
    public boolean applyThis() {
        // TODO Auto-generated method stub


        Effects effects=null;
        if (!shareOrRedirect)
            effects = new Effects(new CustomTargetEffect(new FixedTargeting(
                    KEYS.TARGET2), new DuplicateEffect(true)),
                    new CustomTargetEffect(new FixedTargeting(KEYS.TARGET),
                            new InterruptEffect()));

        Effect EFFECT = new DuplicateSpellEffect(KEYS.TARGET.name(), false,
                true);
        EFFECT.setTargetGroup(ref.getGroup());
        effects = new Effects(EFFECT);
        Event.STANDARD_EVENT_TYPE event_type = Event.STANDARD_EVENT_TYPE.SPELL_RESOLVED;
        conditions.add(ConditionMaster.getPropCondition("EVENT_SPELL",
                G_PROPS.SPELL_TAGS, SPELL_TAGS.MIND_AFFECTING.name()));
        return false;
    }
}
