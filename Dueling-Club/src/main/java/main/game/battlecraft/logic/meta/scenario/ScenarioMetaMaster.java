package main.game.battlecraft.logic.meta.scenario;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.DataModel;
import main.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import main.game.battlecraft.logic.dungeon.location.LocationMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import main.game.battlecraft.logic.meta.scenario.hq.HqShopManager;
import main.game.battlecraft.logic.meta.universal.*;
import main.game.core.game.ScenarioGame;
import main.system.auxiliary.StringMaster;
import main.test.frontend.ScenarioLauncher;

/**
 * Created by JustMe on 5/13/2017.
 */
public class ScenarioMetaMaster extends MetaGameMaster<ScenarioMeta> {

    public ScenarioMetaMaster(String data) {
        super(data);
    }

    /*
        on clicking a mission...
        full loading 
        > create dungeon(s)
        > create party units (maybe  place them at once)
         */
    @Override
    public void preStart() {

        String missionName =
         getMetaDataManager().getMissionName();

        if (StringMaster.isEmpty(missionName)) {
            int missionIndex = StringMaster.getInteger(ScenarioLauncher.missionIndex);

            getMetaGame().setMissionIndex(missionIndex);

            chosenMission(StringMaster.openContainer(getMetaGame().getScenario().
             getProperty(PROPS.SCENARIO_MISSIONS)).get(missionIndex));
            missionName = getMetaDataManager().getMissionName();
        } else {
            getMetaGame().setMissionIndex(StringMaster.openContainer(getMetaGame().getScenario().
             getProperty(PROPS.SCENARIO_MISSIONS)).indexOf(missionName));

        }
        String levelPath = DataManager.getType(missionName, DC_TYPE.MISSIONS).
         getProperty(PROPS.MISSION_FILE_PATH);
        getGame().getDataKeeper().getDungeonData().setValue(DUNGEON_VALUE.PATH,
         levelPath);

        super.preStart();
    }

    @Override
    public DataModel getEntity() {
        return getMetaGame().getScenario();
    }

    public void chosenMission(String data) {
        getMetaDataManager().setMissionName(data);
    }

    public void loadMission(String data) {
        getGame().getDungeonMaster().init(); //create dungeon

        getGame().getBattleMaster().init(); //create Mission
        //game.load()
        //precombat?
        getGame().start(true); //game loop??
        getMetaGame().getScenario().next();
//        getPartyManager().getParty().getIntParam(mission_index);
//        Mission mission= (Mission) getGame().getBattleMaster().getBattle();
//        getMetaGame().getScenario().setMission(mission);
        //for Mission
//        getDialogueManager().startDialogue(); //block game?

    }

    @Override
    protected ScenarioGame createGame() {
        return new ScenarioGame(this);
    }

    @Override
    protected PartyManager<ScenarioMeta> createPartyManager() {
        return new ScenarioPartyManager(this);
    }

    @Override
    protected MetaDataManager<ScenarioMeta> createMetaDataManager() {
        return new ScenarioMetaDataManager(this);
    }

    @Override
    protected ShopManager<ScenarioMeta> createShopManager() {
        return new HqShopManager(this);
    }

    @Override
    protected MetaInitializer<ScenarioMeta> createMetaInitializer() {
        return new ScenarioInitializer(this);
    }

    @Override
    public ScenarioMetaDataManager getMetaDataManager() {
        return (ScenarioMetaDataManager) super.getMetaDataManager();
    }

    @Override
    public MissionBattleMaster getBattleMaster() {
        return (MissionBattleMaster) super.getBattleMaster();
    }

    @Override
    public LocationMaster getDungeonMaster() {
        return (LocationMaster) super.getDungeonMaster();
    }

    public String getMissionName() {
        return getPartyManager().getParty().getMission();
    }
}
