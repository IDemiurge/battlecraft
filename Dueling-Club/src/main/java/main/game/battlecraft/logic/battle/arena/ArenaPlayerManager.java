package main.game.battlecraft.logic.battle.arena;

import main.game.battlecraft.logic.battle.BattleMaster;
import main.game.battlecraft.logic.battle.PlayerManager;

/**
 * Created by JustMe on 5/7/2017.
 */
public class ArenaPlayerManager extends PlayerManager<ArenaBattle> {


    public ArenaPlayerManager(BattleMaster<ArenaBattle> master) {
        super(master);
    }

    public void initializePlayers() {
//        player = new DC_Player(PLAYER_NAME, Color.WHITE, true); // emblem?
//        try {
//            player.setHero_type(getPlayerHeroName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        enemyPlayer = new DC_Player(ENEMY_NAME, Color.BLACK, false); // emblem?
//        Player.ENEMY = enemyPlayer;
    }


}
