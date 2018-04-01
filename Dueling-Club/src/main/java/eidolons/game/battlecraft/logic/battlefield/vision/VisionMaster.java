package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_TO_UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.*;
import main.game.bf.GenericVisionManager;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;
import eidolons.test.debug.DebugMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 2/22/2017.
 */
public class VisionMaster implements GenericVisionManager {

    private SightMaster sightMaster;
    private DetectionMaster detectionMaster;
    private IlluminationMaster illuminationMaster;
    private VisibilityMaster visibilityMaster;
    private GammaMaster gammaMaster;
    private HintMaster hintMaster;
    private OutlineMaster outlineMaster;


    private DC_Game game;
    private boolean fastMode;
    private boolean firstResetDone;

    public VisionMaster(DC_Game game) {
        this.game = game;
        detectionMaster = new DetectionMaster(this);
        outlineMaster = new OutlineMaster(this);
        gammaMaster = new GammaMaster(this);
        visibilityMaster = new VisibilityMaster(this);
        illuminationMaster = new IlluminationMaster(this);
        sightMaster = new SightMaster(this);
        hintMaster = new HintMaster(this);

    }



    public void resetVisibilityStatuses() {
        Chronos.mark("VISIBILITY REFRESH");
        ClearShotCondition.clearCache();
        getGammaMaster().clearCache();
        getIlluminationMaster().clearCache();
        getSightMaster().clearCaches();

        if (getActiveUnit() == null) {
            LogMaster.log(1, "***********null active activeUnit for visibility!");
            return;
        }
        boolean mine = getActiveUnit().getOwner().isMe();
        getActiveUnit().setUnitVisionStatus(UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT);
        setRelativeActiveUnitVisibility(getActiveUnit(), game.getStructures());
        setRelativeActiveUnitVisibility(getActiveUnit(), game.getPlayer(!mine).getControlledUnits());

        Set<Obj> cells = isFastMode() ? game.getBattleField().getGrid()
         .getCellsWithinVisionBounds() : game.getCells();
        setRelativeActiveUnitVisibility(getActiveUnit(), cells);

        setAlliedVisibility(game.getPlayer(mine).getControlledUnits());

        getVisibilityMaster().resetOutlinesAndVisibilityLevels();

        setRelativePlayerVisibility(game.getPlayer(mine), game.getStructures()); // DC_Player.NEUTRAL.getControlledUnits());

        setRelativePlayerVisibility(game.getPlayer(mine), game.getPlayer(!mine)
         .getControlledUnits());

        setRelativePlayerVisibility(game.getPlayer(!mine), game.getPlayer(mine)
         .getControlledUnits());

        getActiveUnit().setUnitVisionStatus(VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT);
        getActiveUnit().setVisibilityLevel(VISIBILITY_LEVEL.CLEAR_SIGHT);

         resetLastKnownCoordinates();

        triggerGuiEvents();
        firstResetDone = true;
        Chronos.logTimeElapsedForMark("VISIBILITY REFRESH", true);
    }

    private void resetLastKnownCoordinates() {
        //   TODO must communicate with GridPanel
//        for (Unit u : game.getUnits()) {
//            if (checkVisible(u)) {
//                u.setLastKnownCoordinates(u.getCoordinates());
//            }
//        }
    }
    public void triggerGuiEvents() {
        List<BattleFieldObject> visibleList = new ArrayList<>();
        List<BattleFieldObject> invisibleList = new ArrayList<>();
        for (BattleFieldObject sub : game.getBfObjects()) {
            if (sub.isDead())
                continue;
            if (isVisibleOnGrid(sub))
                visibleList.add(sub);
            else invisibleList.add(sub);
        }
//        WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
        main.system.auxiliary.log.LogMaster.log(1, ">>>>>> invisibleList  = " + invisibleList);
        main.system.auxiliary.log.LogMaster.log(1, ">>>>>> visibleList  = " + visibleList);

        if (LOG_CHANNEL.VISIBILITY_DEBUG.isOn()) {
            String string = "";
            for (BattleFieldObject sub : visibleList) {
                string += sub + ": \n";
                string += "getVisibilityLevelForPlayer= " + sub.getVisibilityLevelForPlayer() + "\n";
                string += "getVisibilityLevel= " + sub.getVisibilityLevel() + "\n";
                string += "getPlayerVisionStatus= " + sub.getPlayerVisionStatus(true) + "\n";
                string += "getGamma= " + sub.getGamma() + "\n";
            }

            LogMaster.log(1, "***********" +
             "" + string);

        }

        GuiEventManager.trigger(GuiEventType.UNIT_VISIBLE_OFF, invisibleList);
        GuiEventManager.trigger(GuiEventType.UNIT_VISIBLE_ON, visibleList);
    }

    private boolean isVisibleOnGrid(BattleFieldObject object) {
        if (object.isMine())
            return true;
        if (object.getVisibilityLevel() == VISIBILITY_LEVEL.UNSEEN)
            return false;
        //TODO why for active player?
        if (object.getPlayerVisionStatus(true) == UNIT_TO_PLAYER_VISION.UNKNOWN)
            if (object.getOutlineTypeForPlayer() == null ||
             object.getOutlineTypeForPlayer() == OUTLINE_TYPE.DEEPER_DARKNESS
             || !firstResetDone)
                return false;
            else if (object.getPlayerVisionStatus(true) == UNIT_TO_PLAYER_VISION.INVISIBLE) {
                return false;
            } else //if (!sub.isWall())
                if (object.getOutlineTypeForPlayer() == OUTLINE_TYPE.BLOCKED_OUTLINE)
                    return false;
                else if (object.getVisibilityLevelForPlayer() == VISIBILITY_LEVEL.UNSEEN)
                    return false;

        return true;
    }

    private void setAlliedVisibility(Set<Obj> controlledUnits) {
        for (Obj obj : controlledUnits) {
            DC_Obj unit = (DC_Obj) obj;
            UNIT_TO_UNIT_VISION status = getSightMaster().getUnitVisionStatusPrivate(unit,
             this.getActiveUnit());
            // UNIT_TO_PLAYER_VISION detectionStatus =
            // UNIT_TO_PLAYER_VISION.DETECTED;

            if (!unit.isMine()) {
                if (unit.getActivePlayerVisionStatus() == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE) {
                    if (status == VisionEnums.UNIT_TO_UNIT_VISION.CONCEALED) {
                        status = VisionEnums.UNIT_TO_UNIT_VISION.BEYOND_SIGHT;
                    }
                    // detectionStatus = UNIT_TO_PLAYER_VISION.INVISIBLE_ALLY;
                }
            }
            // unit.setPlayerVisionStatus(detectionStatus); //TODO NEW - enemy
            // exclusive
            unit.setUnitVisionStatus(status);
        }
    }


    private void setRelativeActiveUnitVisibility(Unit activeUnit, Collection<? extends Obj> units) {
        for (Obj obj : units) {
            DC_Obj unit = (DC_Obj) obj;

            UNIT_TO_UNIT_VISION status = getSightMaster().getUnitVisionStatusPrivate(unit, activeUnit);


            unit.setUnitVisionStatus(status);
        }
    }


    public UNIT_TO_UNIT_VISION getUnitVisibilityStatus(DC_Obj unit, Unit activeUnit) {
        return sightMaster.getUnitVisibilityStatus(unit, activeUnit);
    }

    public UNIT_TO_UNIT_VISION getUnitVisibilityStatus(DC_Obj unit) {
        return sightMaster.getUnitVisibilityStatus(unit, getGame().getManager().getActiveObj());
    }

    private void setRelativePlayerVisibility(DC_Player player, Collection<? extends Obj> units) {

        for (Obj obj : units) {
            DC_Obj target = (DC_Obj) obj;
            if (target instanceof Entrance) {
                target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED);
                continue;
            }
            UNIT_TO_PLAYER_VISION status = getPlayerVisionStatusForObject(player, target);
            target.setPlayerVisionStatus(status);

//            TODO last seen displayed as 50% alpha pic

            if (status == UNIT_TO_PLAYER_VISION.INVISIBLE) {
                //TODO ally-stealth visuals!
            }
        }
    }

    private UNIT_TO_PLAYER_VISION getPlayerVisionStatusForObject(DC_Player player, DC_Obj target) {
        UNIT_TO_PLAYER_VISION status = target.getActivePlayerVisionStatus();

        if (target instanceof Unit) {
            if (ExplorationMaster.isExplorationOn() || ((Unit) target).getAI().isOutsideCombat()) {
                status = (UNIT_TO_PLAYER_VISION.UNKNOWN);
            } else { //DEBUGGING
                status = target.getActivePlayerVisionStatus();
            }
        }
        if (target instanceof Structure) {
            if (!((Structure) target).isWall() || !target.isDetectedByPlayer()) {
                status = (UNIT_TO_PLAYER_VISION.UNKNOWN);
            }
        }
        if (DebugMaster.isOmnivisionOn()) {
            if (player.isMe()) {
                return VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED;

            }
        }
        boolean result = false; // detected or not
        if (target instanceof Unit) {
            if (checkInvisible(target)) {
                if (target.getActivePlayerVisionStatus() != UNIT_TO_PLAYER_VISION.INVISIBLE) {
                    player.getLastSeenCache().put(target, target.getCoordinates());
                }
                return (UNIT_TO_PLAYER_VISION.INVISIBLE);
            }
        }

        for (Obj obj1 : player.getControlledUnits()) {
            if (obj1 != getActiveUnit()) {
                continue; //TODO should check for all allies? will it be slow?
            }
            VISIBILITY_LEVEL visibilityLevel = target.getVisibilityLevel();

            if (visibilityLevel != VISIBILITY_LEVEL.BLOCKED
             && visibilityLevel != VISIBILITY_LEVEL.UNSEEN) {

                if (visibilityLevel == VISIBILITY_LEVEL.CONCEALED) {
                    if (target instanceof BattleFieldObject) {
                        if (!((BattleFieldObject) target).isWall()
                         || !target.isDetectedByPlayer()) { //MAKE NON-WALLS UNKNOWN AGAIN
                            status = (UNIT_TO_PLAYER_VISION.UNKNOWN);
                        }
                    } else
                        status = (UNIT_TO_PLAYER_VISION.CONCEALED);
                } else {
                    target.setDetected(true);
                        if (player.isMe()) { //why not?
                    target.setDetectedByPlayer(true);
                        }
                    if (visibilityLevel == VISIBILITY_LEVEL.CLEAR_SIGHT) {
                        status = (UNIT_TO_PLAYER_VISION.DETECTED);
                    } else {
                        status = (UNIT_TO_PLAYER_VISION.KNOWN);
                    }
                }
                result = true;
                break; // TODO set VL to *max* => Identification Level
            }

        }
        if (result) {
            player.getLastSeenCache().put(target, target.getCoordinates());
        } else {
            if (target.isDetectedByPlayer()) {
                if (target instanceof DC_Cell) {
                    status = (VisionEnums.UNIT_TO_PLAYER_VISION.KNOWN);
                } else if (target.getVisibilityLevel() != VISIBILITY_LEVEL.BLOCKED) {
                    BattleFieldObject unit = (BattleFieldObject) target;
                    if (unit.isWall() || unit.isLandscape()) {
                        status = (VisionEnums.UNIT_TO_PLAYER_VISION.KNOWN);
                    }
                }

            }
        }


        return status;
    }


    public boolean checkVisible(DC_Obj obj) {
        boolean visible = checkVisible(obj, false);
//        if (!visible) {
//            obj.setDetected(false);
//        }
        return visible;
    }

    public boolean checkVisible(DC_Obj obj, boolean active) {
        Unit source = active ? getActiveUnit() : getSeeingUnit();
        if (source == null) {
            return false;
        }
        if (source.getZ() != obj.getZ() && !(obj instanceof Entrance)) // dungeon
        // sublevel
        {
            return false;
        }
        if (source.getOwner().isMe()) {
            if (obj.getOwner().isMe()) {
                return true;
            }
        }
        // if (obj.getPerceptionStatus(active) ==
        // PERCEPTION_STATUS.KNOWN_TO_BE_THERE)
        // return true;

        if (visibilityMaster.isZeroVisibility(obj, active)) {
            if (obj.getPlayerVisionStatus(active) == VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN) {
                return false;
            }
        }

        if (obj.getPlayerVisionStatus(active) == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE
         || obj.getPlayerVisionStatus(active) == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE_ALLY) {
            return false;
        }

        // if (obj.getVisibilityLevel()==VISIBILITY_LEVEL.CLEAR)
        // return true; // ILLUMINATION SHOULD ADD TO SIGHTED-COORDINATES
        // VISIBILITY TODO
        return true;

    }

    /**
     * only invoke after checking if DETECTED
     */
    private boolean checkInvisible(DC_Obj unit) {
        return StealthRule.checkInvisible(unit);
    }

    public void refresh() {
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Refreshing visibility...");
        //Chronos.mark("VISIBILITY REFRESH");
        sightMaster.clearCaches();
        resetVisibilityStatuses();
        //Chronos.logTimeElapsedForMark("VISIBILITY REFRESH");
    }

    public VISIBILITY_LEVEL getVisibilityLevel(Unit source, DC_Obj target) {
        return getVisibilityMaster().getUnitVisibilityLevel(source, target);
    }

    public String getDisplayImagePathForUnit(DC_Obj obj) {
        return getVisibilityMaster().getDisplayImagePathForUnit(obj);
    }

    public boolean checkDetectedForPlayer(DC_Obj obj) {
        return getDetectionMaster().checkDetectedForPlayer(obj);
    }

    public boolean checkDetectedEnemy(DC_Obj obj) {
        return getDetectionMaster().checkDetectedEnemy(obj);
    }

    public boolean checkDetected(DC_Obj obj) {
        return getDetectionMaster().checkDetected(obj);
    }

    public boolean checkDetected(DC_Obj obj, boolean enemy) {
        return getDetectionMaster().checkDetected(obj, enemy);
    }

    @Override
    public boolean checkInvisible(Obj obj1) {
        DC_Obj obj = (DC_Obj) obj1;
        return !checkVisible(obj, true);
    }

    public boolean isFastMode() {
        return fastMode;
    }

    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

    public DetectionMaster getDetectionMaster() {
        return detectionMaster;
    }

    public IlluminationMaster getIlluminationMaster() {
        return illuminationMaster;
    }

    public VisibilityMaster getVisibilityMaster() {
        return visibilityMaster;
    }

    public GammaMaster getGammaMaster() {
        return gammaMaster;
    }

    public SightMaster getSightMaster() {
        return sightMaster;
    }

    public HintMaster getHintMaster() {
        return hintMaster;
    }

    public Unit getActiveUnit() {
        return ExplorationMaster.isExplorationOn()
         ? getSeeingUnit() :
         game.getTurnManager().getActiveUnit(true);
    }

    public OutlineMaster getOutlineMaster() {
        return outlineMaster;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public Unit getSeeingUnit() {
        Unit unit = Eidolons.game.getManager().getMainHero();
        if (unit == null) {
            unit = Eidolons.game.getManager().getActiveObj();
        }
        if (unit == null) {
            unit = (Unit) Eidolons.game.getPlayer(true).getControlledUnits().iterator().next();
        }
        return unit;
    }
}