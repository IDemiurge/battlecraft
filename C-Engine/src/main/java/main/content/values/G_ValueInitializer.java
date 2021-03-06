package main.content.values;

import main.content.VALUE;
import main.content.enums.entity.BfObjEnums;

import static main.content.values.properties.G_PROPS.*;
public class G_ValueInitializer {

    public static void init(){
        BfObjEnums.init();

        VERSION.setDevOnly(true);
        DEV_NOTES.setDevOnly(true);
        TIMESTAMP.setDevOnly(true);
        UNIQUE_ID.setDevOnly(true);

        NAME.setInputReq(VALUE.INPUT_REQ.STRING);
        // MAIN_HAND_ITEM.setDynamic(true);
        // OFF_HAND_ITEM.setDynamic(true);
        // ARMOR_ITEM.setDynamic(true);
        UNIQUE_ID.setDynamic(true);
        STATUS.setDynamic(true);
        // STATUS.setDynamic(true);
        SPELL_POOL.setDynamic(true);

        VARIABLES.setContainer(true);

        TARGETING_MODE.setDefaultValue("SINGLE");
    }
}
