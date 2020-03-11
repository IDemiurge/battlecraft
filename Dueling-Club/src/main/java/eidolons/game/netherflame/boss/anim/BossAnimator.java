package eidolons.game.netherflame.boss.anim;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.EidolonsGame;
import eidolons.game.core.ActionInput;
import eidolons.game.netherflame.boss.entity.BossActionMaster;
import eidolons.game.netherflame.boss.entity.BossUnit;
import eidolons.game.netherflame.boss.sprite.BossView;
import eidolons.game.netherflame.boss.sprite.SpriteModel;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.main.AnimMaster;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.Chronos;

import java.io.File;

public class BossAnimator {
    public static final String SPRITE_PATH_REAPER = PathFinder.getSpritesPath() + "boss/reaper/";
    public static final String SPRITE_PATH_STONE = PathFinder.getSpritesPath() + "boss/stone/";
    private static boolean fastMode;
    private static String spritePath;
    BossView view;
    SpriteModel sprite;
    BossUnit unit;
    AnimMaster master;

    /**
     * any special tricks? Stuff that won't work in normal pipeline?
     */

    public static String getSpritePath(BattleFieldObject obj) {
        return spritePath+ "atlas.txt"; // channeling
    }

    public static final void preloadBoss() {
        Chronos.mark("Boss preload");
        for (File file : FileManager.getFilesFromDirectory(PathFinder.getImagePath() + spritePath, false, true)) {
            if (file.getName().endsWith(".txt")) {
                String path = FileManager.formatPath(GdxImageMaster.cropImagePath(file.getPath())
                        , true);
                if (EidolonsGame.BRIDGE) {
                    if (!PathUtils.getLastPathSegment(path).equalsIgnoreCase("atlas.txt"))
                        if (!PathUtils.getLastPathSegment(path).equalsIgnoreCase("atlas2.txt")) {
                            continue;
                        }
                }
                Assets.loadSprite(path, false, false);

            }
        }
        Chronos.logTimeElapsedForMark("Boss preload");
    }

    public BossAnimator(BossView view, AnimMaster master ) {
        switch (view.getUserObject().getName()) {
            case "Dream Reaper":
                spritePath = BossAnimator.SPRITE_PATH_REAPER;
                break;
            case "Storm Lord":
                spritePath = BossAnimator.SPRITE_PATH_STONE;
                break;
        }
        this.view = view;
        this.sprite = view.getSpriteModel();
        this.unit = view.getUserObject();
        this.master = master;
        GuiEventManager.bind(GuiEventType.BOSS_ACTION, p -> {
            animate((ActionInput) p.get());
        });
        GuiEventManager.bind(GuiEventType.ANIMATION_DONE, p -> {
//            animate((ActionInput) p.getVar());
        });
        if (!fastMode)
            preloadBoss();
    }

    public static void setFastMode(boolean fastMode) {
        BossAnimator.fastMode = fastMode;
    }

    public static boolean getFastMode() {
        return fastMode;
    }

    public String getRoot() {
        return spritePath;
    }


    public enum BossSpriteVariant {
        CHANNELING,
        DEATH,
        BIRTH, DEFAULT, HANDLESS,

    }

    private void updateBossAnims(ActionInput action) {

        BossSpriteVariant variant = getVariantForAction(action.getAction().getName());
        sprite.setVariant(variant);

//        if (unit.getMode() != null) {
//            sprite.setVariant(BossSpriteVariant.CHANNELING);
//            return;
//        }
    }

    private BossSpriteVariant getVariantForAction(String name) {
        switch (BossActionMaster.getReaperAction(name)) {
//            case SEVER:
            case DEATH_RAZOR__PURIFY:
                return BossSpriteVariant.CHANNELING;

//            case SOUL_RIP:
//                return BossSpriteVariant.HANDLESS;
        }
        return BossSpriteVariant.DEFAULT;
    }

    private float getSpeedForAction(String name) {

        switch (BossActionMaster.getReaperAction(name)) {
            case DEATH_RAZOR__PURIFY:
                return 2;
        }
        return 1;
    }

    private void animate(ActionInput action) {
        String spritePath;
        updateBossAnims(action);
        Anim anim = null;
        if (isWeaponAnim(action)) {
            // 3d weapon as usual? Almost.. just need offset perhaps! And that 'finish at %" for proper HIT
            anim = createWeaponAnim(action);
        } else {

        }
        float speed = getSpeedForAction(action.getAction().getName());
//        sprite.setSpeed(speed); //fluctuate?
//        sprite.setVariant(BossSpriteVariant.CHANNELING);
//                sprite.setPlayMode(); //one-time
        anim.setSpeedMod(speed);
        CompositeAnim compositeAnim = wrapAnim(anim);
        compositeAnim.setRef(action.getContext());
        master.add(compositeAnim);


//        playSounds(action);
    }


    private CompositeAnim wrapAnim(Anim anim) {
//        AnimConstructor.getOrCreate()
        return new CompositeAnim(anim);
    }

    private boolean isWeaponAnim(ActionInput action) {
        return action.getAction().isAttackAny();
    }

    private BossWeaponAnim createWeaponAnim(ActionInput action) {
        return new BossWeaponAnim(action.getAction());
    }
}