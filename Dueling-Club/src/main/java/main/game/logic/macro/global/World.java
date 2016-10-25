package main.game.logic.macro.global;

import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.entity.MacroObj;
import main.game.logic.macro.map.Region;
import main.system.auxiliary.ListMaster;

import java.util.List;

public class World extends MacroObj {
    private List<Region> regions;

    /*
     * date, what else? some global stuff about campaign... scheduled events,
     * outcomes...
     */
    public World(MacroGame game, ObjType type, MacroRef ref) {
        super(game, type, ref);
    }

    @Override
    public void setRef(Ref ref) {
        ref.setID("world", getId());
        this.ref = ref;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public Region getRegion(String name) {
        return new ListMaster<Region>().findType(name, regions);
    }

}
