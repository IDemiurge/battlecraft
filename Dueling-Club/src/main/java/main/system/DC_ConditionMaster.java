package main.system;

import main.ability.conditions.*;
import main.ability.conditions.req.CellCondition;
import main.ability.conditions.req.CostCondition;
import main.ability.conditions.req.ItemCondition;
import main.ability.conditions.shortcut.RangeCondition;
import main.ability.conditions.shortcut.SpaceCondition;
import main.ability.conditions.shortcut.StdPassiveCondition;
import main.ability.conditions.special.ClearShotCondition;
import main.ability.conditions.special.GraveCondition;
import main.ability.conditions.special.SneakCondition;
import main.ability.conditions.special.SpellCondition;
import main.ability.conditions.special.SpellCondition.SPELL_CHECK;
import main.content.CONTENT_CONSTS.*;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.*;
import main.elements.conditions.standard.ClassificationCondition;
import main.elements.conditions.standard.OwnershipCondition;
import main.elements.conditions.standard.ZLevelCondition;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class DC_ConditionMaster extends ConditionMaster {
    public static Condition getMoveFilterCondition() {
        return new MoveCondition();
    }

    public static Condition getAliveFilterCondition() {
        return new NotCondition(new StatusCheckCondition(STATUS.DEAD));
    }

    private static Condition findConditionTemplate(String string) {
        TARGETING_MODIFIERS TARGETING_MODIFIERS = new EnumMaster<TARGETING_MODIFIERS>()
                .retrieveEnumConst(TARGETING_MODIFIERS.class, string);
        if (TARGETING_MODIFIERS != null)
            return getTargetingModConditions(TARGETING_MODIFIERS);

        // TARGETING_MODE TARGETING_MODE = new
        // EnumMaster<TARGETING_MODE>().retrieveEnumConst(TARGETING_MODE.class,
        // string) ;
        // if (TARGETING_MODE!=null) TODO
        // return ActivesConstructor.getSingleTargeting(obj)
        // getTargetingModConditions(TARGETING_MODE);

        return null;
    }

    public static Condition getTargetingModConditions(TARGETING_MODIFIERS MOD) {
        String obj_ref = KEYS.MATCH.toString();
        switch (MOD) {
            case SPACE:
                return new SpaceCondition();
            case RANGE:
                return new RangeCondition();
            case FACING:
                return new FacingCondition(FACING_SINGLE.IN_FRONT);
            case VISION:
                return new VisibilityCondition(UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT);
            case NO_VISION:
                return new NotCondition(new VisibilityCondition(UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT));
            case SNEAKY:
                return new SneakCondition();
            case ONLY_LIVING:
                return ConditionMaster.getLivingMatchCondition();
            case NO_EVIL:
                return new NotCondition(new OrConditions(new ClassificationCondition(
                        CLASSIFICATIONS.UNDEAD),
                        new ClassificationCondition(CLASSIFICATIONS.DEMON), new PropCondition(
                        G_PROPS.PRINCIPLES, PRINCIPLES.TREACHERY)));
            case ONLY_EVIL:
                return new OrConditions(new ClassificationCondition(CLASSIFICATIONS.UNDEAD),
                        new ClassificationCondition(CLASSIFICATIONS.DEMON), new PropCondition(
                        G_PROPS.PRINCIPLES, PRINCIPLES.TREACHERY));

            case ONLY_UNDEAD:
                return new ClassificationCondition(CLASSIFICATIONS.UNDEAD);
            case CLEAR_SHOT:
                return getClearShotCondition(obj_ref);
            case NOT_SELF:
                return new NotCondition(new RefCondition(KEYS.MATCH, KEYS.SOURCE));
            case NO_FRIENDLY_FIRE:
                return new NotCondition(new OwnershipCondition(KEYS.MATCH, KEYS.SOURCE));
            case NO_ENEMIES:
                return new NotCondition(new OwnershipCondition(true, KEYS.MATCH + "", KEYS.SOURCE
                        + ""));
            case NO_NEUTRALS:
                return new NotCondition(new OwnershipCondition(KEYS.MATCH + "", true));
            case NO_LIVING:
                return new NotCondition(ConditionMaster.getLivingMatchCondition());
            case NO_UNDEAD:
                return new NotCondition(new ClassificationCondition(CLASSIFICATIONS.UNDEAD));
            default:
                break;
        }
        return null;
    }

    public static Requirement getSpecialReq(String subString, Entity entity) {

        SPECIAL_REQUIREMENTS CONST = new EnumMaster<SPECIAL_REQUIREMENTS>().retrieveEnumConst(
                SPECIAL_REQUIREMENTS.class, VariableManager.removeVarPart(subString));

        if (CONST == null)
            return null;
        Object[] variables = null;
        if (VariableManager.checkVar(subString)) {
            variables = VariableManager.getVariables(subString);
        }
        Condition condition = null;
        switch (CONST) {
            case PARAM:
            case COUNTER:
                condition = new NumericCondition("{SOURCE" + variables[0] + "}", variables[1]
                        .toString());
                break;
            case CUSTOM:
                condition = ConditionMaster.toConditions(variables[0].toString());
                break;

            // boolean var for NOT
            case FREE_CELL:
                condition = getFreeCellCondition(KEYS.SOURCE.toString(), variables[0].toString());
                break;
            case ITEM:
                condition = getItemCondition(variables[0].toString(), variables[1].toString(),
                        variables[2].toString(), KEYS.SOURCE.toString());
                break;
            case NOT_ITEM:
                condition = new NotCondition(getItemCondition(variables[0].toString(), variables[1]
                        .toString(), variables[2].toString(), KEYS.SOURCE.toString()));
                break;
            case REF_NOT_EMPTY: {
                condition = new RefNotEmptyCondition(variables[0].toString(), variables[1]
                        .toString());

                break;
            }
            case FREE_CELL_RANGE:
                break;
            case HAS_ITEM:
                break;
            case NOT_FREE_CELL:
                break;
            default:
                break;
        }
        if (condition == null)
            return null;
        Requirement req = new Requirement(condition, CONST.getText(variables));
        return req;
    }

    private static Condition getItemCondition(String slot, String prop, String val, String obj_ref) {
        return new ItemCondition(obj_ref, slot, prop, val);
    }

    public static Condition getFreeCellCondition(String obj_ref, String direction) {
        UNIT_DIRECTION CONST = new EnumMaster<UNIT_DIRECTION>().retrieveEnumConst(
                UNIT_DIRECTION.class, direction);
        if (CONST == null)
            return null;
        return new CellCondition(obj_ref, CONST);
    }

    public static Conditions getSelectiveTargetingTemplateConditions(
            SELECTIVE_TARGETING_TEMPLATES template) {
        Conditions c = new Conditions();

        if ((template.isDependentOnZ())) {
            c.add(new ZLevelCondition(true));// source and match?
        }

        switch (template) {
            case ANY_ARMOR: {
                c.add(new ObjTypeComparison(OBJ_TYPES.ARMOR));
                break;
            }
            case ANY_WEAPON: {
                c.add(new NotCondition(new PropCondition(G_PROPS.WEAPON_TYPE, WEAPON_TYPE.NATURAL
                        .toString())));
                c.add(new OrConditions(new ObjTypeComparison(OBJ_TYPES.WEAPONS), new Conditions(
                        new PropCondition(G_PROPS.STD_BOOLS, STD_BOOLS.WRAPPED_ITEM),
                        new ObjTypeComparison(OBJ_TYPES.ITEMS))));
                break;
            }
            case ANY_ITEM: {
                c.add(new ObjTypeComparison(OBJ_TYPES.ITEMS));
                break;
            }
            case ENEMY_ARMOR: {
                c.add(new ObjTypeComparison(OBJ_TYPES.ARMOR));
                c.add(ConditionMaster.getEnemyCondition());
                break;
            }
            case ENEMY_ITEM: {
                c.add(new ObjTypeComparison(OBJ_TYPES.ITEMS));
                c.add(ConditionMaster.getEnemyCondition());
                break;
            }
            case ENEMY_WEAPON: {
                c.add(new NotCondition(new PropCondition(G_PROPS.WEAPON_TYPE, WEAPON_TYPE.NATURAL
                        .toString())));
                c.add(new OrConditions(new ObjTypeComparison(OBJ_TYPES.ITEMS),
                        new ObjTypeComparison(OBJ_TYPES.WEAPONS)));
                c.add(ConditionMaster.getEnemyCondition());
                break;
            }
            case MY_ITEM: {
                c.add(new ObjTypeComparison(OBJ_TYPES.ITEMS));
                break;
            }

            case MY_SPELLBOOK:
                c.add(ConditionMaster.getTYPECondition(OBJ_TYPES.SPELLS));
                c.add(new SpellCondition(SPELL_CHECK.ACTIVE));
                c.add(new RefCondition(KEYS.MATCH_SOURCE, KEYS.SOURCE));
                break;

            case MY_ARMOR: {
                // c.add(new ObjTypeComparison(OBJ_TYPES.ARMOR));
                c.add(new RefCondition(KEYS.ARMOR, KEYS.MATCH));
                break;
            }
            case MY_WEAPON: {
                c.add(new NotCondition(new PropCondition(G_PROPS.WEAPON_TYPE, WEAPON_TYPE.NATURAL
                        .toString())));
                c.add(new OrConditions(new ObjTypeComparison(OBJ_TYPES.WEAPONS), new Conditions(
                        new PropCondition(G_PROPS.STD_BOOLS, STD_BOOLS.WRAPPED_ITEM),
                        new ObjTypeComparison(OBJ_TYPES.ITEMS))));
                // c.add(new RefCondition(KEYS.MATCH_SOURCE, KEYS.SOURCE));
                // c.add(new RefCondition(KEYS.WEAPON, KEYS.MATCH));
                break;
            }
            case CELL: {
                c.add(ConditionMaster.getTYPECondition(OBJ_TYPES.TERRAIN));
                c.add(getRangeCondition());
                c.add(new CellCondition(true));
                break;
            }
            case BLAST: {
                c.add(ConditionMaster.getUnit_Char_BfObj_TerrainTypeCondition());
                c.add(getRangeCondition());
                c.add(new FacingCondition(FACING_SINGLE.IN_FRONT));
                c.add(new NotCondition(ConditionMaster.getSelfFilterCondition()));
                break;
            }
            case BLIND_SHOT:
            case SHOT:
                c.add(ConditionMaster.getUnit_Char_BfObjTypeCondition());
                c.add(new NotCondition(ConditionMaster.getSelfFilterCondition()));
                c.add(new FacingCondition(FACING_SINGLE.IN_FRONT));
                c.add(getRangeCondition());
                c.add(new OrConditions(new StdPassiveCondition(STANDARD_PASSIVES.DARKVISION),
                        new NotCondition(new VisibilityCondition(UNIT_TO_UNIT_VISION.CONCEALED))));
                c.add(getClearShotCondition(KEYS.MATCH.name())); //
                // c.add(new NotCondition(new VisibilityCondition(
                // UNIT_TO_PLAYER_VISION.UNKNOWN))); // ??? TODO PERHAPS MAKE
                // ALL SHOT LIKE THIS!
                break;
            case PRECISE_SHOT:
                c.add(new VisibilityCondition(UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT));
                break;
            case UNOBSTRUCTED_SHOT:
                c.add(new NotCondition(ConditionMaster.getSelfFilterCondition()));
                c.add(getRangeCondition());
                break;
            case RAY:
                c.add(ConditionMaster.getDistanceFilterCondition(Ref.KEYS.SOURCE.name(), 1));
                break;
            case MOVE:
                c.add(DC_ConditionMaster.getMoveFilterCondition());
                break;
            case BF_OBJ:
                c.add(ConditionMaster.getTYPECondition(OBJ_TYPES.BF_OBJ));
                break;
            case ANY_ALLY:
                c.add(ConditionMaster.getUnit_CharTypeCondition());
                c.add(ConditionMaster.getOwnershipFilterCondition(Ref.KEYS.SOURCE.name(), true));
                break;
            case ANY_ENEMY:
                c.add(ConditionMaster.getUnit_CharTypeCondition());
                c.add(ConditionMaster.getOwnershipFilterCondition(Ref.KEYS.SOURCE.name(), false));
                break;
            case ANY_UNIT:
                c.add(ConditionMaster.getUnit_CharTypeCondition());
                break;

            case ATTACK:
                c.add(new VisibilityCondition(UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT));

                List<FACING_SINGLE> list = new LinkedList<>();
                list.add(FACING_SINGLE.IN_FRONT);

                c.add(new OrConditions(new FacingCondition(FACING_SINGLE.IN_FRONT),

                        new Conditions(new FacingCondition(FACING_SINGLE.IN_FRONT, FACING_SINGLE.BEHIND),
                                new StringComparison(StringMaster.getValueRef(KEYS.SOURCE,
                                        G_PROPS.STANDARD_PASSIVES), STANDARD_PASSIVES.HIND_REACH + "",
                                        false)),

                        new Conditions(new FacingCondition(FACING_SINGLE.IN_FRONT,
                                FACING_SINGLE.TO_THE_SIDE), new StringComparison(StringMaster.getValueRef(
                                KEYS.SOURCE, G_PROPS.STANDARD_PASSIVES),
                                STANDARD_PASSIVES.BROAD_REACH + "", false))

                ));
                c.add(ConditionMaster.getAttackConditions());
                c.add(getClearShotCondition(KEYS.MATCH.name()));

                break;
            case GRAVE_CELL:
                c.add(new GraveCondition());
                break;
            case CLAIM: {

                c.add(new NotCondition(new StatusCheckCondition(STATUS.CHANNELED)));
                c.add(new NotCondition(new OwnershipCondition(KEYS.SOURCE, KEYS.MATCH)));
                c.add(ConditionMaster.getTYPECondition(OBJ_TYPES.BF_OBJ));
                c.add(ConditionMaster.getDistanceFilterCondition(Ref.KEYS.SOURCE.name(),
                        "{ACTIVE_RANGE}"));

                break;
            }
            default:
                break;

        }
        return c;
    }

    // c.add(new OrConditions(new FacingCondition(FACING_SINGLE.IN_FRONT),
    //
    // new OrConditions(new Conditions(new OrConditions(new FacingCondition(
    // FACING_SINGLE.IN_FRONT), new FacingCondition(FACING_SINGLE.TO_THE_SIDE)),
    // new FacingCondition(FACING_SINGLE.BEHIND)),
    //
    // new OrConditions(new
    // StringComparison(StringMaster.getValueRef(KEYS.SOURCE,
    // G_PROPS.STANDARD_PASSIVES), STANDARD_PASSIVES.HIND_REACH + "", false),
    // new StringComparison(StringMaster.getValueRef(KEYS.ACTIVE,
    // G_PROPS.STANDARD_PASSIVES), STANDARD_PASSIVES.HIND_REACH + "",
    // false))
    //
    // ),
    //
    // new OrConditions(new Conditions(new OrConditions(new FacingCondition(
    // FACING_SINGLE.IN_FRONT), new FacingCondition(FACING_SINGLE.TO_THE_SIDE)),
    //
    // new OrConditions(new
    // StringComparison(StringMaster.getValueRef(KEYS.SOURCE,
    // G_PROPS.STANDARD_PASSIVES), STANDARD_PASSIVES.BROAD_REACH + "", false),
    // new StringComparison(StringMaster.getValueRef(KEYS.ACTIVE,
    // G_PROPS.STANDARD_PASSIVES), STANDARD_PASSIVES.BROAD_REACH + "",
    // false))
    //
    // ))));
    public static Condition getRetainConditionsFromTemplate(RETAIN_CONDITIONS template) {
        switch (template) {
            case CASTER_ALIVE:
                new NotCondition(new StringComparison("{SOURCE_STATUS}", "" + STATUS.DEAD, false));
                break;
            case CASTER_CONSCIOUS:
                new MicroCondition() {
                    public boolean check() {
                        DC_HeroObj hero = (DC_HeroObj) ref.getObj(KEYS.SOURCE);
                        return !hero.checkUncontrollable();
                    }
                };
                break;
            case CASTER_FOCUS_REQ:
                break;
            case TARGET_MATCHES_TARGETING_FILTER:
                break;
            default:
                break;

        }
        return null;
    }

    public static NumericCondition getRangeCondition() {
        return ConditionMaster.getDistanceFilterCondition("SOURCE", "{ACTIVE_RANGE}");
    }

    public static Condition getAutoTargetingTemplateConditions(AUTO_TARGETING_TEMPLATES template) {
        switch (template) {
            case PARTY:
                return new RefCondition("source_party", "match_party");

            case ACTIONS:
                return new Conditions(ConditionMaster.getTYPECondition(OBJ_TYPES.ACTIONS),
                        new RefCondition(KEYS.SOURCE, KEYS.MATCH_SOURCE));

            case SPELLS:
                return new Conditions(ConditionMaster.getTYPECondition(OBJ_TYPES.SPELLS),
                        new RefCondition(KEYS.SOURCE, KEYS.MATCH_SOURCE));

            case WAVE:

                break;

            case ADJACENT:
                break;
            case ALL:
                return (ConditionMaster.getUnit_CharTypeCondition());
            case ALL_ALLIES:
                return ConditionMaster.getAllyCondition().join(
                        (ConditionMaster.getUnit_CharTypeCondition()));
            case ALL_ENEMIES:
                return (ConditionMaster.getEnemyCondition());
            case ENEMY_HERO:
                break;
            case SELF:
                return (ConditionMaster.getSelfFilterCondition());
            default:
                break;

        }
        return null;
    }

    public static boolean checkLiving(DC_HeroObj hero) {

        return checkCondition(ConditionMaster.getLivingCondition(KEYS.SOURCE.toString()), hero
                .getRef());
    }

    public static boolean checkCondition(Condition condition, Ref ref) {
        return condition.check(ref);
    }

    public static Condition getClearShotFilterCondition() {
        return getClearShotCondition("MATCH");
    }

    public static Condition getClearShotCondition(String obj_ref) {
        return new ClearShotCondition("SOURCE", obj_ref);
    }

    public Condition getDynamicCondition(String s) {
        List<String> list = StringMaster.openContainer(s);
        String name = list.get(0);
        String arg = null;
        if (list.size() > 1)
            arg = list.get(1);

        switch (name) {
            case "CostCondition":
                return new CostCondition(arg);
        }
        return null;
    }

    @Override
    public Condition getConditionFromTemplate(CONDITION_TEMPLATES template, String str1, String str2) {
        Condition result = null;
        {
            switch (template) {
                case ITEM: {
                    String slot = str1;
                    String prop = VariableManager.removeVarPart(str2);
                    String val = VariableManager.getVarPart(str2);
                    return new ItemCondition(KEYS.SOURCE.toString(), slot, prop, val);
                }

            }
        }
        if (result == null)
            result = super.getConditionFromTemplate(template, str1, str2);
        return result;
    }

    public enum STD_DC_CONDITIONS implements Condition {
        WAIT {
            public Condition getCondition() {
                return new WaitingFilterCondition();
            }

            @Override
            public String getTooltip() {
                return null;
            }

            @Override
            public Condition join(Condition condition) {
                return null;
            }

            @Override
            public boolean isTrue() {
                // TODO Auto-generated method stub
                return false;
            }
        },;

        public Condition getCondition() {
            return null;
        }

        public Ref getRef() {
            return getCondition().getRef();
        }

        public void setRef(Ref ref) {
            getCondition().setRef(ref);
        }

        public boolean check(Ref ref) {
            return getCondition().check(ref);
        }

        public boolean check() {
            return getCondition().check();
        }

        public boolean check(Entity match) {
            return getCondition().check(match);
        }

    }
}