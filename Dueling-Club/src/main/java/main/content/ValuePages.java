package main.content;

import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;

import java.lang.reflect.Field;

import static main.content.PARAMS.*;

public class ValuePages {
    public static final VALUE[] DESCRIPTION = {G_PROPS.DESCRIPTION};
    public static final VALUE[] LORE = {G_PROPS.LORE};
    public static final VALUE[] GENERIC_AV_HEADER = {G_PROPS.NAME, G_PROPS.IMAGE, G_PROPS.GROUP,

            G_PROPS.WORKSPACE_GROUP, G_PROPS.DEV_NOTES, G_PROPS.UNIT_GROUP, G_PROPS.ASPECT, G_PROPS.DEITY,
            G_PROPS.PRINCIPLES, G_PROPS.ACTIVES, G_PROPS.PASSIVES, G_PROPS.STANDARD_PASSIVES,
            LEVEL, POWER, FORMULA, G_PROPS.DESCRIPTION, G_PROPS.LORE,
            G_PROPS.FLAVOR,

    }; // POWER?
    public static final VALUE[] CHARS_HEADER = {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.BACKGROUND,
            G_PROPS.RACE, G_PROPS.MODE, G_PROPS.STATUS, G_PROPS.STANDARD_PASSIVES,};
    public static final VALUE[] DC_SPELLS_HEADER = {G_PROPS.ASPECT, G_PROPS.SPELL_TYPE,
            G_PROPS.SPELL_GROUP, G_PROPS.TARGETING_MODE, G_PROPS.SPELL_TAGS,};
    public static final VALUE[] SPELLS_UPGRADES = {PROPS.SPELL_UPGRADES,};
    public static final VALUE[] UNITS_HEADER = {G_PROPS.ASPECT, G_PROPS.DEITY,
            // G_PROPS.GROUP,
            G_PROPS.CLASSIFICATIONS, G_PROPS.MODE, G_PROPS.STATUS, G_PROPS.STANDARD_PASSIVES,};
    public static final VALUE[] OUTLINE_VALUES = {
            // G_PROPS.DISPLAYED_NAME,
            // PROPS.VISIBILITY_STATUS,
            PROPS.HINTS, PROPS.LAST_SEEN,
            // OUTLINE_DESCRIPTION

    };
    public static final VALUE[] BF_OBJ_HEADER = {G_PROPS.BF_OBJECT_TYPE, // magical,
            // natural,
            // structure,
            // trap,
            // container,
            G_PROPS.BF_OBJECT_GROUP, // conjuration, plant,
            G_PROPS.BF_OBJECT_TAGS, // destructible, passible, summoned
            G_PROPS.STATUS, // can also be set ablaze!
            G_PROPS.STANDARD_PASSIVES, // non-obstructing, transparent,
    };
    public static final VALUE[] GENERIC_DC_HEADER = {
            // G_PROPS.ASPECT,
            // PARAMS.LEVEL,
            // G_PROPS.PRINCIPLES,
            // G_PROPS.DESCRIPTION,
            // G_PROPS.PASSIVES,
            // G_PROPS.ACTIVES,
            // PARAMS.FORMULA,

            // G_PROPS.PASSIVES,
            // PROPS.SKILLS,
            // G_PROPS.STANDARD_PASSIVES,
            // PROPS.VISIBILITY_STATUS,
            // PROPS.DETECTION_STATUS,
            // PROPS.FACING_DIRECTION,
    };
    public static final VALUE[] DC_TRAILING_PAGE = {G_PROPS.DESCRIPTION, G_PROPS.LORE,

    };
    public static final VALUE[] AV_TRAILING_PAGE = {G_PROPS.DESCRIPTION, G_PROPS.LORE,

    };
    public static final PARAMETER[] UNIT_PARAMETERS = {
            // ACTIONS
            TOUGHNESS, ENDURANCE, STAMINA, ESSENCE, FOCUS,
            MORALE,

            DAMAGE, OFF_HAND_DAMAGE,

            ARMOR, ATTACK, DEFENSE, N_OF_ACTIONS, N_OF_COUNTERS,
            STARTING_FOCUS, SPIRIT, RESISTANCE, SPELL_ARMOR,
            INITIATIVE_MODIFIER, INITIATIVE_BONUS, C_INITIATIVE_BONUS,
            INITIATIVE, C_INITIATIVE,

            BASE_DAMAGE,

            MIN_DAMAGE, MAX_DAMAGE, OFF_HAND_MIN_DAMAGE,
            OFF_HAND_MAX_DAMAGE,

            WEIGHT, CARRYING_CAPACITY, C_CARRYING_WEIGHT, SIGHT_RANGE,
            SIDE_SIGHT_PENALTY, BEHIND_SIGHT_BONUS, STAMINA_REGEN,
            ENDURANCE_REGEN, ESSENCE_REGEN, FOCUS_REGEN,
            ARMOR_PENETRATION, ARMOR_MOD, CONCEALMENT, DETECTION,
            STEALTH,
            UNIT_LEVEL, //
            QUICK_SLOTS, // HEROLT
            STAMINA_PENALTY, ESSENCE_PENALTY, AP_PENALTY,
            FOCUS_PENALTY,};
    public static final VALUE[] UNIT_DYNAMIC_PARAMETERS = {C_N_OF_ACTIONS,
            C_INITIATIVE, C_ENDURANCE, C_TOUGHNESS, C_STAMINA,
            C_FOCUS, C_ESSENCE, C_MORALE, C_INITIATIVE_BONUS,
            C_CARRYING_WEIGHT,

    };
    // INFO LEVELS
    public static final VALUE[] UNIT_PROPERTIES = {G_PROPS.MAIN_HAND_ITEM, G_PROPS.OFF_HAND_ITEM,
            G_PROPS.ARMOR_ITEM, PROPS.INVENTORY, PROPS.QUICK_ITEMS, PROPS.JEWELRY,
            // PARAMS.QUICK_SLOTS_REMAINING,
            PROPS.SKILLS, PROPS.CLASSES, PROPS.FIRST_CLASS, PROPS.SECOND_CLASS,

            PROPS.KNOWN_SPELLS, PROPS.LEARNED_SPELLS, PROPS.MEMORIZED_SPELLS,
            PROPS.VERBATIM_SPELLS,

    };
    public static final PARAMETER[] UPKEEP_PARAMETERS = {ESS_UPKEEP, AP_UPKEEP,
            END_UPKEEP, FOC_UPKEEP, STA_UPKEEP,};
    public static final VALUE[] ATTRIBUTES_VAL = {STRENGTH, VITALITY,
            AGILITY, DEXTERITY, WILLPOWER, INTELLIGENCE, WISDOM,
            KNOWLEDGE, SPELLPOWER, CHARISMA,};
    public static final PARAMETER[] ATTRIBUTES = {STRENGTH, VITALITY,
            AGILITY, DEXTERITY, WILLPOWER, INTELLIGENCE, WISDOM,
            KNOWLEDGE, SPELLPOWER, CHARISMA,};
    public static final PARAMETER[] DEFAULT_ATTRIBUTES = {DEFAULT_STRENGTH,
            DEFAULT_VITALITY, DEFAULT_AGILITY, DEFAULT_DEXTERITY,
            DEFAULT_WILLPOWER, DEFAULT_INTELLIGENCE, DEFAULT_WISDOM,
            DEFAULT_KNOWLEDGE, DEFAULT_SPELLPOWER, DEFAULT_CHARISMA,};
    public static final PARAMETER[] BASE_ATTRIBUTES = {BASE_STRENGTH, BASE_VITALITY,
            BASE_AGILITY, BASE_DEXTERITY, BASE_WILLPOWER,
            BASE_INTELLIGENCE, BASE_WISDOM, BASE_KNOWLEDGE,
            BASE_SPELLPOWER, BASE_CHARISMA,};
    public static final PARAMETER[] MASTERIES_MAGIC = {WIZARDRY_MASTERY,
            SPELLCRAFT_MASTERY, DIVINATION_MASTERY, WARCRY_MASTERY,

            SORCERY_MASTERY,
            AIR_MASTERY,
            WATER_MASTERY,
            EARTH_MASTERY,
            FIRE_MASTERY,
            CONJURATION_MASTERY,
            SYLVAN_MASTERY, ENCHANTMENT_MASTERY, SAVAGE_MASTERY,
            DESTRUCTION_MASTERY, CELESTIAL_MASTERY, DEMONOLOGY_MASTERY,
            BENEDICTION_MASTERY, WARP_MASTERY, REDEMPTION_MASTERY,
            SHADOW_MASTERY, BLOOD_MAGIC_MASTERY, WITCHERY_MASTERY,
            AFFLICTION_MASTERY, PSYCHIC_MASTERY, NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_MAGIC_DEFAULT_SORTED = {WIZARDRY_MASTERY,
            SPELLCRAFT_MASTERY, DIVINATION_MASTERY, WARCRY_MASTERY,

            SORCERY_MASTERY, CONJURATION_MASTERY, ENCHANTMENT_MASTERY,
            ELEMENTAL_MASTERY, SYLVAN_MASTERY, SAVAGE_MASTERY,
            DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY, WARP_MASTERY,
            CELESTIAL_MASTERY, BENEDICTION_MASTERY, REDEMPTION_MASTERY,
            SHADOW_MASTERY, WITCHERY_MASTERY, PSYCHIC_MASTERY,
            BLOOD_MAGIC_MASTERY, AFFLICTION_MASTERY, NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_SKILL_DISPLAY = {

            WIZARDRY_MASTERY, SPELLCRAFT_MASTERY, DIVINATION_MASTERY,
            WARCRY_MASTERY, ATHLETICS_MASTERY, MOBILITY_MASTERY,
            DISCIPLINE_MASTERY, MEDITATION_MASTERY, DEFENSE_MASTERY,
            ARMORER_MASTERY, SHIELD_MASTERY, TACTICS_MASTERY,
            LEADERSHIP_MASTERY, ITEM_MASTERY, DETECTION_MASTERY,
            STEALTH_MASTERY,

            BLADE_MASTERY, BLUNT_MASTERY, AXE_MASTERY, POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY, MARKSMANSHIP_MASTERY,
            UNARMED_MASTERY,

            SORCERY_MASTERY, ELEMENTAL_MASTERY, CONJURATION_MASTERY,
            SYLVAN_MASTERY, ENCHANTMENT_MASTERY, SAVAGE_MASTERY,
            DESTRUCTION_MASTERY, CELESTIAL_MASTERY, DEMONOLOGY_MASTERY,
            BENEDICTION_MASTERY, WARP_MASTERY, REDEMPTION_MASTERY,
            SHADOW_MASTERY, BLOOD_MAGIC_MASTERY, WITCHERY_MASTERY,
            AFFLICTION_MASTERY, PSYCHIC_MASTERY, NECROMANCY_MASTERY,

    };
    public static final PARAMETER[] MASTERIES_MAGIC_DISPLAY = {SORCERY_MASTERY,
            ELEMENTAL_MASTERY, CONJURATION_MASTERY, SYLVAN_MASTERY,
            ENCHANTMENT_MASTERY, SAVAGE_MASTERY, DESTRUCTION_MASTERY,
            CELESTIAL_MASTERY, DEMONOLOGY_MASTERY, BENEDICTION_MASTERY,
            WARP_MASTERY, REDEMPTION_MASTERY, SHADOW_MASTERY,
            BLOOD_MAGIC_MASTERY, WITCHERY_MASTERY, AFFLICTION_MASTERY,
            PSYCHIC_MASTERY, NECROMANCY_MASTERY,};
    public static final PARAMETER[] MASTERIES_WEAPONS_DISPLAY = {BLADE_MASTERY,
            BLUNT_MASTERY, AXE_MASTERY, POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY, MARKSMANSHIP_MASTERY,
            UNARMED_MASTERY,};
    public static final PARAMETER[] MASTERIES_COMBAT_DISPLAY = {DEFENSE_MASTERY,
            ARMORER_MASTERY, WARCRY_MASTERY, TACTICS_MASTERY,
            LEADERSHIP_MASTERY,

            SHIELD_MASTERY, ATHLETICS_MASTERY, MOBILITY_MASTERY,};
    public static final PARAMETER[] MASTERIES_MISC_DISPLAY = {

            ITEM_MASTERY,

            DISCIPLINE_MASTERY, MEDITATION_MASTERY,

            DETECTION_MASTERY, STEALTH_MASTERY,

            WIZARDRY_MASTERY, SPELLCRAFT_MASTERY, DIVINATION_MASTERY,};
    public static final PARAMETER[] MASTERIES_COMBAT = {BLADE_MASTERY,
            BLUNT_MASTERY, AXE_MASTERY, POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY, TWO_HANDED_MASTERY, MARKSMANSHIP_MASTERY,
            UNARMED_MASTERY, DEFENSE_MASTERY, ARMORER_MASTERY,
            WARCRY_MASTERY,

            SHIELD_MASTERY,};
    public static final PARAMETER[] MASTERIES_MISC = {ATHLETICS_MASTERY,
            MOBILITY_MASTERY, ARMORER_MASTERY, ITEM_MASTERY,
            TACTICS_MASTERY, LEADERSHIP_MASTERY,

            DISCIPLINE_MASTERY,

            MEDITATION_MASTERY, DETECTION_MASTERY, STEALTH_MASTERY,
            // PARAMS.ENCHANTER_MASTERY,
            // PARAMS.JEWELER_MASTERY,
    };
    public static final PARAMETER[] MASTERIES = {
            ATHLETICS_MASTERY,
            MOBILITY_MASTERY,
            BLADE_MASTERY,
            BLUNT_MASTERY,
            AXE_MASTERY,
            POLEARM_MASTERY,
            DUAL_WIELDING_MASTERY,
            TWO_HANDED_MASTERY,
            UNARMED_MASTERY,
            MARKSMANSHIP_MASTERY,

            SHIELD_MASTERY,
            DEFENSE_MASTERY,
            ARMORER_MASTERY,
            ITEM_MASTERY,
            DISCIPLINE_MASTERY,
            // PARAMS.MARKSMANSHIP_MASTERY,
            // PARAMS.SHIELD_MASTERY,
            STEALTH_MASTERY, DETECTION_MASTERY,

            TACTICS_MASTERY, LEADERSHIP_MASTERY,

            WARCRY_MASTERY,
            DIVINATION_MASTERY,

            MEDITATION_MASTERY, WIZARDRY_MASTERY,
            SPELLCRAFT_MASTERY,
            SORCERY_MASTERY, CONJURATION_MASTERY,
            ENCHANTMENT_MASTERY,
            AIR_MASTERY,
            WATER_MASTERY,
            EARTH_MASTERY,
            FIRE_MASTERY,
            SYLVAN_MASTERY,
            SAVAGE_MASTERY,
            DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY,
            WARP_MASTERY,
            CELESTIAL_MASTERY, BENEDICTION_MASTERY,
            REDEMPTION_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY,
            PSYCHIC_MASTERY, BLOOD_MAGIC_MASTERY, AFFLICTION_MASTERY,
            NECROMANCY_MASTERY,


    };
    // public static final VALUE[] MASTERIES_2 = {
    //
    // };
    public static final PARAMETER[] NATURAL_RESISTANCES = {SLASHING_RESISTANCE,
            PIERCING_RESISTANCE, BLUDGEONING_RESISTANCE, POISON_RESISTANCE,
            FIRE_RESISTANCE, COLD_RESISTANCE, ACID_RESISTANCE,
            LIGHTNING_RESISTANCE, SONIC_RESISTANCE, LIGHT_RESISTANCE,};

    public static final PARAMETER[] ELEMENTAL_RESISTANCES = {FIRE_RESISTANCE,
            COLD_RESISTANCE, ACID_RESISTANCE, LIGHTNING_RESISTANCE,
            SONIC_RESISTANCE, LIGHT_RESISTANCE,};
    public static final PARAMETER[] ASTRAL_RESISTANCES = {ARCANE_RESISTANCE,
            CHAOS_RESISTANCE, HOLY_RESISTANCE, SHADOW_RESISTANCE,
            PSIONIC_RESISTANCE, DEATH_RESISTANCE,};

    public static final PARAMETER[] MAGIC_RESISTANCES = {DEATH_RESISTANCE,
            FIRE_RESISTANCE, WATER_RESISTANCE, AIR_RESISTANCE,
            EARTH_RESISTANCE, CHAOS_RESISTANCE, ARCANE_RESISTANCE,
            HOLY_RESISTANCE, SHADOW_RESISTANCE, POISON_RESISTANCE,};

    public static final PARAMETER[] PHYSICAL_RESISTANCES = {PIERCING_RESISTANCE,
            BLUDGEONING_RESISTANCE, SLASHING_RESISTANCE,};

    public static final PARAMETER[] RESISTANCES = {
            PIERCING_RESISTANCE,
            BLUDGEONING_RESISTANCE, SLASHING_RESISTANCE, POISON_RESISTANCE,
            FIRE_RESISTANCE, COLD_RESISTANCE, ACID_RESISTANCE,
            LIGHTNING_RESISTANCE, SONIC_RESISTANCE, LIGHT_RESISTANCE,

            CHAOS_RESISTANCE, ARCANE_RESISTANCE, HOLY_RESISTANCE,
            SHADOW_RESISTANCE, PSIONIC_RESISTANCE, DEATH_RESISTANCE,

            // PARAMS.WATER_RESISTANCE,
            // PARAMS.AIR_RESISTANCE,
            // PARAMS.EARTH_RESISTANCE,
    };

    public static final PARAMETER[] ARMOR_VS_DAMAGE_TYPES = {

            PARAMS.FIRE_ARMOR, PARAMS.COLD_ARMOR, PARAMS.ACID_ARMOR,
            PARAMS.LIGHTNING_ARMOR, PARAMS.SONIC_ARMOR, PARAMS.LIGHT_ARMOR,

            PARAMS.CHAOS_ARMOR, PARAMS.ARCANE_ARMOR, PARAMS.HOLY_ARMOR,
            PARAMS.SHADOW_ARMOR, PARAMS.PSIONIC_ARMOR, PARAMS.DEATH_ARMOR,

            PARAMS.PIERCING_ARMOR,
            PARAMS.BLUDGEONING_ARMOR,
            PARAMS.SLASHING_ARMOR,

    };

    public static final PARAMETER[] DURABILITY_VS_DAMAGE_TYPES = {

            PARAMS.FIRE_DURABILITY_MOD, PARAMS.COLD_DURABILITY_MOD, PARAMS.ACID_DURABILITY_MOD,
            PARAMS.LIGHTNING_DURABILITY_MOD, PARAMS.SONIC_DURABILITY_MOD, PARAMS.LIGHT_DURABILITY_MOD,

            PARAMS.CHAOS_DURABILITY_MOD, PARAMS.ARCANE_DURABILITY_MOD, PARAMS.HOLY_DURABILITY_MOD,
            PARAMS.SHADOW_DURABILITY_MOD, PARAMS.PSIONIC_DURABILITY_MOD, PARAMS.DEATH_DURABILITY_MOD,

            PARAMS.PIERCING_DURABILITY_MOD,
            PARAMS.BLUDGEONING_DURABILITY_MOD,
            PARAMS.SLASHING_DURABILITY_MOD,

    };

    public static final PARAMETER[] PRINCIPLE_ALIGNMENTS = {WAR_ALIGNMENT,
            PEACE_ALIGNMENT, HONOR_ALIGNMENT, TREACHERY_ALIGNMENT,
            LAW_ALIGNMENT, FREEDOM_ALIGNMENT, CHARITY_ALIGNMENT,
            AMBITION_ALIGNMENT, TRADITION_ALIGNMENT, PROGRESS_ALIGNMENT,

    };
    public static final PARAMETER[] IDENTITY_PARAMS = {IDENTITY_POINTS_PER_LEVEL,
            STARTING_IDENTITY_POINTS,};
    public static final PARAMETER[] PRINCIPLE_IDENTITIES = {WAR_IDENTITY,
            PEACE_IDENTITY, HONOR_IDENTITY, TREACHERY_IDENTITY,
            LAW_IDENTITY, FREEDOM_IDENTITY, CHARITY_IDENTITY,
            AMBITION_IDENTITY, TRADITION_IDENTITY, PROGRESS_IDENTITY};
    public static final VALUE[] ADDITIONAL = {HERO_LEVEL, TOTAL_XP, XP,
            GOLD, FREE_MASTERIES, ATTR_POINTS, MASTERY_POINTS,

            XP_COST_REDUCTION,

            GOLD_MOD, XP_GAIN_MOD,

    }; // everything
    public static final VALUE[] UNIT_LEVEL_PARAMETERS = {PROPS.ATTRIBUTE_PROGRESSION,
            PROPS.MASTERY_PROGRESSION, PROPS.XP_PLAN, PROPS.SPELL_PLAN, PROPS.MAIN_HAND_REPERTOIRE,
            PROPS.OFF_HAND_REPERTOIRE, PROPS.ARMOR_REPERTOIRE, PROPS.JEWELRY_ITEM_TRAIT_REPERTOIRE,
            PROPS.QUICK_ITEM_REPERTOIRE, PROPS.JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE,
            PROPS.QUALITY_LEVEL_RANGE, PROPS.ALLOWED_MATERIAL, JEWELRY_GOLD_PERCENTAGE,
            QUICK_ITEM_GOLD_PERCENTAGE, ARMOR_GOLD_PERCENTAGE,
            MAIN_HAND_GOLD_PERCENTAGE, PROPS.MEMORIZATION_PRIORITY, PROPS.VERBATIM_PRIORITY,};
    public static final VALUE[] LEVEL_PARAMETERS = {HERO_LEVEL, TOTAL_XP, XP,
            GOLD, FREE_MASTERIES, MASTERY_POINTS, ATTR_POINTS,
            ATTR_POINTS_PER_LEVEL, MASTERY_POINTS_PER_LEVEL,
            XP_COST_REDUCTION,

            GOLD_MOD, XP_GAIN_MOD, XP_LEVEL_MOD,
            IDENTITY_POINTS_PER_LEVEL, STARTING_IDENTITY_POINTS,

            // PARAMS.STRENGTH_PER_LEVEL,
            // PARAMS.VITALITY_PER_LEVEL,
            // PARAMS.AGILITY_PER_LEVEL,
            // PARAMS.DEXTERITY_PER_LEVEL,
            // PARAMS.WILLPOWER_PER_LEVEL,
            // PARAMS.INTELLIGENCE_PER_LEVEL,
            // PARAMS.WISDOM_PER_LEVEL,
            // PARAMS.KNOWLEDGE_PER_LEVEL,
            // PARAMS.SPELLPOWER_PER_LEVEL,
            // PARAMS.CHARISMA_PER_LEVEL,

    };
    public static final PARAMETER[] SNEAK_MODS = {SNEAK_DAMAGE_MOD,
            SNEAK_ATTACK_MOD, SNEAK_DEFENSE_MOD, SNEAK_ARMOR_MOD,
            // PARAMS.SNEAK_DAMAGE_BONUS,
            // PARAMS.SNEAK_ATTACK_BONUS, PARAMS.SNEAK_DEFENSE_PENETRATION,
            // PARAMS.SNEAK_ARMOR_PENETRATION

    };
    // else?

    //
    public static final VALUE[] ACTION_PROPS = {PROPS.DAMAGE_TYPE, G_PROPS.ACTION_TYPE,
            G_PROPS.ACTION_TAGS, PROPS.HOTKEY, PROPS.ALT_HOTKEY, PROPS.ACTION_MODES,
            PROPS.ROLL_TYPES_TO_SAVE, PROPS.TARGETING_MODIFIERS, G_PROPS.TARGETING_MODE,
            PROPS.EFFECTS_WRAP, G_PROPS.CUSTOM_SOUNDSET,};
    public static final VALUE[] ACTION_PROPS2 = {PROPS.RESISTANCE_MODIFIERS,
            PROPS.RESISTANCE_TYPE, PROPS.ON_ACTIVATE, PROPS.ON_HIT, PROPS.ON_KILL, PROPS.AI_LOGIC,
            PROPS.AI_PRIORITY_FORMULA,};
    public static final VALUE[] ACTION_PROPS_DC = {G_PROPS.ACTION_TYPE, G_PROPS.ACTION_TAGS,
            PROPS.DAMAGE_TYPE, PROPS.HOTKEY};
    public static final VALUE[] ACTION_PARAMS_DC = {ATTACK_MOD, DEFENSE_MOD,
            DAMAGE_MOD, DAMAGE_BONUS, ARMOR_PENETRATION, ARMOR_MOD,
            BLEEDING_MOD, COUNTER_MOD, FORCE_MOD, FORCE_DAMAGE_MOD,
            RANGE, COOLDOWN,

    };
    public static final VALUE[] ACTION_PARAMS_DC2 = {STR_DMG_MODIFIER,
            AGI_DMG_MODIFIER, INT_DMG_MODIFIER, SP_DMG_MODIFIER,
            CRITICAL_MOD, IMPACT_AREA, G_PARAMS.RADIUS, AUTO_ATTACK_RANGE,
            SIDE_DAMAGE_MOD, DIAGONAL_DAMAGE_MOD, SIDE_ATTACK_MOD,
            DIAGONAL_ATTACK_MOD
            // G_PARAMS.DURATION

    };
    // attack/defense/damage/str/agi... mods, costs, damage type,
    // bleeding modifiers, bash on focus/initiative/stamina,
    // plus some roll effects like knockdown (mass, strength, reflex...).
    public static final VALUE[] ACTION_ATTACK_PARAMS = {ATTACK_MOD, DEFENSE_MOD,
            DAMAGE_MOD, DAMAGE_BONUS, ARMOR_PENETRATION, ARMOR_MOD,
            BLEEDING_MOD, COUNTER_MOD, FORCE_MOD, FORCE_DAMAGE_MOD,
            FORCE_MAX_STRENGTH_MOD, FORCE_MOD_SOURCE_WEIGHT, STR_DMG_MODIFIER,
            AGI_DMG_MODIFIER, INT_DMG_MODIFIER, SP_DMG_MODIFIER,
            DURABILITY_DAMAGE_MOD, CRITICAL_MOD, ACCURACY, IMPACT_AREA,
            AUTO_ATTACK_RANGE, SIDE_DAMAGE_MOD, DIAGONAL_DAMAGE_MOD,
            SIDE_ATTACK_MOD, DIAGONAL_ATTACK_MOD

            , CLOSE_QUARTERS_ATTACK_MOD, CLOSE_QUARTERS_DAMAGE_MOD,
            LONG_REACH_ATTACK_MOD, LONG_REACH_DAMAGE_MOD,

    };
    public static final VALUE[] ACTION_PARAMS = {G_PARAMS.RADIUS, RANGE, COOLDOWN,
            G_PARAMS.DURATION};
    public static final VALUE[] SPELL_PARAMETERS = {CIRCLE, SPELL_DIFFICULTY,
            G_PARAMS.DURATION, G_PARAMS.RADIUS, RANGE, COOLDOWN,
            SPELLPOWER_MOD, FORCE, FORCE_SPELLPOWER_MOD,
            FORCE_DAMAGE_MOD, FORCE_KNOCK_MOD, FORCE_PUSH_MOD, XP_COST,

    };
    public static final VALUE[] QUICK_ITEM_PARAMETERS = {CHARGES, COOLDOWN,
            AP_COST, STA_COST, ESS_COST, FOC_COST, FOC_REQ,
            ENDURANCE_COST, G_PARAMS.DURATION, RANGE, G_PARAMS.RADIUS,
            // PARAMS.SPELLPOWER_BONUS,
            // PARAMS.SPELLPOWER_MOD,
    };
    public static final VALUE[] ITEM_PARAMETERS = {WEIGHT, GOLD_COST,

    };
    public static final VALUE[] ITEM_PROPERTIES = {PROPS.ITEM_SPELL, G_PROPS.ITEM_TYPE,
            G_PROPS.ITEM_GROUP};
    public static final VALUE[] QUICK_ITEM_PROPERTIES = {

            G_PROPS.TARGETING_MODE, PROPS.EFFECTS_WRAP, PROPS.TARGETING_MODIFIERS, PROPS.DAMAGE_TYPE,
            G_PROPS.SOUNDSET, G_PROPS.CUSTOM_SOUNDSET, G_PROPS.IMPACT_SPRITE,};
    public static final VALUE[] DC_SPELL_PROPERTIES = {G_PROPS.SPELL_TYPE, G_PROPS.SPELL_GROUP,
            G_PROPS.SPELL_TAGS, PROPS.DAMAGE_TYPE,};
    public static final VALUE[] SPELL_PROPERTIES = {G_PROPS.SPELL_GROUP, G_PROPS.SPELL_TYPE,
            G_PROPS.SPELL_TAGS,

            G_PROPS.TARGETING_MODE, PROPS.EFFECTS_WRAP, PROPS.RETAIN_CONDITIONS,
            PROPS.TARGETING_MODIFIERS, PROPS.DAMAGE_TYPE, G_PROPS.SPELL_UPGRADE_GROUPS,

            G_PROPS.SOUNDSET, G_PROPS.CUSTOM_SOUNDSET, G_PROPS.IMPACT_SPRITE};
    public static final PARAMETER[] COSTS = {AP_COST, STA_COST, ESS_COST,
            FOC_COST, FOC_REQ, ENDURANCE_COST, CP_COST,

    };
    public static final VALUE[] UPKEEP_PARAMS = {AP_UPKEEP, STA_UPKEEP,
            ESS_UPKEEP, FOC_UPKEEP, END_UPKEEP,

    };
    public static final VALUE[] SKILL_PARAMETERS = {CIRCLE, SKILL_DIFFICULTY,
            XP_COST, RANK_MAX, RANK_SD_MOD, RANK_FORMULA_MOD,
            RANK_XP_MOD, RANK_REQUIREMENT,

    };
    public static final VALUE[] SKILL_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.SKILL_GROUP,
            G_PROPS.BASE_TYPE,

    };
    public static final VALUE[] SKILL_ADDITIONAL = {

            PROPS.ATTRIBUTE_BONUSES, PROPS.PARAMETER_BONUSES, RANK_MAX, RANK_FORMULA_MOD,
            RANK_XP_MOD, RANK_REQUIREMENT,

            PROPS.LINK_VARIANT, PROPS.TREE_NODE_GROUP, PROPS.TREE_NODE_PRIORITY,
            TREE_LINK_OFFSET_X, TREE_LINK_OFFSET_Y,

            TREE_NODE_OFFSET_X, TREE_NODE_OFFSET_Y,

            HT_CUSTOM_POS_X, HT_CUSTOM_POS_Y,

    };
    public static final VALUE[] CLASS_HEADER2 = {PROPS.LINK_VARIANT, PROPS.TREE_NODE_GROUP,
            PROPS.TREE_NODE_PRIORITY, TREE_LINK_OFFSET_X, TREE_LINK_OFFSET_Y,

            TREE_NODE_OFFSET_X, TREE_NODE_OFFSET_Y,};
    public static final VALUE[] CLASS_HEADER = {G_PROPS.ACTIVES, PROPS.SKILL_OR_REQUIREMENTS,
            PROPS.SKILL_REQUIREMENTS, PROPS.REQUIREMENTS,

            G_PROPS.CLASS_TYPE, G_PROPS.CLASS_GROUP,

            G_PROPS.BASE_TYPE, PROPS.ALT_BASE_LINKS, PROPS.ALT_BASE_TYPES, PROPS.ATTRIBUTE_BONUSES,
            PROPS.PARAMETER_BONUSES, CIRCLE, XP_COST,

            RANK_MAX, RANK_FORMULA_MOD, RANK_XP_MOD, RANK_REQUIREMENT,
            PROPS.BASE_CLASSES_ONE, PROPS.BASE_CLASSES_TWO,

    };
    public static final VALUE[] REQUIREMENTS = {PROPS.SKILL_REQUIREMENTS,
            PROPS.SKILL_OR_REQUIREMENTS, PROPS.CLASSES, PROPS.KNOWN_SPELLS, PROPS.VERBATIM_SPELLS,
            PROPS.REQUIREMENTS,

    };
    public static final PARAMETER[] PENALTIES_MAIN = {
            STAMINA_PENALTY, AP_PENALTY,
            FOCUS_PENALTY, ESSENCE_PENALTY,
    };
    public static final PARAMETER[] PENALTIES_MOVE = {
            MOVE_STA_PENALTY,
            MOVE_AP_PENALTY,
    };
    public static final PARAMETER[] PENALTIES_ATK = {
            ATTACK_STA_PENALTY,
            ATTACK_AP_PENALTY,
    };
    public static final PARAMETER[] PENALTIES_SPELL = {
            SPELL_STA_PENALTY, SPELL_ESS_PENALTY,
            SPELL_FOC_PENALTY, SPELL_AP_PENALTY,
    };
    public static final PARAMETER[] PENALTIES = {STAMINA_PENALTY, AP_PENALTY,
            FOCUS_PENALTY, ESSENCE_PENALTY, ATTACK_STA_PENALTY,
            ATTACK_AP_PENALTY, SPELL_STA_PENALTY, SPELL_ESS_PENALTY,
            SPELL_FOC_PENALTY, SPELL_AP_PENALTY, MOVE_STA_PENALTY,
            MOVE_AP_PENALTY,

    };
    public static final VALUE[] ARMOR_PARAMETERS = {MATERIAL_QUANTITY, ARMOR_LAYERS,
            COVER_PERCENTAGE, HARDNESS, ARMOR_MODIFIER,
            DURABILITY_MODIFIER, COST_MODIFIER, ATTACK_MOD,
            DEFENSE_MOD, DEFENSE_BONUS, C_DURABILITY, WEIGHT,
            GOLD_COST,

    };
    public static final VALUE[] WEAPON_PARAMETERS = {MATERIAL_QUANTITY, IMPACT_AREA,
            HARDNESS, DICE, BASE_DAMAGE_MODIFIER, DURABILITY_MODIFIER,
            STR_DMG_MODIFIER, AGI_DMG_MODIFIER, SP_DMG_MODIFIER,
            INT_DMG_MODIFIER,

            COST_MODIFIER, ARMOR_MOD, ATTACK_MOD, DAMAGE_MOD,
            DEFENSE_MOD, ARMOR_PENETRATION, ATTACK_BONUS, DAMAGE_BONUS,
            DEFENSE_BONUS, C_DURABILITY, WEIGHT, GOLD_COST,

    };
    public static final VALUE[] JEWELRY_PROPERTIES = {G_PROPS.MATERIAL, G_PROPS.JEWELRY_GROUP,
            PROPS.MAGICAL_ITEM_TRAIT, PROPS.MAGICAL_ITEM_LEVEL, PROPS.ENCHANTMENT,
            G_PROPS.PASSIVES, G_PROPS.STANDARD_PASSIVES};
    public static final VALUE[][] JEWELRY_PAGES = {DESCRIPTION, JEWELRY_PROPERTIES, ATTRIBUTES,};
    public static final VALUE[] ARMOR_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.ARMOR_TYPE,
            G_PROPS.ARMOR_GROUP, G_PROPS.ITEM_MATERIAL_GROUP, G_PROPS.MATERIAL,};
    public static final VALUE[] WEAPON_PROPERTIES = {G_PROPS.MASTERY, G_PROPS.WEAPON_TYPE,
            G_PROPS.WEAPON_GROUP, G_PROPS.WEAPON_CLASS, G_PROPS.WEAPON_SIZE,
            G_PROPS.ITEM_MATERIAL_GROUP, PROPS.DAMAGE_TYPE, G_PROPS.MATERIAL,
            G_PROPS.STANDARD_PASSIVES,

    };
    public static final VALUE[] AUTO_TEST_VALUES = {PROPS.AUTO_TEST_TYPE, PROPS.AUTO_TEST_GROUP,
            AUTO_TEST_ID, PROPS.AUTO_TEST_MEASUREMENTS, PROPS.AUTO_TEST_ASSERTIONS,
            PROPS.AUTO_TEST_CONSTRAINTS, PROPS.AUTO_TEST_WEAPON, PROPS.AUTO_TEST_OFFHAND_WEAPON,
            PROPS.AUTO_TEST_PREFS, PROPS.AUTO_TEST_RULE_FLAGS, PROPS.AUTO_TEST_CONSTRAINTS,};
    public static final VALUE[][] SKILL_PAGES = {SKILL_PROPERTIES, SKILL_PARAMETERS, REQUIREMENTS,
            SKILL_ADDITIONAL, ATTRIBUTES, RESISTANCES, UNIT_PARAMETERS, AUTO_TEST_VALUES};
    public static final VALUE[][] ACTION_PAGES = {ACTION_PROPS, ACTION_ATTACK_PARAMS,
            ACTION_PARAMS, COSTS, SNEAK_MODS, ACTION_PROPS2, AUTO_TEST_VALUES,};
    public static final VALUE[][] ACTION_PAGES_DC = {DESCRIPTION, COSTS, ACTION_PROPS_DC,
            ACTION_PARAMS_DC, ACTION_PARAMS_DC2};
    public static final VALUE[][] ARMOR_PAGES = {ARMOR_PROPERTIES, ARMOR_PARAMETERS, PENALTIES,
            UNIT_PARAMETERS};
    public static final VALUE[][] ALT_QUICK_ITEM_PAGES = {DESCRIPTION, QUICK_ITEM_PARAMETERS,
            COSTS};
    public static final VALUE[][] QUICK_ITEM_PAGES = {QUICK_ITEM_PROPERTIES, ITEM_PROPERTIES,
            QUICK_ITEM_PARAMETERS, ITEM_PARAMETERS, COSTS};
    public static final VALUE[][] WEAPON_PAGES = {WEAPON_PROPERTIES, WEAPON_PARAMETERS, PENALTIES};
    public static final VALUE[][] ALT_CHAR_PAGES = {CHARS_HEADER, ATTRIBUTES, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES};
    public static final VALUE[][] ALT_UNIT_PAGES = {UNITS_HEADER, ATTRIBUTES, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES, DESCRIPTION};
    public static final VALUE[][] ALT_BF_OBJ_PAGES = {BF_OBJ_HEADER, NATURAL_RESISTANCES,
            ASTRAL_RESISTANCES,}; // DIFFERENT
    public static final VALUE[][] UNIT_PAGES = {UNIT_PARAMETERS, UNIT_DYNAMIC_PARAMETERS,
            UNIT_PROPERTIES, ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, RESISTANCES,
            UNIT_LEVEL_PARAMETERS, LEVEL_PARAMETERS, PRINCIPLE_ALIGNMENTS, PRINCIPLE_IDENTITIES};
    public static final VALUE[][] CHAR_PAGES = {UNIT_PARAMETERS, UNIT_DYNAMIC_PARAMETERS,
            UNIT_PROPERTIES, ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, RESISTANCES, LEVEL_PARAMETERS,
            PRINCIPLE_ALIGNMENTS, PRINCIPLE_IDENTITIES};
    public static final VALUE[][] CLASS_PAGES = {CLASS_HEADER, CLASS_HEADER2,
            // ATTRIBUTES_VAL,
            // MA
            UNIT_PARAMETERS, AUTO_TEST_VALUES,};
    public static final VALUE[][] SPELL_PAGES = {SPELL_PARAMETERS, COSTS, SPELL_PROPERTIES,
            UPKEEP_PARAMS, SPELLS_UPGRADES};
    // VALUE
    // ICONS?
    public static final PARAMETER[] BACKGROUND_PARAMS = {
            // PARAMS.TOUGHNESS,
            // PARAMS.ENDURANCE, PARAMS.STAMINA, PARAMS.ESSENCE,
            // PARAMS.STARTING_FOCUS, PARAMS.SPIRIT,
            WEIGHT, CARRYING_CAPACITY, SIGHT_RANGE, SIDE_SIGHT_PENALTY,
            DETECTION, STEALTH,};
    public static final PARAMETER[] BACKGROUND_PARAMS_ADDITIONAL = {XP, GOLD,
            ATTR_POINTS, MASTERY_POINTS,

            FREE_MASTERIES, MASTERY_POINTS_PER_LEVEL, ATTR_POINTS_PER_LEVEL,
            GIRTH,
            // PARAMS.MEMORIZATION_CAP,
            // PARAMS.XP_GAIN_MOD,
            // PARAMS.XP_LEVEL_MOD,
            // PARAMS.XP_COST_REDUCTION,
            // PARAMS.GOLD_COST_REDUCTION,

    };
    public static final PROPERTY[] BACKGROUND_PROPS = {G_PROPS.BACKGROUND,
            G_PROPS.RACE,
            G_PROPS.ASPECT,
            G_PROPS.DEITY, // allowed deities?
            G_PROPS.PRINCIPLES, G_PROPS.STANDARD_PASSIVES, PROPS.OFFHAND_NATURAL_WEAPON,
            PROPS.NATURAL_WEAPON, PROPS.MASTERY_GROUPS_MAGIC, PROPS.MASTERY_GROUPS_MISC,
            PROPS.MASTERY_GROUPS_WEAPONS, G_PROPS.SOUNDSET,};
    public static final VALUE[][] BACKGROUND_VALUES = {DESCRIPTION, BACKGROUND_PROPS,
            DEFAULT_ATTRIBUTES, BASE_ATTRIBUTES, MASTERIES, BACKGROUND_PARAMS, RESISTANCES,
            BACKGROUND_PARAMS_ADDITIONAL, LORE, IDENTITY_PARAMS};
    // IN-GAME
    public static final String[] ALT_PAGE_NAMES = {
            "Units Header;Attributes;Natural Resistances;Astral Resistances"
            // + "Resistances;Masteries;" +
            // "Parameters;Properties;Additional"
            , // UNITS
            "Spell Header;Spell Parameters;Spell Properties;Upgrades", // SPELLS
            "Chars Header;Attributes;Natural Resistances;Astral Resistances;",
            "", // ABILS
            "Bf Obj Header;Natural Resistances;Astral Resistances;", // BF_OBJ
            "", // BUFFS
            "Description;Costs;Action Properties;Action Parameters;Action Parameters2;", // ACTION
            "Armor properties;Armor parameters;Penalties", // ARMOR
            "Weapon properties;Weapon parameters;Penalties", // WEAPONS
            "SKILL_PROPERTIES;SKILL_PARAMETERS;REQUIREMENTS;ATTRIBUTES;Resistances;UNIT_PARAMETERS", // SKILLS
            "Usable Item Properties;Usable Item Parameters;", "Description;", // garb
            "Garb Properties;Garb Parameters;", // g
            "Jewelry Properties;Jewelry Parameters;", // J
            "Header;Params;Props;" // CLASSES

    };
    // ARCANE VAULT
    // this system is stupid...
    public static final String[] PAGE_NAMES = {
            "Parameters;Dynamic Parameters;Properties;Attributes;Base Attributes;Masteries;Resistances;Additional;Additional2", // UNITS
            "Spell Properties;Spell Parameters;Costs;Upkeep;Upgrades", // SPELLS
            // this

            "Parameters;Dynamic Parameters;Properties;Attributes;Masteries;Resistances;Additional;Additional2", // CHARS

            "", // ABILS
            "", // BF_OBJ
            "", // BUFFS
            "Action Properties;Attack Parameters;Action Parameters;Costs;Sneak;Action Properties2;", // ACTIONS

            "Armor properties;Armor parameters", // ARMOR
            "Weapon properties;Weapon parameters", // WEAPONS
            "SKILL_PROPERTIES;SKILL_PARAMETERS;REQUIREMENTS;ATTRIBUTES;RESISTANCES;UNIT_PARAMETERS;Auto Test", // SKILLS
            "Usable Item Properties;Usable Item Parameters;Item Properties;Item Parameters;", // usables
            "Description;", // garb
            "Description;Jewelry Properties;Jewelry Parameters;", // j

            "Header;Params;Props;Auto Test" // CLASSES

    };
    public static final PARAMETER[] MASTERIES_MAGIC_SCHOOLS = {

            PSYCHIC_MASTERY, SHADOW_MASTERY, WITCHERY_MASTERY,
            NECROMANCY_MASTERY, AFFLICTION_MASTERY, BLOOD_MAGIC_MASTERY,
            SAVAGE_MASTERY, SYLVAN_MASTERY, ELEMENTAL_MASTERY,
            CONJURATION_MASTERY, SORCERY_MASTERY, ENCHANTMENT_MASTERY,
            REDEMPTION_MASTERY, CELESTIAL_MASTERY, BENEDICTION_MASTERY,
            WARP_MASTERY, DESTRUCTION_MASTERY, DEMONOLOGY_MASTERY,};

    public static final PARAMETER[] MAIN_UNIT_INFO_PARAMETERS = {
            RESISTANCE,
            DEFENSE,
            ARMOR,
            FORTITUDE,
            SPIRIT,
    };
    public static final VALUE[][] UNIT_INFO_PARAMS_PHYSICAL = {
            {
                    INITIATIVE_MODIFIER, INITIATIVE,
                    STARTING_FOCUS,
//      PARAMS.BASE_MORALE,
                    ENDURANCE_REGEN, STAMINA_REGEN,
                    TOUGHNESS_RECOVERY,
//      PARAMS.UNCONSCIOUS_THRESHOLD
            },
            {
                    SIGHT_RANGE, SIDE_SIGHT_PENALTY,
                    HEIGHT, GIRTH,
                    WEIGHT, CARRYING_CAPACITY,
                    ILLUMINATION, CONCEALMENT
            },
            {
                    FOCUS_RETAINMENT, FOCUS_RESTORATION,
                    MORALE_RETAINMENT, MORALE_RESTORATION,
                    FEAR_RESISTANCE, WOUNDS_RESISTANCE,
                    BASH_RESISTANCE, INTERRUPT_RESISTANCE,
//     FATIGUE, CONFUSION,
            },

    };
    public static final VALUE[][] UNIT_INFO_PARAMS_COMBAT = {
            {

                    ATTACK_AP_PENALTY, ATTACK_STA_PENALTY,
                    MOVE_AP_PENALTY, MOVE_STA_PENALTY,

                    DIAGONAL_ATTACK_MOD, DIAGONAL_DAMAGE_MOD,
                    SIDE_ATTACK_MOD, SIDE_DAMAGE_MOD,
            },
            {

                    CRITICAL_MOD, AUTO_CRIT_CHANCE,
                    CLOSE_QUARTERS_ATTACK_MOD, CLOSE_QUARTERS_DAMAGE_MOD,
                    LONG_REACH_ATTACK_MOD, LONG_REACH_DAMAGE_MOD,
                    COUNTER_MOD, LONG_REACH_DAMAGE_MOD,

            },
            {

                    ARMOR_PENETRATION, BLOCK_PENETRATION,
                    DEFENSE_PENETRATION, PARRY_PENETRATION,
                    ACCURACY, EVASION,
                    BLOCK_CHANCE, PARRY_CHANCE,


            },
    };
    public static final String[] INFO_TABLE_NAMES = {
            "Physical", "Combat", "Magic", "Misc", "Rolls", "Mods"
    };
    private static final VALUE[] DC_SPELL_PARAMETERS = {

            AP_COST, STA_COST, ESS_COST, ENDURANCE_COST, FOC_COST,
            FOC_REQ, RANGE, G_PARAMS.RADIUS, SPELLPOWER_MOD,
            G_PARAMS.DURATION, COOLDOWN, C_COOLDOWN,

    };
    public static final VALUE[][] ALT_SPELL_PAGES = {DESCRIPTION, DC_SPELL_PARAMETERS,
            DC_SPELLS_HEADER, SPELLS_UPGRADES};
    private static final VALUE[][] UNIT_INFO_PARAMS_MODS = {

            {
                    COUNTER_MOD,
                    THROW_ATTACK_MOD,
                    THROW_DAMAGE_MOD,
                    COOLDOWN_MOD,
                    BLEEDING_MOD,
            },
            {
                    INSTANT_DAMAGE_MOD,
                    INSTANT_ATTACK_MOD,
                    INSTANT_DEFENSE_MOD,
                    AOO_DAMAGE_MOD,
                    AOO_ATTACK_MOD,
                    AOO_DEFENSE_MOD,
                    COUNTER_ATTACK_MOD,
                    COUNTER_DEFENSE_MOD,
            },
            {
                    FORCE_MOD,
                    FORCE_KNOCK_MOD
                    , FORCE_PUSH_MOD
                    , FORCE_PROTECTION
                    , FORCE_DAMAGE_MOD
                    , FORCE_SPELL_DAMAGE_MOD
                    , FORCE_MOD_SOURCE_WEIGHT
                    , FORCE_SPELLPOWER_MOD
            },
            {
                    PASSAGE_ATTACK_MOD
                    ,
                    ENGAGEMENT_ATTACK_MOD
                    ,
                    FLIGHT_ATTACK_MOD
                    ,
                    STUMBLE_ATTACK_MOD
                    ,
                    PASSAGE_DEFENSE_MOD
                    ,
                    ENGAGEMENT_DEFENSE_MOD
                    ,
                    FLIGHT_DEFENSE_MOD
                    ,
                    STUMBLE_DEFENSE_MOD
                    ,

                    DISENGAGEMENT_ATTACK_MOD
                    ,
                    DISENGAGEMENT_DEFENSE_MOD,
            },
            {
                    STOP_DISENGAGEMENT_CHANCE_MOD,
                    PASS_DISENGAGEMENT_CHANCE_MOD
                    ,
                    STOP_ENGAGEMENT_CHANCE_MOD
                    ,
                    PASS_ENGAGEMENT_CHANCE_MOD,
                    STOP_FLIGHT_CHANCE_MOD,
                    PASS_FLIGHT_CHANCE_MOD,
                    STOP_PASSAGE_CHANCE_MOD,
                    PASS_PASSAGE_CHANCE_MOD,
            },
    };
    private static final VALUE[][] UNIT_INFO_PARAMS_MISC = {
            {
                    QUICK_SLOTS, ITEM_COST_MOD,
                    DETECTION, STEALTH,
                    SNEAK_DEFENSE_PENETRATION, SNEAK_ARMOR_PENETRATION,
                    SNEAK_ATTACK_MOD, SNEAK_DAMAGE_MOD,
            },
            {
                    CADENCE_AP_MOD, CADENCE_ATTACK_MOD,
                    CADENCE_BONUS, CADENCE_DAMAGE_MOD,
                    CADENCE_DEFENSE_MOD, CADENCE_FOCUS_BOOST,
                    CADENCE_STA_MOD, CADENCE_RETAINMENT_CHANCE,
            },
            {
                    WATCH_DEFENSE_MOD, WATCH_ATTACK_MOD,
                    WATCH_AP_PENALTY_MOD, WATCH_DETECTION_MOD,
                    WATCH_ATTACK_OTHERS_MOD, WATCH_DEFENSE_OTHERS_MOD,
                    WATCHED_ATTACK_MOD,
            },

    };
    private static final VALUE[][] UNIT_INFO_PARAMS_ROLLS = {
            {
                    REACTION_ROLL_SAVE_BONUS, REACTION_ROLL_BEAT_BONUS,
                    REFLEX_ROLL_SAVE_BONUS, REFLEX_ROLL_BEAT_BONUS,
                    DEFENSE_ROLL_SAVE_BONUS, DEFENSE_ROLL_BEAT_BONUS,
                    DISARM_ROLL_SAVE_BONUS, DISARM_ROLL_BEAT_BONUS,
            },
            {
                    FORTITUDE_ROLL_SAVE_BONUS, FORTITUDE_ROLL_BEAT_BONUS,
                    BODY_STRENGTH_ROLL_SAVE_BONUS, BODY_STRENGTH_ROLL_BEAT_BONUS,
                    MASS_ROLL_SAVE_BONUS, MASS_ROLL_BEAT_BONUS,
                    FORCE_ROLL_SAVE_BONUS, FORCE_ROLL_BEAT_BONUS,
            },
            {
                    MIND_AFFECTING_ROLL_SAVE_BONUS, MIND_AFFECTING_ROLL_BEAT_BONUS,
                    QUICK_WIT_ROLL_SAVE_BONUS, QUICK_WIT_ROLL_BEAT_BONUS,
                    FAITH_ROLL_SAVE_BONUS, FAITH_ROLL_BEAT_BONUS,
                    DETECTION_ROLL_SAVE_BONUS, DETECTION_ROLL_BEAT_BONUS,
            },
    };
    private static final VALUE[][] UNIT_INFO_PARAMS_MAGIC = {
            {
                    RESISTANCE_PENETRATION,
            },
    };
    public static final VALUE[][][] UNIT_INFO_PARAMS = {
            UNIT_INFO_PARAMS_PHYSICAL,
            UNIT_INFO_PARAMS_COMBAT,
            UNIT_INFO_PARAMS_MAGIC,
            UNIT_INFO_PARAMS_MISC,
            UNIT_INFO_PARAMS_ROLLS,
            UNIT_INFO_PARAMS_MODS,
    };
    public static VALUE[] ARMOR_GRADES;

    static {
        // TODO revamp value_page usage?
        // List<MultiParameter> armorGradeMultiParams =
        // DC_ContentManager.getArmorGradeMultiParams();
        // ARMOR_GRADES = armorGradeMultiParams.toArray(new
        // VALUE[armorGradeMultiParams.size()]);
    }

    static {
        for (Field f : ValuePages.class.getFields()) {
            if (f.getType().isArray()) {

            }
        }
    }

    public enum PAGE_NAMES {
        HEADER,
        PARAMETERS,
        PROPERTIES,
        MASTERIES,
        ATTRIBUTES,
        BASE_ATTRIBUTES,
        RESISTANCES,
        DYNAMIC_PARAMETERS,
        COSTS,
        ADDITIONAL,
        DEBUG,
        AV_HEADER,
        AV_BOTTOM,
        AV_TRAILING_PAGE,
        DC_TRAILING_PAGE,
    }

}
