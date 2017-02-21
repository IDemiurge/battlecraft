package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.texture.TextureManager;

public class InitiativeAndActionPointsPanel extends TablePanel {
    public InitiativeAndActionPointsPanel(String initiative, String actionPoints) {
        rowDirection = TOP_RIGHT;
        VerticalValueContainer initiativeContainer = new VerticalValueContainer(TextureManager.getOrCreateR("UI/value icons/n_of_counters_s.png"), initiative);
        VerticalValueContainer actionContainer = new VerticalValueContainer(TextureManager.getOrCreateR("UI/value icons/n_of_actions_s.png"), actionPoints);

        initiativeContainer.fill().left().bottom();
        actionContainer.fill().right().bottom();

        Table innerTable = new Table();
        innerTable.add(initiativeContainer, actionContainer);

        addElement(new Container(innerTable).fill().left().bottom());
    }
}
