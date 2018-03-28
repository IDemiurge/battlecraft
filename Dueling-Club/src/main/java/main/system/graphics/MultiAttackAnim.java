package main.system.graphics;

import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.battlecraft.rules.combat.attack.Attack;
import main.system.graphics.AnimationManager.ANIM_TYPE;

import java.awt.*;

public class MultiAttackAnim extends MultiAnim {
    Attack attack;

    public MultiAttackAnim(Ref ref, Object... args) {
        super(ANIM_TYPE.ATTACK, args);
        this.attack = (Attack) args[0];
        for (Obj obj : ref.getGroup().getObjects()) {
            AttackAnimation anim = new AttackAnimation(attack);
            animations.add(anim);
            anim.setTarget(obj);
        }
    }

    @Override
    protected PhaseAnimation createAnimation(Object mainArg, Obj targetArg) {
        return null;
    }

    @Override
    protected Image getThumbnailImage() {
        return null;
    }
}
