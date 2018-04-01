package eidolons.libgdx.gui.panels.dc.inventory;

import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.HeroManager;
import eidolons.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import main.entity.Entity;
import main.entity.type.ObjType;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 3/30/2017.
 */
public class InventoryClickHandlerImpl implements InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!

    protected Unit unit;
    protected ObjType buffer;
    protected boolean dirty;

    public InventoryClickHandlerImpl(Unit unit) {
        this.unit = unit;
        buffer = new ObjType(unit.getType());
//        buffer = unit.getGame().getState().getManager()
//         .getKeeper().getCloner().clone()

//        InventoryTransactionManager.updateType(hero);
//        bufferedType = hero.getType();
//        heroModel = hero;
//        inventoryManager.getInvListManager().setHero(heroModel);
//        CharacterCreator.getHeroManager().addHero(heroModel);


//        operationsData = "";
//        cachedValue = cell.getProperty(PROPS.DROPPED_ITEMS);
    }

    @Override
    public boolean cellClicked(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                               boolean altClick, Entity cellContents) {

        boolean result = false;
        OPERATIONS operation = getOperation(cell_type, clickCount, rightClick,
         altClick, cellContents);
        if (operation == null) {
            result = false;
        } else {
            //        String arg = getArg(cell_type, clickCount, rightClick,
//         altClick, cellContents);
//        if (arg == null) return false;
            if (Eidolons.game.getInventoryManager().tryExecuteOperation
             (operation, cellContents)) {
                dirty = true;
                result = true;
            }
        }

        if (result) {
            GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY,
             new InventoryDataSource(unit));
        }

        return result;

    }


    protected OPERATIONS getOperation(CELL_TYPE cell_type, int clickCount, boolean rightClick,
                                      boolean altClick, Entity cellContents) {
        if (cell_type == null) {
            return null;
        }
        if (cellContents == null) {
            return null;
        }
        switch (cell_type) {
            case AMULET:
            case RING:
            case WEAPON_MAIN:
            case WEAPON_OFFHAND:
                if (altClick) {
                    return OPERATIONS.DROP;
                }
                if (rightClick || clickCount > 1) {
                    return OPERATIONS.UNEQUIP;
                }
                return null;

            case QUICK_SLOT:
                if (rightClick) {
                    return OPERATIONS.UNEQUIP_QUICK_SLOT;
                }
                if (clickCount > 1) {
                    if (HeroManager.isQuickSlotWeapon(cellContents)) {
                        return OPERATIONS.EQUIP;
                    } else {
                        return OPERATIONS.UNEQUIP_QUICK_SLOT;
                    }
                }
                if (altClick) {
                    return OPERATIONS.DROP;
                }
                return null;
            case ARMOR:
                //preCheck can be unequipped

                break;
            case INVENTORY:
                if (altClick) {
                    return OPERATIONS.EQUIP_QUICK_SLOT;
                }
                if (rightClick) {
                    return OPERATIONS.DROP;
                }
                if (clickCount > 1) {
                    if (HeroManager.isQuickSlotOnly(cellContents))
                        return OPERATIONS.EQUIP_QUICK_SLOT;
                    return OPERATIONS.EQUIP;
                }
        }
        return null;
    }

    @Override
    public boolean itemDragAndDropped(CELL_TYPE cell_type,
                                      Entity cellContents, Entity droppedItem
    ) {

        return false;
    }

    @Override
    public void undoClicked() {
        if (!isUndoEnabled()) {
            return;
        }
//        inventoryManager.getInvListManager().setOperationsLeft(getOperationsLeft());
        if (CharacterCreator.getHeroManager().undo(unit)) {
//         modifications --;
            Integer op = unit.getGame().getInventoryManager().getOperationsLeft();
            op--;
            if (op == unit.getGame().getInventoryManager().getOperationsPool()) {
                dirty = false;
            }
            refreshPanel();
        }
    }

    public void refreshPanel() {
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, unit);
    }

    @Override
    public void doneClicked() {
        if (!isDoneEnabled()) {
            return;
        }
//        InventoryTransactionManager.updateType(unit); ???
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, true);
    }

    @Override
    public void cancel() {
        unit.applyType(buffer);
    }

    @Override
    public void cancelClicked() {
        if (!isCancelEnabled()) {
            return;
        }
        cancel();
        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, false);

    }

    @Override
    public boolean isUndoEnabled() {
        return dirty;
    }

    @Override
    public boolean isDoneEnabled() {
        return dirty;
    }

    @Override
    public boolean isCancelEnabled() {
        return true;
    }

    protected String getArg(CELL_TYPE cell_type, int clickCount, boolean rightClick, boolean altClick, Entity cellContents) {
        return cellContents.getName();
    }

}