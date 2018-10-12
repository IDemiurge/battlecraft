package eidolons.libgdx.gui.menu.selection.rng;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 10/11/2018.
 */
public class RngListPanel extends ItemListPanel {
    @Override
    public List<SelectableItemData> toDataList(List<? extends Entity> objTypes) {
        List<SelectableItemData> list = new LinkedList<>();
        for (Entity sub : objTypes) {
            SelectableItemData item = new SelectableItemData(sub);
            list.add(item);
            item.setDescription("Deep, treacherous dungeon brought to you by Machine's Imagination");
            item.setFullsizeImagePath(item.getEntity().getProperty(G_PROPS.FULLSIZE_IMAGE));
        }

        return list;
    }
}
