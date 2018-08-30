package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/26/2018.
 */
public class RngTypeChooser {
    public static ObjType chooseType(Coordinates c,
                                     ROOM_CELL value,
                                     LevelBlock block, DungeonLevel dungeonLevel) {

        value = checkRandomCellResolves(value);

        DUNGEON_STYLE style= block.getStyle();
        String mapString= RngBfObjProvider.getWeightString(value, style);
        if (mapString==null )
            return null;
        mapString=filter(mapString, c, block, dungeonLevel);
        OBJ_TYPE T= DC_TYPE.BF_OBJ;
        ObjType type = RandomWizard.getObjTypeByWeight(mapString, T);
        return type;
    }

    private static ROOM_CELL checkRandomCellResolves(ROOM_CELL value) {
        if (value.getRandomWeightMap() == null) {
            return value;
        }
        WeightMap<ROOM_CELL> map = new WeightMap(value.getRandomWeightMap(),ROOM_CELL.class );
        return map.getRandomByWeight();
    }

    private static String filter(String mapString, Coordinates c,
                                 LevelBlock block, DungeonLevel dungeonLevel) {
//remove what?
        return mapString;
    }
}
