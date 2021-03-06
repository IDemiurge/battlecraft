package boss.logic.rules;

import boss.BossHandler;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import boss.BossManager;
import boss.BossModel;

public class BossVision <T extends BossModel> extends BossHandler<T> {
    public BossVision(BossManager<T> manager) {
        super(manager);
    }
    /*
    in fact, we might be wise to show HIS vision via overlays!
    status info args
     */

    public boolean isMainHeroVisible(){
        return isVisible(Eidolons.getMainHero());
    }

    public boolean isVisible(Unit hero) {
        return true;
    }
}
