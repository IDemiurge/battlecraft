package eidolons.game.battlecraft.rules.action;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.battlecraft.rules.round.WaterRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.netherflame.igg.pale.PaleAspect;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StackingRule implements ActionRule {

    private static final int MAX_OVERLAYING_ON_CELL = 4;
    private static final Integer CORPSE_GIRTH_FACTOR = 3;
    private static StackingRule instance;
    private DC_Game game;
    private HashMap<Entity, HashMap<Coordinates, Boolean>> cache = new HashMap();

    public StackingRule(DC_Game game2) {
        game = game2;
        instance = this;
    }


    public static boolean checkCanPlace(Coordinates c, Entity unit,
                                        List<? extends Entity> otherUnits) {
        return checkCanPlace(100, c, unit, otherUnits);
    }

    public static boolean checkCanPlace(Integer maxSpaceTakenPercentage, Coordinates c, Entity unit,
                                        List<? extends Entity> otherUnits) {
        if (EntityCheckMaster.isOverlaying(unit)) {
            boolean result = DC_Game.game.getOverlayingObjects(c).size() < MAX_OVERLAYING_ON_CELL;
            if (!result) {
                LogMaster.log(1, c
                 + "******* Cell already has max number of overlaying Objects!");
            }

            return result;
            // TODO limit number of overlays?
        }
        if (unit instanceof Entrance)
            return true;

        return instance.canBeMovedOnto(maxSpaceTakenPercentage, unit, c,  otherUnits);
    }

    public static void actionMissed(DC_ActiveObj action) {
        if (RuleKeeper.isRuleOn(RULE.MISSED_ATTACK_REDIRECTION))
            return;
        Ref ref = action.getRef();
        Obj target = ref.getTargetObj();
        Set<BattleFieldObject> units = action.getGame().getObjectsAt(
         action.getOwnerUnit().getCoordinates());
        units.addAll(action.getGame().getObjectsAt(
         target.getCoordinates()));
        units.remove(action.getOwnerUnit());
        units.remove(target);
        if (units.isEmpty()) {
            return;
        }
        Map<BattleFieldObject, Integer> map = new HashMap<>();
        for (BattleFieldObject unit : units) {
            map.put(unit, unit.getIntParam(PARAMS.GIRTH));
        }
        BattleFieldObject randomTarget = new RandomWizard<BattleFieldObject>().getObjectByWeight(map);
        ref.setTarget(randomTarget.getId());

        action.getGame().getLogManager().log(action.getName()+" has missed " +
                target.getNameIfKnown() +
                " and hit " +
                randomTarget +
                        " instead!");

        action.activatedOn(ref);

    }

    public boolean canBeMovedOnto(Entity unit, Coordinates c) {
        return canBeMovedOnto(unit, c,   null);
    }

    public boolean canBeMovedOnto(Entity unit, Coordinates c,
                                  List<? extends Entity> otherUnits) {
        return canBeMovedOnto(100, unit, c,   otherUnits);
    }

    private boolean canBeMovedOnto(Integer maxSpaceTakenPercentage, Entity unit, Coordinates c ,
                                   List<? extends Entity> otherUnits) {
        HashMap<Coordinates, Boolean> bools = cache.get(unit);
        boolean result = false;
        if (maxSpaceTakenPercentage == 100) {
            if (bools != null) {
                if (bools.containsKey(c)) {
                    return bools.get(c);
                }
            } else {
                bools = new HashMap<>();
                cache.put(unit, bools);
            }
        }

        //getVar all units on the cell
        DequeImpl<? extends Entity> units = new DequeImpl<>(otherUnits);
        for (BattleFieldObject u : game.getObjMaster(). getObjects (  c.x, c.y, false )) {
            if (!units.contains(u)) {
                if (u.isDead())
                    continue;
                if (u.isWall())
                    return false;
                if (u.isImpassable())
                    return false;
                if (u.isWater())
                    return WaterRule.checkPassable(u, unit);
                if (!u.isNeutral() && !u.isMine())
                    if (game.getVisionMaster().checkInvisible(u)) {
                        continue;
                    }
                if (!u.isAnnihilated())
                    //                    continue; TODO why was Type necessary?
                    units.addCast(!u.isDead() ? u.getType() : u);
            }
        }
        if (!EidolonsGame.DUEL)
        if (unit != null)
        if (EntityCheckMaster.isImmaterial(  unit ))
            return true;
        if (unit == null) {
            // unit = DataManager.getType(HeroCreator.BASE_HERO, DC_TYPE.CHARS);
            // instead, just empty type with 0 girth!
            unit = new ObjType();
        } else
        if (unit.getIntParam(PARAMS.GIRTH)==0) {
            return true;
        }
        if (PaleAspect.ON) {
            return true;
        }
        //check if '1 unit per cell' is on
        if (maxSpaceTakenPercentage <= 0) {
            if (!units.isEmpty()) {
                return false;
            }
        }


        DC_Cell cell;
        if (!game.isSimulation()) {
            cell = game.getCellByCoordinate(c);
        } else {
            cell = new DC_Cell(c, game);
        }
        if (cell == null) {
            //TODO cell is utter void!
            return false;
        }
        if (cell.isVOID()) {
            if (!unit.checkProperty(G_PROPS.STANDARD_PASSIVES, UnitEnums.STANDARD_PASSIVES.VOIDWALKER.getName())) {
                return false;
            }
        }

        //TODO ???
        if (game.isSimulation()) {
            if (units.size() > 1) {
                return false;
            }
        }
        // no passable/overlaying!
        int space = NumberUtils.getInteger(PARAMS.SPACE.getDefaultValue());
        if (c != null) {
            if (!game.isSimulation()) {
                space = cell.getIntParam(PARAMS.SPACE);
            }
        }

        int girth = 0;
        for (Entity u : units) {
            if (u == unit) {
                continue;
            }
            if (EntityCheckMaster.isWall(u)) {
                return false;
            }
            if (EntityCheckMaster.isEntrance(u)) {
                continue;
            }
            if (u.isDead()) {
                if (u instanceof Structure)
                    continue;

                girth += u.getIntParam(PARAMS.GIRTH) / CORPSE_GIRTH_FACTOR;
            } else
                girth += u.getIntParam(PARAMS.GIRTH);
            //           TODO  if (DoorMaster.isDoor((BattleFieldObject) u)) {
        }
        // [QUICK FIX]
        if (unit.getIntParam(PARAMS.GIRTH) == 0) {
            girth += NumberUtils.getInteger(PARAMS.GIRTH.getDefaultValue());
        } else {
            girth += unit.getIntParam(PARAMS.GIRTH);
        }
        space = space * maxSpaceTakenPercentage / 100;
        cell.setParam(PARAMS.GIRTH, girth);
        if (space >= girth) {
            result = true;
        } else {
            if (unit.getIntParam(PARAMS.GIRTH) > space) {
                if (units.isEmpty()) {
                    result = true;
                }
            }
        }
        if (!result) {
            units.removeIf(u -> u.isDead());
            if (units.size()==1) {
                if (units.get(0) instanceof Door) {
                    return true;
                }
            }
        }
        if (maxSpaceTakenPercentage == 100) //only cache for default cases!
        {
            bools.put(c, result);
        }
        return result;
    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        clearCache();
    }

    @Override
    public boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return true;
    }

    public void clearCache() {
        cache.clear();
    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        return true;
    }

}
