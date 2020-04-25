package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.game.DC_Game.GAME_MODES;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/8/2017.
 */
public class FacingAdjuster<E extends DungeonWrapper> extends DungeonHandler  {
    protected Map<Coordinates, FACING_DIRECTION> facingMap = new HashMap<>();

    public FacingAdjuster(DungeonMaster  master) {
        super(master);

//        for (MicroObj unit : list) {
//            FACING_DIRECTION facing;
//            if (!game.isOffline()) {
//                // TODO not always vertical!
//                facing = FacingMaster.getFacingFromDirection(getPositioner().getClosestEdgeY(
//                 unit.getCoordinates()).getDirection().flip());
//            } else
////             TODO    if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
//            {
//                facing = FacingMaster.getPresetFacing(me);
//            }
//            ((BattleFieldObject) unit).setFacing(facing);
//        }
    }


    public void adjustFacing(Unit unit) {
        unit.setFacing(unit.isMine() ? getPartyMemberFacing(unit)
         : getFacingForUnit(unit.getCoordinates(), unit.getName()));
    }

    public void adjustFacing(List<Unit> unitsList) {
        unitsList.forEach(unit -> adjustFacing(unit));
    }

    public FACING_DIRECTION getFacingOptimal(Coordinates c, boolean mine) {
        Collection<Obj> units = getGame().getPlayer(!mine).collectControlledUnits();
        return FacingMaster.getOptimalFacingTowardsUnits(c, units);


    }

//    public FACING_DIRECTION getFacingInitial(Coordinates c) {
//        // TODO
//        return FacingMaster
//         .getRelativeFacing(c, getGame().getDungeon().getPlayerSpawnCoordinates());
//
//    }

    boolean isAutoOptimalFacing() {
        return true;
    }

    public FACING_DIRECTION getFacingForUnit(Coordinates c, String typeName) {
       Map<Coordinates, FACING_DIRECTION> map=getUnitFacingMap();
        if (map!=null) {
            return map.get(c);
        }
        return getFacingOptimal(c, false);
    }

    protected Map<Coordinates, FACING_DIRECTION> getUnitFacingMap() {
        return getBattleMaster().getDungeonMaster().getDungeonLevel().getUnitFacingMap();
    }

    public FACING_DIRECTION getPartyMemberFacing(Unit unit) {
        if (unit.isPlayerCharacter()) {
            FACING_DIRECTION presetFacing = EidolonsGame.getPresetFacing(unit);
            if (presetFacing != null) {
                return presetFacing;
            }
        }
        if (getGame().getGameMode() == GAME_MODES.DUNGEON_CRAWL) {
            return FacingMaster.getOptimalFacingTowardsEmptySpaces(unit);
        }
        Coordinates c = unit.getCoordinates();
        if (isAutoOptimalFacing())
            return getFacingOptimal(c, true);
        if (facingMap.containsKey(c)) {
            return facingMap.get(c);
        }

        // TODO


        return main.game.bf.directions.FACING_DIRECTION.NORTH;
    }

    public void unitPlaced(Coordinates adjacentCoordinate, FACING_DIRECTION facingFromDirection) {
    }

}
