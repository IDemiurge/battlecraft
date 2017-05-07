package main.game.battlecraft.ai;

import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.CONTENT_CONSTS2.ORDER_TYPE;
import main.content.C_OBJ_TYPE;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.obj.DC_Cell;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.ai.advanced.companion.CompanionMaster;
import main.game.battlecraft.ai.advanced.companion.Order;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.tools.AiExecutor;
import main.game.bf.Coordinates;
import main.game.module.dungeoncrawl.ai.DungeonCrawler.ENGAGEMENT_LEVEL;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;

import java.util.*;
import java.util.stream.Collectors;

public class UnitAI {

    public static final int LOG_LEVEL_FULL = 2;
    public static final int LOG_LEVEL_BASIC = 1;
    public static final int LOG_LEVEL_RESULTS = 0;
    public static final int LOG_LEVEL_NONE = -1;
    private static final Integer DEFAULT_VERBATIM_MOD = null;
    Unit unit;
    AI_TYPE type;
    AiExecutor executor;
    AI_BEHAVIOR_MODE currentBehavior;
    private boolean inSequence;
    private List<Action> forcedActions;
    private Map<String, Action> actions = new XLinkedMap<>();
    private Map<ObjType, Integer> actionPriorityMods;
    private Map<ObjType, Integer> actionPriorityBonuses;
    private Action lastAction;
    private Coordinates originalCoordinates;
    private boolean engaged;
    private ActionSequence standingOrders;
    private GroupAI groupAI;
    private ORDER_TYPE orderType;
    private boolean ordered;
    private boolean pathBlocked;
    private int logLevel = LOG_LEVEL_RESULTS;
    private Order currentOrder;
    private Map<GOAL_TYPE, Object> argMap;

    public UnitAI(Unit unit) {
        this.unit =   unit;
        initType();
        setOriginalCoordinates(unit.getCoordinates());

    }



    private void initType() {
        type = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class, unit
         .getProperty(PROPS.AI_TYPE));
        if (unit.isMine() || type ==null ){
            CompanionMaster.initCompanionAiParams(this);
        }
    }

    public int getLogLevel() {
        // TODO group leader? by level? By selection?
        return logLevel;

    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    // private Map<String, Integer> actionPriorityConsts = new
    // XLinkedMap<String, Integer>();

    @Override
    public String toString() {
        return "AI: " + getUnit();
    }

    private void initActionPriorities() {
        actionPriorityBonuses = RandomWizard.constructWeightMap(unit
                .getProperty(PROPS.ACTION_PRIORITY_BONUSES), C_OBJ_TYPE.ACTIVE);

        actionPriorityMods = RandomWizard.constructWeightMap(unit
                .getProperty(PROPS.ACTION_PRIORITY_MODS), C_OBJ_TYPE.ACTIVE);

        for (ObjType spell : DataManager.toTypeList(unit.getProperty(PROPS.VERBATIM_SPELLS),
                DC_TYPE.SPELLS)) {
            Integer mastery = unit.getIntParam(ContentManager.getSpellMasteryForSpell(spell));

            // WHY VERBATIM? MAYBE FROM *TYPE*, YES...

            int mod = DEFAULT_VERBATIM_MOD + mastery;

        }
        // default additions?
        /*
         * verbatim spell list per mastery
		 */
    }

    public synchronized Unit getUnit() {
        return unit;
    }

    public synchronized boolean isInSequence() {
        return inSequence;
    }

    public synchronized void setInSequence(boolean inSequence) {
        this.inSequence = inSequence;
    }

    public synchronized AiExecutor getExecutor() {
        return executor;
    }

    public synchronized void setExecutor(AiExecutor executor) {
        this.executor = executor;
    }

    public AI_TYPE getType() {
        if (type == null) {
            initType();
        }
        return type;
    }

    public void setType(AI_TYPE type) {
        this.type = type;
    }

    public List<Action> getForcedActions() {

        if (forcedActions == null) {
            forcedActions = new LinkedList<>();
        }
        return forcedActions;
    }

    public BEHAVIOR_MODE getBehaviorMode() {
        return unit.getBehaviorMode();

    }



    public Map<ObjType, Integer> getActionPriorityMods() {
        if (actionPriorityMods == null) {
            actionPriorityMods = new XLinkedMap<>();
        }
        return actionPriorityMods;
    }

    public void setActionPriorityMods(Map<ObjType, Integer> actionPriorityMods) {
        this.actionPriorityMods = actionPriorityMods;
    }

    public Map<ObjType, Integer> getActionPriorityBonuses() {
        if (actionPriorityBonuses == null) {
            actionPriorityBonuses = new XLinkedMap<>();
        }
        return actionPriorityBonuses;
    }

    public void setActionPriorityBonuses(Map<ObjType, Integer> actionPriorityBonuses) {
        this.actionPriorityBonuses = actionPriorityBonuses;
    }

    public boolean checkMod(AI_MODIFIERS trueBrute) {
        return unit.checkAiMod(trueBrute);
    }

    public Action getLastAction() {
        return lastAction;
    }

    public void setLastAction(Action action) {
        this.lastAction = action;
    }

    public Coordinates getOriginalCoordinates() {
        return originalCoordinates;
    }

    public void setOriginalCoordinates(Coordinates originalCoordinates) {
        this.originalCoordinates = originalCoordinates;
    }

    public int getMaxWanderDistance() {
        // default - percent of size? 'don't leave the Block'
        // getType()
        // checkMod(trueBrute)
        return 5;
    }

    public List<AI_BEHAVIOR_MODE> getBehaviors() {
        List<AI_BEHAVIOR_MODE> list = new LinkedList<>();
        if (checkAmbush()) {
            list.add(AI_BEHAVIOR_MODE.AMBUSH);
        }
        if (checkStalk()) {
            list.add(AI_BEHAVIOR_MODE.STALK);
        }
        if (checkAggro()) {
            list.add(AI_BEHAVIOR_MODE.AGGRO);
        }

        if (new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel()) < 1) {
            list.add(getPassiveBehavior());
        }

        return list;
    }

    private AI_BEHAVIOR_MODE getPassiveBehavior() {
        // return AI_BEHAVIOR_MODE.PATROL;
        // return AI_BEHAVIOR_MODE.GUARD;
        return AI_BEHAVIOR_MODE.WANDER;
    }

    private boolean checkAggro() {
        int index = new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel());
        if (index < 1) {
            return false;
        }
        if (index > 3) {
            return true;
        }
        return unit.getAiType() == AiEnums.AI_TYPE.BRUTE;
    }

    private boolean checkStalk() {
        // if (getGroup().getEngagementLevel()==ENGAGEMENT_LEVEL.UNSUSPECTING)
        if (new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel()) < 1) {
            return false;
        }
        return unit.getAiType() == AiEnums.AI_TYPE.SNEAK;
    }

    private boolean checkAmbush() {
        if (new EnumMaster<ENGAGEMENT_LEVEL>().getEnumConstIndex(getEngagementLevel()) < 1) {
            return false;
        }
        // intelligence preCheck? group preCheck?
        if (unit.getAiType() == AiEnums.AI_TYPE.ARCHER) {
            return true;
        }
        if (unit.getAiType() == AiEnums.AI_TYPE.CASTER) {
            return true;
        }
        // if (group.isAmbushing()) return true;
        // leader?
        return false;
    }

    private ENGAGEMENT_LEVEL getEngagementLevel() {

        return getGroup().getEngagementLevel();
    }

    public boolean isLeader() {
        return getGroup().getLeader() == getUnit();
    }

    public boolean isEngaged() {
        return engaged;
    }

    public void setEngaged(boolean engaged) {
        this.engaged = engaged;
    }

    public boolean checkStandingOrders() {
        // change orders
        checkOrdersChange();

        if (getStandingOrders() != null) {
            if (ListMaster.isNotEmpty(getStandingOrders().getActions())) {
                if (getStandingOrders().get(0).canBeActivated()) {
                    if (getStandingOrders().get(0).canBeTargeted()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getPlayerOrder() {
        // TODO Auto-generated method stub
        return null;
    }

    private void checkOrdersChange() {

        if (unit.isMine()) { // could be multiplayer!..
            if (isPlayerOrdered()) {
                // if (!isPromptDisabled())
                String TRUE = "Orders";
                String FALSE = "Proceed";
                String NULL = "Cancel"; // wait, resume last, on your own!
                Boolean result = DialogMaster.askAndWait("Do you want to change "
                        + getPlayerOrder() + "?", TRUE, FALSE, NULL);

                if (result == null) {

                }

            }
        }

        // groupAI.getOrCreate
        if (orderType == ORDER_TYPE.PURSUIT) {
            if (standingOrders.getLastAction().getTarget() instanceof DC_Cell) {
                DC_Cell cell = (DC_Cell) standingOrders.getLastAction().getTarget();
                cell.getCoordinates();

            } else if (orderType == ORDER_TYPE.WANDER) {
                // groupAI.getWanderDirection()
            }
        }

    }

    private boolean isPlayerOrdered() {
        return ordered;
    }

    public void setPlayerOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public void standingOrderActionComplete() {
        if (standingOrders != null) {
            standingOrders.removeFirstAction();
        }
    }

    public ActionSequence getStandingOrders() {
        return standingOrders;
    }

    public void setStandingOrders(ActionSequence standingOrders) {
        if (standingOrders.getType() == AiEnums.GOAL_TYPE.MOVE) {
            orderType = ORDER_TYPE.MOVE;
        } else if (standingOrders.getType() == AiEnums.GOAL_TYPE.WANDER) {
            orderType = ORDER_TYPE.WANDER;
        } else if (standingOrders.getType() == AiEnums.GOAL_TYPE.WANDER) {
            orderType = ORDER_TYPE.PATROL;
        } else if (standingOrders.getType() == AiEnums.GOAL_TYPE.STALK
                || standingOrders.getType() == AiEnums.GOAL_TYPE.AGGRO) {
            orderType = ORDER_TYPE.PURSUIT;
        }
        this.standingOrders = standingOrders;
    }

    public void checkSetOrders(ActionSequence sequence) {
        orderType = null;
        if ((sequence.getType() == AiEnums.GOAL_TYPE.WANDER)) {
            orderType = ORDER_TYPE.WANDER;
        }
        if ((sequence.getType() == AiEnums.GOAL_TYPE.PATROL)) {
            orderType = ORDER_TYPE.PATROL;
        }

        if (orderType != null) {
            standingOrders = sequence;
        }

    }

    public GroupAI getGroup() {
        return getGroupAI();
    }

    public boolean isPathBlocked() {
        return pathBlocked;
    }

    public void setPathBlocked(boolean pathBlocked) {
        this.pathBlocked = pathBlocked;
    }

    public GroupAI getGroupAI() {
        if (groupAI == null) {
            AI_Manager.getCustomUnitGroup(getUnit()).add(getUnit());
            groupAI = (AI_Manager.getCustomUnitGroup(getUnit()));
        }
        return groupAI;
    }

    public void setGroupAI(GroupAI groupAI) {
        this.groupAI = groupAI;
    }

    public AI_BEHAVIOR_MODE getCurrentBehavior() {
        return currentBehavior;
    }

    public void setCurrentBehavior(AI_BEHAVIOR_MODE currentBehavior) {
        this.currentBehavior = currentBehavior;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public Integer getGoalPriorityMod(GOAL_TYPE goalType) {
        if (currentOrder==null ) return null ;

        if (currentOrder.getStrictPriority()!=null ){
           return  Arrays.stream(currentOrder.getStrictPriority().getGoalTypes()).collect(Collectors.toList()).contains(goalType)
             ? 1000 : 0;
        }

        return currentOrder.getPriorityModsMap().get(goalType);
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Map<GOAL_TYPE, Object> getArgMap() {
        if (argMap==null )
            argMap = new HashMap<>();
        return argMap;
    }

    public void setArg(GOAL_TYPE goalType, Object arg) {
        getArgMap().put(goalType, arg);
    }

    public enum AI_BEHAVIOR_MODE {
        WANDER, AMBUSH, AGGRO, STALK, PATROL, GUARD,
    }

}