package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.data.XLinkedMap;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/31/2018.
 * <p>
 * Logic:
 * <p>
 * > Never fully 'conceal' overlay
 * > especially light emitters...
 * so we generally try to get as many passable cells adjacent in that direction as possible...
 * <p>
 * we take the direction and its two 45 degrees versions
 */
public class RngOverlayManager {

    Map<Coordinates, List<Pair<String, DIRECTION>>> map;
    DungeonLevel level;

    public void initDirectionMap(DungeonLevel level) {
        this.level = level;
        map = new XLinkedMap<>();
        for (ObjAtCoordinate obj : level.getObjects()) {
            if (EntityCheckMaster.isOverlaying(obj.getType())) {
                DIRECTION direction = getDirection(obj.getCoordinates(),
                 level.getBlockForCoordinate(obj.getCoordinates()));

                Pair<String, DIRECTION> pair = new ImmutablePair<>(obj.getType().getName(),
                 direction);
                if (map.get(obj.getCoordinates()) != null)
                    while (map.get(obj.getCoordinates()).contains(pair)) {
                        direction = getDirection(obj.getCoordinates(),
                         level.getBlockForCoordinate(obj.getCoordinates()));
                        pair = new ImmutablePair<>(obj.getType().getName(),
                         direction);
                    }

                MapMaster.addToListMap(map, obj.getCoordinates(), pair);
            }
        }
        level.setDirectionMapData(getDirectionMapData());
    }

    public DIRECTION getDirection(Coordinates c, LevelBlock block) {
        /*
        #####
        #OOO#
        ##O##

         */
        boolean orthogonalOnly;
        boolean diagonalOnly;
        try {
            WeightMap<DIRECTION> map =new WeightMap<>(
             ContainerUtils.constructContainer(Arrays.stream(DIRECTION.clockwise).
              map(direction -> direction + StringMaster.wrapInParenthesis(
               TilesMaster.getInSpectrum(c, block.getTileMap(), true, direction) + ""))
              .collect(Collectors.toList())), DIRECTION.class);
            return map.getRandomByWeight();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return null;
    }

    public String getDirectionMapData() {
        String directionData = "";
        for (Coordinates coordinates : map.keySet()) {
            String entry = ContainerUtils.constructContainer(
             map.get(coordinates).stream().map(
              pair -> pair.getKey() + StringMaster.wrapInParenthesis(pair.getValue().name()))
              .collect(Collectors.toList()));
            directionData += coordinates.toString() + "=" + entry + ";";
        }


        //                DIRECTION direction = getDirection(coordinates, block);
        //        model.getDirectionMap().put(coordinates, direction);


        return directionData;
    }
}
