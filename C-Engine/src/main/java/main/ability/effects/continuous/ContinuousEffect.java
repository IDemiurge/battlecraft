package main.ability.effects.continuous;

import main.ability.effects.*;
import main.content.ContentValsManager;
import main.entity.Ref;
import main.entity.obj.Attachment;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

public class ContinuousEffect extends MicroEffect {
    Integer basis; // static ability; remove CE when basis is removed
    Attachment attachment;
    private Effect effect;
    private int duration;

    public ContinuousEffect(Effect effect) {
        this.setEffect(effect);
        if ((effect instanceof ContinuousEffect)) {
            this.setEffect(((ContinuousEffect) effect).getEffect());
        }
        if (getEffect() != null) {
            getEffect().setQuietMode(true);
        }
    }

    public static Effect transformEffectToContinuous(Effect effect) {
        if (effect instanceof OneshotEffect) {
            throw new RuntimeException("OneshotEffect cannot be made Continuous!" + effect);
        }


        if ((effect instanceof Effects)) {
            for (Effect e : ((Effects) effect)) {
                if (!(e instanceof ContinuousEffect) && !(e instanceof AttachmentEffect)) {
                    {
                        ((Effects) effect).remove(e);
                        LogMaster.log(LOG_CHANNEL.EFFECT_PASSIVE_DEBUG, "effect wrapped " + e);
                        e = transformEffectToContinuous(e);
                        ((Effects) effect).add(e);

                    }
                }
            }
            return effect;
        } else {
            boolean isTransform = !(effect instanceof ContinuousEffect)
             && !(effect instanceof AttachmentEffect);
            if (isTransform) {
                if (effect instanceof ContainerEffect) {
                    ContainerEffect containerEffect = (ContainerEffect) effect;
                    isTransform = !(containerEffect.getEffect() instanceof AttachmentEffect);
                }
            }
            if (isTransform) {

                effect = new ContinuousEffect(effect);
                return effect;

            }

        }

        return effect;
    }

    @Override
    public String toXml() {
        return getEffect().toXml();
    }

    @Override
    public Effect getCopy() {
        return new ContinuousEffect(effect.getCopy());
    }

    @Override
    public String getTooltip() {
        return getEffect().getTooltip();
    }

    @Override
    public Formula getFormula() {
        return effect.getFormula();
    }

    public void setFormula(Formula newFormula) {
        getEffect().setFormula(newFormula);
    }

    @Override
    public void initLayer() {
        if (effect != null) {

            effect.initLayer();
            setLayer(effect.getLayer());
        } else {
            super.initLayer();
        }
    }

    @Override
    public int getLayer() {
        if (effect != null) {
            return effect.getLayer();
        }
        return super.getLayer();
    }

    public int tick() {
        duration--;
        if (duration > 1) {
            remove();
        }
        return duration;
    }

    public void remove() {
        effect.remove();
//        ref.getGame().getState().removeEffect(this);
        if (getTrigger() != null) {
            ref.getGame().getState().manager.removeTrigger(getTrigger());
        }

    }

    @Override
    public boolean apply(Ref ref) {
        setRef(ref);
        return apply();
    }

    @Override
    public boolean applyThis() {
        return getEffect().applyThis();
    }

    public boolean apply() {
        return getEffect().apply();
    }

    public Ref getRef() {
        return getEffect().getRef();
    }

    public void setRef(Ref ref) {
        super.setRef(ref);
        getEffect().setRef(this.ref);
    }

    @Override
    public boolean isContinuousWrapped() {
        return true;
    }

    public String toString() {
        return "Cont-" + getEffect().toString();
    }

    public int getDuration() {
        return ContentValsManager.INFINITE_VALUE;
    }

    public boolean isRetainAfterDeath() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setRetainAfterDeath(boolean retainAfterDeath) {
        // TODO Auto-generated method stub

    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
        effect.setContinuousWrapped(true);
    }

    public boolean checkRetainCondition() {
        // TODO Auto-generated method stub
        return false;
    }
}
