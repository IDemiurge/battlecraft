package main.game.battlecraft.rules.round;

import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.arena.Wave;
import main.game.battlecraft.logic.meta.PartyManager;
import main.game.core.game.DC_Game;
import main.game.module.adventure.travel.Encounter;
import main.system.auxiliary.StringMaster;
import main.test.Refactor;

import java.util.List;

@Refactor
public class ScoutingRule extends RoundRule {
    private Entity scout;

    public ScoutingRule(DC_Game game) {
        super(game);
    }

    public static List<String> getScoutedWaves(Encounter e) {
        return StringMaster.openContainer(e.getTypeNames());
    }

    public void checkEnemiesDetected(Unit hero) {
        // pre-define spawning side!
        // roll each turn -
        // "detection roll vs least stealth among the wave units?"
        // only vs next wave of course

//        for (Wave enemyWave : game.getBattleMaster().getSpawner().getScheduledWaves().keySet()) {
//            scout = hero;
//            // detection = scout.getIntParam(PARAMS.DETECTION);
//            int distance = 1
//                    + game.getBattleMaster().getSpawner().getScheduledWaves().get(enemyWave)
//                    - game.getState().getRound();
//            Ref ref = new Ref(game);
//            ref.setTarget(enemyWave.getId());
//            ref.setSource(scout.getId());
//            SCOUT_INFO_LEVEL level = null;
//            for (SCOUT_INFO_LEVEL l : SCOUT_INFO_LEVEL.values()) {
//                // enemyWave.getUnitMap().values()){
//                // } //spotting each unit separately?
//                // String success =detection+ "*" + l.getDifficulty();
//                String fail = distance + "*" + l.getDifficulty(); // TODO
//                try {
//                    if (RollMaster.roll(GenericEnums.ROLL_TYPES.DETECTION, fail, "-", ref)) {
//                        level = l;
//                        break;
//                    }
//                } catch (Exception e) {
//                    // e.printStackTrace();
//
//                }
//            }
//            if (level == null) {
//                continue;
//            }
//            logInfo(level, enemyWave);
//        }
    }

    @Override
    public boolean check(Unit unit) {
        if (PartyManager.getParty() == null) {
            if (unit.isHero()) {
                if (unit.getOwner().isMe()) {
                    return true;
                }
            }
        } else if (PartyManager.getParty().getMembers().contains(unit)) {
            return true;
        }
        return false;
    }

    @Override
    public void apply(Unit hero) {
        // checkEnemiesDetected(hero); TODO
    }

    private void logInfo(SCOUT_INFO_LEVEL level, Wave wave) {
        String string = "";
        // TODO spot individual units?
        switch (level) {
            case SIGNS:
                string = scout.getName() + " has perceived signs of possible presence of: "
                        + wave.getName();
                break;
            case TRACKS:
                string = scout.getName() + " has perceived unmistakable signs of : "
                        + wave.getName();
                break;
            case SPOTTED_APPROACH:
                string = scout.getName() + " has spotted " + wave.getName()
                        + " approaching from the " + wave.getSide();
                break;
            case VISUAL_CONTACT:
                string = scout.getName() + " has spotted " + getWaveUnitInfo(wave)
                        + " approaching from the " + wave.getSide();

                break;
            default:
                break;
        }
        wave.getGame().getLogManager().log(StringMaster.MESSAGE_PREFIX_ALERT + string);

    }

    private String getWaveUnitInfo(Wave wave) {
        String info = "[";
//		for (ObjType u : wave.getUnitMap().values()) {
//			info += u.getName() + ", ";
//		}
        StringMaster.cropLast(info, 2);
        info += "]";
        return info;
    }

    public enum SCOUT_INFO_LEVEL {
        VISUAL_CONTACT(30), SPOTTED_APPROACH(20), TRACKS(10), SIGNS(5),;
        private int difficulty;

        private SCOUT_INFO_LEVEL(int difficulty) {
            this.difficulty = difficulty;
        }

        public int getDifficulty() {
            return difficulty;
        }
    }

}
