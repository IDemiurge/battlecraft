package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.enums.entity.ItemEnums.WEAPON_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.anims.sprite.SpriteAnimationFactory;
import main.libgdx.anims.weapons.Ready3dAnim;
import main.libgdx.texture.TexturePackerLaunch;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 9/6/2017.
 */
public class AnimMaster3d {

    private static final String SEPARATOR = "_";
    private static final String ANIM = "anim";
    private static final int frames = 30;
    //  TINY, SMALL, MEDIUM, LARGE, HUGE
    private static final int[] height_for_size = {
     64, 128, 196, 256, 320,
    };
    private static final int[] width_for_size = {
     256, 320, 384, 448, 512,
    };
    private static Map<String, TextureAtlas> atlasMap = new HashMap<>();

    public AnimMaster3d() {
        GuiEventManager.bind(GuiEventType.MOUSE_HOVER, p -> {

        });
    }


    public static void preloadAtlases(Unit unit) {
        DC_WeaponObj weapon = unit.getWeapon(false);
        if (weapon != null)
            getAtlas(weapon);
        weapon = unit.getWeapon(true);
        if (weapon != null)
            getAtlas(weapon);
        weapon = unit.getNaturalWeapon(false);
        if (weapon != null)
            getAtlas(weapon);
        weapon = unit.getNaturalWeapon(true);
        if (weapon != null)
            getAtlas(weapon);
        for (DC_QuickItemObj sub : unit.getQuickItems()) {
            if (sub.isAmmo()) {
                getAtlas(sub.getWrappedWeapon());
            }
        }
    }

    public static Vector2 getOffset(DC_ActiveObj activeObj) {
        return null;
    }

    public static String getAtlasFileKeyForAction(String projection,
                                                  String weaponName, String actionName,
                                                  Boolean offhand) {
        StringBuilder s = new StringBuilder();
        s.append(
         StringMaster.join(SEPARATOR,
          weaponName,
          actionName, ANIM,projection
         ));

        if (BooleanMaster.isTrue(offhand))
            s.append(SEPARATOR + "l");
        if (BooleanMaster.isFalse(offhand))
            s.append(SEPARATOR + "r");

        return s.toString().toLowerCase().replace(" ", SEPARATOR);
    }

    public static String getAtlasFileKeyForAction(Boolean projection,
                                                  DC_ActiveObj activeObj, WEAPON_ANIM_CASE aCase) {
        DC_WeaponObj weapon = activeObj.getActiveWeapon();
        String actionName = null;
        String projectionString = "to" ;

       if (aCase!= WEAPON_ANIM_CASE.RELOAD){
           projectionString =(projection == null ? "hor" :
            (projection ? "from" : "to"));
       }
        if (aCase.isMissile()) {
            if (weapon.getLastAmmo() == null)
                return null;
            weapon = weapon.getLastAmmo().getWrappedWeapon();

        }
        if (aCase.isMiss()) {
            actionName = "miss";
        } else
            switch (aCase) {
                case RELOAD:
//                    weapon
                    actionName = "reload";
                    break;
                case MISSILE:
                    actionName = null;
                    break;
                case READY:
                    actionName = "awaiting";
                    break;
                case PARRY:
                    actionName = "parry";
                    break;
                case BLOCKED:
                    actionName = "blocked";
                    break;
                default:
                    actionName = activeObj.getName().replace("Offhand ", "");
            }
        Boolean offhand = null;
        if (isAssymetric(weapon.getProperty(G_PROPS.BASE_TYPE)))
            offhand = (activeObj.isOffhand());

        return getAtlasFileKeyForAction(projectionString,
         weapon.getProperty(G_PROPS.BASE_TYPE),
         actionName, offhand);
    }

    public static String getAtlasPath(DC_WeaponObj weapon) {

        StrPathBuilder s = new StrPathBuilder(
         PathFinder.getWeaponAnimPath(), "atlas",
         weapon.getWeaponType().toString().replace("_", " ")
         , weapon.getWeaponGroup().toString().replace("_", " ")
         + TexturePackerLaunch.ATLAS_EXTENSION);
        return s.toString();
    }

    private static boolean isAssymetric(String activeWeapon) {
        switch (activeWeapon) {
            case "Fist":
                return true;

        }
        return false;
    }

    public static SpriteAnimation getFxSpriteForAction(DC_ActiveObj activeObj) {
        return null;
    }


    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     Obj targetObj, WEAPON_ANIM_CASE aCase, PROJECTION projection) {
        return getSpriteForAction(duration, activeObj, targetObj, aCase, projection.bool);
    }

    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     Obj targetObj, WEAPON_ANIM_CASE aCase, Boolean projection) {
        // loops,

        //TODO who is displayed above on the cell?
        boolean offhand = activeObj.isOffhand();
        Boolean flipHor = null;
        if (projection == null) {
            flipHor = PositionMaster.isToTheLeft(activeObj.getOwnerObj(), targetObj);
        } else {
            flipHor = offhand;
        }
//modify texture? coloring, sizing,
        float angle = PositionMaster.getAngle(activeObj.getOwnerObj(), targetObj);
//float baseAngle =
        float rotation = angle * 2 / 3;
        String name = getAtlasFileKeyForAction(projection, activeObj, aCase);

        TextureAtlas atlas = getAtlas(activeObj, aCase);
        Array<AtlasRegion> regions = atlas.findRegions(name);
        if (regions.size == 0) {
            regions = findAtlasRegions(atlas, projection, activeObj, true);
        }
        if (regions.size == 0) { if (activeObj.getParentAction()!=null )
            regions = findAtlasRegions(atlas, projection, activeObj, false);
        }
        if (regions.size == 0)
            main.system.auxiliary.log.LogMaster.log(
             1, activeObj + ": " + aCase + " no 3d sprites: " + name + atlas);
        if (TexturePackerLaunch.TRIM) {
            regions.forEach(region -> {
//                region.setRegionHeight(getHeight(activeObj));
//                region.setRegionWidth(getWidth(activeObj));
                region.setRegionWidth(region.originalWidth);
                region.setRegionHeight(region.originalHeight);
            });
        }
        float frameDuration = duration / regions.size;
        int loops = 0;
        if (aCase.isMissile()) {
            loops = Math.max(0,
             PositionMaster.getDistance(activeObj.getOwnerObj(), targetObj) - 1);
        }
        if (loops != 0)
            frameDuration /= loops;

        SpriteAnimation sprite = SpriteAnimationFactory.
         getSpriteAnimation(regions, frameDuration, loops);
//        sprite.setRotation(rotation);
        sprite.setFlipX(flipHor);
        return sprite;
    }

    private static int getWidth(DC_ActiveObj activeObj) {
        WEAPON_SIZE size = activeObj.getActiveWeapon().getWeaponSize();
        int i = EnumMaster.getEnumConstIndex(WEAPON_SIZE.class, size);
        return width_for_size[i];
    }

    private static int getHeight(DC_ActiveObj activeObj) {
        WEAPON_SIZE size = activeObj.getActiveWeapon().getWeaponSize();
        int i = EnumMaster.getEnumConstIndex(WEAPON_SIZE.class, size);
        return height_for_size[i];
    }

    private static Array<AtlasRegion> findAtlasRegions(TextureAtlas atlas,
                                                       Boolean projection,
                                                       DC_ActiveObj activeObj,
                                                       boolean searchOtherWeaponOrAction) {
        String name = getAtlasFileKeyForAction(projection, activeObj, WEAPON_ANIM_CASE.NORMAL);
        List<Entity> types = null;
        if (searchOtherWeaponOrAction) {
            types = new LinkedList<>(DataManager.getBaseWeaponTypes()).stream().
         filter(type -> type.getProperty(G_PROPS.WEAPON_GROUP).equals(
          activeObj.getActiveWeapon().getProperty(G_PROPS.WEAPON_GROUP))).collect(Collectors.toList());
        } else {
            types = new LinkedList<>(
             activeObj.getParentAction().getSubActions());
        }
        Array<AtlasRegion> regions = null;
        for (Entity sub : types) {
            name = sub.getName() + name.substring(name.indexOf(SEPARATOR));
            regions = atlas.findRegions(name.toLowerCase());

            main.system.auxiliary.log.LogMaster.log(
             1, activeObj + " searching " + name);
            if (regions.size > 0)
                break;
        }
        return regions;

    }

    private static TextureAtlas getAtlas(DC_ActiveObj activeObj, WEAPON_ANIM_CASE aCase) {
        DC_WeaponObj weapon = activeObj.getActiveWeapon();
        if (aCase.isMissile()) {
            if (weapon.getLastAmmo() == null)
                return null;
            weapon = weapon.getLastAmmo().getWrappedWeapon();
        }
        return getAtlas(weapon);
    }

    private static TextureAtlas getAtlas(DC_WeaponObj weapon) {

        String path = getAtlasPath(weapon);
        TextureAtlas atlas = atlasMap.get(path);
        if (atlas == null) {
            atlas = new TextureAtlas(path);
            atlasMap.put(path, atlas);
        }
        return atlas;
    }


    public static boolean is3dAnim(DC_ActiveObj active) {
        if (active.isRanged())
            return true;
        if (active.getName().contains("Sword Swing"))
            return true;
        if (active.getName().contains("Slash"))
            return true;
        if (active.getName().contains("Thrust"))
            return true;
        if (active.getActiveWeapon().getWeaponType() == WEAPON_TYPE.BLUNT)
            return true;
        if (active.getActiveWeapon().getWeaponGroup() == WEAPON_GROUP.FISTS)
            return true;
        return false;
    }


    public static int getWeaponActionSpeed(DC_ActiveObj active) {
        if (active.isRanged())
            return 400;
        if (active.getActiveWeapon().isTwoHanded())
            return 40;
        return 50;
    }

    public static void hoverOff(DC_UnitAction entity) {
        if (!isReadyAnimSupported(entity))
            return;
        Anim anim = getReadyAnim(entity);
        anim.setDone(true);
    }

    public static void initHover(DC_UnitAction entity) {
        if (!isReadyAnimSupported(entity))
            return;
        Anim anim = getReadyAnim(entity);
        if (!anim.isDone())
            return;
        anim.setDone(false);
        AnimMaster.getInstance().addAttached(anim);

        //counter?
    }

    private static boolean isReadyAnimSupported(DC_UnitAction entity) {
        return entity.getActiveWeapon().getName().contains("Short Sword");
    }

    private static Anim getReadyAnim(DC_UnitAction entity) {
        CompositeAnim composite = AnimMaster.getInstance().getConstructor().getOrCreate(entity);
        Anim anim = composite.getContinuous();
        if (anim == null) {
            anim = new Ready3dAnim(entity);
            composite.setContinuous(anim);
        }
        anim.start(entity.getRef());
        return anim;
    }

    public enum PROJECTION {
        FROM(true), TO(false), HOR(null),;
        public Boolean bool;

        PROJECTION(Boolean bool) {
            this.bool = bool;
        }
    }

    public enum WEAPON_ANIM_CASE {
        NORMAL,
        MISSILE_MISS,
        MISSILE,
        MISS,
        READY,
        PARRY,
        BLOCKED,
        RELOAD,;

        public boolean isMissile() {
            return this == WEAPON_ANIM_CASE.MISSILE || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }

        public boolean isMiss() {
            return this == WEAPON_ANIM_CASE.MISS || this == WEAPON_ANIM_CASE.MISSILE_MISS;
        }
    }
}
