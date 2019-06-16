package eidolons.ability.effects.oneshot.dialog;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageSource;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.std.sprite.CustomSpriteAnim;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.audio.DC_SoundMaster;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

public class TownPortalEffect extends DC_Effect { //TODO make this a subclass!
    @Override
    public boolean applyThis() {
//TODO confirm instead?
        EUtils.onConfirm("Use this to journey back to safety?", true,
                () -> {
                    String path = RandomWizard.random() ?
                            Sprites.SHADOW_DEATH:
                            Sprites.SHADOW_SUMMON;
//                "spell/town portal.txt";
                    DC_ActiveObj action;
                    if (ref.getActive() == null) {
                        action  =getUnit().getLastAction();
                    } else {
                        action  = (DC_ActiveObj) ref.getActive();
                    }
                    CustomSpriteAnim anim = new CustomSpriteAnim(action, path) {
                    };
                    anim.setRef(ref);
                    DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.NEW__TOWN_PORTAL_START);
                    anim.setOnDone(p -> {
                        DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.NEW__TOWN_PORTAL_DONE);
                        Eidolons.onNonGdxThread(() ->
                                getGame().getMetaMaster().getTownMaster().tryReenterTown());
                    });
                    GuiEventManager.trigger(GuiEventType.CUSTOM_ANIMATION, anim);


                });

        boolean result = (boolean) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.CONFIRM);

        if (!result)
            return false;
//        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
//                msg, img, btn, false, getRunnable(), getChannel(), true));


        return true;
    }

    private Runnable getRunnable() {
        return () -> {
            getGame().getMetaMaster().getTownMaster().tryReenterTown();

        };
    }
}