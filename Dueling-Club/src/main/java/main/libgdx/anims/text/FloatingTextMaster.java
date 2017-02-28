package main.libgdx.anims.text;

import com.badlogic.gdx.graphics.Color;
import main.ability.effects.ModeEffect;
import main.ability.effects.common.ModifyStatusEffect;
import main.elements.costs.Cost;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.game.ai.tools.target.EffectFinder;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.CompositeAnim;
import main.system.Producer;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;

import java.util.List;

/**
 * Created by JustMe on 2/7/2017.
 */
public class FloatingTextMaster {
    private static FloatingTextMaster instance;

    public FloatingTextMaster() {
        instance = this;
    }

    public static FloatingTextMaster getInstance() {
        if (instance == null) instance = new FloatingTextMaster();
        return instance;
    }

    public static void setInstance(FloatingTextMaster instance) {
        FloatingTextMaster.instance = instance;
    }

    private Color getColor(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case COSTS:
                Cost cost = (Cost) arg;
                return GdxColorMaster.getParamColor(cost.getPayment().getParamToPay());

        }
        return Color.RED;
    }

    private String getImage(Entity active, TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case CRITICAL:
                break;
            case SNEAK:
                break;
            case DODGE:
                break;
            case COSTS:
                Cost cost = (Cost) arg;
                return ImageManager.getValueIconPath(cost.getPayment().getParamToPay());
            case STATUS:
                break;
            case MODE:
                break;
        }
        return null;
    }

    private String getText(Entity active, TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case CRITICAL:
                return "Critical Attack!";
            case SNEAK:
                return "Sneak Attack!";
            case COSTS:
                Cost cost = (Cost) arg;
                return String.valueOf(-cost.getPayment().getLastPaid());
        }
        return arg.toString();
    }

    public boolean isEventDisplayable(Event e) {
        return getCase(e) != null;
    }

    private TEXT_CASES getCase(Event e) {
        if (e.getType() instanceof STANDARD_EVENT_TYPE) {
            switch ((STANDARD_EVENT_TYPE) e.getType()) {
                case COSTS_HAVE_BEEN_PAID:
                    return TEXT_CASES.COSTS;
                case ATTACK_CRITICAL:
                    return TEXT_CASES.CRITICAL;
                case ATTACK_DODGED:
                    return TEXT_CASES.DODGE;
            }
        }
        return null;
    }

    public void addFloatingTextForEventAnim(Event e, CompositeAnim compositeAnim) {
        TEXT_CASES CASE = getCase(e);
        if (CASE == null) return;
        ANIM_PART part = getPart(CASE);
        Anim anim = compositeAnim.getMap().get(part);
        Object[] args = CASE.getArgs(e);
        DC_ActiveObj active = (DC_ActiveObj) e.getRef().getActive();
        float delay = 0;
        for (Object arg : args) {
            FloatingText floatingText = getFloatingText(active, CASE, arg);
            floatingText.setDelay(delay);
            floatingText.setDuration(3);
            floatingText.setDisplacementX(0);
            floatingText.setDisplacementY(140);
            anim.initPosition(); // TODO rework this!
            floatingText.setPosition(CASE.atOrigin ? anim.getOrigin() : anim.getDestination());
            delay += floatingText.getDuration() / 2;

            anim.addFloatingText(floatingText
            );
            LogMaster.log(1, e + "***** adding floating text for " + anim + " : " + floatingText);

        }
    }

    private ANIM_PART getPart(TEXT_CASES aCase) {
        switch (aCase) {
            case COSTS:
                return ANIM_PART.MAIN;
        }
        return ANIM_PART.IMPACT;
    }


    public FloatingText getFloatingText(Entity active, TEXT_CASES CASE, Object arg) {

        FloatingText floatingText =
         new FloatingText(
          () -> getText(active, CASE, arg), () -> getImage(active, CASE, arg)
          , getColor(CASE, arg));


//        floatingText.setDisplacementX(x);
//        floatingText.setDisplacementY(y);
//        floatingText.setDuration(dur);
        return floatingText;
    }

    public enum TEXT_CASES {
        DEFAULT,
        REQUIREMENT,
        CRITICAL,
        SNEAK,
        DODGE,
        BLOCK,
        ROLL,

        COSTS(true, (e) -> {
            DC_ActiveObj a = (DC_ActiveObj) e.getRef().getActive();
            List<Cost> costs = a.getCosts().getCosts();
            costs.removeIf(c -> c.getPayment().getLastPaid() == 0
//            getAmountFormula().toString().isEmpty()
            );
            return costs.toArray();
        }),
        STATUS(
         false, (e) -> {
            ModifyStatusEffect ef = (ModifyStatusEffect)
             EffectFinder.getFirstEffectOfClass((DC_ActiveObj) e.getRef().getActive(), ModifyStatusEffect.class);
//                if (ef==null )
            return ef.getValue().split(";");
        }),
        MODE(
         false, (e) -> {
            ModeEffect ef = (ModeEffect)
             EffectFinder.getFirstEffectOfClass((DC_ActiveObj) e.getRef().getActive(), ModifyStatusEffect.class);
            return new Object[]{
             ef.getMode()
            };
        });
        public boolean atOrigin;
        private Producer<Event, Object[]> producer;

        TEXT_CASES() {

        }

        TEXT_CASES(boolean atOrigin, Producer<Event, Object[]> producer) {
            this.atOrigin = atOrigin;
            this.producer = producer;
        }

        public Object[] getArgs(Event e) {
            if (producer == null)
                return new Object[]{
                 "arg!"
                };
            return producer.produce(e);
        }
    }

}