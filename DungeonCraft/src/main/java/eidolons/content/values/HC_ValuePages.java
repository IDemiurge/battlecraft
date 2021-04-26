package eidolons.content.values;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.values.ValuePageManager;
import eidolons.content.values.ValuePages;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;

import java.util.List;

public class HC_ValuePages {
    // full info on characters, items, spells etc
    // description mode? on all hc panels?

    // WILL THERE BE THE ADDITIONAL PAGE WITH ALL MISSING VALUES?
    public static final VALUE[] DEITY_HEADER = {G_PROPS.ASPECT, G_PROPS.PRINCIPLES,
     PROPS.FAVORED_SPELL_GROUPS,
     // PROPS.ENEMY_DEITIES,
     // PROPS.PRIME_MISSION,
     // PROPS.TRUE_NAME,
     PROPS.PERKS};

    public static final VALUE[][] DEITY_PAGES = {DEITY_HEADER,
     // ValuePages.DESCRIPTION,
     ValuePages.LORE, ValuePages.PRINCIPLE_ALIGNMENTS, ValuePages.PRINCIPLE_IDENTITIES};

    public static final VALUE[] CHAR_HEADER = {G_PROPS.RACE, G_PROPS.ASPECT, G_PROPS.DEITY,
     G_PROPS.PRINCIPLES, G_PROPS.STANDARD_PASSIVES,};

    public static final VALUE[] CHAR_PARAMS_1 = {PARAMS.TOUGHNESS, PARAMS.ENDURANCE,
      PARAMS.ESSENCE, PARAMS.FOCUS,   PARAMS.INITIATIVE,
     PARAMS.EXTRA_ATTACKS,  PARAMS.STARTING_FOCUS,
     PARAMS.ARMOR_PENETRATION, PARAMS.RESISTANCE_PENETRATION,

    };
    public static final VALUE[] CHAR_PARAMS_2 = {PARAMS.CARRYING_CAPACITY,
     PARAMS.C_CARRYING_WEIGHT, PARAMS.SIGHT_RANGE, PARAMS.SIDE_SIGHT_PENALTY,
     PARAMS.BEHIND_SIGHT_BONUS, PARAMS.CONCEALMENT, PARAMS.DETECTION, PARAMS.STEALTH,
     PARAMS.ENDURANCE_REGEN, PARAMS.ESSENCE_REGEN, PARAMS.FOCUS_REGEN,};

    public static final VALUE[] CHAR_PARAMS_3 = {PARAMS.MIN_DAMAGE, PARAMS.MAX_DAMAGE,
     PARAMS.OFF_HAND_MIN_DAMAGE, PARAMS.OFF_HAND_MAX_DAMAGE, PARAMS.ARMOR_PENETRATION,

    };
    public static final VALUE[] CHAR_PARAMS_4 = {PARAMS.BASE_DAMAGE, PARAMS.ATTACK,
     PARAMS.DEFENSE, PARAMS.ATTACK_MOD, PARAMS.DEFENSE_MOD, PARAMS.ARMOR, PARAMS.SPIRIT,
     PARAMS.RESISTANCE, PARAMS.RESISTANCE_PENETRATION,   PARAMS.WEIGHT,
     PARAMS.QUICK_SLOTS,

    };
    public static final VALUE[][] BACKGROUND_CHAR_PAGES = ValuePages.BACKGROUND_VALUES;

    public static final VALUE[][] CHAR_PAGES = {CHAR_HEADER, CHAR_PARAMS_1, CHAR_PARAMS_2,
     CHAR_PARAMS_3, CHAR_PARAMS_4, ValuePages.PENALTIES, ValuePages.NATURAL_RESISTANCES,
     ValuePages.ASTRAL_RESISTANCES, ValuePages.ATTRIBUTES, ValuePages.PRINCIPLE_IDENTITIES,
     ValuePages.PRINCIPLE_ALIGNMENTS};

    public static final VALUE[] CLASS_HEADER = {

     G_PROPS.DESCRIPTION,
     //
     // G_PROPS.BASE_TYPE, G_PROPS.CLASS_GROUP, G_PROPS.CLASS_TYPE,
     PROPS.REQUIREMENTS, PROPS.ATTRIBUTE_BONUSES, PROPS.PARAMETER_BONUSES,
     G_PROPS.PRINCIPLES, G_PROPS.FLAVOR,
     // PASSIVES?
     // DESCRIPTION FORM?
     // "+5 STR, +6 AGI... "
    };

    public static final VALUE[] CLASS_PARAMS = {PARAMS.CIRCLE,

    };
    public static final VALUE[][] CLASS_PAGES = {CLASS_HEADER, ValuePages.ATTRIBUTES, CLASS_PARAMS};

    public static final DC_TYPE[] TYPES = {DC_TYPE.CHARS, DC_TYPE.CLASSES, DC_TYPE.ARMOR,
     DC_TYPE.WEAPONS,};
    public static final String[] PAGE_NAMES = {

    };

    public static final VALUE[] ARMOR_PARAMETERS = {PARAMS.ARMOR, PARAMS.DURABILITY,
     PARAMS.HARDNESS, PARAMS.COVER_PERCENTAGE, PARAMS.MATERIAL_QUANTITY,
     PARAMS.ARMOR_LAYERS,

     PARAMS.QUICK_SLOTS, PARAMS.WEIGHT, PARAMS.ATTACK_MOD, PARAMS.DEFENSE_MOD,

     PARAMS.STEALTH, PARAMS.DETECTION,

    };

    public static final VALUE[] ARMOR_PARAMETERS_2 = {PARAMS.STEALTH, PARAMS.DETECTION,
     PARAMS.SIGHT_RANGE, PARAMS.SIDE_SIGHT_PENALTY,};

    public static final VALUE[] WEAPON_PARAMETERS = {

     PARAMS.DAMAGE_BONUS,
     PARAMS.DICE, PARAMS.DIE_SIZE,
     PARAMS.ATTACK_MOD, PARAMS.DEFENSE_MOD,
     PARAMS.STR_DMG_MODIFIER, PARAMS.AGI_DMG_MODIFIER,
     PARAMS.WEIGHT, PARAMS.DURABILITY,

    };
    public static final VALUE[] WEAPON_PARAMETERS_2 = {PARAMS.SP_DMG_MODIFIER,
     PARAMS.INT_DMG_MODIFIER, PARAMS.BASE_DAMAGE_MODIFIER, PARAMS.DURABILITY_MODIFIER,
       PARAMS.ATTACK_MOD, PARAMS.DAMAGE_MOD, PARAMS.DEFENSE_MOD,

    };

    public static final VALUE[] ARMOR_PROPERTIES = {G_PROPS.ARMOR_TYPE, G_PROPS.ARMOR_GROUP,
     G_PROPS.MATERIAL, G_PROPS.QUALITY_LEVEL,};

    public static final VALUE[] WEAPON_PROPERTIES = {G_PROPS.WEAPON_TYPE, G_PROPS.WEAPON_CLASS,
     PROPS.DAMAGE_TYPE, G_PROPS.WEAPON_SIZE, G_PROPS.STANDARD_PASSIVES,

    };
    public static final VALUE[] PARTY_PARAMETERS = {PARAMS.GLORY, PARAMS.LEVEL,
     // stats?
    };

    public static final VALUE[][] SKILL_PAGES = {
     new VALUE[]{PROPS.SKILL_REQUIREMENTS, G_PROPS.BASE_TYPE, PROPS.ATTRIBUTE_BONUSES,
      PROPS.PARAMETER_BONUSES, G_PROPS.DESCRIPTION,},

     ValuePages.ATTRIBUTES, ValuePages.SKILL_PARAMETERS,

    };

    public static final VALUE[] PARTY_PROPERTIES = {PROPS.MEMBERS,

    };
    public static final VALUE[][] PARTY_PAGES = {PARTY_PROPERTIES, PARTY_PARAMETERS,};

    public static final VALUE[][] ARMOR_PAGES = {ARMOR_PARAMETERS, ValuePages.DESCRIPTION,
     ARMOR_PROPERTIES, ValuePages.PENALTIES, ValuePages.NATURAL_RESISTANCES,
     ValuePages.ASTRAL_RESISTANCES

    };

    public static final VALUE[][] WEAPONS_PAGES = {WEAPON_PARAMETERS, ValuePages.DESCRIPTION,
     WEAPON_PROPERTIES, ValuePages.PENALTIES,
     // WEAPON_PARAMETERS_2,
    };

    public static final VALUE[][] ACTION_PAGES = {ValuePages.ACTION_PARAMS_DC,
     ValuePages.ACTION_PARAMS_DC2, ValuePages.DESCRIPTION, ValuePages.COSTS,
     ValuePages.ACTION_PROPS_DC,};

    public static List<List<VALUE>> getPageLists(DC_TYPE TYPE) {
        VALUE[][] pages = null;
        switch (TYPE) {
            case CHARS:
                pages = CHAR_PAGES;
                break;
            case PARTY:
                pages = PARTY_PAGES;
                break;
        }
        return ValuePageManager.getValueLists(pages);
    }

}
