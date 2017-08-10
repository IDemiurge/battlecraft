package main.libgdx.gui.tooltips;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public abstract class ToolTip<T extends Actor> extends TablePanel<T> {

    private boolean showing;

    public ToolTip() {

    }

    public InputListener getController() {
        return new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                onMouseMoved(event, x, y);
                return true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                onMouseEnter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                onMouseExit(event, x, y, pointer, toActor);
            }
        };
    }

    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (showing) {
            return;
        }
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
        showing = true;
    }

    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
    }

    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
        showing = false;
    }
}
