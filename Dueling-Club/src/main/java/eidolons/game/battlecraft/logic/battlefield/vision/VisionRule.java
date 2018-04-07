package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.ConcealmentRule;
import eidolons.game.battlecraft.rules.mechanics.IlluminationRule;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.test.debug.DebugMaster;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.game.bf.Coordinates;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

/**
 * Created by JustMe on 4/1/2018.
 * <p>
 * what are the annoying cases now?
 * <p>
 * shifting outlines - back and forth
 * especially on BF
 * new outline get()
 * <p>
 * VIS LEVEL
 * <p>
 * SIGHT
 * <p>
 * PLAYER STATUS
 * <p>
 * use cases:
 * <p>
 * AI
 * Targeting
 * Information (examine/ tooltip)
 * Location
 * Sneak Attacks
 * Aggro
 */
public class VisionRule {
    VisionMaster master;
    VisionController controller;
    private boolean playerUnseenMode= CoreEngine.isFastMode();

    public VisionRule(VisionMaster master) {
        this.master = master;
        this.controller = master.getVisionController();
    }

    public void setPlayerUnseenMode(boolean playerUnseenMode) {
        this.playerUnseenMode = playerUnseenMode;
    }

    public static boolean isSightInfoAvailable(BattleFieldObject observer) {
        if (observer.isMine())
            return true;
        return observer.getPlayerVisionStatus() == PLAYER_VISION.DETECTED;
    }

    public void fullReset(Unit... observers) {
        BattleFieldObject[][][] array = master.getGame().getMaster().getObjCells();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                BattleFieldObject[] objects = master.getGame().getMaster().getObjects(i, j);
                DC_Cell cell = master.getGame().getCellByCoordinate(new Coordinates(i, j));

                for (Unit observer : observers) {
                    if (!isResetRequired(observer, cell))
                        continue;
                    if (isGammaResetRequired(observer, cell)) {
                        cell.setGamma(observer, master.getGammaMaster().getGamma(
                         observer, cell));
                    }
                    master.getSightMaster().resetUnitVision(observer, cell);
                    for (BattleFieldObject sub : objects) {
                        //check ignore?
                        if (!isObjResetRequired(observer, sub))
                            continue;
                        if (isGammaResetRequired(observer, sub))
                            sub.setGamma(observer, master.getGammaMaster().getGamma(observer, sub));
//                    master.getSightMaster().resetSightStatuses(observer);
                        master.getSightMaster().resetUnitVision(observer, sub);
//                        controller.getUnitVisionMapper()
//                        sub.setUnitVisionStatus(observer, master.getUnitVisibilityStatus(sub, observer));
                        controller.getVisibilityLevelMapper().set(observer, sub, visibility(observer, sub));
                        controller.getOutlineMapper().set(observer, sub, outline(observer, sub));
                        controller.getPlayerVisionMapper().set(observer.getOwner(), sub, playerVision(observer, sub));
                    }
                }
            }
        }

    }

    private boolean isGammaResetRequired(Unit observer, DC_Obj sub) {
        return true;
    }

    private boolean isObjResetRequired(Unit observer, DC_Obj sub) {
        if (sub instanceof Unit)
            if (!observer.isPlayerCharacter())
                return observer.isHostileTo(sub.getOwner());
        if (sub.isDead())
            return false;
        return true;
    }

    private boolean isResetRequired(Unit observer, DC_Obj cell) {
        //changed position
        //is close enough
        //is hostile
        if (observer.isDead())
            return false;
        if (observer.isUnconscious())
            return false;
        if (observer.isPlayerCharacter())
            return true;
        else
        if (playerUnseenMode){
            return false;
        }

        if (PositionMaster.getExactDistance(observer, cell) > observer.getMaxVisionDistance()) {
            return false;
        }
        return true;
    }

    public VISIBILITY_LEVEL visibility(Unit source, DC_Obj object) {
        UNIT_VISION sight = controller.getUnitVisionMapper().get(source, object);
        boolean landmark = object instanceof Structure;
//        if (object instanceof BattleFieldObject) {
//            landmark = ((BattleFieldObject) object).isWall() || ((BattleFieldObject) object).isLandscape();
//        }

        switch (sight) {
            case IN_PLAIN_SIGHT:
                return VISIBILITY_LEVEL.CLEAR_SIGHT;
            case BLOCKED:
                //if
                return VISIBILITY_LEVEL.BLOCKED;
            case IN_SIGHT:
                if (landmark)
                    if (controller.getDetectionMapper().get(source.getOwner(), object)) {
                        return VISIBILITY_LEVEL.CONCEALED;
                    }

                return VISIBILITY_LEVEL.OUTLINE;
            case BEYOND_SIGHT:

                if (landmark) {
                    if (controller.getDetectionMapper().get(source.getOwner(), object)) {
                        return VISIBILITY_LEVEL.CONCEALED;
                    }
                }
        }
        return VISIBILITY_LEVEL.UNSEEN;
//            TODO case CONCEALED:
//                break;

    }

    public PLAYER_VISION playerVision(Unit source, BattleFieldObject object) {
        if (DebugMaster.isOmnivisionOn()) {
            if (source.isMine()) {
                return PLAYER_VISION.DETECTED;

            }
        }
        if (object instanceof Unit) {
            if (StealthRule.checkInvisible(object)) {
                return (PLAYER_VISION.INVISIBLE);
            }
        }

        VISIBILITY_LEVEL visibilityLevel = controller.getVisibilityLevelMapper().
         get(source, object);
        switch (visibilityLevel) {
            case CLEAR_SIGHT:
                reveal(source, object);
                return PLAYER_VISION.DETECTED;
            case OUTLINE:
                return PLAYER_VISION.UNKNOWN;
            case CONCEALED:
                return PLAYER_VISION.KNOWN;
            case BLOCKED:
                if (object.isWall()) {
                    if (object.isDetected(source.getOwner())) {
                        return PLAYER_VISION.KNOWN;
                    }
                }
            case UNSEEN:
                hide(source, object);
                return PLAYER_VISION.INVISIBLE;
//                case VAGUE_OUTLINE:
//                    break;
        }

        return null;
    }

    public boolean isDisplayedOnGrid(Unit source, BattleFieldObject object) {
        if (object.isMine())
            return true;
        if (object.isOverlaying()) {
            return controller.getPlayerVisionMapper().get(source.getOwner(), object) ==
             PLAYER_VISION.DETECTED;
        }
        if (controller.getPlayerVisionMapper().get(source.getOwner(), object)
         == PLAYER_VISION.INVISIBLE)
            return false;
        return true;
    }

    public boolean isExamineAllowed(Unit source, BattleFieldObject object) {
        PLAYER_VISION vision = controller.getPlayerVisionMapper().get(source.getOwner(), object);
        if (vision == PLAYER_VISION.INVISIBLE || vision == PLAYER_VISION.UNKNOWN)
            return false;
        return true;
    }

    private void reveal(Unit source, BattleFieldObject object) {
        if (BooleanMaster.isTrue(controller.getDetectionMapper()
         .get(source.getOwner(), object)))
            return;
        controller.getDetectionMapper().set(source.getOwner(), object, true);
        if (isDetectionLogged(source, object))
            master.getGame().getLogManager().logReveal(source, object);
    }

    private boolean isDetectionLogged(Unit source, BattleFieldObject object) {
        if (object instanceof Structure)
            return false;

        if (source != object)
            if (source.isMine())
                if (source.isHostileTo(object.getOwner()))
                    return true;
        return false;
    }

    private void hide(Unit source, BattleFieldObject object) {
        if (BooleanMaster.isFalse(controller.getDetectionMapper()
         .get(source.getOwner(), object)))
            return;
        controller.getLastSeenMapper().set(source.getOwner(), object,
         object.getLastCoordinates());

        controller.getDetectionMapper().set(source.getOwner(), object, false);
        if (isDetectionLogged(source, object))
            master.getGame().getLogManager().logHide(source, object);
    }

    public OUTLINE_TYPE outline(Unit source, BattleFieldObject object) {
        if (DebugMaster.isOmnivisionOn()) {
            if (source.isMine())
                return null;
        }
        if (object.getGame().isSimulation() || object.getGame().isDebugMode()) {
            return null;
        }
        if (object instanceof Entrance) {
            return null;
        }

        VISIBILITY_LEVEL visibility = controller.getVisibilityLevelMapper().get(source, object);

        if (visibility == VISIBILITY_LEVEL.OUTLINE) {
            OUTLINE_TYPE outline = master.getOutlineMaster().getOutline(object, source);
            if (outline == null) {
                if (source.isMine())
                    //TODO QUICK FIX - NOW ENEMIES HAVE 100% CLEARSHOT AND WILL AGGRO IF THIS WORKS FOR THEM
                    object.setVisibilityLevel(source, VISIBILITY_LEVEL.CLEAR_SIGHT);
            }
            return outline;
        }

        if (controller.getDetectionMapper().get(source.getOwner(), object) ||
         visibility == VISIBILITY_LEVEL.CLEAR_SIGHT) {
            if (ConcealmentRule.isConcealed(source, object)) {
                return OUTLINE_TYPE.DEEPER_DARKNESS;
            }
            if (IlluminationRule.isConcealed(source, object)) {
                return OUTLINE_TYPE.BLINDING_LIGHT;
            }
            return null;

        }


        return null;
    }


    public boolean isAggro(Unit hero, Unit unit) {

        if (controller.getVisibilityLevelMapper().get(unit, hero) == VISIBILITY_LEVEL.CLEAR_SIGHT)
            return true;

//        if (isDisplayedOnGrid(unit, hero))
//            return true;

        return false;
    }


    public void togglePlayerUnseenMode() {
        playerUnseenMode = !playerUnseenMode;
    }
}