package eidolons.libgdx.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.bf.SuperActor.BLENDING;
import eidolons.libgdx.shaders.FluctuatingShader;
import eidolons.libgdx.shaders.post.PostProcessController;
import eidolons.libgdx.utils.ShaderBatch;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by JustMe on 10/12/2018.
 */
public class CustomSpriteBatch extends ShaderBatch {

    private static CustomSpriteBatch instance;
    PostProcessController controller;
    private BLENDING blending;
    private Set<FluctuatingShader> shaders = new HashSet<>();
    private ShaderProgram bufferedShader;


    public CustomSpriteBatch() {
    }

    public static CustomSpriteBatch getMainInstance() {
        if (instance == null) {
            instance = new CustomSpriteBatch();
        }
        return instance;
    }

    public BLENDING getBlending() {
        return blending;
    }

    public void setBlending(BLENDING blending) {
        switch (blending) {
            case SCREEN:
                setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);


                //                setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                //                setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                break;
        }
        this.blending = blending;
    }

    public void resetBlending() {
        setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blending= null;
    }

    @Override
    public Color getColor() {
        return super.getColor();
        //        return new Color(super.getColor()).mul(GdxMaster.getBrightness());
    }

    @Override
    public void setShader(ShaderProgram shader) {
        if (  shader != getShader()) {
            bufferedShader = getShader();
        }
        super.setShader(shader);
    }

    public void setFluctuatingShader(FluctuatingShader shader) {
        setShader(shader.getShader());
        shaders.add(shader);
    }

    public void shaderFluctuation(float delta) {
        shaders.forEach(shader -> shader.act(delta));
    }


    protected void shaderReset() {
        setShader(bufferedShader);
    }

}
