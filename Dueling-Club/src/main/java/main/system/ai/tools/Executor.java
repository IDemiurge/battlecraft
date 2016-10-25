package main.system.ai.tools;

import main.game.DC_Game;
import main.system.ai.logic.actions.Action;

public class Executor {

    public Executor(DC_Game game) {
        // TODO Auto-generated constructor stub
    }

    public boolean execute(Action action) {
        boolean result = false;
        try {
            result = action.activate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!result)
            action.getActive().actionComplete();

        return result;
    }
}