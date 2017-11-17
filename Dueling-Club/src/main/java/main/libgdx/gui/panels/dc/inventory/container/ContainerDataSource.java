package main.libgdx.gui.panels.dc.inventory.container;

import main.entity.Entity;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.module.dungeoncrawl.objects.ContainerObj;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import main.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import main.libgdx.gui.panels.dc.inventory.InventoryValueContainer;
import main.libgdx.gui.panels.dc.inventory.InventoryValueContainerFactory;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryTableDataSource;
import main.system.auxiliary.data.ListMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerDataSource implements InventoryTableDataSource {

    private InventoryValueContainerFactory factory;
    private DC_Obj obj;
    private ContainerClickHandler handler;

    public ContainerDataSource(DC_Obj obj, Unit unit) {
        this.obj = obj;
        handler =new ContainerClickHandler((ContainerObj) obj, unit);// obj.getGame().getInventoryManager().getClickHandler();
        factory = new InventoryValueContainerFactory(handler);
    }

    public ContainerClickHandler getHandler() {
        return handler;
    }

    @Override
    public List<InventoryValueContainer> getInventorySlots() {
        Collection<? extends Entity> list = null;
        if (obj instanceof ContainerObj) {
            list =    new LinkedList<>( ((ContainerObj) obj).getItems());
        } else {
            list = obj.getGame().getDroppedItemManager().getDroppedItems(obj);
        }
        ListMaster.fillWithNullElements(list
         , InventorySlotsPanel.SIZE);
        return factory.getList(list, CELL_TYPE.INVENTORY);
    }
}
