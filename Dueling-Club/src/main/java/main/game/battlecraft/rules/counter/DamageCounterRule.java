package main.game.battlecraft.rules.counter;

import main.ability.effects.oneshot.DealDamageEffect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.math.Formula;

public abstract class DamageCounterRule extends DC_CounterRule {

    public DamageCounterRule(DC_Game game) {
        super(game);

    }

    public String getSound() {
        // TODO Auto-generated method stub
        return null;
    }


    public abstract DAMAGE_TYPE getDamageType();

    public abstract String getDamagePerCounterFormula();

    public abstract boolean isEnduranceOnly();

    public boolean isUnblockable() {
        return true;
    }


    public boolean apply(Unit unit) {
        if (!check(unit)) {
            return false;
        }

        Ref ref = Ref.getSelfTargetingRefCopy(unit);
        return new DealDamageEffect(getDamageType().toString(),

         new Formula(getCounterRef() + " * ("
          + getDamagePerCounterFormula() + ")"),

         GenericEnums.DAMAGE_MODIFIER.PERIODIC, DAMAGE_MODIFIER.QUIET,
         (isEnduranceOnly() ? DAMAGE_MODIFIER.ENDURANCE_ONLY : null),
         (isUnblockable() ? DAMAGE_MODIFIER.UNBLOCKABLE : null))
         .apply(ref);
    }

}
