package main.game.meta;

import main.entity.Ref;
import main.entity.obj.DC_Obj;
import main.entity.type.ObjType;
import main.game.Game;
import main.game.player.Player;

public class Faction extends DC_Obj {

    public Faction(ObjType type, Player owner, Game game) {
        super(type, owner, game, new Ref(game));
    }

}