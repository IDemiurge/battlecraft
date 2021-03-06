package eidolons.ability.ignored.dialog;

import eidolons.ability.InventoryTransactionManager;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.math.Formula;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

public class InventoryDialogEffect extends DialogEffect {
    protected Formula numberOfOperations;

    public InventoryDialogEffect(Formula numberOfOperations) {
        this.numberOfOperations = numberOfOperations;
    }

    @Override
    protected void automaticDialogResolve() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean applyThis() {
        hero = (Unit) ref.getSourceObj();
        List<DC_HeroItemObj> before = new ArrayList<>(hero.getInventory());
        getGame().getInventoryTransactionManager().setActive(true);
        Integer operations = numberOfOperations.getInt(ref);
//        getGame().getInventoryManager().setHero(getSource());
        getGame().getInventoryManager().setOperationsPool(operations);
//        CharacterCreator.getHeroManager().addHero(getSource());

        GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY, hero);
        if (ExplorationMaster.isExplorationOn())
        {
            firePickupEvents(before);
            return true;
        }
        boolean result = (boolean) WaitMaster.waitForInputAnew(
         InventoryTransactionManager.OPERATION);
        getGame().getInventoryTransactionManager().setActive(false);
        if (!result) {
            ref.getActive().setCancelled(true);
        } else {
            firePickupEvents(before);
        }
        return result;
    }

    private void firePickupEvents(List<DC_HeroItemObj> before) {
        for (DC_HeroItemObj item : before) {
            if (!hero.getInventory().contains(item)){
                Ref ref = hero.getRef().getCopy();
                ref.setObj(KEYS.ITEM, item);
                getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.ITEM_ACQUIRED, ref));
            }
        }

        for (DC_HeroItemObj item : hero.getInventory()) {
            if (!before.contains(item)){
                Ref ref = hero.getRef().getCopy();
                ref.setObj(KEYS.ITEM, item);
                getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.ITEM_LOST, ref));
            }
        }
    }

    @Deprecated
    @Override
    protected boolean showDialog() {
        return false;
    }

    protected boolean isPickUp() {
        return false;
    }


}
