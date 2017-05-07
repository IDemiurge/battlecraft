package main.entity.tools.active;

import main.ability.DC_CostsFactory;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.values.properties.G_PROPS;
import main.elements.costs.Cost;
import main.elements.costs.CostImpl;
import main.elements.costs.Costs;
import main.elements.costs.Payment;
import main.entity.active.DC_ActiveObj;
import main.entity.tools.EntityInitializer;
import main.entity.tools.EntityMaster;
import main.entity.active.DC_ActionManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveInitializer extends EntityInitializer<DC_ActiveObj> {


    public ActiveInitializer(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public ActiveMaster getMaster() {
        return (ActiveMaster) super.getMaster();
    }

    public void construct() {
        if (!getEntity().isConstructed()) {
            try {
                super.construct();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getMaster().getHandler().getTargeter().initTargetingMode();
    }

    public void initCosts() {
        initCosts(false);
    }

    public void initCosts(boolean anim) {
        Costs costs = null;
        if (getEntity().isFree()) {
            costs = new Costs(new LinkedList<>());
        } else {
            try {
                costs = DC_CostsFactory.getCostsForSpell(getEntity(),
                 getChecker().isSpell());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Cost cp_cost = costs.getCost(PARAMS.C_N_OF_COUNTERS);
            Cost ap_cost = costs.getCost(PARAMS.C_N_OF_ACTIONS);
            boolean noCounterCost = cp_cost == null;
            if (!noCounterCost) {
                noCounterCost = cp_cost.getPayment().getAmountFormula().toString().isEmpty()
                 || cp_cost.getPayment().getAmountFormula().toString().equals("0");
            }
            if (noCounterCost) { // if not specifically set...
                if (getHandler().isExtraAttackMode()) {
                    cp_cost = new CostImpl(new Payment(PARAMS.C_N_OF_COUNTERS, ap_cost.getPayment()
                     .getAmountFormula()));
                    cp_cost.getPayment().getAmountFormula().applyModifier(
                     getEntity().getOwnerObj().getIntParam(PARAMS.EXTRA_ATTACKS_POINT_COST_MOD));
                    cp_cost.setCostParam(PARAMS.CP_COST);

                }
            } else {
                if (!(getHandler().isExtraAttackMode())) {
                    costs.getCosts().remove(cp_cost);
                }
            }
            if (getHandler().isAttackOfOpportunityMode()) { // TODO only if watched? better
                // here perhaps!
                cp_cost.addAltCost(ap_cost);
            }
            costs.removeCost(getHandler().isExtraAttackMode() ? PARAMS.C_N_OF_ACTIONS : PARAMS.C_N_OF_COUNTERS);
        }
        if (anim) {
//          getAnimator().  addCostAnim();

        }
        costs.setActive(getEntity());
        if (!getHandler().isInstantMode() || getHandler().isCounterMode()) {
            getHandler().getActivator().setCanActivate(costs.canBePaid(getRef()));
        }

        costs.setActiveId(getId());
        getEntity().setCosts(costs);
    }






    public ACTION_TYPE_GROUPS initActionTypeGroup() {
        if (checkProperty(G_PROPS.ACTION_TYPE_GROUP)) {
            return new EnumMaster<ACTION_TYPE_GROUPS>()
             .retrieveEnumConst(ACTION_TYPE_GROUPS.class, getProperty(G_PROPS.ACTION_TYPE_GROUP));
        }
        if (getChecker().isStandardAttack()) {
            return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
        }
        if (StringMaster.isEmpty(getProperty(G_PROPS.ACTION_TYPE))) {
            return ActionEnums.ACTION_TYPE_GROUPS.SPELL;
        }
        ACTION_TYPE type =
         getEntity().getActionType();
//         new EnumMaster<ACTION_TYPE>().retrieveEnumConst(ACTION_TYPE.class,
//         getProperty(G_PROPS.ACTION_TYPE));
        if (type == null) {
            return ActionEnums.ACTION_TYPE_GROUPS.SPELL;
        }
        switch (type) {
            case HIDDEN:
                return ActionEnums.ACTION_TYPE_GROUPS.HIDDEN;
            case MODE:
                return ActionEnums.ACTION_TYPE_GROUPS.MODE;
            case SPECIAL_ACTION:
                return ActionEnums.ACTION_TYPE_GROUPS.SPECIAL;
            case SPECIAL_ATTACK:
                return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
            case ADDITIONAL_MOVE:
                return ActionEnums.ACTION_TYPE_GROUPS.MOVE;
            case SPECIAL_MOVE:
                return ActionEnums.ACTION_TYPE_GROUPS.MOVE;
            case STANDARD:
                return DC_ActionManager.getStdActionType(getEntity());

        }
        return ActionEnums.ACTION_TYPE_GROUPS.SPELL;
    }

    @Override
    protected void initDefaults() {

    }

    @Override
    public void init() {
        super.init();
        addDynamicValues();
    }

    public void addDynamicValues() {
        Integer cooldown = getIntParam(PARAMS.COOLDOWN);
        if (cooldown < 0) {
            getEntity().setParam(PARAMS.C_COOLDOWN, cooldown);
        } else {
            getEntity().setParam(PARAMS.C_COOLDOWN, 0);
        }
        // TODO adjust costs based on hero's skills

    }

    @Override
    public Executor getHandler() {
        return (Executor) super.getHandler();
    }

    @Override
    public ActiveChecker getChecker() {
        return (ActiveChecker) super.getChecker();
    }

}
