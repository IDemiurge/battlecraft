package eidolons.game.battlecraft.ai.tools.path;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.ActionManager;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.AiLogger;
import eidolons.game.battlecraft.ai.tools.target.ReasonMaster;
import eidolons.game.battlecraft.ai.tools.target.ReasonMaster.FILTER_REASON;
import eidolons.game.battlecraft.ai.tools.time.TimeLimitMaster;
import eidolons.game.battlecraft.ai.tools.time.TimeLimitMaster.METRIC;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.EUtils;
import eidolons.system.libgdx.GdxAdapter;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.elements.Filter;
import main.elements.costs.Costs;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.text.Log;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathBuilder extends AiHandler {
    private static final int FILTER_THRESHOLD = 10;
    private static final int MAX_PATH_SIZE = 8;
    private static PathBuilder instance;
    protected Coordinates targetCoordinate;
    private Unit unit;
    private Action targetAction;
    private List<Coordinates> targetCells;
    private Coordinates originalCoordinate;
    private Coordinates c_coordinate;
    private Coordinates previousCoordinate;
    private FACING_DIRECTION c_facing;
    private FACING_DIRECTION originalFacing;
    private FACING_DIRECTION previousFacing;

    // should these be dynamic? stronger units should getOrCreate more!
    private Choice base_choice;
    private List<ActionPath> paths;
    private List<ActionPath> filteredPaths;
    private ActionPath path;
    private Costs bestCost;
    private Integer bestResult;
    private boolean failed;

    private PathChoiceMaster pathChoiceMaster;
    private int timeLimit;
    public boolean simplified;

    private PathBuilder(AiMaster master) {
        super(master);
    }

    public static PathBuilder getInstance(AiMaster master) {
        instance = new PathBuilder(master);
        return instance;
    }

    public static PathBuilder getInstance() {
        return instance;
    }

    public PathBuilder init(List<DC_ActiveObj> moveActions, Action targetAction) {

        if (moveActions == null) {
            moveActions = new ArrayList<>(getUnit().getActionMap().get(ACTION_TYPE.STANDARD));
            moveActions.add(getUnit().getAction("Move"));
        }
        if (targetAction == null) {
            targetAction = AiActionFactory.newAction(("Move"), getUnitAi());
        }
        this.targetAction = targetAction;
        init();
        pathChoiceMaster.init(unit, targetAction, targetCoordinate, moveActions);
        return this;
    }

    private void init() {
        pathChoiceMaster = new PathChoiceMaster(this);
        unit = targetAction.getSource();
        originalCoordinate = unit.getCoordinates();
        originalFacing = unit.getFacing();
    }

    private void reset() {
        c_facing = originalFacing;
        c_coordinate = originalCoordinate;
        previousCoordinate = null;
        previousFacing = null;
        failed = false;

    }

    private void resetUnit() {
        unit.removeTempCoordinates();
        unit.removeTempFacing();

    }

    protected boolean checkEmpty(Coordinates c) {
        return unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit, c);
        // return !unit.getGame().getBattleField().getGrid().isOccupied(c);
        // unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit, c);
        // unit.getGame().getBattleField().getGrid().getObj(c) == null;
    }

    protected void adjustUnit() {
        unit.setTempCoordinates(c_coordinate);
        unit.setTempFacing(c_facing);
        unit.getGame().getRules().getStackingRule().clearCache();
    }

    public List<ActionPath> build(List<Coordinates> targetCoordinates
            , List<DC_ActiveObj> moveActions, Action targetAction) {
        this.targetAction = targetAction;
        init();
        pathChoiceMaster.init(unit, targetAction, targetCoordinate, moveActions);
        return build(targetCoordinates);
    }

    public List<ActionPath> build(List<Coordinates> targetCoordinates) {
        Filter.pathbuilding = true;
        try {
            paths = buildPaths(targetCoordinates);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            Filter.pathbuilding = false;
            resetUnit();
        }
        main.system.auxiliary.log.LogMaster.log(1,
                targetAction.getActive().getOwnerUnit().getNameAndCoordinate()
                        + "'s Paths: " + ContainerUtils.joinList(paths, Strings.NEW_LINE));

        try {
            filterPaths();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (filteredPaths.isEmpty()) {
            filteredPaths = paths;
        }
        if (!filteredPaths.isEmpty())
            if (filteredPaths.get(0).getActions().size() >= MAX_PATH_SIZE) {
                return filteredPaths;
            }
        return filteredPaths;
    }

    private List<ActionPath> buildPaths(List<Coordinates> targetCoordinates) {
        Chronos.mark(getChronosPrefix() + targetAction);
        paths = new ArrayList<>();
        bestResult = null;
        targetCells = targetCoordinates;

        // depth-first search
        loop:
        for (Coordinates dest : targetCells) {
            Chronos.mark(getChronosPrefix() + dest);
            targetCoordinate = dest;
            reset();
            path = new ActionPath(originalCoordinate);
            // TODO first step must be activateable!
            List<Choice> choices = pathChoiceMaster.getChoices(simplified, path, c_coordinate, targetCoordinate, c_facing);
            for (Choice choice : choices) {
                base_choice = choice;
                Chronos.mark(getChronosPrefix() + base_choice);
                path = new ActionPath(targetCoordinate);
                if (!step(choice)) {
                    break;
                }
                if (unit.getUnitAI().getLogLevel() > AiLogger.LOG_LEVEL_RESULTS) {
                    Chronos.logTimeElapsedForMark(getChronosPrefix() + choice); // TODO
                }
                if (!simplified)
                    if (!failed)
                        if (isDebug()) {
                            EUtils.showInfoText(true, "Ai built " + path);
                            DC_MovementManager.playerPath = path.choices.stream()
                                    .map(Choice::getCoordinates).collect(Collectors.toList());

                            DC_MovementManager.playerDestination = path.getTargetCoordinates();
                            GdxAdapter.onInputGdx_(() -> WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.INPUT, true));
                            WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.INPUT);
                        }
                // mark removed???
            }
            if (unit.getUnitAI().getLogLevel() > AiLogger.LOG_LEVEL_BASIC) {
                Chronos.logTimeElapsedForMark(getChronosPrefix() + dest);
            }
        }
        if (unit.getUnitAI().getLogLevel() > AiLogger.LOG_LEVEL_BASIC) {
            Chronos.logTimeElapsedForMark(getChronosPrefix() + targetAction);
        }
        return paths;
    }


    private void applyChoice(Choice choice) {
        previousCoordinate = c_coordinate;
        c_coordinate = choice.getCoordinates();
        if (choice.getTurns() != null) {
            previousFacing = c_facing;
            for (Boolean turn : choice.getTurns()) {

                c_facing = FacingMaster.rotate(c_facing, turn);
            }
        }
    }

    private boolean checkFinished() {
        // targetCells.contains(c_coordinate))
        // TODO how to ensure that all targets are arrived at by a path?
        return targetCells.contains(c_coordinate) || targetCoordinate.equals(c_coordinate);
    }

    private boolean step(Choice choice) {

        applyChoice(choice);
        path.add(choice);
        if (checkFinished()) {
            if (targetAction.getActive().getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.MOVE) {
                checkAddFaceTurn(); // TODO better preCheck?
            }
            if (checkFailed()) {
                return true;
            }
            finished();
            return true;
        }
        if (!TimeLimitMaster.checkTimeLimitForAi(getUnitAi()))
            return false;
        if (Chronos.getTimeElapsedForMark(getChronosPrefix() + targetAction) >
                (timeLimit > 0 ? timeLimit :
                        TimeLimitMaster.getTimeLimitForPathBuilding()
                                * TimeLimitMaster.CRITICAL_FAIL_FACTOR)) {
            Chronos.logTimeElapsedForMark(getChronosPrefix() + targetAction);
            LogMaster.log(1, "*** CRITICAL_FAIL TimeLimitForPathBuilding "
                    + targetAction);
            return false;
        }
        if (paths.size() > 0) {
            // if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH_STEP,
            // getChronosPrefix() + targetAction))
            // return false; TODO ???
            if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH, getChronosPrefix() + base_choice)) {
                return false;
            }
            if (!TimeLimitMaster.checkTimeLimit(METRIC.PATH_CELL, getChronosPrefix()
                    + targetCoordinate)) {
                return false;
            }
            if (!TimeLimitMaster.checkTimeLimit(METRIC.ACTION, getChronosPrefix() + targetAction)) {
                return false;
            }
        }
        if (checkFailed()) {
            return true;
        }

        List<Choice> choices = pathChoiceMaster.getChoices(simplified, path, c_coordinate, targetCoordinate, c_facing);
        // depth first search
        for (Choice nextChoice : choices) {
            if (!step(nextChoice)) {
                return false;
            }
            if (simplified)
                if (!paths.isEmpty())
                    return false;
            // if (!failed)
            clonePath();
            back();
        }
        return true;
    }

    private boolean checkDuplicate() {
        return paths.contains(path);
    }

    private void checkAddFaceTurn() {
        unit.setTempFacing(c_facing);
        unit.setTempCoordinates(c_coordinate);
        if (ReasonMaster.checkReasonCannotTarget(FILTER_REASON.FACING, targetAction)) {
            List<Action> sequence = getTurnSequenceConstructor().getTurnSequence(targetAction);
            for (Action a : sequence) {
                path.add(new Choice(c_coordinate, a));
            }
        }
        unit.setTempFacing(originalFacing);
        unit.setTempCoordinates(originalCoordinate);
    }

    private void clonePath() {
        path = new ActionPath(path);
        // reset();
    }

    private void back() {
        if (!path.getChoices().isEmpty()) {
            path.getChoices().remove(path.getChoices().size() - 1);
        }
        if (previousCoordinate != null) {
            c_coordinate = previousCoordinate;
        }
        previousCoordinate = null;
        if (previousFacing != null) {
            c_facing = previousFacing;
        }
        previousFacing = null;
    }

    private void finished() {
        if (checkDuplicate()) {
            return;
        }
        paths.add(path);
        int result = getPathPriority();
        log(result);

        if (bestResult == null) {
            bestResult = result;
        } else {
            if (result > bestResult) {
                bestResult = result;
            }
        }
        // Integer pathPriority = PriorityManager.getPathPriority(path);
        // Costs c;
        // c.compare(bestCost);
        // if (pathPriority >= bestPathPriority)
        // this.bestPathPriority = pathPriority;

    }

    private void log(int result) {
        LogMaster.log(LOG_CHANNEL.PATHING_DEBUG, result
                + " priority for path: " + path);
    }

    private boolean checkFailed() {
        failed = false;
        if (path.getChoices().size() > MAX_PATH_SIZE) {
            failed = true;
        } else {
            if (bestResult == null) {
                return failed;
            }
            Integer pathPriority = getPathPriority();

            if (pathPriority < bestResult || pathPriority == 0) {
                // if (isBestPathOnly())
                failed = true;
            }
        }
        return failed;
    }

    private Integer getPathPriority() {
        //TODO ai Review - why is this doing so badly?

        // Costs cost = getPathCosts(path);
        // int result = getPriorityManager().getCostFactor(cost, unit);
        // main.system.auxiliary.log.LogMaster.log(1,result+" COST FACTOR: " +cost);
        // int stackFactor = 0;
        // result = result
        //         * getParamAnalyzer().getActionNumberFactor(size) / 100;

        int result;
        path.setPriority(result =100/path.getActions().size());
        return result;
    }

    public Costs getPathCosts(ActionPath path) {
        return ActionManager.getTotalCost(path.getActions());
    }

    public ActionPath getPathByPriority(List<Coordinates> targetCoordinates) {
        paths = build(targetCoordinates);
        if (paths.isEmpty()) {
            return null;
        }
        return paths.get(0);
    }

    private String getChronosPrefix() {
        return "Building path for ";
    }

    private void filterPaths() {
        filteredPaths = new ArrayList<>();

        if (Log.check(Log.LOG_CASE.pathing))
            main.system.auxiliary.log.LogMaster.log(1, " Filtering against "
                    + bestResult);
        for (ActionPath p : paths) {
            int priority = p.getPriority();
            if (Log.check(Log.LOG_CASE.pathing))
                main.system.auxiliary.log.LogMaster.log(1, p + " paths priority = "
                        + priority);

            if (bestResult - priority >= FILTER_THRESHOLD) {
                continue;
            }
            filteredPaths.add(p);
        }
        if (Log.check(Log.LOG_CASE.pathing))
            main.system.auxiliary.log.LogMaster.log(1, targetAction + "'s Filtered Paths: " +
                    ContainerUtils.joinList(filteredPaths, Strings.NEW_LINE));

    }

    public Coordinates getPreviousCoordinate() {
        return previousCoordinate;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public PathBuilder setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    public void init(Unit unit, List<DC_ActiveObj> moveActions, Action targetAction) {
        this.unit = unit;
        if (targetAction == null) {
            targetAction = new Action(unit.getTurnAction(true));
        }
        init(moveActions, targetAction);

    }
}
