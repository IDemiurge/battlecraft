package eidolons.game.module.herocreator.logic.spells;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.unit.Unit;
import eidolons.content.DC_Formulas;
import eidolons.system.math.DC_MathManager;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.conditions.NumericCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.entity.FilterMaster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
@Deprecated
public class DivinationMaster {
    public static final String BUFF_FAVORED = "Favored";
    private static final int MAX_SPELL_DIVINE_CHANCE = 20;
    private static Unit hero;
    private static List<ObjType> spellPool;
    private static NumericCondition sdCondition;
    private static int pool;
    private static Map<SPELL_GROUP, Integer> spellGroups;

    public static void removeDivination(Unit hero) {
        List<Spell> spellsToRemove = new ArrayList<>();
        for (Spell spell : hero.getSpells()) {
            if (spell.getSpellPool() == SpellEnums.SPELL_POOL.DIVINED) {
                spellsToRemove.add(spell);
            }
        }
        hero.getSpells().removeAll(spellsToRemove);
        hero.setProperty(PROPS.DIVINED_SPELLS, "");
    }

    public static void divine(Unit hero, int h) {

    }

    public static List<Spell> divine(Unit diviningHero) {
        hero = diviningHero;
        List<Spell> list = new ArrayList<>();
        pool =0;//  getDivinationPool(hero);
        sdCondition =null ; //TODO

        Spell spell;
        spellGroups = initSpellGroups();

        if (spellGroups.isEmpty()) {
            return list;
        }

        Loop.startLoop(1000);
        while (!Loop.loopEnded()) {
            initSpellPool();
            if (spellPool.isEmpty()) {
                continue;
            }

            spell = tryDivine();
            if (spell == null) {
                break;
            }

            list.add(spell);
        }

        return list;

    }

    private static Spell tryDivine() {
        Spell spell = null;
        for (ObjType spellType : spellPool) {
            if (!checkSpell(spellType)) {
                continue;
            }

            spell = SpellMaster.getSpellFromHero(hero, spellType.getName());
            if (spell == null) {
                spell = new Spell(spellType, hero.getOwner(),
                 hero.getGame(), hero.getRef());
                hero.getSpells().add(spell);
                if (SpellMaster.checkHeroHasSpell(hero, spellType)) {
                    applyKnownSpellDivinationEffect(spell);
                    hero.getGame().getLogManager().log(hero.getNameIfKnown() + " applies a Divination bonus to a spell: " + spell);
                }
            } else {
                if (!spell.getGame().isSimulation()) {
                    applyKnownSpellDivinationEffect(spell);
                    hero.getGame().getLogManager().log(hero.getNameIfKnown() + " applies a Divination bonus to a spell: " + spell);
                }
            }
            pool -= spell.getIntParam(PARAMS.SPELL_DIFFICULTY);
            spell.setProperty(G_PROPS.SPELL_POOL, SpellEnums.SPELL_POOL.DIVINED.toString());
            hero.addProperty(PROPS.DIVINED_SPELLS, spell.getName());
            hero.getGame().getLogManager().log(hero.getNameIfKnown() + " has Divined a spell: " + spell);
            return spell;

        }

        return spell;
    }

    private static void applyKnownSpellDivinationEffect(Spell spell) {

        Ref ref = Ref.getSelfTargetingRefCopy(spell);
        AddBuffEffect buffEffect = new AddBuffEffect(BUFF_FAVORED, new Effects(
         new ModifyValueEffect(PARAMS.ESS_COST,
          MOD.MODIFY_BY_PERCENT, "-25"),
         new ModifyValueEffect(PARAMS.FOC_REQ,
          MOD.MODIFY_BY_PERCENT, "-25"),
         new ModifyValueEffect(PARAMS.FOC_COST,
          MOD.MODIFY_BY_PERCENT, "-25"),
         new ModifyValueEffect(PARAMS.TOU_COST,
          MOD.MODIFY_BY_PERCENT, "-25"),
         new ModifyValueEffect(PARAMS.SPELLPOWER_MOD,
          MOD.MODIFY_BY_PERCENT, "25")));
        buffEffect.apply(ref);

        buffEffect.getBuff().setDuration((hero.getIntParam(PARAMS.CHARISMA) + // TODO
         // LASTING
         // GRACE!
         hero.getIntParam(PARAMS.DIVINATION_MASTERY)) / 5);

        buffEffect.getBuff().setProperty(G_PROPS.STD_BOOLS,
         "" + GenericEnums.STD_BOOLS.STACKING, true);
    }

    private static boolean checkSpell(ObjType spellType) {
        if (spellType.getIntParam(PARAMS.SPELL_DIFFICULTY) <= 0) {
            return false;
        }
        if (!SpellMaster.checkStandardSpell(spellType)) {
            return false;
        }
        if (!WorkspaceMaster.checkTypeIsGenerallyReady(spellType)) {
            return false;
        }

        // TODO if already available, add a buff to it for 50% ess cost and cd

        return spellType.getIntParam(PARAMS.SPELL_DIFFICULTY) <= pool;
    }

    private static void initSpellPool() {
        spellPool = new ArrayList<>();

        // add up all weights, roll, preCheck within which range. Should be fair,
        // right?

        SPELL_GROUP chosenGroup = new RandomWizard<SPELL_GROUP>()
         .getObjectByWeight(spellGroups);
        List<ObjType> types = DataManager.toTypeList(
         DataManager
          .getTypesSubGroupNames(DC_TYPE.SPELLS, StringMaster
           .format(chosenGroup.name())),
         DC_TYPE.SPELLS);
        FilterMaster.filterOut(types, sdCondition);
        types.sort(getComparator());
        spellPool.addAll(types);
    }

    private static Map<SPELL_GROUP, Integer> initSpellGroups() {
        String spellGroupProperty = hero.getDeity().getType()
         .getProperty(PROPS.FAVORED_SPELL_GROUPS);

        boolean forced = false;
        if (!hero.getProperty(PROPS.DIVINATION_FORCED_SPELL_GROUPS).isEmpty()) {
            spellGroupProperty = hero
             .getProperty(PROPS.DIVINATION_FORCED_SPELL_GROUPS);
            forced = true;
        }

        Map<SPELL_GROUP, Integer> map = new RandomWizard<SPELL_GROUP>()
         .constructWeightMap(spellGroupProperty, SPELL_GROUP.class);
        if (forced) {
            return map;
        }
        if (hero.checkBool(GenericEnums.STD_BOOLS.DIVINATION_SPELL_GROUPS_INVERTED)) {
            map = new MapMaster<SPELL_GROUP, Integer>().invertMapOrder(map, true);
        }
        // TODO ++ USE

        int use = hero.getIntParam(PARAMS.DIVINATION_USE_FIRST);
        int crop = hero.getIntParam(PARAMS.DIVINATION_CROP_FIRST);
        if (use != 0) {
            crop = map.size() - use;
        }
        if (crop > 0) {
            if (crop >= map.size()) {
                crop = map.size() - 1;
            }
            new MapMaster<SPELL_GROUP, Integer>().crop(map, crop, true);
        } else {
            use = hero.getIntParam(PARAMS.DIVINATION_USE_LAST);
            if (use != 0) {
                crop = map.size() - use;
            } else {
                crop = hero.getIntParam(PARAMS.DIVINATION_CROP_LAST);
            }
            if (crop > 0) {
                if (crop >= map.size()) {
                    crop = map.size() - 1;
                }
                new MapMaster<SPELL_GROUP, Integer>().crop(map, crop, false);
            }

        }
        return map;

    }

    private static Comparator<? super ObjType> getComparator() {
        return new Comparator<ObjType>() {

            @Override
            public int compare(ObjType o1, ObjType o2) {
                Integer sd1 = o1.getIntParam(PARAMS.SPELL_DIFFICULTY);
                Integer sd2 = o2.getIntParam(PARAMS.SPELL_DIFFICULTY);

                if (RandomWizard.roll(sd1, sd2)) {
                    return 1;
                }
                return -1;
            }
        };
    }

    private static boolean rollSpell(ObjType spellType) {
        int chance = getChanceForSpell(spellType);
        return RandomWizard.chance(chance);

    }

    private static int getChanceForSpell(ObjType spellType) {
        int index = ListMaster.getIndexString(spellGroups.keySet(),
         spellType.getProperty(G_PROPS.SPELL_GROUP), true);
        if (index <= -1) {
            return 0;
        }
        index++;

        // getOrCreate the group randomly, then a spell randomly from it!

        int weight = spellGroups.get(new EnumMaster<SPELL_GROUP>()
         .retrieveEnumConst(SPELL_GROUP.class,
          spellType.getProperty(G_PROPS.SPELL_GROUP)));

        return (Math.min(MAX_SPELL_DIVINE_CHANCE, spellPool.size()) / index); // poolsize?
    }

    public static boolean rollRemove(Spell spell) {
        int n1 = spell.getIntParam(PARAMS.SPELL_DIFFICULTY); // TODO ETERNAL
        // GRACE!
        int n2 = spell.getOwnerUnit().getIntParam(PARAMS.CHARISMA); // WILLPOWER?
        return RandomWizard.roll(n1, n2);
    }

}
