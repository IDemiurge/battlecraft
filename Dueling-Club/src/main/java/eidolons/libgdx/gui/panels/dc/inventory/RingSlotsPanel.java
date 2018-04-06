package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;

import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class RingSlotsPanel extends TablePanel {
    boolean left;

    public RingSlotsPanel(boolean left) {
        this.left = left;
//        addElement(new ValueContainer(getOrCreateR(CELL_TYPE.RING.getSlotImagePath())));
//        addElement(new ValueContainer(getOrCreateR(CELL_TYPE.RING.getSlotImagePath())));
//        row();
//        addElement(new ValueContainer(getOrCreateR(
//         CELL_TYPE.RING.getSlotImagePath())));
//        addElement(new ValueContainer(getOrCreateR(CELL_TYPE.RING.getSlotImagePath())));
    }

//    @Override
//    public void clear() {
//
//    }

    @Override
    public void afterUpdateAct(float delta) {
        if (getUserObject() == null)
            return;
        clear();
        super.afterUpdateAct(delta);
        final List<InventoryValueContainer> rings =
         ((EquipDataSource) getUserObject()).rings();

        int a = 0;
        for (int i = 0; i < 8; i++) {
            if (i % 2 == (left ? 1 : 0)) {
                continue;
            }
            a++;
            ValueContainer valueContainer = rings.get(i);
            if (valueContainer == null) {
                valueContainer = new ValueContainer(getOrCreateR(CELL_TYPE.RING.getSlotImagePath()));
            }
            add(valueContainer).expand(0, 0);
            if ((a) % 2 == 0) {
                row();
            }
        }
    }
}