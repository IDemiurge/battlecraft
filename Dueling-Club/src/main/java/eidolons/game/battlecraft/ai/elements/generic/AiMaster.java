package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.behavior.BehaviorMaster;
import eidolons.game.battlecraft.ai.advanced.companion.MetaGoalMaster;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.advanced.machine.AiPriorityConstantMaster;
import eidolons.game.battlecraft.ai.advanced.machine.PriorityProfile;
import eidolons.game.battlecraft.ai.advanced.machine.PriorityProfileManager;
import eidolons.game.battlecraft.ai.elements.actions.ActionManager;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.actions.sequence.PathSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.actions.sequence.TurnSequenceConstructor;
import eidolons.game.battlecraft.ai.elements.goal.GoalManager;
import eidolons.game.battlecraft.ai.elements.task.TaskManager;
import eidolons.game.battlecraft.ai.logic.types.atomic.AtomicAi;
import eidolons.game.battlecraft.ai.tools.*;
import eidolons.game.battlecraft.ai.tools.path.CellPrioritizer;
import eidolons.game.battlecraft.ai.tools.path.PathBuilder;
import eidolons.game.battlecraft.ai.tools.priority.PriorityManager;
import eidolons.game.battlecraft.ai.tools.priority.PriorityModifier;
import eidolons.game.battlecraft.ai.tools.priority.ThreatAnalyzer;
import eidolons.game.battlecraft.ai.tools.prune.PruneMaster;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.game.core.game.DC_Game;
import main.content.values.parameters.PARAMETER;
import main.game.ai.AI_Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiMaster {
    protected AI_Logic logic;

    protected DC_Game game;
    protected Unit unit;
    protected StringBuffer messageBuilder;
    protected TaskManager taskManager;
    protected GoalManager goalManager;
    protected ActionManager actionManager;
    protected PriorityManager priorityManager;
    protected PruneMaster pruneMaster;
    protected PathBuilder pathBuilder;
    protected TargetingMaster targetingMaster;
    protected Analyzer analyzer;
    protected ParamAnalyzer paramAnalyzer;
    protected ActionSequenceConstructor actionSequenceConstructor;
    protected AiExecutor executor;
    protected CellPrioritizer cellPrioritizer;
    protected PathSequenceConstructor pathSequenceConstructor;
    protected TurnSequenceConstructor turnSequenceConstructor;
    private SituationAnalyzer situationAnalyzer;
    private ThreatAnalyzer threatAnalyzer;
    private BehaviorMaster behaviorMaster;
    private AtomicAi atomicAi;
    private List<AiHandler> handlers = new ArrayList<>();
    private AiScriptExecutor scriptExecutor;
    private MetaGoalMaster metaGoalMaster;
    private AiPriorityConstantMaster priorityConstantMaster;
    private PriorityProfileManager priorityProfileManager;
    private PriorityModifier priorityModifier;

    public AiMaster(DC_Game game) {
        this.game = game;
        this.actionSequenceConstructor = new ActionSequenceConstructor(this);
        this.taskManager = new TaskManager(this);
        this.goalManager = new GoalManager(this);
        this.actionManager = new ActionManager(this);
        this.pruneMaster = new PruneMaster(this);
        this.pathBuilder = PathBuilder.getInstance(this);
        this.targetingMaster = new TargetingMaster(this);
        this.analyzer = new Analyzer(this);
        this.paramAnalyzer = new ParamAnalyzer(this);
        this.situationAnalyzer = new SituationAnalyzer(this);
        this.threatAnalyzer = new ThreatAnalyzer(this);
        this.cellPrioritizer = new CellPrioritizer(this);
        this.pathSequenceConstructor = new PathSequenceConstructor(this);
        this.turnSequenceConstructor = new TurnSequenceConstructor(this);
        this.behaviorMaster = new BehaviorMaster(this);
        this.atomicAi = new AtomicAi(this);
        this.scriptExecutor = new AiScriptExecutor(this);
        this.metaGoalMaster = new MetaGoalMaster(this);
        this.priorityConstantMaster = new AiPriorityConstantMaster(this);
        this.priorityProfileManager = new PriorityProfileManager(this);
        this.priorityModifier = new PriorityModifier(this);

        executor = new AiExecutor(game);


    }


    public void initialize() {
        this.actionSequenceConstructor.initialize();
        this.taskManager.initialize();
        this.goalManager.initialize();
        this.actionManager.initialize();
        this.pruneMaster.initialize();
        this.pathBuilder.initialize();
        this.targetingMaster.initialize();
        this.analyzer.initialize();
        this.paramAnalyzer.initialize();
        this.cellPrioritizer.initialize();
        this.pathSequenceConstructor.initialize();
        this.turnSequenceConstructor.initialize();
        this.behaviorMaster.initialize();
        this.atomicAi.initialize();
        this.threatAnalyzer.initialize();
        this.situationAnalyzer.initialize();
        this.metaGoalMaster.initialize();
        this.priorityConstantMaster.initialize();
        this.priorityProfileManager.initialize();
        this.priorityModifier.initialize();
    }

    public PriorityProfile getProfile() {
        return getPriorityProfileManager().getProfile();
    }

    public AiPriorityConstantMaster getPriorityConstantMaster() {
        return priorityConstantMaster;
    }

    public PriorityProfileManager getPriorityProfileManager() {
        return priorityProfileManager;
    }

    public PriorityModifier getPriorityModifier() {
        return priorityModifier;
    }

    public MetaGoalMaster getMetaGoalMaster() {
        return metaGoalMaster;
    }

    public BehaviorMaster getBehaviorMaster() {
        return behaviorMaster;
    }

    public AtomicAi getAtomicAi() {
        return atomicAi;
    }

    public AI_Logic getLogic() {
        return logic;
    }

    public StringBuffer getMessageBuilder() {
        if (messageBuilder == null)
            messageBuilder = new StringBuffer();
        return messageBuilder;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public PriorityManager getPriorityManager() {
        return priorityManager;
    }

    public PruneMaster getPruneMaster() {
        return pruneMaster;
    }

    public PathBuilder getPathBuilder() {
        return pathBuilder;
    }

    public TargetingMaster getTargetingMaster() {
        return targetingMaster;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public ParamAnalyzer getParamAnalyzer() {
        return paramAnalyzer;
    }

    public ActionSequenceConstructor getActionSequenceConstructor() {
        return actionSequenceConstructor;
    }

    public AiExecutor getExecutor() {
        return executor;
    }

    public CellPrioritizer getCellPrioritizer() {
        return cellPrioritizer;
    }

    public PathSequenceConstructor getPathSequenceConstructor() {
        return pathSequenceConstructor;
    }

    public TurnSequenceConstructor getTurnSequenceConstructor() {
        return turnSequenceConstructor;
    }

    public ThreatAnalyzer getThreatAnalyzer() {
        return threatAnalyzer;
    }

    public SituationAnalyzer getSituationAnalyzer() {
        return situationAnalyzer;
    }

    public float getParamPriority(PARAMETER p) {
        return getPriorityConstantMaster().getParamPriority(p);
    }

    public float getConstValue(AiConst p) {
        return getPriorityConstantMaster().getConstValue(p);
    }

    public int getConstInt(AiConst p) {
        return getPriorityConstantMaster().getConstInt(p);
    }

    public List<AiHandler> getHandlers() {
        return handlers;
    }

    public AiScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }

    public DC_Game getGame() {
        return game;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public UnitAI getUnitAI() {
        return unit.getAI();
    }
}