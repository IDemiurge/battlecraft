package main.level_editor.sim;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.DC_BattleFieldManager;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_BattleFieldGrid;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.ScenarioGame;
import eidolons.game.core.master.ObjCreator;
import eidolons.system.hotkey.DC_KeyManager;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.battle.player.Player;
import main.level_editor.gui.stage.LE_KeyManager;
import main.level_editor.metadata.object.LE_IdManager;
import main.level_editor.sim.impl.LE_DungeonMaster;

import java.util.Map;

public class LE_GameSim extends ScenarioGame {
    private static final String DUMMY = "Base Human Unit";
    private final LE_IdManager simIdManager;
    private Unit dummyPC;
    private DC_Player ally;
    private DC_Player enemy;
    /*

     */

    public LE_GameSim(LE_MetaMaster metaMaster) {
        super(metaMaster);
        simIdManager = new LE_IdManager();
        setSimulation(true);
    }

    @Override
    public DC_Player getPlayer(boolean me) {
        return me ? ally : enemy;
    }

    @Override
    protected DC_KeyManager createKeyManager() {
        return new LE_KeyManager(getManager());
    }

    @Override
    protected DC_GameManager createGameManager() {
        return new DC_GameManager(getState(), this) {
            @Override
            public Unit getActiveObj() {
                return dummyPC;
            }

            @Override
            protected boolean isResetDisabled() {
                return true;
            }

            @Override
            public void init() {
                super.init();
                ally = new DC_Player();
                enemy = new DC_Player();

                setObjCreator(new ObjCreator(getGame()) {
                    @Override
                    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
                        MicroObj obj = super.createUnit(type, x, y, owner, ref);
                        if (type.getName().equalsIgnoreCase(DUMMY)) {
                            return obj;
                        }
                        Integer id = simIdManager.objectCreated(obj);
                        getMetaMaster().getDungeonMaster().getLayerManager().addToCurrent(id, obj);
                        return obj;
                    }
                });
            }
        };
    }

    public Integer getId(BattleFieldObject bfObj) {
        return simIdManager.getId(bfObj);
    }

    public void setObjIdMap(Map<Integer, Obj> objIdMap) {
        simIdManager.setObjIds(objIdMap);
    }

    @Override
    public void removed(Obj obj) {
        Integer id = simIdManager.objectRemoved(obj);
        getMetaMaster().getDungeonMaster().getLayerManager().removeFromCurrent(id, obj);
    }

    @Override
    public void softRemove(BattleFieldObject obj) {
        super.softRemove(obj);
        removed(obj);
    }


    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void battleInit() {
        Coordinates c = Coordinates.getMiddleCoordinate(FACING_DIRECTION.NONE);
        dummyPC = (Unit) createUnit(DataManager.getType(DUMMY, DC_TYPE.UNITS), c.x, c.y, getPlayer(true),
                new Ref(LE_GameSim.this));
        Eidolons.setMainHero(dummyPC);
        dungeonMaster.init();
        grid = new DC_BattleFieldGrid(getDungeon());
        battleFieldManager = new DC_BattleFieldManager(this);
//        dummyPC = createUnit()
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public void start(boolean first) {
        dungeonMaster.gameStarted();

        try {
            getManager().reset();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    @Override
    protected DungeonMaster createDungeonMaster() {
        return new LE_DungeonMaster(this);
    }

    public LE_IdManager getSimIdManager() {
        return simIdManager;
    }

}
