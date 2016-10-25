package main.ability.conditions;

import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.content.CONTENT_CONSTS.UNIT_TO_UNIT_VISION;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref.KEYS;
import main.entity.obj.BattlefieldObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.game.DC_Game;

public class VisibilityCondition extends ConditionImpl {

    private UNIT_TO_PLAYER_VISION p_vision;
    private UNIT_TO_UNIT_VISION u_vision;
    private KEYS source;
    private KEYS match;

    public VisibilityCondition(UNIT_TO_PLAYER_VISION p_vision) {
        this(null, null, p_vision);
    }

    public VisibilityCondition(UNIT_TO_UNIT_VISION u_vision) {
        this(null, null, u_vision);
    }

    public VisibilityCondition(KEYS source, KEYS match, UNIT_TO_PLAYER_VISION p_vision) {
        this.match = match;
        this.source = source;
        this.p_vision = p_vision;
    }

    public VisibilityCondition(KEYS source, KEYS match, UNIT_TO_UNIT_VISION u_vision) {
        this.match = match;
        this.source = source;
        this.u_vision = u_vision;
    }

    @Override
    public boolean check() {
        if (!(ref.getObj(KEYS.MATCH) instanceof BattlefieldObj))
            return false;
        DC_Obj match = (DC_Obj) ref.getObj(KEYS.MATCH);
        boolean result = false;
        if (this.match == null && this.source == null) {
            if (match.getActivePlayerVisionStatus() == p_vision) {
                return true;
            }
            if (match.getUnitVisionStatus().isSufficient(u_vision)) {
                return true;
            }
            return false;
        }

        if (p_vision != null) {
            DC_HeroObj unit = (DC_HeroObj) ref.getObj(source);
            result = unit.getActivePlayerVisionStatus() == p_vision;
        } else if (u_vision != null) {
            match = (DC_Obj) ref.getObj(this.match);
            // if (((DC_Game) game).getManager().isAI_Turn()) { what's the idea?
            DC_HeroObj activeObj = (DC_HeroObj) ref.getObj(source);
            result = ((DC_Game) game).getVisionManager().getUnitVisibilityStatus(match, activeObj)
                    .isSufficient(u_vision);
            // }
        }

        return result;
    }

}