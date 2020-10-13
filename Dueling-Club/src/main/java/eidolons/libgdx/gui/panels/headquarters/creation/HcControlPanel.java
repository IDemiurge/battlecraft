package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.TablePanelX;

/**
 * Created by JustMe on 7/6/2018.
 */
public class HcControlPanel extends TablePanelX{
    public HcControlPanel() {
        //symbol/title
        super(320, 64);
        add(new SymbolButton(STD_BUTTON.CANCEL, this::cancel));
        add(new SymbolButton(STD_BUTTON.UNDO, this::undo));
        add(new SymbolButton(STD_BUTTON.NEXT, this::redo));
        add(new SymbolButton(STD_BUTTON.HELP, this::cancel));
    }

    private void undo() {
        HeroCreationMaster.undo();
    }
    private void redo() {
        HeroCreationMaster.redo();
    }

    private void cancel() {
    }
}
