package main.test.libgdx.TestGameCreation;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.test.libgdx.G_Engine;

/**
 * Created by PC on 01.11.2016.
 */
public class MyGameLaunch {
    static LwjglApplication app;
    public static int wid = 600;
    public static int hei = 800;
    public static void main(String[] args) {
        LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
//        configuration.samples = 3;
//        configuration.resizable = false;
        configuration.width = wid;
        configuration.height = hei;
        app =  new LwjglApplication (new GameLoop(),configuration);
    }
}
