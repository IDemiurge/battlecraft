package main.entity.tools.active;

import main.content.enums.entity.UnitEnums;
import main.content.mode.STD_MODES;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.rules.action.WatchRule;
import main.system.auxiliary.secondary.BooleanMaster;

/**
 * Created by JustMe on 2/25/2017.
 */
public class Activator extends ActiveHandler{


    private Boolean canActivate ;
    private boolean broken;
    private DC_ActiveObj lastSubaction;

    public Activator(DC_ActiveObj entity, ActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    public String getStatusString() {
        return (BooleanMaster.isTrue(canActivate)) ? "Activate " : "" +
         getAction().getCosts().getReasonsString() + " to activate ";
    }
    public boolean canBeActivated(Ref ref, boolean first) {
        if (!first || broken) {
            if (canActivate != null) {

                return canActivate;
            }
        }
        if (getChecker(). checkStatus(UnitEnums.STATUS.BLOCKED)) {
            return false;
        }
        // toBase();
        boolean result = false;
        try {
          getInitializer().  initCosts(); // TODO ++ check if there are any targets

            result =getAction(). getCosts().canBePaid(getRef());
            broken = false;
        } catch (Exception e) {
            if (!broken) {
                e.printStackTrace();
            }
            broken = true;
        } finally {
            canActivate=(result);
        }
        return result;
    }


    private Boolean checkSubActionModeActivation() {
        // TODO triggered activation?
        DC_UnitAction action = getModeAction();
        if (action == null) {
            return null;
        }
        return action.canBeActivated(getRef());

    }
    public boolean canBeManuallyActivated() {
        if (getChecker(). isBlocked()) {
            return false;
        }
        Boolean checkSubActionMode =
         checkSubActionModeActivation();
        if (checkSubActionMode != null) {
            return checkSubActionMode;
        }

        return canBeActivated(getRef(), true);
    }
    public DC_UnitAction getModeAction() {
        String mode = ownerObj.getActionMode(getEntity());
        if (mode == null) {
            return null;
        }
        if (getChecker().isAttack()) {
            return (DC_UnitAction) game.getActionManager().getAction(mode, ownerObj);
        }
        return (DC_UnitAction) game.getActionManager().getAction(mode + " " + getName(), ownerObj);
    }


    public boolean canBeActivatedAsExtraAttack(Boolean instant_counter_opportunity) {
      getHandler().  setExtraAttackMode(instant_counter_opportunity, true);
        boolean res = false;
        try {
            res = canBeActivated(getRef(), true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getHandler(). setExtraAttackMode(instant_counter_opportunity, false);
        }
        return res;
    }

    public boolean canBeActivatedAsCounter() {
        return canBeActivatedAsExtraAttack(false);
    }

    public boolean canBeActivatedAsInstant() {
        return canBeActivatedAsExtraAttack(true);
    }

    public boolean canBeActivatedAsAttackOfOpportunity(boolean pending, Unit target) {
        boolean watch = getOwnerObj().getMode().equals(STD_MODES.ALERT)
         || WatchRule.checkWatched(getOwnerObj(), target);

        if (!watch) {
            if (pending) {
                return false;
            }
            return canBeActivatedAsInstant();
        }
        if (!pending) {
            if (canBeActivatedAsInstant()) {
                return true;
            }
        }

        return canBeActivatedAsExtraAttack(null);

    }

    public boolean tryOpportunityActivation(DC_ActiveObj triggeringAction) {
        return tryExtraAttackActivation(triggeringAction, null);
    }

    public boolean tryInstantActivation(DC_ActiveObj triggeringAction) {
        return tryExtraAttackActivation(triggeringAction, true);
    }

    public boolean tryCounterActivation(DC_ActiveObj triggeringAction) {
        return tryExtraAttackActivation(triggeringAction, false);
    }

    public boolean tryExtraAttackActivation(DC_ActiveObj triggeringAction,
                                            Boolean instant_counter_opportunity) {
       getHandler(). setExtraAttackMode(instant_counter_opportunity, true);
        try {
            if (canBeActivated(getRef(), true)) {
                getHandler().activateOn(triggeringAction.getOwnerObj());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getHandler().setExtraAttackMode(instant_counter_opportunity, false);
        }
        return false;
    }

    public Boolean getCanActivate() {
        return canActivate;
    }

    public void setCanActivate(Boolean canActivate) {
        this.canActivate = canActivate;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(Boolean broken) {
        this.broken = broken;
    }

    public DC_ActiveObj getLastSubaction() {
        return lastSubaction;
    }

    public void setLastSubaction(DC_ActiveObj lastSubaction) {
        this.lastSubaction = lastSubaction;
    }
}