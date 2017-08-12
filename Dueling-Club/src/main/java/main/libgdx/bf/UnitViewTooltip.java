package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class UnitViewTooltip extends ValueTooltip {

    BaseView view;

    public UnitViewTooltip(BaseView view) {
        this.view = view;
    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        updateRequired = true;
        GuiEventManager.trigger(GuiEventType.UNIT_VIEW_HOVER_ON, view);
        super.onMouseEnter(event, x, y, pointer, fromActor);
    }
    protected void onMouseMoved(InputEvent event, float x, float y) {
        if (showing) {
            return;
        }
        super.onMouseMoved(event, x, y);
        GuiEventManager.trigger(GuiEventType.UNIT_VIEW_HOVER_ON, view);
        showing = true;
    }


    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.onMouseExit(event, x, y, pointer, toActor);
        GuiEventManager.trigger(GuiEventType.UNIT_VIEW_HOVER_OFF, view);
    }
}
