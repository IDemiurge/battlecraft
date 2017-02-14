package main.ability.effects.oneshot.special;

import main.ability.effects.DC_Effect;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.DC_HeroObj;

public class ActivateEffect extends DC_Effect {
    private KEYS key;
    private boolean spell;
    private String name;

    public ActivateEffect(String name, KEYS key, boolean spell) {
        this.name = name;
        this.key = key;
        this.spell = spell;
    }

    public ActivateEffect(String name) {
        this(name, KEYS.TARGET, false);
    }

    @Override
    public boolean applyThis() {
        DC_HeroObj hero = (DC_HeroObj) ref.getObj(key);
        DC_ActiveObj active;

        if (!spell) {
            active = hero.getAction(name);
        } else {
            active = hero.getSpell(name);
        }

        active.activate(Ref.getSelfTargetingRefCopy(hero));

        return true;
    }

}
