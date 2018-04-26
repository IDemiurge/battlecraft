package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.screens.DungeonScreen;
import main.content.CONTENT_CONSTS2.EMITTER_PRESET;
import main.system.auxiliary.RandomWizard;

/**
 * Created by JustMe on 1/10/2017.
 */
public class Ambience extends EmitterActor {
    private static boolean modifyParticles;
    float moveSpeedMax = 12;
    float moveRadius = 300;
    Vector2 velocity;
    Vector2 acceleration;
    Vector2 originPos;

    public Ambience(String path) {
        super(path);
    }

    public Ambience(EMITTER_PRESET fx) {
        super(fx);
    }

    public static void setModifyParticles(boolean modifyParticles) {
        Ambience.modifyParticles = modifyParticles;
    }

    @Override
    public void act(float delta) {
        if (!ParticleManager.isAmbienceOn())
            return;
        if (!isVisible())
            return;
        if (isCullingOn())
            if (DungeonScreen.getInstance().controller != null)
                if (!DungeonScreen.getInstance().controller.
                 isWithinCamera(getX(), getY(), getWidth() * 2, getHeight() * 2)) {
                    return;
                }

        super.act(delta);
        if (!ParticleManager.isAmbienceMoveOn()) {
            return;
        }
        float angle = acceleration.angle();
        float dst = originPos.dst(new Vector2(getX(), getY()));
        if (dst > moveRadius) {
            angle += RandomWizard.getRandomInt(360);
            acceleration.setAngle(angle);
        }
        velocity.add(acceleration);
        velocity = velocity.limit(moveSpeedMax);
    }

    public void added() {
        originPos = new Vector2(getX(), getY());
        acceleration = new Vector2(1, 1);
        velocity = new Vector2(0, 0);
    }

    @Override
    public float getWidth() {
//        FuncMaster.getGreatest()
//        getEffect().getEmitters().forEach(e->
//        e.getSpawnWidth().getHighMax());
        return 400;
    }

    @Override
    public float getHeight() {
        return 400;
    }

    @Override
    public void draw(Batch spriteBatch, float delta) {

        if (!ParticleManager.isAmbienceOn())
            return;
        if (isCullingOn())
            if (DungeonScreen.getInstance().controller != null)
                if (!DungeonScreen.getInstance().controller.
                 isWithinCamera(getX(), getY(), getWidth(), getHeight())) {
                    return;
                }
        if (modifyParticles) {
            getEffect().modifyParticles();
        }
        super.draw(spriteBatch, delta);
    }

    protected boolean isCullingOn() {
        return true;
    }

    public enum AMBIENT_SFX {
        MIST,


    }
}
