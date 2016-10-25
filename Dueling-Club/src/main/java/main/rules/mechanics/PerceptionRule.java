package main.rules.mechanics;

import main.content.PARAMS;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.logic.dungeon.DungeonCrawler.ENGAGEMENT_LEVEL;
import main.game.logic.macro.rules.TurnRule;
import main.rules.action.ActionRule;
import main.system.ai.tools.Analyzer;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PerceptionRule extends TurnRule implements ActionRule {

    public void newTurn() {
        // TODO additional round of 'sniffing'

    }

    public void actionComplete(ActiveObj activeObj) {
        DC_HeroObj unit = (DC_HeroObj) activeObj.getOwnerObj();
        PERCEPTION_STATUS status;
        int actionNoise = activeObj.getIntParam(PARAMS.NOISE, false)
                + activeObj.getOwnerObj().getIntParam(PARAMS.NOISE, false);
        for (DC_HeroObj source : getUnits(activeObj)) {
//			status = getPerceptionStatus(source, activeObj, actionNoise);
            // map of statuses would be nice...
            // statusMap.get(source).put(unit, status);
//			checkResetPlayerPerceptionStatus(status, source, unit);
            // personal independent detection?
        }
        // GroupAI instead!!! Let it know where to look...
        // perceived(source, unit); // creeps' KNOW ... -> stop wandering
        // around!
        // this status

    }

    private void perceptionEventAI(PERCEPTION_STATUS status, DC_HeroObj source,
                                   DC_HeroObj target, ActiveObj action) {
        switch (status) {
            case KNOWN_TO_BE_SOMEWHERE:
                source.getUnitAI().getGroup()
                        .setEngagementLevel(ENGAGEMENT_LEVEL.ALARMED);
                break;
            case KNOWN_TO_BE_THERE:
                source.getUnitAI().getGroup()
                        .setEngagementLevel(ENGAGEMENT_LEVEL.AGGRO);
                source.getUnitAI().getGroup()
                        .addEnemyKnownCoordinates(target.getCoordinates());
                break;
            case SUSPECTED_TO_BE_SOMEWHERE:
                source.getUnitAI().getGroup()
                        .setEngagementLevel(ENGAGEMENT_LEVEL.SUSPECTING);
                break;
            case SUSPECTED_TO_BE_THERE:
                source.getUnitAI().getGroup()
                        .setEngagementLevel(ENGAGEMENT_LEVEL.ALARMED);
                source.getUnitAI()
                        .getGroup()
                        .addEnemyKnownCoordinates(
                                target.getCoordinates()
                                        .getAdjacentCoordinates()
                                        .toArray(
                                                new Coordinates[target
                                                        .getCoordinates()
                                                        .getAdjacentCoordinates()
                                                        .size()]));
                break;
        }
    }

    public void perceptionEvent(PERCEPTION_STATUS status, DC_HeroObj source,
                                DC_HeroObj target, ActiveObj action) {
        if (status == PERCEPTION_STATUS.UNKNOWN)
            return;
        if (source.isAiControlled())
            perceptionEventAI(status, source, target, action);
        else
            perceptionEventPlayer(status, source, target, action);

    }

    private void perceptionEventPlayer(PERCEPTION_STATUS status,
                                       DC_HeroObj source, DC_HeroObj target, ActiveObj action) {
        String message = source.getName() + ": ";
        // text anim over the source
        source.getGame().getLogManager().log(message);
        // "Something moves in the shadows...";
        // source + " there are sounds of movement/spellcasting/battle"; ++
        // RELATIVE DIRECTION
        // "Something moves at ";
        // "Something moves "+ getDirectionDescription(d);
        // target.getName()+ " is " + " at ";

    }

    private void checkResetPlayerPerceptionStatus(PERCEPTION_STATUS status,
                                                  DC_HeroObj source, DC_HeroObj unit) {
        if (source.isAiControlled()) {
//			PERCEPTION_STATUS_PLAYER s = source.getUnitAI().getGroup()
//					.getPerceptionStatus();
            for (PERCEPTION_STATUS perc : PERCEPTION_STATUS.values()) {

            }
            // PERCEPTION_STATUS.values()).indexOf(status);

            // engagement level?
        } else {
            // determines *visibility* as well? or issues a message? could also
            // have some visual effect on the interface - red-eyed monster on
            // the top ;)

        }

    }

//	private PERCEPTION_STATUS getPerceptionStatus(DC_HeroObj unit,
//			ActiveObj activeObj, int actionNoise) {
//		int perception = unit.getIntParam(PARAMS.PERCEPTION);
//		if (perception <= 0)
//			return PERCEPTION_STATUS.UNKNOWN;
//		int noise = actionNoise - getNoiseBarrier(unit);
//		if (noise <= 0)
//			return PERCEPTION_STATUS.UNKNOWN;
//		MathMaster.addFactor(noise, getNoiseMod(unit, activeObj));
//		// reflect in volume
//		// additional modifier if within spectrum?
//		PERCEPTION_STATUS status;
//		// getPerceptionMod();
//		// distance decibels! =)
//		// should some actions be quiet?
//		for (PERCEPTION_STATUS s : PERCEPTION_STATUS.values()) {
//			status = s;
//			if (status.getBarrier() >= getBarrier(noise, perception))
//				break;
//		}
//		return status;
//	}
//
//	private Integer getNoiseMod(DC_HeroObj unit, ActiveObj activeObj) {
//		for (DC_HeroObj u : unit.getGame().getUnits()) {
//			PositionMaster.getDistance(unit, u);
//		}
//		return null;
//	}

    private int getNoiseBarrier(Obj unit) {
        int totalNoise = 0;
        for (DC_HeroObj u : Analyzer.getUnits((DC_HeroObj) unit, null, false,
                false, false)) {
            int distance = PositionMaster.getDistance(u, unit);
            if (distance == 0)
                distance = 1;
            totalNoise += u.getIntParam(PARAMS.NOISE) / distance;
        }
        return totalNoise;
    }

    private Collection<DC_HeroObj> getUnits(ActiveObj activeObj) {
        // TODO max distance?
        List<DC_HeroObj> list = new LinkedList<>();
        for (Obj unit : DC_Game.game
                .getPlayer(activeObj.getOwnerObj().isMine())
                .getControlledUnits()) {
            list.add((DC_HeroObj) unit);
        }
        return list;
    }

    private Integer getNoiseMod(DC_HeroObj unit, ActiveObj activeObj) {
        // TODO Auto-generated method stub
        return null;
    }

    private int getNoiseBarrier(DC_HeroObj unit) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean unitBecomesActive(DC_HeroObj unit) {
        return false;
    }

    public enum PERCEPTION_STATUS_PLAYER {
        UNKNOWN,
        KNOWN_TO_BE_THERE,
        SUSPECTED_TO_BE_THERE,
        KNOWN_TO_BE_SOMEWHERE,
        SUSPECTED_TO_BE_SOMEWHERE;

    }

    public enum PERCEPTION_STATUS {
        UNKNOWN,
        SUSPECTED_TO_BE_SOMEWHERE,
        KNOWN_TO_BE_SOMEWHERE,
        SUSPECTED_TO_BE_THERE,
        KNOWN_TO_BE_THERE;

        int perceptionBarrier;
    }

}