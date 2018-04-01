package eidolons.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import eidolons.content.PARAMS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;

public class LustRule extends DC_CounterRule {

    public LustRule(DC_Game game) {
        super(game);
    }

    @Override
    public String getCounterName() {
        return COUNTER.Lust.getName();
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Lust;
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        // TODO Auto-generated method stub
        return unit.getIntParam(PARAMS.SPIRIT);
    }

    @Override
    public String getBuffName() {
        return null;
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        // TODO Auto-generated method stub
        return super.getSpecialRoundEffects();
    }

    @Override
    protected Effect getEffect() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public STATUS getStatus() {
        // checkCharmed()
        return null;
    }
    /*
     * reduce focus change ownership
	 * 
	 * positive effect on Warp Demons - let them regenerate focus/morale for
	 * each Lust counter on units on adjacent cells and on gain essence from
	 * ones on themselves!
	 * 
	 * status buffs?
	 * 
	 * bewitched? :)
	 */
}