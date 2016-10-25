package main.rules.mechanics;

import main.ability.effects.DealDamageEffect;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.VisionManager;
import main.rules.ForceRule;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.PositionMaster;

import java.util.List;

public class CollisionRule {
    /*
	 * move action targets a cell with invisible unit on it...
	 * 
	 * outcomes: 1) stop short 2) displace 3) step over without noticing
	 * 
	 * supporting layered units? targeting might be problematic [layer] should
	 * be defined
	 */

    public static Coordinates collision(Ref ref, DC_ActiveObj activeObj, DC_HeroObj moveObj,
                                        DC_HeroObj collideObj) {
        return collision(ref, activeObj, moveObj, collideObj, false);
    }

    public static Coordinates collision(Ref ref, DC_ActiveObj activeObj, DC_HeroObj moveObj,
                                        DC_HeroObj collideObj, boolean summon) {
        return collision(ref, activeObj, moveObj, collideObj, summon, 0);
    }

    public static Coordinates collision(Ref ref, DC_ActiveObj activeObj, DC_HeroObj moveObj,
                                        DC_HeroObj collideObj, boolean summon, int force) {
        // ++ AoO!
        // permit auto-retreat ? maybe some skill will make hidden unit
        // *passable* ;)
        Coordinates dest = collideObj.getCoordinates();

        Coordinates final_coordinate = dest;
        if (!canBeOn(moveObj, collideObj.getCoordinates())) {
            // dest.getAdjacentCoordinate(direction);
            Boolean above = PositionMaster.isAboveOr(moveObj, collideObj);
            Boolean left = PositionMaster.isToTheLeftOr(moveObj, collideObj);
            // TODO adjust in a loop in case there are multiple obstacles
            // around!
            int x = 0;
            if (left != null)
                x = (left ? -1 : 1);
            int y = 0;
            if (above != null)
                y = (above ? -1 : 1);

            final_coordinate = new Coordinates(dest.x + x, dest.y + y);
            if (!canBeOn(moveObj, final_coordinate)) {
                final_coordinate = adjustCoordinateRandomly(dest, moveObj);
            }
            // TODO ++ spare Action Points? Percentage or so...
            // saved_ap =
            // activeObj.getIntParam(PARAMS.AP_COST)*distance_factor*save_factor;
        }
        if (!moveObj.getOwner().equals(collideObj.getOwner())) {
            // any action
            Boolean incomerAlive = AttackOfOpportunityRule.collisionAoO(moveObj.getDummyAction(),
                    collideObj);
            if (!BooleanMaster.isFalse(incomerAlive))
                InstantAttackRule.checkCollisionAttack(moveObj, activeObj, collideObj);
            // if (incomerAlive == null) {
            // if (!summon)
            // AttackOfOpportunityRule.checkAttack(moveObj,
            // collideObj.getAction("Move"));
            // } else if (incomerAlive)
            // if (!summon)
            // AttackOfOpportunityRule.checkAttack(moveObj,
            // collideObj.getAction("Move"));
        }
        // if (!VisionManager.checkVisible((DC_Obj) moveObj))
        // return false; TODO
        if (moveObj.isDead())
            return null;
        if (collideObj.isDead())
            return final_coordinate;
        if (force != 0) {
            new DealDamageEffect(ForceRule.getCollisionDamageFormula(moveObj, collideObj, force,
                    true), DAMAGE_TYPE.BLUDGEONING).apply(Ref.getSelfTargetingRefCopy(collideObj));
            new DealDamageEffect(ForceRule.getCollisionDamageFormula(moveObj, collideObj, force,
                    false), DAMAGE_TYPE.BLUDGEONING).apply(Ref.getSelfTargetingRefCopy(moveObj));
        }
        if (!VisionManager.checkVisible(collideObj))
            // if (moveObj.canAct())
            StealthRule.applySpotted(collideObj); // ROLL?
        return final_coordinate;
    }

    private static boolean canBeOn(DC_HeroObj moveObj, Coordinates coordinates) {
        return moveObj.getGame().getRules().getStackingRule().canBeMovedOnto(moveObj, coordinates);
    }

    private static Coordinates adjustCoordinateRandomly(Coordinates dest, DC_HeroObj moveObj) {
        List<Coordinates> adjacent = dest.getAdjacentCoordinates();
        Loop.startLoop(adjacent.size());
        while (!Loop.loopEnded()) {
            dest = new RandomWizard<Coordinates>().getRandomListItem(adjacent);
            // dest.getAdjacentCoordinate(FacingManager.getRandomFacing().getDirection());
            if (canBeOn(moveObj, dest))
                return dest;
        }
        return null;
    }

}