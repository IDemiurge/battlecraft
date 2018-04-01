package eidolons.game.battlecraft.logic.meta.skirmish;

import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.meta.scenario.Objective;
import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.logic.meta.skirmish.SkirmishMaster.NEMESIS_GROUP;

import java.util.List;

public class Skirmish extends Scenario {
    Dungeon dungeon;
    List<String> encounterSequence;
    List<NEMESIS_GROUP> nemesisGroups;
    Objective objective;

    public Skirmish(ObjType missionType, Dungeon level) {
        super(missionType);
        dungeon = level;
    }

    public Skirmish(ObjType missionType, String levelPath) {
        super(missionType);
        ObjType type;
        if (DataManager.isTypeName(levelPath)) {
            type = DataManager.getType(levelPath, DC_TYPE.DUNGEONS);
            dungeon = new Dungeon(type);
        } else {
//            dungeon = new LocationBuilder().buildDungeon(levelPath);
        }

    }

    public List<NEMESIS_GROUP> getNemesisGroups() {
        return nemesisGroups;
    }

    public void setNemesisGroups(List<NEMESIS_GROUP> nemesisGroups) {
        this.nemesisGroups = nemesisGroups;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public List<String> getEncounterSequence() {
        return encounterSequence;
    }

    public void setEncounterSequence(List<String> encounterSequence) {
        this.encounterSequence = encounterSequence;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }
}