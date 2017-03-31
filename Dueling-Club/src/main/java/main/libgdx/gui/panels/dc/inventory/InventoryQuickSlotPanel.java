package main.libgdx.gui.panels.dc.inventory;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.QuickSlotDataSource;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventoryQuickSlotPanel extends TablePanel {


    public InventoryQuickSlotPanel() {

    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);

        final List<InventoryValueContainer> quickSlots = ((QuickSlotDataSource) getUserObject()).getQuickSots();


        int maxLength = Math.min(8, quickSlots.size());

        for (int i = 0; i < maxLength; i++) {
            ValueContainer valueContainer = quickSlots.get(i);
            if (valueContainer == null) {
                valueContainer = new ValueContainer(getOrCreateR("UI/empty_pack.jpg"));
            }

            addElement(valueContainer).fill(0, 1).expand(0, 1).center();
        }

        for (int i = maxLength; i < 8; i++) {
            addElement(new ValueContainer(getOrCreateR("UI/disabled_pack.jpg")))
                    .fill(0, 1).expand(0, 1).center();
        }
    }
}
