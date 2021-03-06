package eidolons.ability.effects.oneshot.item;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;

public class EquipEffect extends MicroEffect implements OneshotEffect {

    private DC_HeroItemObj item;
    private final Boolean weapon;
    private Boolean quickItem = false;

    @OmittedConstructor
    public EquipEffect(DC_HeroItemObj item) {
        this.item = item;
        weapon = item instanceof DC_WeaponObj;
    }

    public EquipEffect() {
        quickItem = true;
        weapon = true;
    }

    public EquipEffect(Boolean weapon) {
        this.weapon = weapon;
    }

    @Override
    public boolean applyThis() {
        if (item == null) {
            if (quickItem) {
                item = (DC_HeroItemObj) ref.getObj(KEYS.ITEM);
            } else {
                item = (DC_HeroItemObj) ref.getObj((weapon) ? KEYS.WEAPON
                 : KEYS.ARMOR);
            }
        }
        // preCheck if item can be equipped at all!

        Unit hero = (Unit) ref.getTargetObj();
        ITEM_SLOT slot = ItemEnums.ITEM_SLOT.ARMOR;
        // preCheck if main hand is occupied

        boolean mainHand = true;
        // item.getProp(prop)
        if (hero.getMainWeapon() != null && hero.getOffhandWeapon() == null) {
            mainHand = false;
        }
        if (weapon || quickItem) {
            slot = (mainHand || quickItem) ? ItemEnums.ITEM_SLOT.MAIN_HAND
             : ItemEnums.ITEM_SLOT.OFF_HAND;
        }

        ref.setID((weapon || quickItem) ? KEYS.WEAPON : KEYS.ARMOR, item
         .getId());

        return hero.equip(item, slot);
    }
}
