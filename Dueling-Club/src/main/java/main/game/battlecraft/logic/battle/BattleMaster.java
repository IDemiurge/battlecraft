package main.game.battlecraft.logic.battle;

import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.game.battlecraft.logic.dungeon.Positioner;
import main.game.battlecraft.logic.dungeon.Spawner;
import main.game.battlecraft.logic.meta.MetaGameMaster;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public abstract class BattleMaster<E extends Battle> {

    protected E battle;
    protected BattleOptionManager optionManager;
    protected BattleStatManager statManager;
    protected BattleConstructor constructor;
    protected BattleOutcomeManager outcomeManager;
    protected PlayerManager playerManager;
    private DC_Game game;

    public BattleMaster(DC_Game game) {
        this.game = game;
        this.battle = createBattle();
        this.optionManager = createOptionManager();
        this.statManager = createStatManager();
        this.constructor = createConstructor();
        this.outcomeManager = createOutcomeManager();
        this.playerManager = createPlayerManager();
    }

    public void startGame() {
//        getConstructor().init();
    }


    protected abstract E createBattle();

    protected abstract PlayerManager<E> createPlayerManager();

    protected abstract BattleOutcomeManager<E> createOutcomeManager();

    protected abstract BattleConstructor<E> createConstructor();

    protected abstract BattleStatManager<E> createStatManager();

    protected abstract BattleOptionManager<E> createOptionManager(); //<E>

    public BattleOptionManager getOptionManager() {
        return optionManager;
    }

    public BattleStatManager getStatManager() {
        return statManager;
    }

    public BattleConstructor getConstructor() {
        return constructor;
    }

    public BattleOutcomeManager getOutcomeManager() {
        return outcomeManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public E getBattle() {
        return battle;
    }

    public DungeonMaster getDungeonMaster() {
        return game.getDungeonMaster();
    }

    public MetaGameMaster getMetaMaster() {
        return game.getMetaMaster();
    }

    public Positioner getPositioner() {
        return getDungeonMaster().getPositioner();
    }

    public Spawner getSpawner() {
        return getDungeonMaster().getSpawner();
    }

    public Dungeon getDungeon() {
        return getDungeonMaster().getDungeonWrapper().getDungeon();
    }

    public DC_Game getGame() {
        return game;
    }

}