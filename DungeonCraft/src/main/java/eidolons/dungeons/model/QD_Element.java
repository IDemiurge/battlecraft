package eidolons.game.netherflame.dungeons.model;

import java.util.List;

public abstract class QD_Element<T extends QD_Element> {

    public abstract Class getWrappedClass();

    List<T> children;
}
