package eidolons.game.battlecraft.logic.meta.igg.soul;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import main.entity.type.ObjType;

/**
 * Shadow action costs?
 *
 * Soul Ash
 *
 *
 *
 */
public class SoulforceRule   {

    /**
     *
    exchange for life

     advance the Lord

     auto-accumulate?

     souls - manually consume or use for <...> </...>

     */

    public int applySoulforce(Unit hero, HeroChain chain) {
        int amount=0;
        chain.getParty().setParam(PARAMS.SOULFORCE, amount);

        return 0;
    }
        public int getSoulforceCostToRaise(Unit hero) {

        return 0;
    }
        public int getSoulforceCost(DC_ActiveObj action) {
        //displayed how?

        //perhaps it is Shadow's Essence?

        return 0;
    }
    public static int getSoulforceFromKill(Unit killed) {
        return getForce(killed.getType());
    }

    public static int getForce(ObjType type) {
        return type.getIntParam(PARAMS.POWER);
    }

}
