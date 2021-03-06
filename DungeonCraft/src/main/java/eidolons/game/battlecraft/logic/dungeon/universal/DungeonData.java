package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 5/10/2017.
 */
public class DungeonData extends DataUnit<DUNGEON_VALUE> {
    public static final Boolean FORMAT = false;

    public DungeonData() {

    }

    public DungeonData(String data) {
        super(data);
    }

    @Override
    public Boolean getFormat() {
        return FORMAT;
    }

    public enum DUNGEON_VALUE {
        TYPE_NAME,
        PATH,
        RANDOM,
        FILTER_VALUE_NAME,
        FILTER_VALUE,
        WORKSPACE_FILTER,
    }
}
