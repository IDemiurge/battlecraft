package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.gui.screen.LE_Screen;

public class LE_KeyHandler extends LE_Handler {

    public LE_KeyHandler(LE_Manager manager) {
        super(manager);
    }

    public void keyDown(int keyCode) {
        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (ctrl){
            switch (keyCode) {
                case Input.Keys.SPACE:
                    LevelEditor.getCurrent().getManager().getEditHandler().edit();
                    break;
                case Input.Keys.Z:
                    LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                    break;
                case Input.Keys.Y:
                    LevelEditor.getCurrent().getManager().getOperationHandler().redo();
                    break;
                case Input.Keys.C:
                    LevelEditor.getCurrent().getManager().getModelManager().copy();
                    break;
                case Input.Keys.V:
                    LevelEditor.getCurrent().getManager().getModelManager().paste();
                    break;

            }
        }
        switch (keyCode) {
            case Input.Keys.ALT_RIGHT:
                getModel().getDisplayMode().toggleAll();
                break;
            case Input.Keys.CONTROL_RIGHT:
                getModel().getDisplayMode().onAll();
                break;
            case Input.Keys.SHIFT_RIGHT:
                getModel().getDisplayMode(). offAll();
                break;

            case Input.Keys.FORWARD_DEL:
            case Input.Keys.DEL:
                getObjHandler().removeSelected();
                break;
            case Input.Keys.ESCAPE:
                //do we have a 'main menu'?
                getSelectionHandler().deselect();
                break;
            case Input.Keys.ENTER:
                LE_Screen.getInstance().getGuiStage().getDialog().ok();
                //approve dialogue?

            case Input.Keys.SPACE:

                //camera?

        }
        }
        //hold down:
        /*
        apply view mode
        toggle layer
        toggle mouse mode

         */

    public void keyTyped(char character) {
        boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (ctrl){
            switch (character) {
                case Input.Keys.SPACE:
                    LevelEditor.getCurrent().getManager().getEditHandler().edit();
                    break;
                case Input.Keys.Z:
                    LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                    break;
                case Input.Keys.Y:
                    LevelEditor.getCurrent().getManager().getOperationHandler().redo();
                    break;
                case Input.Keys.C:
                    LevelEditor.getCurrent().getManager().getModelManager().copy();
                    break;
                case Input.Keys.V:
                    LevelEditor.getCurrent().getManager().getModelManager().paste();
                    break;

            }
        }
    }
}
