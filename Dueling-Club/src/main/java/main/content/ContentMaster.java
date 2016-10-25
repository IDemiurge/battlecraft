package main.content;

import main.content.CONTENT_CONSTS.RACE;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.List;

public class ContentMaster {
    /*
	 * additional version dependencies: 
	 * >> Hero personality and life traits...
	 * >> 
	 */

    public static final String fullVersionSpellGroups = "Warp;Blood Magic;Redemption;Psychic;Enchantment;Savage;Demonology";
    public static final String fullVersionDeities = "Dark Gods;Fide Arcanum;";

    public static final String basicVersionSpellGroups = "Destruction;Affliction;Blood Magic;Benediction;Celestial;Shadow;Witchery;Sorcery;Conjuration;Sylvan;Elemental;";

    // blocked will prevent from playing with a party that has X, taking these
    // items in HC...
    public boolean isTypeVersionBlocked(ObjType type, GAME_VERSION version) {

        switch ((OBJ_TYPES) type.getOBJ_TYPE_ENUM()) {
            case ARMOR:
            case WEAPONS:
            case GARMENT:
            case ITEMS:
            case JEWELRY:
                return checkItemBlocked(type, version);
            case CHARS:
                if (type.getSubGroupingKey().equalsIgnoreCase(StringMaster.BACKGROUND))
                    return checkHeroBackgroundBlocked(type, version);

            case CLASSES:
                return checkItemBlocked(type, version);

            case DEITIES:

            case DUNGEONS:

            case SKILLS:

            case SPELLS:

            case UNITS:

        }
        return false;

    }

    private boolean checkHeroBackgroundBlocked(ObjType type, GAME_VERSION version) {
        if (type.getGroupingKey().equalsIgnoreCase(RACE.HUMAN.toString()))
            if (!type.getName().contains("Realm"))
                return true;
            else
                return !type.getName().contains("Man of");

        return false;
    }

    private boolean checkItemBlocked(ObjType type, GAME_VERSION version) {
        if (version == GAME_VERSION.BATTLECRAFT_PROMO)
            if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.JEWELRY)
                return true;
        if (isAboveBasic(version)) {

            List<String> blockedList = getBlockedTypeGroupsBasic((OBJ_TYPES) type
                    .getOBJ_TYPE_ENUM());

            for (String blocked : blockedList)
                if (StringMaster.compareByChar(type.getGroupingKey(), blocked))
                    return true;

        }
        return false;
    }

    private List<String> getBlockedTypeGroupsBasic(OBJ_TYPES TYPE) {
        switch (TYPE) {
            case WEAPONS:
            case GARMENT:
            case ITEMS:

            case CHARS:

            case CLASSES:

            case DEITIES:

            case DUNGEONS:

            case SKILLS:

            case SPELLS:

            case UNITS:
        }
        return null;
    }

    private boolean isAboveBasic(GAME_VERSION version) {
        return EnumMaster.getEnumConstIndex(GAME_VERSION.class, version) > getBasicVersionIndex();
    }

    private int getBasicVersionIndex() {
        return EnumMaster.getEnumConstIndex(GAME_VERSION.class, GAME_VERSION.BATTLECRAFT_BASIC);
    }

    // connect to server to verify any version above basic!
    public enum GAME_VERSION {
        BATTLECRAFT_PROMO, BATTLECRAFT_BASIC, BATTLECRAFT_FULL, BATTLECRAFT_SPECIAL,
    }

}
