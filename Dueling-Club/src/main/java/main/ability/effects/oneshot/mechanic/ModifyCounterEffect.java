package main.ability.effects.oneshot.mechanic;

import main.ability.effects.*;
import main.content.DC_ContentManager;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.data.ability.AE_ConstrArgs;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.item.DC_HeroItemObj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.entity.CounterMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.math.MathMaster;

public class ModifyCounterEffect extends MicroEffect  implements OneshotEffect, ResistibleEffect, ReducedEffect {

    private String counterName;
    private MOD modtype;
    private Integer resistanceMod;


    public ModifyCounterEffect(String name, MOD modtype, Formula formula) {
        this.modtype = modtype;
        this.formula = formula;
        this.counterName = name;
        mapThisToConstrParams(name, modtype, formula);
    }

    public ModifyCounterEffect(COUNTER counter, MOD modtype, String amount) {
        this(counter.getName(), modtype, amount);
    }

    @AE_ConstrArgs(argNames = {"name","modtype","amount",})
    public ModifyCounterEffect(String name, MOD modtype, String amount) {
        this(name, modtype, new Formula(amount));
    }

    public String toString() {
        return modtype.toString() + " Counter: " + formula;
    }

    @Override
    public boolean applyThis() {

        Ref REF = Ref.getCopy(ref);
        Integer amount = formula.getInt(ref);

//        MODIFY_BY_CONST Counter: {ACTIVE_PARAMS.BLEEDING_MOD}/100*(20-({TARGET_C_TOUGHNESS}*100/{TARGET_TOUGHNESS})*10/100)
        if (getResistanceMod() != null) {
            amount = MathMaster.applyMod(amount, getResistanceMod());
        }
        int mod = 0;
        if (ref.getTargetObj() instanceof DC_HeroItemObj) {
            try {
                mod = ref.getSourceObj().getIntParam(
                        DC_ContentManager.getCoatingAppliedModParam(CounterMaster
                                .findCounterConst(counterName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Integer modValue = MathMaster.addFactor(amount, mod);
        REF.setAmount(modValue);
        REF.setValue(KEYS.STRING, counterName);
        new Event(STANDARD_EVENT_TYPE.COUNTER_BEING_MODIFIED, REF).fire();
        boolean result = false;
        switch (modtype) {
            case MODIFY_BY_CONST:
                result = ref.getTargetObj().modifyCounter(counterName, modValue);
                break;
            case MODIFY_BY_PERCENT:
                result = ref.getTargetObj().modifyCounter(counterName, modValue);
                break;
            case SET:
                result = ref.getTargetObj().setCounter(counterName, modValue);
                break;
            default:
                break;

        }
        if (CoreEngine.isPhaseAnimsOn())
        if (result) {
            try {
                getAnimation().addPhaseArgs(PHASE_TYPE.COUNTER, counterName, modtype, modValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }
        REF.setAmount(ref.getTargetObj().getCounter(counterName));

        return new Event(STANDARD_EVENT_TYPE.COUNTER_MODIFIED, REF).fire();
    }

    @Override
    public Integer getResistanceMod() {
        return resistanceMod;
    }

    @Override
    public void setResistanceMod(int mod) {
        this.resistanceMod = mod;

    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }
}
