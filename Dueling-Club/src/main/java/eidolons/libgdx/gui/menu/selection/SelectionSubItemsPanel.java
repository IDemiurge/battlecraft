package eidolons.libgdx.gui.menu.selection;

import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanelX;

/**
 * Created by JustMe on 7/3/2018.
 */
public class SelectionSubItemsPanel extends TablePanelX{
    private final ItemListPanel panel;

    public SelectionSubItemsPanel(String[] items, SelectableItemData item, ItemListPanel itemListPanel) {
        setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        this.panel = itemListPanel;
        for (String sub : items) {
            TextButtonX line = new TextButtonX( sub,
             StyleHolder.getHqTextButtonStyle(16), ()-> clicked(item, sub) );
            add(line).center().row();
        }
    }

    private void clicked(SelectableItemData item, String sub) {
        panel.subItemClicked(item, sub);
    }
}
