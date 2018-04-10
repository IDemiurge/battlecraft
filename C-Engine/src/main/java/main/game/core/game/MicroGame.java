package main.game.core.game;

import main.ability.effects.Effect;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.bf.BattleFieldGrid;
import main.game.bf.BattleFieldManager;
import main.game.bf.Coordinates;
import main.game.bf.GraveyardManager;
import main.game.core.state.MicroGameState;
import main.game.logic.battle.player.Player;

import java.util.Collection;
import java.util.Set;

public abstract class MicroGame extends Game {

    protected boolean host;
    protected Player player1;
    protected Player player2;
    protected String gameName;
    protected BattleFieldManager battleFieldManager;
    protected String unitData1;
    protected String unitData2;

    public MicroGame() {
        this.gameName = "Battlecraft Game";
    }

    public MicroGame(Player player1, Player player2, String gamename, String objData,
                     String objData2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameName = gamename;
        this.unitData1 = objData;
        this.unitData2 = objData2;

    }

    @Override
    public MicroGameState getState() {
        return (MicroGameState) state;
    }

    public void setState(MicroGameState state) {
        this.state = state;

    }

    public Player getPlayer(boolean me) {
        if (me) {
            return player1;
        }
        return player2;
    }

    public String getName() {
        return gameName;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public BattleFieldManager getBattleFieldManager() {
        return battleFieldManager;
    }

    public MicroObj createUnit(ObjType type, int x, int y, Player owner) {
        return manager.createUnit(type, x, y, owner);
    }


    public MicroObj createSpell(ObjType type, Player player, Ref ref) {
        return manager.createSpell(type, player, ref);
    }

    public MicroObj createSpell(ObjType type, MicroObj owner, Ref ref) {
        return manager.createSpell(type, owner, ref);
    }

    public MicroObj createUnit(ObjType type, Coordinates c, Player owner) {
        return manager.createUnit(type, c, owner);
    }

    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        return manager.createUnit(type, x, y, owner, ref);
    }

    public MicroObj createBuff(BuffType type, Obj active, Player player, Ref ref, Effect effect,
                               int duration, Condition retainCondition) {
        return manager.createBuff(type, active, player, ref, effect, duration, retainCondition);
    }

    public GraveyardManager getGraveyardManager() {
        return null;
    }


    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }


    public void remove(Obj obj) {
        state.removeObject(obj.getId());

    }

    public Collection<? extends Obj> getCellsForCoordinates(Set<Coordinates> coordinates) {
        return null;
    }


    public abstract BattleFieldGrid getGrid();
}
