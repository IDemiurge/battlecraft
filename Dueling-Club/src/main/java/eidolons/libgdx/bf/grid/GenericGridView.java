package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.light.LightEmitter;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.screens.map.layers.LightLayer;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import java.util.function.Supplier;

/**
 * Created by JustMe on 4/6/2018.
 * for structures? anything that does not need QueueView
 */
public class GenericGridView extends UnitView {
    public static final int ARROW_ROTATION_OFFSET = 90;
    protected GroupX arrow;
    protected Image emblemLighting;
    protected Image icon;
    protected int arrowRotation;
    protected float alpha = 1f;
    protected boolean cellBackground;
    protected LastSeenView lastSeenView;
    protected FadeImageContainer torch;

    public GenericGridView(UnitViewOptions o) {
        super(o);
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getIconTexture(), o.getEmblem());
        cellBackground = o.cellBackground;
        setVisible(false);
    }

    @Override
    public void reset() {
        super.reset();
        if (emblemImage != null) {
            emblemImage.setPosition(getWidth() - emblemImage.getWidth(),
             getHeight() - emblemImage.getHeight());
            emblemLighting.setPosition(getWidth() - emblemLighting.getWidth(),
             getHeight() - emblemLighting.getHeight());
            if (getTeamColor() != null)
                emblemLighting.setColor(getTeamColor());
        }
        if (arrow != null)
            arrow.setPosition(getWidth() / 2 - arrow.getWidth() / 2, 0);

    }

    protected void init(TextureRegion arrowTexture, int arrowRotation, Texture iconTexture, TextureRegion emblem) {

        if (arrowTexture != null) {
            arrow = new GroupX();
            arrow.addActor(new Image(arrowTexture));
            arrow.setSize(arrowTexture.getRegionWidth(), arrowTexture.getRegionHeight());
            arrow.addActor(torch = new FadeImageContainer(StrPathBuilder.build("ui", "unit light directional.png")){
                @Override
                public boolean isAlphaFluctuationOn() {
                    return getBaseAlpha()>0;
                }

                @Override
                public void setBaseAlpha(float baseAlpha) {
                    super.setBaseAlpha(baseAlpha/2);
                }

                @Override
                public void setColor(Color color) {
                    super.setColor(color);
                }
            });
            torch.setPosition(GdxMaster.centerWidth(torch)-7, arrow.getHeight()-torch.getHeight());
            torch.setColor(GdxColorMaster.FIRE);
            torch.getColor().a=0;
            torch.setAlphaTemplate(ALPHA_TEMPLATE.LIGHT_EMITTER_RAYS);

            addActor(arrow);
//            arrow.setPosition(getWidth() / 2 - arrow.getWidth() / 2, 0);
            arrow.setOrigin(getWidth() / 2  , getHeight() / 2 );
            this.arrowRotation = arrowRotation + ARROW_ROTATION_OFFSET;
            arrow.setRotation(this.arrowRotation);
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (emblem != null) {
            emblemLighting = new Image(TextureCache.getOrCreateR(STD_IMAGES.LIGHT.getPath()));
            emblemLighting.setSize(getEmblemSize() * 10 / 9, getEmblemSize() * 10 / 9);
            emblemLighting.setPosition(getWidth() - emblemLighting.getWidth(), getHeight() - emblemLighting.getHeight());
            if (getTeamColor() != null)
                emblemLighting.setColor(getTeamColor());
            addActor(emblemLighting);

            emblemImage = new FadeImageContainer(new Image(emblem));
            addActor(emblemImage);
            emblemImage.setSize(getEmblemSize(), getEmblemSize());
            emblemImage.setPosition(getWidth() - emblemImage.getWidth(), getHeight() - emblemImage.getHeight());
        }
        setInitialized(true);
    }

    protected float getEmblemSize() {
        if (isMainHero())
            return 36;
        return 32;
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.isVisible() != visible) {
            super.setVisible(visible);
            if (getParent() instanceof GridCellContainer) {
                ((GridCellContainer) getParent()).recalcUnitViewBounds();

            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (alpha != 1f) {
            parentAlpha = alpha;
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (!isVisible())
            return; //TODO make withinCamera()  work with actions
        if (getY() < 0)
            setY(0);
        if (getX() < 0)
            setX(0);
        if (emblemLighting != null)
            alphaFluctuation(emblemLighting, delta);
        super.act(delta);
        if (hpBar!=null )
            hpBar.act(delta);
    }


    public void updateRotation(int val) {
        if (arrow != null) {
           val= val % 360;
            if (isVisible())
            ActorMaster.addRotateByAction(arrow, arrowRotation, val  + 90);
            else
                arrow.setRotation(val   + 90);
            arrowRotation = val + 90;

        }
    }

    public boolean isHpBarVisible() {
        if (!isVisible())
            return false;
        if (!getHpBar().canHpBarBeVisible())
            return false;
        if (!isCellBackground() && HpBar.getHpAlwaysVisible())
            return true;
        return getHpBar(). isHpBarVisible();
    }


    public void setOutlinePathSupplier(Supplier<String> pathSupplier) {
        if (pathSupplier.get()!=null )
        if (!ImageManager.isImage(pathSupplier.get())) {
            return;
        }
        this.outlineSupplier = () -> StringMaster.isEmpty(pathSupplier.get()) ? null : TextureCache.getOrCreateR(pathSupplier.get());
    }

    public void setMainHero(boolean mainHero) {
        super.setMainHero(mainHero);
    }

    protected void updateVisible() {
//        if (isIgnored()) //TODO [quick fix] this should actually be on for speed, but somehow in-camera views get ignored
//            return;
        if (getOutline() != null) {
            if (emblemImage != null)
                emblemImage.setVisible(false);
            if (modeImage != null)
                modeImage.setVisible(false);
            if (arrow != null)
                arrow.setVisible(false);
            if (getHpBar() != null)
                getHpBar().setVisible(false);

        } else {
            if (emblemImage != null)
                emblemImage.setVisible(true);
            if (modeImage != null)
                modeImage.setVisible(true);
            if (arrow != null)
                arrow.setVisible(true);
            if (getHpBar() != null)
                getHpBar().setVisible(isHpBarVisible());
        }
    }

    protected void setPortraitTexture(TextureRegion textureRegion) {
        getPortrait().setTexture(TextureCache.getOrCreateTextureRegionDrawable(textureRegion));
    }

    @Override
    public float getWidth() {
        if (super.getWidth() == 0)
            return GridMaster.CELL_W;
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        if (super.getHeight() == 0)
            return GridMaster.CELL_H;
        return super.getHeight();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        if (icon != null) {
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setRotation(arrowRotation);
        }

        if (getScaledWidth() == 0)
            return;
        if (getScaledHeight() == 0)
            return;

        if (emblemImage != null)
            emblemImage.setScale(getScaledWidth(), getScaledHeight());
    }


    public int getId() {
        return curId;
    }

    public boolean isCellBackground() {
        return cellBackground;
    }




    @Override
    public void setHpBar(HpBar hpBar) {
        super.setHpBar(hpBar);
        hpBar.setPosition(GdxMaster.centerWidth(hpBar), -hpBar.getHeight() / 2);
    }

    @Override
    public boolean isCachedPosition() {
        return isCellBackground();
    }

    public void animateHpBarChange() {
        if (!getHpBar().isVisible())
            return;
        getHpBar().animateChange();

    }

    public LastSeenView getLastSeenView() {
        return lastSeenView;
    }

    public void setLastSeenView(LastSeenView lastSeenView) {
        this.lastSeenView = lastSeenView;
    }
}

