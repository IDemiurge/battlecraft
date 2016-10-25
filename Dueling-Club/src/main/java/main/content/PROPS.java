package main.content;

import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.properties.PROPERTY;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

public enum PROPS implements PROPERTY { // SPECIAL_ATTACKS, MOVES, ACTIONS
    // INSTEAD OF 'ACTIVES'!
    // AGE(null, "chars"),
    LAST_ARCADE(null, "meta"), // "meta data" obj_type?
    MEMBERS(null, true, "party"),
    LEADER(null, false, "party"),

    PERKS(null, true, "deities"),

    STATS_SLAIN_ENEMIES(null, true, "party"),
    STATS_FALLEN_HEROES(null, true, "party"),
    ARCADE_STATUS(null, false, "party"),
    DUNGEON_TAGS(null, true, "dungeons"),
    MAP_BACKGROUND(null, false, "dungeons", "area", "route", "place"),
    DUNGEON_MAP_TEMPLATE(null, false, "area", "dungeons", "route", "place"),
    DUNGEON_MAP_MODIFIER(null, false, "dungeons", "route", "place"),
    MAP_OBJECTS(null, true, "dungeons", "route", "place"),
    MAP_PRESET_OBJECTS(null, true, "dungeons"),
    COLOR_THEME(null, false, "dungeons", "route", "place"),
    ALT_COLOR_THEME(null, false, "dungeons"),
    LOOT_GROUPS(null, true, "dungeons"),
    // THEMED BACKGROUND?
    PARTY_SPAWN_COORDINATES(null, false, "dungeons"),
    ENEMY_SPAWN_COORDINATES(null, false, "dungeons"),
    DUNGEON_PLAN(null, false, "dungeons"),

    SUBLEVELS(null, true, "dungeons"),
    FILLER_TYPE(null, false, "dungeons"),
    ARCADE_LOOT_TYPE(null, true, "dungeons"),
    SUBDUNGEON_TYPE(null, false, "dungeons"),
    SPAWNING_DELAYS(null, true, "dungeons"),
    ENCOUNTERS(null, true, "dungeons"),
    ALT_ENCOUNTERS(null, true, "dungeons"),
    POWER_ENCOUNTERS(null, true, "dungeons"),

    REGULAR_ENCOUNTERS(null, true, "dungeons"),
    ELITE_ENCOUNTERS(null, true, "dungeons"),
    BOSS_ENCOUNTERS(null, true, "dungeons"),
    DUNGEON_MAIN_ENTRANCES(null, true, "dungeons"),
    ENCOUNTER_GROUPS(null, true, "dungeons", "route", "place", "area"),
    ENCOUNTER_SPAWN_POINTS(null, true, "dungeons"),
    ENCOUNTER_BOSS_SPAWN_POINTS(null, true, "dungeons"),

    ALT_POWER_ENCOUNTERS(null, true, "dungeons"),
    ALT_SPAWNING_DELAYS(null, true, "dungeons"),
    ALT_ARCADE_LOOT_TYPE(null, true, "dungeons"),
    DUNGEON_TEMPLATES(null, true, "dungeons"),
    DUNGEON_TEMPLATE_TYPE(null, false, "dungeons"),
    ADDITIONAL_ROOM_TYPES(null, true, "dungeons"),

    MASTERY_GROUPS_MAGIC(null, true, "units", "chars"),
    MASTERY_GROUPS_WEAPONS(null, true, "units", "chars"),
    MASTERY_GROUPS_MISC(null, true, "units", "chars"),

    ATTRIBUTE_PROGRESSION(null, true, "units", "chars"),
    MASTERY_PROGRESSION(null, true, "units", "chars"),

    SPELL_PLAN(null, true, "units", "chars"),
    SPELL_UPGRADES(null, true, "spells", "chars"),
    SPELL_UPGRADE_GROUPS(null, true, "spells", "skills", "classes", "units", "chars"),

    SPELL_UPGRADES_PLAN(null, true, "units", "chars"),
    ACTION_PRIORITY_MODS(null, true, "units", "chars"),
    ACTION_PRIORITY_BONUSES(null, true, "units", "chars"),

    VERBATIM_PRIORITY(null, true, "units", "chars"),
    MEMORIZATION_PRIORITY(null, true, "units", "chars"),

    MAIN_HAND_REPERTOIRE(null, true, "units", "chars"),
    OFF_HAND_REPERTOIRE(null, true, "units", "chars"),
    ARMOR_REPERTOIRE(null, true, "units", "chars"),
    QUICK_ITEM_REPERTOIRE(null, true, "units", "chars"),
    JEWELRY_ITEM_TRAIT_REPERTOIRE(null, true, "units", "chars"),
    JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE(null, true, "units", "chars"),

    QUALITY_LEVEL_RANGE(null, true, "units", "chars"),
    ALLOWED_MATERIAL(null, true, "units", "chars"),

    // quick items/jewelry?
    XP_PLAN(null, true, "units"),
    GOLD_PLAN(null, true, "units"),

    HOTKEY(null, false, "actions", "spells"),
    ALT_HOTKEY(null, false, "actions", "spells"),

    AI_LOGIC(null, false, "actions", "spells"),
    AI_TYPE(null, false, "units", "chars"),
    AI_MODIFIERS(null, true, "units", "chars"),
    AI_PRIORITY_FORMULA(null, false, "actions", "spells"),
    BEHAVIOR_MODE(null, false, "units", "chars"),
    STANDARD_ACTION_PASSIVES("STD_ACTION_PASSIVES", true, "actions"),
    STANDARD_SPELL_PASSIVES("STD_SPELL_PASSIVES", true, "spells"),

    UPKEEP_FAIL_ACTION(null, false, "spells"),

    // REQUIRED_SKILLS("REQUIRED_SKILLS", true, "actions", "spells", "skills"),
    // REQUIRED_SPELLS("REQUIRED_SPELLS", true, "actions", "spells", "skills"),
    // ++ ATTRS
    // ++ MASTERIES
    LAST_SEEN(null, true, "units", "chars", "bf obj"),
    HINTS(null, true, "units", "chars", "bf obj"),
    PERCEPTION_STATUS_PLAYER(null, false, "units", "chars", "bf obj"),
    PERCEPTION_STATUS(null, false, "units", "chars", "bf obj"),
    FACING_DIRECTION(null, false, "units", "chars", "bf obj"),
    DIRECTION(null, false, "units", "chars", "bf obj"),
    VISIBILITY_STATUS(null, false, "units", "chars", "bf obj", "terrain"),
    DETECTION_STATUS(null, false, "units", "chars", "bf obj", "terrain"),
    DAMAGE_TYPE(null, false, "units", "chars", "weapons", "actions", "items", "spells"),
    VISION_MODE(null, false, "units", "chars", "bf obj"),

    OBJECT_ARMOR_TYPE(null, false, "units", "chars", "bf obj"),
    BF_OBJECT_SIZE(null, false, "bf obj"),
    BF_OBJ_MATERIAL(null, false, "bf obj"),
    BF_OBJ_QUALITY(null, true, "bf obj"),
    DIMENSION(null, false, "bf obj", "units", "chars"),
    PALETTE(null, false, "bf obj", "units", "chars"),
    // IMMUNE(null, true, "units", "chars", "armor"),
    // RESISTANT(null, true, "units", "chars", "armor"),
    // VULNERABLE(null, true, "units", "chars", "armor"),

    SKILLS("Skills", true, "chars", "units"),
    SPECIAL_ABILITIES("SPECIAL_ABILITIES", true, "chars", "units"),
    SKILL_REQUIREMENTS("Skill REQ", true, "spells", "skills", "classes"),
    SKILL_OR_REQUIREMENTS("Skill OR REQ", true, "spells", "skills", "classes"),
    TREE_NODE_GROUP(null, false, "spells", "skills", "classes"),
    LINK_VARIANT(null, false, "spells", "skills", "classes"),
    TREE_NODE_PRIORITY(null, false, "spells", "skills", "classes"),

    CLASSES("Classes", true, "chars"),
    FIRST_CLASS("chars", "First Class"),
    SECOND_CLASS("chars", "Second Class"),
    REQUIREMENTS("Requirements", true, "spells", "skills", "classes"),
    INVENTORY(null, true, "units", "chars"),
    QUICK_ITEMS(null, true, "units", "chars"),
    JEWELRY(null, true, "chars"),
    OFFHAND_NATURAL_WEAPON(null, false, "units", "chars"),
    NATURAL_WEAPON(null, false, "units", "chars"),

    // ALIGNMENTS(null, true, "units", "chars", "deities", "classes", "skills"),
    // IDENTITY(null, true, "units", "chars", "deities", "classes", "skills"),

    ITEM_SPELL(null, false, "items"),
    // all spells except verbatim/memorized
    SPELLBOOK(null, true, "units", "chars", "actions", "spells", "skills"),
    VERBATIM_SPELLS(null, true, "units", "chars", "actions", "spells", "skills"),
    // all spells
    KNOWN_SPELLS(null, true, "units", "chars"),
    LEARNED_SPELLS(null, true, "units", "chars"),
    MEMORIZED_SPELLS(null, true, "units", "chars"),
    DIVINED_SPELLS(null, true, "units", "chars"),
    UPGRADED_SPELLS(null, true, "units", "chars"),
    PREPARED_SPELLS(null, true, "units", "chars"),
    INJURIES(null, true, "units", "chars") {
        @Override
        public boolean isWriteToType() {
            return true;
        }
    },

    SPECIAL_ACTION_MODES(null, true, "units", "chars", "skills", "classes"),
    ACTION_MODES(null, true, "actions"),
    PARTY_UNITS("chars", null),
    FACTION("chars", null),
    HIRED_MERCENARIES("chars", null),

    DEFAULT_ATTACK_ACTION(null, false, "weapons"),
    DEFAULT_COUNTER_ATTACK_ACTION(null, false, "weapons"),
    DEFAULT_ATTACK_OF_OPPORTUNITY_ACTION(null, false, "weapons"),
    DEFAULT_INSTANT_ATTACK_ACTION(null, false, "weapons"),

    TRUE_NAME("deities", null),
    DIVINATION_PARAMETER("chars", null),
    DIVINATION_FORCED_SPELL_GROUPS("chars", null),
    FAVORED_SPELL_GROUPS("deities", null),
    BONUS_HERO_TRAITS("deities", null),
    FOLLOWER_UNITS("deities", null),
    ALLIED_DEITIES("deities", null),
    FRIEND_DEITIES("deities", null),
    ENEMY_DEITIES("deities", null),
    SPECIAL_FAVORED_SPELLS("deities", null),
    FAVORED_ASPECT("deities", "Favored Aspect"),
    SECOND_FAVORED_ASPECT("deities", "Second Favored Aspect"),
    THIRD_FAVORED_ASPECT("deities", "Third Favored Aspect"),

    PRIME_MISSION("deities", null),
    SECONDARY_MISSION("deities", null),
    WORLDS("deities", null), // TODO: WORLD enum with pics, music and specials
    BUFF_NAME(null, true, "spells", "actions", "items"),
    RESISTANCE_TYPE("spells", "Resistance type"),
    TARGETING_MODIFIERS(null, true, "spells", "actions", "items"),
    // TARGETING_CONDITIONS(null, true, "spells", "actions", "items"), via spec
    // req "custom"
    EFFECTS_WRAP("EFFECTS WRAP", false, "spells", "actions", "items"),
    RESISTANCE_MODIFIERS("", true, "spells", "actions"),

    ON_BEING_HIT("", true, "units", "chars", "armor"),
    ON_HIT("ON_HIT ", true, "spells", "actions", "units", "chars", "weapons"),
    ON_ACTIVATE("", true, "spells", "actions"),
    ON_KILL("", true, "spells", "actions", "units", "chars", "weapons"),
    WEAPON_ATTACKS("", true, "weapons"),
    FILLER_TYPES("encounters", null), // adjust for difficulty if
    // cannot add
    // GROUP
    UNIT_TYPES(null, true, "encounters"), // POOL
    UNIT_POOL(null, true, "factions"),
    HERO_BACKGROUNDS(null, true, "factions"),
    FACTION_DUNGEONS(null, true, "factions"),
    HEADQUARTER_DUNGEON(null, true, "factions"),
    ALLY_FACTIONS(null, true, "factions"),
    GROWTH_PRIORITIES(null, true, "encounters"), // POOL

    EXTENDED_PRESET_GROUP(null, true, "encounters"),
    SHRUNK_PRESET_GROUP(null, true, "encounters"),
    PRESET_GROUP("PRESET_GROUP", true, "encounters"),
    SPAWNING_SIDE(null, false, "encounters"),
    // for
    ENCHANTMENT(null, true, "jewelry", "weapons", "armor"),

    BOSS_TYPE("encounters", null),
    JEWELRY_PASSIVE_ENCHANTMENT("jewelry", null),
    MAGICAL_ITEM_LEVEL("jewelry", null),
    MAGICAL_ITEM_TRAIT("jewelry", null),

    DEAD_UNITS(null, true, "terrain"),
    DROPPED_ITEMS(null, true, "terrain"),
    BASE_CLASSES_ONE(null, true, "classes"),
    BASE_CLASSES_TWO(null, true, "classes"),
    MULTICLASSES(null, true, "chars"),

    ROLL_TYPES_TO_SAVE(null, true, "spells", "actions"),
    ROLL_TYPES_TO_DISPEL_EACH_TURN(null, true, "spells", "actions"),
    CHANNELING_SOUND(null, false, "spells", "actions"),
    RETAIN_CONDITIONS(null, false, "spells", "actions"),
    ITEM_SHOP_CATEGORY(null, false, "items", "weapons", "armor"),

    ALT_BASE_LINKS(null, true, "skills", "classes"),
    ALT_BASE_TYPES(null, true, "skills", "classes"),
    PARAMETER_BONUSES(null, true, "skills", "classes"),
    ATTRIBUTE_BONUSES(null, true, "skills", "classes"),
    APPLY_REQS(null, true, "skills", "classes"),

    AUTO_TEST_TYPE(null, true, "skills", "actions", "spells", "abils", "classes"),
    AUTO_TEST_GROUP(null, true, "skills", "actions", "spells", "abils", "classes"),
    AUTO_TEST_RULE_FLAGS(null, true, "skills", "actions", "spells", "abils", "classes"),
    AUTO_TEST_ASSERTIONS(null, true, "skills", "actions", "spells", "abils", "classes"),
    AUTO_TEST_MEASUREMENTS(null, true, "skills", "actions", "spells", "abils", "classes"),
    AUTO_TEST_PREFS(null, true, "skills", "actions", "spells", "abils", "classes"),
    AUTO_TEST_CONSTRAINTS(null, true, "skills", "actions", "spells", "abils", "classes"),

    AUTO_TEST_WEAPON(null, false, "skills", "actions", "classes"),
    AUTO_TEST_OFFHAND_WEAPON(null, false, "skills", "actions", "classes"),

    ARCADE_LEVELS(null, true, "arcades"),
    ARCADE_ENEMY_GROUPS(null, true, "arcades"),
    ARCADE_FACTIONS(null, true, "arcades"),

    TERRAIN_TYPE(null, false, "terrain"),

    DURABILITY_GRADE_PIERCING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_PIERCING", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_BLUDGEONING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_BLUDGEONING", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_SLASHING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_SLASHING", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_POISON(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_POISON", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_FIRE(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_FIRE", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_COLD(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_COLD", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_LIGHTNING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_LIGHTNING", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_ACID(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_ACID", "_")), false, "weapons", "armor"),

    DURABILITY_GRADE_SONIC(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_SONIC", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_LIGHT(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_LIGHT", "_")), false, "weapons", "armor"),

    DURABILITY_GRADE_ARCANE(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_ARCANE", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_CHAOS(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_CHAOS", "_")), false, "weapons", "armor"),

    DURABILITY_GRADE_SHADOW(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_SHADOW", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_HOLY(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_HOLY", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_DEATH(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_DEATH", "_")), false, "weapons", "armor"),
    DURABILITY_GRADE_PSIONIC(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "DURABILITY_GRADE_PSIONIC", "_")), false, "weapons", "armor"),
    //
    RESIST_GRADE_PIERCING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_PIERCING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_BLUDGEONING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_BLUDGEONING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_SLASHING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_SLASHING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_POISON(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_POISON", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_FIRE(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_FIRE", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_COLD(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_COLD", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_LIGHTNING(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_LIGHTNING", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_ACID(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_ACID", "_")), false, "units", "chars", "weapons", "armor"),

    RESIST_GRADE_SONIC(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_SONIC", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_LIGHT(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_LIGHT", "_")), false, "units", "chars", "weapons", "armor"),

    RESIST_GRADE_ARCANE(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_ARCANE", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_CHAOS(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_CHAOS", "_")), false, "units", "chars", "weapons", "armor"),

    RESIST_GRADE_SHADOW(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_SHADOW", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_HOLY(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_HOLY", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_DEATH(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_DEATH", "_")), false, "units", "chars", "weapons", "armor"),
    RESIST_GRADE_PSIONIC(StringMaster.getWellFormattedString(StringMaster.getLastPart(
            "RESIST_GRADE_PSIONIC", "_")), false, "units", "chars", "weapons", "armor"),
    // common,
    // rare,
    // special,
    // legendary
    // etc
    // unit in the middle of each GROUP

    ;

    static {
        FAVORED_SPELL_GROUPS.setContainer(true);
        NATURAL_WEAPON.setDefaultValue("Average Fist");
        // OFFHAND_NATURAL_WEAPON.setDefaultValue("Average Fist");
        SPELL_UPGRADES.setDynamic(true);

        HINTS.setDynamic(true);
        LAST_SEEN.setDynamic(true);
        DEAD_UNITS.setDynamic(true);
        UPKEEP_FAIL_ACTION.setDynamic(true);
        DROPPED_ITEMS.setDynamic(true);
        // QUICK_ITEMS.setDynamic(true);
        // INVENTORY.setDynamic(true);
        // JEWELRY.setDynamic(true);
        // VERBATIM_SPELLS.setDynamic(true);
        // MEMORIZED_SPELLS.setDynamic(true);
        DIVINED_SPELLS.setDynamic(true);
        SPAWNING_SIDE.setDynamic(true);
        // KNOWN_SPELLS.setDynamic(true);
        // LEARNED_SPELLS.setDynamic(true);

        FACING_DIRECTION.setDynamic(true);
        VISIBILITY_STATUS.setDynamic(true);
        DETECTION_STATUS.setDynamic(true);
        PERCEPTION_STATUS_PLAYER.setDynamic(true);
        PERCEPTION_STATUS.setDynamic(true);

        OBJ_TYPES.SPELLS.setUpgradeRequirementProp(KNOWN_SPELLS);
        OBJ_TYPES.CLASSES.setUpgradeRequirementProp(CLASSES);
        OBJ_TYPES.SKILLS.setUpgradeRequirementProp(SKILLS);

        // DYNAMIC CONTAINERS

        DEFAULT_COUNTER_ATTACK_ACTION.setLowPriority(true);
        DEFAULT_ATTACK_OF_OPPORTUNITY_ACTION.setLowPriority(true);
        SPELLBOOK.setLowPriority(true);
        VERBATIM_SPELLS.setLowPriority(true);
        MEMORIZED_SPELLS.setLowPriority(true);
        PARTY_UNITS.setLowPriority(true);
        HIRED_MERCENARIES.setLowPriority(true);
        PREPARED_SPELLS.setLowPriority(true);

        ON_BEING_HIT.setLowPriority(true);
        ON_HIT.setLowPriority(true);
        ON_ACTIVATE.setLowPriority(true);
        ON_KILL.setLowPriority(true);

        VISION_MODE.setLowPriority(true);

        XP_PLAN.setSuperLowPriority(true);
        XP_PLAN.setLowPriority(true);
        ATTRIBUTE_PROGRESSION.setSuperLowPriority(true);
        ATTRIBUTE_PROGRESSION.setLowPriority(true);
    }

    boolean writeToType;
    INPUT_REQ inputReq;
    private Metainfo metainfo;
    private String name;
    private String descr;
    private String entityType;
    private boolean dynamic;
    private String defaultValue;
    private int AV_ID;
    private boolean lowPriority = false;
    private String[] entityTypes;
    private boolean container;
    private boolean superLowPriority = false;
    private boolean highPriority;
    private String fullName;
    private String shortName;
    private Map<OBJ_TYPE, Object> defaultValuesMap;

    PROPS(String shortName, boolean container, String... entityTypes) {
        this(entityTypes[0], shortName, "", false, ContentManager.getDefaultEmptyValue(), 0);
        this.entityTypes = entityTypes;
        this.container = container;
    }

    PROPS(String entityType, String shortName) {
        this(entityType, shortName, "", false, ContentManager.getDefaultEmptyValue(), 0);
    }

    PROPS(String entityType, String shortName, String descr, boolean dynamic, String defaultValue,
          int AV_ID) {
        this.name = StringMaster.getWellFormattedString(name());
        this.shortName = shortName;
        if (StringMaster.isEmpty(shortName))
            this.shortName = name;
        // this.shortName = StringMaster.capitalizeFirstLetter(name()
        // .toLowerCase());
        this.fullName = name();
        this.descr = descr;
        this.entityType = entityType;
        this.dynamic = dynamic;
        this.defaultValue = defaultValue;
        this.AV_ID = AV_ID;
    }

    @Override
    public INPUT_REQ getInputReq() {
        return inputReq;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the shortName
     */
    public String getName() {
        return name;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }

    /**
     * @return the entityType
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * @return the dynamic
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * @param dynamic the dynamic to set
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(defaultValue);
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the aV_ID
     */
    public int getAV_ID() {
        return AV_ID;
    }

    /**
     * @param aV_ID the aV_ID to set
     */
    public void setAV_ID(int aV_ID) {
        AV_ID = aV_ID;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getEntityTypes() {
        return entityTypes;
    }

    @Override
    public Metainfo getMetainfo() {
        return metainfo;
    }

    @Override
    public boolean isContainer() {
        // TODO Auto-generated method stub
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    @Override
    public boolean isSuperLowPriority() {
        return superLowPriority;
    }

    @Override
    public void setSuperLowPriority(boolean superLowPriority) {
        this.superLowPriority = superLowPriority;
    }

    @Override
    public boolean isHighPriority() {
        return highPriority;
    }

    @Override
    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isPrinciple() {
        for (PRINCIPLES p : PRINCIPLES.values())
            if (p.toString().equalsIgnoreCase(getName()))
                return true;
        return false;
    }

    public boolean isWriteToType() {
        return writeToType;
    }

    public void setWriteToType(boolean writeToType) {
        this.writeToType = writeToType;
    }

    public synchronized String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Map<OBJ_TYPE, Object> getDefaultValuesMap() {
        if (defaultValuesMap == null)
            defaultValuesMap = new HashMap<OBJ_TYPE, Object>();
        return defaultValuesMap;
    }

    @Override
    public void addSpecialDefault(OBJ_TYPE type, Object value) {
        getDefaultValuesMap().put(type, value);

    }

    @Override
    public Object getSpecialDefault(OBJ_TYPE type) {
        return getDefaultValuesMap().get(type);

    }

}