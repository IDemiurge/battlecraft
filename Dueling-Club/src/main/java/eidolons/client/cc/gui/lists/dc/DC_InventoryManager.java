package eidolons.client.cc.gui.lists.dc;

import eidolons.client.cc.CharacterCreator;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import eidolons.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandlerImpl;
import eidolons.system.audio.DC_SoundMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * 2 properties?
 *
 * @author JustMe
 */
public class DC_InventoryManager {

    protected Integer operationsLeft = 0;
    protected Integer operationsPool = 0;
    private DC_Game game;
    private Unit hero;
    private OBJ_TYPE TYPE;
    private InventoryClickHandler clickHandler;

    public DC_InventoryManager(DC_Game game) {
        this.TYPE = DC_TYPE.ITEMS;
        this.game = game;
    }


    public boolean hasOperations(int n) {
        return getOperationsLeft() >= n;
    }


    public boolean hasOperations() {
        return getOperationsLeft() > 0;
    }


    public Integer getOperationsLeft() {
        return operationsLeft;
    }


    public void setOperationsLeft(Integer operationsLeft) {
        this.operationsLeft = operationsLeft;
    }

    public Integer getOperationsPool() {
        return operationsPool;
    }

    public void setOperationsPool(Integer operationsPool) {
        this.operationsPool = operationsPool;
        setOperationsLeft(operationsPool);
    }

    public void processOperationCommand(String string) {
        for (String substring : StringMaster.open(string)) {
            OPERATIONS operation = new EnumMaster<OPERATIONS>().retrieveEnumConst(OPERATIONS.class,
             substring.split(StringMaster.PAIR_SEPARATOR)[0]);
            String typeName = (substring.split(StringMaster.PAIR_SEPARATOR)[1]);
            execute(operation, typeName);
        }
    }

    private void execute(OPERATIONS operation, String typeName) {
        DC_HeroItemObj item = getHero().findItem(typeName, getMode(operation));
        execute(operation, item.getType());
    }

    public boolean tryExecuteOperation(OPERATIONS operation, Entity type) {
        if (!canDoOperation(operation, type)) {
            return false;
        }
        try {
            return checkExecuted(operation, type);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
    }

    private boolean canDoOperation(OPERATIONS operation, Entity type) {
        if (ExplorationMaster.isExplorationOn()) {
            return true;
        }

        if (!hasOperations()) {
            return false;
        }

//        String s = CharacterCreator.getHeroManager()
//         .checkRequirements(getHero(), type, RequirementsManager.NORMAL_MODE);
        // if (s != null) {
        return true;
    }


    private boolean checkExecuted(OPERATIONS operation, Entity type) {
        int cost = execute(operation, type);
        if (cost == 0) {
            return false;
        }
        operationDone(cost, operation, type.getName());
        return true;
    }

    private int execute(OPERATIONS operation, Entity type) {
        boolean alt = false;
//        if (operation == OPERATIONS.PICK_UP) {
//            item = unit.getGame().getDroppedItemManager().findDroppedItem(typeName,
//             unit.getCoordinates()); // TODO finish
//        }
        DC_HeroItemObj item = null;
        item = (DC_HeroItemObj) type;
//        getHero().
//         findItem(type.getName(),
//          getMode(operation));
        boolean drop = false;
        int cost = 0;
        switch (operation) {
            case EQUIP:
                cost = CharacterCreator.getHeroManager().addSlotItem(getHero(), type, alt);
                break;
            case DROP:
                drop = true;
            case UNEQUIP:
                cost = 1;
                getHero().unequip(item, drop);
                break;
            case EQUIP_QUICK_SLOT:
                // CharacterCreator.getHeroManager().addItem(unit, type,
                // OBJ_TYPES.ITEMS, PROPS.QUICK_ITEMS, true);
                cost = CharacterCreator.getHeroManager().addQuickItem(getHero(), type);
                break;
            case PICK_UP:
                cost = 1;
                game.getDroppedItemManager().pickUp(getHero(), type);
                break;

            case UNEQUIP_QUICK_SLOT:
                cost = 1;
                CharacterCreator.getHeroManager().removeQuickSlotItem(getHero(), type);
                break;
        }
        return cost;
    }

    private Boolean getMode(OPERATIONS operation) {
        Boolean mode = null;
        if (operation == OPERATIONS.UNEQUIP_QUICK_SLOT) {
            mode = true;
        }
        if (operation == OPERATIONS.EQUIP || operation == OPERATIONS.EQUIP_QUICK_SLOT) {
            mode = false;
        }
        return mode;
    }


    public boolean operationDone(OPERATIONS operation, String string) {
        return operationDone(1, operation, string);
    }


    public boolean operationDone(int n, OPERATIONS operation, String string) {
        setOperationsLeft(getOperationsLeft() - n);
        boolean result = hasOperations();
//        game.getInventoryTransactionManager().getWindow()
//         .appendOperationData(operation, string);
//        game.getInventoryTransactionManager().getWindow().setNumberOfOperations(
//         operationsLeft);
        return result;
    }

    public boolean addType(ObjType itemType, boolean alt) {
        if (!hasOperations()) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return false;
        }
        int result = CharacterCreator.getHeroManager().addSlotItem(getHero(), itemType, alt);
        operationDone(result, OPERATIONS.EQUIP, itemType.getName());
        return true;
    }


    public void removeType(Entity item, PROPERTY p) {
        if (!hasOperations()) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return;
        }
        OPERATIONS operations = OPERATIONS.UNEQUIP;
        if (p == PROPS.INVENTORY) {
            CharacterCreator.getHeroManager().removeItem(getHero(), item, p, TYPE, true);
        } else {
            if (item.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
                CharacterCreator.getHeroManager().removeJewelryItem(getHero(), item);
            } else if (p == PROPS.QUICK_ITEMS) {
                CharacterCreator.getHeroManager().removeQuickSlotItem(getHero(), item);
                operations = OPERATIONS.UNEQUIP_QUICK_SLOT;
            }
        }
        operationDone(operations, item.getName());
    }


    public Unit getHero() {
        if (hero == null) {
            return game.getManager().getActiveObj();
        }
        return hero;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
        clickHandler = new InventoryClickHandlerImpl(hero);
    }

    public InventoryClickHandler getClickHandler() {
        if (clickHandler == null) {
            clickHandler = new InventoryClickHandlerImpl(getHero());
        }
        return clickHandler;
    }


    public enum OPERATIONS {
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT,
    }

}