package eidolons.game.battlecraft.logic.dungeon.test;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.LocationInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.*;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestDungeonMaster extends DungeonMaster<TestDungeon> {

    public TestDungeonMaster(DC_Game game) {
        super(game);
//        setChooseLevel(CHOOSE_LEVEL);
//        // setDungeonPath(DEFAULT_DUNGEON_LEVEL);
//        presetDungeonType = getDEFAULT_DUNGEON();
//        dungeonPath = DEFAULT_DUNGEON_PATH;
    }

    @Override
    protected DungeonBuilder createBuilder() {
        if (isLocation())
            return new LocationBuilder(this);
        return new TestDungeonBuilder<>(this);
    }

    @Override
    protected DungeonInitializer createInitializer() {
        if (isLocation())
            return new LocationInitializer(this);
        return new TestDungeonInitializer(this);
    }
    private boolean isLocation() {
        return  true;
    }

    @Override
    protected FacingAdjuster<TestDungeon> createFacingAdjuster() {
        return new FacingAdjuster<>(this);
    }

    @Override
    protected Positioner createPositioner() {
        return new TestPositioner(this);
    }

    @Override
    protected Spawner createSpawner() {
        return new TestSpawner(this);
    }

}
