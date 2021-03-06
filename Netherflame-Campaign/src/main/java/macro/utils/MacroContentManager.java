package macro.utils;

import main.content.ContentValsManager;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.entity.Entity;

public class MacroContentManager {

    public static void addDefaultValues(Entity entity) {
        for (MACRO_PARAMS p : MACRO_PARAMS.values()) {
            if (ContentValsManager.isValueForOBJ_TYPE(entity.getOBJ_TYPE_ENUM(), p)) {
                if (!entity.getParamMap().containsKey(p)) {
                    if (!p.getDefaultValue().isEmpty()) {
                        entity.setParam(p, p.getDefaultValue(), true);
                    }
                }
            }
        }
        for (MACRO_PROPS p : MACRO_PROPS.values()) {
            if (ContentValsManager.isValueForOBJ_TYPE(entity.getOBJ_TYPE_ENUM(), p)) {
                if (!entity.getPropMap().containsKey(p)) {
                    if (!p.getDefaultValue().isEmpty()) {
                        entity.setProperty(p, p.getDefaultValue(), true);
                    }
                }
            }
        }

    }
}
