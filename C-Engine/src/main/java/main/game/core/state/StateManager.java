package main.game.core.state;

import main.ability.effects.Effect;
import main.content.OBJ_TYPE;
import main.elements.triggers.Trigger;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.core.game.GameManager;
import main.game.logic.event.Event;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.Map;


/**
 * Created by JustMe on 2/15/2017.
 */
public abstract class StateManager {

    protected Game game;
    protected GameState state;

    public StateManager(GameState state) {
        this.state = state;
        this.game = state.getGame();
    }

    public void afterEffects() {
        for (Obj obj : state.objMap.values()) {
            obj.afterEffects();
        }
    }

    public Game getGame() {
        return game;
    }

    public GameState getState() {
        return state;
    }

    public void resetAllSynchronized() {
        state.getTriggers().removeIf(trigger -> trigger.isRemoveOnReset());
        getManager().checkForChanges(false);
        allToBase();
        checkCounterRules();
        applyEffects(Effect.ZERO_LAYER);
        resetUnitObjects();
        resetRawValues();
        applyEffects(Effect.BASE_LAYER);
        afterEffects();
        applyEffects(Effect.SECOND_LAYER);
        applyEffects(Effect.BUFF_RULE);
        checkContinuousRules();
        afterBuffRuleEffects();
        resetCurrentValues();
        makeSnapshotsOfUnitStates();
    }

    protected abstract void makeSnapshotsOfUnitStates();


    protected abstract void afterBuffRuleEffects();

    public abstract boolean checkObjIgnoresToBase(Obj obj);

    protected abstract void resetCurrentValues();

    protected abstract void allToBase();

    private GameManager getManager() {
        return state.getGame().getManager();
    }


    public void checkTriggers(Event e) {
        if (state.getTriggers().size() == 0) {
            return;
        }
        LogMaster.log(0, state.getTriggers().size() + "");
        for (Trigger trigger : state.triggers) {

            trigger.check(e);
        }
    }


    public void applyEffects(int layer) {
        applyEffects(layer, null);
    }

    public void applyEffects(int layer, Obj unit) {
        if (state.getEffects().size() == 0) {
            return;
        }
        for (Effect effect : state.effects) {
            if (effect.getLayer() == layer) {
                if (unit != null) {
                    if (effect.getRef().getTargetObj() != unit) {
                        continue;
                    }
                }
                Obj target = effect.getRef().getTargetObj();
                if (checkObjIgnoresToBase(target))
                    continue;

                if (LOG_CHANNEL.EFFECT_DEBUG.isOn())
                    LogMaster.log(LOG_CHANNEL.EFFECT_DEBUG, layer
                     + " Layer, applying effect : " + state.effects);

                if (!effect.apply()) {
                    if (LOG_CHANNEL.EFFECT_DEBUG.isOn())
                        LogMaster.log(LogMaster.EFFECT_DEBUG, layer
                         + " Layer, effect failed: " + state.effects);
                }
            }
        }
    }

    public void addTrigger(Trigger t) {
        this.state.getTriggers().add(t);
    }

    public void addEffect(Effect effect) {
        effect.initLayer();
        // effect.getRef().getBasis(); // TODO ref cloning revealed its purpose
        // at
        // last!!!
        state.getEffects().add(effect);
        if (LogMaster.EFFECT_DEBUG_ON)
            LogMaster.log(LogMaster.EFFECT_DEBUG, effect.getClass()
             .getSimpleName()
             + " effect added : " + state.getEffects().size() + state.effects);
        // if (game.isStarted())
        // resetAllSynchronized();
    }


    public void addObject(Obj obj) {

        state.objMap.put(obj.getId(), obj);

        OBJ_TYPE TYPE = obj.getOBJ_TYPE_ENUM();
        if (TYPE == null) {
//            LogMaster.log(1, obj.getNameAndId() + " has no TYPE!");
            return;
        }
        Map<Integer, Obj> map = state.getObjMaps().get(TYPE);
        if (map == null) {
            return;
        }

        map.put(obj.getId(), obj);

    }

    public ObjType getTypeById(Integer id) {
        if (id == null) {
            LogMaster.log(LogMaster.CORE_DEBUG,
             "searching for type by null id");
            return null;
        }
        try {
            return state.getTypeMap().get(id);
        } catch (Exception e) {
            LogMaster
             .log(LogMaster.CORE_DEBUG_1, "no type found by id " + id);
            return null;
        }
    }


    public void resetRawValues() {
    }

    public abstract void checkContinuousRules();

    public abstract void checkCounterRules();

    public void resetUnitObjects() {

    }


    public void refreshAll() {
    }

    public void resetValues() {
    }

    public abstract void checkRules(Event e);

    public abstract void clear();
}
