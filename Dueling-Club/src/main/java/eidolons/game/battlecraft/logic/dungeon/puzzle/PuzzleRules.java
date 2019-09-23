package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.system.ConditionsUtils;
import main.elements.conditions.Condition;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.entity.ConditionMaster;

public class PuzzleRules extends PuzzleElement {

    public enum PUZZLE_RULE_ACTION{
        FAIL,
        WIN,
        DEATH,
        COUNT_DOWN,
    }

    PUZZLE_RULE_ACTION action;
    PuzzleMaster.PUZZLE_ACTION_BASE base;

    public PuzzleRules(Puzzle puzzle) {
        super(puzzle);
       }

    public PuzzleRules(Puzzle puzzle, PUZZLE_RULE_ACTION action, PuzzleMaster.PUZZLE_ACTION_BASE base) {
        super(puzzle);
        this.action = action;
        this.base = base;
    }

    @Override
    public void started() {
        if (base == null) {
            return;
        }
         puzzle.createTrigger(PuzzleTrigger.PUZZLE_TRIGGER.ACTION,
                 getActionEvent(), getActionChecks(), createAction() );

    }
    //special rule for the puzzle
    protected Runnable createAction() {
        return ()->{
            switch (action) {
                case COUNT_DOWN:
                    puzzle.decrementCounter();
                    break;
            }
        };
    }

    protected Condition getActionChecks() {
        return ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAINHERO);
    }

    protected Event.EVENT_TYPE getActionEvent() {
        switch (base) {
            case FACING:
                return Event.STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING;
            case ACTION:
                return Event.STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE;
            case MOVE:
                return Event.STANDARD_EVENT_TYPE.UNIT_BEING_MOVED;
            case MOVE_AFTER:
                return   Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING;
        }
        return null;
    }

    public PUZZLE_RULE_ACTION getAction() {
        return action;
    }

    public void setAction(PUZZLE_RULE_ACTION action) {
        this.action = action;
    }

    public PuzzleMaster.PUZZLE_ACTION_BASE getBase() {
        return base;
    }

    public void setBase(PuzzleMaster.PUZZLE_ACTION_BASE base) {
        this.base = base;
    }

    public void manipulatorActs(Manipulator manipulator) {

        DC_Cell cell = DC_Game.game.getCellByCoordinate(Eidolons.getMainHero().getCoordinates());

        cell.setOverlayRotation(cell.getOverlayRotation()+90);

        GuiEventManager.trigger(GuiEventType.CELL_RESET, cell);

    }
}