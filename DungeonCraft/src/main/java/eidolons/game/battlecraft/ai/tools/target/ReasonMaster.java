package eidolons.game.battlecraft.ai.tools.target;

import eidolons.ability.conditions.FacingCondition;
import eidolons.ability.conditions.VisibilityCondition;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.DistanceCondition;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;

import java.util.ArrayList;
import java.util.List;

public class ReasonMaster {
    public static boolean checkReasonCannotActivate(DC_ActiveObj action, String reason) {
        List<String> reasons = ReasonMaster.getReasonsCannotActivate(action);
        for (String r : reasons) {
            if (StringMaster.compareByChar(r, reason, false)) {
                return true;
            }
        }
        for (String r : reasons) {
            if (StringMaster.compare(r, reason, false)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkReasonCannotActivate(Action action, String reason) {
        List<String> reasons = ReasonMaster.getReasonsCannotActivate(action);
        for (String r : reasons) {
            if (StringMaster.compareByChar(r, reason, false)) {
                return true;
            }
        }
        for (String r : reasons) {
            if (StringMaster.compare(r, reason, false)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getReasonsCannotActivate(DC_ActiveObj action) {

        Ref REF = action.getRef().getCopy();
        REF.setMatch(action.getRef().getTarget());
        REF.setID(KEYS.PAYEE, action.getOwnerUnit().getId());
        return getReasonsCannotActivate(action, REF);
    }

    public static List<String> getReasonsCannotActivate(Action action) {
        // action.getActive().getCosts().getReasons()
        Ref REF = action.getRef().getCopy();
        REF.setMatch(action.getRef().getTarget());
        REF.setID(KEYS.PAYEE, action.getSource().getId());
        return getReasonsCannotActivate(action.getActive(), REF);
        // Map<String, Condition> reqs =
        // action.getActive().getCosts().getRequirements().getReqMap();
        // for (Condition c : reqs.values()) {
        // if (!c.preCheck(REF)) {
        // reasons.add
        // (new MapMaster<String, Condition>().getKeyForValue(reqs, c));
        // }
        // }
        //
        // return reasons;
    }

    public static List<String> getReasonsCannotActivate(DC_ActiveObj active, Ref REF) {
        List<String> reasons = new ArrayList<>();
        REF.setID(KEYS.PAYEE, REF.getSource());
        if (active.getCosts().canBePaid(REF)) {
            return reasons;
        }
        return active.getCosts().getReasonList();
    }

    public static boolean checkReasonCannotTarget(FILTER_REASON reason, Action action) {
        return !getReasonsCannotTarget(action, false, false, reason).isEmpty();
    }

    public static List<FILTER_REASON> getReasonsCannotTarget(Action action
            //     , boolean ignoreOthersIfNotFacing
    ) {

        List<FILTER_REASON> reasonsCannotTarget = getReasonsCannotTarget(action, true, false, null);
        // if reasons are something other than single FACING, we don't care about OTHERS
        if (!reasonsCannotTarget.isEmpty()) {
            if (reasonsCannotTarget.size() != 1
                    && reasonsCannotTarget.get(0) != (FILTER_REASON.FACING)) {
                return reasonsCannotTarget;
            }
        }

        reasonsCannotTarget = getReasonsCannotTarget(action, true, true, null);
        return reasonsCannotTarget;
    }

    public static List<FILTER_REASON> getReasonsCannotTarget(Action action,
                                                             boolean useConditionResultCache, Boolean checkMiscOrOnly, FILTER_REASON searchedReason) {
        Ref REF = action.getRef().getCopy();
        REF.setMatch(action.getRef().getTarget());
        Targeting targeting = action.getActive().getTargeting();

        if (targeting == null) {
            targeting = TargetingMaster.findTargeting(action.getActive());
        }
        if (targeting == null) {
            return new ArrayList<>();
        }
        Conditions conditions = targeting.getFilter().getConditions();
        // conditions.preCheck(REF);
        List<FILTER_REASON> reasons = new ArrayList<>();
        for (Condition c : conditions) {
            FILTER_REASON reason = getReason(c);
            if (searchedReason != null) {
                if (reason != searchedReason) {
                    continue;
                }
            }
            if (checkMiscOrOnly == null) {
                if (reason != FILTER_REASON.OTHER) {
                    continue;
                }
            } else if (!checkMiscOrOnly) {
                if (reason == FILTER_REASON.OTHER) {
                    continue;
                }
            }
            //TODO ai Review - is this caching possible/needed?
            //= c.isTrue();
            // if (result==null || !useConditionResultCache) {
            boolean result = c.preCheck(REF);
            // }
            // else {
            //    LogMaster.log(1, c + " reason uses cached result: " + result+ " on " + REF);
            // }
            if (!result) {
                if (reason != null) {
                    reasons.add(reason);
                }
            }
        }

        return reasons;
    }

    private static FILTER_REASON getReason(Condition c) {
        if (c instanceof VisibilityCondition) {
            return FILTER_REASON.VISION;
        }

        if (c instanceof FacingCondition) {
            return FILTER_REASON.FACING;
        }

        if (c instanceof DistanceCondition) {
            return FILTER_REASON.DISTANCE;
        }

        if (c instanceof Conditions) // TODO ClassMaster
        {
            if (ConditionMaster.contains((Conditions) c, FacingCondition.class)) {
                return FILTER_REASON.FACING;
            }
        }
        return FILTER_REASON.OTHER;
    }

    public static boolean isAdjacentTargeting(Action targetAction) {
        Targeting targeting = targetAction.getActive().getTargeting();
        try {
            for (Condition c : targeting.getFilter().getConditions()) {
                if (c instanceof DistanceCondition) {
                    if (((DistanceCondition) c).getDistance().getInt(targetAction.getRef()) == 1) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    // kind of like analyzer here
    public enum FILTER_REASON {
        VISION, FACING, DISTANCE, IMMUNE, CONCEALMENT, SUICIDE, OTHER

    }

}
