package main.game.battlecraft.logic.meta.universal;

import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/7/2017.
 */
public abstract class MetaGameMaster<E extends MetaGame> {

    protected    String data;
   protected PartyManager<E> partyManager;
    protected  MetaInitializer<E> initializer;
    protected  ShopManager<E> shopManager;
    protected  MetaDataManager<E> metaDataManager;
    protected  E metaGame;
    protected   DC_Game game; //<? extends DC_Game>
    //    PrecombatManager<E> precombatManager;
//    AfterCombatManager<E> afterCombatManager;

    public MetaGameMaster(String data) {
        this.data=data;
        partyManager=createPartyManager();
         initializer=createMetaInitializer ();
         shopManager=createShopManager();
          metaDataManager=createMetaDataManager();
    }


    //from data? if save
    protected abstract  DC_Game createGame();
    protected abstract PartyManager<E> createPartyManager();

    protected abstract MetaDataManager<E> createMetaDataManager();

    protected abstract ShopManager<E> createShopManager();

    protected abstract MetaInitializer<E> createMetaInitializer();


    public void init(){
//        shopManager.init();
//        metaDataManager.init();
        game=createGame();
        metaGame=  initializer.initMetaGame(data);
        partyManager.initPlayerParty();
    }

    public void gameStarted(){
        partyManager.gameStarted();
//        getGame().getDataKeeper().setDungeonData(new DungeonData(getMetaGame()));

    }

    public String getData() {
        return data;
    }

    public E getMetaGame() {
        return metaGame;
    }

    public DC_Game getGame() {
        return game;
    }

    public PartyManager<E> getPartyManager() {
        return partyManager;
    }

    public MetaInitializer<E> getInitializer() {
        return initializer;
    }

    public ShopManager<E> getShopManager() {
        return shopManager;
    }

    public MetaDataManager<E> getMetaDataManager() {
        return metaDataManager;
    }
}