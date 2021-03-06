package eidolons.ability;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.values.ValuePages;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.HeroManager;
import eidolons.game.module.herocreator.logic.spells.SpellMaster;
import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.RequirementsManager;
import main.entity.type.ObjType;
import main.system.auxiliary.*;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;

import java.util.List;
import java.util.Map;

public class UnitLibrary {
    private static Map<ObjType, Integer> spellPool;
    private static Unit unit;
    private static HeroManager heroManager;

    public static void learnSpellsForUnit(Unit learner) {
        unit = learner;
        // a better approach: iterate thru new/memo/verb until <...>
        // at each iteration, learn 1 new spell if possible, memorize max and
        // getOrCreate the rest max en verbatim
        if (!unit.checkProperty(PROPS.SPELL_PLAN) || getPlan().contains(Strings.BASE_CHAR)) {
            if (UnitAnalyzer.checkIsCaster(unit)) {
                generateSpellPlan();
            } else {
                return;
            }
        }
        boolean result = true;
        while (result) {
            result = learnSpells(LEARN_CASE.NEW) || learnSpells(LEARN_CASE.MEMORIZE)
             || learnSpells(LEARN_CASE.EN_VERBATIM);
        }

    }

    private static void generateSpellPlan() {
        String property = getPlan();
        String plan = property.replace(Strings.BASE_CHAR, "");
        if (!plan.isEmpty()) {
            if (!plan.endsWith(";")) {
                plan += ";"; // ++ syntax for cancelling [group] spells...
            }
        }
        StringBuilder planBuilder = new StringBuilder(plan);
        for (PARAMETER mastery : ValuePages.MASTERIES_MAGIC_SCHOOLS) {
            Integer score = unit.getIntParam(mastery);
            if (score <= 0) {
                continue;
            }
            List<ObjType> types = DataManager.getTypesGroup(DC_TYPE.SPELLS, mastery.getName());
            for (ObjType t : types) {
                if (planBuilder.toString().contains(t.getName())) {
                    continue;
                }
                int weight = Math.max(1, score - t.getIntParam(PARAMS.SPELL_DIFFICULTY));
                planBuilder.append(t.getName()).append(StringMaster.wrapInParenthesis("" + weight)).append(Strings.CONTAINER_SEPARATOR);
            }
        }
        plan = planBuilder.toString();
        unit.setProperty(PROPS.SPELL_PLAN, plan, true);
    }

    private static String getPlan() {
        return unit.getProperty(PROPS.SPELL_PLAN);
    }

    private static boolean learnSpells(LEARN_CASE lc) {
        if (lc == LEARN_CASE.UPGRADE) {
            // ???
        }
        boolean result = false;
        // balancing between learning new and learning en verbatim...
        initPool(lc);
        Loop.startLoop(75);
        while (!Loop.loopEnded() && !spellPool.isEmpty()) {
            ObjType spellType = new RandomWizard<ObjType>().getObjectByWeight(spellPool);
            if (checkCanLearnSpell(spellType, lc)) {
                if (!learnSpell(spellType, lc)) {
                    return false;
                }
                // spellPool.remove(spellType);
                if (lc == LEARN_CASE.NEW || lc == LEARN_CASE.UPGRADE) {
                    return true; // TODO *one at a time, right?*
                }
                result = true;
            }
            initPool(lc);
        }
        unit.initSpells(true);
        return result;
    }

    private static void initPool(LEARN_CASE lc) {
        SpellMaster.initSpellbook(unit);
        spellPool = new XLinkedMap<>();
        for (String substring : ContainerUtils.open(unit.getProperty(getSourceProp(lc)))) {
            ObjType type = DataManager.getType(VariableManager.removeVarPart(substring),
             DC_TYPE.SPELLS);
            if (checkCanLearnSpell(type, lc)) {
                Integer weight = StringMaster.getWeight(substring);
                if (weight <= 0) {
                    weight = 1;
                }
                spellPool.put(type, weight);
            }
        }
    }

    private static boolean checkCanLearnSpell(ObjType type, LEARN_CASE lc) {
        if (type == null) {
            return false;
        }

        if (!WorkspaceMaster.checkTypeIsReadyForUse(type)) {
            return false;
        }

        if (lc == LEARN_CASE.NEW || lc == LEARN_CASE.UPGRADE) {
            if (SpellMaster.checkKnown(unit, type)) {
                return false; // already in reqs?
            }
        }
        if (lc == LEARN_CASE.EN_VERBATIM || lc == LEARN_CASE.MEMORIZE) {
            if (!SpellMaster.checkKnown(unit, type)) {
                return false;
            }
        }

        if (lc == LEARN_CASE.EN_VERBATIM || lc == LEARN_CASE.MEMORIZE) {
            if (!SpellMaster.checkKnown(unit, type)) {
                return false;
            }
        }

        if (unit.checkProperty(getTargetProp(lc), type.getName())) {
            return false;
        }
        String reason = unit.getGame().getRequirementsManager().check(unit, type, getMode(lc));
        if (reason == null) {
            return true;
        } else {// TODO there could be more than one reason, right? =)
            if (reason.equals(InfoMaster.SPELL_KNOWN)) {
                if (lc == LEARN_CASE.EN_VERBATIM) {
                    return true;
                }
            }
            if (reason.contains(PARAMS.SPELL_POINTS.getName())) {
                // TODO discount
                return unit.checkParam(PARAMS.SPELL_POINTS_UNSPENT,
                        getSpellPointCost(type)  );
            }
        }
        return false;
    }

    private static int getSpellPointCost(ObjType type) {
        return type.getIntParam(PARAMS.CIRCLE);
    }

    private static int getMode(LEARN_CASE lc) {
        if (lc == LEARN_CASE.EN_VERBATIM) {
            return RequirementsManager.VERBATIM_MODE;
        }
        return (lc == LEARN_CASE.MEMORIZE) ? RequirementsManager.ALT_MODE
         : RequirementsManager.NORMAL_MODE;
    }

    private static void learnSpellUpgradesForUnit() {
        // TODO
        // if (empty) autoupgrade = true; => find and add!

        // DataManager.getTypesGroup(TYPE, group)
    }

    private static boolean learnSpell(ObjType spellType, LEARN_CASE lc) {
        boolean result;
        if (lc == LEARN_CASE.MEMORIZE) {
            result = getHeroManager().addMemorizedSpell(unit, spellType);
        } else {
            result = getHeroManager().addItem(unit, spellType, DC_TYPE.SPELLS, getTargetProp(lc),
             false, false);
        }
        if (!result) {
            return false;
        }
        LogMaster.devLog("SPELL TRAINING: " + unit.getName() + " learns "
         + spellType.getName() + " (" + lc.toString() + "), remaining sps: "
         + unit.getIntParam(PARAMS.SPELL_POINTS_UNSPENT));

//        getHeroManager().update(unit);
        return true;
    }

    private static HeroManager getHeroManager() {
        if (heroManager == null) {
            heroManager = new HeroManager(DC_Game.game);
            heroManager.setTrainer(true);
        }
        return heroManager;
    }

    private static PROPERTY getTargetProp(LEARN_CASE lc) {
        switch (lc) {
            case EN_VERBATIM:
                return PROPS.VERBATIM_SPELLS;
            case MEMORIZE:
                return PROPS.MEMORIZED_SPELLS;
            case NEW:
            case UPGRADE:
                return PROPS.LEARNED_SPELLS;
        }
        return null;
    }

    private static PROPERTY getSourceProp(LEARN_CASE lc) {
        switch (lc) {
            case EN_VERBATIM:
            case MEMORIZE:
                return PROPS.SPELLBOOK;
            case NEW:
                return PROPS.SPELL_PLAN;
        }
        return null;
    }

    public enum LEARN_CASE {
        NEW, EN_VERBATIM, MEMORIZE, UPGRADE
    }
}
