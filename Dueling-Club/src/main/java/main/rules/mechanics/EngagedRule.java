package main.rules.mechanics;

import main.content.CONTENT_CONSTS.ACTION_TAGS;
import main.content.CONTENT_CONSTS.ACTION_TYPE_GROUPS;
import main.content.CONTENT_CONSTS.SPELL_TAGS;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.game.battlefield.attack.Attack;
import main.rules.action.ActionRule;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.ai.AI_Manager;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;

import java.util.LinkedList;
import java.util.List;

public class EngagedRule implements ActionRule {
    public static final int ATTACK_MOD = 25;
    public static final int DEFENSE_MOD = 25;
    /*
     * defense penalty against other units
     * cannot move or target other units without breaking engagement
     * make AoO against E-target for auto-breaking engagement (special Disengage can be used)
    choose an enemy to engage...
         * auto-engagement too 	 */
    private DC_Game game;

    public EngagedRule(DC_Game game) {
        this.game = game;
    }

    public static boolean isDisengagement(Attack attack) {
//		attacker.isInvisible();
//		PositionMaster.getDistance(attack.getAttacked(), attack.getAttacker());
//		if (distance>0)
//		// TODO close , visible, ...
//		return false;

        return true;
    }

    public static void applyMods(DC_HeroObj heroObj) {
        // TODO add status (buff?) and

        heroObj.modifyParameter(PARAMS.DEFENSE_MOD, -(DEFENSE_MOD - DEFENSE_MOD
                * heroObj.getIntParam(PARAMS.ENGAGEMENT_DEFENSE_REDUCTION_MOD) / 100));
        heroObj.addStatus(STATUS.ENGAGED.toString());
        // bonus against ET!

    }

    public boolean unitMoved(DC_HeroObj obj, int x, int y) {
        return true;
        // List<DC_HeroObj> units = obj.getGame().getObjectsOnCoordinate(new
        // Coordinates(x, y));
        // List<DC_HeroObj> list = new LinkedList<>();
        // for (DC_HeroObj unit : units) {
        // if (unit.isBfObj())
        // continue;
        // if (unit.isMine())
        // continue;
        // if (unit.getActiveVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT)
        // continue;
        // if (unit.isUnconscious())
        // continue;
        // if (StealthRule.checkInvisible(unit))
        // continue;
        //
        // // remove if no visibility
        // list.add(unit);
        // }
        // if (list.size() == 0)
        // return true;
        // // if (units.size() == 1) { //TODO forced?
        // // engage(obj, units.get(0));
        // // return true;
        // // }
        // DC_HeroObj engaged = chooseEngagedUnit(obj, list);
        // if (engaged != null)
        // engage(obj, engaged);
        // return true;
    }

    private DC_HeroObj chooseEngagedUnit(DC_HeroObj obj, List<DC_HeroObj> units) {
        // if (isAutoEngageOff(obj))
        if (obj.isAiControlled()) {
            return AI_Manager.chooseEnemyToEngage(obj, units);
        }
        return (DC_HeroObj) DialogMaster.objChoice("Choose an enemy to engage...", units
                .toArray(new Obj[units.size()]));

    }

    public boolean checkCanDisengage(DC_HeroObj disengager) {
        return disengager.getIntParam(PARAMS.C_N_OF_ACTIONS) >= 1 + getEngagers(disengager).size();
        // getDisengageCost(disengager).canBePaid();
        // new Costs(map) //check provoke

        // TODO ++ visuals - some little sword pointing...
        // NESW on cells? for directional blocking
    }

    public List<DC_HeroObj> getEngagers(DC_HeroObj engaged) {
        List<DC_HeroObj> list = new LinkedList<>();
        for (DC_HeroObj u : engaged.getGame().getUnits()) {
            if (u.getEngagementTarget() == engaged) {
                list.add(u);
            }
        }
        return list;
    }

    public boolean checkDisengagingActionCancelled(DC_ActiveObj action) {
        if (action.getRef().getTargetObj() == action.getOwnerObj().getEngagementTarget())
            return false;

        DC_HeroObj unit = action.getOwnerObj();
        boolean prompt = !unit.isAiControlled()
                && action.getActionGroup() != ACTION_TYPE_GROUPS.MODE
                && action.getActionGroup() != ACTION_TYPE_GROUPS.ATTACK;
        if (prompt)
            if (action.getRef().getTargetObj() == null || action.getRef().getTargetObj() == unit) {
                // self-targeting or misc instants shouldn't bother
                if (action.checkProperty(G_PROPS.SPELL_TAGS, SPELL_TAGS.INSTANT.toString()))
                    prompt = false;
                else if (action.checkProperty(G_PROPS.ACTION_TAGS, ACTION_TAGS.INSTANT.toString()))
                    prompt = false;

                // auto-disengage if moved away! (blink)

                // spells TODO
            }

        if (!prompt)
            return false;
        return !promptDisengage(action);
    }

    public Boolean promptDisengage(DC_ActiveObj action) {
        if (action.getOwnerObj().isAiControlled()) {
            disengaged(action);
            return true;
        }
        DC_HeroObj disengager = action.getOwnerObj();
        List<DC_HeroObj> engagers = getEngagers(disengager);
        // remove if not aoo
        String units = StringMaster.getStringFromEntityList(engagers).replace(";", ", ");
        units = StringMaster.replaceLast(units, ", ", "");
        String disengageString = "";
        if (disengager.getEngagementTarget() != null)
            disengageString = "disengage you from " + disengager.getEngagementTarget().getName()
                    + " and ";
        else if (units.isEmpty())
            return true;

        String string = "Activating " + action.getName() + " will " + disengageString
                + "prompt an Attack of Opportunity from " + units + "... ";
        String TRUE = "Proceed";
        String FALSE = "Cancel";
        String NULL = "Disengage";
        // optional prompting!
        Boolean result = DialogMaster.askAndWait(string, TRUE, FALSE, NULL);
        if (result == null) {
            disengaged(action);
            result = true;
        } else if (result)
            disengaged(action);
        return result;

    }

    @Override
    public void actionComplete(ActiveObj activeObj) {
        updateEngagementTargets();

    }

    @Override
    public boolean unitBecomesActive(DC_HeroObj unit) {
        return true;
    }

    public void updateEngagementTargets() {
        for (DC_HeroObj unit : game.getUnits()) {
            DC_HeroObj engagementTarget = unit.getEngagementTarget();
            boolean disengage = false;
            if (engagementTarget != null) {
                if (!engagementTarget.isDead() || engagementTarget.isUnconscious()
                        || engagementTarget.isImmobilized())
                    disengage = true;
                if (!disengage)
                    if (PositionMaster.getDistance(unit, engagementTarget) >= 1)
                        disengage = true;
            }
            if (disengage) {
                unit.setEngagementTarget(null);
                for (DC_HeroObj unit1 : getEngagers(unit)) {
                    unit1.setEngagementTarget(null);
                }
            }

        }
    }

    public void disengaged(DC_ActiveObj action) {
        // disengager.getGame().fireEvent(new
        // Event(STANDARD_EVENT_TYPE.DISENGAGED, disengager.getRef()));
        DC_HeroObj disengaged = action.getOwnerObj();
        for (DC_HeroObj unit : action.getGame().getUnits()) {
            if (unit.getEngagementTarget() == disengaged) {
                checkAoO(action, unit);
                if (PositionMaster.getDistance(unit, disengaged) >= 1)
                    unit.setEngagementTarget(null);
            }
        }
        disengaged.setEngagementTarget(null);
    }

    public void engage(DC_HeroObj engager, DC_HeroObj engaged) {
        // when moved, instead of Collision perhaps... though in some cases
        // getting pushed out is OK
        engager.setEngagementTarget(engaged);
        SoundMaster.playEffectSound(SOUNDS.THREAT, engager);
        if (RandomWizard.random())
            SoundMaster.playEffectSound(SOUNDS.THREAT, engaged);
        if (!isAutoEngageOff(engaged))
            if (engaged.getEngagementTarget() == null) // TODO check dead!
            {
                engaged.setEngagementTarget(engager);
            }

    }

    private boolean isAutoEngageOff(DC_HeroObj engaged) {
        // TODO std bool? std passive? experienced"
        return false;
    }

    public boolean isEngaged(DC_HeroObj unit) {
        return getEngagers(unit) != null;
    }

    public void checkAoO(DC_ActiveObj action, DC_HeroObj unit) {
        try {
            AttackOfOpportunityRule.triggerAttack(unit, action.getOwnerObj(), true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        // TODO mod!

    }

}