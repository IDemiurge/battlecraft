package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import main.content.CONTENT_CONSTS2.SFX;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleAnimation implements ParticleInterface {
    @Override
    public SFX getTemplate() {
        return null;
    }

    @Override
    public ParticleEffect getEffect() {
        return null;
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void updatePosition(float x, float y) {

    }


    @Override
    public void start() {

    }
    /*
    states: start, run, done
    params: length, delay,
    templates
    args: SFX...effects

     */
}
