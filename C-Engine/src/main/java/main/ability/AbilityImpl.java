package main.ability;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.elements.ReferredElement;
import main.elements.targeting.AutoTargeting;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;

public class AbilityImpl extends ReferredElement implements Ability {

    protected Targeting targeting;
    protected Effects effects;

    private boolean interrupted;
    private boolean forceTargeting = true;
    private boolean forcePresetTargeting;

    public AbilityImpl(Targeting t, Effect e) {
        targeting = t;
        if (e == null) {
            LogMaster.log(1, "null abil effects!");
        }
        if (e instanceof Effects) {
            effects = (Effects) e;
        } else {
            effects = new Effects(e);
        }
    }

    @Override
    public String toString() {
        return "Ability has effects: " + effects.toString();
    }

    @Override
    public boolean isInterrupted() {
        if (interrupted) {
            interrupted = false;
            return true;
        }
        boolean result = false;
        for (Effect effect : effects) {
            if (effect != null) {
                result |= effect.isInterrupted();
            }
        }
        return result;
    }

    @Override
    public void setInterrupted(boolean b) {
        this.interrupted = b;

    }

    @Override
    public boolean activate() {
        return activatedOn(ref);
    }

    public boolean activate(boolean transmit) {
        return false;
    }

    @Override
    public boolean activatedOn(Ref ref) {
        setRef(ref);

        //preCheck if targeting is overridden
        if (!(targeting instanceof AutoTargeting)) {
            if (!(targeting instanceof FixedTargeting)) {
                if (isForcePresetTargeting() || targeting == null) {
                    if (ref.getTarget() != null || ref.getGroup() != null) {
                        return resolve(); //without targeting.select()
                    } else {
                        return false; // inconsistent data
                    }
                }
            }
        }

        boolean selectResult = targeting.select(ref);
        ActiveObj a = ref.getActive();
        if (selectResult) {
            if (a != null) {
                a.setCancelled(null);
            }
            return resolve();
        } else {
            if (a != null) {
                if (a.isCancelled() != null) {
                    a.setCancelled(true);
                }
            }
            return false;
        }

    }

    @Override
    public boolean resolve() {
        LogMaster.log(0, "ABILITY_BEING_RESOLVED " + getClass().getSimpleName());
        Event event = new Event(STANDARD_EVENT_TYPE.ABILITY_BEING_RESOLVED, ref);
        if (game.fireEvent(event)) {

            GuiEventManager.trigger(GuiEventType.ABILITY_RESOLVES,
             new EventCallbackParam( this));

            return effects.apply(ref);
        } else {
            return false;
        }
    }

    @Override
    public void addEffect(Effect effect) {
        effects.add(effect);

    }

    @Override
    public Ref getRef() {
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        // does not clone
        this.game = ref.getGame();
        this.ref = ref;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setTargeting(Targeting targeting) {
        this.targeting = targeting;
    }

    public Effects getEffects() {
        return effects;
    }

    public void setEffects(Effects effects) {
        this.effects = effects;
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        return true;
    }

    public boolean isForceTargeting() {
        return forceTargeting;
    }

    public void setForceTargeting(boolean forceTargeting) {
        this.forceTargeting = forceTargeting;
    }

    public boolean isForcePresetTargeting() {
        return forcePresetTargeting;
    }

    public void setForcePresetTargeting(boolean forcePresetTargeting) {
        this.forcePresetTargeting = forcePresetTargeting;
    }

}
