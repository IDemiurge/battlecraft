package main.level_editor.gui.top;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.level_editor.LevelEditor;

public class LE_ButtonStripe extends HorizontalFlowGroup {

    SmartButton controlPanel;
    SmartButton palettePanel;
    SmartButton structurePanel;
    SmartButton brushes;
    SmartButton viewModes;
    SmartButton save;
    SmartButton saveV;

    SmartButton undo;

    public LE_ButtonStripe() {
        super(10);
        setHeight(80);
        setWidth(900);
        addActor(undo = new SmartButton(ButtonStyled.STD_BUTTON.LE_UNDO, () ->
                Eidolons.onGdxThread(() -> {
                    try {
                        LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                })));
        TablePanelX<Actor> container = new TablePanelX(80, 60) {
            @Override
            public void layout() {
                super.layout();
                save.setY(save.getY() + 20);
                //                saveV.setY(saveV.getY()+20);
            }
        };
        container.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        addActor(container);
        container.add(
                save = new SmartButton(ButtonStyled.STD_BUTTON.REPAIR, () ->
                        Eidolons.onNonGdxThread(() -> LevelEditor.getCurrent().getManager().getDataHandler().saveFloor()))).top();
        container.add(
                saveV = new SmartButton(ButtonStyled.STD_BUTTON.CHEST, () ->
                        Eidolons.onGdxThread(() -> LevelEditor.getCurrent().getManager().
                                getDataHandler().saveVersion()))).top();
        //        addActor(new TablePanelX<>(40, getHeight()));
        addActor(controlPanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_CTRL, null));
        addActor(palettePanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_PALETTE, null));
        addActor(structurePanel = new SmartButton(ButtonStyled.STD_BUTTON.LE_STRUCT, null));

        addActor(brushes = new SmartButton(ButtonStyled.STD_BUTTON.LE_BRUSH, () -> {
            boolean b = LevelEditor.getModel().isBrushMode();
            LevelEditor.getModel().setBrushMode(!b);
            //            if (getStage() instanceof LE_GuiStage) {
            //                ((LE_GuiStage) getStage()).toggleUiVisible();
            //            }
        }));
        addActor(viewModes = new SmartButton(ButtonStyled.STD_BUTTON.LE_VIEWS, () -> {
            LevelEditor.getModel().getDisplayMode().toggleAll();
        }));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        brushes.setChecked(LevelEditor.getModel().isBrushMode());
    }

    public SmartButton getControlPanel() {
        return controlPanel;
    }

    public SmartButton getPalettePanel() {
        return palettePanel;
    }

    public SmartButton getStructurePanel() {
        return structurePanel;
    }

    public SmartButton getBrushes() {
        return brushes;
    }

    public SmartButton getViewModes() {
        return viewModes;
    }
}
