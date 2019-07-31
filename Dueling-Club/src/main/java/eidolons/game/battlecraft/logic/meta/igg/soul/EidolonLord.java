package eidolons.game.battlecraft.logic.meta.igg.soul;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.Soul;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.SoulMaster;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class EidolonLord extends LightweightEntity {

    public static EidolonLord lord;

    HeroChain chain;

    public void soulsLost(Soul... souls) {

        for (Soul soul : souls) {
            if (soul == null) {
                continue;
            }
            removeProperty(PROPS.LORD_SOULS, soul.getUnitType().getName());
            getGame().getLogManager().log("A Soul is lost: " + soul.getUnitType().getName());
        }
        SoulMaster.clear();

        //TODO check warnings?

    }

    public HeroChain getChain() {
        return chain;
    }

    public void setChain(HeroChain chain) {
        this.chain = chain;
    }

    public EidolonLord(ObjType type) {
        super(type);
        lord = this;
        lord.setParam(PARAMS.C_SOULFORCE, getIntParam(PARAMS.BASE_SOULFORCE));
    }

    public void soulforceGained(int amount) {
        lord.addParam(PARAMS.C_SOULFORCE, amount);
        GuiEventManager.trigger(GuiEventType.SOULFORCE_RESET, this);
    }

    public void soulforceLost(int amount) {
        lord.addParam(PARAMS.C_SOULFORCE, -amount);
        GuiEventManager.trigger(GuiEventType.SOULFORCE_RESET, this);

    }

    public Integer getSoulforce() {
        return super.getIntParam(PARAMS.C_SOULFORCE);
    }

    public Integer getSoulforceMax() {
        return super.getIntParam(PARAMS.SOULFORCE);
    }

    public void soulsGained(Soul soul) {
        addProperty(PROPS.LORD_SOULS, soul.getUnitType().getName());
        getGame().getLogManager().log("A Soul is trapped: " + soul.getUnitType().getName());
    }


    /**
     * from save
     * > soul list
     * > memories
     * and more...
     */


}
