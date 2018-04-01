package eidolons.game.module.dungeoncrawl.explore;

import eidolons.client.cc.logic.party.Party;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.ai.AggroMaster;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.anims.AnimationConstructor;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;

/**
 * Created by JustMe on 8/2/2017.
 */
public class ExplorationMaster {
    static boolean explorationOn;
    private static boolean testMode;
    private static boolean realTimePaused;
    private static boolean waiting;
    DC_Game game;
    ExplorationAiMaster aiMaster;
    ExplorationTimeMaster timeMaster;
    private ExploreEnemyPartyMaster enemyPartyMaster;
    private ExplorePartyMaster partyMaster;
    private ExploreCleaner cleaner;
    private ExplorationResetHandler resetter;
    private DungeonCrawler crawler;
    private ExplorationActionHandler actionHandler;

    public ExplorationMaster(DC_Game game) {
        this.game = game;
        aiMaster = new ExplorationAiMaster(this);
        timeMaster = new ExplorationTimeMaster(this);
        resetter = new ExplorationResetHandler(this);
        crawler = new DungeonCrawler(this);
        cleaner = new ExploreCleaner(this);
        actionHandler = new ExplorationActionHandler(this);
        partyMaster = new ExplorePartyMaster(this);
        enemyPartyMaster = new ExploreEnemyPartyMaster(this);
    }

    public static boolean isWaiting() {
        return waiting;
    }

    public static void setWaiting(boolean waiting) {
        ExplorationMaster.waiting = waiting;
    }

    public static boolean isRealTimePaused() {
        return realTimePaused;
    }

    public static void setRealTimePaused(boolean realTimePaused) {
        ExplorationMaster.realTimePaused = realTimePaused;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static void setTestMode(boolean testMode) {
        ExplorationMaster.testMode = testMode;
    }

    public static boolean isExplorationSupported(DC_Game game) {
//        if (testMode)
//            return true;
//        if (game.getGameMode() == GAME_MODES.ARENA)
//            return false;
//            return true;
//        }
//TODO only if disabled by <?>>
        return true;
    }

    public static boolean isExplorationOn() {
        return explorationOn;
    }

    public ExplorePartyMaster getPartyMaster() {
        return partyMaster;
    }

    public ExploreEnemyPartyMaster getEnemyPartyMaster() {
        return enemyPartyMaster;
    }

    public void switchExplorationMode(boolean on) {
        if (explorationOn == on)
            return;
        explorationOn = on;
        explorationToggled();
    }

    private ExplorerUnit createExplorerUnit(Party party) {
        ExplorerUnit e = new ExplorerUnit(null);

        return e;
    }

    private void explorationToggled() {
        //speed up resets?
        //cache unit state?
        if (isExplorationOn()) {
            //TODO quick-fix
            cleaner.cleanUpAfterBattle();
            game.getLogManager().logBattleEnds();
            getResetter().setFirstResetDone(false);

            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.ATMO);
        } else {
            game.getLogManager().logBattleStarts();
            if (AnimationConstructor.isPreconstructEnemiesOnCombatStart())
                AggroMaster.getLastAggroGroup().forEach(unit -> {
                    AnimMaster.getInstance().getConstructor().preconstructAll(unit);
                });
            getResetter().setFirstResetDone(false);
            try {
                MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.BATTLE);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        getResetter().setFirstResetDone(false);
        game.startGameLoop();

//        game.getManager().reset();
        //exceptions: triggers, scripts,

    }

    public DC_Game getGame() {
        return game;
    }

    public ExplorationAiMaster getAiMaster() {
        return aiMaster;
    }

    public ExplorationTimeMaster getTimeMaster() {
        return timeMaster;
    }

    public ExplorationResetHandler getResetter() {
        return resetter;
    }

    public DungeonCrawler getCrawler() {
        return crawler;
    }

    public ExploreCleaner getCleaner() {
        return cleaner;
    }

    public ExplorationActionHandler getActionHandler() {
        return actionHandler;
    }

    public void init() {
        explorationOn = getCrawler().checkExplorationDefault();
    }

    public ExploreGameLoop getLoop() {
        if (!explorationOn)
            return null;
        return (ExploreGameLoop) game.getLoop();
    }
}