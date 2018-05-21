package eidolons.libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/29/2017.
 */
public class ScrollTextPanel extends GroupX {
    protected boolean updatePos = false;
    protected float offsetX = 20;
    protected ScrollPanel<Message> scrollPanel;
    float defaultHeight;
    float defaultWidth;
    int fontSize;
    FONT fontStyle;
    private Image bg;

    public ScrollTextPanel(float defaultHeight, float defaultWidth, int fontSize, FONT fontStyle) {
        this.defaultHeight = defaultHeight;
        this.defaultWidth = defaultWidth;
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
        init();
    }

    public ScrollTextPanel(float defaultHeight, float defaultWidth) {
        this.defaultHeight = defaultHeight;
        this.defaultWidth = defaultWidth;
        init();
    }

    public ScrollTextPanel() {
        init();
    }

    public void initBg() {
        if (getBgPath() != null) {
            bg = new Image(TextureCache.getOrCreateR(getBgPath()));

            if (defaultWidth == 0)
                defaultWidth = bg.getImageWidth();
            if (defaultHeight == 0)
                defaultHeight = bg.getHeight();
            bg.setFillParent(true);
            addActor(bg);
        }
    }
        public void init() {

        setSize(GdxMaster.adjustSize(getDefaultWidth()),
         GdxMaster.adjustSize(getDefaultHeight()));

        initScrollPanel();
            initBg();

        updatePos = true;
    }


    protected int getFontSize() {
        if (fontSize != 0) {
            return fontSize;
        }
        return 16;
    }

    protected FONT getFontStyle() {
        if (fontStyle != null) {
            return fontStyle;
        }
        return FONT.MAIN;
    }

    protected float getDefaultWidth() {
        if (defaultWidth != 0) {
            return defaultWidth;
        }
        return 400;
    }

    protected float getDefaultHeight() {
        if (defaultHeight != 0) {
            return defaultHeight;
        }
        return 400;
    }

    protected String getBgPath() {
        return null;
    }

    public void setText(String text) {

        scrollPanel.getInnerScrollContainer().getActor().clear();
        //TODO split?!
        for (String substring : StringMaster.openContainer(text, StringMaster.NEW_LINE)) {
            TextBuilder builder = getBuilder();
            Message message = builder.addString(substring).build(getWidth()*0.92f);
            scrollPanel.addElement(message).width(getWidth());
        }
//        outside.setTouchable(Touchable.enabled);
    }


    protected TextBuilder getBuilder() {
        return new TextBuilder() {
            @Override
            protected FONT getFontStyle() {
                return
                 ScrollTextPanel.this.getFontStyle();
            }

            @Override
            protected int getFontSize() {
                return ScrollTextPanel.this.getFontSize();
            }

            @Override
            protected float getAdjustCoef() {
                return GdxMaster.fontSizeAdjustCoef;
            }
        };
    }


    protected void initScrollPanel() {
        if (scrollPanel != null) {
            scrollPanel.remove();
        }
        scrollPanel = new ScrollPanel(){
            @Override
            protected boolean isAlwaysScrolled() {
                return ScrollTextPanel.this.isAlwaysScrolled();
            }
        };

        scrollPanel.pad(1, 10, 1, 10);
        scrollPanel.fill();

        addActor(scrollPanel);
    }

    protected boolean isAlwaysScrolled() {
        return false;
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (updatePos) {
            updateAct();
            updatePos = false;
        }
    }

    protected void updateAct() {
        scrollPanel.setBounds(10, 10, getWidth() - 20, getHeight() - 20);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            return null;
        }

        if (actor instanceof Image) {
            return null;
        }
        return actor;
    }
}
