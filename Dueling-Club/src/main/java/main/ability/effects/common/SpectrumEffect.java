package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.PARAMS;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.Formula;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;

public class SpectrumEffect extends DC_Effect {
    protected static final String X = "x";
    protected String paramString;
    protected Effects effects;
    protected boolean applyThrough = true;
    protected boolean circular;
    protected boolean vision;
    protected Condition filterConditions;
    protected String reductionForDistanceModifier;
    protected BattleFieldObject bfObj;
    protected FACING_DIRECTION previousFacing;
    String rangeFormula;
    KEYS source = KEYS.SOURCE;
    int defaultSidePenalty = 1;
    // String reductionForDistance = "(x)/distance+sqrt(x)";
    String reductionForDistance = "-(x)/10*(2+distance*sqrt(distance))"; // *sqrt(x)

    public SpectrumEffect(Effect effects, String rangeFormula, Boolean circular) {
        if (effects != null)
            this.effects = new Effects(effects);
        this.rangeFormula = rangeFormula;
        this.circular = circular;
    }

    public SpectrumEffect(Effect effects) {
        this(effects, "", false);
        this.vision = true;
    }

    public SpectrumEffect(Effect effects, String rangeFormula) {
        this(effects, rangeFormula, false);
    }

    public SpectrumEffect(String paramString, String rangeFormula) {
        this.paramString = paramString;
        this.rangeFormula = rangeFormula;
    }

    public SpectrumEffect(Condition filterConditions, Effect effect) {
        this(effect);
        this.filterConditions = filterConditions;

    }

    @Override
    public String toString() {
        return ref.getSourceObj() + "'s Spectrum effect with " + effects;
    }

    public boolean applyThis() {
        Integer range = new Formula(rangeFormula).getInt(ref);
        Integer backwardRange = 0;
        Integer sidePenalty = 0;
        if (circular) {
            backwardRange = range;
        }
        if (vision) {
            range = new Formula(StringMaster.getValueRef(KEYS.SOURCE, PARAMS.SIGHT_RANGE))
             .getInt(ref);
            backwardRange = null; // TODO
            sidePenalty = null;
        }

        if (ref.getObj(source) instanceof BattleFieldObject)
            bfObj = ((BattleFieldObject) ref.getObj(source));
        else {
            //TODO
        }
        if (bfObj instanceof Unit)
            bfObj = bfObj;
        FACING_DIRECTION facing = bfObj.getFacing();
//        Boolean clockwise = null;
//        if (previousFacing != null)
//            clockwise = facing.getDirection().getDegrees() < previousFacing.getDirection().getDegrees();
//        if (clockwise != null)
//        {
//            facing = FacingMaster.rotate(facing, clockwise);
//        }
        List<Coordinates> coordinates = new LinkedList<>(getGame().getVisionMaster().getSightMaster()
         .getSpectrumCoordinates(
          range, sidePenalty, backwardRange, bfObj,
          vision, facing));

//        previousFacing = facing;

        main.system.auxiliary.log.LogMaster.log(1, this + " applied on " + coordinates);
        // boolean x-ray ++ tall/short/etc
        if (effects == null) {
            initEffects();
        }

        for (Coordinates c : coordinates) {
            // TODO WHAT IF IT'S ON A DIFFERENT Z-LEVEL?
            // applyThrough = true; // ?
            // if (!applyThrough)
            // if (!(getGame().getObjectByCoordinate(c, true) instanceof
            // DC_Cell))
            // continue;
            DequeImpl<? extends Obj> objects = new DequeImpl<>(getGame().getObjectsOnCoordinate(
             getGame().getDungeon().getZ(), c, null, true, applyThrough));

            if (applyThrough) {
                objects.addCast(getGame().getCellByCoordinate(c));
            }

            for (Obj o : objects) {
                ref.setMatch(o.getId());
                if (filterConditions != null) {
                    if (!filterConditions.preCheck(ref)) {
                        continue;
                    }
                }
                Integer target = o.getId();
                // for (Effect effect : effects.getEffectsStage()) {
                // Ref REF = Ref.getCopy(ref);
                // REF.setTarget(target);
                // effect.apply(REF);
                // }
                // target = getGame().getCellByCoordinate(c).getId();
                if (getGame().getObjectById(target) == null) {
                    continue;
                }
                for (Effect effect : effects.getEffects()) {
                    Ref REF = Ref.getCopy(ref);
                    REF.setTarget(target);
                    if (reductionForDistance != null) {
                        effect.resetOriginalFormula(); // for the first time
                        // to set original
                        effect.resetOriginalFormula();
                        String reduction = reductionForDistance;
                        if (reductionForDistanceModifier != null)
                            reduction += (reductionForDistanceModifier);

                        Formula effectFormula = effect.getFormula();
                        reduction = reduction.replace(X, effectFormula.toString());
                        int distance = PositionMaster.getDistance(REF.getSourceObj(), REF
                         .getTargetObj());
                        reduction = reduction.replace("distance", distance + "");
                        effectFormula.append(reduction);
                        //TODO
                        Integer amount = effectFormula.getInt(ref);
                        if (amount < 0) {
                            effect.setAmount(amount);
                        }
                        effect.setAmount(amount);
                    }
                    effect.apply(REF);
                }
            }
        }
        return true;
    }

    // public Object getAppliedEffects() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    protected void initEffects() {
        ref.setID(KEYS.INFO, ref.getId(KEYS.ACTIVE));
        effects = EffectFinder.initParamModEffects(paramString, ref);
    }

    public String getRangeFormula() {
        return rangeFormula;
    }

    public void setRangeFormula(String rangeFormula) {
        this.rangeFormula = rangeFormula;
    }

    public Effects getEffects() {
        return effects;
    }

    public void setEffects(Effects effects) {
        this.effects = effects;
    }

    public boolean isApplyThrough() {
        return applyThrough;
    }

    public void setApplyThrough(boolean applyThrough) {
        this.applyThrough = applyThrough;
    }

    public boolean isCircular() {
        return circular;
    }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public String getReductionForDistanceModifier() {
        return reductionForDistance;
    }

    public void setReductionForDistanceModifier(String reductionForDistance) {
        this.reductionForDistanceModifier = reductionForDistance;
    }

}
