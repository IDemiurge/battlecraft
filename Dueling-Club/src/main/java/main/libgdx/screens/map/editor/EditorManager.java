package main.libgdx.screens.map.editor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import main.content.DC_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.entity.type.ObjType;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.MacroRef;
import main.game.module.adventure.entity.MacroObj;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Region;
import main.libgdx.GdxMaster;
import main.libgdx.screens.map.editor.EditorControlPanel.MAP_EDITOR_MOUSE_MODE;
import main.libgdx.screens.map.obj.MapActor;
import main.system.GuiEventManager;
import main.system.MapEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/10/2018.
 */
public class EditorManager {
    static MAP_EDITOR_MOUSE_MODE mode;
    private static Map<MapActor, MacroObj> actorObjMap = new HashMap<>();

    public static void remove(int screenX, int screenY) {
        try {
            modify(false, screenX, screenY);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }
    public static void add(int screenX, int screenY) {
        try {
            modify(true, screenX, screenY);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }
        public static void modify(boolean addOrRemove, int screenX, int screenY) {
        MAP_EDITOR_MOUSE_MODE mode = EditorManager.getMode();
       /*
       add listener to each actor
        */
        Vector2 v = EditorMapView.getInstance().getMapStage().
         screenToStageCoordinates(new Vector2(screenX, screenY));
        int x = (int) v.x;
        int y = (int) v.y;
        if (mode != null)
            switch (mode) {
                case POINT:
                    MacroManager.getPointMaster().clicked(x, y);
                    return;
                case EMITTER:
                    if (addOrRemove) {
                        EditorMapView.getInstance().getEditorParticles().clicked(x, y);

                    } else {
                        EditorMapView.getInstance().getEditorParticles().removeClosest(x, y);
                    }


                    return;
            }

        ObjType type = EditorMapView.getInstance().getGuiStage().getPalette().getSelectedType();
        if (type == null)
            return;
        MacroObj obj = create(type);
        added(obj, screenX, screenY);
    }

    public static void added(MacroObj obj, Integer x, Integer y) {
        if (x != null)
            obj.setX((int) (x + EditorMapView.getInstance().getCamera().position.x
             - GdxMaster.getWidth() / 2));
        if (y != null)
            obj.setY((int) (GdxMaster.getHeight() / 2 - y +
             EditorMapView.getInstance().getCamera().position.y));
        assignRegion(obj, x, y);
        GuiEventManager.trigger((obj instanceof Place) ?
         MapEvent.CREATE_PLACE :
         MapEvent.CREATE_PARTY, obj);
    }

    private static void assignRegion(MacroObj obj, Integer x, Integer y) {
        Region r = null;
        for (Region sub : obj.getGame().getRegions()) {
//        if (new Rectangle(sub.getX(), sub.getY(), sub.getWidth(),
//         sub.getHeight()).contains(new Point(x, y)))
            r = sub;
        }
        obj.getRef().setRegion(r);
    }

    private static <E extends MapActor> MacroObj create(ObjType type) {
        MacroRef ref = new MacroRef(MacroGame.game);
        if (type.getOBJ_TYPE_ENUM() == MACRO_OBJ_TYPES.PLACE)
            return new Place(MacroGame.game, type, ref);
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.PARTY)
            try {
                MacroParty party = new MacroParty(type, MacroGame.game, ref);
                party.getParty().initMembers();
                return party;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        return null;
    }

    public static <E extends MapActor> void remove(E actor) {
        MacroObj obj = actorObjMap.remove(actor);
        MacroGame.getGame().getState().removeObject(obj.getId());
        GuiEventManager.trigger(MapEvent.REMOVE_MAP_OBJ, actor);
    }

    public static <E extends MapActor> EventListener getMouseListener(E actor) {
        return EditorInputController.getListener(actor);
    }

    public static MAP_EDITOR_MOUSE_MODE getMode() {
        return mode;
    }

    public static void setMode(MAP_EDITOR_MOUSE_MODE mode) {
        EditorManager.mode = mode;
    }

    public static void map(MapActor actor, MacroObj obj) {
        actorObjMap.put(actor, obj);
    }

    public static void undo() {
        switch (mode) {
            case CLEAR:
                break;
            case TRACE:
                break;
            case ADD:
                break;
            case EMITTER:
                EditorMapView.getInstance().getEditorParticles().removeLast();
                break;
        }
    }

}