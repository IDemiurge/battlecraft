package main.game.battlecraft.ai.tools;

import main.content.enums.system.AiEnums;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.advanced.machine.AiConst;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.tools.priority.ParamPriorityAnalyzer;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;

import java.util.List;

/**
 * Created by JustMe on 4/9/2017.
 */
public class SituationAnalyzer extends AiHandler {

    public SituationAnalyzer(AiMaster master) {
        super(master);
    }

    public int getMeleeDangerFactor(Unit unit) {
        return getMeleeDangerFactor(unit, true, true);
    }

    public int getRangedDangerFactor(Unit unit) {
        int factor=0;
        for (Entity e : Analyzer.getVisibleEnemies(unit.getAI())) {
            Unit enemy = (Unit) e;
            int rangedThreat = getThreatAnalyzer().getRangedThreat(unit,
             enemy);
            factor += rangedThreat;
            LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Ranged threat " + rangedThreat + " from " + enemy.getName());
        }

        int mod = getConstInt(AiConst.DANGER_RANGED_BASE) - ParamPriorityAnalyzer.getUnitLifeFactor(unit);
        LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Ranged threat mod " + mod + " for " + unit.getName());

        if (mod != 0) {
            factor = factor * mod / 100;
        }
        LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Ranged threat factor " + factor + " for " + unit.getName());

        return factor;
    }

    public int getMeleeDangerFactor(Unit unit, boolean adjacentOnly, boolean now) {
        List<? extends Entity> units = (!adjacentOnly) ? Analyzer.getAdjacentEnemies(unit, false)
         : Analyzer.getMeleeEnemies(unit);
        int factor = 0;
        for (Entity e : units) {
            Unit enemy = (Unit) e;
            int meleeThreat =getThreatAnalyzer(). getMeleeThreat(enemy, now);
            factor += meleeThreat;
            LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Melee threat " + meleeThreat + " from " + enemy.getName());
        }

        int mod = getConstInt(AiConst.DANGER_RANGED_BASE) - ParamPriorityAnalyzer.getUnitLifeFactor(unit);
        LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Melee threat mod " + mod + " for " + unit.getName());

        if (mod != 0) {
            factor = factor * mod / 100;
        }
        LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Melee threat factor " + factor + " for " + unit.getName());

        return factor;
    }




    public int getCastingPriority(Unit unit) {
        UnitAI unit_ai = unit.getAI();
        if (!Analyzer.hasSpells(unit)){
//            if (!Analyzer.hasAnySpecialActions(unit))
                return 0;
            }
        // per spell? TODO

        if (unit_ai.getType() == AiEnums.AI_TYPE.BRUTE) {
            return 25;
        }
        if (unit_ai.getType() == AiEnums.AI_TYPE.ARCHER) {
            return 35;
        }
        if (unit_ai.getType() == AiEnums.AI_TYPE.TANK) {
            return 50;
        }
        if (unit_ai.getType().isRanged()) {
            return 200;
        }
        return 100;
    }

    public boolean canAttackNow(UnitAI ai) {
        for (ActiveObj a : ai.getUnit().getActives()) {
            DC_ActiveObj action = (DC_ActiveObj) a;
            if (action.isAttackAny())
                for (DC_Obj enemy : getAnalyzer().getEnemies(ai.getUnit(), false, false,
                 action.isMelee())) {
                    if (action.canBeActivated())
                        if (action.canBeTargeted(enemy.getId()))
                            return true;
                }
        }
        return false;
    }
}
