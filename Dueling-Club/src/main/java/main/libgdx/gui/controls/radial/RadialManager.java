package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.Texture;
import main.content.C_OBJ_TYPE;
import main.elements.Filter;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.TargetRunnable;
import main.libgdx.gui.controls.radial.RadialMenu.CreatorNode;
import main.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource;
import main.libgdx.texture.TextureCache;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static main.system.GuiEventManager.trigger;
import static main.system.GuiEventType.SELECT_MULTI_OBJECTS;

public class RadialManager {

    public static Texture getTextureForActive(DC_ActiveObj obj, DC_Obj target) {
        Ref ref = obj.getOwnerObj().getRef().getTargetingRef(target);
        return getTextureForActive(obj, ref);
    }

    public static Texture getTextureForActive(DC_ActiveObj obj, Ref ref) {
        return !obj.canBeActivated(ref) ?
         TextureCache.getOrCreateGrayscale(obj.getImagePath())
         : TextureCache.getOrCreate(obj.getImagePath());
    }

    public static List<RadialMenu.CreatorNode> createNew(DC_Obj target) {
        Unit source = (Unit) Game.game.getManager().getActiveObj();
        if (source == null) {
            return null;
        }
        List<ActiveObj> activeObjs = source.getActives();

        List<RadialMenu.CreatorNode> moves = new ArrayList<>();
        List<RadialMenu.CreatorNode> turns = new ArrayList<>();
        List<RadialMenu.CreatorNode> attacks = new ArrayList<>();

        Set<ActiveObj> actives = new HashSet<>();

        for (ActiveObj obj : activeObjs) {
            if (actives.contains(obj)) {
                continue;
            }
            if (obj.getTargeting() == null) {
                continue;
            }
            if (!(obj instanceof DC_ActiveObj)) {
                continue;
            }

            DC_ActiveObj dcActiveObj = ((DC_ActiveObj) obj);
            actives.add(dcActiveObj);

            if (dcActiveObj.isMove()) {
                moves.add(configureMoveNode(target, dcActiveObj));
            }

            if (dcActiveObj.isTurn()) {
                turns.add(createNode( new ImmutableTriple<>(
                 dcActiveObj::invokeClicked,
                 getTextureForActive(dcActiveObj, target),
                 dcActiveObj.getName()
                )));
            }

            if (dcActiveObj.isAttackGeneric()) {
                configureAttackNode(target, attacks, dcActiveObj);
            }

        }

        Texture examineTexture = TextureCache.getOrCreate("UI/actions/examine.png");
        Texture moveAction = TextureCache.getOrCreate("/UI/actions/Move gold.jpg");
        Texture turnAction = TextureCache.getOrCreate("/UI/actions/turn anticlockwise quick2 - Copy.jpg");
        Texture attackAction = TextureCache.getOrCreate("/mini/actions/New folder/Achievement_Arena_2v2_2.jpg");
        Texture yellow = new Texture(GridPanel.class.getResource("/data/marble_yellow.png").getPath());
        Texture red = new Texture(GridPanel.class.getResource("/data/marble_red.png").getPath());
        Texture green = new Texture(GridPanel.class.getResource("/data/marble_green.png").getPath());

        List<RadialMenu.CreatorNode> list = new LinkedList<>();

        if (C_OBJ_TYPE.UNITS_CHARS.equals(target.getOBJ_TYPE_ENUM())) {
            if (target instanceof Unit) {
                RadialMenu.CreatorNode examine = new RadialMenu.CreatorNode();
                examine.texture = examineTexture;
                examine.action = () -> {
                    GuiEventManager.trigger(
                     GuiEventType.SHOW_UNIT_INFO_PANEL,
                     new EventCallbackParam<>(new UnitDataSource(((Unit) target))));
                };
                examine.name = "examine";
                list.add(examine);
            }
        }

        RadialMenu.CreatorNode attM = new RadialMenu.CreatorNode();
        attM.texture = attacks.get(0).texture;
        attM.childNodes = attacks.get(0).childNodes;
        attM.name = attacks.get(0).name;
        list.add(attM);

        RadialMenu.CreatorNode movesN1 = new RadialMenu.CreatorNode();
        movesN1.texture = moveAction;
        movesN1.childNodes =  (moves);
        list.add(movesN1);

        if (attacks.size() > 1) {
            RadialMenu.CreatorNode attO = new RadialMenu.CreatorNode();
            attO.texture = attacks.get(1).texture;
            attO.childNodes = attacks.get(1).childNodes;
            attO.name = attacks.get(1).name;
            list.add(attO);

        }
        if (!source.getSpells().isEmpty()) {
            RadialMenu.CreatorNode spellNode = new RadialMenu.CreatorNode();
            spellNode.texture = TextureCache.getOrCreate(ImageManager.getRadialSpellIconPath());
            spellNode.childNodes = SpellRadialManager.getSpellNodes(source, target);
            spellNode.name = "Spells";
            if (spellNode.childNodes.size() == 0) {
                spellNode.action = () -> {
                };//spell manager may return empty spell list, close menu
                System.out.println("Error: SpellRadialManager.getSpellNodes() return empty list");
            }
            list.add(spellNode);
        }
        RadialMenu.CreatorNode turnsN1 = new RadialMenu.CreatorNode();
        turnsN1.texture = turnAction;
        turnsN1.childNodes =  (turns);
        list.add(turnsN1);

        return list;
    }

    private static CreatorNode configureSelectiveTargetedNode(  DC_ActiveObj active) {
        Ref ref1 = active.getRef();
        ref1.setMatch(active.getOwnerObj().getId()); // for filter
        Filter<Obj> filter = active.getTargeting().getFilter();
        filter.setRef(ref1);
        Set<Obj> objects = new HashSet<>();
        if (CoreEngine.isActionTargetingFiltersOff()) {
            for (Unit o : DC_Game.game.getUnits()) {
                objects.add(o);
            }
        } else {
            objects = filter.getObjects();
        }
        boolean valid = objects.size() > 0;

        Pair<Set<Obj>, TargetRunnable> p = new ImmutablePair<>(objects,
         active::activateOn);
        CreatorNode innn = new CreatorNode();
        innn.name = active.getName();
        innn.texture = valid ? TextureCache.getOrCreate(active.getImagePath()) : TextureCache.getOrCreateGrayscale(active.getImagePath());
        innn.action = () -> {
        if (valid){
            trigger(SELECT_MULTI_OBJECTS, new EventCallbackParam(p));
        }else {
            FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.ERROR, "", active);
        } 
        } ;


        return innn;
    }

    private static void configureAttackNode(DC_Obj target, List<RadialMenu.CreatorNode> nn1, DC_ActiveObj dcActiveObj) {
        RadialMenu.CreatorNode inn1 = new RadialMenu.CreatorNode();

        inn1.texture = getTextureForActive(dcActiveObj, target);
        inn1.name = dcActiveObj.getName();
        inn1.action = null;
        List<RadialMenu.CreatorNode> list = new ArrayList<>();
        if (dcActiveObj.getRef().getSourceObj() == target) {
            for (DC_ActiveObj active1 : dcActiveObj.getSubActions()) {
                list.add(configureSelectiveTargetedNode( active1));
            }

            inn1.childNodes = list;
            nn1.add(inn1);
        } else if (dcActiveObj.getTargeting() instanceof SelectiveTargeting) {
            for (DC_ActiveObj dc_activeObj : dcActiveObj.getSubActions()) {
                RadialMenu.CreatorNode innn = new RadialMenu.CreatorNode();
                innn.name = dc_activeObj.getName();
                innn.texture = TextureCache.getOrCreate(dc_activeObj.getImagePath());
                innn.action = () -> dc_activeObj.activateOn(target);
                list.add(innn);
            }
            inn1.childNodes = list;
            nn1.add(inn1);
        }

        DC_WeaponObj activeWeapon = dcActiveObj.getActiveWeapon();
        if (activeWeapon != null && activeWeapon.isRanged()) {
            if (dcActiveObj.getRef().getObj(Ref.KEYS.AMMO) == null) {
                for (DC_QuickItemObj ammo : dcActiveObj.getOwnerObj().getQuickItems()) {
                    RadialMenu.CreatorNode innn = new RadialMenu.CreatorNode();
                    innn.name = "Reload with " + ammo.getName();
                    innn.texture = TextureCache.getOrCreate(ammo.getImagePath());
                    innn.action = ammo::invokeClicked;
                    list.add(innn);
                }
            }
        }
    }


    private static CreatorNode configureMoveNode(DC_Obj target, DC_ActiveObj dcActiveObj) {
        Triple<Runnable, Texture, String> result;
        if (target == dcActiveObj.getOwnerObj()) {
            return  configureSelectiveTargetedNode(  dcActiveObj);
        }
        if (dcActiveObj.getTargeting() instanceof SelectiveTargeting) {
            result = new ImmutableTriple<>(
             () -> {
                 DC_Cell cell = target.getGame().getCellByCoordinate(target.getCoordinates());
                 if (dcActiveObj.getTargeter().canBeTargeted(cell.getId()))
                     dcActiveObj.activateOn(target);
             },
             TextureCache.getOrCreate(dcActiveObj.getImagePath()),
             dcActiveObj.getName()
            );
        } else {
            result = new ImmutableTriple<>(
             dcActiveObj::invokeClicked,
             getTextureForActive(dcActiveObj, target),
             dcActiveObj.getName()
            );
        }

        return createNode(result);
    }
    private static RadialMenu.CreatorNode createNode(Triple<Runnable, Texture,
     String>  triple ) {
        RadialMenu.CreatorNode inn1 = new RadialMenu.CreatorNode();
        inn1.texture = triple.getMiddle();
        inn1.action = triple.getLeft();
        inn1.name = triple.getRight();
        return inn1;
    }

        private static List<RadialMenu.CreatorNode> createNodes(List<Triple<Runnable, Texture, String>> pairs) {
        List<RadialMenu.CreatorNode> nn1 = new ArrayList<>();
        for (final Triple<Runnable, Texture, String> triple : pairs) {
            nn1.add(createNode(triple));
        }
        return nn1;
    }

    private static List<RadialMenu.CreatorNode> createNodes(final String name, Texture t) {
        List<RadialMenu.CreatorNode> nn1 = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            RadialMenu.CreatorNode inn1 = new RadialMenu.CreatorNode();
            inn1.texture = t;
            final int finalI = i;
            inn1.action = () -> System.out.println(name + finalI);
            nn1.add(inn1);
        }
        return nn1;
    }
}
