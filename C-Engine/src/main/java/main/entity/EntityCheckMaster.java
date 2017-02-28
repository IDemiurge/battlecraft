package main.entity;

import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.EnumMaster;

import java.util.List;

public class EntityCheckMaster {

    public static boolean checkPropertyAny(List<? extends Entity> list, PROPERTY prop, Object value) {
        for (Entity sub : list) {
            if (sub.checkProperty(prop, value.toString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPropertyAll(List<? extends Entity> list, PROPERTY prop, Object value) {
        for (Entity sub : list) {
            if (!sub.checkProperty(prop, value.toString())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOverlaying(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity.checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.OVERLAYING)
                || entity.checkProperty(G_PROPS.CLASSIFICATIONS, "" + UnitEnums.CLASSIFICATIONS.ATTACHED);
    }

    public static BACKGROUND getBackground(Entity hero) {
        return new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class, hero
                .getProperty(G_PROPS.BACKGROUND));

    }

    public static RACE getRace(Entity hero) {
        return new EnumMaster<RACE>().retrieveEnumConst(RACE.class, hero.getProperty(G_PROPS.RACE));
    }

    public static GENDER getGender(Entity hero) {
        return new EnumMaster<GENDER>().retrieveEnumConst(GENDER.class, hero
                .getProperty(G_PROPS.GENDER));
    }
}