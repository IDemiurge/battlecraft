package main.entity.tools.active;

import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.tools.EntityLogger;
import main.entity.tools.EntityMaster;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveLogger extends EntityLogger<DC_ActiveObj> {

    private LogEntryNode entry;

    public ActiveLogger(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    public LogEntryNode getEntry() {
        return entry;
    }

    public void logCompletion() {

        if (getMaster().getChecker().isAttackAny()) {
            Obj targetObj = getRef().getTargetObj();
            if (targetObj == null) {
                if (getEntity().getLastSubaction() != null) {
                    targetObj = getEntity().getLastSubaction().getRef().getTargetObj();
                }
            }
            if (targetObj == null) {
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ATTACK, getOwnerObj().getNameIfKnown());
            } else {
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ATTACK, getOwnerObj().getNameIfKnown(),
                 // lastSubaction.getName()
                 targetObj.getNameIfKnown());
            }
        } else {
            game.getLogManager().doneLogEntryNode();
            if (getEntry() != null) {
                getEntry().setLinkedAnimation(getMaster().getAnimator().getAnimation());
            }
        }
    }

    public ENTRY_TYPE log() {
        // TODO *player's* detection, not AI's!
        String string = getOwnerObj().getNameIfKnown() + " is activating "
         + getEntity().getDisplayedName();
        LogMaster.gameInfo(StringMaster.getStringXTimes(80 - string.length(), ">") + string);

        boolean logAction = getOwnerObj().getVisibilityLevel() == VISIBILITY_LEVEL.CLEAR_SIGHT
         && !getMaster().getChecker().isAttackAny();
        entry = null;
        ENTRY_TYPE entryType = ENTRY_TYPE.ACTION;
        if (getMaster().getChecker().getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
            entryType = ENTRY_TYPE.MOVE;
            logAction = true;
        }
        if (!getMaster().getChecker().isAttackAny()) {
            entry = game.getLogManager().newLogEntryNode(entryType, getOwnerObj(), this);
        }

        if (logAction) {
            game.getLogManager().log(">> " + string);
        } else if (VisionManager.checkVisible(getOwnerObj(), false) && !getMaster().getChecker().isAttackAny()) {
            String text = " performs an action... ";
            game.getLogManager().log(">> " + getOwnerObj().getNameIfKnown() + text);
        }
        return entryType;
    }

    public ENTRY_TYPE getEntryType() {
        return ENTRY_TYPE.ACTION;
    }

    @Override
    public ActiveMaster getMaster() {
        return (ActiveMaster) super.getMaster();
    }

    @Override
    public Executor getHandler() {
        return (Executor) super.getHandler();
    }

    public boolean isActivationLogged() {
        if (ExplorationMaster.isExplorationOn()) {
            if (getEntity().isTurn() || getEntity().isMove()
             || getEntity().getActionType() == ACTION_TYPE.HIDDEN
             ) {
                return false;
            }

            if (getGame().isDebugMode() ||
             (getEntity().getOwnerObj().isMine() &&
              getEntity().getOwnerObj().getPlayerVisionStatus(false) != UNIT_TO_PLAYER_VISION.KNOWN
              && getEntity().getOwnerObj().getPlayerVisionStatus(false) != UNIT_TO_PLAYER_VISION.DETECTED
             )) {
                return true;
            }
            return false;
        }
        if (!getEntity().getOwnerObj().isMine())
            if (!getGame().isDebugMode() &&
             getEntity().getOwnerObj().getPlayerVisionStatus(false) == UNIT_TO_PLAYER_VISION.CONCEALED
             ||
             getEntity().getOwnerObj().getPlayerVisionStatus(false) == UNIT_TO_PLAYER_VISION.INVISIBLE
             )
                return false;
        return true;
    }


    public boolean isTargetLogged() {
        if (entity.getActionGroup() == ACTION_TYPE_GROUPS.MODE)
            return false;
        if (entity.getActionGroup() == ACTION_TYPE_GROUPS.TURN)
            return false;
        if (entity.getActionGroup() == ACTION_TYPE_GROUPS.MOVE)
            return false;

        return true;

    }

    public Unit getOwnerObj() {
        return getEntity().getOwnerObj();
    }

}
