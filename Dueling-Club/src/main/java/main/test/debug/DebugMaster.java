package main.test.debug;

import main.ability.effects.AddBuffEffect;
import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.ability.effects.common.CreateObjectEffect;
import main.ability.effects.common.SummonEffect;
import main.ability.effects.oneshot.common.ModifyPropertyEffect;
import main.ability.effects.oneshot.common.OwnershipChangeEffect;
import main.client.battle.Wave;
import main.client.cc.CharacterCreator;
import main.client.cc.logic.items.ItemGenerator;
import main.client.cc.logic.spells.LibraryManager;
import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.ITEM_SLOT;
import main.content.CONTENT_CONSTS.MATERIAL;
import main.content.CONTENT_CONSTS.QUALITY_LEVEL;
import main.content.*;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.elements.conditions.Conditions;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.*;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.DC_GameState;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.DC_ObjInitializer;
import main.game.battlefield.UnitGroupMaster;
import main.game.logic.dungeon.Dungeon;
import main.game.player.DC_Player;
import main.game.player.Player;
import main.rules.DC_ActionManager;
import main.swing.builders.DC_Builder;
import main.swing.components.obj.drawing.DrawMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.swing.generic.services.dialog.EnumChooser;
import main.system.ConditionMaster;
import main.system.DC_Formulas;
import main.system.ai.AI_Manager;
import main.system.ai.GroupAI;
import main.system.ai.UnitAI;
import main.system.auxiliary.*;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.math.MathMaster;
import main.system.options.OptionsMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.test.TestMasterContent;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.PresetMaster;
import main.test.auto.AutoTestMaster;
import main.test.frontend.FAST_DC;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.Stack;

/**
 * @author JustMe
 */

public class DebugMaster {
    public static final char HOTKEY_CHAR = 'd';
    public static final char FUNCTION_HOTKEY_CHAR = 'f';

    public static boolean ALT_AI_PLAYER;
    private static boolean omnivision;
    private static boolean mapDebugOn = true;
    private static boolean altMode;
    public DEBUG_FUNCTIONS[] onStartFunctions = {DEBUG_FUNCTIONS.GOD_MODE,
            DEBUG_FUNCTIONS.SPAWN_WAVE};
    DC_HeroObj target = null;
    private String lastFunction;
    private Stack<String> executedFunctions = new Stack<>();
    private DC_Builder bf;

    // public void editAi(DC_HeroObj unit) {
    // UnitAI ai = unit.getUnitAI();
    // }
    private DC_Game game;
    private DC_GameState state;
    private DebugPanel debugPanel;
    private ObjType selectedType;
    private DC_Player altAiPlayer;
    private String lastType;
    private Obj arg;
    private String type;
    public DebugMaster(DC_GameState state, DC_Builder bf) {
        this.state = state;
        this.game = state.getGame();
        this.bf = bf;
    }

    public static boolean isOmnivisionOn() {
        return omnivision;
    }

    public static void setOmnivisionOn(boolean HACK) {
        omnivision = HACK;
    }

    public static boolean isMapDebugOn() {
        return mapDebugOn;
    }

    public static boolean isAltMode() {
        return altMode;
    }

    public static void setAltMode(boolean altMode2) {
        altMode = altMode2;
    }

    public void promptFunctionToExecute() {
        SoundMaster.playStandardSound(RandomWizard.random() ? STD_SOUNDS.DIS__OPEN_MENU
                : STD_SOUNDS.SLING);
        String message = "Input function name";
        String funcName = JOptionPane.showInputDialog(null, message, lastFunction);
        if (funcName == null) {
            if (AutoTestMaster.isRunning())
                funcName = DEBUG_FUNCTIONS.AUTO_TEST_INPUT.name();
            else {
                reset();
                return;
            }
        }
        if (AutoTestMaster.isRunning())
            if (funcName.equalsIgnoreCase("re")) {
                new Thread(new Runnable() {
                    public void run() {
                        AutoTestMaster.runTests();
                    }
                }, " thread").start();
                return;
            }
        if (funcName.contains(" ")) {
            if (funcName.trim().equals("")) {
                int length = funcName.length();
                if (executedFunctions.size() >= length) {
                    for (int i = 0; i < length; i++)
                        funcName = executedFunctions.pop();
                }
            }

        }
        if (StringMaster.isInteger(funcName)) {
            try {
                Integer integer = StringMaster.getInteger(funcName);
                if (integer >= DEBUG_FUNCTIONS.values().length) {
                    executeDebugFunctionNewThread(HIDDEN_DEBUG_FUNCTIONS.values()[integer
                            - DEBUG_FUNCTIONS.values().length]);
                    playFuncExecuteSound();
                    return;
                }
                {
                    executeDebugFunctionNewThread(DEBUG_FUNCTIONS.values()[integer]);
                    playFuncExecuteSound();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        DEBUG_FUNCTIONS function = new EnumMaster<DEBUG_FUNCTIONS>().retrieveEnumConst(
                DEBUG_FUNCTIONS.class, funcName);
        if (function != null) {
            executeDebugFunctionNewThread(function);
            playFuncExecuteSound();
            return;
        }

        HIDDEN_DEBUG_FUNCTIONS function2 = new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>()
                .retrieveEnumConst(HIDDEN_DEBUG_FUNCTIONS.class, funcName);
        if (function2 != null)
            executeDebugFunctionNewThread(function2);
        else {
            function = new EnumMaster<DEBUG_FUNCTIONS>().retrieveEnumConst(DEBUG_FUNCTIONS.class,
                    funcName, true);

            function2 = new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>().retrieveEnumConst(
                    HIDDEN_DEBUG_FUNCTIONS.class, funcName, true);
            if (StringMaster.compareSimilar(funcName, function.toString()) > StringMaster
                    .compareSimilar(funcName, function2.toString()))
                executeDebugFunctionNewThread(function);
            else
                executeDebugFunctionNewThread(function2);

        }
        playFuncExecuteSound();

    }

    private void playFuncExecuteSound() {
        SoundMaster.playStandardSound(RandomWizard.random() ? STD_SOUNDS.SKILL_LEARNED
                : STD_SOUNDS.SPELL_UPGRADE_LEARNED);
    }

    public void editAi(DC_HeroObj unit) {
        UnitAI ai = unit.getUnitAI();
        GroupAI group = ai.getGroup();
        DialogMaster.confirm("What to do with " + group);

        String TRUE = "Info";
        String FALSE = "Set Behavior";
        String NULL = "Set Parameter";
        String string = "What to do with " + group + "?";
        Boolean result = DialogMaster.askAndWait(string, TRUE, FALSE, NULL);
        main.system.auxiliary.LogMaster.log(1, " ");
        DIRECTION info = group.getWanderDirection();

        // TODO GLOBAL AI LOG LEVEL
        if (result == null) {
            AI_PARAM param = new EnumMaster<AI_PARAM>().retrieveEnumConst(AI_PARAM.class,
                    ListChooser.chooseEnum(AI_PARAM.class));
            if (param != null) {
                switch (param) {
                    case LOG_LEVEL:
                        ai.setLogLevel(DialogMaster.inputInt(ai.getLogLevel()));
                        break;
                }
            }

        }

		/*
         * display on BF: >> Direction >> Target coordinate for each unit or
		 * patrol >> Maybe even path... >>
		 *
		 *
		 */

        group.getPatrol();

    }

    public void showDebugWindow() {
        if (getDebugPanel() == null) {
            initDebugPanel();
        }
        // if (!getDebugPanel().isVisible())
        // initDebugPanel();
        getDebugPanel().getFrame().setVisible(true);
        toggleDebugPanel();
        if (game.isSimulation()) {
            getDebugPanel().refresh();
        }
    }

    private void toggleDebugPanel() {

        this.getDebugPanel().getFrame().setAlwaysOnTop(!getDebugPanel().getFrame().isAlwaysOnTop());

        if (getDebugPanel().getFrame().isAlwaysOnTop())
            getDebugPanel().getFrame().requestFocus();

    }

    private void initDebugPanel() {
        this.setDebugPanel(new DebugPanel(this));

    }

    public Object executeDebugFunction(DEBUG_FUNCTIONS func) {
        executedFunctions.push(func.toString());
        boolean transmitted = false;
        if (game.isOnline())
            if (func.transmitted) {
                transmitted = true;

            }

        DC_HeroObj infoObj = null;
        Ref ref = null;
        try {
            infoObj = (DC_HeroObj) getObj();
        } catch (Exception e) {
            infoObj = (DC_HeroObj) game.getManager().getActiveObj();
        }
        try {
            ref = new Ref(game, game.getManager().getActiveObj().getId());
        } catch (Exception e) {
        }
        Coordinates coordinate = null;
        String data = null;
        OBJ_TYPES TYPE;
        switch (func) {
            case RUN_AUTO_TESTS:
                AutoTestMaster.runTests();
                break;
            case AUTO_TEST_INPUT:
                WaitMaster.receiveInput(WAIT_OPERATIONS.AUTO_TEST_INPUT, true);
                break;
            case SET_OPTION:
                OptionsMaster.promptSetOption();
                break;
            case ADD_GROUP:
                File groupFile = ListChooser.chooseFile(PathFinder.getUnitGroupPath());
                if (groupFile == null)
                    break;
                coordinate = getGame().getBattleFieldManager().pickCoordinate();
                if (coordinate == null)
                    break;
                data = FileManager.readFile(groupFile);

                UnitGroupMaster.setCurrentGroupHeight(MathMaster.getMaxY(data));
                UnitGroupMaster.setCurrentGroupWidth(MathMaster.getMaxX(data));
                UnitGroupMaster.setMirror(isAltMode());
                // String flip = ListChooser.chooseEnum(FLIP.class);
                // if (flip != null)
                // UnitGroupMaster.setFlip(new
                // EnumMaster<FLIP>().retrieveEnumConst(FLIP.class,
                // flip));
                // else
                // UnitGroupMaster.setFlip(null);
                try {
                    DC_ObjInitializer.processObjData(game.getPlayer(isAltMode()), data, coordinate);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    UnitGroupMaster.setMirror(false);
                }

                break;
            case TOGGLE_DUMMY:
                game.setDummyMode(!game.isDummyMode());
                TestMasterContent.setForceFree(game.isDummyMode());
                break;

            case PRESET:
                PresetMaster.handlePreset(isAltMode());

                break;
            case DUNGEON_PLAN_INFO: {
                main.system.auxiliary.LogMaster.log(1, ""
                        + game.getDungeonMaster().getDungeon().getPlan());
                break;
            }
            case DUNGEON_BLOCK_INFO: {
                displayList("", game.getDungeonMaster().getDungeon().getPlan().getBlocks(), 1);
                break;
            }
            case DUNGEON_ZONES_INFO: {
                break;
            }
            case TOGGLE_DUNGEON_DEBUG: {
                mapDebugOn = !mapDebugOn;
                break;
            }
            case DUNGEON_ADD_SUBLEVEL: {
                Dungeon sublevel = null;
                game.getDungeonMaster().getDungeon().getSubLevels().add(sublevel);

                break;
            }

            case HIDDEN_FUNCTION: {
                int i = DialogMaster.optionChoice(HIDDEN_DEBUG_FUNCTIONS.values(), "...");
                if (i != -1)
                    executeHiddenDebugFunction(HIDDEN_DEBUG_FUNCTIONS.values()[i]);
                break;
            }
            case TOGGLE_AUTO_UNIT:
                if (!infoObj.isOwnedBy(game.getPlayer(true))) {
                    infoObj.setOriginalOwner(game.getPlayer(true));
                    infoObj.setOwner(game.getPlayer(true));
                } else {
                    infoObj.setAiControlled(!infoObj.isAiControlled());
                }
                WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, true);
                break;

            case ADD_DUNGEON:
                DC_Game.game.getDungeonMaster().addDungeon();

                // game.getManager().killAllUnits(isAltMode());
                // DC_Game.game.getDungeonMaster().addDungeon();
                // if (!isAltMode())
                // game.getArenaManager().getSpawnManager().spawnParty(true);
                // else {
                // game.getArenaManager().getSpawnManager().spawnParty(false);
                // }
                return func;

            case LOAD_DUNGEON:
                DC_Game.game.getDungeonMaster().reloadDungeon();

                return func;
            case PAUSE:
                DC_Game.game.setPaused(!DC_Game.game.isPaused());
                break;
            case TOGGLE_OMNIVISION:
                omnivision = !omnivision;
                break;
            case AUTO_COMBAT:
                game.getPlayer(true).setAi(!game.getPlayer(true).isAi());
                WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, true);
                break;
            case ADD_TEST_SPELLS:
                TestMasterContent.addTestActives(false, infoObj.getType(), true);
                break;
            case ADD_ALL_SPELLS:
                TestMasterContent.addTestActives(true, infoObj.getType(), true);
                break;
            case TOGGLE_GRAPHICS_TEST:
                DrawMaster.GRAPHICS_TEST_MODE = !DrawMaster.GRAPHICS_TEST_MODE;
                if (DrawMaster.GRAPHICS_TEST_MODE)
                    DrawMaster.FULL_GRAPHICS_TEST_MODE = DialogMaster.confirm("Full test on?");
                else
                    DrawMaster.FULL_GRAPHICS_TEST_MODE = false;
                break;
            case TOGGLE_LOG: {
                String e = ListChooser.chooseEnum(LOG_CHANNELS.class);
                LogMaster.toggle(e);
                break;
            }

            case TOGGLE_FREE_ACTIONS:
                TestMasterContent.toggleFree();
                break;
            case GOD_MODE:
                TestMasterContent.toggleImmortal();
                // game.getManager().getActiveObj().setGodMode(
                // !game.getManager().getActiveObj().isGodMode());
                // DebugUtilities.initGodMode(game.getManager().getActiveObj(),
                // game.getManager()
                // .getActiveObj().isGodMode());
                // game.getManager().refreshAll();
                break;

            case RESTART:
                if (!altMode)
                    if (DialogMaster.confirm("Select anew?")) {
                        FAST_DC.getGameLauncher().selectiveInit();
                    }

                game.getManager().killAllUnits(true, false);
                game.getArenaManager().getSpawnManager().spawnParty(true);
                game.getArenaManager().getSpawnManager().spawnParty(false);
                game.getManager().refreshAll();
                WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, true);
                return func;
            case CLEAR:
                boolean respawn = !isAltMode();
                game.getManager().killAllUnits(isAltMode());
                if (respawn) {
                    // /respawn!
                    game.getArenaManager().getSpawnManager().spawnParty(true);
                    game.getArenaManager().getSpawnManager().spawnParty(false);
                }
                game.getManager().refreshAll();

                break;
            case KILL_ALL_UNITS:
                game.getManager().killAll(isAltMode());
                break;

            case ACTIVATE_UNIT:
                if (isAltMode())
                    getObj().modifyParameter(PARAMS.C_N_OF_ACTIONS, 100);
                if (getObj().isMine()) {
                    game.getManager().setActivatingAction(null);
                    game.getManager().activeSelect(getObj());
                } else {
                    WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, true);
                    WaitMaster.WAIT(1234);
                    getObj().modifyParameter(PARAMS.C_N_OF_ACTIONS, 100);
                }

                game.getVisionManager().refresh();
                break;

            case ADD_ITEM:
                if (isAltMode()) {
                    TYPE = OBJ_TYPES.WEAPONS;
                } else
                    TYPE = (OBJ_TYPES) DialogMaster.getChosenOption("Choose item type...",
                            OBJ_TYPES.WEAPONS, OBJ_TYPES.ARMOR, OBJ_TYPES.ITEMS, OBJ_TYPES.JEWELRY);
                if (isAltMode()) {
                    if (!selectWeaponType())
                        break;
                } else if (!selectType(TYPE))
                    break;

                if (!selectTarget(ref))
                    target = infoObj;
                if (target == null)
                    break;
                boolean quick = false;
                if (isAltMode())
                    quick = false;
                else if (TYPE == OBJ_TYPES.ITEMS)
                    quick = true;
                else if (TYPE == OBJ_TYPES.WEAPONS)
                    quick = DialogMaster.confirm("quick slot item?");
                DC_HeroItemObj item = ItemFactory.createItemObj(selectedType, target.getOwner(),
                        game, ref, quick);
                if (!quick) {
                    if (TYPE != OBJ_TYPES.JEWELRY)
                        target.equip(item, TYPE == OBJ_TYPES.ARMOR ? ITEM_SLOT.ARMOR
                                : ITEM_SLOT.MAIN_HAND);
                } else
                    target.addQuickItem((DC_QuickItemObj) item);

                // target.addItemToInventory(item);

                game.getManager().refreshGUI();
                break;
            case ADD_SPELL:
                if (!selectType(OBJ_TYPES.SPELLS))
                    break;
                if (!selectTarget(ref))
                    target = infoObj;
                if (target == null)
                    break;
                TestMasterContent.setTEST_LIST(TestMasterContent.getTEST_LIST()
                        + selectedType.getName() + ";");

                target.getSpells().add(
                        new DC_SpellObj(selectedType, target.getOwner(), game, target.getRef()));
                game.getManager().refreshGUI();
                break;
            case ADD_SKILL:
            case ADD_ACTIVE:
                PROPERTY prop = G_PROPS.ACTIVES;
                OBJ_TYPES T = OBJ_TYPES.ACTIONS;
                if (func == DEBUG_FUNCTIONS.ADD_SKILL) {
                    prop = PROPS.SKILLS;
                    T = OBJ_TYPES.SKILLS;
                }
                String type = ListChooser.chooseType(T);
                if (type == null)
                    break;

                if (!new SelectiveTargeting(new Conditions(ConditionMaster
                        .getTYPECondition(C_OBJ_TYPE.BF_OBJ))).select(ref))
                    break;
                lastType = type;
                new AddBuffEffect(type + " hack", new ModifyPropertyEffect(prop, MOD_PROP_TYPE.ADD,
                        type), new Formula("1")).apply(ref);
                if (func == DEBUG_FUNCTIONS.ADD_ACTIVE) {
                    infoObj.getActives().add(game.getActionManager().getAction(type, infoObj));
                    ((DC_ActionManager) game.getActionManager()).constructActionMaps(infoObj);
                }

                // game.getManager().reset();
                // instead of toBase()
                break;
            case ADD_PASSIVE:
                // same method
                infoObj.getPassives().add(
                        AbilityConstructor.getPassive(ListChooser.chooseType(OBJ_TYPES.ABILS),
                                infoObj));
                infoObj.activatePassives();
                break;
            case CHANGE_OWNER:
                // if already has, make permanent
                new AddBuffEffect("ownership hack", new OwnershipChangeEffect(), new Formula("1"))
                        .apply(ref);

                break;

            case END_TURN:
                game.getManager().setActivatingAction(null);
                WaitMaster.receiveInput(WAIT_OPERATIONS.TURN_CYCLE, false);
                return func;
            case KILL_UNIT:
                infoObj.kill(infoObj, !isAltMode(), isAltMode());
                // game.getManager().killUnitQuietly((DC_UnitObj)
                // game.getManager()
                // .getInfoObj());
                break;

            case ADD_CHAR:
                summon(true, OBJ_TYPES.CHARS, ref);
                break;
            case ADD_OBJ:
                summon(null, OBJ_TYPES.BF_OBJ, new Ref(game));
                break;
            case ADD_UNIT:
                summon(true, OBJ_TYPES.UNITS, ref);
                break;
            case SET_WAVE_POWER:
                Integer forcedPower = null;
                forcedPower = DialogMaster.inputInt();
                if (forcedPower < 0)
                    forcedPower = null;
                game.getArenaManager().getWaveAssembler().setForcedPower(forcedPower);
                break;
            case SPAWN_CUSTOM_WAVE:
                coordinate = getGame().getBattleFieldManager().pickCoordinate();
                ObjType waveType = ListChooser.chooseType_(OBJ_TYPES.ENCOUNTERS);
                Wave wave = new Wave(coordinate, waveType, game, ref, game.getPlayer(!isAltMode()));

                String value = new ListChooser(SELECTION_MODE.MULTIPLE, StringMaster
                        .openContainer(wave.getProperty(PROPS.UNIT_TYPES)), OBJ_TYPES.UNITS)
                        .choose();
                wave.setProperty(PROPS.UNIT_TYPES, value);
                // PROPS.EXTENDED_PRESET_GROUP
                break;
            case SPAWN_PARTY:

                coordinate = getGame().getBattleFieldManager().pickCoordinate();
                ObjType party = ListChooser.chooseType_(OBJ_TYPES.PARTY);
                game.getArenaManager().getSpawnManager().spawnParty(coordinate, null, party);

                break;
            case SPAWN_WAVE:
                if (!isAltMode()) {
                    coordinate = getGame().getBattleFieldManager().pickCoordinate();
                } else {
                    FACING_DIRECTION side = new EnumChooser<FACING_DIRECTION>()
                            .choose(FACING_DIRECTION.class);
                    // if (side== FACING_DIRECTION.NONE)
                    game.getArenaManager().getSpawnManager().getPositioner().setForcedSide(side);
                }
                String typeName = ListChooser.chooseType(OBJ_TYPES.ENCOUNTERS);
                if (typeName == null)
                    return func;
                try {
                    game.getArenaManager().getSpawnManager().spawnWave(typeName,
                            game.getPlayer(ALT_AI_PLAYER), coordinate);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    game.getArenaManager().getSpawnManager().getPositioner().setForcedSide(null);
                }
                game.getManager().refreshAll();
                break;
            case ADD_ENEMY_UNIT:
                summon(false, OBJ_TYPES.UNITS, new Ref(game));
                // ref = new Ref(game
                // // , game.getManager().getActiveObj().getId()
                // );
                // ref.setPlayer(game.getPlayer(false));
                // typeName = ListChooser.chooseType(OBJ_TYPES.UNITS);
                // if (StringMaster.isEmpty(typeName))
                // break;
                // new SelectiveTargeting(new Conditions(
                // ConditionMaster.getTYPECondition(OBJ_TYPES.TERRAIN)))
                // .select(ref);
                // effect = new SummonEffect(typeName);
                // effect.apply(ref);
                // effect.getUnit().setOwner(game.getPlayer(false));
                // game.getManager().refreshAll();
                break;

            case TOGGLE_ALT_AI: {
                ALT_AI_PLAYER = !ALT_AI_PLAYER;
                break;
            }
            case TOGGLE_DEBUG: {
                game.setDebugMode(!game.isDebugMode());
                Launcher.setDEBUG_MODE(!Launcher.isDEBUG_MODE_DEFAULT());
                break;
            }
            case WAITER_INPUT: {
                String input = DialogMaster.inputText("operation");
                WAIT_OPERATIONS operation = new EnumMaster<WAIT_OPERATIONS>().retrieveEnumConst(
                        WAIT_OPERATIONS.class, input);
                if (operation == null)
                    operation = new EnumMaster<WAIT_OPERATIONS>().retrieveEnumConst(
                            WAIT_OPERATIONS.class, input, true);
                if (operation == null) {
                    DialogMaster.error("no such operation");
                    return func;
                }
                input = DialogMaster.inputText("input");
                WaitMaster.receiveInput(operation, input);
            }
            case REMOVE_HACKS:
                break;
            case CLEAR_WAVES:
                game.getArenaManager().getSpawnManager().clear();
                game.getArenaManager().getBattleConstructor().setIndex(0);
                break;
            case SCHEDULE_WAVES:
                game.getArenaManager().getBattleConstructor().setIndex(0);
                game.getArenaManager().getBattleConstructor().construct();
                break;
        }
        reset();

        if (transmitted) {
            String transmittedData = lastType + StringMaster.NET_DATA_SEPARATOR + infoObj
                    + StringMaster.NET_DATA_SEPARATOR + data + StringMaster.NET_DATA_SEPARATOR
                    + ref;
            game.getCommunicator().transmitDebugFunction(func, transmittedData);
        }
        return func;

    }

    private boolean selectWeaponType() {
        MATERIAL material = MATERIAL.STEEL;
        // if (DialogMaster.confirm("Select material?"))
        QUALITY_LEVEL quality = QUALITY_LEVEL.NORMAL;
        // if (DialogMaster.confirm("Select material?"))
        selectedType = DataManager.getWeaponItem(quality, material, ListChooser
                .chooseType(ItemGenerator.getBaseTypes(OBJ_TYPES.WEAPONS)));
        return selectedType != null;
    }

    private void summon(Boolean me, OBJ_TYPES units, Ref ref) {

        Player player = Player.NEUTRAL;
        if (me != null) {
            player = game.getPlayer(me);
            if (!me)
                if (ALT_AI_PLAYER) {
                    if (altAiPlayer == null)
                        altAiPlayer = new DC_Player("", null, false);
                    player = altAiPlayer;
                }
        }
		/*
		 * alt mode: >> random >> preset >> last
		 */
        ref.setPlayer(player);
        String typeName = null;
        if (arg instanceof DC_HeroObj) {
            Obj obj = (Obj) arg;
            typeName = (obj.getType().getName());
        }

        // new ListChooser(mode, listData, TYPE)

        if (altMode) {
            typeName = lastType;
            // RandomWizard.getRandomType(units).getName();
        } else
            typeName = ListChooser.chooseType(units);
        if (!DataManager.isTypeName(typeName)) {
            typeName = DialogMaster.inputText("Then enter it yourself...");
        }
        if (typeName == null)
            return;
        if (!DataManager.isTypeName(typeName)) {
            ObjType foundType = DataManager.findType(typeName, units);
            if (foundType == null)
                return;
            typeName = foundType.getName();

        }
        if (arg instanceof DC_Cell) {
            Obj obj = (Obj) arg;
            ref.setTarget(obj.getId());
        }
        if (!new SelectiveTargeting(new Conditions(ConditionMaster
                .getTYPECondition(OBJ_TYPES.TERRAIN))).select(ref))
            return;
        lastType = typeName;
        SummonEffect effect = (me == null) ? new CreateObjectEffect(typeName, true)
                : new SummonEffect(typeName);
        if (units == OBJ_TYPES.UNITS)

            if (checkAddXp()) {
                Formula xp = new Formula(""
                        + (DC_Formulas.getTotalXpForLevel(DataManager.getType(typeName,
                        OBJ_TYPES.UNITS).getIntParam(PARAMS.LEVEL)
                        + DialogMaster.inputInt()) - DC_Formulas
                        .getTotalXpForLevel(DataManager.getType(typeName,
                                OBJ_TYPES.UNITS).getIntParam(PARAMS.LEVEL))));
                effect = new SummonEffect(typeName, xp);
            }

        effect.setOwner(player);
        effect.apply(ref);

        if (player.isAi()) {
            AI_Manager.getCustomUnitGroup().add(effect.getUnit());
        }
        game.getManager().refreshAll();
    }

    private boolean checkAddXp() {
        return false;
        // if (alt)
        // return DialogMaster.confirm("Leveled?");
    }

    private void reset() {
        game.getManager().reset();
        game.getManager().refreshAll();
    }

    private boolean selectTarget(Ref ref) {
        if (!new SelectiveTargeting(new Conditions(ConditionMaster
                .getTYPECondition(C_OBJ_TYPE.BF_OBJ))).select(ref))
            return false;

        target = (DC_HeroObj) ref.getTargetObj();
        return true;
    }

    private boolean selectType(OBJ_TYPE TYPE) {
        if (isAltMode()) {
            String name = DialogMaster.inputText("Type name...");
            if (name == null)
                return false;
            selectedType = DataManager.getType(name, TYPE);
            if (selectedType != null)
                return true;
            selectedType = DataManager.findType(name, TYPE);
            if (selectedType != null)
                return true;
        }
        String type = ListChooser.chooseType(TYPE);
        if (type == null)

            return false;

        selectedType = DataManager.getType(type, TYPE);
        return true;
    }

    private Obj getObj() {
        if (game.isSimulation())
            return CharacterCreator.getHero();
        return game.getManager().getInfoObj();
    }

    public void executeDebugFunctionNewThread(final HIDDEN_DEBUG_FUNCTIONS func) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeHiddenDebugFunction(func);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cleanUp();
                }
            }

        }).start();
    }

    public void cleanUp() {
        DebugMaster.setAltMode(false);
        setArg(null);
    }

    public void executeDebugFunctionNewThread(final DEBUG_FUNCTIONS func) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeDebugFunction(func);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cleanUp(); // TODO refactor alt click!!!
                }
            }
        }).start();

    }

    public Object executeHiddenDebugFunction(HIDDEN_DEBUG_FUNCTIONS func) {
        executedFunctions.push(func.toString());

        DC_HeroObj infoObj = null;
        try {
            infoObj = (DC_HeroObj) getObj();
        } catch (Exception e) {
            infoObj = (DC_HeroObj) game.getManager().getActiveObj();
        }
        switch (func) {
            case WRITE_GROUP:
                Entity entity = DC_Game.game.getValueHelper().getEntity();
                if (entity == null)
                    break;
                XML_Writer
                        .writeXML_ForTypeGroup(entity.getOBJ_TYPE_ENUM(), entity.getGroupingKey());

            case WRITE:
                if (!(DC_Game.game.getValueHelper().getEntity() instanceof ObjType))
                    break;
                XML_Writer.writeXML_ForTypeGroup(DC_Game.game.getValueHelper().getEntity()
                        .getOBJ_TYPE_ENUM());
                break;
            case WRITE_TYPE:
                if (!(DC_Game.game.getValueHelper().getEntity() instanceof ObjType))
                    break;
                XML_Writer.writeXML_ForType((ObjType) DC_Game.game.getValueHelper().getEntity());
                break;
            case TOGGLE_AV_MODE:
                CoreEngine.setArcaneVault(!CoreEngine.isArcaneVault());
                CoreEngine.setArcaneVaultMode(true);
                break;

            case RECONSTRUCT: {
                for (ActiveObj obj : infoObj.getPassives()) {
                    obj.setConstructed(false);
                }
                for (ActiveObj obj : infoObj.getActives()) {
                    obj.setConstructed(false);
                }
                break;
            }
            case RELOAD_TYPES: {
                XML_Reader.readTypes(false, true);
                game.initObjTypes();
                break;
            }
            case RESTART_GAME:
                executeDebugFunction(DEBUG_FUNCTIONS.KILL_ALL_UNITS);
                DC_ObjInitializer.processUnitDataString(game.getPlayer(true),
                        game.getPlayerParty(), game);
                game.getManager().unitActionCompleted(null, false);
                break;
            case BF_RESURRECT_ALL:
                break;
            case DISPLAY_EVENT_LOG:
                break;
            case DISPLAY_REF:
                display(game.getManager().getInfoObj().getName() + "'s REF:", game.getManager()
                        .getInfoObj().getRef()
                        + "");
                break;
            case DISPLAY_TRIGGERS:
                // display("Triggers: ", game.getState().getAttachedTriggers());
                displayList("Triggers: ", game.getState().getTriggers(), 1);
                break;
            case DISPLAY_STATE:
                display("State: ", game.getState());
                break;
            case DISPLAY_EFFECTS:
                displayList("Effects ", game.getState().getEffects(), 1);
                break;
            case DISPLAY_OBJECTS:
                for (OBJ_TYPE sub : game.getState().getGame().getState().getObjMaps().keySet())
                    displayList(sub + ": ", game.getState().getGame().getState().getObjMaps().get(
                            sub).keySet(), 1);

            case DISPLAY_UNITS:
                displayList("Units ", game.getState().getGame().getUnits(), 1);

                break;
            case DISPLAY_UNIT_INFO:
                display("INFO OBJ: ", getUnitInfo(game.getManager().getInfoObj()));

                break;
            case HERO_ADD_ALL_SPELLS:

                for (ObjType type : DataManager.getTypes(OBJ_TYPES.SPELLS)) {
                    DC_HeroObj hero = (DC_HeroObj) game.getManager().getInfoObj();
                    if (LibraryManager.checkHeroHasSpell(hero, type))
                        continue;
                    LibraryManager.addVerbatimSpell(hero, type);
                    DC_SpellObj spell = new DC_SpellObj(type, hero.getOriginalOwner(), hero
                            .getGame(), hero.getRef());
                    hero.getSpells().add(spell);
                }
                break;
            default:
                break;

        }
        return null;
    }

    private void displayList(String string, Collection<?> list, int chunkSize) {
        main.system.auxiliary.LogMaster.log(1, string + " list (" + list.size() + ")");

        int i = 0;
        String chunk = ">> ";
        for (Object o : list) {
            if (i >= chunkSize) {
                main.system.auxiliary.LogMaster.log(1, chunk);
                i = 0;
                chunk = ">> ";

            }
            chunk += o.toString() + " <|> ";
            i++;

        }
        main.system.auxiliary.LogMaster.log(1, chunk);

        main.system.auxiliary.LogMaster.log(1, string + " list (" + list.size() + ")");
    }

    private String getUnitInfo(DC_Obj infoObj) {
        String str = "Unit info: \n";
        for (PARAMETER param : PARAMS.values()) {
            if (!param.isDynamic())
                continue;
            str += param.toString();
            str += " = ";
            str += infoObj.getValue(param);
            str += "\n";
        }
        for (PARAMETER param : PARAMS.values()) {
            if (!param.isAttribute())
                continue;
            str += param.toString();
            str += " = ";
            str += infoObj.getValue(param);
            str += "\n";
        }
        for (PARAMETER param : PARAMS.values()) {
            if (param.isDynamic() || param.isAttribute())
                continue;
            str += param.toString();
            str += " = ";
            str += infoObj.getValue(param);
            str += "\n";
        }
        for (PROPERTY p : ContentManager.getPropList()) {
            if (!(ContentManager.isValueForOBJ_TYPE(OBJ_TYPES.CHARS, p) || ContentManager
                    .isValueForOBJ_TYPE(OBJ_TYPES.UNITS, p)))
                continue;
            str += p.toString();
            str += " = ";
            str += infoObj.getValue(p);
            str += "\n";
        }
        return str;
    }

    private void display(String str, Object obj) {
        main.system.auxiliary.LogMaster.log(1, "" + str + obj.toString());

    }

    public Object typeInFunction() {
        String funcName = JOptionPane.showInputDialog("Type in function to execute");
        HIDDEN_DEBUG_FUNCTIONS func = new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>().retrieveEnumConst(
                HIDDEN_DEBUG_FUNCTIONS.class, funcName);
        if (func != null)
            return executeHiddenDebugFunction(func);
        else {
            DEBUG_FUNCTIONS func1 = new EnumMaster<DEBUG_FUNCTIONS>().retrieveEnumConst(
                    DEBUG_FUNCTIONS.class, funcName);
            if (func1 != null)
                return executeDebugFunction(func1);
        }
        return null;
    }

    public DC_Builder getBf() {
        return bf;
    }

    public void setBf(DC_Builder bf) {
        this.bf = bf;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public DC_GameState getState() {
        return state;
    }

    public void setState(DC_GameState state) {
        this.state = state;
    }

    public DebugPanel getDebugPanel() {
        return debugPanel;
    }

    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
    }

    public void toggleDebugGui() {
        getBf().getTopPanel().toggleDebugGui();

    }

    public Obj getArg() {
        return arg;
    }

    public void setArg(Obj arg) {
        this.arg = arg;
    }

    public enum SIMULATION_FUNCTIONS {
        REMAP,

    }

    public enum AI_PARAM {
        LOG_LEVEL
    }

    public enum DEBUG_FUNCTIONS {
        // GAME

        //

        END_TURN(true),
        // UNIT
        ADD_UNIT(true),
        ADD_ENEMY_UNIT(true),
        ADD_GROUP(true),
        PRESET,
        KILL_UNIT(true),
        TOGGLE_DEBUG,
        RESTART,
        CLEAR(true),
        SPAWN_WAVE,
        TOGGLE_OMNIVISION(true),

        PAUSE,
        ADD_DUNGEON,
        ADD_SKILL(true),
        ADD_ACTIVE(true),
        ADD_SPELL(true),
        ADD_CHAR(true),
        ADD_OBJ(true),
        // MISC

        CLEAR_WAVES,
        ACTIVATE_UNIT(true),
        TOGGLE_DUMMY(true),

        SPAWN_PARTY(true),
        SPAWN_CUSTOM_WAVE(true),
        ADD_ITEM(true),
        KILL_ALL_UNITS,

        SET_OPTION,
        EDIT_AI,
        LOAD_DUNGEON,

        SET_WAVE_POWER,
        WAITER_INPUT,
        SCHEDULE_WAVES,

        AUTO_COMBAT,
        TOGGLE_ALT_AI,
        TOGGLE_AUTO_UNIT,
        TOGGLE_GRAPHICS_TEST,
        TOGGLE_FREE_ACTIONS,
        HIDDEN_FUNCTION,
        ADD_PASSIVE(true),
        GOD_MODE(true),
        CHANGE_OWNER(true),
        ADD_TEST_SPELLS(true),
        ADD_ALL_SPELLS(true),
        TOGGLE_LOG,
        REMOVE_HACKS,
        DUNGEON_PLAN_INFO,
        DUNGEON_BLOCK_INFO,
        DUNGEON_ZONES_INFO,
        TOGGLE_DUNGEON_DEBUG,
        DUNGEON_ADD_SUBLEVEL,

        AUTO_TEST_INPUT,
        RUN_AUTO_TESTS,;

        boolean transmitted;

        DEBUG_FUNCTIONS() {

        }

        DEBUG_FUNCTIONS(boolean transmitted) {
            this.transmitted = transmitted;
        }
    }

    public enum HIDDEN_DEBUG_FUNCTIONS {
        HERO_ADD_ALL_SPELLS,
        BF_RESURRECT_ALL,
        DISPLAY_TRIGGERS,
        DISPLAY_EFFECTS,
        DISPLAY_EVENT_LOG,
        DISPLAY_LOG,
        DISPLAY_STATE,
        DISPLAY_OBJECTS,
        DISPLAY_UNITS,
        DISPLAY_REF,
        DISPLAY_UNIT_INFO,
        RECONSTRUCT,
        RELOAD_TYPES,
        RESTART_GAME,

        TOGGLE_AV_MODE,
        AV_MODE_WRITE,
        WRITE_TYPE,
        WRITE_GROUP,
        WRITE,

    }

}