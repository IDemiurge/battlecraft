package eidolons.game.battlecraft.rules.counter;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.TriggerRule;
import eidolons.game.core.EUtils;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.game.logic.event.Event;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.entity.CounterMaster;

public class UndyingCounterRule extends TriggerRule {
    public static boolean check(Event event) {
        if (!(event.getRef().getTargetObj() instanceof Unit)) {
            return false;
        }
        Unit unit = (Unit) event.getRef().getTargetObj();
        int n = NumberUtils.getInteger(unit.getCustomParamMap().get(CounterMaster.findCounter("undying")));

//                event.getRef().getSourceObj().getCounter(CounterMaster.findCounter(UnitEnums.COUNTER.Undying.getName()));
        if (n==0) {
            return false;
        }
        unit.modifyCounter(UnitEnums.COUNTER.Undying, -1);
        unit.preventDeath();
//     TODO shouldn't be necessary?
//      unit.addProperty(G_PROPS.STANDARD_PASSIVES, UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE.getName());
        String msg=unit.getName() + " will not die yet... [" +
                (n-1) +
                "]";
//        DC_SoundMaster.playStandardSound();
        EUtils.showInfoText(true, msg);

        return true;
    }

    @Override
    public void initTrigger() {

    }
}