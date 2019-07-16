package eidolons.libgdx.bf.grid;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.puzzle.cell.MazePuzzle;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.anims.std.MoveAnimation;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.bf.overlays.HpBarManager;
import eidolons.system.audio.DC_SoundMaster;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallback;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Map;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 5/2/2018.
 */
public class GridManager {
    GridPanel panel;

    public GridManager(GridPanel panel) {
        this.panel = panel;

        GuiEventManager.bind(INGAME_EVENT_TRIGGERED, onIngameEvent());


    }

    public Map<BattleFieldObject, BaseView> getViewMap() {
        return panel.getViewMap();
    }

    private EventCallback onIngameEvent() {
        return param -> {
            Event event = (Event) param.get();
            Ref ref = event.getRef();

            boolean caught = false;

            if (event.getType() == STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED) {
                GuiEventManager.trigger(GuiEventType.EFFECT_APPLIED, event.getRef().getEffect());
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING
             || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE
             || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE) {
                if ((ref.getObj(KEYS.TARGET ) instanceof BattleFieldObject)) {
                BattleFieldObject hero = (BattleFieldObject) ref.getObj(KEYS.TARGET);
//                if (hero.isMainHero()) TODO this is an experiment (insane) feature...
//                    if (hero.isMine()) {
//                        turnField(event.getType());
//                    }
                BaseView view = getViewMap().get(hero);
                if (view != null && view instanceof GridUnitView) {
                    GridUnitView unitView = ((GridUnitView) view);
                    unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
//                    SoundController.getCustomEventSound(SOUND_EVENT.UNIT_TURNS, );
                    if (hero instanceof Unit)
                        DC_SoundMaster.playTurnSound((Unit) hero);
                }
                }
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_FALLEN_UNCONSCIOUS
             ) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_ON, ref.getSourceObj());
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
                if (!DeathAnim.isOn() || ref.isDebug()) {
                    GuiEventManager.trigger(DESTROY_UNIT_MODEL, ref.getTargetObj());
                }
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                if (!MoveAnimation.isOn()) //|| AnimMaster.isAnimationOffFor(ref.getSourceObj(), viewMap.get(ref.getSourceObj())))
                    panel.removeUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
               unitMoved(event.getRef().getSourceObj());
                caught = true;
            } else if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                caught = true;
            } else if (event.getType().name().startsWith("PROP_")) {
                caught = true;
            } else if (event.getType().name().startsWith("ABILITY_")) {
                caught = true;
            } else if (event.getType().name().startsWith("EFFECT_")) {
                caught = true;
            } else if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                if (GuiEventManager.isParamEventAlwaysFired(event.getType().getArg())) {
                    if (!HpBar.isResetOnLogicThread())
                        checkHpBarReset(event.getRef().getSourceObj());
                   }
                caught = true;
            }
            if (event.getType()==STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE) {
                if (!HpBar.isResetOnLogicThread())
                    checkHpBarReset( event.getRef().getTargetObj());
                caught = true;
            }

            if (!caught) {
             /*      System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
           */
            }
        };
    }

    public void unitMoved(Obj sourceObj) {
        if (!MoveAnimation.isOn() || AnimMaster.isAnimationOffFor(sourceObj,
                getViewMap().get(sourceObj))
        || sourceObj.getGame().getManager().getActiveObj()!= sourceObj
                //what about COUNTER ATTACK?!
                //TODO igg demo hack for force and teleport now...
        )
            panel.unitMoved((BattleFieldObject) sourceObj);
    }

    public void checkHpBarReset(Obj obj) {
        HpBarView view = (HpBarView) getViewMap().get(obj);
        if (view != null)
            if (view.getActor(). isVisible())
                if (view.getHpBar() != null)
                    if (
                     !ExplorationMaster.isExplorationOn()
                      || HpBarManager.canHpBarBeVisible((BattleFieldObject) view.getActor().getUserObject()))
                        view.resetHpBar();
    }


}
