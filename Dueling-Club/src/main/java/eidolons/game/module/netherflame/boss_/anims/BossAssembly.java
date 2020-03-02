package eidolons.game.module.netherflame.boss_.anims;

import eidolons.game.module.netherflame.boss_.BOSS_PART;
import eidolons.libgdx.gui.generic.GroupX;

import java.util.LinkedHashSet;
import java.util.Set;

public class BossAssembly extends GroupX {
    Set<BossPart> parts;
    BossAnimHandler animator;

    public BossAssembly(BOSS_PART[] parts) {
        this.parts = new LinkedHashSet<>();

        //sort by index?
        for (BOSS_PART PART : parts) {
            BossPart part = new BossPart(PART, animator);
            addActor(part);
        }
    }

    public void animate(BossAnims.BOSS_ANIM animType){
        parts.forEach(part -> animator.animate(part, animType));
    }
    public void hidePart(){

    }
}
