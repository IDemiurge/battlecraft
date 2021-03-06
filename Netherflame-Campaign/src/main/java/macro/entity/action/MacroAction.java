package macro.entity.action;

import eidolons.game.core.Eidolons;
import macro.entity.MacroObj;
import macro.entity.MacroRef;
import macro.entity.action.MacroActionManager.MACRO_MODES;
import macro.entity.action.MacroActionManager.MACRO_PARTY_ACTIONS;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.Abilities;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.sound.AudioEnums;

public class MacroAction extends MacroObj implements ActiveObj {

    /*
     * What do I really need from DC_Active? Would it not be better to write
     * things from scratch?
     *
     * What *do* I need here? 1) click - activate - MAM or Effects 2) render in
     * panel 3) for special spells and tricks and AV-based macro actions like
     * town portal, hasty travel, elemental weather, global spells...
     */
    private MACRO_MODES mode;
    private MACRO_PARTY_ACTIONS mpa;

    public MacroAction(ObjType type, MacroRef ref) {
        super(ref.getGame(), type, ref, ref.getGame().getPlayerParty()
         .getOwner());
    }

    public MacroAction(ObjType type, MacroRef ref, MACRO_PARTY_ACTIONS mpa) {
        this(type, ref);
        this.mpa = mpa;
        setName(mpa.toString());
        setImage(mpa.getImagePath());
    }

    public MacroAction(ObjType type, MacroRef ref, MACRO_MODES mode) {
        this(type, ref);
        this.mode = mode;
        setName(mode.toString());
        setImage(mode.getImagePath());
    }

    public boolean activate(boolean transmit) {
        return false;
    }

    @Override
    public boolean activatedOn(Ref ref) {
        // set hero/party
        setRef(ref);
        return activateThis((MacroRef) ref);
    }

    public boolean activateThis(MacroRef ref) {
        if (mode != null) { // ref from where, exactly? ActiveSelected obj,
            // perhaps, by clicking Members?
//            Unit hero = MacroManager.getSelectedPartyMember();
//            hero.setMacroMode(mode);
            return true;
        }
        if (mpa != null) {
            // getOrCreate active party - consider that for real Dynamics, the world
            // should be filled with real parties that take real actions!

            MapActionHandler.partyAction(mpa, ref.getParty());
        }
        getGame().getLoop().actionInputManual(null);//new ActionInput(this, new Context(ref)));
        // costs?
        return false;
    }

    @Override
    public void invokeRightClicked() {

    }

    public void invokeClicked() {
        new Thread(new Runnable() {
            public void run() {
                clicked();
            }
        }).start();
    }

    @Override
    public void clicked() {
        if (!MacroActionManager.isActionsBlocked() && canBeActivated()) {
            MacroActionManager.setActionsBlocked(true);
            DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLICK_ACTIVATE);
            try {
                activate();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                MacroActionManager.setActionsBlocked(false);
            }
        } else {
            if (MacroActionManager.isActionsBlocked()) {
//                getGame().getManager().cancelSelection();
            }
            playCancelSound();
        }
    }

    @Override
    public boolean activate() {
//        MacroRef ref = new MacroRef(MacroManager.getSelectedPartyMember());
//        ref.setParty(MacroManager.getActiveParty());
        return activatedOn(ref);
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        if (MacroActionManager.isActionsBlocked()) // while activating
        // perhaps...
        {
            return false;
        }
        if (mode != null) {
            // preCheck mode already on?
        }
        if (mpa != null) {
            // preCheck order cancelable
        }
        return true;
    }

    @Override
    public void setRef(Ref REF) {
        REF.setID(KEYS.ACTIVE, getId());
        super.setRef(REF);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isZone() {
        return false;
    }

    @Override
    public boolean isMissile() {
        return false;
    }

    @Override
    public boolean isOffhand() {
        return false;
    }


    @Override
    public boolean isAttackGeneric() {
        return false;
    }

    @Override
    public boolean isBlocked() {
        return false;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public boolean isMelee() {
        return false;
    }

    @Override
    public boolean isMove() {
        return false;
    }

    @Override
    public boolean isTurn() {
        return false;
    }

    @Override
    public Obj getTargetObj() {
        return null;
    }

    @Override
    public GroupImpl getTargetGroup() {
        return null;
    }

    @Override
    public void playCancelSound() {
        // TODO Auto-generated method stub

    }

    @Override
    public Obj getOwnerUnit() {
        return Eidolons.getMainHero();
    }

    @Override
    public void setCancelled(Boolean c) {
        // TODO Auto-generated method stub

    }

    @Override
    public Boolean isCancelled() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInterrupted() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean isFree() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEffectSoundPlayed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setEffectSoundPlayed(boolean effectSoundPlayed) {
        // TODO Auto-generated method stub

    }

    @Override
    public Abilities getAbilities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Targeting getTargeting() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isForcePresetTarget() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setForcePresetTarget(boolean b) {
        // TODO Auto-generated method stub

    }

    public MACRO_MODES getMode() {
        return mode;
    }

    public void setMode(MACRO_MODES mode) {
        this.mode = mode;
    }

    public MACRO_PARTY_ACTIONS getMpa() {
        return mpa;
    }

    public void setMpa(MACRO_PARTY_ACTIONS mpa) {
        this.mpa = mpa;
    }

}
