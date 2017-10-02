package main.entity.item;

import main.content.*;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActionManager;
import main.entity.active.DC_QuickItemAction;
import main.entity.obj.HeroItem;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.module.dungeoncrawl.objects.Trap;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.TypeInitializer;
import main.system.sound.SoundMaster.STD_SOUNDS;

public class DC_QuickItemObj extends DC_HeroItemObj implements HeroItem {
    private static final VALUE[] TRANSLATED_VALUES = {G_PROPS.STD_BOOLS};
    // or aggregation?
    private DC_QuickItemAction active;
    private boolean wrapped;

    private DC_WeaponObj wrappedWeapon;
    private Trap wrappedTrap;
    private boolean ammo;
    private boolean trap;

    public DC_QuickItemObj(ObjType type, Player owner, MicroGame game, Ref ref) {
        super(type, owner, game, ref, null);

    }

    public DC_QuickItemObj(ObjType type, Player owner, MicroGame game, Ref ref, boolean wrapped) {
        super(type, owner, game, ref, null);
        this.wrapped = wrapped;
        type.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.WRAPPED_ITEM + "", true);
        // durability into charges?

        // generate active

    }

    public boolean isAmmo() {
        return ammo;
    }

    private void generateWrappedActive() {
        String typeName;
        setWrappedWeapon(new DC_WeaponObj(type, owner, getGame(), ref));

        if (checkProperty(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.AMMO + "")) {
            this.ammo = true;
            typeName = DC_ActionManager.RELOAD + " " + type.getGroup();
            ref.setID(KEYS.AMMO, id);
            setParam(PARAMS.CHARGES, getIntParam(PARAMS.DURABILITY));// TODO C_
            // in
            // any
            // pesistent
            // version!
            setParam(PARAMS.C_CHARGES, getIntParam(PARAMS.CHARGES));
        } else {
            typeName = DC_ActionManager.THROW;

            // will it not have effect on hero?

            ref.setID(KEYS.WEAPON, getWrappedWeapon().getId());
            setParam(PARAMS.CHARGES, 1);
            setParam(PARAMS.C_CHARGES, 1);
        }

        ObjType type = new ObjType(DataManager.getType(typeName, DC_TYPE.ACTIONS));
        type.setProperty(G_PROPS.IMAGE, getImagePath());
        type.setProperty(G_PROPS.NAME, type.getName() + ""
                + StringMaster.wrapInParenthesis(getName()));
        type.setGame(game);
        setActive(new DC_QuickItemAction(type, getOriginalOwner(), getGame(), ref));
        getActive().setItem(this);

    }

    @Override
    public void construct() {
        if (active == null) {
            if (wrapped) {
                generateWrappedActive();
                return;
            }
        }
        if (!constructed) {
            ref.setID(KEYS.ACTIVE, getId());
        } else {
            if (getActive() == null) {
                generateWrappedActive();
            } else {
                ref.setID(KEYS.ACTIVE, getActive().getId()); // TODO for parsing?
            }
        }
        super.construct();

        if (!StringMaster.isEmpty(getProperty(PROPS.ITEM_SPELL))) {

        }

        if (actives == null) // ?
        {
            return;
        }
        if (actives.isEmpty()) {
            return;
        }

        ObjType type = initActiveType();
        setActive(new DC_QuickItemAction(type, getOriginalOwner(), getGame(), ref));
        getActive().setActives(getActives());
        getActive().setConstructed(true);
        getActive().setItem(this);

    }

    @Override
    public void setRef(Ref REF) {
        // if (wrapped)
        REF = REF.getCopy();
        super.setRef(REF);
        this.ref.setID(KEYS.ITEM, id);
        if (getWrappedWeapon() != null) {
            ref.setID(KEYS.WEAPON, getWrappedWeapon().getId());
        }
        if (ammo) {
            ref.setID(KEYS.AMMO, getId());
        }
        if (active != null) {
            ref.setID(KEYS.ACTIVE, getActive().getId());
        }
    }

    private ObjType initActiveType() {
        ObjType type = new TypeInitializer().getNewType(DC_TYPE.ACTIONS);
        type.setProperty(G_PROPS.IMAGE, getImagePath());
        type.setProperty(G_PROPS.NAME, getName() + "'s active");
        type.setGame(game);

        for (VALUE v : ValuePages.COSTS) {
            type.copyValue(v, this);
        }
        for (VALUE v : ValuePages.QUICK_ITEM_PARAMETERS) {
            type.copyValue(v, this);
        }
        for (VALUE v : ValuePages.QUICK_ITEM_PROPERTIES) {
            type.copyValue(v, this);
        }
        for (VALUE v : TRANSLATED_VALUES) {
            type.copyValue(v, this);
        }

        return type;
    }

    @Override
    public void afterEffects() {
        super.afterEffects();
        if (!wrapped) {
            if (active != null) {
                // what about reload?
                for (VALUE v : ValuePages.QUICK_ITEM_PARAMETERS) {
                    active.copyValue(v, this);
                }
                for (VALUE v : ValuePages.QUICK_ITEM_PROPERTIES) {
                    active.copyValue(v, this);
                }
                for (VALUE v : TRANSLATED_VALUES) {
                    active.copyValue(v, this);
                }
            }
        }
    }

    public boolean activate() {

        if (!isConstructed() || wrapped) {
            construct();
        }
        if (wrapped) {
            activatePassives();
        }
        if (canBeActivated()) {
            setRef(ref.getSourceObj().getRef());
            ref.getSourceObj().getRef().setID(KEYS.ITEM, getId());
            if (getActive().isForcePresetTarget()) {
                if (getActive().getRef().getTargetObj() != null) {
                    ref.setTarget(getActive().getRef().getTarget());
                }
            } else {
                getActive().setRef(ref);
            }
            ref.setID(KEYS.ACTIVE, getActive().getId());
              getActive().activate();
            if (getActive().isCancelled() != null) {
                if (getActive().isCancelled()) {
                    return false;
                }
            }
            // if (!game.isDebugMode())
            removeCharge();
            getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_HAS_USED_QUICK_ITEM, ref));
        } else {

        }

        return true;
    }

    @Override
    public void activatePassives() {
        if (getActive() != null) {
            ref.setID(KEYS.ACTIVE, getActive().getId());
        }
        super.activatePassives();
    }

    @Override
    protected KEYS getKey() {
        return KEYS.SLOT_ITEM;
    }

    public boolean canBeActivated() {
        if (!constructed) {
            constructConcurrently();
        }
        if (getActive() == null) {
            return false;
        }
        if (getIntParam(PARAMS.C_CHARGES) > 0) {
            return getActive().canBeActivated(ref, true);
        }
        // outOfCharges();
        return false;
    }

    private void removeCharge() {
        modifyParameter(PARAMS.C_CHARGES, -1);
        if (wrapped) {
            modifyParameter(PARAMS.C_DURABILITY, -1);
        }
        if (getIntParam(PARAMS.C_CHARGES) <= 0) {
            outOfCharges();
        }

    }

    @Override
    protected void addDynamicValues() {
        super.addDynamicValues();
        setParam(PARAMS.C_CHARGES, getIntParam(PARAMS.CHARGES));
    }

    private void outOfCharges() {
        if (!checkBool(GenericEnums.STD_BOOLS.PERMANENT_ITEM)) {
            getHero().removeQuickItem(this);
        }

    }

    protected boolean isConstructOnInit() {
        return false;
    }

    @Override
    public void clicked() {
        // TODO // threading?
        if (getGame().getLoop().getActivatingAction() != null) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            return;
        }
        if (game.getManager().isSelecting()) {
            super.clicked();
        } else {
            activate();
        }
    }

    public void invokeClicked() {
        new Thread(this, "Item active " + getName() + id).start();
    }

    @Override
    public void invokeRightClicked() {
        super.invokeRightClicked();
    }

    @Override
    public void apply() {
        if (getHero() == null) {
            initHero();
        }
        if (wrapped) {
            return;
        }
        activatePassives();
    }

    @Override
    public String getToolTip() {
        if (!constructed) {
            constructConcurrently();
        }
        if (active == null) {
            return getName();
        }
        if (canBeActivated()) {
            return getName();
        } else {
            return active.getToolTip();
        }
    }

    @Override
    protected PARAMETER getDurabilityParam() {
        return null; // charges?
    }

    protected void modifyHeroParameters() {

    }

    // @Override
    // protected void activatePassives() {
    // // if (wrapped)
    // // return;
    // for (String prop : StringMaster
    // .openContainer(getProperty(G_PROPS.STANDARD_PASSIVES))) {
    // getHero().addProperty(G_PROPS.STANDARD_PASSIVES, prop);
    // }
    // for (String prop : StringMaster
    // .openContainer(getProperty(G_PROPS.PASSIVES))) {
    // getHero().addProperty(G_PROPS.PASSIVES, prop);
    // }
    // }
    @Override
    protected boolean isActivatePassives() {
        return wrapped; // TODO so potions cannot have, say, spec effects
        // applied to them? I could of course use hero
        // instead... but it's a limitation! :)
    }

	/*
     *
	 * 
	 * Cooldown Charges Out of charges - remove if removable
	 * 
	 * Will these items also provide passive bonuses? What about rings and
	 * talismans?
	 */

    public DC_QuickItemAction getActive() {
        if (active==null )
            if (!isConstructed())
                construct();
        return active;
    }

    public void setActive(DC_QuickItemAction active) {
        this.active = active;
    }

    public DC_WeaponObj getWrappedWeapon() {
        return wrappedWeapon;
    }

    public void setWrappedWeapon(DC_WeaponObj wrappedWeapon) {
        this.wrappedWeapon = wrappedWeapon;
    }

    public void activate(Ref ref) {
        setRef(ref);
        getActive().setForcePresetTarget(true);
        activate();

    }

    public boolean isConcoction() {
        return checkProperty(G_PROPS.ITEM_GROUP, ItemEnums.ITEM_GROUP.CONCOCTIONS.toString());
    }

    public boolean isCoating() {
        return checkProperty(G_PROPS.ITEM_GROUP, ItemEnums.ITEM_GROUP.COATING.toString());
    }

    public boolean isPotion() {
        return checkProperty(G_PROPS.ITEM_GROUP, ItemEnums.ITEM_GROUP.POTIONS.toString());
    }

}
