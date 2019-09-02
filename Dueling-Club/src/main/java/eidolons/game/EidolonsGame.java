package eidolons.game;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.content.values.parameters.PARAMETER;
import main.data.StringMap;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.launch.CoreEngine;

import java.util.Map;

/**
 * Created by JustMe on 5/13/2017.
 */
public class EidolonsGame {
    public static boolean BRIDGE = false;
    public static boolean BOSS_FIGHT;
    public static boolean TUTORIAL_MISSION;
    public static boolean TUTORIAL_PATH;
    public static boolean BRIDGE_CROSSED;
    public static boolean FIRST_BATTLE_STARTED;
    public static boolean DUEL = false;

    public static boolean INTRO_STARTED;
    public static boolean TURNS_DISABLED;
    public static boolean MOVES_DISABLED;
    private static Map<String, Boolean> varMap = new StringMap<>();
    private static Map<String, Boolean> actionMap = new StringMap<>();

    public static final void set(String field, boolean val) {
        try {
            EidolonsGame.class.getField(field.toUpperCase()).set(null, val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            try {
                EidolonsGame.class.getField(field).set(null, val);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    public static boolean get(String field) {
        if (varMap.get(field) == null) {
            return false;
        }
        return varMap.get(field);
    }

    public static void setVarMap(String value, Boolean valueOf) {
        varMap.put(value, valueOf);
    }

    public static boolean getActionSwitch(String field) {
        if (actionMap.get(field) == null) {
            return false;
        }
        return actionMap.get(field);
    }

    public static void setActionSwitch(String value, Boolean valueOf) {
        actionMap.put(value, valueOf);
    }

    private MetaGameMaster metaMaster;
    private boolean aborted;

    static Map<TUTORIAL_STAGE, Boolean> completionMap;

    public static boolean isHqEnabled() {
        if (EidolonsGame.BRIDGE)
            if (!EidolonsGame.BRIDGE_CROSSED)
                return false;
        return true;
    }

    public static boolean isAltControlPanel() {
        if (EidolonsGame.BRIDGE_CROSSED)
            return false;
        return BRIDGE;
    }

    public static FACING_DIRECTION getPresetFacing(Unit unit) {
        if (BRIDGE) {

            if (OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.TESTER_VERSION)) {
                return FACING_DIRECTION.EAST;
            }
            return FACING_DIRECTION.NORTH;
        }
        return null;
    }

    public static void stageDone(TUTORIAL_STAGE stage) {
        completionMap.put(stage, true);
    }

    public static boolean isLordPanelEnabled() {
        return !BRIDGE;
    }


    public enum TUTORIAL_STAGE {

        alert,
        essence,
        meditate,


    }

    public static boolean isSpellsEnabled() {
        return CoreEngine.isIDE() && !EidolonsGame.DUEL;
    }

    public static boolean isParamBlocked(PARAMETER parameter) {
        return false;
    }

    public static boolean isActionBlocked(DC_ActiveObj activeObj) {
        /**
         * boolean map?
         *
         */
        if (activeObj == null) {
            return false;
        }
        if (activeObj.isDisabled()) {
            return true;
        }
        if (activeObj.isMove()) {
            return MOVES_DISABLED;
        }
        if (activeObj.isTurn()) {
            return TURNS_DISABLED;
        }

        return false;
    }

    public MetaGameMaster getMetaMaster() {
        return metaMaster;
    }

    public void setMetaMaster(MetaGameMaster metaMaster) {
        this.metaMaster = metaMaster;
    }

    public void init() {
        if (metaMaster.getData().equalsIgnoreCase("ashen path")) {
            BRIDGE = true;
        }
        metaMaster.init();
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        if (aborted) main.system.auxiliary.log.LogMaster.log
                (1, "game aborted!!!!!!");
        this.aborted = aborted;
    }

}
