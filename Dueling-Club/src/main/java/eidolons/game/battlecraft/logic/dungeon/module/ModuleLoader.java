package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.libgdx.screens.ScreenMaster;
import main.game.bf.BattleFieldManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;

import java.util.Set;
import java.util.stream.Collectors;

public class ModuleLoader extends DungeonHandler {

    private Module loading;
    private Module last;


    /*
    dispose of textures/...

    visual fx - blackout or so
    show loadscreen or whatnot
    fade music
    init ambi
    init music

    load assets
    create units/objects
    spawn encounters

    fade in
    zoom/cam
    step out
    init vfx
     */
    public ModuleLoader(DungeonMaster  master) {
        super(master);
        GuiEventManager.bind(GuiEventType.GRID_RESET, p -> {
            loadGdxGrid((Module) p.get());
        });
    }

    public void loadGdxGrid(Module module) {
        ScreenMaster.getScreen().moduleEntered(module,  getObjects(module));
    }

    private DequeImpl<BattleFieldObject> getObjects(Module module) {
        module.initObjects();
        Set<BattleFieldObject> set = game.getBfObjects().stream().filter(obj -> module.getCoordinatesSet().contains(obj.getCoordinates())).collect(Collectors.toSet());
        return new DequeImpl<>(set);
    }

    public void loadModuleFull(Module module){
        last = getMetaMaster().getModuleMaster()
                .getCurrent();
        loading = module;
        initLogicalGrid(module);
//        freeResources();
//        initTransitFx();
//        showLoadScreen();
//        initMusic();
//        loadAssets(module);
        GuiEventManager.trigger(GuiEventType.GRID_RESET, module);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, Eidolons.getMainHero());
    }

    public void loadInitial() {
        initLogicalGrid(getModule());
    }
    private void initLogicalGrid(Module module) {
        game.enterModule(module);
        game.getDungeonMaster().getBuilder().initModuleSize(module);
        BattleFieldManager.entered(module.getId() );
        if (!CoreEngine.isLevelEditor()) {
            spawnEncounters(module);
        }
        //TODO
//        PositionMaster.initDistancesCache(loading.getId(),
//                getModule().getEffectiveWidth(),
//                getModule().getEffectiveHeight());
    }

    private void spawnEncounters(Module module) {
        getBattleMaster().getEncounterSpawner().spawnEncounters(
                module.getEncounters());
    }

    private void initMusic() {
        AmbienceDataSource.AMBIENCE_TEMPLATE template = loading.getVfx();
        GuiEventManager.trigger(GuiEventType.UPDATE_AMBIENCE, template);
    }


    private void loadAssets(Module module) {
        String descriptors = module.getData().getValue(LevelStructure.MODULE_VALUE.assets);
        for (String path : ContainerUtils.openContainer(descriptors, ",")) {
//            BfObjEnums.SPRITES.valueOf()
            boolean ktx=false;
            Assets.loadSprite(path, false, ktx);
        }
    }

    private void freeResources() {
        //can we really just dispose of all textures?
        /*
        perhaps we can create a descriptor map from objects, then cross it with current one to figure out
        what we can dispose

        To be fair, such fervor is only needed for either low-end pcs or fat levels!
        Maybe we should dispose only when the module is '2 steps behind'?
        Do we support 'return to module'?
        Not always!

        IDEA: trigger addAsset when init() objects for grid
         */
        GuiEventManager.trigger(GuiEventType.DISPOSE_SCOPE);
    }

}
