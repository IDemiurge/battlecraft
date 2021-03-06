package libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.bf.grid.cell.BaseView;
import eidolons.content.consts.Sprites;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.List;

public class SpriteMaster {
    private static final int DEFAULT_FPS = 12;
    private static int n = 1;
    private static SpriteX s;

    public static List<SpriteX> getSpriteForUnit(BattleFieldObject obj, boolean over, BaseView baseView) {
        String path = getPath(obj, over);
        List<SpriteX> list = new ArrayList<>();
        if (path == null) {
            return list;
        }
        List<String> paths = ContainerUtils.openContainer(path);
        n = paths.size();
        for (int i = 0; i < paths.size(); i++) {
            s = new SpriteX(paths.get(i));
            BfObjEnums.SPRITES sprite = null;
            for (BfObjEnums.SPRITES sprites : BfObjEnums.SPRITES.values()) {
                if (sprite == BfObjEnums.SPRITES.EMPTY) {
                    continue;
                }
                if (FileManager.formatPath(sprites.path, true).equalsIgnoreCase(
                        FileManager.formatPath(paths.get(i), true))) {
                    sprite = sprites;
                }
            }
            if (sprite == null) {
                sprite = BfObjEnums.SPRITES.EMPTY;
            }
            list.add(s);
            s.setFlipX(isFlipX(sprite, n, i));
//            s.setFlipyX(isFlipX( paths.getVar(i), n , i ));
            s.setFps(getFps(sprite, over, obj));
            s.setBlending(getBlending(sprite, over, obj));

            if (getColor(i, sprite, over, obj) != null) {
                s.setColor(getColor(i, sprite, over, obj));
            }

            s.setRotation(getRotation(sprite, over, obj, i));
            s.setX(getX(sprite, over, obj, i, n));
            s.setY(getY(sprite, over, obj, i, n));
//            if (over)
            boolean offset = isOffset(sprite, over, i, n);
            if (offset) {
                s.getSprite().centerOnParent(baseView);
                // s.getSprite().setOffsetX(s.getWidth() / 2 - 64);
                // s.getSprite().setOffsetY(s.getHeight() / 2 - 64);
            }
            s.setOrigin(0, s.getHeight() / 2);
            if (obj.isOverlaying()) {
                s.setScale(0.5f);
                // s.setWidth(s.getWidth()/2);
                // s.setHeight(s.getHeight()/2);
            }
            s.act(RandomWizard.getRandomFloatBetween(0, 3));
            if (!obj.isOverlaying())
                if (obj.isLightEmitter()) {
                s.getSprite().centerOnParent(baseView);
                s.getSprite().setOffsetX(40);
                s.getSprite().setOffsetY(64);
            }
        }
//        s.setScale();
//        s.setColor();

        return list;
    }

    private static boolean isOffset(BfObjEnums.SPRITES sprite, boolean over, int i, int n) {
        switch (sprite) {
            case BONE_WINGS:
                return false;
        }
        return true;
    }

    private static boolean isFlipX(BfObjEnums.SPRITES s, int n, int i) {
        boolean odd = i % 2 == 1;
        switch (s) {
            case BONE_WINGS:
                return !odd;
        }
        return false;
    }

    private static float getRotation(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj, int i) {
        switch (sprite) {
            case TENTACLE:
            case WHITE_TENTACLE:
                if (obj.getName().contains("Adept")) {
                    return 90;
                }
                return 360 / n * i;
        }
        return 0;
    }

    private static float getY(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj, int i, int n) {
        if (over && n <= 1) {
            return 0;
        }
        // if (sprite==ORB)
        //     return 32;
        // if (sprite==ALTAR) {
        //     return 64;
        // }
        return -i * s.getHeight() / 12 + i * i * s.getHeight() / 54;
    }

    private static float getX(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj, int i, int n) {
        // if (sprite==ORB) {
        //     return 32;
        // } if (sprite==ALTAR) {
        //     return 64;
        // }
//        if (getParent() instanceof BaseView) {
//            switch (((BaseView) getParent()).getUserObject().getName()) {
//                case "Eldritch Sphere":
//                    getSprite().setOffsetX( getWidth()-128+32);
//                    getSprite().setOffsetY( getHeight()-128);
//                    break;
//                case "Ghost Light":
//                    getSprite().setBlending(GenericEnums.BLENDING.SCREEN);
//                    break;
//            }
//        }
        if (over && n <= 1) {
            return 0;
        }
        return -i * s.getWidth() / 12 + i * i * s.getWidth() / 54 + s.getWidth() / 2;
    }

    private static String getPath(BattleFieldObject obj, boolean over) {
        String spritePath = null;
        String parsed = "";
        String toParse = "";
        if (over) {
            toParse = obj.getProperty(PROPS.OVERLAY_SPRITES);
        } else if (!obj.getProperty(PROPS.UNDERLAY_SPRITES).isEmpty()) {
            toParse = obj.getProperty(PROPS.UNDERLAY_SPRITES);
        }
        if (!toParse.isEmpty()) {
            for (String s : ContainerUtils.openContainer(toParse)) {
                BfObjEnums.SPRITES c = new EnumMaster<BfObjEnums.SPRITES>().
                        retrieveEnumConst(BfObjEnums.SPRITES.class, s);
                if (c != null) {
                    parsed += c.path + ";";
                }
            }
            return parsed;
        }
        if (over) {
            if (obj.isLightEmitter()) {
                return Sprites.FIRE_LIGHT;
            }
              {
                switch (obj.getName()) {
                    case "Dream Siphon":
                        return StringMaster.getStringXTimes(4, Sprites.WHITE_TENTACLE + ";");
                    case "Mystic Pool":
                        return StringMaster.getStringXTimes(2, Sprites.FLOAT_WISP + ";");
                    case ("Ghost Light"):
                        return Sprites.FLOAT_WISP;
                    case ("Inscription"):
                        return Sprites.RUNE_INSCRIPTION;
                    case ("Torch"):
                        return Sprites.TORCH;
                    case ("Eldritch Sphere"):
                        return Sprites.ORB;
                }
            }
        } else {
//            if (obj instanceof Unit)

            switch (obj.getName()) {
//                case "Hollow Adept":
//                    return Sprites.BONE_WINGS;
//                      return StringMaster.getStringXTimes(2, Sprites.BONE_WINGS + ";");
//                    return StringMaster.getStringXTimes(2, Sprites.WHITE_TENTACLE + ";");

                case "Charger":
                    return StringMaster.getStringXTimes(4, Sprites.WHITE_TENTACLE + ";");
                case "Pale Wing":
                case "Black Wing":
                    return StringMaster.getStringXTimes(2, Sprites.BONE_WINGS + ";");
//                case "Mistborn Horror":
//                    return StringMaster.getStringXTimes(8, Sprites.WHITE_TENTACLE+";");
//                    return StringMaster.getStringXTimes(6, Sprites.WHITE_TENTACLE + ";");
            }

        }
        return spritePath;
    }

    private static Color getColor(int i, BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj) {
        if (obj.isLightEmitter()) {
          return  GdxColorMaster.getColorForTheme(obj.getColorTheme());
        }
        switch (obj.getName()) {
            case "Netherbound Horror":
                if (i==4) {
                    return new Color(0.78f, 1, 0.46f, 0);
                }
                return new Color(0.78f, 1, 0.46f, 1);
        }
        return null;
    }

    private static GenericEnums.BLENDING getBlending(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj) {
        switch (obj.getName()) {
            case "Adeptus Carnifex":
            case "Black Wing":
//            case "Mistborn Horror":
            case "Dream Siphon":
                //            case "Netherbound Horror":
                return GenericEnums.BLENDING.INVERT_SCREEN;
            case "Hollow Adept":
                return GenericEnums.BLENDING.SCREEN;
        }
        switch (sprite) {
            case FIRE_LIGHT:
            case VEIL:
            case FLOAT_WISP:
            case WHITE_TENTACLE:
                return GenericEnums.BLENDING.SCREEN;
        }
        if (obj.isOverlaying()) {
            return GenericEnums.BLENDING.SCREEN;
        }
        if (over) {
            return GenericEnums.BLENDING.SCREEN;
        }
        return null;
    }

    private static int getFps(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj) {
        return DEFAULT_FPS;
    }
}
