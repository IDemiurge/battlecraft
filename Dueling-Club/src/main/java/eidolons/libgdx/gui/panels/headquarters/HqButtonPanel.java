package eidolons.libgdx.gui.panels.headquarters;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;

/**
 * Created by JustMe on 4/25/2018.
 */
public class HqButtonPanel extends HqElement {
    public HqButtonPanel() {
        if (!Flags.isJar()) {
            add(new SmartTextButton("Level Up", STD_BUTTON.MENU, this::levelUp));
            add(new SmartTextButton("Save Type", STD_BUTTON.MENU, this::saveType));
            add(new SmartTextButton("Save as New", STD_BUTTON.MENU, this::saveTypeNew));
        }

            add(new SmartTextButton("View Info", STD_BUTTON.MENU, this::viewInfo));


        if (!HqDataMaster.isSimulationOff())
        add(new SmartTextButton("Undo All", STD_BUTTON.MENU, this::undoAll));


        add(new SmartTextButton("Done", STD_BUTTON.MENU, this::saveAndExit));
    }

    @Override
    protected void update(float delta) {

    }

    private void saveAndExit() {
        HqDataMaster.saveHero(dataSource.getEntity());
        HqMaster.closeHqPanel();
    }

    private void saveType() {
        HqDataMaster.saveHero(dataSource.getEntity(), true, false);
    }

    private void saveTypeNew() {
        HqDataMaster.saveHero(dataSource.getEntity(), true, true);
    }

    private void viewInfo() {
        GuiEventManager.trigger(GuiEventType.SHOW_UNIT_INFO_PANEL, new UnitDataSource(dataSource.getEntity()));
    }

    private void undoAll() {
        HqDataMaster.undoAll(dataSource.getEntity());
    }

    private void levelUp() {
        HqDataMaster.operation(dataSource, HERO_OPERATION.LEVEL_UP);
    }

}
