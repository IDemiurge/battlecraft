package main.utilities.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ScreenUtils;
import eidolons.utils.GdxUtil;
import libgdx.GDX;
import libgdx.bf.generic.ImageContainer;
import libgdx.shaders.GrayscaleShader;
import eidolons.content.consts.Images;

import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.BLENDING;
import main.data.filesys.PathFinder;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Created by JustMe on 5/21/2018.
 *
 * VFX to sprite-sheet?
 * how many frames?
 * so suppose a vfx lasts 3 sec and goes with normal fps... 180?!
 *
 *
 *
 */
public class GdxMixer extends GdxUtil {

    private final Supplier<ShaderProgram> shaderProgram;
    private final ArrayList<Actor> actors;
    private final String pathRoot;
    private final Float[] alpha = new Float[0];
    private FrameBuffer fbo = null;
    private SpriteBatch batch;
    private final int size;

    Runnable drawer;



    public GdxMixer(Supplier<ShaderProgram> shaderProgram, BLENDING blending,
                    String pathRoot, int size, Actor... actors) {
        this.shaderProgram = shaderProgram;
        this.size = size;
        this.pathRoot = pathRoot;
        this.actors = new ArrayList<>(Arrays.asList(actors));
        start();
    }
public enum MIXER_LAUNCH{
    MASK,
    SHADER,
    EMITTER,


}
    public static void main(String[] args) {
        CoreEngine.systemInit();
        //swing dialogue init?
        int size;
        String rootPath;

        new GdxMixer(GrayscaleShader::getGrayscaleShader, GenericEnums.BLENDING.MULTIPLY,
         Images.EMPTY_ARMOR,
//         PathFinder.getGeneratorRootPath() + "mixed.png",
         64
        );
    }

    @Override
    protected void execute() {
        this.actors.add(new ImageContainer(pathRoot));
        render(null);
        mix();
    }

    @Override
    protected boolean isExitOnDone() {
        return false;
    }

    private void renderToFbo() {
        if (shaderProgram != null) {
            batch.setShader(shaderProgram.get());
        }
        int i = 0;
        batch.begin();
        for (Actor sub : actors) {
            sub.draw(batch, alpha.length > i ? alpha[i++] : 1);
            //            batch.setBlendFunction(blending.blendSrcFunc, blending.blendDstFunc);
        }
        batch.end();
    }

    @Override
    protected int getWidth() {
        return size;
    }

    @Override
    protected int getHeight() {
        return size;
    }

    public void mix() {

        FileHandle handle = GDX.file(
         PathFinder.getImagePath() +
          getPath());

        // bind
        //        Gdx.gl20.glBindFramebuffer(GL20.GL_READ_FRAMEBUFFER, fbo.getFramebufferHandle());
        //        Gdx.gl20.glReadBuffer(GL20.GL_COLOR_ATTACHMENT0);
        // read content
        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, getWidth(), getHeight());
        // unbind
        //        Gdx.gl20.glBindFramebuffer(GL20.GL_READ_FRAMEBUFFER, 0);
        //           Gdx. gl20.glReadBuffer(GL20.GL_BACK);

        PixmapIO.writePNG(handle, pixmap);
    }

    private String getPath() {
        return pathRoot;
    }

    @Override
    public void render() {
        render(null);
    }

    public void render(SpriteBatch spriteBatch) {
        if (spriteBatch == null)
            spriteBatch = new SpriteBatch();
        this.batch = spriteBatch;

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        boolean m_fboEnabled = true;
        if (m_fboEnabled)      // enable or disable the supersampling
        {
            if (fbo == null) {
                // m_fboScaler increase or decrease the antialiasing quality
                float m_fboScaler = 1.5f;
                fbo = FrameBuffer.createFrameBuffer(
                 Format.RGB565, (int) (width * m_fboScaler),
                 (int) (height * m_fboScaler), false);
                TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());
                region.flip(false, true);
            }

            fbo.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        }

        // this is the main render function
        renderToFbo();

        if (fbo != null) {
            fbo.end();

//            spriteBatch.begin();
//            spriteBatch.draw(region, 0, 0, width, height);
//            spriteBatch.end();
        }
    }

}
