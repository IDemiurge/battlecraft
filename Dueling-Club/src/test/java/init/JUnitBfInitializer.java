package java.init;

import main.client.DC_Engine;
import main.game.core.game.DC_Game;
import main.test.PresetLauncher;
import main.test.PresetLauncher.LAUNCH;
import main.test.debug.GameLauncher;

import java.res.JUnitResources;
import java.tests.JUnitTests;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitBfInitializer {

    private static DC_Game game;
    private static JUnitTests tests;

    public static void main(String[] strings) {
//        PathFinder.setJUnitMode(true); to find all test/resources
        DC_Engine.systemInit();
        JUnitResources.init();
//        LogMaster.setJUnit(true); //log everything* or nothing to speed up
        LAUNCH launch = PresetLauncher.initLaunch(LAUNCH.JUnit.name());
//        if (JUnitTests.itemGenerationOff)
//            launch.itemGenerationOff=true; TODO other flags

        GameLauncher launcher = new GameLauncher(null , null);
        game = launcher.initDC_Game();
        game.start(true); //TODO
        tests = new JUnitTests();
        tests.run();

    }
}
