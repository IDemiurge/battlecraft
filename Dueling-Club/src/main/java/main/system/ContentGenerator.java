package main.system;

import main.content.CONTENT_CONSTS.*;
import main.content.DC_ContentManager;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.rules.DC_ActionManager.WEAPON_ATTACKS;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContentGenerator {

    static PARAMS[] params = {PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.ARMOR,};

    public static void adjustParties() {

        for (ObjType type : DataManager.getTypes(OBJ_TYPES.PARTY)) {
            ObjType leader = DataManager.getType(type.getProperty(PROPS.LEADER), OBJ_TYPES.CHARS);
            if (leader == null) {
                List<String> members = StringMaster.openContainer(type.getProperty(PROPS.MEMBERS));
                if (members.isEmpty())
                    continue;
                leader = DataManager.getType(members.get(0), OBJ_TYPES.CHARS);
                if (leader == null)
                    continue;
                type.setProperty(PROPS.LEADER, leader.getName());
            }

            type.setImage(leader.getImagePath());
        }

    }

    public static void generateWaveUnitTypes(ObjType t) {
        List<String> list = new LinkedList<>();
        for (String u : StringMaster.openContainer(t.getProperty(PROPS.SHRUNK_PRESET_GROUP))) {
            if (!list.contains(u))
                list.add(u);
        }
    }

    public static void setAbilBaseTypes() {
        List<List<ObjType>> groups = new LinkedList<>();
        List<ObjType> group = new LinkedList<>();

    }

    public static void convertSkillStrings(ObjType type) {
        for (String s : StringMaster.openContainer(type.getProp("passives"))) {
            switch (s) {
                case "ParamBonus":
                    type.addProperty(PROPS.PARAMETER_BONUSES, s);
                    break;
            }
        }

    }

    public static void initMaterials() {

        for (MATERIAL material : MATERIAL.values()) {
            Map<DAMAGE_TYPE, RESIST_GRADE> map = getDefaultResistMap(material.getGroup());
            // setSpecials(material, map);
            map = getDefaultSelfDamageMap(material.getGroup());
            material.setResistGradeMap(map);
            material.setSelfDamageGradeMap(map);
            int hardness = getHardness(material);
            material.setHardness(hardness);
        }
    }

    private static int getHardness(MATERIAL material) {
        switch (material) {
            case COPPER:
                return 65;
            case ADAMANTIUM:
            case MITHRIL:
            case STEEL:
                return 115;
        }
        return getDefaultHardness(material.getGroup());
    }

    private static int getDefaultHardness(ITEM_MATERIAL_GROUP group) {
        switch (group) {
            case BONE:
                return 65;
            case CLOTH:
                return 15;
            case LEATHER:
                return 50;
            case METAL:
                return 85;
            case NATURAL:
                return 25;
            case STONE:
                return 100;
            case WOOD:
                return 35;
        }
        return 0;
    }

    public static void generateSpellParams(ObjType t) {
        if (t.getName().contains(" Bolt")) {
            t.addProperty(G_PROPS.SPELL_TAGS, "Missile", true);
        }

        if (t.checkProperty(G_PROPS.SPELL_TAGS, "missile")) {
            t.setParam(PARAMS.IMPACT_AREA, 15);
        }
    }

    public static void generateArmorParams(ObjType t) {

        Integer n = t.getIntParam(PARAMS.MATERIAL_QUANTITY);
        Integer layers = t.getIntParam(PARAMS.ARMOR_LAYERS);
        if (layers == 0)
            layers = 1;
        else {

            Integer durability_modifier = t.getIntParam(PARAMS.DURABILITY_MODIFIER);
            int i = durability_modifier / layers / 5;
            i *= 5;
            t.setParam(PARAMS.DURABILITY_MODIFIER, i);
            return;
        }
        if (n > 6) {
            layers = 2;
            n = n * 4 / 5;
        }
        if (n > 9) {
            layers = 3;
            n = n * 2 / 3;
        }
        int cover = 15 * n;
        t.setParam(PARAMS.COVER_PERCENTAGE, cover);
        t.setParam(PARAMS.MATERIAL_QUANTITY, n * layers);
        t.setParam(PARAMS.ARMOR_LAYERS, layers);
        // armor mod?

    }

    private static Map<DAMAGE_TYPE, RESIST_GRADE> getDefaultResistMap(ITEM_MATERIAL_GROUP group) {
        Map<DAMAGE_TYPE, RESIST_GRADE> map = new HashMap<>();
        for (DAMAGE_TYPE dmg_type : DAMAGE_TYPE.values()) {
            if (!dmg_type.isMagical())
                map.put(dmg_type, RESIST_GRADE.Normal);
            else
                map.put(dmg_type, RESIST_GRADE.Ineffective);
        }

        switch (group) {
            case BONE:
                map.put(DAMAGE_TYPE.PIERCING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Resistant);
                break;
            case CLOTH:
            case LEATHER:
                map.put(DAMAGE_TYPE.BLUDGEONING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.COLD, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.FIRE, RESIST_GRADE.Resistant);
                break;
            case METAL:
            case STONE:
                map.put(DAMAGE_TYPE.FIRE, RESIST_GRADE.Resistant);
                break;

        }
        switch (group) {
            case BONE:
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Impregnable);
                break;
            case CLOTH:
                map.put(DAMAGE_TYPE.PIERCING, RESIST_GRADE.Vulnerable);
                break;
            case LEATHER:
                map.put(DAMAGE_TYPE.FIRE, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SLASHING, RESIST_GRADE.Vulnerable);
                break;
            case METAL:
                map.put(DAMAGE_TYPE.SLASHING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.BLUDGEONING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.LIGHT, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.ACID, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.COLD, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Ineffective);
                break;
            case STONE:
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Impregnable);
                map.put(DAMAGE_TYPE.PIERCING, RESIST_GRADE.Resistant);
                break;
            case CRYSTAL:
                map.put(DAMAGE_TYPE.FIRE, RESIST_GRADE.Impregnable);
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Impregnable);
                map.put(DAMAGE_TYPE.PIERCING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SLASHING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.ACID, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.BLUDGEONING, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.COLD, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.LIGHT, RESIST_GRADE.Ineffective);
                break;

        }
        // fill with 'normals'
        return map;
    }

    private static Map<DAMAGE_TYPE, RESIST_GRADE> getDefaultSelfDamageMap(ITEM_MATERIAL_GROUP group) {
        Map<DAMAGE_TYPE, RESIST_GRADE> map = new HashMap<>();
        for (DAMAGE_TYPE dmg_type : DAMAGE_TYPE.values()) {
            if (!dmg_type.isMagical())
                map.put(dmg_type, RESIST_GRADE.Normal);
            else
                map.put(dmg_type, RESIST_GRADE.Ineffective);
        }

        switch (group) {
            case BONE:
                map.put(DAMAGE_TYPE.PIERCING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Resistant);
                break;
            case CLOTH:
            case LEATHER:
                map.put(DAMAGE_TYPE.BLUDGEONING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.COLD, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.FIRE, RESIST_GRADE.Resistant);
                break;
            case METAL:
            case STONE:
                map.put(DAMAGE_TYPE.FIRE, RESIST_GRADE.Resistant);
                break;
        }
        switch (group) {
            case BONE:
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Impregnable);
                break;
            case CLOTH:
                map.put(DAMAGE_TYPE.PIERCING, RESIST_GRADE.Vulnerable);
                break;
            case LEATHER:
                map.put(DAMAGE_TYPE.FIRE, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SLASHING, RESIST_GRADE.Vulnerable);
                break;
            case METAL:
                map.put(DAMAGE_TYPE.BLUDGEONING, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.LIGHT, RESIST_GRADE.Resistant);
                map.put(DAMAGE_TYPE.SONIC, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.ACID, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.COLD, RESIST_GRADE.Vulnerable);
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Ineffective);
                break;
            case STONE:
                map.put(DAMAGE_TYPE.LIGHTNING, RESIST_GRADE.Impregnable);
                map.put(DAMAGE_TYPE.PIERCING, RESIST_GRADE.Resistant);
                break;

        }
        // fill with 'normals'
        return map;
    }

    public static String getNaturalArmorTypeForUnit(DC_HeroObj attacked) {
        return StringMaster.getWellFormattedString(getObjectArmorTypeForUnit(attacked).toString());
    }

    public static OBJECT_ARMOR_TYPE getObjectArmorTypeForUnit(DC_HeroObj attacked) {
        if (attacked.checkClassification(CLASSIFICATIONS.WRAITH))
            return OBJECT_ARMOR_TYPE.ETHEREAL;
        if (attacked.checkClassification(CLASSIFICATIONS.MECHANICAL))
            return OBJECT_ARMOR_TYPE.METAL;
        if (attacked.checkClassification(CLASSIFICATIONS.CONSTRUCT))
            return OBJECT_ARMOR_TYPE.STONE;
        if (attacked.checkClassification(CLASSIFICATIONS.ELEMENTAL))
            return OBJECT_ARMOR_TYPE.CRYSTAL;
        if (attacked.checkClassification(CLASSIFICATIONS.UNDEAD))
            return OBJECT_ARMOR_TYPE.BONE;
        return OBJECT_ARMOR_TYPE.FLESH;
    }

    private static RESIST_GRADE getGradeForUnitType(ObjType t, DAMAGE_TYPE dmg_type) {
        OBJECT_ARMOR_TYPE armor_type = getObjectArmorTypeForUnit(new DC_HeroObj(t));
        ITEM_MATERIAL_GROUP group = armor_type.getGroup();
        if (group == null) {
            return RESIST_GRADE.Normal;
        }
        return getDefaultResistMap(group).get(dmg_type);
    }

    public static void generateArmorPerDamageType(ObjType t, MATERIAL material) {
        Integer armor = t.getIntParam(PARAMS.ARMOR);

        for (DAMAGE_TYPE dmg_type : DAMAGE_TYPE.values()) {
            RESIST_GRADE grade = (material == null) ? getGradeForUnitType(t, dmg_type) : material
                    .getResistGrade(dmg_type);
            PROPERTY prop = DC_ContentManager.getResistGradeForDmgType(dmg_type);
            if (prop == null)
                continue;

            t.setProperty(prop, grade.toString());
            t.setParam(DC_ContentManager.getArmorParamForDmgType(dmg_type), Math.round(armor
                    * grade.getPercent() / 100));
            if (material == null)
                grade = new EnumMaster<RESIST_GRADE>().retrieveEnumConst(RESIST_GRADE.class, t
                        .getProperty(DC_ContentManager.getResistGradeForDmgType(dmg_type)));
            else
                grade = material.getSelfDamageGrade(dmg_type);
            t.setProperty(DC_ContentManager.getSelfDamageGradeForDmgType(dmg_type), grade
                    .toString());

            prop = DC_ContentManager.getSelfDamageGradeForDmgType(dmg_type);
            if (prop == null)
                continue;
            grade = new EnumMaster<RESIST_GRADE>().retrieveEnumConst(RESIST_GRADE.class, t
                    .getProperty(prop));

            t.setParam(DC_ContentManager.getArmorSelfDamageParamForDmgType(dmg_type), Math
                    .round(grade.getPercent()));

        }

    }

    private static String getDefaultWeaponActions(WEAPON_GROUP group) {
        switch (group) {
            case AXES:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Axe_Swing, WEAPON_ATTACKS.Chop, WEAPON_ATTACKS.Hack,
                        WEAPON_ATTACKS.Hook));
            case POLLAXES:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Spike_Stab, WEAPON_ATTACKS.Axe_Swing, WEAPON_ATTACKS.Chop,
                        WEAPON_ATTACKS.Hack, WEAPON_ATTACKS.Hook));

            case FLAILS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Heavy_Swing, WEAPON_ATTACKS.Chain_Thrust,
                        WEAPON_ATTACKS.Head_Smash));
            case HAMMERS:
                return StringMaster
                        .constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Heavy_Swing,
                                WEAPON_ATTACKS.Head_Smash, WEAPON_ATTACKS.Slam));
            case MACES:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Head_Smash, WEAPON_ATTACKS.Heavy_Swing));
            case CLUBS:
                return StringMaster
                        .constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Heavy_Swing,
                                WEAPON_ATTACKS.Head_Smash, WEAPON_ATTACKS.Slam));

            case GREAT_SWORDS:
            case LONG_SWORDS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Sword_Swing, WEAPON_ATTACKS.Slash,
                        WEAPON_ATTACKS.Blade_Thrust, WEAPON_ATTACKS.Hilt_Smash));
            case SHORT_SWORDS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Blade_Thrust, WEAPON_ATTACKS.Slash,
                        WEAPON_ATTACKS.Hilt_Smash, WEAPON_ATTACKS.Stab));
            case DAGGERS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Blade_Thrust, WEAPON_ATTACKS.Slash, WEAPON_ATTACKS.Stab));

            case SCYTHES:
                return StringMaster.constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Hook,
                        WEAPON_ATTACKS.Axe_Swing, WEAPON_ATTACKS.Hack, WEAPON_ATTACKS.Pole_Push,
                        WEAPON_ATTACKS.Pole_Smash));
            case SPEARS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Spear_Poke, WEAPON_ATTACKS.Impale,
                        WEAPON_ATTACKS.Pole_Smash, WEAPON_ATTACKS.Pole_Push));
            case STAVES:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Pole_Smash, WEAPON_ATTACKS.Pole_Thrust,
                        WEAPON_ATTACKS.Pole_Push));
            case SHIELDS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Shield_Push, WEAPON_ATTACKS.Shield_Bash));
            case CLAWS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Slice, WEAPON_ATTACKS.Rip));
            case FISTS:
                return StringMaster.constructStringContainer(ListMaster
                        .toList(WEAPON_ATTACKS.Punch, WEAPON_ATTACKS.Fist_Swing,
                                WEAPON_ATTACKS.Elbow_Smash));
            case FEET:
                return StringMaster
                        .constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Hook));
            case MAWS:
                return StringMaster.constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Bite,
                        WEAPON_ATTACKS.Dig_Into, WEAPON_ATTACKS.Tear));
            case FANGS:
                return StringMaster.constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Bite,
                        WEAPON_ATTACKS.Dig_Into));
            case TAILS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Tail_Smash, WEAPON_ATTACKS.Tail_Sting));
            case HORNS:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Pierce, WEAPON_ATTACKS.Tear));
            case NEEDLES:
                return StringMaster.constructStringContainer(ListMaster.toList(
                        WEAPON_ATTACKS.Pierce, WEAPON_ATTACKS.Slice, WEAPON_ATTACKS.Stab));
            case HOOVES:
                return StringMaster.constructStringContainer(ListMaster
                        .toList(WEAPON_ATTACKS.Hoof_Slam));
            case BEAKS:
                return StringMaster.constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Bite,
                        WEAPON_ATTACKS.Tear, WEAPON_ATTACKS.Dig_Into));
            case EYES:
                return StringMaster
                        .constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Hook));
            case FORCE:
                return StringMaster
                        .constructStringContainer(ListMaster.toList(WEAPON_ATTACKS.Hook));

        }
        return null;
    }

    public static void generateWeaponParams(ObjType t) {
        // if (!t.checkProperty(PROPS.WEAPON_ATTACKS)) {
        WEAPON_GROUP group = new EnumMaster<WEAPON_GROUP>().retrieveEnumConst(WEAPON_GROUP.class, t
                .getProperty(G_PROPS.WEAPON_GROUP));
        if (group != null) {
            String weaponActions = getDefaultWeaponActions(group);
            if (weaponActions != null) {
                if (!t.checkProperty(PROPS.WEAPON_ATTACKS))
                    t.setProperty(PROPS.WEAPON_ATTACKS, weaponActions);
            }
            String defaultWeaponAction = getDEFAULT_ATTACK_ACTION(group);
            t.setProperty(PROPS.DEFAULT_ATTACK_ACTION, defaultWeaponAction);
            defaultWeaponAction = getDEFAULT_COUNTER_ATTACK_ACTION(group);
            t.setProperty(PROPS.DEFAULT_COUNTER_ATTACK_ACTION, defaultWeaponAction);
            defaultWeaponAction = getDEFAULT_INSTANT_ATTACK_ACTION(group);
            t.setProperty(PROPS.DEFAULT_INSTANT_ATTACK_ACTION, defaultWeaponAction);
            defaultWeaponAction = getDEFAULT_ATTACK_OF_OPPORTUNITY_ACTION(group);
            t.setProperty(PROPS.DEFAULT_ATTACK_OF_OPPORTUNITY_ACTION, defaultWeaponAction);
        }
        if (t.getIntParam(PARAMS.IMPACT_AREA) == 0) {
            if (group == null) {
                WEAPON_TYPE ty = new EnumMaster<WEAPON_TYPE>().retrieveEnumConst(WEAPON_TYPE.class,
                        t.getProperty(G_PROPS.WEAPON_TYPE));
                if (ty == WEAPON_TYPE.NATURAL) {
                    Integer area = 20;
                    t.setParam(PARAMS.IMPACT_AREA, area);
                } else if (ty == WEAPON_TYPE.MAGICAL) {
                    Integer area = 15;
                    t.setParam(PARAMS.IMPACT_AREA, area);
                }
                return;
            }
            WEAPON_SIZE size = new EnumMaster<WEAPON_SIZE>().retrieveEnumConst(WEAPON_SIZE.class, t
                    .getProperty(G_PROPS.WEAPON_SIZE));
            if (size == null)
                return;
            Integer area = getDefaultImpactArea(group, size);
            t.setParam(PARAMS.IMPACT_AREA, area);
        }
    }

    private static String getDEFAULT_ATTACK_OF_OPPORTUNITY_ACTION(WEAPON_GROUP group) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getDEFAULT_INSTANT_ATTACK_ACTION(WEAPON_GROUP group) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getDEFAULT_COUNTER_ATTACK_ACTION(WEAPON_GROUP group) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getDEFAULT_ATTACK_ACTION(WEAPON_GROUP group) {
        switch (group) {

        }
        return null;
    }

    private static Integer getDefaultImpactArea(WEAPON_GROUP g, WEAPON_SIZE size) {
        switch (g) {

            case ARROWS:
                return 10;
            case HAMMERS:
                switch (size) {
                    case HUGE:
                        return 60;
                    case LARGE:
                        return 45;
                    case MEDIUM:
                        return 35;
                    case SMALL:
                        return 25;
                    case TINY:
                        return 15;
                }
            case AXES:
                switch (size) {
                    case HUGE:
                        return 25;
                    case LARGE:
                        return 20;
                    case MEDIUM:
                        return 15;
                    case SMALL:
                        return 10;
                    case TINY:
                        return 5;
                }
            case BOLTS:
                return 15;
            case BOWS:
                return 20;
            case CLUBS:
                return 30;
            case CROSSBOWS:
                return 10;
            case DAGGERS:
                return 10;
            case FLAILS:
                return 25;
            case GREAT_SWORDS:
                return 40;
            case LONG_SWORDS:
                return 30;
            case MACES:
                return 30;
            case POLLAXES:
                return 20;
            case SCYTHES:
                return 30;
            case SHIELDS:
                return 50;
            case SHORT_SWORDS:
                return 20;
            case SPEARS:
                return 15;
            case STAVES:
                return 20;
            case WANDS:
                return 10;
            case ORBS:
                return 10;

        }
        return 25;
    }

    // public static void generateBfObjParams(ObjType t) {
    // BF_OBJECT_TYPE type = new EnumMaster<BF_OBJECT_TYPE>().retrieveEnumConst(
    // BF_OBJECT_TYPE.class, t.getProperty(G_PROPS.BF_OBJECT_TYPE));
    // BF_OBJECT_GROUP group = new
    // EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(
    // BF_OBJECT_GROUP.class, t.getProperty(G_PROPS.BF_OBJECT_GROUP));
    // BF_OBJECT_SIZE size = new EnumMaster<BF_OBJECT_SIZE>().retrieveEnumConst(
    // BF_OBJECT_SIZE.class, t.getProperty(PROPS.BF_OBJECT_SIZE));
    // BF_OBJ_MATERIAL material = new
    // EnumMaster<BF_OBJ_MATERIAL>().retrieveEnumConst(
    // BF_OBJ_MATERIAL.class, t.getProperty(PROPS.BF_OBJ_MATERIAL));
    // OBJECT_ARMOR_TYPE armorType = new
    // EnumMaster<OBJECT_ARMOR_TYPE>().retrieveEnumConst(
    // OBJECT_ARMOR_TYPE.class, t.getProperty(PROPS.OBJECT_ARMOR_TYPE));
    //
    // if (armorType == null) {
    // switch (type) {
    //
    // }
    // }
    //
    // // TODO
    //
    // // stone/wood/metal/ ...
    // int i = 0;
    // List<String> list = getParamsFor(armorType);
    // for (PARAMS p : params) {
    // t.setParam(p, list.get(i));
    // i++;
    // }
    // list = getResistancesForArmorType(armorType);
    // for (PARAMETER p : ValuePages.RESISTANCES) {
    // t.setParam(p, list.get(i));
    // i++;
    // }
    //
    // }

    // TODO use mods from size? or multiply later...
    // private static String getParam(OBJECT_ARMOR_TYPE armorType, PARAMS p) {
    // switch (armorType) {
    // case AETHER:
    // return getAETHERParam(p);
    // case BONE:
    // return getBONEParam(p);
    // case CRYSTAL:
    // return getCRYSTALParam(p);
    // case ETHEREAL:
    // return getETHEREALParam(p);
    // case FLESH:
    // return getFLESHParam(p);
    // case METAL:
    // return getMETALParam(p);
    // case STONE:
    // return getSTONEParam(p);
    // case WOOD:
    // return getWOODParam(p);
    //
    // }
    //
    // return null;
    // }
    //
    // private static List<String> getResistancesForArmorType(OBJECT_ARMOR_TYPE
    // armorType) {
    // List<String> list = new LinkedList<>();
    // for (PARAMETER p : ValuePages.RESISTANCES) {
    // switch ((PARAMS) p) {
    // case PIERCING_RESISTANCE:
    // list.add(getPIERCING_RESISTANCE(armorType));
    // list.add(get(armorType));
    // break;
    // case BLUDGEONING_RESISTANCE:
    // list.add(getBLUDGEONING_RESISTANCE(armorType));
    // break;
    // case SLASHING_RESISTANCE:
    // list.add(getSLASHING_RESISTANCE(armorType));
    // break;
    // case POISON_RESISTANCE:
    // list.add(getPOISON_RESISTANCE(armorType));
    // break;
    // case FIRE_RESISTANCE:
    // list.add(getFIRE_RESISTANCE(armorType));
    // break;
    // case COLD_RESISTANCE:
    // list.add(getCOLD_RESISTANCE(armorType));
    // break;
    // case ACID_RESISTANCE:
    // list.add(getACID_RESISTANCE(armorType));
    // break;
    // case LIGHTNING_RESISTANCE:
    // list.add(getLIGHTNING_RESISTANCE(armorType));
    // break;
    // case SONIC_RESISTANCE:
    // list.add(getSONIC_RESISTANCE(armorType));
    // break;
    // case LIGHT_RESISTANCE:
    // list.add(getLIGHT_RESISTANCE(armorType));
    // break;
    // case CHAOS_RESISTANCE:
    // list.add(getCHAOS_RESISTANCE(armorType));
    // break;
    // case ARCANE_RESISTANCE:
    // list.add(getARCANE_RESISTANCE(armorType));
    // break;
    // case HOLY_RESISTANCE:
    // list.add(getHOLY_RESISTANCE(armorType));
    // break;
    // case SHADOW_RESISTANCE:
    // list.add(getSHADOW_RESISTANCE(armorType));
    // break;
    // case PSIONIC_RESISTANCE:
    // list.add(getPSIONIC_RESISTANCE(armorType));
    // break;
    // case DEATH_RESISTANCE:
    // list.add(getDEATH_RESISTANCE(armorType));
    // break;
    // }
    // }
    //
    // return list;
    // }
    //
    // private static List<String> getParamsFor(OBJECT_ARMOR_TYPE armorType) {
    // List<String> list = new LinkedList<>();
    // for (PARAMS p : params) {
    // list.add(getParam(armorType, p));
    // }
    //
    // return list;
    // }

    public static void initHeight(ObjType t) {
        OBJ_TYPES TYPE = (OBJ_TYPES) t.getOBJ_TYPE_ENUM();
        if (t.getIntParam(PARAMS.HEIGHT) != 0)
            return;
        int height = 0;
        switch (TYPE) {
            case BF_OBJ:

                BF_OBJECT_TYPE type = new EnumMaster<BF_OBJECT_TYPE>().retrieveEnumConst(
                        BF_OBJECT_TYPE.class, t.getProperty(G_PROPS.BF_OBJECT_TYPE));
                BF_OBJECT_GROUP group = new EnumMaster<BF_OBJECT_GROUP>().retrieveEnumConst(
                        BF_OBJECT_GROUP.class, t.getProperty(G_PROPS.BF_OBJECT_GROUP));
                if (type != null)
                    switch (type) {
                        // TODO

                    }
                if (group != null)
                    switch (group) {
                        case ROCKS:
                            height = 100;
                            break;
                        case TREES:
                            height = 1000;
                            break;
                        default:
                            break;

                    }
                break;

            case UNITS:
                List<CLASSIFICATIONS> c = new EnumMaster<CLASSIFICATIONS>().getEnumList(
                        CLASSIFICATIONS.class, t.getProperty(G_PROPS.CLASSIFICATIONS));

                if (c.contains(CLASSIFICATIONS.GIANT)) {
                    height = 600;

                } else if (c.contains(CLASSIFICATIONS.HUMANOID)) {
                    height = 180;

                } else if (c.contains(CLASSIFICATIONS.TALL)) {
                    height = 225;

                } else if (c.contains(CLASSIFICATIONS.SHORT)) {
                    height = 125;

                }

            case CHARS:
                RACE race = new EnumMaster<RACE>().retrieveEnumConst(RACE.class, t
                        .getProperty(G_PROPS.RACE));
                BACKGROUND bg = new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class, t
                        .getProperty(G_PROPS.BACKGROUND));
                GENDER g = new EnumMaster<GENDER>().retrieveEnumConst(GENDER.class, t
                        .getProperty(G_PROPS.GENDER));
                if (race != null)
                    switch (race) {
                        case DEMON:
                            height = 200;
                            break;
                        case DWARF:
                            height = 130;
                            break;
                        case ELF:
                            height = 190;
                            break;
                        case GOBLINOID:
                            height = 160;
                            break;
                        case HUMAN:
                            height = 180;
                            if (bg == BACKGROUND.MAN_OF_WOLF_REALM)
                                height += 10;
                            if (bg == BACKGROUND.MAN_OF_EAGLE_REALM)
                                height += 15;
                            break;
                        case VAMPIRE:
                            height = 180;
                            break;
                        default:
                            break;
                    }
                if (g == GENDER.FEMALE) {
                    height -= height / 5;
                }
                break;
        }
        t.setParam(PARAMS.HEIGHT, height);

        // per race?
        // per obj type

    }

}