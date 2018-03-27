package main.game.core.atb;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.game.core.atb.AtbController.AtbUnit;
import main.system.GuiEventManager;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static main.system.GuiEventType.INITIATIVE_CHANGED;

/**
 * Created by JustMe on 3/26/2018.
 */
public class AtbUnitImpl implements AtbUnit {
    protected AtbController atbController;
    protected Unit unit;
    protected float timeTillTurn;

    public AtbUnitImpl(AtbController atbController, Unit unit) {
        this.atbController = atbController;
        this.unit = unit;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public float getInitialInitiative() {
        return RandomWizard.getRandomFloatBetween() * AtbController.TIME_IN_ROUND * 0.25f;
    }

    @Override
    public float getAtbReadiness() {
        return StringMaster.getFloat(unit.getParam(PARAMS.C_INITIATIVE));
    }

    @Override
    public void setAtbReadiness(float i) {
        double value = (i);

        atbController.getManager().getGame().getLogManager().log(
         getUnit().getName() + " has " +
          value +
          "%" +
          " readiness");
        if (unit.getIntParam(PARAMS.C_INITIATIVE) == value)
            return;
        unit.setParam(PARAMS.C_INITIATIVE, value + "");
        GuiEventManager.trigger(
         INITIATIVE_CHANGED,
         new ImmutablePair<>(getUnit(), new ImmutablePair<>((int) Math.round(value * 10), getTimeTillTurn()))
        );
    }

    @Override
    public float getInitiative() {
        if (unit.canActNow())
            return new Float(unit.getParamDouble(PARAMS.N_OF_ACTIONS));
        return 0;
    }

    @Override
    public float getTimeTillTurn() {
        return timeTillTurn;
    }

    @Override
    public void setTimeTillTurn(float i) {
        if (timeTillTurn != i) {
            timeTillTurn = i;
            GuiEventManager.trigger(
             INITIATIVE_CHANGED,
             new ImmutablePair<>(getUnit(), new ImmutablePair<>(Math.round(getAtbReadiness() * 10)
              , getTimeTillTurn()))
            );
        }
    }
}
