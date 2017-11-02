package main.libgdx.anims.particles.lighting;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import main.content.PARAMS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.MicroObj;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.GdxColorMaster;
import main.libgdx.GdxMaster;
import main.libgdx.bf.GridConst;
import main.libgdx.screens.DungeonScreen;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.GL20.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

/**
 * Created by PC on 19.11.2016.
 */
public class LightMap {
    private static World world;
    private static RayHandler rayHandler;
    private static float SECOND = 1000000000;
    private static float ambient = 0.05f;
    private static float ambientAlpha = 0.05f;
    private Color ambientColor;
    private static int testA;
    private static int testB;
    private static PointLight mouseLight;
    Box2DDebugRenderer debugRenderer;
    private Map<MicroObj, Body> bodyMap;
    private float cellWidth;
    private float cellHeight;
    private int rows;
    private int cols;
    private Map<Integer, FireLightProt> fireLightProtMap;
    private boolean valid;
    private DequeImpl<BattleFieldObject> units;
    private PARAMS lightParam;
    private PARAMS darkParam;
    private int LIGHT_MULTIPLIER = 30;


    public LightMap(DequeImpl<BattleFieldObject> units, int rows, int cols) {
        World world = new World(new Vector2(0, 0), true);
        init(units, world, new RayHandler(world), GridConst.CELL_W, GridConst.CELL_H, rows, cols);
    }


    public static void resizeFBOb() {
        testB += 50;
        testA += 50;
        rayHandler.resizeFBO(testA, testB);
        rayHandler.update();
    }

    public static void resizeFBOa() {
        testA -= 50;
        testB -= 50;
        rayHandler.resizeFBO(testA, testB);
        rayHandler.update();
    }

    public static void mouseMouseMove(float x, float y, float zoom) {
        mouseLight.setPosition(x, y);
        boolean checkoutX;
        boolean checkoutY;
        float x_distance_from_the_border = Math.abs(x - GdxMaster.getWidth());
        float y_distance_from_the_border = Math.abs(y - GdxMaster.getHeight());
        checkoutX = !(x_distance_from_the_border < LightingManager.mouse_light_distance_to_turn_off || x_distance_from_the_border > GdxMaster.getWidth() - LightingManager.mouse_light_distance_to_turn_off);
        checkoutY = !(y_distance_from_the_border < LightingManager.mouse_light_distance_to_turn_off || y_distance_from_the_border > GdxMaster.getWidth() - LightingManager.mouse_light_distance_to_turn_off);
        if (checkoutX & checkoutY) {
            mouseLight.setDistance(LightingManager.mouse_light_distance);
        } else {
            mouseLight.setDistance(0);
        }
    }

    public static void mouseLightDistance(float distance) {
        mouseLight.setDistance(distance);
    }

    public static float getAmbient() {
        return ambient;
    }

    //
    private void init(DequeImpl<BattleFieldObject> units, World world,
                      RayHandler rayHandler, float cellWidth, float cellHeight, int rows, int cols) {

        testA = 1600;
        testB = 900;

        this.units = units;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        if (rows > 0) {
            this.rows = rows - 1;
        } else {
            this.rows = 0;
        }
        if (cols > 0) {
            this.cols = cols - 1;
        } else {
            this.cols = 0;
        }

        LightMap.world = world;
        LightMap.rayHandler = rayHandler;
        LightMap.rayHandler.setBlur(true);
        LightMap.rayHandler.setBlurNum(15);
          ambientColor = new Color(0.2f, 0.1f, 0.3f, ambientAlpha);
        LightMap.rayHandler.setAmbientLight(ambientColor);
        LightMap.rayHandler.setAmbientLight(LightingManager.ambient_light);
        RayHandler.setGammaCorrection(true);
        debugRenderer = new Box2DDebugRenderer();
        mouseLight = new PointLight(rayHandler, 100, Color.RED, LightingManager.mouse_light_distance, 0, 0);
        LightingManager.setMouse_light(true);
        new ConeLight(rayHandler, 3, GdxColorMaster.ENEMY_COLOR, 200, 350, 350, 200, 100);

        updateMap();
//        bindEvents();
    }



    public static void setAmbient(float c) {
        rayHandler.setAmbientLight(c);

        rayHandler.update();
        ambient = c;
    }


    //    public void updateMap(Map<DC_HeroObj, BaseView> units) {
    public void updateMap() {
        new ConeLight(rayHandler, 3, GdxColorMaster.ENEMY_COLOR, 200, 350, 350, 200, 100);

        LogMaster.log(LogMaster.VISIBILITY_DEBUG,
         "UpdateMap method was called");
        valid = false;
        if (bodyMap != null) {
            bodyMap.clear();
        }

        if (fireLightProtMap != null) {
            for (Map.Entry<Integer, FireLightProt> entry : fireLightProtMap.entrySet()) {
                entry.getValue().dispose();
            }
            fireLightProtMap.clear();
        }

        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        for (int i = 0; i < bodies.size; i++) {
            world.destroyBody(bodies.get(i));
        }

        fireLightProtMap = new HashMap<>();
        bodyMap = new HashMap<>();
        this.units = DC_Game.game.getBfObjects();

        for (int i = 0; i < units.size(); i++) {
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.KinematicBody;
            Body body = world.createBody(bdef);
            lightParam = PARAMS.LIGHT_EMISSION; //ILLUMINATION
            darkParam = PARAMS.CONCEALMENT;
            BattleFieldObject unit = units.get(i);
            int lightStrength = unit.getIntParam(lightParam) - unit.getIntParam(darkParam);
            if (lightStrength > 0) {
//                emitters.add(unit);
                body.setTransform(units.get(i).getX() * cellWidth + cellWidth / 2, this.rows * cellHeight - units.get(i).getY() * cellHeight + cellHeight / 2, 0);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(cellWidth / 2, cellHeight / 2);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
                FireLightProt fireLightProt = new FireLightProt(world, rayHandler,
                 unit.getX() * cellWidth + cellWidth / 2,
                 unit.getY() *
                  cellHeight + cellHeight / 2,
                 lightStrength * LIGHT_MULTIPLIER, 360, SECOND);
                fireLightProt.attachToBody(body);
                fireLightProtMap.put(i, fireLightProt);
                valid = true;
            } else {
                body.setTransform(units.get(i).getX() * cellWidth + cellWidth / 2, this.rows * cellHeight - units.get(i).getY() * cellHeight + cellHeight / 2, 0);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(cellWidth / 2, cellHeight / 2);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
            }
            bodyMap.put(units.get(i), body);
        }
    }

    public void updatePos(MicroObj obj) {
        if (bodyMap.containsKey(obj)) {
            Coordinates c = obj.getCoordinates();
            bodyMap.get(obj).setTransform(c.getX() * cellWidth + cellWidth / 2, this.rows * cellHeight - c.getY() * cellHeight + cellHeight / 2, 0);
        }
    }

    public void updateLight() {
        if (!isValid()) {
            return;
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        // TODO : add DequeImpl<DC_HeroObj> units update here, about DIrection - Game->GetDirectionmap
        for (Map.Entry<Integer, FireLightProt> entry : fireLightProtMap.entrySet()) {
//            entry.getValue().update();
        }
//        world.step(1 / 60, 4, 4);
        rayHandler.setCombinedMatrix(DungeonScreen.camera);
        rayHandler.updateAndRender();
        if (LightingManager.debug) {
            debugRenderer.render(world, DungeonScreen.camera.combined);
        }
    }

    public void updateObject(BattleFieldObject heroObj) {
        // TODO: 12.12.2016 pointlighter around the mouse - 35 ligth emission and arround active Unite - (his emission +20) (DC_Game.game.getManager.getActiveUnit()
    }

    public boolean isValid() {
        if (!LightingManager.isLightOn()) {
            return false;
        }
        return valid;
    }
}