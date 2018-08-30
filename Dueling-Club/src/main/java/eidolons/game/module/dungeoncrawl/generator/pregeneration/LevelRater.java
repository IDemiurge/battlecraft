package eidolons.game.module.dungeoncrawl.generator.pregeneration;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker.LEVEL_REQUIREMENTS;
import eidolons.game.module.dungeoncrawl.generator.graph.GraphPath;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphEdge;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_STAT;
import main.system.auxiliary.secondary.GeometryMaster;
import main.system.data.DataUnit;
import main.system.datatypes.WeightMap;
import main.system.math.MathMaster;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/27/2018.
 */
public class LevelRater {
    private final LevelData data;
    private final LevelModel model;
    private final DataUnit<LEVEL_REQUIREMENTS> reqs;
    private final LevelStats stats;
    private final LevelGraph graph;
    private final DungeonLevel level;

    public LevelRater(DungeonLevel level) {
        this.level = level;
        this.data = level.getData();
        this.model = level.getModel();
        this.graph = model.getGraph();
        this.reqs = level.getData().getReqs();
        this.stats = new LevelStats(level);
    }

    /*
        :: check against previous levels - make sure it’s real different
        :: same random seed, but with an offset

        heuristics

        balances of zones, room, exits

        best packed

        maximum graph adherence

        or at least linkage...
         */
    public float rateLevel() {
        float rate = 0;
//make sure all room templates are used
        rate += getParametersRate();
        rate += getDistancesRate();
//        rate += getGraphRate();
        rate += getVariety();
        rate += getZoneBalanceRate();
        rate += getRoomBalanceRate();
        rate += getLayoutRate();

        return rate;
    }

    private float getLayoutRate() {
        List<Room> deadEnds = model.getRoomMap().values().stream().filter(r ->
         r.getUsedExits().isEmpty()).collect(Collectors.toList());

        int pen = 0;
        for (Room deadEnd : deadEnds) {
            switch (deadEnd.getType()) {
                case DEATH_ROOM:
                case GUARD_ROOM:
                    pen += 2;
                case TREASURE_ROOM:
                    break;
                default:
                    pen += 1;
            }
        }

        return model.getRoomMap().size()*2+100 - pen * 10;
    }

    private float getGraphRate() {
        for (GraphPath path : graph.getPaths()) {

        }
        LevelGraphNode tip = graph.findFirstNodeOfType(ROOM_TYPE.ENTRANCE_ROOM);
//        model.findFirstRoomOfType(ROOM_TYPE.ENTRANCE_ROOM)
        for (LevelGraphEdge edge : graph.getAdjList().get(tip)) {
//            model.getRoomLinkMap().
//            graph.getAdjList().get(node)
        }
        return 0;
    }

    private float getZoneBalanceRate() {
        WeightMap<String> map = new WeightMap<>(
         );
        for (LevelZone zone : model.getZones()) {
            map.put(zone.getIndex() + "", zone.getSubParts().size());
        }
        return MathMaster.getBalanceCoef(map);
    }
    private float getVariety() {
        WeightMap<EXIT_TEMPLATE> map = new WeightMap<>(stats.getValue(LEVEL_STAT.EXIT_TEMPLATE_COUNT), EXIT_TEMPLATE.class);
        return MathMaster.getBalanceCoef(map);

        //exit templates?
    }
    private float getParametersRate() {
        float rate = stats.getIntValue(LEVEL_STAT.FILL_PERCENTAGE);
        return rate;
    }

    private float getDistancesRate() {
        float preferred = getPreferredExitToEntranceDimensionRatio();
        float ratio = (float) (level.getEntranceCoordinates().dst_(level.getExitCoordinates()) /
         GeometryMaster.hyp(model.getCurrentWidth(), model.getCurrentHeight()));
        float diff = Math.abs(ratio - preferred);
        return 50 - diff * 100;
    }

    private float getPreferredExitToEntranceDimensionRatio() {
        return 0.6f;
    }



    private float getRoomBalanceRate() {
        float diff = 0;
        WeightMap<ROOM_TYPE> map = new WeightMap<>(stats.getValue(LEVEL_STAT.ROOM_TYPE_COUNT), ROOM_TYPE.class);

        for (ROOM_TYPE type : ROOM_TYPE.mainRoomTypes) {
            Integer n = map.get(type);
            if (n == null) {
                n = 0;
            }
            diff += Math.pow(
             Math.abs(data.getRoomCoeF(type) - n), 2);

        }
        return 100 - diff;
    }
}
