package main.rules.perk;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;

public class HeightRule {

    public static boolean isTaller(Unit source, Unit target) {
        int source_height = source.getIntParam(PARAMS.HEIGHT);
        int target_height = target.getIntParam(PARAMS.HEIGHT);

        return false;

    }

}
