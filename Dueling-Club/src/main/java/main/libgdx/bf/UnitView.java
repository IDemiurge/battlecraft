package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import main.libgdx.StyleHolder;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.shaders.GrayscaleShader;
import main.system.auxiliary.log.LogMaster;

import java.util.concurrent.atomic.AtomicInteger;

public class UnitView extends BaseView {
    protected static AtomicInteger lastId = new AtomicInteger(1);
    protected final int curId;
    protected int initiativeIntVal;
    protected TextureRegion clockTexture;
    protected Label initiativeLabel;
    protected Image clockImage;
    protected boolean mobilityState = true;//mobility state, temporary.
    protected boolean flickering;
    protected Image emblem;
    protected boolean greyedOut;


    public UnitView(UnitViewOptions o) {
        this(o, lastId.getAndIncrement());
    }

    protected UnitView(UnitViewOptions o, int curId) {
        super(o);
        this.curId = curId;
        init(o.getClockTexture(), o.getClockValue(), o.getEmblem());
        setTeamColor(o.getTeamColor());
    }

    public void setToolTip(ToolTip toolTip) {
        addListener(toolTip.getController());
    }

    private void init(TextureRegion clockTexture, int clockVal, TextureRegion emblem) {
        this.initiativeIntVal = clockVal;

        if (emblem != null) {
            this.emblem = new Image(emblem);
            //TODO add sized team-colored border
            this.emblem.setAlign(Align.bottomLeft);
            addActor(this.emblem);
        }
        if (clockTexture != null) {
            this.clockTexture = clockTexture;
            initiativeLabel = new Label("[#00FF00FF]" + String.valueOf(clockVal) + "[]", StyleHolder.getDefaultLabelStyle());
            initiativeLabel.setFontScale(1.3f);
            clockImage = new Image(clockTexture);
            addActor(clockImage);
            addActor(initiativeLabel);
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        if (initiativeLabel != null) {
            clockImage.setPosition(
             getWidth() - clockTexture.getRegionWidth(),
             0
            );
            initiativeLabel.setPosition(
             clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth()),
             clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        }
    }

    public void updateInitiative(Integer val) {
        if (clockTexture != null) {
            initiativeIntVal = val;
            initiativeLabel.setText("[#00FF00FF]" + String.valueOf(val) + "[]");

            initiativeLabel.setPosition(
             clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth()),
             clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        } else {
            LogMaster.error("Initiative set to wrong object type != OBJ_TYPES.UNITS");
        }
    }

    @Override
    public void act(float delta) {

        if (flickering) {

        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (greyedOut) {
            batch.setShader(GrayscaleShader.getGrayscaleShader());
        }
        super.draw(batch, parentAlpha);
        batch.setShader(null);
    }

    public void setFlickering(boolean flickering) {
        this.flickering = flickering;
    }

    public void setGreyedOut(boolean greyedOut) {
        this.greyedOut = greyedOut;
    }

    public void toggleGreyedOut() {
        this.greyedOut = !greyedOut;
    }

    public int getCurId() {
        return curId;
    }

    public int getInitiativeIntVal() {
        return initiativeIntVal;
    }


    public boolean getMobilityState() {
        return mobilityState;
    }

    public void setMobilityState(boolean mobilityState) {
        this.mobilityState = mobilityState;
    }
}
