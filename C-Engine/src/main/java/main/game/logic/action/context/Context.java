package main.game.logic.action.context;

import main.entity.Ref;
import main.entity.obj.Obj;
import main.system.launch.Flags;

import java.util.Map;

/**
 * Created by JustMe on 3/23/2017.
 */
public class Context extends Ref {
    Obj source;
    Obj target;
    Map<IdKey, Integer> idMap;

    public Context(Obj source, Obj target) {
        super(source);
        this.target = target;
        if (target != null) {
            setTarget(target.getId());
        }
        this.source = source;
    }

    public Context(Ref ref) {
        setClone(true);
        cloneMaps(ref);
        setPlayer(ref.getPlayer());
        setEvent(ref.event);
        setGroup(ref.getGroup());
        setBase(ref.base);
        setGame(ref.game);
        setEffect(ref.getEffect());
        setTriggered(ref.isTriggered());
        setDebug(ref.isDebug());
        if (Flags.isPhaseAnimsOn())
            setAnimationActive(ref.getAnimationActive());

        target = getTargetObj();
        source = getSourceObj();
    }

    public void setSource(Obj source) {
        this.source = source;
        if (source != null)
            setTarget(source.getId());
    }

    public void setTarget(Obj target) {
        this.target = target;
        if (target != null)
            setTarget(target.getId());
    }

    @Override
    public Obj getTargetObj() {
        if (target == null) {
            return super.getTargetObj();
        }
        return target;
    }


    public enum IdKey {

        TARGET,
        SOURCE,
        BASIS,
        ACTIVE,
        SPELL,
        WEAPON,
        ARMOR,
        OFFHAND,
        BUFF,
        SUMMONER,
        SUMMONED,
        PAYEE,
        ITEM,
        SKILL,
        PARTY,
        INFO,
        AMMO,
        RANGED,
    }
}
