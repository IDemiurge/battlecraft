package main.level_editor;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.system.audio.MusicMaster;
import main.content.DC_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.level_editor.functions.model.LE_DataModel;
import main.level_editor.gui.screen.LE_Screen;
import main.level_editor.sim.LE_GameSim;
import main.level_editor.sim.LE_MetaMaster;
import main.level_editor.struct.boss.BossDungeon;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.function.Supplier;

public class LevelEditor {
    public static Window.WindowStyle windowStyle;
    private static Floor current;
    private static boolean campaignMode;
    private static Campaign campaign;
    private static BossDungeon dungeon;

    public static void main(String[] args) {
        DC_Engine.systemInit(false);
        new EditorApp(args).start();

    }

    public static void initFloor(LE_MetaMaster meta) {
        LE_GameSim game = meta.init();

        DC_Engine.gameStartInit();

        game.initAndStart();
        String name = meta.getMetaDataManager().getDataPath();
        Floor floor = new Floor(name, game, game.getDungeon());
        floorSelected(floor);
    }

    public static void newFloorSelected(String name) {
        String path =!campaignMode? name : getRootPath() + name;
        if (campaignMode){
//            dungeon.getFloorPath(name);
        } else {
        }
        LE_MetaMaster meta = new LE_MetaMaster(path);
        initFloor(meta);
    }

    private static String getRootPath() {
        return new StrPathBuilder().build(PathFinder.getDungeonLevelFolder(),
                campaign.getName(), dungeon.getName());
    }

    public static void floorSelected(Floor floor) {
        current = floor;
        SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.CLICK_ACTIVATE);
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.EDITOR, floor));
    }

    public static Integer getId(BattleFieldObject bfObj) {
        return getCurrent().getGame().getId(bfObj);
    }

    public static LE_DataModel getModel() {
        return getCurrent().getManager().getModelManager().getModel();
    }

    public static Window.WindowStyle getWindowStyle() {
        if (windowStyle == null) {
            windowStyle = new Window.WindowStyle(StyleHolder.getHqLabelStyle(
                    GdxMaster.adjustFontSize(20)).font
                    , StyleHolder.getDefaultLabelStyle().fontColor,
                    new NinePatchDrawable(NinePatchFactory.getLightDecorPanelFilledDrawable()));
        }
        return windowStyle;
    }

    public static Floor getCurrent() {
        return current;
    }


    public static void welcome(
            String toOpen  ) {
        //welcome screen?..
//        editorSettings = FileManager.readFile(getSettingsPath());
//        toOpen = getOpenDefault();
        if (toOpen == null) {
            toOpen = promptOpen();
        }
        LE_MetaMaster meta;
        if (toOpen.contains("campaigns")) {
            campaignMode = true;
            ObjType type = DataManager.getType(toOpen, MACRO_OBJ_TYPES.CAMPAIGN);
            campaign = new Campaign(type);
            dungeon = campaign.getCurrentDungeon();
            meta = new LE_MetaMaster(campaign);
            //use data soruce / update() paradigm?     VS cached LE_Screens, eh?
        } else {
            meta = new LE_MetaMaster(toOpen);
        }
        initFloor(meta);
    }

    private static String promptOpen() {
        GuiEventManager.trigger(GuiEventType.LE_CHOOSE_FILE);
        return (String) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.SELECTION);
    }
}