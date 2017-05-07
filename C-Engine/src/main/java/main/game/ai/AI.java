package main.game.ai;

import main.entity.obj.Obj;
import main.game.ai.ActionTypeManager.ACTION_TYPES;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;

import java.util.Set;

public abstract class AI {
    protected AI_Controller controller;

    protected AI_Executor executor;
    protected MicroGame game;
    protected Player player;

    public AI(MicroGame game, Player player) {

        this.game = game;
        this.player = player;
    }

    public void init() {
        this.executor = new AI_Executor(this);
        this.controller = new AI_Controller(this);
    }



    public AI_Controller getController() {
        return controller;
    }

    public void setController(AI_Controller controller) {
        this.controller = controller;
    }

    public AI_Executor getExecutor() {
        return executor;
    }

    public void setExecutor(AI_Executor executor) {
        this.executor = executor;
    }

    public MicroGame getGame() {
        return game;
    }

    public void setGame(MicroGame game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


    public Set<Obj> getUnits() {
        return controller.getUnits();
    }

    public AI_Logic getLogic() {
        return controller.getLogic();
    }

    public ACTION_TYPES getAction() {
        return controller.getAction();
    }

    public Obj getUnit() {
        return controller.getUnit();
    }

    public Obj getActive() {
        return controller.getActive();
    }
}
