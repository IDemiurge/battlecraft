package eidolons.game.netherflame.igg;

import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;

/**
 * this is the thing to be constructed from save data?
 *
 */
public class IGG_Meta extends MetaGame {

    private int missionIndex;
    private IGG_Mission mission;
    private ObjType missionType;
    private int actIndex;

    public IGG_Meta(MetaGameMaster master) {
        super(master);
    }

    @Override
    public IGG_MetaMaster getMaster() {
        return (IGG_MetaMaster) super.getMaster();
    }


    public void setMissionType(ObjType missionType) {
        this.missionType = missionType;
    }

    public ObjType getMissionType() {
        return missionType;
    }

    public void setMission(IGG_Demo.IGG_MISSION mission) {
        this.mission = new IGG_Mission(mission);
        missionIndex = mission.getMissionIndex();
        actIndex = mission.getAct();
        setMissionType(DataManager.getType(mission.getMissionName(), DC_TYPE.FLOORS));
    }

    public int getMissionIndex() {
        return missionIndex;
    }

    public int getActIndex() {
        return actIndex;
    }

    public IGG_Mission getMission() {
        return mission;
    }

    public boolean isFinalLevel() {
        return false;
    }
}
