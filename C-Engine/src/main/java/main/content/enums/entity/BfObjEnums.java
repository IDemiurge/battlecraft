package main.content.enums.entity;

import com.badlogic.gdx.graphics.Color;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.PathUtils;

/**
 * Created by JustMe on 2/14/2017.
 */
public class BfObjEnums {

//    createEmitter("spell/shape/soul dissipation", -32, 32);
//    createEmitter("spell/shape/soul dissipation pale", 32, 32);
//    createEmitter("unit/black soul bleed 3", 64, 64);
//    createEmitter("unit/black soul bleed 3", -64, 64);
//    createEmitter("unit/chaotic dark", 32, 32);
//    createEmitter("unit/chaotic dark", -32, 32);
public static void init(){
    CUSTOM_OBJECT.BLACKNESS.vfxOver = GenericEnums.VFX.darkness.path + "(-132, -32);";
    CUSTOM_OBJECT.BLACKNESS.vfxOver += GenericEnums.VFX.darkness.path + "(-132, -32);";
    CUSTOM_OBJECT.BLACKNESS.vfxUnder = GenericEnums.VFX.soul_bleed.path + "(-132, -64);";
    CUSTOM_OBJECT.BLACKNESS.vfxUnder += GenericEnums.VFX.soul_bleed.path + "(-132, -64);";
    CUSTOM_OBJECT.BLACKNESS.vfxOver += GenericEnums.VFX.darkness.path + "(-132, -82);";
    CUSTOM_OBJECT.BLACKNESS.vfxOver += GenericEnums.VFX.darkness.path + "(-132, -82);";
    CUSTOM_OBJECT.BLACKNESS.vfxUnder += GenericEnums.VFX.soul_bleed.path + "(-132, -124);";
    CUSTOM_OBJECT.BLACKNESS.vfxUnder += GenericEnums.VFX.soul_bleed.path + "(-132, -124);";
    CUSTOM_OBJECT.SMALL_LEVIATHAN.spriteColor = new Color(1, 1, 1, 0.7f);
    CUSTOM_OBJECT.SMALL_CLAW.spriteColor = new Color(1, 1, 1, 0.7f);

    CUSTOM_OBJECT.GATE_PILLAR.vfxOver = GenericEnums.VFX.MIST_ARCANE.path + "(32, 128);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.MIST_ARCANE.path + "(-32, 128);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.MIST_ARCANE.path + "(32, 0);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.MIST_ARCANE.path + "(-32, 0);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.MIST_ARCANE.path + "(32, -128);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.MIST_ARCANE.path + "(-32, -128);";

    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.MIST_WIND.path + "(-32, 128);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.ASH.path + "(-32, 128);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.THUNDER_CLOUDS_CRACKS.path + "(-32, 128);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.SNOWFALL_THICK.path + "(-32, 128);";
    CUSTOM_OBJECT.GATE_PILLAR.vfxOver += GenericEnums.VFX.WISPS.path + "(-32, 128);";

//        CUSTOM_OBJECT.black_waters.screen = true;
    CUSTOM_OBJECT.BLACKNESS.vfxUnderMirrorX = true;

    CUSTOM_OBJECT.soul_net.vfxOver += GenericEnums.VFX.soulflux_continuous.path + "(-42, 32);";
    CUSTOM_OBJECT.soul_net.vfxOver += GenericEnums.VFX.soulflux_continuous.path + "(42, 32);";
    CUSTOM_OBJECT.soul_net.setVfxSpeed(0.1f);


    CUSTOM_OBJECT.wisp_floating.vfxOver += "ambient/sprite/willowisps(0, 0);";

    CUSTOM_OBJECT.flames.vfxOver += "ambient/sprite/fires/real fire2(0, 0);";


//        CUSTOM_OBJECT.nether_flames.vfxUnder += "ambient/sprite/fires/nether flame green(0, 0);";
//        CUSTOM_OBJECT.nether_flames.vfxFolderOver = "ambient/sprite/fires/nether flames";


//        CUSTOM_OBJECT.nether_flames.vfxFolderUnder = "ambient/sprite/fires/hypno";
//        CUSTOM_OBJECT.nether_flames.maxEmitters = 1;

//        CUSTOM_OBJECT.nether_flames.setVfxSpeed(0.32f);

//        CUSTOM_OBJECT.nether_flames.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire mass slow up narrow2(0, 0);";
//        CUSTOM_OBJECT.burning_rubble.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire mass slow up narrow2(0, 0);";

    CUSTOM_OBJECT.hypnotic_flames_mass_narrow.vfxOver +=
            "ambient/sprite/fires/hypno/best/hypnofire mass slow up narrow2(0, 0);";

    CUSTOM_OBJECT.smoke.vfxOver +=
            "invert/smoke large(0, 0);";

    CUSTOM_OBJECT.power_field.setVfxSpeed(0.34f);
    CUSTOM_OBJECT.nether_flames.setVfxSpeed(0.4f);
    CUSTOM_OBJECT.burning_rubble.setVfxSpeed(0.4f);
    CUSTOM_OBJECT.smoke.setVfxSpeed(0.64f);
    CUSTOM_OBJECT.hypnotic_flames_mass_narrow.setVfxSpeed(0.74f);
    CUSTOM_OBJECT.hypnotic_flames_mass_narrow.spriteColor = new Color(1, 1, 1, 0.34f);
//        CUSTOM_OBJECT.hypnotic_flames_mass_narrow.invert_screen_vfx = true;

    CUSTOM_OBJECT.anti_flame.copy(CUSTOM_OBJECT.hypnotic_flames_mass_narrow);
    CUSTOM_OBJECT.anti_flame.invert_screen_vfx = true;
    CUSTOM_OBJECT.anti_flame.spritePath = null ;
    CUSTOM_OBJECT.smoke.invert_screen_vfx = true;


//        CUSTOM_OBJECT.hypnotic_flames_slow_up.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire slow up fine(0, 0);";
    CUSTOM_OBJECT.hypnotic_flames_slow_up.vfxOver += "ambient/sprite/fires/hypno/mass/hypnofire green slow up fine(0, 0);";

    CUSTOM_OBJECT.hypnotic_flames.vfxOver +=
            "ambient/sprite/fires/hypno/mass/hypnofire mass supernarrow(0, 0);";

    CUSTOM_OBJECT.hypnotic_flames_green.vfxOver +=
            "ambient/sprite/fires/hypno/mass/hypnofire mass supernarrow green(0, 0);";
    CUSTOM_OBJECT.hypnotic_flames_red.vfxOver +=
            "ambient/sprite/fires/hypno/mass/hypnofire mass supernarrow red(0, 0);";


    CUSTOM_OBJECT.hypnotic_flames_red.always_visible = true;
    CUSTOM_OBJECT.hypnotic_flames.always_visible = true;
    CUSTOM_OBJECT.hypnotic_flames_green.always_visible = true;
    CUSTOM_OBJECT.hypnotic_flames_mass.always_visible = true;
    CUSTOM_OBJECT.hypnotic_flames_mass.spriteColor = new Color(1, 1, 1, 0.57f);
    CUSTOM_OBJECT.hypnotic_flames_mass_narrow.spriteColor = new Color(1, 1, 1, 0.57f);
    CUSTOM_OBJECT.hypnotic_flames_slow_up.always_visible = true;
    CUSTOM_OBJECT.hypnotic_flames_pale.always_visible = true;
    CUSTOM_OBJECT.hypnotic_flames_mass_narrow.always_visible = true;

    CUSTOM_OBJECT.smoke.always_visible = true;

//        CUSTOM_OBJECT.hypnotic_flames_green.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire supernarrow green(0, 0);";
//        CUSTOM_OBJECT.hypnotic_flames_pale.vfxOver += "ambient/sprite/fires/hypno/best/hypnofire pale2(0, 0);";
//        CUSTOM_OBJECT.hypnotic_flames.vfxFolderUnder = "ambient/sprite/fires/hypno/best";

    //portal.  ambient/sprite/swarm of light

//        CUSTOM_OBJECT.nether_flames.vfxOver += "spell/nether/soul flame ambi(0, 0);";
//        CUSTOM_OBJECT.burning_rubble.vfxOver += "spell/nether/toxic fumes(0, 0);";

    CUSTOM_OBJECT.crematory.vfxOver += "ambient/sprite/fires/real fire2(0, -40);";
    CUSTOM_OBJECT.crematory.setVfxSpeed(0.14f);

    CUSTOM_OBJECT.black_waters.vfxOver += "advanced/ambi/black water square small slow(-21, -21);";
    CUSTOM_OBJECT.black_waters.setVfxSpeed(0.6f);

//        CUSTOM_OBJECT.force_field.vfxOver += "spell/nether/soul flame ambi(0, 0);";
//        CUSTOM_OBJECT.force_field.vfxOver += "spell/nether/toxic fumes(0, 0);";
    CUSTOM_OBJECT.force_field.setVfxSpeed(0.34f);
    CUSTOM_OBJECT.force_field.always_visible = true;
    CUSTOM_OBJECT.smoke.always_visible = true;
//        CUSTOM_OBJECT.force_field.attach=true;
//        CUSTOM_OBJECT.force_field.maxEmitters=1;
//        CUSTOM_OBJECT.force_field.vfxChance=0.12f;
//        CUSTOM_OBJECT.force_field.vfxFolderOver ="spell/weave";
//        CUSTOM_OBJECT.force_field.screen=true;
    CUSTOM_OBJECT.force_field.blending = GenericEnums.BLENDING.INVERT_SCREEN;

//        CUSTOM_OBJECT.power_field.vfxOver ="spell/weave/nether weave(0, -0)";
    CUSTOM_OBJECT.power_field.vfxOver = "ambient/sprite/swarm of light(0, -0)";
    //portal.  ambient/sprite/swarm of light
    CUSTOM_OBJECT.power_field.always_visible = true;

    CUSTOM_OBJECT.fire_light.attach = true;
    CUSTOM_OBJECT.LEVIATHAN.attach = true;
    CUSTOM_OBJECT.LEVIATHAN.always_visible = true;
    CUSTOM_OBJECT.LEVIATHAN.blending = GenericEnums.BLENDING.INVERT_SCREEN;
    CUSTOM_OBJECT.BIG_CLAW.blending = GenericEnums.BLENDING.INVERT_SCREEN;
    CUSTOM_OBJECT.BIG_CLAW.backAndForth = true;
    CUSTOM_OBJECT.SMALL_CLAW.backAndForth = true;
//        CUSTOM_OBJECT.SMALL_CLAW.blending = GenericEnums.BLENDING.INVERT_SCREEN;

    CUSTOM_OBJECT.BLACK_CHAINS.blending = GenericEnums.BLENDING.INVERT_SCREEN;
    CUSTOM_OBJECT.BLACK_CHAINS.spriteColor = new Color(0.6f, 1, 0.4f, 0.4f);

    CUSTOM_OBJECT.LEVIATHAN.vfxOver += "advanced/ambi/black water square small slow(-21, -21);";

//        CUSTOM_OBJECT.keserim.alpha = 0.6f;
    CUSTOM_OBJECT.keserim.alpha_template = GenericEnums.ALPHA_TEMPLATE.BLOOM;

    CUSTOM_OBJECT.dark_chrysalis.invert_screen_vfx = true;
    CUSTOM_OBJECT.dark_chrysalis.blending = GenericEnums.BLENDING.INVERT_SCREEN;
//        CUSTOM_OBJECT.dark_chrysalis.always_visible = true;
    CUSTOM_OBJECT.dark_chrysalis.vfxOver = "unit/bloody bleed2(-50, 45);";


    CUSTOM_OBJECT.black_tendrils.blending = GenericEnums.BLENDING.INVERT_SCREEN;
    CUSTOM_OBJECT.nether_tendrils.spriteColor = new Color(0.6f, 1, 0.4f, 1);
    CUSTOM_OBJECT.black_wings.blending = GenericEnums.BLENDING.INVERT_SCREEN;
    CUSTOM_OBJECT.black_wing.blending = GenericEnums.BLENDING.INVERT_SCREEN;

//        CUSTOM_OBJECT.black_waters.vfxFolderOver  =   "advanced/ambi/waters;";
//        CUSTOM_OBJECT.black_waters.vfxChance = 0.1f;

//        CUSTOM_OBJECT.nether_flames.movable = true;
//        CUSTOM_OBJECT.nether_flames.spriteColor = new Color(0.57f, 0.99f, 0.78f, 0.78f);
//        CUSTOM_OBJECT.nether_flames.vfxChance = 44;

//        CUSTOM_OBJECT.burning_rubble.movable = true;
//        CUSTOM_OBJECT.burning_rubble.setVfxSpeed(0.22f);
//        CUSTOM_OBJECT.burning_rubble.screen = true;
//        CUSTOM_OBJECT.burning_rubble.spriteColor = new Color(0.57f, 0.99f, 0.78f, 0.78f);
//        CUSTOM_OBJECT.burning_rubble.vfxFolderOver = "ambient/sprite/fires/rubble";
//        CUSTOM_OBJECT.burning_rubble.maxEmitters = 1;
//        CUSTOM_OBJECT.burning_rubble.vfxChance = 44;
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
//        CUSTOM_OBJECT.hypnotic_flames_green.vfxFolderUnder = "ambient/sprite/fires/hypno";
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
//        CUSTOM_OBJECT.hypnotic_flames_green.vfxFolderUnder = "ambient/sprite/fires/hypno";
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
//        CUSTOM_OBJECT.hypnotic_flames_green.vfxFolderUnder = "ambient/sprite/fires/hypno/hypnofire green2";
//        CUSTOM_OBJECT.hypnotic_flames_green.maxEmitters = 1;
}
    public enum CUSTOM_OBJECT {
        power_field(15, "", "", "", 6),
        force_field(15, "", "", "cells/gate/gate.txt", 6),
        blade(15, "", "", PathUtils.cropFirstPathSegment(SPRITES.BLADE.path), 6),

        clock(15, "", "", PathUtils.cropFirstPathSegment(SPRITES.HELL_WHEEL.path), 6,
                "blade(50, 80)",
                "blade(-50, 80)"),
        keserim(15, "", "", PathUtils.cropFirstPathSegment(SPRITES.COMMENT_KESERIM.path), 5),
        chrysalis(15, "", "", "", 5,
                "gate(0, 130)", "light( 0,  0)"),

        darkness(15, "", "", "", 5 //cells/bf/light wisp float.txt
//                ,                "black wing(-60, -30)", "black wing(60, -30)"
//                , "black tendrils(50, -80)", "black tendrils(-50, -80)"
//                , "keserim(10, -90)"
        ),

        dark_chrysalis(15, "", "", "", 5,
                "black wing(-50, 30)", "black wing(50, 30)"
                , "black tendrils(50, -60)", "black tendrils(-50, -60)"),

        black_tendrils(12, "", "", "unit/white tent.txt", 5),
        nether_tendrils(12, "", "", "unit/white tent.txt", 5),
        black_wing(13, "", "", "unit/wings.txt", 5),
        bone_wing(13, "", "", "unit/wings.txt", 5),

        black_wings(13, "", "", "unit/wings.txt", 5,
                "black wing(-70, 30)", "black wing(70, 30)"),
        bone_wings(13, "", "", "unit/wings.txt", 5,
                "bone wing(-70, 30)", "bone wing(70, 30)"),

        flames(15, "", "", "", 5),
        smoke(15, "", "", "", 5),
        hypnotic_flames_mass(15, "", "", "cells/bf/fire light.txt", 5),
        hypnotic_flames_mass_narrow(15, "", "", "cells/bf/fire light.txt", 5),
        hypnotic_flames_slow_up(15, "", "", "", 5),
        hypnotic_flames_green(15, "", "", "", 5),
        hypnotic_flames_red(15, "", "", "", 5),
        hypnotic_flames_pale(15, "", "", "", 5),
        hypnotic_flames(15, "", "", "", 5),
        anti_flame(15, "", "", "", 5),


        fire_light(15, "", "", "cells/bf/fire light.txt", 5),
        nether_flames(15, "", "", "", 5),
        burning_rubble(15, "", "", "", 5),


        wisp_floating(15, "", "", "cells/bf/light wisp float.txt", 5),
        crematory(15, "", "", "", 5),
        soul_net(15, "", "", "", 5),
        black_waters(15, "", "", "cells/ambi/black waters.txt", 5),
        BLACKNESS(15, "", "", "", 5),
        LIGHT(15, "", "", "cells/bf/fire light.txt", 5),
        GATE(15, "", "", "cells/gate/gate.txt", 5),
        //        LIGHT(13, "", "", "grid/gate.txt", 5, "gate pillar(-150, -100)", "gate pillar(150, -100)"),
        GATE_PILLAR(10, "", "", "cells/gate/pillar.txt", 10),

        BIG_CLAW(15, "", "", "unit/claw big.txt", 10),
        SMALL_CLAW(15, "", "", "unit/small claw.txt", 10),
        LEVIATHAN(15, "", "", "", 10,
                "BIG_CLAW(150, 30)",
                "BIG_CLAW( 0, 30)",
                "BIG_CLAW(150, 110)",
                "BIG_CLAW( 0, 110)"),
        SMALL_LEVIATHAN(15, "", "", "", 10,
                "SMALL_CLAW(120, 30)",
                "SMALL_CLAW( 15, 30)"),
//                "SMALL_CLAW(120, -10)",
//                        "SMALL_CLAW( 15, -10)"),
//                "SMALL_CLAW(120, 20)",
//                "SMALL_CLAW( 15, 20)"
        BLACK_CHAINS(15, "", "", "cells/gate/gate.txt", 5);

        public boolean vfxUnderMirrorX;
        public boolean vfxUnderMirrorY;
        public boolean vfxOverMirrorX;
        public boolean vfxOverMirrorY;

        public Color spriteColor;
        public float vfxChance = 100;
        public boolean movable;

        public boolean invert_screen_vfx;
        //BLENDING
        public boolean invert_screen;
        public boolean screen;
        public boolean ignore_linked_visible;
        public GenericEnums.ALPHA_TEMPLATE alpha_template;
        public boolean attach;
        public double range;
        public int fps;
        public boolean backAndForth;
        public int maxEmitters;
        public String vfxFolderUnder;
        public String vfxFolderOver;
        public String vfxUnder = "";
        public String vfxOver = "";
        public GenericEnums.BLENDING blending= GenericEnums.BLENDING.SCREEN;
        private float vfxSpeed = 1f;
        public boolean always_visible;
        public String spritePath;
        public String[] additionalObjects;

        CUSTOM_OBJECT(int fps, String vfxUnder, String vfxOver, String spritePath, double range, String... additionalObjects) {
            this.range = range;
            this.fps = fps;
            this.vfxUnder = vfxUnder;
            this.vfxOver = vfxOver;
            if (!spritePath.isEmpty()) {
                this.spritePath = PathFinder.getSpritesPath() + spritePath;
            }
            this.additionalObjects = additionalObjects;
        }

        public float getVfxSpeed() {
            return vfxSpeed;
        }

        public void setVfxSpeed(float vfxSpeed) {
            this.vfxSpeed = vfxSpeed;
        }

        public void copy(CUSTOM_OBJECT c) {
            vfxUnder = c.vfxUnder;
            vfxOver = c.vfxOver;
            vfxSpeed = c.vfxSpeed;
            always_visible = c.always_visible;
            spritePath = c.spritePath;
            additionalObjects = c.additionalObjects;
            fps = c.fps;
            backAndForth = c.backAndForth;
        }
    }

    public enum BF_OBJECT_GROUP {
        WALL, COLUMNS, RUINS, CONSTRUCT, GATEWAY, GRAVES,

        WINDOWS, MAGICAL, HANGING, INTERIOR, STATUES, CONJURATE,

        KEY, LOCK, ENTRANCE, TRAP, DOOR, LIGHT_EMITTER, CONTAINER, TREASURE,

        DUNGEON, WATER, TREES, ROCKS, VEGETATION, REMAINS, CRYSTAL,
        ;
    }

    public enum BF_OBJECT_SIZE {

        TINY, SMALL, MEDIUM, LARGE, HUGE
    }

    public enum BF_OBJECT_TAGS {
        INDESTRUCTIBLE,
        PASSABLE,
        SUMMONED,
        COLLAPSABLE,
        ASSYMETRICAL,
        LANDSCAPE,
        OVERLAYING,
        CONSUMABLE,
        WATER,
        ITEM,
        HUGE,
        PUSHABLE,
        LARGE,
        WALL
    }

    public enum BF_OBJECT_TYPE {
        NATURAL, STRUCTURE, PROP, SPECIAL
    }

    public enum BF_OBJ_MATERIAL {
        RED_OAK,
        IRONWOOD,
        BLACKWOOD,
        PALEWOOD,
        BILEWOOD,
        WAILWOOD,
        FEYWOOD,
        COTTON,
        SILK,
        IVORY,
        BLACK_BONE,
        MAN_BONE,
        DRAGON_BONE,
        THIN_LEATHER,
        TOUGH_LEATHER,
        THICK_LEATHER,
        FUR,
        LIZARD_SKIN,
        TROLL_SKIN,
        DRAGONHIDE,
        GRANITE,
        MARBLE,
        ONYX,
        OBSIDIAN,
        CRYSTAL,
        SOULSTONE,
        STAR_EMBER,
        SILVER,
        GOLD,
        COPPER,
        BRASS,
        BRONZE,
        IRON,
        STEEL,
        MITHRIL,
        PLATINUM,
        ADAMANTIUM,
        METEORITE,
        BRIGHT_STEEL,
        DEFILED_STEEL,
        DARK_STEEL,
        WRAITH_STEEL,
        PALE_STEEL,
        WARP_STEEL,
        DEMON_STEEL,
        MOON_SILVER,
        ELDRITCH_STEEL
    }

    public enum BF_OBJ_QUALITY {
        TOUGH,
        CRUMBLING,
        BRITTLE,
        DURABLE,
        RESISTANT,
        ARMORED,
        THICK,
        TOUGH_II,
        CRUMBLING_II,
        BRITTLE_II,
        DURABLE_II,
        RESISTANT_II,
        ARMORED_II,
        THICK_II,
        TOUGH_III,
        CRUMBLING_III,
        BRITTLE_III,
        DURABLE_III,
        RESISTANT_III,
        ARMORED_III,
        THICK_III, WATER,
    }

    public enum BF_OBJ_TYPES implements OBJ_TYPE_ENUM {
        AETHER_FONT,
        EYE_OF_THE_WARP,
        AETHER_SPHERE,
        ARCANE_SIGIL,
        HOLY_SIGIL,
        CHAOS_SIGIL,
        SACRED_ALTAR,
        DEMON_SIGIL,
        OBLIVION_SIGIL,
        VOID_DOORWAY,
        WATCHERS_IN_THE_WALLS,
        SNAKE_TRAP,
        BALLISTA_TRAP,
        ALCHEMIST_SET,
        ALCHEMIST_LABORATORY,
        WIZARDRY_TOOLS,
        WIZARD_TABLE,
        WIZARD_CLOSET,
        WEAPONS_RACK,
        ARMOR_SUIT,
        ARMOR_STAND,
        ARMORY_WALL,
        STONE_KNIGHT,
        DEMON_STATUE,
        ANGEL_STATUE,
        OCCULT_STATUE,
        LIBRARY_SHELF,
        BOOK_SHELF,
        SKULL_PILE,
        GOLDEN_KNIGHT,
        ALCHEMICAL_DEVICE,
        IRON_KNIGHT,
        STUDY_TABLE,
        BATTLE_REMAINS,
        ALCHEMY_ENGINE,
        ANVIL,
        FORGE,
        WIZARD_TRINKETS,
        MYSTIC_POOL,
        TORTURE_CHAIR,
        OVEN,
        LIGHTBRINGER,
        DECEIVER,
        TWILIGHT_ANGEL,
        DARK_ONE,
        SOULGEM,
        SILVER_KNIGHT,
        GARGOYLE_STATUE,
        GARGOYLE_GUARDIAN,
        MARBLE_GARGOYLE,
        GEAR_MECHANISM,
        STEAM_ENGINE,
        WALL_GEARS,
        TAVERN_TABLE,
        GAMBLING_TABLE,
        HUNG_SKELETON,
        CLOCKWORK_DEVICE,
        IRON_GRID,
        DARK_PENDULUM,
        DARK_ANGEL_STATUE,
        EXPERIMENT_DEVICE,
        KNIGHTLY_ARMOR,
        KNIGHTLY_WEAPONS,
        SWORD_STAND,
        ARMORY_STAND,
        TORTURE_DEVICE,
        RACK,
        DWARF_STATUE,
        INSIGNIA,
        DRAIN,
        SEWER_BARS,
        TUNNEL_GRID,
        STONE_ARCH,
        SEWER,
        WATER_CHANNEL,
        FLOOR_GRID,
        WOODEN_TABLE,
        WOODEN_BENCH,
        SNAKE_CARVING,
        ELDER_STATUE,
        KNIGHTUNICODE39CODEENDS_SHIELD,
        LIBRARY_WALL,
        BOOKCASE,
        MOSSY_STATUE,
        FORGOTTEN_GOD,
        IMPALED_SKULL,
        ELVEN_STATUE,
        CROSSBOW_STAND,
        WEAPON_CLOSET,
        CLOSET,
        RUNE_INSCRIPTION,
        KNIGHT_EMBLEM,
        DARK_EMBLEM,
        WALL_SHIELD,
        HUNTER_TROPHY,
        HANGING_SWORDS,
        HANGING_SHIELD,
        GRAVESTONE,
        FRESH_GRAVE,
        DESECRATED_GRAVE,
        ALTAR,
        DRAGON_ALTAR,
        SEALED_SARCOPHAGUS,
        SARCOPHAGUS,
        COFFIN,
        OVERGROWN_GRAVE,
        OVERGROWN_TOMBSTONE,
        TOMBSTONE,
        NOBLE_GRAVESTONE,
        CRUSADER_SHIELD,
        TITAN_HEAD,
        DEVIL_STATUE,
        FALLEN_STATUE,
        BROKEN_STATUE,
        GLOWING_GLYPH,
        MAGIC_CIRCLES,
        BROKEN_SERPENT_STATUE,
        SATYR_STATUE,
        CATHEDRAL_GARGOYLE,
        COBWEBBED_STATUE,
        ARCANE_APPARATUS,
        ARCANE_MACHINES,
        WITCH_STATUES,
        ELECTRIFIER,
        TRANSLOCATOR,
        CHARGER,
        ICICLES,
        ICE_CRUST,
        GRAIN_SACKS,
        TELESCOPE,
        GLOBE,
        CONCENTRIC_RINGS,
        ORRERY,
        ASTROLABE,
        CATHEDRAL_CLOCK,
        GEAR_MACHINE,
        ICE_SHELL,
        BED,
        DUMMY_HUNG_OBJ,
        ELDRITCH_RUNE,
        FIERY_RUNE,
        ANCIENT_RUNE,
        SWORD_RACK,
        SPEAR_RACK,
        AXE_RACK,
        HAMMER_RACK,
        HALBERT_RACK,
        GREATSWORD_RACK,
        PRISTINE_GEMSTONE,
        PRISTINE_AMETHYST,
        DESECRATED_SARCOPHAGUS,
        LORDUNICODE39CODEENDS_TOMB,
        TOMB_NICHE,
        DOOR,
        IRON_DOOR,
        STONE_DOOR,
        CEMETARY_GATE,
        CEMETARY_GATE_OPEN,
        CEMETARY_GATE_BLOCKED,
        IRON_BARS,
        VAULT_DOOR,
        BARRED_DOOR,
        HEAVY_DOOR,
        TREASURE_CHEST,
        RUSTY_CHEST,
        TREASURE_PILE,
        CRATE,
        BARRELS,
        STURDY_CHEST,
        BARREL,
        OLD_CHEST,
        SILVER_CHEST,
        IRON_CHEST,
        ARMORED_CHEST,
        DARK_TUNNEL,
        STAIRS,
        PORTAL,
        WINDING_STAIRS,
        SPIRAL_STAIRWAY,
        TOWER_STAIRS,
        DARK_ENTRANCE,
        CAVE_ENTRANCE,
        TOMB_PORTAL,
        INSECT_TUNNEL,
        STAIRCASE,
        BARROW_ENTRANCE,
        MAUSOLEUM_STAIRS,
        UPPER_STAIRS,
        HEAVY_DOORS,
        BRAZIER,
        SKULL_TORCH,
        TORCH,
        LANTERN,
        WITCHFIRE_BRAZIER,
        FIREPIT,
        BONFIRE,
        OFFERING_FIRE,
        BURNING_SKULL,
        FIERY_SKULL,
        TORCH_COLUMN,
        CANDLES,
        PRISM,
        POWER_WARDS,
        ELDRITCH_ROD,
        HANGING_BRAZIER,
        DEATH_PIT,
        ELVEN_LANTERN,
        CASTLE_WINDOW,
        ELVEN_BRAZIER,
        COBWEBBED_CRATE,
        PRESTINE_STAIRS,
        DARK_STAIRCASE,
        TEMPLE_WINDOWS,
        PRESTINE_CHEST,
        ESSENCE_VAULT,
        FOCUS_VAULT,
        EERIE_PORTAL,
        PADLOCK,
        WOODEN_DOOR,
        CRUDE_DOOR,
        DARK_DOOR,
        STONE_GATE,
        PALACE_GATE,
        CASTLE_GATE,
        ANCIENT_DOOR,
        BONE_DOOR,
        SKULL_DOOR,
        DWARVEN_DOOR,
        ORNATE_DOOR,
        ENCHANTED_ORNATE_DOOR,
        PURPLE_ENCHANTED_ORNATE_DOOR,
        RED_ENCHANTED_ORNATE_DOOR,
        BLUE_ENCHANTED_ORNATE_DOOR,
        TEAL_ENCHANTED_ORNATE_DOOR,
        DWARVEN_RUNE_DOOR,
        AMETHYST_LANTERN,
        SAPPHIRE_LANTERN,
        DIAMOND_LANTERN,
        RUBY_LANTERN,
        TOPAZ_LANTERN,
        UPWARD_STAIRS,
        DOWNWARD_STAIRS,
        WINDING_DOWNWARD_STAIRS,
        WINDING_UPWARD_STAIRS,
        DARK_WINDING_UPWARD_STAIRS,
        DARK_WINDING_DOWNWARD_STAIRS,
        WIDE_UPWARD_STAIRS,
        WIDE_DARK_UPWARD_STAIRS,
        WIDE_DOWNWARD_STAIRS,
        HANGING_WITCHFIRE_BRAZIER,
        HANGING_NETHERFLAME_BRAZIER,
        HANGING_HOLY_FIRE_BRAZIER,
        EMERALD_LANTERN,
        NETHERFLAME_BRAZIER,
        COLDFIRE_BRAZIER,
        HANGING_HELLFIRE_BRAZIER,
        GLOWING_RUNES,
        GLOWING_SILVER_RUNE,
        GLOWING_ARCANE_RUNE,
        HELLFIRE_BRAZIER,
        HOLY_FLAME_BRAZIER,
        BONE_DOOR_ENCHANTED,
        CRIMSON_DOOR,
        CAVE_EXIT,
        ASH_URN,
        ENCHANTED_ASH_URN,
        FALLEN_COLUMN,
        STONE_WALL,
        CAVE_WALL,
        DWARVEN_WALL,
        BRICK_WALL,
        ICE_WALL,
        BONE_WALL,
        SECRET_WALL,
        WOODEN_WALL,
        ICE_BLOCK,
        WOODEN_CORNER,
        WOODEN_PLANKS,
        ROTTEN_PLANKS,
        SOLID_ROCK,
        INSCRIBED_WALL,
        RUNIC_WALL,
        SCARRED_ROCK,
        TILED_WALL,
        IRON_WALL,
        RUNESTONE_WALL,
        IRON_FENCE,
        OVERGROWN_FENCE,
        DELAPIDATED_FENCE,
        FENCE,
        RUINED_WALL,
        THICKET,
        MOSS_WALL,
        THORN_WALL,
        DEAD_BRANCHES,
        OVERGROWN_WALL,
        MOSSUNICODE45CODEENDCOVERED_WALL,
        MOSSY_WALL,
        WHITE_MARBLE_WALL,
        BLACK_MARBLE_WALL,
        BLUE_MARBLE_WALL,
        WOODEN_FENCE,
        CRUMBLING_WALL,
        ORNAMENTED_WOODEN_WALL,
        FORCE_FIELD,
        MARBLE_COLUMN,
        RUINED_STRUCTURE,
        RUINED_COLUMN,
        RUINED_GATEWAY,
        OBELISK,
        TOWER,
        TOWER_OPENING,
        TENT,
        RUINED_MASONRY,
        WELL,
        COLUMN,
        ORNAMENTED_COLUMN,
        PARAPET,
        LIONHEAD_FONTAIN,
        ANCIENT_FONTAIN,
        DARK_FONTAIN,
        WATERY_ALTAR,
        GOLDEN_FOUNTAIN,
        WATERWORKS,
        WATER_RESERVOIRE,
        MONOLITH,
        FOUNDATION,
        RUNE_COLUMN,
        MEMORIAL_STONE,
        OVERGROWN_COLUMN,
        WAR_TENT,
        ARCANE_GATEWAY,
        CHAOS_GATEWAY,
        SHADOW_GATEWAY,
        LUCENT_GATEWAY,
        LIFE_GATEWAY,
        DEATH_GATEWAY,
        OBLIVION_GATE,
        NETHER_GATE,
        ABYSSAL_GATE,
        COBWEBBED_THICKET,
        COBWEBBED_COLUMN,
        DARK_COLUMN,
        CANNON,
        CRYSTAL_SHRINE,
        ARCHWAY,
        SNOWCOVERED_RUINS,
        WOODEN_ARCHWAY,
        SHACK,
        WALL_OF_SKULLS,
        OLD_STONE_WALL,
        ANCIENT_WALL,
        VOLCANIC_WALL,
        JAGGED_STONE_WALL,
        SMOOTH_STONE_WALL,
        CARVED_STONE_WALL,
        ELDRITCH_SPHERE,
        ELDRITCH_SHRINE,
        COSMIC_CRYSTAL,
        CHAOS_CRYSTAL,
        ARCANE_CRYSTAL,
        LUCENT_CRYSTAL,
        DEATH_CRYSTAL,
        DARK_CRYSTAL,
        LIFE_CRYSTAL,
        ROCKS,
        ANCIENT_OAK,
        TREE_SAPLING,
        DEAD_TREE,
        OAK,
        FALLEN_TREE,
        MOSSY_BOULDER,
        SLEEK_ROCK,
        SHRUB,
        STALAGMITE,
        STALACTITE,
        NATURAL_COLUMN,
        REMAINS,
        OLD_BONES,
        DESECRATED_REMAINS,
        SHATTERED_REMAINS,
        ICE_SPIKE,
        TREE_STUMP,
        MOSSY_ROCKS,
        FOREST_CRAGS,
        VAMPIRE_REMAINS,
        DARK_TREE,
        GIANT_TREE,
        ELDER_TREE,
        WITCH_TREE,
        GNARLED_TREE,
        MISSHAPEN_TREE,
        HAUNTED_TREE,
        OLD_TREE,
        YOUNG_OAK,
        UNDERGROUND_COLUMN,
        RUNESTONE,
        DARKENED_POND,
        TREE_ROOTS,
        MOSSY_ROOTS,
        DARK_WATER,
        WATER,
        SHALLOW_WATER,
        FUNGI_VERDE,
        TRANSLUCENT_FUNGI,
        AMETHYST,
        TOPAZ,
        RUBY,
        SAPPHIRE,
        COBWEBBED_TREE,
        COBWEBBED_BRANCHES,
        COBWEBBED_SKULL,
        FUNGI_COVER,
        FUNGI_COVERED_STUMP,
        BOVINE_SKULL,
        BROKEN_SKELETON,
        IMP_STOOL,
        ICY_SPRING,
        WATERFALL,
        ICY_BROOK,
        CHARRED_STUMP,
        BURNING_STUMP,
        DECOMPOSING_CORPSE,
        CHARRED_REMAINS,
        PUTRID_REMAINS,
        DECOMPOSING_REMAINS,
        ANCIENT_REMAINS,
        ANCIENT_SKULL,
        GIANT_MUSHROOM,
        GIANT_LUMINESCENT_MUSHROOM,
        LUMINESCENT_FUNGI,
        LILAMORD,
        FEL_FUNGI,
        YELLOW_LUMINESCENT_FUNGI,
        PURPLE_LUMINESCENT_FUNGI,
        GREEN_LUMINESCENT_FUNGI,
        ;
    }

    public enum BF_OBJ_TYPES_ implements OBJ_TYPE_ENUM {
        CASTLE_WINDOW,
        TEMPLE_WINDOWS,
        FALLEN_COLUMN,
        STONE_WALL,
        CAVE_WALL,
        DWARVEN_WALL,
        BRICK_WALL,
        ICE_WALL,
        BONE_WALL,
        SECRET_WALL,
        WOODEN_WALL,
        ICE_BLOCK,
        WOODEN_CORNER,
        WOODEN_PLANKS,
        ROTTEN_PLANKS,
        SOLID_ROCK,
        INSCRIBED_WALL,
        RUNIC_WALL,
        SCARRED_ROCK,
        TILED_WALL,
        IRON_WALL,
        RUNESTONE_WALL,
        THICKET,
        MOSS_WALL,
        THORN_WALL,
        DEAD_BRANCHES,
        OVERGROWN_WALL,
        MOSSUNICODE45CODEENDCOVERED_WALL,
        MOSSY_WALL,
        WHITE_MARBLE_WALL,
        BLACK_MARBLE_WALL,
        BLUE_MARBLE_WALL,
        WOODEN_FENCE,
        CRUMBLING_WALL,
        ORNAMENTED_WOODEN_WALL,
        FORCE_FIELD,
        COBWEBBED_THICKET,
        WALL_OF_SKULLS,
        OLD_STONE_WALL,
        ANCIENT_WALL,
        VOLCANIC_WALL,
        JAGGED_STONE_WALL,
        SMOOTH_STONE_WALL,
        CARVED_STONE_WALL,
        ELDRITCH_SPHERE,
        ELDRITCH_SHRINE,
        ;
    }

    public enum BF_OBJ_TYPES_CONTAINER implements OBJ_TYPE_ENUM {
        CRATE,
        BARRELS,
        BARREL,
        COBWEBBED_CRATE,
        ASH_URN,
        ENCHANTED_ASH_URN,
        ;
    }

    public enum BF_OBJ_TYPES_DOOR implements OBJ_TYPE_ENUM {
        DOOR,
        IRON_DOOR,
        STONE_DOOR,
        CEMETARY_GATE,
        CEMETARY_GATE_OPEN,
        CEMETARY_GATE_BLOCKED,
        IRON_BARS,
        VAULT_DOOR,
        BARRED_DOOR,
        HEAVY_DOOR,
        WOODEN_DOOR,
        CRUDE_DOOR,
        DARK_DOOR,
        STONE_GATE,
        PALACE_GATE,
        CASTLE_GATE,
        ANCIENT_DOOR,
        BONE_DOOR,
        SKULL_DOOR,
        DWARVEN_DOOR,
        ORNATE_DOOR,
        ENCHANTED_ORNATE_DOOR,
        PURPLE_ENCHANTED_ORNATE_DOOR,
        RED_ENCHANTED_ORNATE_DOOR,
        BLUE_ENCHANTED_ORNATE_DOOR,
        TEAL_ENCHANTED_ORNATE_DOOR,
        DWARVEN_RUNE_DOOR,
        BONE_DOOR_ENCHANTED,
        CRIMSON_DOOR,
        ;
    }

    public enum BF_OBJ_TYPES_DUNGEON implements OBJ_TYPE_ENUM {
        STALAGMITE,
        STALACTITE,
        NATURAL_COLUMN,
        UNDERGROUND_COLUMN,
        FUNGI_VERDE,
        TRANSLUCENT_FUNGI,
        FUNGI_COVER,
        FUNGI_COVERED_STUMP,
        IMP_STOOL,
        GIANT_MUSHROOM,
        GIANT_LUMINESCENT_MUSHROOM,
        LUMINESCENT_FUNGI,
        LILAMORD,
        FEL_FUNGI,
        YELLOW_LUMINESCENT_FUNGI,
        PURPLE_LUMINESCENT_FUNGI,
        GREEN_LUMINESCENT_FUNGI,
        ;
    }

    public enum BF_OBJ_TYPES_GEM implements OBJ_TYPE_ENUM {
        AMETHYST,
        TOPAZ,
        RUBY,
        SAPPHIRE,
        ;
    }

    public enum BF_OBJ_TYPES_GRAVES implements OBJ_TYPE_ENUM {
        GRAVESTONE,
        FRESH_GRAVE,
        DESECRATED_GRAVE,
        SEALED_SARCOPHAGUS,
        SARCOPHAGUS,
        COFFIN,
        OVERGROWN_GRAVE,
        OVERGROWN_TOMBSTONE,
        TOMBSTONE,
        NOBLE_GRAVESTONE,
        DESECRATED_SARCOPHAGUS,
        LORDUNICODE39CODEENDS_TOMB,
        TOMB_NICHE,
        ;
    }

    public enum BF_OBJ_TYPES_LIGHT_EMITTERS implements OBJ_TYPE_ENUM {
        BRAZIER,
        SKULL_TORCH,
        TORCH,
        LANTERN,
        WITCHFIRE_BRAZIER,
        FIREPIT,
        BONFIRE,
        OFFERING_FIRE,
        BURNING_SKULL,
        FIERY_SKULL,
        TORCH_COLUMN,
        CANDLES,
        PRISM,
        POWER_WARDS,
        ELDRITCH_ROD,
        HANGING_BRAZIER,
        ELVEN_LANTERN,
        ELVEN_BRAZIER,
        AMETHYST_LANTERN,
        SAPPHIRE_LANTERN,
        DIAMOND_LANTERN,
        RUBY_LANTERN,
        TOPAZ_LANTERN,
        HANGING_WITCHFIRE_BRAZIER,
        HANGING_NETHERFLAME_BRAZIER,
        HANGING_HOLY_FIRE_BRAZIER,
        EMERALD_LANTERN,
        NETHERFLAME_BRAZIER,
        COLDFIRE_BRAZIER,
        HANGING_HELLFIRE_BRAZIER,
        GLOWING_RUNES,
        GLOWING_SILVER_RUNE,
        GLOWING_ARCANE_RUNE,
        HELLFIRE_BRAZIER,
        HOLY_FLAME_BRAZIER,
        ;
    }

    public enum BF_OBJ_TYPES_LOCK implements OBJ_TYPE_ENUM {
        PADLOCK,
        ;
    }

    public enum BF_OBJ_TYPES_MAGICAL implements OBJ_TYPE_ENUM {
        AETHER_FONT,
        EYE_OF_THE_WARP,
        AETHER_SPHERE,
        ARCANE_SIGIL,
        HOLY_SIGIL,
        CHAOS_SIGIL,
        SACRED_ALTAR,
        DEMON_SIGIL,
        OBLIVION_SIGIL,
        VOID_DOORWAY,
        WATCHERS_IN_THE_WALLS,
        SNAKE_TRAP,
        BALLISTA_TRAP,
        CATHEDRAL_CLOCK,
        ;
    }

    public enum BF_OBJ_TYPES_PROP implements OBJ_TYPE_ENUM {
        ALCHEMIST_SET,
        ALCHEMIST_LABORATORY,
        WIZARDRY_TOOLS,
        WIZARD_TABLE,
        WIZARD_CLOSET,
        WEAPONS_RACK,
        ARMOR_SUIT,
        ARMOR_STAND,
        ARMORY_WALL,
        STONE_KNIGHT,
        DEMON_STATUE,
        ANGEL_STATUE,
        OCCULT_STATUE,
        LIBRARY_SHELF,
        BOOK_SHELF,
        SKULL_PILE,
        GOLDEN_KNIGHT,
        ALCHEMICAL_DEVICE,
        IRON_KNIGHT,
        STUDY_TABLE,
        BATTLE_REMAINS,
        ALCHEMY_ENGINE,
        ANVIL,
        FORGE,
        WIZARD_TRINKETS,
        MYSTIC_POOL,
        TORTURE_CHAIR,
        OVEN,
        LIGHTBRINGER,
        DECEIVER,
        TWILIGHT_ANGEL,
        DARK_ONE,
        SOULGEM,
        SILVER_KNIGHT,
        GARGOYLE_STATUE,
        GARGOYLE_GUARDIAN,
        MARBLE_GARGOYLE,
        GEAR_MECHANISM,
        STEAM_ENGINE,
        WALL_GEARS,
        TAVERN_TABLE,
        GAMBLING_TABLE,
        HUNG_SKELETON,
        CLOCKWORK_DEVICE,
        IRON_GRID,
        DARK_PENDULUM,
        DARK_ANGEL_STATUE,
        EXPERIMENT_DEVICE,
        KNIGHTLY_ARMOR,
        KNIGHTLY_WEAPONS,
        SWORD_STAND,
        ARMORY_STAND,
        TORTURE_DEVICE,
        RACK,
        DWARF_STATUE,
        INSIGNIA,
        DRAIN,
        SEWER_BARS,
        TUNNEL_GRID,
        STONE_ARCH,
        SEWER,
        WATER_CHANNEL,
        FLOOR_GRID,
        WOODEN_TABLE,
        WOODEN_BENCH,
        SNAKE_CARVING,
        ELDER_STATUE,
        KNIGHTUNICODE39CODEENDS_SHIELD,
        LIBRARY_WALL,
        BOOKCASE,
        MOSSY_STATUE,
        FORGOTTEN_GOD,
        IMPALED_SKULL,
        ELVEN_STATUE,
        CROSSBOW_STAND,
        WEAPON_CLOSET,
        CLOSET,
        RUNE_INSCRIPTION,
        KNIGHT_EMBLEM,
        DARK_EMBLEM,
        WALL_SHIELD,
        HUNTER_TROPHY,
        HANGING_SWORDS,
        HANGING_SHIELD,
        CRUSADER_SHIELD,
        TITAN_HEAD,
        DEVIL_STATUE,
        FALLEN_STATUE,
        BROKEN_STATUE,
        GLOWING_GLYPH,
        MAGIC_CIRCLES,
        BROKEN_SERPENT_STATUE,
        SATYR_STATUE,
        CATHEDRAL_GARGOYLE,
        COBWEBBED_STATUE,
        ARCANE_APPARATUS,
        ARCANE_MACHINES,
        WITCH_STATUES,
        ELECTRIFIER,
        TRANSLOCATOR,
        CHARGER,
        ICICLES,
        ICE_CRUST,
        GRAIN_SACKS,
        TELESCOPE,
        GLOBE,
        CONCENTRIC_RINGS,
        ORRERY,
        ASTROLABE,
        GEAR_MACHINE,
        ICE_SHELL,
        BED,
        DUMMY_HUNG_OBJ,
        ELDRITCH_RUNE,
        FIERY_RUNE,
        ANCIENT_RUNE,
        SWORD_RACK,
        SPEAR_RACK,
        AXE_RACK,
        HAMMER_RACK,
        HALBERT_RACK,
        GREATSWORD_RACK,
        PRISTINE_GEMSTONE,
        PRISTINE_AMETHYST,
        ;
    }

    public enum BF_OBJ_TYPES_REMAINS implements OBJ_TYPE_ENUM {
        REMAINS,
        OLD_BONES,
        DESECRATED_REMAINS,
        SHATTERED_REMAINS,
        VAMPIRE_REMAINS,
        COBWEBBED_SKULL,
        BOVINE_SKULL,
        BROKEN_SKELETON,
        DECOMPOSING_CORPSE,
        CHARRED_REMAINS,
        PUTRID_REMAINS,
        DECOMPOSING_REMAINS,
        ANCIENT_REMAINS,
        ANCIENT_SKULL,
        ;
    }

    public enum BF_OBJ_TYPES_ROCKS implements OBJ_TYPE_ENUM {
        ROCKS,
        MOSSY_BOULDER,
        SLEEK_ROCK,
        ICE_SPIKE,
        MOSSY_ROCKS,
        FOREST_CRAGS,
        RUNESTONE,
        ;
    }

    public enum BF_OBJ_TYPES_RUINS implements OBJ_TYPE_ENUM {
        RUINED_WALL,
        RUINED_STRUCTURE,
        RUINED_COLUMN,
        RUINED_GATEWAY,
        RUINED_MASONRY,
        SNOWCOVERED_RUINS,
        ;
    }

    public enum BF_OBJ_TYPES_STANDARD implements OBJ_TYPE_ENUM {
        ALTAR,
        DRAGON_ALTAR,
        DARK_TUNNEL,
        STAIRS,
        PORTAL,
        WINDING_STAIRS,
        SPIRAL_STAIRWAY,
        TOWER_STAIRS,
        DARK_ENTRANCE,
        CAVE_ENTRANCE,
        TOMB_PORTAL,
        INSECT_TUNNEL,
        STAIRCASE,
        BARROW_ENTRANCE,
        MAUSOLEUM_STAIRS,
        UPPER_STAIRS,
        HEAVY_DOORS,
        DEATH_PIT,
        PRESTINE_STAIRS,
        DARK_STAIRCASE,
        EERIE_PORTAL,
        UPWARD_STAIRS,
        DOWNWARD_STAIRS,
        WINDING_DOWNWARD_STAIRS,
        WINDING_UPWARD_STAIRS,
        DARK_WINDING_UPWARD_STAIRS,
        DARK_WINDING_DOWNWARD_STAIRS,
        WIDE_UPWARD_STAIRS,
        WIDE_DARK_UPWARD_STAIRS,
        WIDE_DOWNWARD_STAIRS,
        CAVE_EXIT,
        MARBLE_COLUMN,
        OBELISK,
        COLUMN,
        ORNAMENTED_COLUMN,
        PARAPET,
        LIONHEAD_FONTAIN,
        ANCIENT_FONTAIN,
        DARK_FONTAIN,
        WATERY_ALTAR,
        GOLDEN_FOUNTAIN,
        WATERWORKS,
        WATER_RESERVOIRE,
        MONOLITH,
        FOUNDATION,
        RUNE_COLUMN,
        MEMORIAL_STONE,
        OVERGROWN_COLUMN,
        ARCANE_GATEWAY,
        CHAOS_GATEWAY,
        SHADOW_GATEWAY,
        LUCENT_GATEWAY,
        LIFE_GATEWAY,
        DEATH_GATEWAY,
        OBLIVION_GATE,
        NETHER_GATE,
        ABYSSAL_GATE,
        COBWEBBED_COLUMN,
        DARK_COLUMN,
        CANNON,
        CRYSTAL_SHRINE,
        ARCHWAY,
        WOODEN_ARCHWAY,
        SHACK,
        COSMIC_CRYSTAL,
        CHAOS_CRYSTAL,
        ARCANE_CRYSTAL,
        LUCENT_CRYSTAL,
        DEATH_CRYSTAL,
        DARK_CRYSTAL,
        LIFE_CRYSTAL,
        SHRUB,
        DARKENED_POND,
        TREE_ROOTS,
        MOSSY_ROOTS,
        DARK_WATER,
        WATER,
        SHALLOW_WATER,
        ICY_SPRING,
        WATERFALL,
        ICY_BROOK,
        ;
    }

    public enum BF_OBJ_TYPES_STRUCTURES implements OBJ_TYPE_ENUM {
        IRON_FENCE,
        OVERGROWN_FENCE,
        DELAPIDATED_FENCE,
        FENCE,
        TOWER,
        TOWER_OPENING,
        TENT,
        WELL,
        WAR_TENT,
        ;
    }

    public enum BF_OBJ_TYPES_TREASURE implements OBJ_TYPE_ENUM {
        TREASURE_CHEST,
        RUSTY_CHEST,
        TREASURE_PILE,
        STURDY_CHEST,
        OLD_CHEST,
        SILVER_CHEST,
        IRON_CHEST,
        ARMORED_CHEST,
        PRESTINE_CHEST,
        ESSENCE_VAULT,
        FOCUS_VAULT,
        ;
    }

    public enum BF_OBJ_TYPES_TREES implements OBJ_TYPE_ENUM {
        ANCIENT_OAK,
        TREE_SAPLING,
        DEAD_TREE,
        OAK,
        FALLEN_TREE,
        TREE_STUMP,
        DARK_TREE,
        GIANT_TREE,
        ELDER_TREE,
        WITCH_TREE,
        GNARLED_TREE,
        MISSHAPEN_TREE,
        HAUNTED_TREE,
        OLD_TREE,
        YOUNG_OAK,
        COBWEBBED_TREE,
        COBWEBBED_BRANCHES,
        CHARRED_STUMP,
        BURNING_STUMP,
        ;
    }

    public enum BF_OBJ_WEIGHT {
        TINY,

        COLOSSAL,
    }

    // SUBTYPES


    public enum BF_OBJ_SUB_TYPES_MAGICAL implements OBJ_TYPE_ENUM {
        AETHER_FONT,
        EYE_OF_THE_WARP,
        AETHER_SPHERE,
        ARCANE_SIGIL,
        HOLY_SIGIL,
        CHAOS_SIGIL,
        SACRED_ALTAR,
        DEMON_SIGIL,
        OBLIVION_SIGIL,
        VOID_DOORWAY,
        WATCHERS_IN_THE_WALLS,
        SNAKE_TRAP,
        BALLISTA_TRAP,
        ALCHEMIST_SET,
        ALCHEMIST_LABORATORY,
        WIZARDRY_TOOLS,
        WIZARD_CLOSET,
        ALCHEMICAL_DEVICE,
        ALCHEMY_ENGINE,
        WIZARD_TRINKETS,
        MYSTIC_POOL,
        SOULGEM,
        ALTAR,
        DRAGON_ALTAR,
        CATHEDRAL_CLOCK,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_INTERIOR implements OBJ_TYPE_ENUM {
        WIZARD_TABLE,
        ARMOR_SUIT,
        ARMOR_STAND,
        ARMORY_WALL,
        LIBRARY_SHELF,
        BOOK_SHELF,
        STUDY_TABLE,
        ANVIL,
        FORGE,
        TORTURE_CHAIR,
        OVEN,
        TAVERN_TABLE,
        GAMBLING_TABLE,
        KNIGHTLY_ARMOR,
        ARMORY_STAND,
        TORTURE_DEVICE,
        RACK,
        WOODEN_TABLE,
        WOODEN_BENCH,
        LIBRARY_WALL,
        BOOKCASE,
        WEAPON_CLOSET,
        CLOSET,
        HUNTER_TROPHY,
        GRAIN_SACKS,
        BED,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_CONTAINER implements OBJ_TYPE_ENUM {
        WEAPONS_RACK,
        SWORD_STAND,
        SWORD_RACK,
        SPEAR_RACK,
        AXE_RACK,
        HAMMER_RACK,
        HALBERT_RACK,
        GREATSWORD_RACK,
        CRATE,
        BARRELS,
        BARREL,
        COBWEBBED_CRATE,
        ASH_URN,
        ENCHANTED_ASH_URN,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_STATUES implements OBJ_TYPE_ENUM {
        STONE_KNIGHT,
        DEMON_STATUE,
        ANGEL_STATUE,
        OCCULT_STATUE,
        GOLDEN_KNIGHT,
        IRON_KNIGHT,
        LIGHTBRINGER,
        DECEIVER,
        TWILIGHT_ANGEL,
        DARK_ONE,
        SILVER_KNIGHT,
        GARGOYLE_STATUE,
        GARGOYLE_GUARDIAN,
        MARBLE_GARGOYLE,
        DARK_ANGEL_STATUE,
        DWARF_STATUE,
        ELDER_STATUE,
        MOSSY_STATUE,
        FORGOTTEN_GOD,
        ELVEN_STATUE,
        TITAN_HEAD,
        DEVIL_STATUE,
        FALLEN_STATUE,
        BROKEN_STATUE,
        BROKEN_SERPENT_STATUE,
        SATYR_STATUE,
        CATHEDRAL_GARGOYLE,
        COBWEBBED_STATUE,
        WITCH_STATUES,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_REMAINS implements OBJ_TYPE_ENUM {
        SKULL_PILE,
        BATTLE_REMAINS,
        REMAINS,
        OLD_BONES,
        DESECRATED_REMAINS,
        SHATTERED_REMAINS,
        VAMPIRE_REMAINS,
        COBWEBBED_SKULL,
        BOVINE_SKULL,
        BROKEN_SKELETON,
        DECOMPOSING_CORPSE,
        CHARRED_REMAINS,
        PUTRID_REMAINS,
        DECOMPOSING_REMAINS,
        ANCIENT_REMAINS,
        ANCIENT_SKULL,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_MECHANICAL implements OBJ_TYPE_ENUM {
        GEAR_MECHANISM,
        STEAM_ENGINE,
        WALL_GEARS,
        CLOCKWORK_DEVICE,
        DARK_PENDULUM,
        EXPERIMENT_DEVICE,
        ARCANE_APPARATUS,
        ARCANE_MACHINES,
        ELECTRIFIER,
        TRANSLOCATOR,
        CHARGER,
        TELESCOPE,
        GLOBE,
        CONCENTRIC_RINGS,
        ORRERY,
        GEAR_MACHINE,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_GRAVES implements OBJ_TYPE_ENUM {
        HUNG_SKELETON,
        GRAVESTONE,
        FRESH_GRAVE,
        DESECRATED_GRAVE,
        SEALED_SARCOPHAGUS,
        SARCOPHAGUS,
        COFFIN,
        OVERGROWN_GRAVE,
        OVERGROWN_TOMBSTONE,
        TOMBSTONE,
        NOBLE_GRAVESTONE,
        DESECRATED_SARCOPHAGUS,
        LORDUNICODE39CODEENDS_TOMB,
        TOMB_NICHE,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_ implements OBJ_TYPE_ENUM {
        IRON_GRID,
        STONE_ARCH,
        IMPALED_SKULL,
        WAR_TENT,
        SHRUB,
        DARKENED_POND,
        TREE_ROOTS,
        MOSSY_ROOTS,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_HANGING implements OBJ_TYPE_ENUM {
        KNIGHTLY_WEAPONS,
        INSIGNIA,
        TUNNEL_GRID,
        SNAKE_CARVING,
        KNIGHTUNICODE39CODEENDS_SHIELD,
        RUNE_INSCRIPTION,
        KNIGHT_EMBLEM,
        DARK_EMBLEM,
        WALL_SHIELD,
        HANGING_SWORDS,
        HANGING_SHIELD,
        CRUSADER_SHIELD,
        GLOWING_GLYPH,
        MAGIC_CIRCLES,
        ICICLES,
        ICE_CRUST,
        ASTROLABE,
        ICE_SHELL,
        DUMMY_HUNG_OBJ,
        ELDRITCH_RUNE,
        FIERY_RUNE,
        ANCIENT_RUNE,
        PRISTINE_GEMSTONE,
        PRISTINE_AMETHYST,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_WATER implements OBJ_TYPE_ENUM {
        DRAIN,
        SEWER_BARS,
        SEWER,
        WATER_CHANNEL,
        FLOOR_GRID,
        WATERY_ALTAR,
        WATERWORKS,
        WATER_RESERVOIRE,
        DARK_WATER,
        WATER,
        SHALLOW_WATER,
        ICY_SPRING,
        WATERFALL,
        ICY_BROOK,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_ENTRANCE implements OBJ_TYPE_ENUM {
        CROSSBOW_STAND,
        DARK_TUNNEL,
        STAIRS,
        PORTAL,
        WINDING_STAIRS,
        SPIRAL_STAIRWAY,
        TOWER_STAIRS,
        DARK_ENTRANCE,
        CAVE_ENTRANCE,
        TOMB_PORTAL,
        INSECT_TUNNEL,
        STAIRCASE,
        BARROW_ENTRANCE,
        MAUSOLEUM_STAIRS,
        UPPER_STAIRS,
        HEAVY_DOORS,
        PRESTINE_STAIRS,
        DARK_STAIRCASE,
        EERIE_PORTAL,
        UPWARD_STAIRS,
        DOWNWARD_STAIRS,
        WINDING_DOWNWARD_STAIRS,
        WINDING_UPWARD_STAIRS,
        DARK_WINDING_UPWARD_STAIRS,
        DARK_WINDING_DOWNWARD_STAIRS,
        WIDE_UPWARD_STAIRS,
        WIDE_DARK_UPWARD_STAIRS,
        WIDE_DOWNWARD_STAIRS,
        CAVE_EXIT,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_DOOR implements OBJ_TYPE_ENUM {
        DOOR,
        IRON_DOOR,
        STONE_DOOR,
        CEMETARY_GATE,
        IRON_BARS,
        VAULT_DOOR,
        BARRED_DOOR,
        HEAVY_DOOR,
        WOODEN_DOOR,
        CRUDE_DOOR,
        DARK_DOOR,
        STONE_GATE,
        ANCIENT_DOOR,
        BONE_DOOR,
        SKULL_DOOR,
        DWARVEN_DOOR,
        ORNATE_DOOR,
        ENCHANTED_ORNATE_DOOR,
        PURPLE_ENCHANTED_ORNATE_DOOR,
        RED_ENCHANTED_ORNATE_DOOR,
        BLUE_ENCHANTED_ORNATE_DOOR,
        TEAL_ENCHANTED_ORNATE_DOOR,
        DWARVEN_RUNE_DOOR,
        BONE_DOOR_ENCHANTED,
        CRIMSON_DOOR,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_GATEWAY implements OBJ_TYPE_ENUM {
        CEMETARY_GATE_OPEN,
        CEMETARY_GATE_BLOCKED,
        PALACE_GATE,
        CASTLE_GATE,
        ARCANE_GATEWAY,
        CHAOS_GATEWAY,
        SHADOW_GATEWAY,
        LUCENT_GATEWAY,
        LIFE_GATEWAY,
        DEATH_GATEWAY,
        OBLIVION_GATE,
        NETHER_GATE,
        ABYSSAL_GATE,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_TREASURE implements OBJ_TYPE_ENUM {
        TREASURE_CHEST,
        RUSTY_CHEST,
        TREASURE_PILE,
        STURDY_CHEST,
        OLD_CHEST,
        SILVER_CHEST,
        IRON_CHEST,
        ARMORED_CHEST,
        PRESTINE_CHEST,
        ESSENCE_VAULT,
        FOCUS_VAULT,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_LIGHT_EMITTER implements OBJ_TYPE_ENUM {


        NETHERFLAME_BRAZIER,
        COLDFIRE_BRAZIER,
        HELLFIRE_BRAZIER,
        HOLY_FLAME_BRAZIER,
        WITCHFIRE_BRAZIER,
        ELVEN_BRAZIER,

        BRAZIER,
        FIREPIT,
        BONFIRE,
        OFFERING_FIRE,
        FIERY_SKULL,
        TORCH_COLUMN,
        PRISM,
        POWER_WARDS,
        ELDRITCH_ROD,

        LANTERN,
        HANGING_WITCHFIRE_BRAZIER,
        HANGING_NETHERFLAME_BRAZIER,
        HANGING_HOLY_FIRE_BRAZIER,
        HANGING_HELLFIRE_BRAZIER,
        HANGING_COLDFIRE_BRAZIER,

        CANDLES,
        BURNING_SKULL,
        GLOWING_RUNES,
        GLOWING_SILVER_RUNE,
        GLOWING_ARCANE_RUNE,
        EMERALD_LANTERN,
        SKULL_TORCH,
        TORCH,
        HANGING_BRAZIER,
        ELVEN_LANTERN,
        AMETHYST_LANTERN,
        SAPPHIRE_LANTERN,
        DIAMOND_LANTERN,
        RUBY_LANTERN,
        TOPAZ_LANTERN,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_TRAP implements OBJ_TYPE_ENUM {
        DEATH_PIT,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_WINDOWS implements OBJ_TYPE_ENUM {
        CASTLE_WINDOW,
        TEMPLE_WINDOWS,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_LOCK implements OBJ_TYPE_ENUM {
        PADLOCK,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_COLUMNS implements OBJ_TYPE_ENUM {
        FALLEN_COLUMN,
        MARBLE_COLUMN,
        COLUMN,
        ORNAMENTED_COLUMN,
        RUNE_COLUMN,
        OVERGROWN_COLUMN,
        COBWEBBED_COLUMN,
        DARK_COLUMN,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_WALL implements OBJ_TYPE_ENUM {
        STONE_WALL,
        CAVE_WALL,
        DWARVEN_WALL,
        BRICK_WALL,
        ICE_WALL,
        BONE_WALL,
        SECRET_WALL,
        WOODEN_WALL,
        ICE_BLOCK,
        WOODEN_CORNER,
        WOODEN_PLANKS,
        ROTTEN_PLANKS,
        SOLID_ROCK,
        INSCRIBED_WALL,
        RUNIC_WALL,
        SCARRED_ROCK,
        TILED_WALL,
        IRON_WALL,
        RUNESTONE_WALL,
        IRON_FENCE,
        OVERGROWN_FENCE,
        DELAPIDATED_FENCE,
        FENCE,
        THICKET,
        MOSS_WALL,
        THORN_WALL,
        DEAD_BRANCHES,
        OVERGROWN_WALL,
        MOSSUNICODE45CODEENDCOVERED_WALL,
        MOSSY_WALL,
        WHITE_MARBLE_WALL,
        BLACK_MARBLE_WALL,
        BLUE_MARBLE_WALL,
        WOODEN_FENCE,
        CRUMBLING_WALL,
        ORNAMENTED_WOODEN_WALL,
        COBWEBBED_THICKET,
        WALL_OF_SKULLS,
        OLD_STONE_WALL,
        ANCIENT_WALL,
        VOLCANIC_WALL,
        JAGGED_STONE_WALL,
        SMOOTH_STONE_WALL,
        CARVED_STONE_WALL,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_RUINS implements OBJ_TYPE_ENUM {
        RUINED_WALL,
        RUINED_STRUCTURE,
        RUINED_COLUMN,
        RUINED_GATEWAY,
        RUINED_MASONRY,
        SNOWCOVERED_RUINS,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_CONJURATE implements OBJ_TYPE_ENUM {
        FORCE_FIELD,
        ELDRITCH_SPHERE,
        ELDRITCH_SHRINE,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_CONSTRUCT implements OBJ_TYPE_ENUM {
        OBELISK,
        TOWER,
        TOWER_OPENING,
        TENT,
        WELL,
        PARAPET,
        LIONHEAD_FONTAIN,
        ANCIENT_FONTAIN,
        DARK_FONTAIN,
        GOLDEN_FOUNTAIN,
        MONOLITH,
        FOUNDATION,
        MEMORIAL_STONE,
        CANNON,
        CRYSTAL_SHRINE,
        ARCHWAY,
        WOODEN_ARCHWAY,
        SHACK,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_CRYSTAL implements OBJ_TYPE_ENUM {
        COSMIC_CRYSTAL,
        CHAOS_CRYSTAL,
        ARCANE_CRYSTAL,
        LUCENT_CRYSTAL,
        DEATH_CRYSTAL,
        DARK_CRYSTAL,
        LIFE_CRYSTAL,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_ROCKS implements OBJ_TYPE_ENUM {
        ROCKS,
        MOSSY_BOULDER,
        SLEEK_ROCK,
        ICE_SPIKE,
        MOSSY_ROCKS,
        FOREST_CRAGS,
        RUNESTONE,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_TREES implements OBJ_TYPE_ENUM {
        ANCIENT_OAK,
        TREE_SAPLING,
        DEAD_TREE,
        OAK,
        FALLEN_TREE,
        TREE_STUMP,
        DARK_TREE,
        GIANT_TREE,
        ELDER_TREE,
        WITCH_TREE,
        GNARLED_TREE,
        MISSHAPEN_TREE,
        HAUNTED_TREE,
        OLD_TREE,
        YOUNG_OAK,
        COBWEBBED_TREE,
        COBWEBBED_BRANCHES,
        CHARRED_STUMP,
        BURNING_STUMP,
        ;
    }

    public enum BF_OBJ_SUB_TYPES_DUNGEON implements OBJ_TYPE_ENUM {
        STALAGMITE,
        STALACTITE,
        NATURAL_COLUMN,
        UNDERGROUND_COLUMN,
        FUNGI_VERDE,
        TRANSLUCENT_FUNGI,
        AMETHYST,
        TOPAZ,
        RUBY,
        SAPPHIRE,
        FUNGI_COVER,
        FUNGI_COVERED_STUMP,
        IMP_STOOL,
        GIANT_MUSHROOM,
        GIANT_LUMINESCENT_MUSHROOM,
        LUMINESCENT_FUNGI,
        LILAMORD,
        FEL_FUNGI,
        YELLOW_LUMINESCENT_FUNGI,
        PURPLE_LUMINESCENT_FUNGI,
        GREEN_LUMINESCENT_FUNGI,
        ;
    }

    public enum TEXTURES {
        CHAIN,
        ROCK,
        BONES,
        CRACK,
        ROOT,
        BRANCH,
        BLOOD,
        RUST,
        crystals, jagged,
        debris,ruins,
        cobweb,
        glyph,
        rune, //glowing on the floor!

        wall, //on top?
        pipe,

        ;

        public String path;

        TEXTURES() {
            this.path = (PathFinder.getTexturesPath() + toString() + ".png");
        }

        TEXTURES(String path) {
            this.path = path;
        }
    }
        public enum SPRITES {

        HERO_KESERIM("sprites/hero/keserim2.txt"),
        COMMENT_KESERIM("sprites/unit/comment/keserim comment.txt"),
        ALTAR("sprites/cells/bf/altar.txt"),
        FLOAT_WISP("sprites/cells/bf/light wisp float.txt"),
        FIRE_LIGHT("sprites/cells/bf/fire light.txt"),
        RUNE_INSCRIPTION("sprites/cells/bf/rune.txt"),
        ORB("sprites/cells/bf/orb.txt"),
        WATER("sprites/cells/ambi/black waters.txt"),
        VEIL("sprites/cells/gate/veil.txt"),
        HELL_WHEEL("sprites/cells/parts/underlay.txt"),
        BLADE("sprites/cells/parts/blade hand.txt"),
        TENTACLE("sprites/cells/grid/tent loop.txt"),
        WHITE_TENTACLE("sprites/unit/white tent.txt"),
        BONE_WINGS("sprites/unit/wings.txt"),
        PORTAL("sprites/cells/portal/portal loop.txt"),
        PORTAL_OPEN("sprites/cells/portal/portal open.txt"),
        PORTAL_CLOSE("sprites/cells/portal/portal close.txt"),
        BIG_CLAW_IDLE("sprites/unit/claw big.txt"),
        EMPTY("");
        public String path;

        SPRITES(String path) {
            this.path = path;
        }
    }

    public enum BF_VFX {
        fire,
        fire_green,

        smoke,
        souls,
        mist,
        ash,

        cinders,
        flies,


    }
}
