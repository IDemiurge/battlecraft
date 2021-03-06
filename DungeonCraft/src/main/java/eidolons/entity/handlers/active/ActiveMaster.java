package eidolons.entity.handlers.active;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.core.game.DC_Game;
import main.entity.handlers.*;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveMaster extends EntityMaster<DC_ActiveObj> {
    public ActiveMaster(DC_ActiveObj entity) {
        super(entity);
    }

    @Override
    protected EntityInitializer<DC_ActiveObj> createInitializer() {
        return new ActiveObjInitializer(getEntity(), this);
    }

    @Override
    protected EntityChecker<DC_ActiveObj> createEntityChecker() {
        return new ActiveChecker(getEntity(), this);
    }

    @Override
    protected EntityResetter<DC_ActiveObj> createResetter() {
        return new ActiveResetter(getEntity(), this);
    }

    @Override
    protected EntityCalculator<DC_ActiveObj> createCalculator() {
        return new ActiveCalculator(getEntity(), this);
    }

    @Override
    protected Executor createHandler() {
        return new Executor(getEntity(), this);
    }

    @Override
    public Executor getHandler() {
        return (Executor) super.getHandler();
    }

    @Override
    protected EntityAnimator<DC_ActiveObj> createEntityAnimator() {
        return new EntityAnimator<>(getEntity(), this);
    }

    @Override
    protected EntityLogger<DC_ActiveObj> createEntityLogger() {
        return new ActiveLogger(getEntity(), this);
    }

    @Override
    public ActiveLogger getLogger() {
        return (ActiveLogger) super.getLogger();
    }

    @Override
    public ActiveObjInitializer getInitializer() {
        return (ActiveObjInitializer) super.getInitializer();
    }

    @Override
    public ActiveCalculator getCalculator() {
        return (ActiveCalculator) super.getCalculator();
    }

    @Override
    public ActiveChecker getChecker() {
        return (ActiveChecker) super.getChecker();
    }

    @Override
    public ActiveResetter getResetter() {
        return (ActiveResetter) super.getResetter();
    }

    @Override
    public DC_ActiveObj getEntity() {
        return super.getEntity();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }
}
