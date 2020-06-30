package eidolons.libgdx.bf.overlays.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.google.inject.internal.util.ImmutableList;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.handlers.GridManager;
import eidolons.libgdx.screens.ScreenMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

public abstract class OverlayMap extends SuperActor {

    protected Map<Coordinates, Object> map;
    protected Function<Coordinates, Color> colorFunc;
    protected List<Pair<Vector2, TextureRegion>> drawMap;
    protected Map<Vector2, TextureRegion> drawMapOver;

    protected boolean screen;
    protected Map<Coordinates, TextureRegion> drawMapAlt;

    public OverlayMap() {
        bindEvents();
    }

    protected void bindEvents() {
        GuiEventManager.bind(getUpdateEvent(), p -> {
            update((Map<Coordinates, Object>) p.get());

        });
    }
    public void update(Map<Coordinates, Object> m) {
        map = m;
        drawMap = null;
        drawMapOver = null;
        drawMapAlt = null;
    }


    public void drawAlt(Batch batch, Coordinates v, TextureRegion r) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isOn())
            return;
        if (!screen) {
            // if (isDrawScreen()) {
            //     screen = true;
            //     ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
            //     draw(batch, parentAlpha);
            //     screen = false;
            //     ((CustomSpriteBatch) batch).resetBlending();
            // }
            // batch.setColor(new Color(1, 1, 1, 1));
            if (map == null)
                return;
            if (drawMapOver == null) {
                drawMapOver = new HashMap<>();
                for (Coordinates c : map.keySet()) {
                    fillOverlayMap(c, drawMapOver);
                }
            }
            if (drawMap == null && drawMapAlt == null) {
                if (isAlt()) {
                    fillMapAlt();
                } else {
                    fillMap();
                }
            }
        }
        if (drawMap != null)
            for (Pair<Vector2, TextureRegion> pair : drawMap) {
                Vector2 v = pair.getKey();
                batch.draw(pair.getRight(), v.x, v.y);
            }
        if (drawMapOver != null)
            for (Vector2 key : drawMapOver.keySet()) {
                // if (!checkCoordinateIgnored(key))
                batch.draw(drawMapOver.get(key), key.x, key.y);
            }
        if (drawMapAlt != null) {
            GridPanel grid = ScreenMaster.getGrid();
            for (Coordinates c : drawMapAlt.keySet()) {
                if (!grid.isDrawn(c)) {
                    continue;
                }
                TextureRegion r = drawMapAlt.get(c);
                initColor(c, screen, batch);
                drawAlt(batch, c, r);
            }

            batch.setColor(GdxColorMaster.WHITE);

        }
    }

    protected abstract boolean isOn();


    protected boolean isAlt() {
        return false;
    }

    protected void fillMapAlt() {
        drawMapAlt = new LinkedHashMap<>();
        //sort it for Z!

        for (Coordinates coordinates : map.keySet()) {
            Object o = map.get(coordinates);
            if (isUnder(coordinates, o))
                fillDrawMapAlt(drawMapAlt, coordinates, o);
        }
        for (Coordinates coordinates : map.keySet()) {
            Object o = map.get(coordinates);
            if (!isUnder(coordinates, o))
                fillDrawMapAlt(drawMapAlt, coordinates, o);
        }
    }

    protected boolean isUnder(Coordinates coordinates, Object o) {
        return false;
    }

    protected void fillMap() {
        drawMap = new LinkedList<>();
        for (Coordinates coordinates : map.keySet()) {
            List<DIRECTION> list = null;
            if (map.get(coordinates) instanceof DIRECTION) {
                DIRECTION d = (DIRECTION) map.get(coordinates);
                list = ImmutableList.of(d);
            } else {
                list = (List<DIRECTION>) map.get(coordinates);
            }
            Vector2 v = getV(coordinates, map.get(coordinates));
            fillDrawMap(drawMap, coordinates, list, v);
        }
    }

    protected Vector2 getV(Coordinates coordinates, Object o) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());
        v.set(v.x, v.y - 128);
        return v;
    }

    protected boolean isCustomDraw() {
        return GridManager.isCustomDraw();
    }

    protected void initColor(Coordinates c, boolean screen, Batch batch) {
        if (colorFunc == null) {
            return;
        }
        Color color = colorFunc.apply(c);
        //can we store a function of color from time?
        if (color == null) {
            color = GdxColorMaster.WHITE;
        }
        batch.setColor(color);

    }

    public void setColorFunc(Function<Coordinates, Color> colorFunc) {
        this.colorFunc = colorFunc;
    }

    protected boolean isDrawScreen() {
        return false;
    }

    protected void fillOverlayMap(Coordinates c, Map<Vector2, TextureRegion> drawMapOver) {
    }

    protected abstract void fillDrawMap(List<Pair<Vector2, TextureRegion>> batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v);

    protected void fillDrawMapAlt(Map<Coordinates, TextureRegion> draw,
                                  Coordinates coordinates, Object arg) {

    }

    protected EventType getColorUpdateEvent() {
        return null;
    }

    protected abstract EventType getUpdateEvent();



    protected boolean checkCoordinateIgnored(Coordinates coordinates) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());
        return checkCoordinateIgnored(v);
    }

    protected boolean checkCoordinateIgnored(Vector2 v) {
        float offsetX = v.x;
        float offsetY = v.y;
        return !ScreenMaster.getScreen().controller.
                isWithinCamera(offsetX, offsetY - 128, 128, 128);
    }

}