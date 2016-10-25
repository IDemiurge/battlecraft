package main.ability.effects.group;

import main.ability.effects.AddBuffEffect;
import main.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.CONTENT_CONSTS.ACTION_TYPE;
import main.content.ContentManager;
import main.content.OBJ_TYPES;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.data.ConcurrentMap;
import main.data.ability.OmittedConstructor;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.ObjTypeComparison;
import main.elements.conditions.RefCondition;
import main.elements.targeting.AutoTargeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_UnitAction;
import main.entity.obj.Obj;
import main.system.ConditionMaster;
import main.system.FilterMaster;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.Formula;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class HeroObjectModifyingEffect extends DC_Effect {

    protected String modString;
    protected String conditionString;
    protected String objName;
    protected OBJ_TYPES type;
    protected MOD code = MOD.MODIFY_BY_CONST;
    protected boolean buff = true;
    protected String buffName = "@";
    protected Conditions conditions;
    protected Map<PARAMETER, String> map;
    protected Map<Obj, Effect> effects = new ConcurrentMap<>();
    protected boolean prop;
    protected Map<PROPERTY, String> propMap;
    protected boolean applied;

    protected Condition specialConditions;

    @OmittedConstructor
    public HeroObjectModifyingEffect(OBJ_TYPES type, String conditionString, String modString,
                                     Boolean buff, Boolean prop, Condition c) {
        this.type = type;
        this.conditionString = conditionString;
        this.modString = modString;
        this.buff = true; // so far so good
        this.prop = prop;
        specialConditions = c;
        formula = new Formula("100");
    }

    @OmittedConstructor
    public HeroObjectModifyingEffect(OBJ_TYPES type, String conditionString, String modString,
                                     Boolean buff, Boolean prop) {
        this(type, conditionString, modString, buff, prop, null);
    }

    @OmittedConstructor
    public HeroObjectModifyingEffect(OBJ_TYPES type, String conditionString, String modString) {
        this(type, conditionString, modString, true, false);
    }

    @OmittedConstructor
    public HeroObjectModifyingEffect(String objName, String modString) {
        this(null, "", modString, true, false);
        this.objName = objName;
    }

    @Override
    public int getLayer() {
        if (buff) // ???
            return Effect.ZERO_LAYER;
        return super.getLayer();
    }

    /**
     * Modstring format: valueName(value);valueName2(value2);... Use [mod],
     * [set], [remove] to change the default ADD function of modstring
     */
    public boolean applyThis() {
        if (ref.getTargetObj().isDead() || ref.getSourceObj().isDead()) {
            removeEffects();
            return false;
        }
        if (game.isSimulation())
            if (!checkApplyInSimulation())
                return true;
        // what if the group has changed? perhaps there should be a map...
        if (prop) {
            if (propMap == null)
                propMap = new RandomWizard<PROPERTY>().constructStringWeightMap(modString,
                        PROPERTY.class);
            else
                main.system.auxiliary.LogMaster.log(1, "prop map " + propMap.toString());
        } else if (map == null) // TODO support PROPERTY?
            map = new RandomWizard<PARAMETER>()
                    .constructStringWeightMap(modString, PARAMETER.class);
        else
            main.system.auxiliary.LogMaster.log(0, "map " + map.toString());
        List<? extends Obj> list = getObjectsToModify();
        main.system.auxiliary.LogMaster.log(0, "list " + list.toString());
        main.system.auxiliary.LogMaster.log(0, "effects " + effects.toString());
        for (Obj obj : list) {
            if (obj == null)
                continue;
            if (obj.isDead())
                continue; // TODO clean up for owner is dead!
            Effect effect = effects.get(obj);
            if (effect != null) {
                // if (isPermanent() && isApplied())
                // continue;
                effect.applyThis();
                applied = true;
                continue;
            }
            Ref REF = ref.getCopy();
            REF.setTarget(obj.getId());

            // map = new MapMaster<PARAMETER, String>().constructVarMap(
            // modString, PARAMETER.class);
            Effects modEffects = new Effects();
            if (map != null)
                EffectMaster.initParamModEffects(modEffects, map, ref);
            else if (propMap != null)
                EffectMaster.initPropModEffects(modEffects, propMap, ref);

            applied = true;

            for (Effect e : modEffects.getEffects()) {
                e.resetOriginalFormula();
                e.modifyFormula(getFormula().toString());
            }

            if (buff) {
                AddBuffEffect buffEffect = new AddBuffEffect(buffName, modEffects);
                // TODO LAYER?
                buffEffect.setForcedLayer(getModEffectLayer());
                modEffects.setForcedLayer(getModEffectLayer());

                if (isPermanent())
                    buffEffect.setDuration(ContentManager.INFINITE_VALUE);
                if (!game.isSimulation())
                    effects.put(obj, buffEffect);
                buffEffect.apply(REF);
            } else {
                if (!game.isSimulation())
                    effects.put(obj, modEffects);
                modEffects.apply(REF);

            }
        }
        return true;
    }

    protected boolean checkApplyInSimulation() {
        if (type != null)
            switch (type) {
                case ACTIONS:
                case ITEMS:
                case SPELLS:
                    // return false;
                case ARMOR:
                case WEAPONS:
                    return true;
            }
        return false;
    }

    protected int getModEffectLayer() {
        if (type == OBJ_TYPES.ITEMS)
            return Effect.BASE_LAYER;
        return Effect.SECOND_LAYER;
    }

    protected void removeEffects() {
        for (Effect e : effects.values()) {
            if (buff) {
                ((AddBuffEffect) e).getBuff().kill();
            } else {
                // TODO
            }
        }

    }

    protected boolean isPermanent() {
        return true;
    }

    protected List<Obj> getObjectsToModify() {
        if (objName != null) {
            return getObjectsByName(objName);
        } else {
            Ref REF = ref.getCopy(); // TODO simple condition format - prop,
            // value"
            initFilterConditions();
            DC_HeroObj hero = (DC_HeroObj) ref.getSourceObj();
            if (type != null) {
                List<Integer> list = null;
                try {
                    list = getIdList(hero);
                } catch (Exception e) {
                    main.system.auxiliary.LogMaster.log(1,
                            "Group obj effect failed to get targets: " + this);
                    return new LinkedList<>();
                }

                List<Obj> objList = new LinkedList<>();
                for (Integer id : list) {
                    objList.add(game.getObjectById(id));
                }
                return objList;
            }

            new AutoTargeting(conditions).select(REF);
            return REF.getGroup().getObjects();
        }

    }

    protected void initFilterConditions() {
        if (conditions == null) {
            conditions = new Conditions(getDefaultConditions());
            conditions.add(getSpecialConditions());
            conditions.add(ConditionMaster.toConditions(conditionString));
        }
    }

    protected List<Integer> getIdList(DC_HeroObj hero) {
        List<Integer> list = new LinkedList<>();
        if (hero == null)
            return list;
        switch (type) {
            case ACTIONS:
                for (ACTION_TYPE a : ACTION_TYPE.values()) {
                    if (hero.getActionMap().get(a) != null)
                        for (DC_UnitAction action : hero.getActionMap().get(a))
                            list.add(action.getId());
                }
                break;
            case ARMOR:
                list = new LinkedList<>(new ListMaster<Integer>().getList(hero.getArmor().getId()));
                break;
            case ITEMS:
                for (Obj i : hero.getQuickItems())
                    // if (i.getOBJ_TYPE_ENUM() != OBJ_TYPES.WEAPONS)
                    // non-weapon QI???
                    list.add(i.getId());
                break;
            case SPELLS:
                for (Obj i : hero.getSpells())
                    list.add(i.getId());
                break; // hero.getGame().getManager().getSpells(hero)
            case WEAPONS:
                Integer id = null;
                if (hero.getActiveWeapon(true) != null)
                    id = hero.getActiveWeapon(true).getId();
                Integer id2 = 0;
                if (hero.getActiveWeapon(false) != null)
                    id2 = hero.getActiveWeapon(false).getId();
                list = new LinkedList<>(new ListMaster<Integer>().getList(id2, id));
                List<? extends Obj> l = new LinkedList<>(hero.getQuickItems());

                for (Obj i : l)
                    if (i.getOBJ_TYPE_ENUM() == OBJ_TYPES.WEAPONS)
                        list.add(i.getId());
                break;
        }
        // ListMaster.removeNullElements(list);
        FilterMaster.filter(list, conditions, game);
        return list;
    }

    protected Condition getDefaultConditions() {
        if (type == null)
            type = getTYPE();

        if (type == null)
            return new Conditions(new RefCondition(KEYS.MATCH_SOURCE, KEYS.SOURCE));

        return new Conditions(new ObjTypeComparison(type), new RefCondition(KEYS.MATCH_SOURCE,
                KEYS.SOURCE));
    }

    protected OBJ_TYPES getTYPE() {
        return type;
    }

    protected Condition getSpecialConditions() {
        return specialConditions;
    }

    protected abstract List<Obj> getObjectsByName(String objName);

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }
}