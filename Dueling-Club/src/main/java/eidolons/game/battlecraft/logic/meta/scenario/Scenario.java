package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.mission.Mission;
import eidolons.game.core.game.ScenarioGame;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Scenario extends LightweightEntity {

    private List<Mission> availableMissions;

    public Scenario(ObjType type) {
        super(type);

    }

//    public void init() {
//        toBase();
//    }


    private List<Mission> initAvailableMissions() {
        List<String> missions =
         ContainerUtils.openContainer(getProperty(PROPS.SCENARIO_MISSIONS));
        String currentMission = getGame().getMetaMaster().
         getMissionName();
        int index = missions.indexOf(currentMission);
        //show future missions?
//        getGame().getBattleMaster().getConstructor().getOrCreate(mission)
        List<String> available = ContainerUtils.openContainer(missions.get(index));
        return available.stream().map(mission ->
         new Mission(DataManager.getType(
          mission, DC_TYPE.PARTY),
          this)).collect(Collectors.toList());

    }


    public ObjType getPartyType() {
        return DataManager.getType(
         getProperty(PROPS.SCENARIO_PARTY),
         DC_TYPE.PARTY);
    }

    public void missionChosen() {

    }

    public void next() {
        initAvailableMissions();
        //set mission? init choice?
    }

    public List<Mission> getAvailableMissions() {
        if (availableMissions == null)
            availableMissions = initAvailableMissions();
        return availableMissions;
    }

    public void setAvailableMissions(List<Mission> availableMissions) {
        this.availableMissions = availableMissions;
    }

    @Override
    public ScenarioGame getGame() {
        return (ScenarioGame) super.getGame();
    }
}
