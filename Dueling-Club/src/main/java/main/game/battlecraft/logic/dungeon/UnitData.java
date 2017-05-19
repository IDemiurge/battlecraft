package main.game.battlecraft.logic.dungeon;

import main.game.battlecraft.logic.dungeon.UnitData.PARTY_VALUE;
import main.system.net.data.DataUnit;

public class UnitData extends DataUnit<PARTY_VALUE> {


    public UnitData(String data) {
        super(data);
    }


    /*
    coordinates?
    spawn type?
    player owner
    leader?
    facing?

    use always to spawn?
     would be great! First form consistent data fully, then process it!

    can come from a Mission file... a preset

    facing in () ?

    spawner processing:
    1) check spawn at (then ignore coords? or use offset) or init coords
    2) create for player at coords
    3) init party/group and leader
    4) init facing

     pipelines:
     Preset/Hardcore from LAUNCH
     Creeps for a Mission
     Party from Arcade
     Custom spawn()

     'spawn()' for an HQ party is in fact more like 'place()' ...
    > don't respawn if exists?

     */
    public enum PARTY_VALUE {
        LEADER, MEMBERS,
        PLAYER_NAME,
        SPAWN_AT_COORDINATE,
        SPAWN_SIDE,
        COORDINATES,
        FACING,
        LEVEL,
        FACING_TEMPLATE,

    }


}