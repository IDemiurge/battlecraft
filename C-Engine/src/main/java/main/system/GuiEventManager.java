package main.system;

import main.system.launch.CoreEngine;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class GuiEventManager {
    private static boolean vertx;

    public static boolean isVertx() {
        return vertx;
    }

    public static void setVertx(boolean vertx) {
        GuiEventManager.vertx = vertx;
    }

    public static void bind(GuiEventType type, final EventCallback event) {
        if (CoreEngine.isGraphicsOff())
            return;
        if (!vertx){
            GuiEventManagerOld.bind(type, event);
        }
        else {
            GuiEventManagerVertx.bind(type, event);
        }
    }

    public static void cleanUp() {
        if (!vertx){
            GuiEventManagerOld.cleanUp();
        }
        else {
            GuiEventManagerVertx.cleanUp();
        }

    }
    public static void bindSound(GuiEventType type, final EventCallback event) {

    }
    private static void checkSoundEvent(GuiEventType type, Object obj) {

    }

    public static void trigger(final GuiEventType type) {
        trigger(type, null);
    }

    public static void trigger(final GuiEventType type, Object obj) {
        if (CoreEngine.isGraphicsOff())
            return   ;
        checkSoundEvent(type, obj);
        if (!vertx){
            GuiEventManagerOld.trigger(type, obj);
        }
        else {
            GuiEventManagerVertx.trigger(type, obj);
        }
    }

    public static void processEvents() {
        if (!vertx){
            GuiEventManagerOld.processEvents();
        }
        else {
            GuiEventManagerVertx.processEvents();
        }
    }

}
