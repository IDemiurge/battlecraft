package main.system.ai.logic.behavior;

import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.rules.DC_ActionManager.STD_ACTIONS;
import main.system.ai.GroupAI;
import main.system.ai.UnitAI;
import main.system.ai.UnitAI.AI_BEHAVIOR_MODE;
import main.system.ai.logic.actions.Action;
import main.system.ai.logic.goal.Goal.GOAL_TYPE;
import main.system.ai.logic.path.ActionPath;
import main.system.ai.logic.path.PathBuilder;
import main.system.auxiliary.ListMaster;

public class BehaviorMaster {
    /*
	 * 	No priority-ordering for Behaviors I suppose... 
	 * Though I may want to pick optimal path for Stalking, e.g., based on some special factors like
	 * proximity, path's illumination, ...
	 * and for Search also, there will definitely be options to choose from, but for now, I can 
	 * make it like a SM-Wandering more or less... 
	 */

    private boolean recursion;

    public static AI_BEHAVIOR_MODE initGroupPref(GroupAI groupAI) {
        if (groupAI.getCreepGroup() != null) {

        }
        // else groupAI.getLeader();

        switch (groupAI.getEngagementLevel()) {
            case AGGRO:
                break;
            case ALARMED:
                break;
            case SUSPECTING:
                break;
            case UNSUSPECTING:
                break;

        }
        return AI_BEHAVIOR_MODE.WANDER;
    }

    private static GOAL_TYPE getGoalFromBehavior(AI_BEHAVIOR_MODE b) {
        switch (b) {
            case AGGRO:
                return (GOAL_TYPE.AGGRO);
            case AMBUSH:
                return (GOAL_TYPE.AMBUSH);
            case GUARD:
                return (GOAL_TYPE.GUARD);
            case PATROL:
                return (GOAL_TYPE.PATROL);
            case STALK:
                return (GOAL_TYPE.STALK);
            case WANDER:
                return (GOAL_TYPE.WANDER);
        }
        return GOAL_TYPE.MOVE;
    }

    public AI_BEHAVIOR_MODE getBehavior(UnitAI ai) {
        // for (AI_BEHAVIOR_MODE b : ai.getBehaviors()) {
        // // priority? situation? check each...
        // }
        return ai.getGroup().getBehaviorPref(); // check unit is viable?

    }

    public Action getBehaviorAction(UnitAI ai) {
        GOAL_TYPE type = getGoalFromBehavior(getBehavior(ai));
        return getAction(type, ai);
    }

    private Action getAction(GOAL_TYPE type, UnitAI ai) {

        String action = null;
        Integer target = null;

        // doesn't the group have standing orders as a whole?..
        DC_HeroObj unit = ai.getUnit();
        Ref ref = new Ref(unit);
        GroupAI group = ai.getGroup();
        // checkBehaviorChange(group); where does that happen?
        switch (type) {
            case AMBUSH:
                break;
            case STALK:
                break;
            case GUARD:
            case PATROL:
                PatrolMaster.getPatrolAction(ai);
            case SEARCH: // having already turned on the Mode
            case WANDER:
                if (ai.isLeader()) {
                    Boolean change = WanderMaster.checkWanderDirectionChange(group, type);
                    if (change == null) {
                        action = getIdleAction(ai, type);
                        change = true;
                    }
                    // TODO IDEA: change only on LEADER's TURN, and for others,
                    // either proceed or WAIT; >> Updating blocked() status? or
                    // maybe go meet leader if blocked... or something like it
                    if (change) {
                        group.getWanderStepCoordinateStack().push(
                                group.getLeader().getCoordinates());
                        WanderMaster.changeGroupMoveDirection(group, type);
                    }
                }
                boolean wait = false;
                // ActionSequenceConstructor.getSequence(targetAction, task)
                Coordinates targetCoordinates = WanderMaster.getCoordinates(type, ai);
                if (targetCoordinates == null) {
                    wait = true;
                    // if (!recursion)
                    // return null;
                    // recursion = true;
                    // return getAction(type, ai);
                } else {
                    action = STD_ACTIONS.Move.name();
                    // if (!unit.getAction(action).canBeActivated()) {
                    // }
                    ActionPath path = new PathBuilder(new ListMaster<DC_ActiveObj>().getList(unit
                            .getAction(action)), new Action(unit.getAction(action), new Ref(unit)))
                            .getPathByPriority(new ListMaster<Coordinates>()
                                    .getList(targetCoordinates));
                    if (path == null) {

                        ai.setPathBlocked(true); // TODO check if path
                        // appropriate
                    } else {
                        ai.setPathBlocked(false);
                        return path.getActions().get(0);
                    }
                }
                // null if waiting for catchers up or so... turning and resting
                // etc

                if (wait)
                    action = getIdleAction(ai, type);
                else {
                    // if (change) {
                    // targetCoordinates = WanderMaster.getCoordinates(type,
                    // ai);
                    // }
                    // return path.getActions().get(0);
                }

                // List<Action> s =
                // ActionSequenceConstructor.getTurnSequence(FACING_SINGLE.IN_FRONT,
                // unit, targetCoordinates);
                // if (!s.isEmpty())
                // return s.get(0); // cache for speed-up immediate follow up
                // target =
                // ai.getUnit().getGame().getCellByCoordinate(targetCoordinates).getId();

                // TODO if the unit is 'ahead' of his buddies, he should just
                // wait
                // so there is a 'target cell'? Or is it measured by distance?
                // All going in same
                // direction until a unit gets too far ahead/behind (?) then
                // just wait till catch up (limit) and set new direction
                // limit by max distance from *origin*...

                // break;

        }

        DC_ActiveObj active = unit.getAction(action);

        ref.setTarget(target);
        recursion = false;
        return new Action(active, ref);

    }

    private String getIdleAction(UnitAI ai, GOAL_TYPE type) {
        // TODO turn randomly?
        return null;
    }

    public enum TACTIC {
        DELAY, STALK, AMBUSH, ENGAGE
    }

    public enum LOGIC {
        AVOID_TRAPS,
    }
}