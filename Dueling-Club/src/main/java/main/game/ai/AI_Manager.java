package main.game.ai;

import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.PLAYER_AI_TYPE;
import main.data.XLinkedMap;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.actions.ActionManager;
import main.game.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.ai.elements.generic.AiMaster;
import main.game.ai.elements.goal.GoalManager;
import main.game.ai.elements.task.TaskManager;
import main.game.ai.tools.AiExecutor;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.ai.tools.priority.PriorityManager;
import main.game.battlefield.Coordinates;
import main.game.core.game.DC_Game;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AI_Manager extends AiMaster {
    static Set<Action> brokenActions = new HashSet<>();
    private static GroupAI customGroup;
    private static boolean running;
    private static GroupAI allyGroup;
    private static GroupAI enemyGroup;
    private   ExecutorService executorService;
    private Map<Unit, UnitAI> aiMap = new XLinkedMap<>();
    private PLAYER_AI_TYPE type = AiEnums.PLAYER_AI_TYPE.BRUTE;

    public AI_Manager(DC_Game game) {
        super(game);
//        logic = initLogic();
        priorityManager = DC_PriorityManager.init(this);
    }

    public static GroupAI getAllyGroup() {
        if (allyGroup == null) {
            allyGroup = new GroupAI(null);
        }
        return allyGroup;
    }


    public static GroupAI getEnemyGroup() {
        if (enemyGroup == null) {
            enemyGroup = new GroupAI(null);
        }
        return enemyGroup;
    }

    public static Unit chooseEnemyToEngage(Unit obj, List<Unit> units) {
        if (obj.getAiType() == AiEnums.AI_TYPE.CASTER) {
            return null;
        }
        if (obj.getAiType() == AiEnums.AI_TYPE.ARCHER) {
            return null;
        }
        if (obj.getAiType() == AiEnums.AI_TYPE.SNEAK) {
            return null;
        }
        Unit topPriorityUnit = null;
        int topPriority = -1;
        for (Unit u : units) {
            int priority = DC_PriorityManager.getUnitPriority(u, true);
            if (priority > topPriority) {
                topPriority = priority;
                topPriorityUnit = u;
            }
        }
        return topPriorityUnit;
    }

    public static GroupAI getCustomUnitGroup(Unit unit) {
        if (unit.isMine())
            return
                    getAllyGroup();
        return getEnemyGroup();
    }

    public static boolean isRunning() {
        return running;
    }

    public static Set<Action> getBrokenActions() {
        return brokenActions;
    }

    public void init() {
        initialize();
        game.getPlayer(false).setPlayerAI(new PlayerAI(getType()));
    }

    public Action getAction(Unit unit) {
        if (unit.isMine()) {
            unit.getQuickItemActives();
        }
        Action action = null;
        running = true;
        setUnit(unit);
        Coordinates bufferedCoordinates = unit.getCoordinates();
        try {
            action = actionManager.chooseAction(getAI(unit));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            running = false;
        }
        if (action == null) {
            running = true;
            try {
                action = actionManager.getForcedAction(getAI(unit));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                running = false;
            }
        }
        if (!bufferedCoordinates.equals(unit.getCoordinates())) {
            unit.setCoordinates(bufferedCoordinates);
        }
        return action;
    }

    public boolean makeAction(final Unit unit) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        getExecutorService().execute(() -> {
            Action action = getAction(unit);
                if (action == null) {
                    game.getManager().freezeUnit(unit);
                    game.getManager().unitActionCompleted(null, true);
                } else {
                    try {
                        getAI(unit).setLastAction(action);
                        if (!executor.execute(action)) {
                            brokenActions.add(action);
                        } else {
                            brokenActions.remove(action);
                        }
                        getAI(unit).standingOrderActionComplete();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        // TODO block action, and try again!
                    }
                }

//            WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_COMPLETE);
         });
//        , unit.getName() + " AI Thread").start();

        return true;

    }

    public   ExecutorService getExecutorService() {
        if (executorService == null)
            executorService = Executors.newSingleThreadExecutor();
        return executorService;
    }

    public UnitAI getAI(Unit unit) {
        return unit.getUnitAI();
    }

    public DC_Game getGame() {
        return game;
    }

    public PriorityManager getPriorityManager() {
        return priorityManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public ActionSequenceConstructor getActionSequenceConstructor() {
        return actionSequenceConstructor;
    }

    public AiExecutor getExecutor() {
        return executor;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public PLAYER_AI_TYPE getType() {
        return type;
    }

    public void setType(PLAYER_AI_TYPE type) {
        this.type = type;
    }


}
