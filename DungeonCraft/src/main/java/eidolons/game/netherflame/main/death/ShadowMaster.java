package eidolons.game.netherflame.main.death;

import com.badlogic.gdx.graphics.g2d.Animation;
import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.netherflame.main.NF_Images;
import eidolons.game.netherflame.main.NF_Meta;
import eidolons.game.netherflame.main.NF_MetaMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.tips.TipMessageSource;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;

import static eidolons.system.text.tips.TIP.*;
@Deprecated
public class ShadowMaster extends MetaGameHandler<NF_Meta> {

    private static final boolean TEST_MODE = false;
    static float timesThisHeroFell = 0;
    private static Unit shade;
    private static boolean shadowAlive;
    private boolean summonActive;

    public ShadowMaster(MetaGameMaster master) {
        super(master);
    }

    public static void reset() {
        shade = null;
        shadowAlive = false;
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING, null);
    }

    public static boolean isOn() {
        //what about exploration 'deaths'?
        if (!TEST_MODE)
            if (EidolonsGame.BOSS_FIGHT || EidolonsGame.TUTORIAL_PATH) {
                return false;
            }
        return !OptionsMaster.getGameplayOptions().getBooleanValue(
                GameplayOptions.GAMEPLAY_OPTION.DEATH_SHADOW_OFF);
    }

    public static boolean isShadowAlive() {
        return shadowAlive;
    }

    public static Unit getShadowUnit() {
        return shade;
    }

    public   void heroRecovers() {
        unsummonShade(null, false);
    }

    public boolean death() {
        //        if (timesThisHeroFell==0){
        //            return false;
        //        }
        timesThisHeroFell = 0;
        return true;
    }

    public void heroFell(Event event) {
        if (!isOn()) {
            return;
        }
        if (shadowAlive)
            return;
        if (summonActive)
            return;
        summonActive = true;
        getGame().getLoop().setPaused(true);
        //TODO Gdx sync
        // AnimMaster.waitForAnimations(null);
        // DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__SHADOW_FALL);
        // ShadowAnimation anim = new ShadowAnimation(true, (Entity) event.getRef().getActive(),
        //         () -> afterFall(event));
        // GuiEventManager.trigger(GuiEventType.CUSTOM_ANIMATION, anim);

    }

    private void afterFall(Event event) {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__SHADOW_PRE_SUMMON);
        GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK, 1);
        WaitMaster.WAIT(600);
        if (Eidolons.getMainHero().isDead()) {
            //  TODO this shouldn't happen
            //   dialogueFailed(event, true);
            main.system.auxiliary.log.LogMaster.log(1, "SHADOW: hero was dead when fall event happened! ");
            getMaster().getMissionMaster().getOutcomeManager().defeat(false, true);
        }
        if (ExplorationMaster.isExplorationOn()) {
            /*
            just an SF penalty for slipping during Explore;
            if no SF, then it's actually a lose? Maybe with some roll-chance, anyway
             */
            EUtils.showInfoText("My Avatar fumbles...");
            if (getMaster().getSoulforceMaster().slipPenalty()) {
                //TODO animation
                restoreHero( );
                LogMaster.log(1, "SHADOW: fall prevented; restoreHero! " + event);
                EUtils.showInfoText(true, "A narrow escape...");
            } else {
                //TODO  death();
                getMaster().getMissionMaster().getOutcomeManager().defeat(false, true);
            }
            summonActive = false;
            return;
        }
        timesThisHeroFell++;
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                UNCONSCIOUS.getMessage(),
                NF_Images.SHADOW, "I Am Become Death", false, () ->
                summonShade(event)));
    }

    public void annihilated(Event event) {
        if (!isOn()) {
            return;
        }
        dialogueFailed(event, false);
    }


    private void unsummonShade(Event event, boolean defeat) {
        Eidolons.onThisOrNonGdxThread(() -> {
            LogMaster.log(1, "SHADOW: unsummonShade " + event);
            shade.removeFromGame();
            shadowAlive = false;
            GuiEventManager.trigger(GuiEventType.POST_PROCESSING, null);
            if (!defeat) {
                out();
                restoreHero( );
            } else
                getMaster().getMissionMaster().getOutcomeManager().defeat(false, true);
        });
    }

    private void restoreHero( ) {
        LogMaster.log(1, "SHADOW: restoreHero ");
        Unit hero = Eidolons.getMainHero();

      getGame().getRules().getUnconsciousRule().unitRecovers(hero);
        hero.setParam(PARAMS.C_FOCUS, hero.getParam(PARAMS.STARTING_FOCUS));
        hero.resetDynamicParam(PARAMS.C_TOUGHNESS);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, hero);
        //        getMaster().getGame().getRules().getUnconsciousRule().checkUnitAnnihilated();
    }

    public void victory(Event event) {
        if (!shadowAlive)
            return;
        LogMaster.log(1, "SHADOW: victory ");

        //TODO Gdx sync
        // AnimMaster.waitForAnimations(null);
        // ShadowAnimation anim = new ShadowAnimation(false, (Entity) event.getRef().getActive(),
        //         () -> afterVictory(event)) {
        //     @Override
        //     protected Animation.PlayMode getPlayMode() {
        //         return Animation.PlayMode.REVERSED;
        //     }
        // };
        // GuiEventManager.trigger(GuiEventType.CUSTOM_ANIMATION, anim);

    }

    private void afterVictory(Event event) {
        String msg = SHADE_RESTORE.getMessage();
        //              btn="Into Evernight";
        String btn = "Return";
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                msg, NF_Images.SHADOW, btn, false, () ->
                unsummonShade(event, false)));
    }

    private void dialogueFailed(Event event, boolean outOfTime) {
        if (!shadowAlive)
            return;

        shade.getGame().getLoop().setPaused(true);

        // AnimMaster.waitForAnimations(null);
        String msg = outOfTime ? DEATH_SHADE_TIME.getMessage() : DEATH_SHADE
                .getMessage();
        String btn = "Onward!";
        if (getMaster().getDefeatHandler().isNoLivesLeft()) {
            DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__DEFEAT);
            msg = DEATH_SHADE_FINAL.getMessage();
            //              btn="Into Evernight";
            btn = "Enter the Void";
        }
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                msg, NF_Images.SHADOW, btn, false, () ->
                unsummonShade(event, true)));
    }

    private void out() {
        // TODO  what do we do here?
        // if (getGame().getLoop() instanceof CombatLoop) {
        //     ((CombatLoop) getGame().getLoop()).endCombat();
        // }
    }

    private void summonShade(Event event) {
        LogMaster.log(1, "SHADOW: summonShade " + event);

        Ref ref = event.getRef().getCopy();
        Coordinates c = ref.getSourceObj().getCoordinates();
        FACING_DIRECTION facing = Eidolons.getMainHero().getFacing();
        Coordinates finalC = c;
        c = Positioner.adjustCoordinate(shade,
                c.getAdjacentCoordinate(facing.flip().getDirection()), facing,
                c1 -> new ClearShotCondition().check(finalC, c1) && finalC.dst(c1) <= 1.5f);
        DC_Cell cell = getGame().getCellByCoordinate(c);

        ref.setTarget(cell.getId());

        new SummonEffect("Eidolon Shadow").apply(ref);
        shade = (Unit) ref.getObj(Ref.KEYS.SUMMONED);
        shade.setShadow(true);
        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, shade);
        GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO, shade);
        //        GuiEventManager.trigger(GuiEventType.GAME_RESET, shade);
        shade.setDetectedByPlayer(true);
        getGame().getLoop().setPaused(false);
        //horrid sound!
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__SHADOW_SUMMON);

        //TODO Gdx sync
        // ShadowAnimation anim = new ShadowAnimation(false, (Entity) event.getRef().getActive(),
        //         () -> {
        //             GuiEventManager.trigger(GuiEventType.POST_PROCESSING, PostFxUpdater.POST_FX_TEMPLATE.UNCONSCIOUS);
        //             DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__DREAD);
        //         });
        // GuiEventManager.trigger(GuiEventType.CUSTOM_ANIMATION, anim);
        shadowAlive = true;
        summonActive = false;
    }

    @Override
    public NF_MetaMaster getMaster() {
        return (NF_MetaMaster) super.getMaster();
    }

}
