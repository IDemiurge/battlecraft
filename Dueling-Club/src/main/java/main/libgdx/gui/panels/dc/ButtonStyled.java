package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.StyleHolder;
import main.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;

public class ButtonStyled extends Button implements EventListener {

    Runnable runnable;

    public ButtonStyled(STD_BUTTON b, Runnable runnable) {
        this(b);
        this.runnable = runnable;
        addListener(this);
    }

    public ButtonStyled(STD_BUTTON b) {
        super(
         !b.isVersioned() ?
          new Image(
           TextureCache.getOrCreate(b.path))
          :null ,

          b.isVersioned() ?
           StyleHolder.getCustomButtonStyle(b.path)
           : StyleHolder.getDefaultTextButtonStyle()
        );
    }

    public ButtonStyled(String name) {
//         setStyle();
        setName(name);
    }

    @Override
    public boolean handle(Event e) {
        if (runnable == null)
            return true;
        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;
        if (event.getType() == Type.touchDown) {
            runnable.run();
        }
        return true;
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
    }

    public enum STD_BUTTON {
        OK("UI/components/small/ok.png"),
        CANCEL("UI/components/small/no.png"),
        UNDO("UI/components/small/back2.png"),
        OPTIONS(VISUALS.GEARS.getImgPath()){
            @Override
            public boolean isVersioned() {
                return false;
            }
        },
//        NEXT, LEVEL_UP,
        ;
        String path;

        STD_BUTTON(String path) {
            this.path = path;
        }

        public boolean isVersioned() {
            return true;}
    }
}
