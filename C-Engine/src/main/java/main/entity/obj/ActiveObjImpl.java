package main.entity.obj;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.Game;
import main.game.player.Player;

public abstract class ActiveObjImpl extends Obj implements ActiveObj {

    public ActiveObjImpl(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean activate(Ref ref) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean resolve() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean activate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInterrupted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void addDynamicValues() {
        // TODO Auto-generated method stub

    }

    @Override
    public void toBase() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetPercentages() {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterEffects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

    }

    @Override
    public void clicked() {
        // TODO Auto-generated method stub

    }

}