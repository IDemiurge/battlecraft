package main.client.cc.logic;

import main.client.cc.CharacterCreator;
import main.content.ContentManager;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.DC_Formulas;

import java.util.HashMap;
import java.util.Map;

public class HeroLevelManager {
    public static final VALUE[] LEVEL_RELEVANT_VALUES = {PARAMS.XP_LEVEL_MOD,};
    private static Map<VALUE, String> buffer = new HashMap<>();

    public static void levelUp(DC_HeroObj hero) {
        levelUp(hero, false);
    }

    public static void levelUp(DC_HeroObj hero, Boolean dc_hc_macro) {
        boolean auto = false;
        if (dc_hc_macro == null) {
            auto = true;
            dc_hc_macro = true;
        }
        if (!dc_hc_macro && hero.getGame().isSimulation()) {
            CharacterCreator.getHeroManager().saveHero(hero);
            CharacterCreator.getHeroManager().update(hero);
        }
        ObjType type = hero.getType();

        copyLevelValues(hero, type);

        hero.modifyParameter(PARAMS.MASTERY_POINTS, DC_Formulas.getMasteryFromIntelligence(hero
                .getIntParam(PARAMS.KNOWLEDGE)));

        modifyValues(hero, auto);
        if (!dc_hc_macro)
            CharacterCreator.getHeroManager().update(hero);
        resetLevelValues(type);
    }

    private static void resetLevelValues(ObjType type) {
        for (VALUE v : LEVEL_RELEVANT_VALUES) {
            type.setValue(v, buffer.get(v));
        }
    }

    private static void copyLevelValues(DC_HeroObj hero, ObjType type) {
        for (VALUE v : LEVEL_RELEVANT_VALUES) {
            buffer.put(v, type.getValue(v));
            type.setValue(v, hero.getValue(v));
        }
    }

    public static void addLevels(Entity hero, int levels) {
        for (int i = 0; i < levels; i++)
            modifyValues(hero);
    }

    private static void modifyValues(Entity hero) {
        modifyValues(hero, true);
    }

    private static void modifyValues(Entity hero, boolean auto) {
        hero.getType().modifyParameter(PARAMS.HERO_LEVEL, 1);
        hero.getType().modifyParameter(PARAMS.LEVEL, 1);
        if (!auto) {
            int level = hero.getType().getIntParam(PARAMS.HERO_LEVEL);
            int xpAdded = DC_Formulas.getXpForLevel(level);
            int identityAdded = DC_Formulas.getIdentityPointsForLevel(level);
            int goldAdded = DC_Formulas.getGoldForLevel(level);
            goldAdded += hero.getIntParam(PARAMS.GOLD_PER_LEVEL);
            // gold per level from Craftsman?
            int mod = hero.getIntParam(PARAMS.XP_LEVEL_MOD);
            if (mod != 0) {
                xpAdded = xpAdded * mod / 100;
            }
            mod = hero.getIntParam(PARAMS.GOLD_MOD);
            if (mod != 0) {
                goldAdded = goldAdded * mod / 100;
            }
            hero.modifyParameter(PARAMS.GOLD, goldAdded);
            hero.getType().modifyParameter(PARAMS.TOTAL_XP, xpAdded);
            hero.modifyParameter(PARAMS.XP, xpAdded);
            hero.modifyParameter(PARAMS.IDENTITY_POINTS, identityAdded);
        }

        int p = hero.getIntParam(PARAMS.ATTR_POINTS_PER_LEVEL);
        hero.modifyParameter(PARAMS.ATTR_POINTS, p);
        hero.getType().modifyParameter((PARAMS.ATTR_POINTS_PER_LEVEL),
                DC_Formulas.ATTR_POINTS_PER_LEVEL_BONUS);

        p = hero.getIntParam(PARAMS.MASTERY_POINTS_PER_LEVEL);
        hero.modifyParameter(PARAMS.MASTERY_POINTS, p);
        hero.getType().modifyParameter((PARAMS.MASTERY_POINTS_PER_LEVEL),
                DC_Formulas.MASTERY_POINTS_PER_LEVEL_BONUS);

    }

    private static void upMasteries(Entity hero) {
        for (PARAMETER p : ContentManager.getMasteries()) {
            if (hero.getIntParam(p) > 0) {
                int amount = hero.getIntParam(ContentManager.getPerLevelValue(p.toString()));
                if (amount > 0)
                    hero.modifyParameter(p, amount);
            }
        }
    }

    @Deprecated
    private static void upAttrs(Entity hero) {

        // for (ATTRIBUTE attr : ATTRIBUTE.values()) {
        // int amount = hero.getIntParam(ContentManager.getPerLevelValue(attr
        // .getParameter().toString()));
        // if (amount > 0)
        // hero.modifyParameter(ContentManager.getBaseAttribute(attr
        // .getParameter()), amount);
        // }
    }

}