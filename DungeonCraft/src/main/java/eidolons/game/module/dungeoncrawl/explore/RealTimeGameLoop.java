package eidolons.game.module.dungeoncrawl.explore;

/**
 * Created by JustMe on 9/9/2017.
 */
public interface RealTimeGameLoop {
    void end();

    void act(float delta);

    void setVisualLock(boolean b);
}
