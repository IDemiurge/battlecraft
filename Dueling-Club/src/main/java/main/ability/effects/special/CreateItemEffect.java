package main.ability.effects.special;

import main.ability.effects.oneshot.MicroEffect;
import main.client.cc.logic.items.ItemGenerator;
import main.content.CONTENT_CONSTS.MATERIAL;
import main.content.CONTENT_CONSTS.QUALITY_LEVEL;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.data.DataManager;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroItemObj;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

import java.util.List;

public class CreateItemEffect extends MicroEffect {

    private static final String STD_ARMOR_ITEMS = "Chain Shirt;Cuirass;Half Plate;Full Plate";
    private static final String STD_WEAPON_ITEMS = "Great Sword;Claymore;Long Sword;Broad Sword; Falchion;Short Sword;Dagger;Knife"
            + "Maul;Mace;Battle Hammer;Flail;";
    private static final String EXTENDED_ARMOR_ITEMS = "";
    private static final String EXTENDED_WEAPON_ITEMS = "";

    String prefix = "Conjured ";
    private MATERIAL material;
    private Formula durabilityFormula;
    private boolean groupInitialized;
    private boolean weapon;
    private OBJ_TYPE TYPE;
    private List<String> typeList;
    private Boolean extended;
    private boolean quick;
    private Boolean potions;

    public CreateItemEffect(Boolean weapon, Boolean potions) {
        // quick item!
        quick = true;
        this.potions = potions;
        this.weapon = weapon;
    }

    public CreateItemEffect(Boolean weapon, Boolean extended,
                            Formula durabilityFormula, MATERIAL material) {
        this.durabilityFormula = durabilityFormula;
        this.material = material;
        this.weapon = weapon;
        this.extended = extended;
        main.system.auxiliary.LogMaster.log(1, durabilityFormula + ""
                + material);
    }

    private void initGroup() {
        if (quick) {
            if (weapon) {
                // daggers
            } else {
                if (potions) {

                } else {
                    // concoctions!
                }

            }
        }
        if (!weapon) {

            TYPE = OBJ_TYPES.ARMOR;

            typeList = StringMaster.openContainer(STD_ARMOR_ITEMS);
            if (extended)
                typeList.addAll(StringMaster
                        .openContainer(EXTENDED_ARMOR_ITEMS));
        } else {
            TYPE = OBJ_TYPES.WEAPONS;
            typeList = StringMaster.openContainer(STD_WEAPON_ITEMS);
            if (extended)
                typeList.addAll(StringMaster
                        .openContainer(EXTENDED_WEAPON_ITEMS));
        }

        groupInitialized = true;
    }

    @Override
    public boolean applyThis() {
        if (!groupInitialized) {
            initGroup();
        }
        String typeName = ListChooser.chooseType(typeList, TYPE);
        typeName = typeName.trim();
        if (!DataManager.isTypeName(material.getName() + " " + typeName)) {
            ItemGenerator.getDefaultGenerator().generateItem(
                    QUALITY_LEVEL.NORMAL, material,
                    DataManager.getType(typeName, TYPE));
        }

        typeName = material.getName() + " " + typeName;

        ObjType type = new ObjType(DataManager.getType(typeName, TYPE));

        Integer durability = (type.getIntParam(PARAMS.DURABILITY) * durabilityFormula
                .getInt(ref)) / 100;

        type.setParam(PARAMS.DURABILITY, durability);

        DC_HeroItemObj item = ItemGenerator.getDefaultGenerator().createItem(
                type, ref, false); // init

        String itemName = prefix + item.getName();
        item.setName(itemName);

        // Entity hero = ref.getTargetObj();
        // if (!game.getRequirementsManager().check(hero, item))
        // // if cannot use, add to inventory
        // return new ManipulateInventoryEffect(INVENTORY_ACTIONS.ADD)
        // .apply(ref);

        EquipEffect equipEffect = new EquipEffect(item);

        ref.setID((weapon) ? KEYS.WEAPON : KEYS.ARMOR, item.getId());

        return equipEffect.apply(ref);

    }

}