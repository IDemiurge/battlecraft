package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.stage.ConfirmationPanel;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 12/1/2017.
 */
public class SmartButton extends TextButton implements EventListener {

    private Runnable runnable;
    private boolean fixedSize;
    private boolean ignoreConfirmBlock;

    public SmartButton(String text, TextButtonStyle style) {
        this(text, style, null);

    }

    public SmartButton(STD_BUTTON button, Runnable runnable) {
        this("", button, runnable);
    }


    public SmartButton(String text, STD_BUTTON button, Runnable runnable,
                       FONT font, int size, Color color_) {
        this(text, StyleHolder.getTextButtonStyle(button,
         font, color_, size), runnable);
    }

    public SmartButton(String text, STD_BUTTON button, Runnable runnable) {
        this(text, button, runnable,
         FONT.MAGIC, 20, GdxColorMaster.GOLDEN_WHITE);
    }

    public SmartButton(String text, TextButtonStyle style, Runnable runnable) {
        super(text, style);
        this.runnable = runnable;
        addListener(this);
    }

    public SmartButton(STD_BUTTON button) {
        this("", button, null);
    }

    public SmartButton(String text, Runnable runnable) {
        this(text, StyleHolder.getHqTextButtonStyle(16), runnable);
    }




    @Override
    public float getPrefWidth() {
        if (isFixedSize())
            return getWidth();
        return super.getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        if (isFixedSize())
            return getHeight();
        return super.getPrefHeight();
    }
//    addListener(new SmartClickListener(this) {
//        @Override
//        public void clicked(InputEvent event, float x, float y) {
//            super.clicked(event, x, y);
//            runnable.run();
//        }
//    }
//        );
    @Override
    public boolean handle(Event e) {
        if (runnable == null)
            return true;
        if (!isIgnoreConfirmBlock())
            if (ConfirmationPanel.getInstance().isVisible())
                return true;
        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;
        if (event.getType() == Type.touchUp) {
//            event.getTarget().getStage().hit()
//            event.getTarget().stageToLocalCoordinates(new Vector2(event.getStageX(), event.getStageY()))
            if (GdxMaster.isWithin(event.getTarget(), new Vector2(event.getStageX(), event.getStageY()), true))
                runnable.run();
        }
        return true;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public boolean isIgnoreConfirmBlock() {
        return ignoreConfirmBlock;
    }

    public void setIgnoreConfirmBlock(boolean ignoreConfirmBlock) {
        this.ignoreConfirmBlock = ignoreConfirmBlock;
    }
}
