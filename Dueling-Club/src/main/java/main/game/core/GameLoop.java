package main.game.core;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.Action;
import main.game.core.game.DC_Game;
import main.game.logic.action.context.Context;
import main.libgdx.anims.AnimMaster;
import main.rules.combat.ChargeRule;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;


/**
 * Created by JustMe on 3/23/2017.
 */
public class GameLoop {
    private static   Integer MAX_ANIM_TIME  ;
    private Unit activeUnit;
    private DC_Game game;
    private DC_ActiveObj activatingAction;

    public GameLoop(DC_Game game) {
        this.game = game;
    }

    public void start() {
        while (true) {
            roundLoop();
        }
    }

    //
    private void roundLoop() {
        game.getStateManager().newRound();
        while (true) {
            Boolean result = game.getTurnManager().nextAction();
            if (result == null)
                break;
            if (!result)
                continue;
            activeUnit = game.getTurnManager().getActiveUnit();
            if (activeUnit == null) break;


            if (makeAction())
                break;


        }
        game.getManager().endTurn();
    }

    public DC_Game getGame() {
        return game;
    }

    private Boolean makeAction() {

        Boolean result=null ;
        if (game.getManager().getActiveObj().isAiControlled()) {
            result = activateAction(waitForAI());
        } else {
            result = activateAction(waitForPlayerInput());
        }
//        if ()
        waitForAnimations();
        return result;
    }

    private void waitForAnimations() {
        if (MAX_ANIM_TIME!=null )
            if (MAX_ANIM_TIME>0)
        if (AnimMaster.getInstance().isDrawing())
        WaitMaster.waitForInput(WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, MAX_ANIM_TIME);
    }

    private Boolean activateAction(ActionInput input) {
        if (input == null )
            return true;
        boolean result=false;
        try {
            activatingAction = input.getAction();
            activatingAction.setTargetObj(input.getContext().getTargetObj());
            activatingAction.setTargetGroup(input.getContext().getGroup());
            result=   input.getAction().getHandler().activateOn(input.getContext());
        } catch (Exception e) {
            e.printStackTrace();
            getGame().getManager().unitActionCompleted(input.getAction(), true);
            return true;
        } finally {
            activatingAction = null;
        }if (!result) return false;
            int timeCost = input.getAction().getHandler().getTimeCost();
        Boolean endTurn = getGame().getRules().getTimeRule().
        actionComplete(input.getAction(), timeCost);
        if (!endTurn) {
            game.getManager().reset();
            if (ChargeRule.checkRetainUnitTurn(input.getAction())) {
                endTurn = null;
            }
        }

        getGame().getManager().unitActionCompleted(input.getAction(), endTurn);

        if (BooleanMaster.isTrue(endTurn))
            return true;
        else {
            game.getTurnManager().
             resetInitiative(false);
        }
        return endTurn;
    }

    private ActionInput waitForAI() {
        Action aiAction =
         game.getAiManager().getAction(game.getManager().getActiveObj());
        return new ActionInput(aiAction.getActive(), new Context(aiAction.getRef()));
    }


    private ActionInput waitForPlayerInput() {
        return (ActionInput) WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_INPUT);
    }


    public DC_ActiveObj getActivatingAction() {
        return activatingAction;
    }

    public static void setMaxAnimTime(int maxAnimTime) {
        MAX_ANIM_TIME = maxAnimTime;
    }
}
