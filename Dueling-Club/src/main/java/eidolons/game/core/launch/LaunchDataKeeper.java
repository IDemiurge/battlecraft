package eidolons.game.core.launch;

import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData.PARTY_VALUE;
import eidolons.game.core.game.DC_Game;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnitFactory;
import main.system.data.PlayerData;
import main.system.util.Refactor;
import eidolons.test.Preset;
import eidolons.test.Preset.PRESET_DATA;

/**
 * Created by JustMe on 5/10/2017.
 */
public class LaunchDataKeeper {
    /*
    'new Game()' in testLauncher isn't very refined
    how to feed the preset/hardcode data?
    package into these:
    PlayerData
    UnitData
    DungeonData

    dataKeeper can store them until they are needed

    for MISSION

    for ARENA
     */
    public static final String SEPARATOR = DataUnitFactory.getSeparator(UnitData.FORMAT);
    public static final String PAIR_SEPARATOR = DataUnitFactory.getPairSeparator(UnitData.FORMAT);

    PlayerData[] playerData;
    UnitData[] unitData;
    DungeonData dungeonData;

    public LaunchDataKeeper() {

    }

    public LaunchDataKeeper(DC_Game game, String hardcodedPlayerData,
                            String hardcodedEnemyData,
                            String hardcodedDungeonData) {
        unitData = new UnitData[2];
        //suppose it's just a list of units? coordinates will be figured out later then
        unitData[0] = generateData(hardcodedPlayerData,
//         game.getPlayer(true)
         null, null, null);
        unitData[1] = generateData(hardcodedEnemyData,
//         game.getPlayer(false)
         null, null, null);
        dungeonData = DungeonInitializer.generateDungeonData(hardcodedDungeonData);
    }

    public LaunchDataKeeper(DC_Game game, Preset preset) {
        this(game, preset.getValue(PRESET_DATA.PLAYER_UNITS),
         preset.getValue(PRESET_DATA.ENEMIES),
         preset.getValue(PRESET_DATA.FIRST_DUNGEON));

        PresetLauncher.initPresetData(dungeonData, preset);
    }

    public LaunchDataKeeper(PlayerData[] playerData, UnitData[] unitData, DungeonData dungeonData) {
        this.playerData = playerData;
        this.unitData = unitData;
        this.dungeonData = dungeonData;
    }

    public static UnitData generateData(String dataString) {
        return generateData(dataString, null, null, null);
    }

    public static UnitData generateData(String dataString,
                                        DC_Player player,
                                        Coordinates spawnAt,
                                        Positioner positioner) {
        String units = "";
        String coordinates = "";
        String data = "";

        for (String substring : StringMaster.open(dataString)) {
            if (dataString.contains("=")) {
                coordinates += substring.split("=")[0] + StringMaster.SEPARATOR;
                units += substring.split("=")[1] + StringMaster.SEPARATOR;
            } else if (dataString.contains("(") && dataString.contains(")")) {
                units += VariableManager.removeVarPart(substring) + StringMaster.SEPARATOR;
                coordinates += VariableManager.getVar(substring) + StringMaster.SEPARATOR;
            } else
                units += substring + StringMaster.SEPARATOR;
        }

        if (positioner != null)
            if (coordinates.isEmpty()) {
                StringMaster.joinStringList(
                 StringMaster.convertToStringList(
                  positioner.getPlayerPartyCoordinates(StringMaster.openContainer(units))), ",");
//                List<Coordinates> coordinatesList =
//                 positioner.getCoordinates(player, spawnAt, units);
//                coordinates = StringMaster.joinStringList(
//                 StringMaster.convertToStringList(coordinatesList), ",");
            }
        if (!coordinates.isEmpty())
            data += PARTY_VALUE.COORDINATES + PAIR_SEPARATOR + coordinates + SEPARATOR;
        data += PARTY_VALUE.MEMBERS + PAIR_SEPARATOR + units + SEPARATOR;
        return new UnitData(data);
    }

    @Refactor
    public void addUnitData(UnitData data) {
        int i = 0;
        if (unitData != null) i = unitData.length;
        unitData = new UnitData[i + 1];
        unitData[i] = data;
    }

    public PlayerData[] getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData[] playerData) {
        this.playerData = playerData;
    }

    public UnitData[] getUnitData() {
        return unitData;
    }

    public void setUnitData(UnitData[] unitData) {
        this.unitData = unitData;
    }

    public DungeonData getDungeonData() {
        if (dungeonData == null)
            dungeonData = new DungeonData();
        return dungeonData;
    }

    public void setDungeonData(DungeonData dungeonData) {
        this.dungeonData = dungeonData;
    }

}