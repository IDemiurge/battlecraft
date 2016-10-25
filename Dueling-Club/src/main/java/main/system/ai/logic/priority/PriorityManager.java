package main.system.ai.logic.priority;

import main.ability.effects.*;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.common.RaiseEffect;
import main.ability.effects.common.SummonEffect;
import main.ability.effects.containers.RollEffect;
import main.ability.effects.oneshot.common.ModifyCounterEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.ability.effects.oneshot.common.OwnershipChangeEffect;
import main.ability.effects.oneshot.special.InstantDeathEffect;
import main.ability.effects.special.BehaviorModeEffect;
import main.ability.effects.special.DrainEffect;
import main.content.CONTENT_CONSTS.*;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.*;
import main.content.enums.STD_MODES;
import main.content.parameters.G_PARAMS;
import main.content.parameters.PARAMETER;
import main.content.parameters.Param;
import main.content.properties.G_PROPS;
import main.data.ConcurrentMap;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.Targeting;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.BuffMaster;
import main.game.battlefield.Coordinates;
import main.game.battlefield.DamageMaster;
import main.rules.DC_ActionManager;
import main.rules.DC_ActionManager.STD_MODE_ACTIONS;
import main.rules.generic.UnitAnalyzer;
import main.rules.mechanics.AttackOfOpportunityRule;
import main.system.DC_Formulas;
import main.system.ai.PlayerAI.SITUATION;
import main.system.ai.UnitAI;
import main.system.ai.logic.actions.Action;
import main.system.ai.logic.actions.ActionSequence;
import main.system.ai.logic.actions.ActionSequenceConstructor;
import main.system.ai.logic.goal.Goal.GOAL_TYPE;
import main.system.ai.logic.target.EffectMaster;
import main.system.ai.logic.target.SpellAnalyzer;
import main.system.ai.logic.target.SpellMaster;
import main.system.ai.logic.target.TargetingMaster;
import main.system.ai.tools.Analyzer;
import main.system.ai.tools.ParamAnalyzer;
import main.system.ai.tools.future.FutureBuilder;
import main.system.auxiliary.*;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.math.*;
import main.system.math.roll.RollMaster;
import main.system.text.TextParser;

import java.util.*;

/**
 * 1) check forced 2) create action sequences for default goal 3) choose the
 * topmost
 * <p>
 * to choose an action sequence, first create all possible tasks if there are X
 * enemies, create X Engage tasks, for instance Then for each of these tasks,
 * create all possible action sequences. >> Engage via Attack = turn > turn >
 * move > attack >> Engage via Stone Gaze = turn > turn > stone gaze If all
 * action sequences have the same first action, fast-skip to executing it. So
 * that in the end, it's move>attack VS stone gaze
 *
 * @author JustMe
 */

public class PriorityManager {

    public static final int MAX_PRIORITY = 100000; // how to use a "limit"?
    public static final int DEFAULT_PRIORITY = 100;
    public static final int DEFAULT_ATTACK_PRIORITY = 100;
    public static final int WAIT_PRIORITY_FACTOR = 10;
    public static final String COST_PENALTY_FORMULA =
            // "100/lg({AMOUNT})";
            // "10+180/(2+sqrt({AMOUNT}*10))";
            // "100-(sqrt({AMOUNT}*10)-{AMOUNT}/15)+ ({AMOUNT}*{AMOUNT}/1000)";
            // //min
            // max!
            "100-sqrt({AMOUNT}*5)-{AMOUNT}/20 ";
    // "100/ {AMOUNT}*{AMOUNT}  x^2-bX = 100 " ;
    // "sqrt({AMOUNT}*10) -100/(100-{AMOUNT})"; TODO perhaps I should have a
    // separate formula for each cost param!

    public static final String ACTION_FORMULA = "1000/(({AMOUNT}+1)*10/2)";
    public static final int COUNTER_FACTOR = 4;
    public static final float CONVERGING_FACTOR = 0.5f;
    public static final Integer SUMMON_PRIORITY_MOD = 1000;
    private static float RETREAT_PRIORITY_FACTOR = 0.33f;
    private static DC_HeroObj unit;
    private static UnitAI unit_ai;

    private static int priority;
    private static BEHAVIOR_MODE behaviorMode;
    private static LOG_CHANNELS logChannel = LOG_CHANNELS.AI_DEBUG;
    private static Map<Effect, RollEffect> rollMap;

    public PriorityManager(DC_Game game) {
    }

    private static int getPriorityForActionSequence(ActionSequence as) {
        priority = 0;
        // compare damage or default priority on non-damage actions (formula?)
        //
        unit = as.getAi().getUnit();
        GOAL_TYPE goal = as.getType();
        initLogChannel(goal);
        Action action = as.getLastAction();
        try {
            action.getActive().getGame().getEffectManager().setEffectRefs(
                    action.getActive().getAbilities(), action.getRef());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!action.getActive().getProperty(PROPS.AI_PRIORITY_FORMULA).isEmpty()) {
            setBasePriority(evaluatePriorityFormula(action, action.getActive().getProperty(
                    PROPS.AI_PRIORITY_FORMULA)));
            if (EffectMaster.check(action.getActive(), RollEffect.class))
                try {
                    applyRollPriorityMod((RollEffect) EffectMaster.getEffectsOfClass(
                            action.getActive(), RollEffect.class).get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            applyResistPenalty(action);
        } else
            switch (goal) {

                case ATTACK:
                    setBasePriority(getAttackPriority(as));
                    // preview results?
                    break;
                case WAIT:
                    setBasePriority(getWaitPriority(action));
                    break;
                case DEFEND:
                case PREPARE:
                    setBasePriority(getModePriority(action));
                    break;
                case SUMMONING:
                    setBasePriority(getSummonPriority(action));
                    break;
                case AUTO_DAMAGE:
                case ZONE_DAMAGE:
                case AUTO_DEBUFF:
                case AUTO_BUFF:
                case SELF:
                case BUFF:
                case DEBUFF:
                case RESTORE:
                case DEBILITATE:
                case CUSTOM_HOSTILE:
                case CUSTOM_SUPPORT:
                    setBasePriority(getSpellPriority(goal, action));
                    break;
                case STEALTH:
                    setBasePriority(getStealthPriority(action));
                    break;
                case SEARCH:
                    // TODO sight range/detection factors,
                    setBasePriority(getSearchPriority(as));
                    break;
                case COWER:
                    setBasePriority(getCowerPriority(unit));
                    break;
                case RETREAT:
                    setBasePriority(getRetreatPriority(as));
                    break;
                case COATING:
                    setBasePriority(getCoatingPriority(action.getActive(), action.getTarget()));
                    break;
                case AGGRO:
                case AMBUSH:
                case APPROACH:
                case GUARD:
                case MOVE:
                case OTHER:
                case PATROL:
                case STALK:
                case WANDER:
                    priority = 100;
                    break;
                case ZONE_SPECIAL:

                default:
                    setBasePriority(getSpellPriority(goal, action));
                    break;
            }
        if (priority <= 0)
            return priority;
        Integer bonus = unit_ai.getActionPriorityBonuses().get(action.getActive().getName());
        if (bonus != null)
            priority += bonus;
        int factor = ParamPriorityAnalyzer
                .getAI_TypeFactor(goal, unit.getOwner().getAI().getType());
        addMultiplier(factor, "Global AI factor");
        SITUATION situation = unit.getOwner().getAI().getSituation();
        factor = ParamPriorityAnalyzer.getSituationFactor(goal, situation);
        addConstant(factor, situation.toString() + " situation const");
        addMultiplier(factor, situation.toString() + " situation factor");

        factor = ParamPriorityAnalyzer.getAI_TypeFactor(goal, unit_ai.getType());
        addMultiplier(factor, "AI type factor");

        Integer mod = unit_ai.getActionPriorityMods().get(action.getActive().getName());
        if (mod != null)
            priority = MathMaster.applyMod(priority, mod);

        // if (behaviorMode != BEHAVIOR_MODE.PANIC)
        applyCostPenalty(as);
        applySequenceLengthPenalty(as);
        main.system.auxiliary.LogMaster.log(1, "AI: " + priority + " priority for " + as);
        return priority;

    }

    private static int getCowerPriority(DC_HeroObj unit) {
        int p = Math.round(300 * getCowardiceFactor(unit));
        return p;
    }

    private static int getStealthPriority(Action action) {
        if (action.getActive() instanceof DC_UnitAction) {
            DC_UnitAction unitAction = (DC_UnitAction) action.getActive();
            if (unitAction.getModeEffect() != null) {
                if (unitAction.getModeEffect().getMode().equals(STD_MODES.STEALTH)) {
                    /*
					 * check detection inevitable?
					 */
                    return 300;
                    // should add to the max target priority...
                }

                if (unitAction.getModeEffect().getMode().equals(STD_MODES.HIDE)) {
                    return 200; // ++ wounds
                }
            }
        }
        return 0;
    }

    private static int evaluatePriorityFormula(Action action, String property) {

        Boolean less_or_more_for_health = null;
        for (PRIORITY_FUNCS p : PRIORITY_FUNCS.values()) {
            String text = StringMaster.wrapInBraces(p.name().toLowerCase());
            if (!property.contains(text))
                continue;
            float i = 1;
            switch (p) {
                case DURATION:
                    i = new Float(getDurationMultiplier(action)) / 100;
                    break;
                case CAPACITY:
                    i = getCapacity(action.getSource());
                    break;
                case DANGER:
                    i = getMeleeDangerFactor(unit, false, true) / 100;
                    break;
            }

            property = property.replace(text, "" + i);
        }
        if (property.contains("[less]")) {
            less_or_more_for_health = true;
            property = property.replace("[less]", "");
        }
        if (property.contains("[more]")) {
            less_or_more_for_health = false;
            property = property.replace("[more]", "");
        }
        if (less_or_more_for_health != null) {
            int unitPriority = getUnitPriority(action.getTarget(), less_or_more_for_health);
            property = property.replace("{target_power}", "" + unitPriority);
        }
        Integer p = new Formula(property).getInt(action.getRef());
        return p;
    }

    public static float getCapacity(DC_HeroObj unit) {
        float capacity = 1;
        float sta_factor = 1;
        if (!ParamAnalyzer.isStaminaIgnore(unit))
            sta_factor = new Float(unit.getIntParam(PARAMS.STAMINA_PERCENTAGE))
                    / MathMaster.PERCENTAGE;
        float foc_factor = 1;
        if (!ParamAnalyzer.isFocusIgnore(unit))
            foc_factor = new Float(unit.getIntParam(PARAMS.FOCUS_PERCENTAGE))
                    / MathMaster.PERCENTAGE;

        capacity = foc_factor * sta_factor;
        // if (UnitAnalyzer.checkIsCaster(unit))
        // capacity*=(1-new Float(unit.getIntParam(PARAMS.ESSENCE_PERCENTAGE))
        // / MathManager.PERCENTAGE)*UnitMaster.getSpellXpPercentage(unit)/100;
        // ++ count durability of items?
        return capacity;
    }

    private static int getSpellPriority(GOAL_TYPE type, Action action) {
        switch (type) {

            case ZONE_DAMAGE:
            case AUTO_DAMAGE:
                priority = getZoneSpellPriority(action, true);
                if (action.getSource().getAiType() == AI_TYPE.CASTER)
                    applyMultiplier(150, "AI_TYPE.CASTER");
                if (action.getSource().getAiType() == AI_TYPE.CASTER_OFFENSE)
                    applyMultiplier(200, "AI_TYPE.CASTER_OFFENSE");
                break;
            case SELF:
                priority = getSelfSpellPriority(action);
                break; // could be a lot ...
            // ++ rolls effect, ++ behavior modes,
            case BUFF:
            case DEBUFF:

                priority = getParamModSpellPriority(action, true);
                if (action.getSource().getAiType() == AI_TYPE.CASTER_SUPPORT)
                    applyMultiplier(150, "AI_TYPE.CASTER_SUPPORT");
                break;
            case RESTORE:
            case DEBILITATE:
                if (SpellAnalyzer.isCounterModSpell(action.getActive()))
                    priority = getCounterModSpellPriority(action);
                else
                    priority = getParamModSpellPriority(action, false);
                if (action.getSource().getAiType() == AI_TYPE.CASTER_SUPPORT)
                    applyMultiplier(150, "AI_TYPE.CASTER_SUPPORT");
                break;
            case CUSTOM_HOSTILE:
                priority = getSpellCustomHostilePriority(action);
                break;

        }
        return priority;
    }

    private static int getSelfSpellPriority(Action action) {
        setBasePriority(getUnitPriority(unit, false));
        for (Effect e : EffectMaster.getEffectsFromSpell(action.getActive())) {
            addEffectPriority(action, e);
        }
        return priority;
    }

    private static void addEffectPriority(Action action, Effect e) {
        if (e instanceof AddBuffEffect) {
            getParamModSpellPriority(action, true);
        }
        if (e instanceof ModifyValueEffect) {
            getParamModSpellPriority(action, false); // separate?
        }
        if (e instanceof ModifyCounterEffect) {
            getCounterModSpellPriority(action);
        }
    }

    private static int getSpellCustomHostilePriority(Action action) {
        Effects effects = EffectMaster.getEffectsFromSpell(action.getActive());
        // TODO targets???
        for (Effect e : effects) {
            addConstant(getSpellCustomHostileEffectPriority(action.getTarget(), action.getActive(),
                    e), e.toString());
        }
        applyResistPenalty(action);
        return priority;
    }

    private static void applyResistPenalty(Action action) {
        if (isResistApplied(action)) {
            int factor = ParamPriorityAnalyzer.getResistanceFactor(action);
            addMultiplier(factor, "Resistance factor");
        }
    }

    private static boolean isResistApplied(Action action) {
        if (action.getActive().getProperty(PROPS.RESISTANCE_TYPE).equalsIgnoreCase(
                RESISTANCE_TYPE.IRRESISTIBLE + "")) {
            return false;
        }
        return !action.getTarget().isOwnedBy(action.getSource().getOwner());
    }

    private static int getSpellCustomHostileEffectPriority(DC_Obj target, DC_ActiveObj action,
                                                           Effect e) {
        if (e instanceof AddBuffEffect) {
            AddBuffEffect buffEffect = (AddBuffEffect) e;
            // duration
            int mod = 100;
            // mod =
            // getDurationPriorityMod(buffEffect.getDurationFormula().getInt(action.getRef()));
            return getSpellCustomHostileEffectPriority(target, action, buffEffect.getEffect())
                    * mod / 100;
        }
        if (e instanceof Effects) {
            Effects effects = (Effects) e;
            int p = 0;
            for (Effect eff : effects) {
                p += getSpellCustomHostileEffectPriority(target, action, eff);
            }
            return p;
        }
        if (e instanceof RollEffect) {
            RollEffect rollEffect = (RollEffect) e;
            int mod = getRollPriorityMod(rollEffect);
            return getSpellCustomHostileEffectPriority(target, action, rollEffect.getEffect())
                    * mod / 100;
        }
        if (e instanceof InstantDeathEffect) {
            return 2 * getUnitPriority(target, true);
        }
        if (e instanceof BehaviorModeEffect) {
            int duration = new Formula(action.getParam(G_PARAMS.DURATION)).getInt(action.getRef());
            BehaviorModeEffect behaviorModeEffect = (BehaviorModeEffect) e;
            switch (behaviorModeEffect.getMode()) {
                case BERSERK:
                    return getUnitPriority(target, true) * (Math.min(4, duration / 5 * 3));
                case CONFUSED:
                    return getUnitPriority(target, true) * (Math.min(2, duration / 2));
                case PANIC:
                    return getUnitPriority(target, true) * (Math.min(3, duration / 3 * 2));
                default:
                    break;
            }

        }
        if (e instanceof OwnershipChangeEffect) {
            int duration = new Formula(action.getParam(G_PARAMS.DURATION)).getInt(action.getRef());
            return getUnitPriority(target, true) * (Math.min(5, duration));

        }
        return 0;
    }

    private static void initLogChannel(GOAL_TYPE goal) {
        switch (goal) {
            case ATTACK:
            case DEFEND:
            case MOVE:
            case PREPARE:
            case RESTORE:
            case WAIT:
                logChannel = LOG_CHANNELS.AI_DEBUG;
                break;
            default:
                logChannel = LOG_CHANNELS.AI_DEBUG2;
                break;
        }

    }

    public static int getSummonPriority(Action action) {
        // TODO precalculate summoned unit power, its initiative, its
        // positioning...
        ObjType summoned = SpellMaster.getSummonedUnit(action.getActive(), action.getRef());

        Integer unitXp = 0;
        if (summoned == null) {
            return 0;
        }
        try {
            SummonEffect summonEffect = (SummonEffect) EffectMaster.getEffectsOfClass(
                    action.getActive().getAbilities(), SummonEffect.class).get(0);
            unitXp = summonEffect.getSummonedUnitXp().getInt(action.getRef());

            if (summonEffect instanceof RaiseEffect) {
                RaiseEffect raiseEffect = (RaiseEffect) summonEffect;
                unitXp += raiseEffect.getCorpse().getIntParam(PARAMS.TOTAL_XP) / 5;
                // TODO for items!
                Obj weapon = raiseEffect.getCorpse().getRef().getObj(KEYS.WEAPON);
                if (weapon != null)
                    unitXp += weapon.getIntParam(PARAMS.GOLD_COST) / 5;
                Obj armor = raiseEffect.getCorpse().getRef().getObj(KEYS.ARMOR);
                if (armor != null)
                    unitXp += armor.getIntParam(PARAMS.GOLD_COST) / 5;
                Obj weapon2 = raiseEffect.getCorpse().getRef().getObj(KEYS.OFFHAND);
                if (weapon2 != null)
                    unitXp += weapon2.getIntParam(PARAMS.GOLD_COST) / 5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int power = summoned.getIntParam(PARAMS.POWER) + unitXp / DC_Formulas.POWER_XP_FACTOR;
        setBasePriority(power);

        if (summoned.getOBJ_TYPE_ENUM() == OBJ_TYPES.BF_OBJ) {

        }

        // getUnitPriority(targetObj)

        // try to get as close to enemies as possible...
        Coordinates coordinate = action.getRef().getTargetObj().getCoordinates();
        boolean prefer_melee = UnitAnalyzer.isMeleePreferred(summoned);
        if (prefer_melee) {
            prefer_melee = UnitAnalyzer.isOffensePreferred(summoned);
        }
        // TODO special for forcefield - add consts per enemy path blocked
        for (Entity entity : !prefer_melee ? Analyzer.getAllies(unit_ai) : Analyzer
                .getAdjacentEnemies(unit, false)) {
            // get distance?
            if (entity instanceof Obj) {
                Obj obj = (Obj) entity;
                int distance = Math.max(1, PositionMaster.getDistance(coordinate, obj
                        .getCoordinates()));
                // if (prefer_melee)
                addMultiplier(getUnitPriority(obj) / distance, obj.getName() + "'s proximity");
            }
        }
        // create unit quietly? precalc its best moves?
        applyMultiplier(SUMMON_PRIORITY_MOD, "SUMMON_PRIORITY_MOD");
        // priority = MathManager.applyMod(priority, SUMMON_PRIORITY_MOD);//
        // into a
        // AI_SUMMON_PRIORITY_MOD!
        return priority;
    }

    private static void setBasePriority(int i) {
        // main.system.auxiliary.LogMaster.log(getLogChannel(),
        // "Base Priority set: " + i);
        priority = i;

    }

    public static void applyRollPriorityMod(RollEffect rollEffect) {
        int perc = RollMaster.getRollChance(rollEffect.getRollType(), rollEffect.getSuccess(),
                rollEffect.getFail(), rollEffect.getRef());
        if (perc < 100)
            applyMultiplier(perc, rollEffect.getRollType() + " Roll");
    }

    public static int getRollPriorityMod(RollEffect rollEffect) {
        return RollMaster.getRollChance(rollEffect.getRollType(), rollEffect.getSuccess(),
                rollEffect.getFail(), rollEffect.getRef());
    }

    public static int getRetreatPriority(ActionSequence as) {
        float cowardice_factor = getCowardiceFactor(as.getAi().getUnit());
        if (cowardice_factor == 0) { // panic?
            priority = 0;
            return priority;
        }
        if (unit_ai.getBehaviorMode() == BEHAVIOR_MODE.PANIC)
            cowardice_factor *= 2;

        int currentMeleeDangerFactor = 0;
        try {
            currentMeleeDangerFactor = getMeleeDangerFactor(unit, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentMeleeDangerFactor == 0 && unit_ai.getBehaviorMode() != BEHAVIOR_MODE.PANIC) {
            priority = 0;
            return priority;
        }
        Coordinates c = unit.getCoordinates();
        unit.setCoordinates(as.getLastAction().getTarget().getCoordinates());
        int meleeDangerFactor = 0;
        try {
            meleeDangerFactor = getMeleeDangerFactor(unit, false, false);
            // meleeDangerFactor =getDangerFactorByMemory(unit); TODO for panic!

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            unit.setCoordinates(c);
        }
        priority = currentMeleeDangerFactor - meleeDangerFactor;
        if (priority <= 0)
            return 0;
        if (unit_ai.getBehaviorMode() == BEHAVIOR_MODE.PANIC) {
            addConstant(250, "Panic");
        }
        priority = MathMaster.round(priority * cowardice_factor * RETREAT_PRIORITY_FACTOR);

        // if (meleeDangerFactor != 0)
        // priority = priority / meleeDangerFactor;
        // else if (unit_ai.getBehaviorMode() != BEHAVIOR_MODE.PANIC)
        // priority = 0;
        // check melee threat if unit were on this cell?
        // check ranged threat? hide behind obstacles...
        return priority;
    }

    private static float getCowardiceFactor(DC_HeroObj coward) {
        // ARCHER/CASTER!
        int mod = 100;
        if (coward.getAiType() == AI_TYPE.ARCHER)
            mod = 200;
        if (coward.getAiType() == AI_TYPE.CASTER)
            mod = 150;
        // 10/spirit
        if (coward.getIntParam(PARAMS.C_MORALE) < 0)
            mod += -coward.getIntParam(PARAMS.C_MORALE);
        if (coward.checkAiMod(AI_MODIFIERS.COWARD))
            mod = mod * 3 / 2;

        int spirit = coward.getIntParam(PARAMS.SPIRIT);
        float factor = 10 / new Float((Math.sqrt(spirit) + new Float(spirit) / 3)) * mod / 100;

        return factor;
    }

    public static int getDangerFactorByMemory(DC_HeroObj unit2) {
        // for (DC_HeroObj enemy: Analyzer.getEnemies(unit2, false)){
        // memoryMaps.get(unit_ai).get(enemy);TODO
        // }
        return 0;
    }

    public static int getZoneSpellPriority(Action action, boolean damage) {
        int base_priority = 0;
        DC_ActiveObj active = action.getActive();
        Targeting targeting = active.getTargeting();

        if (targeting instanceof FixedTargeting || targeting instanceof SelectiveTargeting)
            targeting = TargetingMaster.getZoneEffect(active);
        // Set<Obj> objects = targeting.getFilter().getObjects(action.getRef());
        Ref REF = action.getRef().getCopy();
        targeting.select(REF);
        List<Obj> objects = (REF.getGroup() != null) ? REF.getGroup().getObjects()
                : new LinkedList<>(targeting.getFilter().getObjects(action.getRef()));
        for (Obj obj : objects) { // getZoneTargets(active)
            // TODO
            if (obj instanceof DC_HeroObj) {
                if (obj.isNeutral() || obj.isDead())
                    continue;
                DC_HeroObj target = (DC_HeroObj) obj;
                int p = (damage) ? getDamagePriority(active, target, false)
                        : getParamModSpellPriority(action);
                Boolean less_or_more_for_health = null;
                if (p == 200)
                    less_or_more_for_health = null;// ?
                p = getUnitPriority(target, less_or_more_for_health) * p / 100;

                boolean ally = target.isOwnedBy(unit.getOwner());
                if (ally) {
                    if (action.getSource().checkAiMod(AI_MODIFIERS.CRUEL))
                        p /= 2;
                    if (action.getSource().checkAiMod(AI_MODIFIERS.MERCIFUL))
                        p *= 2;
                    base_priority -= p;
                } else {
                    if (action.getSource().checkAiMod(AI_MODIFIERS.TRUE_BRUTE))
                        p *= 2;
                    base_priority += p;
                    base_priority += base_priority / 6;
                }
            }
        }
        return base_priority;
    }

    public static int getSearchPriority(ActionSequence as) {
        // TODO Auto-generated method stub
        return 25;
    }

    public static int getParamModSpellPriority(Action action) {
        return getParamModSpellPriority(action, null);
    }

    public static int getParamModSpellPriority(Action action, Boolean buff) {
        DC_ActiveObj spell = action.getActive();
        DC_Obj target = action.getTarget();
        if (buff == null)
            buff = EffectMaster.check(spell.getAbilities(), AddBuffEffect.class);
        if (buff)
            if (!spell.checkBool(STD_BOOLS.STACKING))
                try {
                    List<ObjType> buffsFromSpell = BuffMaster.getBuffsFromSpell(spell);
                    if (buffsFromSpell.isEmpty()) {
                        priority = 0;
                        return 0;
                    }
                    ObjType objType = buffsFromSpell.get(0);
                    if (!objType.checkBool(STD_BOOLS.STACKING))
                        if (target.hasBuff(objType.getName())) {
                            priority = 0;
                            return 0;
                        }
                } catch (Exception e) {

                }

        setBasePriority(getUnitPriority(target, false));

        boolean ally = target.getOwner().equals(unit.getOwner());
        // boolean mod = EffectMaster.check(spell.getAbilities(),
        // ModifyValueEffect.class);
        List<Effect> effects = EffectMaster.getEffectsOfClass(spell.getAbilities(),
                (buff) ? AddBuffEffect.class : ModifyValueEffect.class);
        if (buff) {
            List<Effect> list = new LinkedList<>();
            for (Effect e : effects)
                list.addAll(EffectMaster.getBuffEffects(e, ModifyValueEffect.class));
            effects = list; // TODO count the duration from buffEffect
        }
        initRollMap(spell, effects);
        boolean valid = false;
        for (Effect e : effects) {
            ModifyValueEffect valueEffect = (ModifyValueEffect) e;
            for (String sparam : StringMaster.openContainer(valueEffect.getParamString()))
                for (PARAMETER param : DC_ContentManager.getParams(sparam)) {
                    if (TextParser.checkHasValueRefs(valueEffect.getFormula().toString())) {
                        String parsed = TextParser.parse(valueEffect.getFormula().toString(),
                                action.getRef(), TextParser.ACTIVE_PARSING_CODE);
                        parsed = TextParser.replaceCodes(parsed);
                        valueEffect.setFormula(new Formula(parsed));

                    }
                    int amount = valueEffect.getFormula().getInt(action.getRef());

                    if (valueEffect.getMod_type() == MOD.MODIFY_BY_PERCENT) {
                        amount = MathMaster.getFractionValueCentimal(target.getIntParam(param,
                                valueEffect.getFormula().toString()
                                        .contains(StringMaster.BASE_CHAR)), amount);

                    }
                    boolean drain = (e instanceof DrainEffect);

                    int final_value = target.getIntParam(param) + amount;
                    int min_max = valueEffect.initMinMaxAmount(target, amount);
                    if (min_max != Integer.MAX_VALUE && min_max != Integer.MIN_VALUE)
                        if (amount >= 0) {

                            if (final_value > min_max)
                                amount = min_max - target.getIntParam(param);
                        } else {
                            if (amount < min_max)
                                amount = target.getIntParam(param) - min_max;
                        }
                    if (!ally && !drain) {
                        amount = -amount;
                    }
                    valid |= applyParamModPriority(target, e, param, amount);
                    if (drain) {
                        // TODO limit the amount!
                        applyParamModPriority(unit, null, param, amount);
                    }
                }

            if (!buff) {
                if (!ally && valid) {
                    applyResistPenalty(action);
                }
            } else {
                int duration = new Formula(spell.getParam(G_PARAMS.DURATION)).getInt(action
                        .getRef());
                applyMultiplier(getDurationMultiplier(action), duration + " duration");
            }
        }
        if (!valid)
            applyMultiplier(0, "Empty");
        return priority;
    }

    private static int getDurationMultiplier(Action action) {
        int multiplier = 0;
        int duration = new Formula(action.getActive().getParam(G_PARAMS.DURATION)).getInt(action
                .getRef());
        duration = MathMaster
                .addFactor(duration, ParamPriorityAnalyzer.getResistanceFactor(action));
        multiplier = (int) Math.sqrt(duration * 1000) + 50;
        return multiplier;
    }

    private static boolean applyParamModPriority(DC_Obj target, Effect e, PARAMETER param,
                                                 int amount) {
        boolean valid = false;
        RollEffect roll = rollMap.get(e);
        if (roll != null)
            roll.getRef().setTarget(target.getId());
        float numericPriority = ParamPriorityAnalyzer.getParamNumericPriority(param, target);
        int mod = 100;
        if (roll != null) {
            mod = getRollPriorityMod(roll);
        }
        int multiplier = Math.round(numericPriority * (amount) * mod / 100);
        if (multiplier != 0)
            valid = true;
        addMultiplier(multiplier, param.getShortName() + " const");

        amount = MathMaster.getCentimalPercentage(amount, target.getIntParam(param));
        int p = ParamPriorityAnalyzer.getParamPriority(param, // LIMIT?
                target);

        multiplier = MathMaster.getFractionValueCentimal(p, amount) * mod / 100;
        addMultiplier(multiplier, param.getShortName() + " mod");
        return valid;
    }

    private static void initRollMap(DC_ActiveObj spell, List<Effect> effects) {
        rollMap = new ConcurrentMap<>();

        List<RollEffect> rollEffects = EffectMaster.getRollEffects(spell);
        for (RollEffect roll : rollEffects) {
            for (Effect e : effects) {
                Effect effect = roll.getEffect();
                if (effect instanceof Effects) {
                    Effects rolledEffects = (Effects) effect;
                    for (Effect e1 : rolledEffects.getEffects()) {
                        rollMap.put(e1, roll);
                    }
                    break;
                }
                if (effect == e) {
                    rollMap.put(e, roll);
                    break;
                }

            }
        }
    }

    public static int getCounterModSpellPriority(Action action) {
        // check immunity? find counter rule by name and check....

        List<Effect> effects = EffectMaster.getEffectsOfClass(action.getActive().getAbilities(),
                ModifyCounterEffect.class);
        initRollMap(action.getActive(), effects);
        for (Effect e : effects) {
            if (e instanceof ModifyCounterEffect) {
                ModifyCounterEffect modifyCounterEffect = (ModifyCounterEffect) e;
                float mod = DC_CounterMaster.getCounterPriority(modifyCounterEffect
                        .getCounterName(), action.getTarget());
                if (rollMap.get(e) != null)
                    mod = mod * 100 / getRollPriorityMod(rollMap.get(e));

                int amount = modifyCounterEffect.getFormula().getInt(action.getRef());
                setBasePriority(getUnitPriority(action.getTarget(), true));
                addMultiplier(Math.round(mod * amount), modifyCounterEffect.getCounterName()
                        + StringMaster.wrapInParenthesis(amount + ""));
            }
        }
        applyResistPenalty(action);

        return priority;
    }

    public static int getAttackPriority(ActionSequence as) {
        Action action = as.getLastAction();
        if (action.getTarget() instanceof DC_Cell)
            return 0;
        DC_HeroObj targetObj = (DC_HeroObj) action.getTarget();
        DC_ActiveObj active = action.getActive();
        return getAttackPriority(active, targetObj);

    }

    public static int getCoatingPriority(DC_ActiveObj active, DC_Obj targetObj) {
        List<Effect> effects = EffectMaster.getEffectsOfClass(active, ModifyCounterEffect.class);
        if (effects.isEmpty())
            return priority;
        ModifyCounterEffect e = (ModifyCounterEffect) effects.get(0);
        setBasePriority(getUnitPriority(unit, true));
        addConstant(getItemPriority(targetObj), targetObj.getName());
        Integer amount = e.getFormula().getInt(active.getRef());
        int mod = amount;
        mod *= DC_CounterMaster.getCounterPriority(e.getCounterName(), targetObj);
        addMultiplier(mod, e.getCounterName() + " coating "
                + StringMaster.wrapInParenthesis(amount + ""));
        if (targetObj.getCounter(e.getCounterName()) > 0) {
            addMultiplier(-75 * targetObj.getCounter(e.getCounterName()) / amount, e
                    .getCounterName()
                    + " - Already Coated "
                    + StringMaster.wrapInParenthesis(targetObj.getCounter(e.getCounterName()) + ""));
        }
        // TODO if (quick) if (ammo) if (alreadyCoated)
        return priority;
    }

    private static int getItemPriority(DC_Obj targetObj) {
        return targetObj.getIntParam(PARAMS.GOLD) / 5;
    }

    public static int getAttackPriority(DC_ActiveObj active, DC_HeroObj targetObj) {

        if (unit.getBehaviorMode() != BEHAVIOR_MODE.BERSERK
                && unit.getBehaviorMode() != BEHAVIOR_MODE.CONFUSED)
            if (targetObj.isOwnedBy(unit.getOwner()))
                return -10000;
        boolean attack = !(active instanceof DC_SpellObj);

        int damage_priority = getDamagePriority(active, targetObj, attack);
        if (active.isThrow())
            if (damage_priority < getUnitPriority(targetObj) * 2)
                return -1;// TODO
        // getActionNumberFactor

        int counter_penalty = 0;
        if (active.getActionGroup() != ACTION_TYPE_GROUPS.ATTACK) {
            // if (AttackOfOpportunityRule.checkAction(active)) {
            // counter_penalty = getAttackOfOpportunityPenalty(active,
            // targetObj);
            // }TODO

        } else if (!unit.checkAiMod(AI_MODIFIERS.TRUE_BRUTE))
            if (targetObj.canCounter() && !unit.checkPassive(STANDARD_PASSIVES.NO_RETALIATION)
                    && !active.checkProperty(G_PROPS.STANDARD_PASSIVES))
                if (damage_priority != getLethalDamagePriority()
                        || targetObj.checkPassive(STANDARD_PASSIVES.FIRST_STRIKE)) {
                    counter_penalty = getCounterPenalty(targetObj);
                }
        // TODO ++ counter penalty

        int enemy_priority = getUnitPriority(targetObj);
        priority = enemy_priority;
        if (!unit.checkAiMod(AI_MODIFIERS.TRUE_BRUTE))
            priority += DEFAULT_ATTACK_PRIORITY;
        if (active.getActionGroup() != ACTION_TYPE_GROUPS.ATTACK) {
            if (active.isRanged()) {
                if (unit.getAiType() == AI_TYPE.ARCHER)
                    damage_priority *= 2;
            } else if (unit.getAiType() == AI_TYPE.BRUTE)
                damage_priority *= 2;
        } else if (active.getActionGroup() != ACTION_TYPE_GROUPS.SPELL)
            if (unit.getAiType() == AI_TYPE.CASTER)
                damage_priority *= 2;

        applyMultiplier(damage_priority, "damage");
        // applyCostPenalty(as);
        addMultiplier(counter_penalty, "counter");

        // TODO spec effects!!!

        if (active.isThrow()) {
            // DC_WeaponObj item = null;
            // boolean offhand = active.isOffhand();
            // boolean quick = active instanceof DC_ItemActiveObj;
            // if (quick) {
            // DC_ItemActiveObj itemActiveObj = (DC_ItemActiveObj) active;
            // item = itemActiveObj.getItem().getWrappedWeapon();
            // } else {
            // item = unit.getWeapon(offhand);
            // }
            // if (item == null) {
            // return priority;
            // }
            // int factor = (int) -Math.sqrt(item.getIntParam(PARAMS.GOLD_COST))
            // * 50;
            // if (!quick)
            // factor *= 2;
            // if (!offhand)
            // factor *= 2;
            // addConstant(factor, "item thrown");
            // if (priority > 0)
            // addMultiplier(factor / 5, "item thrown");
        }

        int p = getSpecialEffectsPriority(SPECIAL_EFFECTS_CASE.ON_ATTACK, active.getOwnerObj(),
                targetObj);

        if (targetObj.isNeutral())
            addMultiplier(-90, "Neutral");
        return priority;
    }

    private static int getSpecialEffectsPriority(SPECIAL_EFFECTS_CASE CASE, DC_HeroObj source,
                                                 DC_HeroObj target) {
        int p = 0;
        if (source.getSpecialEffects() != null)
            if (source.getSpecialEffects().get(CASE) != null) {
                Effect e = source.getSpecialEffects().get(CASE);
                if (e instanceof Effects) {
                    Effects effects = (Effects) e;
                    for (Effect ef : effects) {
                        p += getSpecialEffectPriority(e);
                    }
                }
            }
        return p;
    }

    private static int getSpecialEffectPriority(Effect e) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static int getAttackOfOpportunityPenalty(DC_ActiveObj action, DC_HeroObj targetObj) {
        int penalty = 0;
        List<DC_ActiveObj> list = AttackOfOpportunityRule.getAttacks(action);

        for (DC_ActiveObj a : list) {
            penalty -= getDamagePriority(a, unit) / 2;
        }

        return penalty;
    }

    private static void applySequenceLengthPenalty(ActionSequence as) {
        int length = as.getActions().size() - 1;
        int penalty = (int) -Math.round(length * Math.sqrt(length) * 3);
        if (penalty < -95)
            penalty = -95;
        String string = "length";
        addMultiplier(penalty, string);
    }

    private static void applyCostPenalty(ActionSequence as) {
        Costs cost = ActionSequenceConstructor.getTotalCost(as.getActions());
        int cost_penalty = getCostFactor(cost, unit);
        String string = "cost";
        try {
            if (as.getLastAction().getActive().isChanneling()) {
                cost_penalty += cost_penalty
                        * cost.getCost(PARAMS.C_N_OF_ACTIONS).getPayment().getAmountFormula()
                        .getInt(as.getLastAction().getRef()) / 5;
                string = "channeling cost";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        applyMultiplier(cost_penalty, string);

    }

    public static int getCounterPenalty(DC_HeroObj targetObj) {
        // TODO free?
        return -getDamagePriority(targetObj.getAction(DC_ActionManager.COUNTER_ATTACK), unit)
                / COUNTER_FACTOR;
    }

    public static int getDamagePriority(DC_ActiveObj action, Obj targetObj) {
        return getDamagePriority(action, targetObj, true);
    }

    public static int getDamagePriority(DC_ActiveObj action, Obj targetObj, boolean attack) {
        int damage = 0;
        List<Effect> effects = EffectMaster.getEffectsOfClass(action, (attack) ? AttackEffect.class
                : DealDamageEffect.class);
        initRollMap(action, effects);
        for (Effect e : effects) {
            try {
                int mod = 100;
                RollEffect roll = rollMap.get(e);
                if (roll != null) {
                    roll.getRef().setTarget(targetObj.getId());

                    mod = getRollPriorityMod(roll);
                }
                damage += FutureBuilder.getDamage(action, targetObj, e) * mod / 100;

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        if (DamageMaster.isLethal(damage, targetObj))
            if (checkKillPrioritized(targetObj, action))

                return getLethalDamagePriority();

        int e = targetObj.getIntParam(PARAMS.C_ENDURANCE) - damage;
        int t = targetObj.getIntParam(PARAMS.C_TOUGHNESS) - damage;
        e = MathMaster.getCentimalPercentage(damage, targetObj.getIntParam(PARAMS.C_ENDURANCE));
        t = MathMaster.getCentimalPercentage(damage, targetObj.getIntParam(PARAMS.C_TOUGHNESS));

        return Math.max(e, t * 2 / 3);
    }

    public static boolean checkKillPrioritized(Obj targetObj, DC_ActiveObj action) {
        if (targetObj.isNeutral())
            return false;
        // check if action deals exceeding damage?
        // if (PositionMaster.getDistance(targetObj, unit) > 1) {
        // if (Analyzer.getEnemies((DC_HeroObj) targetObj, false).size() > 0) {
        // // melee threat instead?
        //
        // return false;
        // }
        // }// check if wounded
        return true;
    }

    public static int getLethalDamagePriority() {
        return DEFAULT_PRIORITY * 2;
    }

    public static int getUnitPriority(Obj targetObj) {
        return getUnitPriority(targetObj, null); // TODO TODO !!!
    }

    public static int getUnitPriority(Obj targetObj, Boolean less_or_more_for_health) {
        return getUnitPriority(unit_ai, targetObj, less_or_more_for_health);
    }

    public static int getUnitPriority(UnitAI unit_ai, Obj targetObj, Boolean less_or_more_for_health) {
        DC_HeroObj target_unit = (DC_HeroObj) targetObj;
        if (targetObj instanceof DC_HeroAttachedObj) {
            DC_HeroAttachedObj attachedObj = (DC_HeroAttachedObj) targetObj;
            targetObj = attachedObj.getOwnerObj();
        }
        Integer basePriority = targetObj.getIntParam(PARAMS.POWER);
        if (unit_ai.getBehaviorMode() == BEHAVIOR_MODE.BERSERK)
            basePriority = 100;
        else if (targetObj.getOBJ_TYPE_ENUM() == OBJ_TYPES.BF_OBJ) {
            if (!Analyzer.isBlockingMovement(unit_ai.getUnit(), (DC_HeroObj) targetObj))
                return 0;
            basePriority = 20;
        }
        Integer healthMod = 100;
        if (less_or_more_for_health != null) {
            healthMod = getHealthFactor(targetObj, less_or_more_for_health);
        } else {
            if (target_unit.isUnconscious())
                return basePriority / 4; // [QUICK FIX] - more subtle?
        }
        basePriority = basePriority * healthMod / 100;
        return basePriority;// TODO
        // limit?
    }

    public static int getHealthFactor(Obj targetObj, Boolean less_or_more_for_health) {
        Integer healthMod = 100;
        int end = targetObj.getIntParam(PARAMS.ENDURANCE_PERCENTAGE) / MathMaster.MULTIPLIER;
        int tou = targetObj.getIntParam(PARAMS.TOUGHNESS_PERCENTAGE) / MathMaster.MULTIPLIER;
        int mod = Math.min(tou, end);
        if (mod == 0)
            return (less_or_more_for_health ? 0 : 100);
        if (less_or_more_for_health)
            healthMod = healthMod * (150) / (100 + mod);
        else
            healthMod = healthMod * 100 / (150 - mod);
        return healthMod;
    }

    public static int getModePriority(Action a) {
        STD_MODE_ACTIONS action = new EnumMaster<STD_MODE_ACTIONS>().retrieveEnumConst(
                STD_MODE_ACTIONS.class, a.getActive().getName());
        if (action == null)
            return 0; // custom modes? TODO
        switch (action) {
            case Concentrate:
                return getModePriority(unit, STD_MODES.CONCENTRATION);
            case Defend:
                return getDefendPriority(unit_ai);
            case On_Alert:
                return getAlertPriority(unit);
            case Meditate:
                return getModePriority(unit, STD_MODES.MEDITATION);
            case Rest:
                return getModePriority(unit, STD_MODES.RESTING);
        }
        return priority;
    }

    public static int getAlertPriority(DC_HeroObj unit) {
        return getModePriority(unit, STD_MODES.ALERT);
    }

    public static int getWaitPriority(Action action) {
        DC_HeroObj target = (DC_HeroObj) action.getTarget();
        boolean ally = (target.getOwner().equals(unit.getOwner()));
        if (ally) {
            if (unit_ai.getType() == AI_TYPE.CASTER || unit_ai.getType() == AI_TYPE.ARCHER) {
                if (!Analyzer.isBlocking(unit, target))
                    return 0;
            }
            if (!Analyzer.isBlockingMovement(unit, target)) // if I were sorting
                // continuously even
                // as I calculate, I
                // could reference
                // current top
                // priority action
                // to see what to
                // wait for
                return 0;
        } else {
            if (getMeleeDangerFactor(unit) != 0)
                return 0;
            if (!Analyzer.checkRangedThreat(target))
                return 0;
            if (unit.checkInSightForUnit(target))
                return 0;

        }

        int ap = action.getSource().getIntParam(PARAMS.C_N_OF_ACTIONS) - 1;
        // differentiate between waiting on enemy and ally?
        int base_priority = WAIT_PRIORITY_FACTOR * ap;

        int factor = (ally) ? 100 : 50;

        priority = MathMaster.getFractionValueCentimal(base_priority, factor);

        if (!ally) {
            addMultiplier(100 - ParamPriorityAnalyzer.getUnitLifeFactor(unit), "Unit life factor");

            addMultiplier(getMeleeThreat(target), "Enemy melee threat");

        } else
            addMultiplier(100 - ParamPriorityAnalyzer.getUnitLifeFactor(target), "ally life factor");
        // check if ally is blocking move/view
        // relevance factor -> "need to take your place" OR
        // "waiting for you to come closer"

        return priority;
    }

    public static int getDefendPriority(UnitAI unit_ai) {
        // subtract current ap

        int base_priority = unit_ai.getType() == AI_TYPE.TANK ? 75 : 25;
        return 5 + base_priority * getMeleeDangerFactor(unit) / 100;
    }

    public static int getModePriority(DC_HeroObj unit, STD_MODES mode) {
        PARAMETER p = ContentManager.getPARAM(mode.getParameter());
        Integer percentage = unit.getIntParam(ContentManager.getPercentageParam(new Param(
                ContentManager.getBaseParameterFromCurrent(p))))
                / MathMaster.MULTIPLIER;
        int factor = getParamPriority(p, unit); // how important/good it is for
        // this unit
        int modifier = 0; // how important/good is it now
        switch (mode) {
            case ALERT:
                // if (!Analyzer.getAdjacentEnemies(unit, true, true).isEmpty())
                return 0;
            // situation "ranged threat"
            // for (u u : Analyzer.getUnits(ai, ally, enemy,
            // vision_no_vision, dead))
            // if (distance)

            // percentage = Math TODO
            // .min(getParamPriority(PARAMS.C_STAMINA, unit),
            // getParamPriority(PARAMS.C_N_OF_ACTIONS, unit));
            // factor = (unit_ai.getType() == AI_TYPE.TANK) ? 100 : 50;
            // modifier = getMeleeDangerFactor(unit);
            // break;
            case CONCENTRATION: // param priority should vary depending on unit
                // type...
                modifier = -getMeleeDangerFactor(unit);
                // if (modifier==0)
                // if (unit.getOwner().getAI().getSituation()
                // ==SITUATION.ENGAGED)
                // modifier = getRangedTotalThreat(); TODO

                break;
            case MEDITATION:
                factor = getCastingPriority(unit);
                modifier = -getMeleeDangerFactor(unit);

                break;
            case RESTING:

                modifier = -getMeleeDangerFactor(unit);
                // check ignore TODO
                break;
        }
        priority = factor;
        applyMultiplier(100 - percentage, "param percentage missing");
        // ++ Life factor?
        addMultiplier(modifier, "situational");

        return priority;
        // modifier * (DEFAULT_PRIORITY - percentage) / factor;
    }

    public static int getMeleeDangerFactor(DC_HeroObj unit) {
        return getMeleeDangerFactor(unit, true, true);
    }

    public static int getMeleeDangerFactor(DC_HeroObj unit, boolean adjacentOnly, boolean now) {
        List<? extends Entity> units = (!adjacentOnly) ? Analyzer.getAdjacentEnemies(unit, false)
                : Analyzer.getMeleeEnemies(unit);
        int factor = 0;
        for (Entity e : units) {
            DC_HeroObj enemy = (DC_HeroObj) e;
            int meleeThreat = getMeleeThreat(enemy, now);
            factor += meleeThreat;
            LogMaster.log(logChannel, "Melee threat " + meleeThreat + " from " + enemy.getName());
        }

        int mod = 125 - ParamPriorityAnalyzer.getUnitLifeFactor(unit);
        LogMaster.log(logChannel, "Melee threat mod " + mod + " for " + unit.getName());

        if (mod != 0)
            factor = factor * mod / 100;
        LogMaster.log(logChannel, "Melee threat factor " + factor + " for " + unit.getName());

        return factor;
    }

    public static int getMeleeThreat(DC_HeroObj enemy) {
        return getMeleeThreat(enemy, true);
    }

    public static int getMeleeThreat(DC_HeroObj enemy, boolean now) {
        if (now)
            if (!enemy.canActNow() || !enemy.canAttack())
                return 0;
        int threat = 0;
        int factor = 1;
        DC_UnitAction attack = enemy.getAction(DC_ActionManager.ATTACK);
        if (attack == null)
            return 0;
        if (now) {
            attack.initCosts();
            try {
                int ap_factor = enemy.getIntParam(PARAMS.C_N_OF_ACTIONS)
                        / attack.getCosts().getCost(PARAMS.C_N_OF_ACTIONS).getPayment()
                        .getAmountFormula().getInt(enemy.getRef());
                int sta_factor = enemy.getIntParam(PARAMS.C_STAMINA)
                        / attack.getCosts().getCost(PARAMS.C_STAMINA).getPayment()
                        .getAmountFormula().getInt(enemy.getRef());
                int foc_factor = enemy.getIntParam(PARAMS.C_FOCUS)
                        / attack.getCosts().getCost(PARAMS.C_FOCUS).getPayment().getAmountFormula()
                        .getInt(enemy.getRef());
                factor = Math.min(sta_factor, ap_factor);
                factor = Math.min(factor, foc_factor);// extract to
                // getTimesActivate()
                // TODO
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        threat = FutureBuilder.precalculateDamage(attack, unit, true) * factor;

        // special attacks? dual wielding?

        int distance = 1 + PositionMaster.getDistance(unit, enemy);
        threat /= distance;

        return threat;
    }

    public static int getCostFactor(Costs cost, DC_HeroObj unit) {
        // if (!cost.canBePaid(unit.getRef()))
        // return -100;
        int penalty = 0;
        for (Cost c : cost.getCosts()) {
            PARAMETER p = c.getPayment().getParamToPay();
            int base_value = getParamPriority(p, unit); // return a *formula*
            // perhaps?
            if (base_value <= 0)
                continue;
            int perc = DC_MathManager.getCentimalPercentage(c.getPayment().getAmountFormula()
                    .getInt(unit.getRef()), unit.getIntParam(p));
            if (perc > 100) {
                if (p != PARAMS.C_N_OF_ACTIONS) {
                    return 0;
                }
            }
            if (perc <= 0)
                continue;

            // speaking of real numbers, stamina/foc should have a non-linear
            // formula I reckon
            //

            int amount = MathMaster.getFractionValueCentimal(base_value, perc);
            penalty += (amount);
        }
        return MathMaster.calculateFormula(COST_PENALTY_FORMULA, penalty);
    }

    public static int getParamPriority(PARAMETER p, DC_HeroObj unit) {
        // int percentage = DC_MathManager.getParamPercentage(unit, p);
        // if (percentage == 0) {
        // return -100;
        // }
        int base_priority = 0;
        if (p == PARAMS.C_STAMINA) {
            if (ParamAnalyzer.isStaminaIgnore(unit))
                return 0;
            return 125; // actions
        }
        if (p == PARAMS.C_N_OF_ACTIONS)
            return 150;
        if (p == PARAMS.C_ESSENCE)
            // return getCastingPriority(unit);
            return 50;
        if (p == PARAMS.C_FOCUS) {
            if (ParamAnalyzer.isFocusIgnore(unit))
                return 0;
            if (!UnitAnalyzer.hasFocusBlockedActions(unit))
                return 50;
            else
                return 100;
        }
        // check actions
        if (p == PARAMS.C_ENDURANCE)
            return 125;
        // check toughness
        return (base_priority);
        // * 100 - percentage / MathManager.MULTIPLIER) / 100;
    }

    public static int getCastingPriority(DC_HeroObj unit) {
        if (!Analyzer.hasSpells(unit))
            return 0;
        if (unit_ai.getType() == AI_TYPE.CASTER)
            return 200;
        // per spell? TODO

        if (unit_ai.getType() == AI_TYPE.BRUTE)
            return 25;
        if (unit_ai.getType() == AI_TYPE.ARCHER)
            return 35;
        if (unit_ai.getType() == AI_TYPE.TANK)
            return 50;
        return 100;
    }

    public static void applyMultiplier(int factor, String string) {
        if (factor == 0) {
            priority = 0;

        } else {
            priority = priority * (factor) / 100;
            if (priority < 0)
                priority = 0;
        }
        if (unit_ai.getLogLevel() >= UnitAI.LOG_LEVEL_FULL)
            main.system.auxiliary.LogMaster.log(getLogChannel(), " Applying [" + string
                    + "] multiplier: " + factor + "; priority = " + priority);

    }

    public static void addConstant(int constant, String string) {
        priority += (constant);
        if (unit_ai.getLogLevel() >= UnitAI.LOG_LEVEL_FULL)
            main.system.auxiliary.LogMaster.log(getLogChannel(), " Adding [" + string
                    + "] constant: " + constant + "; priority = " + priority);
    }

    private static LOG_CHANNELS getLogChannel() {
        return logChannel;
    }

    public static void addMultiplier(int factor, String string) {
        if (factor == 0)
            return;
        if (priority < 0 && factor < 0)
            return;
        priority += priority * (factor) / 100;
        if (priority < 0)
            priority = 0;
        if (unit_ai.getLogLevel() >= UnitAI.LOG_LEVEL_FULL)
            main.system.auxiliary.LogMaster.log(getLogChannel(), " Adding [" + string
                    + "] multiplier: " + factor + "; priority = " + priority);
    }

    public static ActionSequence chooseByPriority(List<ActionSequence> actions) {
        setPriorities(actions);
        // if (behaviorMode == null)
        applyConvergingPathsPriorities(actions);
        return getByPriority(actions);
    }

    public static ActionSequence getByPriority(List<ActionSequence> actions) {
        Chronos.mark("Priority sorting ");
        // Collections.sort(actions, getComparator());
        int max_priority = Integer.MIN_VALUE;
        ActionSequence sequence = null;
        for (ActionSequence a : actions) {
            int p = a.getPriority();
            if (p > max_priority) {
                max_priority = p;
                sequence = a;
            }
        }
        Chronos.logTimeElapsedForMark("Priority sorting ");
        main.system.auxiliary.LogMaster.log(1, unit + " has chosen" + "" + sequence
                + " with priorioty of " + priority);
        return sequence;
    }

    public static void setPriorities(List<ActionSequence> actions) {
        Chronos.mark("Priority calc");
        for (ActionSequence action : actions) { // into separate method to
            // debug!
            if (action == null)
                continue;
            unit = action.getAi().getUnit();
            unit_ai = action.getAi();

            // Chronos.mark("Calculating priority for " + action);
            int priority = -1;
            try {
                priority = getPriority(action);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Chronos.logTimeElapsedForMark("Calculating priority for " +
            // action);
            action.setPriority(priority);
        }
        Chronos.logTimeElapsedForMark("Priority calc");
    }

    public static int getPriority(ActionSequence sequence) {
        behaviorMode = unit_ai.getBehaviorMode();
        if (behaviorMode == BEHAVIOR_MODE.BERSERK) {
            return getPriorityForActionSequence(sequence);
        } else if (behaviorMode == BEHAVIOR_MODE.CONFUSED) {
            return RandomWizard.getRandomInt(100);
        }
        return getPriorityForActionSequence(sequence);
    }

    public static void applyConvergingPathsPriorities(List<ActionSequence> actions) {
        // TODO make sure that they are not pruned at path-building phase!
        // split into groups and for each group, add bonus per group member!
        Map<Action, List<ActionSequence>> asGroups = new HashMap<>();
        for (ActionSequence as : actions) {
            Action firstAction = as.get(0);
            List<ActionSequence> group = asGroups.get(firstAction);
            if (group == null) {
                group = new LinkedList<>();
                asGroups.put(firstAction, group);
            }
            group.add(as);
        }
        for (Action firstAction : asGroups.keySet()) {
            for (ActionSequence as : asGroups.get(firstAction)) {
                for (ActionSequence as2 : asGroups.get(firstAction)) {
                    if (as2 == as)
                        continue;
                    if (as2.getLastAction().getTarget() != null)
                        if (as2.getLastAction().getTarget().equals(as.getLastAction().getTarget()))
                            continue;
                    int bonus = MathMaster.round(as2.getPriority()
                            / asGroups.get(firstAction).size() * (CONVERGING_FACTOR));
                    main.system.auxiliary.LogMaster.log(getLogChannel(), bonus
                            + " Converging Paths bonus added to " + as.getPriority()
                            + as.getActions() + " from " + as2.getPriority() + as2.getActions());
                    as.setPriority(as.getPriority() + bonus);
                }
            }
        }
    }

    public static Comparator<? super ActionSequence> getComparator() {
        return new Comparator<ActionSequence>() {
            @Override
            public int compare(ActionSequence o1, ActionSequence o2) {
                int a1 = o1.getPriority();
                int a2 = o2.getPriority();
                if (a1 == a2) {
                    // return (RandomWizard.random()) ? 1 : -1;
                    return 0;
                }
                if (a1 > a2)
                    return -1;
                return 1;
            }
        };
    }

    public static DC_HeroObj getUnit() {
        return unit;
    }

    public static void setUnit(DC_HeroObj unit) {
        PriorityManager.unit = unit;
    }

    public static UnitAI getUnit_ai() {
        return unit_ai;
    }

    public static void setUnit_ai(UnitAI unit_ai) {
        PriorityManager.unit_ai = unit_ai;
        unit = unit_ai.getUnit();
    }

    public int getPriorityForEffect(AI_EFFECT_PRIORITIZING aep, Effect e, Action a) {
        DC_Obj target = a.getTarget();
        switch (aep) {
            case COUNTER_MOD:
                break;
            case ATTACK:
                break;

            case DAMAGE:

                // return getdParamModSpellPriority(a);
            case BUFF:
                return getParamModSpellPriority(a, true);
            case PARAM_MOD:
                return getParamModSpellPriority(a);
            case SUMMON:
                return getSummonPriority(a);
            case MODE:
                break;
            case BEHAVIOR_MODE:
                break;

        }
        return 0;
    }

    public int getActionNumberFactor(int n) {
        return MathMaster.calculateFormula(ACTION_FORMULA, n);
    }

    public enum AI_EFFECT_PRIORITIZING {
        ATTACK, DAMAGE, BUFF, PARAM_MOD, COUNTER_MOD, SUMMON, MODE, BEHAVIOR_MODE,
    }

    public enum PRIORITY_FUNCS {
        DURATION, DANGER, CAPACITY,
    }

}