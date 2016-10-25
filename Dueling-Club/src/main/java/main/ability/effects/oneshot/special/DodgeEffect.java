package main.ability.effects.oneshot.special;

import main.elements.conditions.standard.ChanceCondition;
import main.system.math.Formula;

public class DodgeEffect extends BlockEffect {

    public DodgeEffect(Formula formula) {
        super(BLOCK_TYPES.ATTACK);
        this.formula = formula;
    }

    @Override
    public boolean applyThis() {
        conditions.add(new ChanceCondition(formula));
        return super.applyThis();
    }

}
