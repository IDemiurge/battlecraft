package main.game.battlecraft.ai.elements.actions.sequence;

import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.AiActionFactory;
import main.game.battlecraft.ai.elements.actions.AiQuickItemAction;
import main.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.goal.Goal;
import main.game.battlecraft.ai.elements.goal.GoalManager;
import main.game.battlecraft.ai.elements.task.Task;
import main.game.battlecraft.ai.elements.task.TaskManager;
import main.game.battlecraft.ai.tools.path.ActionPath;
import main.game.battlecraft.ai.tools.target.ReasonMaster;
import main.game.battlecraft.ai.tools.target.ReasonMaster.FILTER_REASON;
import main.game.battlecraft.ai.tools.target.TargetingMaster;
import main.game.battlecraft.ai.tools.time.TimeLimitMaster;
import main.game.bf.Coordinates;
import main.game.battlecraft.logic.battlefield.DC_MovementManager;
import main.game.core.game.Game;
import main.entity.active.DC_ActionManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ActionSequenceConstructor extends AiHandler {

    int defaultDistancePruneFactor = 3;
    private Game game;
    private Unit unit;
    private List<Coordinates> prioritizedCells;

    public ActionSequenceConstructor(AiHandler master) {
        super(master);
    }


    public List<ActionSequence> createActionSequences(UnitAI ai) {
        List<ActionSequence> list = new ArrayList<>();
        getActionSequenceConstructor().setPrioritizedCells(null);
        boolean forced=false;
        if (ai.getCurrentOrder()!=null )
            forced = true;
        for (GOAL_TYPE type : GoalManager.getGoalsForUnit(ai)) {
            List<ActionSequence> sequences = null;
            try {
                Goal goal=new Goal(type, null // ???
                 , ai);
                goal.setForced(forced);
                sequences = createActionSequences(goal, ai);
                list.addAll(sequences);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    private String getChronosPrefix() {
        return "TIMED AI ACTION ";
    }

    public List<ActionSequence> createActionSequences(Goal goal, UnitAI ai) {
        List<ActionSequence> actionSequences = new LinkedList<>();
        List<DC_ActiveObj> actions = AiUnitActionMaster.getFullActionList(goal.getTYPE(), ai.getUnit());
        actions.addAll(addSubactions(actions));
        for (DC_ActiveObj action : actions) {
            Chronos.mark(getChronosPrefix() + action);
            List<Task> tasks = getTaskManager().getTasks(goal.getTYPE(), ai, goal.isForced(), action);
            for (Task task : tasks) {
                if (task.isBlocked()) {
                    continue;
                }
                if (actionSequences.size() > 0) {
                    long time = TimeLimitMaster.getTimeLimitForAction();
                    if (Chronos.getTimeElapsedForMark(getChronosPrefix() + action) > time) {
                        LogMaster.log(1, "*********** TIME ELAPSED FOR  "
                         + action + StringMaster.wrapInParenthesis(time + ""));
                        break;
                    }
                }
                String string = task.toString();
                Obj obj = action.getGame().getObjectById((Integer) task.getArg());
                if (obj != null) {
                    string = obj.getName();
                }
                Chronos.mark(getChronosPrefix() + string);
                try {
                    addSequences(task, actionSequences, action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Chronos.logTimeElapsedForMark(getChronosPrefix() + string);
            }
            Chronos.logTimeElapsedForMark(getChronosPrefix() + action);
        }
        return actionSequences;
    }

    private void addSequences(Task task, List<ActionSequence> sequences, DC_ActiveObj active) {
        Ref ref = task.getUnit().getRef().getCopy();
        Integer arg = TaskManager.checkTaskArgReplacement(task, active);
//        if (arg == null) {
//       if (isArgNeeded(active))     return;
//        }
        ref.setTarget(arg);
        List<ActionSequence> newSequences = null;
        Action action = AiActionFactory.newAction(active, ref);
        action.setTask(task);
        try {
            newSequences = getSequences(action, task.getArg(), task);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ListMaster.isNotEmpty(newSequences)) {
            sequences.addAll(newSequences);
        } else {
            // if no pathing is required/available [QUICK FIX]
            if (!action.canBeTargetedOnAny()) {
                return;
            }
            ActionSequence sequence = getSequence(action, task);
            if (sequence != null) {
                if (active.isRanged()) {
                    sequences.addAll(AiUnitActionMaster.splitRangedSequence(sequence));
                } else {
                    sequences.add(sequence);
                }
            } else {

                // if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_NONE)
                // TODO smarter logging?
                // main.system.auxiliary.LogMaster.log(1, "***" +
                // action.toString()
                // + " could not be constructed into an action sequence!");

                // return;
            }
        }
    }


    private List<DC_ActiveObj> addSubactions(List<DC_ActiveObj> actions) {
        List<DC_ActiveObj> subactions = new LinkedList<>();
        for (DC_ActiveObj a : actions) {
            subactions.addAll(a.getSubActions());
        }
        return subactions;
    }


    public List<ActionSequence> getSequences(Action action, Object arg, Task task) {
        List<ActionSequence> list = new ArrayList<>();
        game = action.getRef().getGame();
        unit = action.getSource();

        if (task.getAI().getBehaviorMode() == AiEnums.BEHAVIOR_MODE.PANIC) {
            // target action = FLEE;
        }
        if (task.getType() == AiEnums.GOAL_TYPE.RETREAT) {
            {
                List<ActionSequence> sequencesFromPaths = getSequencesFromPaths(
                 getPathSequenceConstructor().getRetreatPaths(arg), task, action);
                return sequencesFromPaths;
            } // TODO
        }

        boolean singleAction = action.isSingle();
        if (!singleAction) {
            if (arg != null) {
                singleAction =
                 // action.canBeTargeted(StringMaster.getInteger(arg
                 // .toString()));

                 TargetingMaster.canBeTargeted(action, true);
            } else {
                singleAction = (action).canBeActivated();
            }
        }
        // if (!singleAction)
        // if (ReasonMaster.getReasons(action).getOrCreate(0)==FILTER_REASON.FACING)
        if (singleAction) {
            ActionSequence sequence = getSequence(action, task);
            if (sequence == null) {
                return null;
            }
            list.add(sequence);
            return list;
        }
        if (!task.isForced()) {
            if (task.getType() != AiEnums.GOAL_TYPE.ATTACK && task.getType() != AiEnums.GOAL_TYPE.RETREAT
             && task.getType() != AiEnums.GOAL_TYPE.SEARCH && task.getType() != AiEnums.GOAL_TYPE.MOVE
             && !task.getType().isBehavior()) {
                return null;
            }
        }
        if (task.getType() == AiEnums.GOAL_TYPE.SUMMONING) {
            return null;
        }
        if (task.getType() == AiEnums.GOAL_TYPE.ZONE_DAMAGE) {
            return null; // TODO until pathing/cell-pr. is fixed
        }
        if (!task.isForced()) {
            if (task.getAI().checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
                return null;
            }

            if (task.getAI().getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
                return null;
            }
            if ((!action.getActive().isRanged() && task.getAI().getType() == AiEnums.AI_TYPE.ARCHER)
             || (task.getAI().getType() == AiEnums.AI_TYPE.CASTER && !unit.getSpells().isEmpty())) {
                return null;
            }
        }

        Unit unit = (Unit) action.getRef().getSourceObj();
        List<DC_ActiveObj> moveActions = getMoveActions(action);

        if (!ListMaster.isNotEmpty(moveActions)) {
            // [QUICK FIX]
            if (!unit.getAction(DC_ActionManager.STD_ACTIONS.Turn_Anticlockwise.name())
             .canBeActivated(action.getRef(), true)
             && !unit.getAction(DC_ActionManager.STD_ACTIONS.Move.name()).canBeActivated(
             action.getRef(), true)) {
                return null;
            }
        } else {
            // if (prioritizedCells == null)
            // prioritizedCells = CellPrioritizer
            // .getMeleePriorityCellsForUnit(unit.getUnitAI(),
            // moveActions, action);
        }

        List<ActionPath> paths = getPathSequenceConstructor().getPathSequences(moveActions, action);
        list = getSequencesFromPaths(paths, task, action);
        if (list.isEmpty()) {
            paths = getPathSequenceConstructor().getPathSequences(moveActions, action);
            list = getSequencesFromPaths(paths, task, action);
        }
        return list;

    }

    private List<ActionSequence> getSequencesFromPaths(List<ActionPath> paths, Task task,
                                                       Action action) {
        List<ActionSequence> list = new ArrayList<>();
        for (ActionPath path : paths) {
            ActionSequence sequence = new ActionSequence(path.getActions(), task, task.getAI());
            if (action.getActive().isRanged()) {
                List<Action> rangedAttackSequence = getAttackSequence(action, task);
                if (rangedAttackSequence.isEmpty()) {
                    return list;
                }
                // TODO
                sequence.getActions().addAll(rangedAttackSequence);
            } else {
                sequence.getActions().add(action);
            }
            list.add(sequence);
        }
        return list;
    }

    private List<AiQuickItemAction> getRangedReloadAction(Action action) {
        Obj weapon = action.getActive().getRef().getObj(KEYS.RANGED);
        WEAPON_GROUP weapon_group = null;
        List<AiQuickItemAction> list = new ArrayList<>();
        if (weapon instanceof DC_WeaponObj) {
            DC_WeaponObj dc_WeaponObj = (DC_WeaponObj) weapon;
            if (dc_WeaponObj.getWeaponGroup() == ItemEnums.WEAPON_GROUP.BOWS) {
                weapon_group = ItemEnums.WEAPON_GROUP.ARROWS;
            }
            if (dc_WeaponObj.getWeaponGroup() == ItemEnums.WEAPON_GROUP.CROSSBOWS) {
                weapon_group = ItemEnums.WEAPON_GROUP.BOLTS;
            }

            for (DC_QuickItemObj ammo : action.getSource().getQuickItems()) {
                if (!ammo.isAmmo()) {
                    continue;
                }
                ammo.construct();
                if (ammo.getWrappedWeapon().getWeaponGroup() == weapon_group) {
                    AiQuickItemAction qia = new AiQuickItemAction(ammo);
                    if (qia.canBeActivated()) {
                        list.add(qia);
                    }

                }
            }
        }
        return list;
    }

    private List<DC_ActiveObj> getMoveActions(Action action) {

        // QUICK FIX
        if (ReasonMaster.checkReasonCannotTarget(FILTER_REASON.FACING, action)) {
            return null;
        }
        return DC_MovementManager.getMoves(unit);
    }

    public ActionSequence getSequence(Action targetAction, Task task) {
        List<Action> actions = new ArrayList<>();
        UnitAI ai = task.getAI();
        targetAction.getRef().setID(KEYS.ACTIVE, targetAction.getActive().getId());
        switch (task.getType()) {
            case ATTACK: {
                actions = getAttackSequence(targetAction, task); // only facing!
                break;
            }
            case DEBUFF:
            case BUFF:
                actions.addAll(getTurnSequenceConstructor().getTurnSequence(targetAction));
                actions.add(targetAction);
                break;
            // case RETREAT:
            // // preCheck *FLEE*
            // // actions = getMoveSequence(targetAction, task);
            // break;
            // case SEARCH:
            // break;
            // case SELF:
            // break;
            // case ZONE_SPECIAL:
            // break;
            default:
                actions.add(targetAction);
                break;
        }
        if (actions.isEmpty()) {
            return null;
        }
        // not very good
        Action action = actions.get(0);
        if (!action.canBeActivated()) {
            LogMaster.log(1, "No sequence for "
             + actions.get(actions.size() - 1) + " - " + action.getActive().getName() + ": "
             + action.getActive().getCosts().getReason());
            return null;
        }

        return new ActionSequence(actions, task, ai);
    }

    private List<Action> getAttackSequence(Action targetAction, Task task) {
        List<Action> list = new ArrayList<>();
        if (task.getArg() instanceof Integer) {
            Integer id = (Integer) task.getArg();

//            if (targetAction.getActive().isRanged()) {
//                targetAction.getActive().setForcePresetTarget(true);
//                if (!targetAction.canBeActivated()) {
//                    if (ReasonMaster.checkReasonCannotActivate(targetAction,
//                            SPECIAL_REQUIREMENTS.REF_NOT_EMPTY.getText(KEYS.RANGED.toString(),
//                                    KEYS.AMMO.toString()))) {
//                        List<AiQuickItemAction> reloadActions = getRangedReloadAction(targetAction);
//                        // will then split- ActionManager.splitRangedSequence()
//                        if (reloadActions.isEmpty()) {
//                            return list;
//                        }
//                        list.addAll(reloadActions);
//                    }
//                }
//            }
            if (targetAction.canBeTargeted(id)) {
                list.add(targetAction);
            } else {
                List<FILTER_REASON> reasons = ReasonMaster.getReasonsCannotTarget(targetAction);
                reasons.remove(FILTER_REASON.VISION); // ??
                if (reasons.size() > 1 && !targetAction.getActive().isRanged()) {
                    return list;
                }
                if (reasons.contains(FILTER_REASON.FACING)) {
                    list.addAll(getTurnSequenceConstructor().getTurnSequence(targetAction));
                    list.add(targetAction);
                } else if (targetAction.getActive().isRanged()) {
                    list.add(targetAction);
                }
            }
        }

        return list;
    }

    private Coordinates getNextClosestCoordinate(Unit unit, Action targetAction) {
        // TODO Auto-generated method stub
        return null;
    }


    // getOrCreate the *best* move sequence? create all then auto-compare via priority
    // manager
    // private   Collection<? extends Action> getMoveSequence(Action
    // action) {
    // Conditions conditions = (action.getActive()
    // .getTargeting().getFilter().getConditions());
    // DistanceCondition condition = null;
    // for (Condition c : conditions) {
    // if (c instanceof DistanceCondition) {
    // condition = (DistanceCondition) c;
    // break;
    // }
    // }
    // Formula distance = condition.getDistance();
    // Coordinates target_coordinates = action.getRef().getTargetObj()
    // .getCoordinates();
    // Coordinates source_coordinates = action.getRef().getSourceObj()
    // .getCoordinates();
    // List<DC_ActiveObj> moveActions = ActionManager
    // .getFullActionList(GOAL_TYPE.CLOSE_IN, (DC_HeroObj) action
    // .getRef().getSourceObj());
    //
    // Condition cellConditions = new Conditions(new NotCondition(
    // new OccupiedCondition(KEYS.MATCH.toString())),
    // new DistanceCondition(distance, KEYS.MATCH.toString(),
    // KEYS.TARGET.toString()));
    // List<Obj> viableCoordinates = new Filter<Obj>(action.getRef(),
    // cellConditions).filter(DC_Game.game.getCells());
    // target_coordinates = PositionMaster
    // .getClosestCoordinate(source_coordinates, viableCoordinates);
    // // List<ActionSequence> sequences = new ArrayList<>();
    // // for (Obj cell : viableCoordinates) {
    // // ActionSequence sequence = generateMoveSequence(source_coordinates,
    // // cell
    // // .getCoordinates(), moveActions);
    // // sequences.add(sequence);
    // // }
    // // // create a list of sequences, then choose by priority
    // // // or perhaps just go for the closest cell always, what could be the
    // // // reason not to? Nothing that isn't above AI's level! :)
    // // PriorityManager.sortByPriority(sequences);
    // return generateMoveSequence(source_coordinates, target_coordinates,
    // moveActions)
    // .getActions();
    // }
    //
    // private   ActionSequence generateMoveSequence(Coordinates
    // source_coordinates, Coordinates target_coordinates, List<DC_ActiveObj>
    // moveActions) {
    // // how do I simulate special moves best?
    // // or is it like we just generate all the possible sequences and
    // // choose... by shortness?
    // // still, I have to simulate the action's effect!
    // // and since their effects are "selective"...
    // // in the worst case, I can of course settle for ignoring the special
    // // moves...
    //
    // // for (DC_ActiveObj action : moveActions)
    // // if (action.getName().equals(STD_ACTIONS.Move.name()))
    // // standardMove = action;
    //
    // // let's built a path cell by cell then, ensuring the right facing at
    // // each step!
    // // maybe not "turning sequence", but just *individual turns* for each
    // // move if necessary
    //
    // // yes, i can build such a path for *std moves*...
    //
    // // I would want units to
    //
    // // PriorityManager.sortByPriority(sequences);
    // // return sequences.getOrCreate(0);
    //
    // return null;
    // }

    public List<Coordinates> getPrioritizedCells() {
        return prioritizedCells;
    }

    public void setPrioritizedCells(List<Coordinates> prioritizedCells) {
        this.prioritizedCells = prioritizedCells;
    }

    // perhaps it should build a move sequence for each cell *from which*
    // the targeting can be done? pruned of course by proximity...
    // this way flyers would be able to land behind their targets
    // use these move actions to build a path

}