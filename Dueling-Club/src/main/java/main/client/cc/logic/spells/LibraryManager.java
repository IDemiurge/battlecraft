package main.client.cc.logic.spells;

import main.content.ContentManager;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.elements.conditions.NumericCondition;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_SpellObj;
import main.entity.type.ObjType;
import main.system.DC_Formulas;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class LibraryManager {

    private static final String SD = "{SPELL_SPELL_DIFFICULTY}";
    private static final String STANDARD = "STANDARD";

    public static Condition getSpellRequirements(DC_SpellObj spell) {
        // SD: Mastery or Knowledge
        String spellGroup = spell.getProperty(G_PROPS.SPELL_GROUP);
        return new NumericCondition("max({SOURCE_KNOWLEDGE}/2-5," + "{SOURCE_" + spellGroup
                + "MASTERY})", SD);
    }

    public static synchronized String initSpellbook(DC_HeroObj hero) {
        if (hero.getGame().isSimulation())
            checkNewAutoVerbatim(hero);
        List<ObjType> list = new LinkedList<>();
        for (ObjType type : DataManager.getTypes(OBJ_TYPES.SPELLS)) {
            if (checkKnown(hero, type)) {
                // if (!hero.checkProperty(PROPS.LEARNED_SPELLS,
                // type.getName()))
                if (!isUpgraded(hero, type))
                    list.add(type);

            }
        }

        String known = "";
        for (ObjType type : list) {
            known += type.getName() + StringMaster.getContainerSeparator();
        }
        hero.setProperty(PROPS.KNOWN_SPELLS, known, true);

        String spellbook = "";
        for (ObjType type : list) {
            if (!hero.checkProperty(PROPS.MEMORIZED_SPELLS, type.getName()))
                if (!hero.checkProperty(PROPS.VERBATIM_SPELLS, type.getName()))
                    spellbook += type.getName() + StringMaster.getContainerSeparator();
        }
        hero.setProperty(PROPS.SPELLBOOK, spellbook, true);
        return spellbook;
    }

    private static boolean isUpgraded(DC_HeroObj hero, ObjType type) {
        return hero.checkProperty(PROPS.UPGRADED_SPELLS, type.getName());
    }

    public static boolean checkHeroHasSpell(DC_HeroObj hero, ObjType type) {
        if (hero.checkProperty(PROPS.MEMORIZED_SPELLS, type.getName()))
            return true;
        if (hero.checkProperty(PROPS.DIVINED_SPELLS, type.getName()))
            return true;
        if (hero.checkProperty(PROPS.VERBATIM_SPELLS, type.getName()))
            return true;
        return false;
    }

    public static DC_SpellObj getSpellFromHero(DC_HeroObj hero, String name) {
        for (DC_SpellObj s : hero.getSpells()) {
            if (s.getName().equalsIgnoreCase(name))
                return s;
        }

        return null;
    }

    public static void checkNewAutoVerbatim(DC_HeroObj hero) {
        for (String spell : StringMaster.openContainer(hero.getProperty(PROPS.LEARNED_SPELLS))) {
            boolean result = false;
            ObjType type = DataManager.getType(spell, OBJ_TYPES.SPELLS);
            if (checkStandardSpell(type)) {
                if (checkDoubleKnowledge(hero, type))
                    result = true;

                if (checkKnowledge(hero, type)) // optimize-merge!
                    if (checkMastery(hero, type)) {
                        result = true;
                    }
            }

            if (result) {
                if (hero.checkProperty(PROPS.MEMORIZED_SPELLS, type.getName()))
                    hero.removeProperty(PROPS.MEMORIZED_SPELLS, type.getName(), true);
                addVerbatimSpell(hero, type);
            }
        }
    }

    public static boolean checkKnown(DC_HeroObj hero, ObjType type) {
        if (isLearned(hero, type))
            return true;
        if (checkStandardSpell(type)) {
            if (checkDoubleKnowledge(hero, type))
                return true;
            if (checkKnowledge(hero, type)) // Intelligence ?
                if (checkMastery(hero, type))
                    return true;
        }
        return false;
    }

    public static boolean isLearned(DC_HeroObj hero, ObjType type) {
        return hero.checkProperty(PROPS.LEARNED_SPELLS, type.getName());
    }

    public static boolean isKnown(DC_HeroObj hero, Entity type) {
        return hero.checkProperty(PROPS.KNOWN_SPELLS, type.getName());
    }

    public static boolean checkKnowledge(DC_HeroObj hero, ObjType type) {
        if (type.getIntParam(PARAMS.SPELL_DIFFICULTY) == 0)
            return false;
        return hero.checkParam(PARAMS.KNOWLEDGE, type.getParam(PARAMS.SPELL_DIFFICULTY));
    }

    // 2X KNOWLEDGE FOR ANY SPELL
    // IF MASTERY IS >1
    public static boolean checkDoubleKnowledge(DC_HeroObj hero, ObjType type) {
        return (hero.checkParam(PARAMS.KNOWLEDGE, ""
                + Math.round(type.getIntParam(PARAMS.SPELL_DIFFICULTY)
                * DC_Formulas.KNOWLEDGE_ANY_SPELL_FACTOR)))
                && hero.checkParam(ContentManager
                .findMastery(type.getProperty(G_PROPS.SPELL_GROUP)));
    }

    public static boolean checkMastery(DC_HeroObj hero, ObjType type) {

        return hero.checkParam(ContentManager.findMastery(type.getProperty(G_PROPS.SPELL_GROUP)),
                type.getParam(PARAMS.SPELL_DIFFICULTY));
    }

    public static boolean checkStandardSpell(ObjType type) {
        return type.checkSingleProp(G_PROPS.GROUP, STANDARD);
    }

    public static boolean replaceSpellVersion(DC_HeroObj hero, Entity type, PROPERTY poolProp) {
        return hasSpellVersion(hero, type, poolProp, true);
    }

    public static DC_SpellObj getVerbatimSpellVersion(DC_HeroObj hero, Entity type) {
        hero.initSpells(true);
        boolean upgrade = type.isUpgrade();
        String baseName = (upgrade) ? type.getProperty(G_PROPS.BASE_TYPE) : type.getName();

        for (DC_SpellObj s : hero.getSpells()) {
            // TODO refactor into checkUpgrade(spell, type)
            if (StringMaster.compare(s.getSpellPool() + "", PROPS.VERBATIM_SPELLS.getName(), false)) {
                if (s.isUpgrade()) {
                    if (s.getProperty(G_PROPS.BASE_TYPE).equalsIgnoreCase(baseName)) {
                        return s;
                    }
                } else if (s.getName().equalsIgnoreCase(baseName)) {
                    return s;
                }

            }
        }
        return null;
    }

    public static boolean hasSpellVersion(DC_HeroObj hero, Entity type, PROPERTY poolProp,
                                          boolean replace) {
        hero.initSpells(true);
        boolean upgrade = type.isUpgrade();
        String baseName = (upgrade) ? type.getProperty(G_PROPS.BASE_TYPE) : type.getName();
        for (DC_SpellObj spell : hero.getSpells()) {
            if (StringMaster.compare(spell.getSpellPool() + "", poolProp.getName(), false)) {
                if (spell.isUpgrade()) {
                    if (spell.getProperty(G_PROPS.BASE_TYPE).equalsIgnoreCase(baseName)) {
                        if (replace)
                            replaceSpell(hero, type, poolProp, spell);
                        return true;
                    }
                } else if (spell.getName().equalsIgnoreCase(baseName)) {
                    if (replace)
                        replaceSpell(hero, type, poolProp, spell);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean replaceSpell(DC_HeroObj hero, Entity type, PROPERTY poolProp,
                                        DC_SpellObj spell) {
        if (poolProp == PROPS.VERBATIM_SPELLS) {
            hero.addProperty(PROPS.UPGRADED_SPELLS, spell.getName(), true);
        }
        return hero.getType().replaceContainerPropItem(poolProp, type.getName(), spell.getName());
    }

    public static boolean hasSpellVersion(DC_HeroObj hero, Entity type, PROPERTY pool) {
        return hasSpellVersion(hero, type, pool, false);
    }

    public static void addVerbatimSpell(DC_HeroObj hero, ObjType type) {
        hero.addProperty(PROPS.VERBATIM_SPELLS, type.getName(), true);
        hero.addProperty(PROPS.LEARNED_SPELLS, type.getName(), true);
    }

}
