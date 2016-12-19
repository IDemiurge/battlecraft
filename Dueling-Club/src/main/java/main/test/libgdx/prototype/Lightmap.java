package main.test.libgdx.prototype;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.MicroObj;
import main.game.battlefield.Coordinates;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 19.11.2016.
 */
<<<<<<<HEAD:Dueling-Club/src/main/java/main/test/libgdx/prototype/Lightmap.java
public class Lightmap {
    Map<MicroObj, Body> bodyMap;
    World world;
    RayHandler rayHandler;

    private void init(DequeImpl<MicroObj> un, World world, RayHandler rayHandler,float cellWidth, float cellHeight){
=======
        public class LightmapTest {
            private Map<MicroObj, Body> bodyMap;
            private World world;
            private RayHandler rayHandler;
            private float cellWidth;
            private float cellHeight;

            private void init(DequeImpl<MicroObj> un, World world, RayHandler rayHandler, float cellWidth, float cellHeight) {
                this.cellWidth = cellWidth;
                this.cellHeight = cellHeight;
>>>>>>>4ef 48468430 a425ba81820ca46e99b8804b4eaf4:
                Dueling - Club / src / main / java / main / test / libgdx / prototype / LightmapTest.java
        this.world = world;
        this.rayHandler = rayHandler;
                bodyMap = new HashMap<>();
                for (int i = 0; i < un.size(); i++) {
//            System.out.println("===================");
//            System.out.println(un.get(i).getName());
//            System.out.println(un.get(i).getIntParam(PARAMS.LIGHT_EMISSION));
//            if (un.get(i).getName().equalsIgnoreCase("Torch")){
                    if (un.get(i).getIntParam(PARAMS.LIGHT_EMISSION) > 0) {
//                System.out.println("Torch Code DETECTED");
//                System.out.println("Coords: " + un.get(i).getX() + " || " + un.get(i).getY() + " || " + un.get(i).getCoordinates());
//                System.out.println("Add Body");
//                System.out.println("Add PointLighther and Attach it to the body");
                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.KinematicBody;
                Body body = world.createBody(bdef);
<<<<<<<HEAD:
                        Dueling - Club / src / main / java / main / test / libgdx / prototype / Lightmap.java
                body.setTransform(un.get(i).getX() * cellWidth + 10, un.get(i).getY() * cellHeight, 0);
=======
                        body.setTransform(un.get(i).getX() * cellWidth, un.get(i).getY() * cellHeight, 0);
>>>>>>>4ef 48468430 a425ba81820ca46e99b8804b4eaf4:
                        Dueling - Club / src / main / java / main / test / libgdx / prototype / LightmapTest.java
                PolygonShape shape = new PolygonShape();
                        shape.setAsBox(cellWidth / 20, cellHeight / 20);
                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                body.createFixture(fdef);
                        PointLight pointLight = new PointLight(rayHandler, 100, Color.RED, un.get(i).getIntParam(PARAMS.LIGHT_EMISSION) * 5, un.get(i).getX(), un.get(i).getY());
                pointLight.attachToBody(body);
                        bodyMap.put(un.get(i), body);
//                System.out.println("Created a body for Torch with a point light");
<<<<<<<HEAD:
                        Dueling - Club / src / main / java / main / test / libgdx / prototype / Lightmap.java
            }else {
//                if (!un.get(i).getName().equalsIgnoreCase("Stone Wall")){
                if (un.get(i).getType().toString().contains("units")){

                    // getobjType
=======


                } else {
//                if (!un.get(i).getName().equalsIgnoreCase("Stone Wall")){
                    if (un.get(i).getType().toString().contains("units")) {
>>>>>>>4ef 48468430 a425ba81820ca46e99b8804b4eaf4:
                        Dueling - Club / src / main / java / main / test / libgdx / prototype / LightmapTest.java
//                    System.out.println("Unit detected - need only to create a body");
                    BodyDef bdef = new BodyDef();
                    bdef.type = BodyDef.BodyType.KinematicBody;
                    Body body = world.createBody(bdef);
                        body.setTransform(un.get(i).getX() * cellWidth + cellWidth / 2, un.get(i).getY() * cellHeight + cellHeight / 2, 0);
                    PolygonShape shape = new PolygonShape();
                        shape.setAsBox(cellWidth / 2, cellHeight / 2);
                    FixtureDef fdef = new FixtureDef();
                    fdef.shape = shape;
                    body.createFixture(fdef);
//                    System.out.println("Created a body");
//                    System.out.println("Position on: " + un.get(i).getX() + "||" + un.get(i).getY());
                        bodyMap.put(un.get(i), body);
//                    System.out.println("===================");
                }
            }
        }
    }

<<<<<<<HEAD:
                Dueling - Club / src / main / java / main / test / libgdx / prototype / Lightmap.java
    public Lightmap(DequeImpl<MicroObj> un, float cellWidth, float cellHeight) {
        World world = new World(new Vector2(0,0),true);
     init(un,world,new RayHandler(world),cellWidth,cellHeight);
    }

    public Lightmap(DequeImpl<MicroObj> un, World world, RayHandler rayHandler) {
       init(un,world,rayHandler,132,113);
=======
    public LightmapTest(DequeImpl < MicroObj > un, float cellWidth, float cellHeight){
                        World world = new World(new Vector2(0, 0), true);
                        init(un, world, new RayHandler(world), cellWidth, cellHeight);
    }


    public LightmapTest(DequeImpl < MicroObj > un, World world, RayHandler rayHandler) {
                        init(un, world, rayHandler, 132, 113);
>>>>>>>4ef 48468430 a425ba81820ca46e99b8804b4eaf4:
                        Dueling - Club / src / main / java / main / test / libgdx / prototype / LightmapTest.java
                    }

                public void updatePos (MicroObj obj){
        if (bodyMap.containsKey(obj)) {
            Coordinates c = obj.getCoordinates();
            bodyMap.get(obj).setTransform(c.getX() * cellWidth, c.getY() * cellHeight, 0);
        }
    }

                public void updateLight () {
                    rayHandler.render();
                }

                public void updateObject (DC_HeroObj heroObj){
                    // TODO: 12.12.2016 update illumination and other ligth here
                }
            }