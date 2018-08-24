package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/30/2018.
 */
public class RngLightFiller extends RngFiller {
    public RngLightFiller(WeightMap<ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    protected int getWrapByExitChance(Room room) {
        return 50;
    }

    protected int getWrapPreExitChance(Room room) {
        return 80;
    }
    @Override
    public LEVEL_VALUES getFillCoefConst() {
        return LEVEL_VALUES.FILL_LIGHT_EMITTER_COEF;
    }

    @Override
    protected ROOM_CELL getFillCellType() {
        return ROOM_CELL.LIGHT_EMITTER;
    }
    @Override
    public float getFillCoef() {
        return 0.5f;
    }

    @Override
    public boolean isNoAdjacencyLimits() {
        return false;
    }

    @Override
    public boolean isNeverBlock() {
        return true;
    }

    @Override
    public boolean isCornersOnly() {
        return true;
    }

    @Override
    public boolean isFloorOrWallFiller() {
        return true;
    }

    @Override
    protected int getFillCornersChance(Room room) {
        switch (room.getType()) {
            case THRONE_ROOM:
            case COMMON_ROOM:
                return 50;
            case TREASURE_ROOM:
            case GUARD_ROOM:
                return 50;
            case ENTRANCE_ROOM:
            case EXIT_ROOM:
                return 80;
        }
        return 0;
    }

    //    @Override
//    public boolean isAlternativeCenterDistance() {
//        return true;
//    }
//
//    @Override
//    public float getMaxDistanceFromCenter() {
//        return 2;
//    }
//
//    @Override
//    public float getMaxDistanceFromEdge() {
//        return 3;
//    }
}
