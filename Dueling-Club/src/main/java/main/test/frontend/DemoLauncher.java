package main.test.frontend;

import com.badlogic.gdx.Game;
import main.game.battlecraft.DC_Engine;
import main.game.core.game.DC_Game;

public class DemoLauncher {
    private Game frontGame;
    private DC_Game coreGame;

    public DemoLauncher() {

        frontGame = new Game() {
            @Override
            public void create() {

            }
        };

        DC_Engine.systemInit();

        DC_Engine.init();

        coreGame = new DC_Game(false);
        coreGame.init();
        DC_Game.setGame(coreGame);
        coreGame.start(true);



    }

    public static void main(String[] args) {
        new DemoLauncher();
    }
}
