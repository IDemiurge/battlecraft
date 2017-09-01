package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.anims.particles.Ambience;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.screens.ScreenData;
import main.system.graphics.FontMaster.FONT;
import main.system.text.TipMaster;
import main.test.frontend.ScenarioLauncher;

import java.util.LinkedList;
import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class LoadingStage extends Stage {
    protected ScreenData data;
    private boolean fogOn = true;
    private boolean engineInit = true;
    private boolean fullscreen = true;
    private boolean loaderWheel;
    private Image fullscreenImage;
    private List<Ambience> fogList = new LinkedList<>();
    private Image loadingImage;
    private SuperContainer logoImage;
    private Label missionName;
    private Label tip;
    private float counter = 0;

    @Override
    public Actor hit(float stageX, float stageY, boolean touchable) {
        return super.hit(stageX, stageY, touchable);
    }

    public LoadingStage(ScreenData data) {
        this.data = data;
        if (data.equals("Loading...")) {
            engineInit = true;
            loaderWheel = true;
            fogOn = false;
        }

        tip = new Label("Tip: " +
         TipMaster.getTip()
         + "(click to show next tip)", StyleHolder.getSizedLabelStyle
         (StyleHolder.DEFAULT_FONT, 20));
        tip.addListener(TipMaster.getListener(tip));
        //TODO click to show next tip
        tip.setPosition(GdxMaster.centerWidth(tip), 0);

        if (!fullscreen) {
            final TextureRegion logoTexture =
             getOrCreateR("UI/logo.png");
            logoImage = new SuperContainer(new Image(logoTexture));
            logoImage.setPosition(0, Gdx.graphics.getHeight() - loadingImage.getHeight());
            if (fogOn)
                addActor(ParticleManager.addFogOn(new Vector2(logoImage.getX() , logoImage.getY() ), fogList));
            addActor(logoImage);
        } else {
            final TextureRegion fullscreenTexture =
             getOrCreateR(
              (engineInit) ? "UI/logo fullscreen.png"
               : "UI/moe loading screen.png");
            fullscreenImage = new Image(fullscreenTexture);
            addActor(fullscreenImage);

        }
        addActor(tip);
        if (ScenarioLauncher.running) {
            missionName = new Label(data.getName()
             , StyleHolder.getSizedLabelStyle(FONT.AVQ, 24));
            missionName.setPosition(GdxMaster.centerWidth(missionName),
             GdxMaster.top(missionName));
            addActor(missionName);
            if (fogOn) {
                addActor(ParticleManager.addFogOn(
                 new Vector2(missionName.getX()+300, missionName.getY()-300)
                 , fogList));
                addActor(ParticleManager.addFogOn(
                 new Vector2(missionName.getX(), missionName.getY()-300)
                 , fogList));
                addActor(ParticleManager.addFogOn(new Vector2(missionName.getX(), missionName.getY()), fogList));
            }
        }
        if (loaderWheel) {
            final TextureRegion loadingTexture = getOrCreateR("UI/loading-wheel-trans_256х256.png");
            loadingImage = new Image(loadingTexture);
            loadingImage.setOrigin(Align.center);
            loadingImage.setPosition(
             Gdx.graphics.getWidth() / 2 - loadingImage.getWidth() / 2,
             Gdx.graphics.getHeight() / 2 - loadingImage.getHeight() / 2);
            //loadingTexture.getTexture();
            addActor(loadingImage);
        }
    }

    @Override
    public void setViewport(Viewport viewport) {
        super.setViewport(viewport);
    }

    @Override
    public void act(float delta) {
        super.act(delta);


        if (fogList != null)
            for (Ambience fog : fogList)
                fog.act(delta);

        if (loadingImage != null) {
            counter += delta;
            if (counter >= 0.05) {
                loadingImage.setRotation(loadingImage.getRotation() + 30);
                counter = 0;
            }
        }
        if (logoImage != null)
            logoImage.act(delta);
    }

    public void done() {
        if (fogList != null)
            for (Ambience fog : fogList)
                fog.getEffect().dispose();
    }

    @Override
    public void draw() {
        final Matrix4 combined = getCamera().combined.cpy();
        getCamera().update();

        final Group root = getRoot();

        if (!root.isVisible()) return;

        combined.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(combined);
        batch.begin();
        root.draw(batch, 1);
        batch.end();
    }
}
