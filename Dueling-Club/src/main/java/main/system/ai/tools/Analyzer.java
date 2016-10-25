package main.system.ai.tools;

import main.ability.conditions.WaitingFilterCondition;
import main.ability.effects.common.RaiseEffect;
import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.content.OBJ_TYPES;
import main.data.XList;
import main.entity.obj.*;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.VisionManager;
import main.game.player.Player;
import main.swing.components.panels.DC_UnitActionPanel.ACTION_DISPLAY_GROUP;
import main.system.ai.UnitAI;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;

public class Analyzer {
    public static List<DC_HeroObj> getAllies(UnitAI ai) {
        List<DC_HeroObj> list = new XList<DC_HeroObj>();
        for (DC_HeroObj unit : getGame().getUnits()) {
            if (unit.getOwner() != ai.getUnit().getOwner())
                continue;
            if (unit.isDead())
                continue;
            list.add(unit);
        }

        return list;
    }

    public static List<DC_HeroObj> getWaitUnits(UnitAI ai) {
        List<DC_HeroObj> list = new XList<DC_HeroObj>();
        for (DC_HeroObj unit : getGame().getUnits()) {
            if (unit.equals(ai.getUnit()))
                continue;
            if (!WaitingFilterCondition.canBeWaitedUpon(ai.getUnit(), unit))
                continue;
            list.add(unit);
        }

        return list;
    }

    public static List<DC_HeroObj> getVisibleEnemies(UnitAI ai) {
        return getUnits(ai, false, true, true, false);
    }

    public static List<DC_HeroObj> getUnits(UnitAI ai, Boolean ally,
                                            Boolean enemy, Boolean vision_no_vision, Boolean dead) {
        return getUnits(ai, ally, enemy, vision_no_vision, dead, false);
    }

    public static List<DC_HeroObj> getUnits(UnitAI ai, Boolean ally,
                                            Boolean enemy, Boolean vision_no_vision, Boolean dead,
                                            Boolean neutral) {

        List<DC_HeroObj> list = new XList<DC_HeroObj>();
        for (DC_HeroObj unit : getGame().getUnits()) {
            if (unit.getZ() != ai.getUnit().getZ())// TODO
                continue;
            if (!enemy)
                if (unit.getOwner() != ai.getUnit().getOwner())
                    continue;
            if (!ally)
                if (unit.getOwner() == ai.getUnit().getOwner())
                    continue;
            if (!neutral)
                if (unit.getOwner() == Player.NEUTRAL || unit.isBfObj())
                    continue;
            if (vision_no_vision)
                if (!VisionManager.checkVisible(unit))
                    continue;
            if (!dead)
                if (unit.isDead())
                    continue;

            list.add(unit);
        }

        return list;
    }

    private static DC_Game getGame() {
        return DC_Game.game;

    }

    public static boolean hasSpecialActions(DC_HeroObj unit) {
        if (hasSpells(unit))
            return true;
        if (unit.getQuickItems() != null)
            if (!unit.getQuickItems().isEmpty())
                return true;
        if (ListMaster.isNotEmpty(unit.getActionMap().get(
                ACTION_DISPLAY_GROUP.SPEC_ACTIONS)))
            return true;
        return false;
    }

    public static boolean hasQuickItems(DC_HeroObj unit) {
        if (unit.getQuickItems() != null)
            if (!unit.getQuickItems().isEmpty())
                return true;
        return false;
    }

    public static boolean hasSpells(DC_HeroObj unit) {
        return !unit.getSpells().isEmpty();
    }

    public static boolean canCast(DC_HeroObj target) {
        if (!hasSpells(target))
            return false;
        for (DC_SpellObj s : target.getSpells()) {
            if (s.canBeActivated())
                return true;
        }
        return false;
    }

    public static boolean isBlocking(DC_HeroObj unit, DC_HeroObj target) {
        // TODO melee vs ranged

        return false;

    }

    public static boolean checkRangedThreat(DC_HeroObj target) {
        if (!hasSpecialActions(target))
            return false;
        if (canCast(target))
            return true;

        return false;

    }

    public static boolean isBlockingMovement(DC_HeroObj unit, DC_HeroObj target) {
        if (!unit.getCoordinates().isAdjacent(target.getCoordinates()))
            return false;

        Coordinates c = unit.getCoordinates().getAdjacentCoordinate(
                unit.getFacing().getDirection());
        if (c == null)
            return false;
        if (c.equals(target.getCoordinates())) {
            return true;
        }
        return false;

    }

    public static List<DC_Cell> getLastSeenEnemyCells(UnitAI ai) {
        List<DC_Cell> list = new LinkedList<>();
        for (DC_Obj obj : ai.getUnit().getOwner().getLastSeenCache().keySet()) {
            if (isEnemy(obj, ai.getUnit())) {
                Coordinates coordinates = ai.getUnit().getOwner()
                        .getLastSeenCache().get(obj);
                list.add((DC_Cell) obj.getGame().getCellByCoordinate(
                        coordinates));
            }
        }

        return list;
    }

    public static boolean isEnemy(Obj targetObj, DC_Obj source) {
        if (targetObj != null)
            if (!targetObj.getOwner().equals(Player.NEUTRAL))
                if (!targetObj.getOwner().equals(source.getOwner()))
                    if (!targetObj.getOBJ_TYPE_ENUM().equals(OBJ_TYPES.BF_OBJ))
                        return true;
        return false;
    }

    public static List<? extends DC_Obj> getMeleeEnemies(DC_HeroObj unit) {
        return getAdjacentEnemies(unit, true);
    }

    public static List<? extends DC_Obj> getAdjacentEnemies(DC_HeroObj unit,
                                                            boolean checkAttack) {
        return getAdjacentEnemies(unit, false, checkAttack);
    }

    public static List<? extends DC_Obj> getAdjacentEnemies(DC_HeroObj unit,
                                                            boolean checkAct, boolean checkAttack) {
        return getEnemies(unit, checkAct, checkAttack, true);
    }

    public static List<? extends DC_Obj> getEnemies(DC_HeroObj unit,
                                                    boolean checkAct, boolean checkAttack, boolean adjacentOnly) {
        return getUnits(unit, true, checkAct, checkAttack, adjacentOnly);
    }

    public static List<DC_HeroObj> getUnits(DC_HeroObj unit,
                                            Boolean enemy_or_ally_only, boolean checkAct, boolean checkAttack,
                                            boolean adjacentOnly) {
        List<DC_HeroObj> list = new LinkedList<>();
        for (Coordinates coordinates : unit.getCoordinates()
                .getAdjacentCoordinates()) {
            Obj obj = unit.getGame().getUnitByCoordinate(coordinates);
            if (obj == null)
                continue;

            DC_HeroObj enemy = (DC_HeroObj) obj;
            if (enemy_or_ally_only != null) {
                if (enemy_or_ally_only)
                    if (obj.getOwner().equals(unit.getOwner()))
                        continue;
                    else if (!obj.getOwner().equals(unit.getOwner()))
                        continue;
            }

            if (!checkAct)
                list.add(enemy);
            if (enemy.canActNow())
                if (!checkAttack)
                    list.add(enemy);
                else if (enemy.canAttack())
                    list.add(enemy);

        }

        return list;
    }

    public static List<DC_Cell> getSafeCells(UnitAI ai) {
        // filter by distance?
        // prune
        // to speed it up... now that I've disabled the special actions on
        // retreat...
        // I can just choose an *adjacent* cell for *Move* and make the right
        // turning sequence if necessary
        // when stuck, "Cower" mode would be nice to have :)

		/*
         * let's say, all *detected* cells that are not adjacent to enemies!
		 */
        // TODO performance dicatates adjacent only...
        return getCells(ai, true, false, true);

        // List<DC_Cell> cells = getCells(ai, false, false, true);
        // List<DC_Cell> list = new ArrayList<>();
        // int max_distance = -1;
        // loop: for (DC_Cell c : cells) {
        // for (Coordinates c1 : c.getCoordinates().getAdjacentCoordinates()) {
        // DC_HeroObj enemy = ai.getUnit().getGame()
        // .getUnitByCoordinate(c1);
        // if (!isEnemy(enemy, ai.getUnit()))
        // continue;
        // if (enemy.canAct())
        // continue loop;
        // }
        // int distance = PositionMaster.getDistance(ai.getUnit()
        // .getCoordinates(), c.getCoordinates());
        //
        // if (distance >= max_distance + 1) {
        // max_distance = distance;
        // list.add(c);
        // }
        // }
        // List<DC_Cell> remove = new LinkedList<>();
        // for (DC_Cell c : list) {
        // int distance = PositionMaster.getDistance(ai.getUnit()
        // .getCoordinates(), c.getCoordinates());
        // if (distance + 1 < max_distance) {
        // remove.add(c);
        // }
        // }
        //
        // for (DC_Cell c : remove)
        // list.remove(c);
        //
        // DIRECTION direction =
        // FacingManager.rotate180(ai.getUnit().getFacing())
        // .getDirection();
        // Coordinates behind = ai.getUnit().getCoordinates()
        // .getAdjacentCoordinate(direction);
        //
        // if (!list.contains(behind)) {
        // if (ai.getUnit().getGame().getUnitByCoordinate(behind) == null)
        // list.add(ai.getUnit().getGame().getCellByCoordinate(behind));
        // }

        // return list;

    }

    public static List<DC_Cell> getMoveTargetCells(UnitAI ai) {
        return getCells(ai, true);
    }

    public static List<DC_Cell> getCells(UnitAI ai, boolean detected) {
        return getCells(ai, false, detected, true);
    }

    public static List<DC_Cell> getCells(UnitAI ai, boolean adjacent,
                                         boolean detected, boolean free) {
        return getCells(ai.getUnit(), adjacent, detected, free);
    }

    public static List<DC_Cell> getCells(DC_HeroObj targetUnit,
                                         boolean adjacent, boolean detected, boolean free) {
        List<DC_Cell> list = new LinkedList<>();
        for (Obj obj : targetUnit.getGame().getCells()) {
            DC_Cell cell = (DC_Cell) obj;

            if (adjacent)
                if (!obj.getCoordinates().isAdjacent(
                        targetUnit.getCoordinates()))
                    continue;

            if (free) {
                DC_HeroObj unit = targetUnit.getGame().getUnitByCoordinate(
                        cell.getCoordinates());
                if (unit != null) {
                    if (VisionManager.checkVisible(unit))
                        continue;
                }
            }
            if (detected) // TODO in sight etc
                if (cell.getActivePlayerVisionStatus() != UNIT_TO_PLAYER_VISION.DETECTED)
                    continue;
            list.add(cell);

        }

        return list;
    }

    public static List<? extends DC_Obj> getStalkCells(UnitAI ai) {
        // get closest enemy?
//        DC_HeroObj enemy = getClosestEnemy(ai);
//        List<DC_Obj> list = new LinkedList<>();
//        for (DC_Cell cell : getCells(ai, false, false, true)) {
//            if (PositionMaster.getDistance(cell, enemy) <= HearingRule
//                    .getSafeDistance(ai.getUnit(), enemy))
//                list.add(cell);
//        }
//        return list;
        return null ;
    }

    public static List<? extends DC_Obj> getWanderCells(UnitAI ai) {
        DIRECTION d = ai.getGroup().getWanderDirection();
        // permittedCells = ai.getGroup().getWanderBlocks();
        List<DC_Obj> list = new LinkedList<>();
        for (DC_Cell cell : getCells(ai, false, false, true)) {
            if (d != null)
                if (DirectionMaster.getRelativeDirection(cell, ai.getUnit()) != d)
                    continue;
            if (PositionMaster.getDistance(cell, ai.getUnit()) <= ai
                    .getMaxWanderDistance())
                list.add(cell);
        }
        if (list.isEmpty()) {
            // change direction?
        }
        return list;
    }

    public static List<DC_Cell> getSummonCells(UnitAI ai, DC_ActiveObj action) {

        if (EffectMaster.check(action, RaiseEffect.class)) {
            return getCorpseCells(ai.getUnit());
        }
        List<DC_Cell> cells = getCells(ai, true, true, true);
        boolean melee = true;
        // try{
        // } check melee TODO
        if (melee) {
            for (DC_HeroObj e : getVisibleEnemies(ai)) {
                // e.getCoordinates().getAdjacentCoordinates()
                cells.addAll(getCells(e, true, false, true));
            }
        }

        return cells;
    }

    private static List<DC_Cell> getCorpseCells(DC_HeroObj unit) {

        return unit.getGame().getCellsForCoordinates(
                unit.getGame().getGraveyardManager().getCorpseCells());
    }

    public static List<? extends DC_Obj> getZoneDamageCells(DC_HeroObj unit) {
        List<DC_Cell> cells = getCells(unit.getUnitAI(), false, false, true);
        List<DC_Cell> remove_cells = new LinkedList<>();
        loop:
        for (DC_Cell c : cells) {
            for (Coordinates c1 : c.getCoordinates().getAdjacentCoordinates()) {
                DC_HeroObj enemy = unit.getGame().getUnitByCoordinate(c1);
                if (isEnemy(enemy, unit))
                    continue loop;
            }
            remove_cells.add(c);
        }

        for (DC_Cell c : remove_cells) {
            cells.remove(c);
        }
        return cells;
    }

    public static List<DC_Cell> getSearchCells(UnitAI ai) {
        List<DC_Cell> cells = getLastSeenEnemyCells(ai);
        if (!cells.isEmpty())
            return new ListMaster<DC_Cell>()
                    .getList(new RandomWizard<DC_Cell>()
                            .getRandomListItem(cells));
        return new ListMaster<DC_Cell>().getList(ai
                .getUnit()
                .getGame()
                .getCellByCoordinate(
                        new RandomWizard<Coordinates>().getRandomListItem(ai
                                .getUnit().getCoordinates()
                                .getAdjacentCoordinates())));
        // List<DC_Cell> list = new LinkedList<>();
        //
        // DC_HeroObj unit = ai.getUnit();
        // Coordinates originalCoordinates = unit.getCoordinates();
        // for (DC_Cell cell : getCells(ai, true)) {
        // unit.setCoordinates(cell.getCoordinates());
        // for (DC_Cell target_cell : cells) {
        // if (!list.contains(cell))
        // if (target_cell.checkInSightForUnit(unit))
        // list.add(cell);
        // }
        // }
        // unit.setCoordinates(originalCoordinates);
        // return list;
    }

}