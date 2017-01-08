package main.test.libgdx.prototype;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.MicroObj;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 19.11.2016.
 */
public class Lightmap {
    private Map<MicroObj, Body> bodyMap;
    private static World world;
    private static RayHandler rayHandler;
    private float cellWidth;
    private float cellHeight;
    private int rows;
    private int cols;
    private Map<Integer, FireLightProt> fireLightProtMap;
    private static float SECOND = 1000000000;
    private static float ambient = 0.05f;
    Box2DDebugRenderer debugRenderer;
    private static int testA;
    private static int testB;
    private ParticleEffect pf;


    private void init(DequeImpl<DC_HeroObj> un, World world, RayHandler rayHandler, float cellWidth, float cellHeight, int rows, int cols) {
        testA = 1600;
        testB = 900;

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

        Lightmap.world = world;
        Lightmap.rayHandler = rayHandler;
        Lightmap.rayHandler.setBlur(true);
        Lightmap.rayHandler.setBlurNum(15);
        Lightmap.rayHandler.setAmbientLight(Color.GRAY);
        Lightmap.rayHandler.setAmbientLight(LightingManager.ambient_light);
        Lightmap.rayHandler.setBlurNum(15);
        RayHandler.setGammaCorrection(true);
        debugRenderer = new Box2DDebugRenderer();
        fireLightProtMap = new HashMap<>();
        bodyMap = new HashMap<>();
        for (int i = 0; i < un.size(); i++) {

            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.KinematicBody;
            Body body = world.createBody(bdef);

            if (un.get(i).getIntParam(PARAMS.LIGHT_EMISSION) > 0) {
                body.setTransform(un.get(i).getX() * cellWidth + cellWidth / 2, this.rows * cellHeight - un.get(i).getY() * cellHeight + cellHeight / 2, 0);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(cellWidth / 2, cellHeight / 2);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
                // TEMP
                FireLightProt fireLightProt = new FireLightProt(world, rayHandler,
                        un.get(i).getX() * cellWidth + cellWidth / 2, un.get(i).getY() *
                        cellHeight + cellHeight / 2, un.get(i).getIntParam(PARAMS.LIGHT_EMISSION) * 30, 360, SECOND);
//                FireLightProt fireLightProt = new FireLightProt();
                fireLightProt.attachToBody(body);
                //TEMP END
                fireLightProtMap.put(i, fireLightProt);
            } else {
                body.setTransform(un.get(i).getX() * cellWidth + cellWidth / 2, this.rows * cellHeight - un.get(i).getY() * cellHeight + cellHeight / 2, 0);
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(cellWidth / 2, cellHeight / 2);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
            }
            bodyMap.put(un.get(i), body);
        }
    }

    public Lightmap(DequeImpl<DC_HeroObj> un, float cellWidth, float cellHeight, int rows, int cols) {
        World world = new World(new Vector2(0, 0), true);
        init(un, world, new RayHandler(world), cellWidth, cellHeight, rows, cols);
    }

    public void updatePos(MicroObj obj) {
        if (bodyMap.containsKey(obj)) {
            Coordinates c = obj.getCoordinates();
            bodyMap.get(obj).setTransform(c.getX() * cellWidth + cellWidth / 2, this.rows * cellHeight - c.getY() * cellHeight + cellHeight / 2, 0);
        }
    }

    public void updateLight() {

        for (Map.Entry<Integer, FireLightProt> entry : fireLightProtMap.entrySet()) {
            entry.getValue().update();

        }
//        for (int q =0;q<fireLightProtMap.entrySet().size();q++){
//                fireLightProtMap.getOrCreate(q).update(Gdx.graphics.getDeltaTime());
//        }
        world.step(1 / 60, 4, 4);
        rayHandler.setCombinedMatrix(GameScreen.camera);
        rayHandler.updateAndRender();

        debugRenderer.render(world, GameScreen.camera.combined);
    }

    public void updateObject(DC_HeroObj heroObj) {
//        int lightEmmi = heroObj.getIntParam(PARAMS.LIGHT_EMISSION);
//        if (lightMap.containsKey(heroObj)) {
//            lightMap.getOrCreate(heroObj).setDistance(lightEmmi * 15);
//        }
        // TODO: 12.12.2016 pointlighter around the mouse - 35 ligth emission and arround active Unite - (his emission +20) (DC_Game.game.getManager.getActiveUnit()
        //      light_emission + 20)
    }

    public static void setAmbint(float c) {
        rayHandler.setAmbientLight(c);
        rayHandler.update();
        ambient = c;
    }

    public static float getAmbint() {
        return ambient;
    }
    public static void resizeFBOb(){
        testB += 50;
        testA += 50;
        rayHandler.resizeFBO(testA,testB);
        rayHandler.update();
    }
    public static void resizeFBOa(){
        testA -= 50;
        testB -= 50;
        rayHandler.resizeFBO(testA,testB);
        rayHandler.update();
    }

}