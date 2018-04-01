package eidolons.ability;

import eidolons.client.cc.HeroManager;
import main.content.DC_TYPE;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.ValuePages;
import main.content.values.parameters.PARAMETER;
import main.data.DataManager;
import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import eidolons.game.core.game.DC_Game;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.datatypes.WeightMap;

import java.util.List;
import java.util.Map;

public class UnitTrainer {
    /*
     * learn skills as per mastery will requirements work fine? managing xp
	 * prioritizing skills mastery priority vs "xp/skill plan" monsters should
	 * have some other means of progression... perhaps var-passives like
	 * Feral/Rage, but also level-blocked Actives! In xp plan then, and with xp
	 * cost, so it is learned here if possible...
	 */

    private static HeroManager heroManager;

    public static void train(Unit trainee) {
        WeightMap<ObjType> pool = initXpItemPool(trainee);

        for (int i = 0; i < 100; i++) {
            ObjType newSkill = getSkill(trainee, pool);

            if (newSkill != null) {
                learn(newSkill, trainee);
            }

            pool = initXpItemPool(trainee);
            if (pool.isEmpty()) {
                break;
            }
        }

        trainee.initSkills();
        getHeroManager().update(trainee);
    }

    private static boolean isDeterministicMode(Unit trainee) {
        return trainee.getGame().isDummyMode();

    }

    private static ObjType getSkill(Unit trainee, WeightMap<ObjType> pool) {
        if (isDeterministicMode(trainee)) {
            return pool.getGreatest();
        }
        return new RandomWizard<ObjType>().getObjectByWeight(pool);
    }

    private static void generateSkillPlan(Unit trainee) {
        /*
         * weights per mastery level and skill difficulty TODO
		 */
        String plan = getPlan(trainee).replace(StringMaster.BASE_CHAR, "");
        if (!plan.isEmpty()) {
            if (!plan.endsWith(";")) {
                plan += ";"; // ++ syntax for cancelling [mastery] skills...
            }
        }
        for (PARAMETER mastery : ValuePages.MASTERIES) {
            Integer score = trainee.getIntParam(mastery);
            if (score <= 0) {
                continue;
            }
            List<ObjType> types = DataManager.toTypeList(DataManager.getTypesSubGroupNames(
             DC_TYPE.SKILLS, mastery.getName()), DC_TYPE.SKILLS);
            for (ObjType t : types) {
                if (plan.contains(t.getName())) {
                    continue;
                }
                if (!WorkspaceMaster.checkTypeIsReadyForUse(t)) {
                    continue;
                }
                int weight = Math.max(1, score - t.getIntParam(PARAMS.SKILL_DIFFICULTY));
                plan += t.getName() + StringMaster.wrapInParenthesis("" + weight)
                 + StringMaster.CONTAINER_SEPARATOR;
            }
        }

        trainee.setProperty(PROPS.XP_PLAN, plan, true);
    }

    private static void learn(ObjType newSkill, Unit trainee) {
        if (getHeroManager().addItem(trainee, newSkill, DC_TYPE.SKILLS, PROPS.SKILLS)) {
            LogMaster.log(1,
             "SKILL TRAINING: " + trainee.getName() + " learns " + newSkill.getName()
              + ", remaining xp: " + trainee.getIntParam(PARAMS.XP));
        }
        // getHeroManager().update(unit); ??
    }

    private static HeroManager getHeroManager() {
        if (heroManager == null) {
            heroManager = new HeroManager(DC_Game.game);
        }
        heroManager.setTrainer(true);
        return heroManager;
    }

    private static WeightMap<ObjType> initXpItemPool(Unit trainee) {
        if (StringMaster.isEmpty(getPlan(trainee)) || getPlan(trainee).contains(StringMaster.BASE_CHAR)) {
            generateSkillPlan(trainee);
        }
        WeightMap<ObjType> pool = new WeightMap<>();
        Map<ObjType, Integer> map = new RandomWizard<ObjType>().constructWeightMap(getPlan(trainee),
         ObjType.class, DC_TYPE.SKILLS);

        for (ObjType type : map.keySet()) {
            if (type == null) continue;
            if (trainee.checkProperty(PROPS.SKILLS, type.getName())) {
                continue; // TODO ++ exceptions
            }

            String reason = trainee.getGame().getRequirementsManager().check(trainee, type);

            if (reason != null) {
                continue;
            }

            pool.put(type, map.get(type));
            // we really can't have weights here - must be more or less
            // sequential, since it'll be skill trees!
            // and i dont wanna override reqs

        }

        // filter affordable
        // random-pick
        return pool;
    }

    private static String getPlan(Unit trainee) {
        return trainee.getProperty(PROPS.XP_PLAN);
    }

}