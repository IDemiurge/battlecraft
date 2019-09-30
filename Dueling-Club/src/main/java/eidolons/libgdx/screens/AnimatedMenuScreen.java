package eidolons.libgdx.screens;


import eidolons.game.battlecraft.logic.meta.igg.story.IggActChoicePanel;
import eidolons.game.battlecraft.logic.meta.igg.story.brief.BriefingData;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.menu.selection.rng.RngSelectionPanel;
import eidolons.libgdx.gui.menu.selection.saves.SaveSelectionPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioSelectionPanel;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.video.VideoMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.graphics.RESOLUTION;
import main.content.DC_TYPE;
import main.entity.Entity;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */
public class AnimatedMenuScreen extends ScreenWithVideoLoader {

    MainMenu mainMenu;

    public AnimatedMenuScreen() {
        initMenu();
    }

    protected void initMenu() {

        GuiEventManager.bind(GuiEventType.BRIEFING_START, p -> {
            mainMenu.setVisible(false);
        });
        GuiEventManager.bind(GuiEventType.BRIEFING_FINISHED, p -> {
            mainMenu.setVisible(true);
        });
//        getOverlayStage().clear(); TODO igg demo fix why needed??
        mainMenu = MainMenu.getInstance();
        mainMenu.setVisible(true);
        getOverlayStage().addActor(mainMenu);
        mainMenu.setPosition(
                GdxMaster.centerWidth(mainMenu)
                , GdxMaster.centerHeight(mainMenu));
//        if (CoreEngine.isIDE()) {
//            if (Eidolons.getResolution() != RESOLUTION._1920x1080) {
//                mainMenu.setPosition(0, (float) (Eidolons.getResolutionDimensions().getHeight() - mainMenu.getHeight() - 100));
//            }
//        }
    }

    @Override
    public void backToLoader() {
        initMenu();
        super.backToLoader();
        getOverlayStage().setActive(true);
    }

    protected boolean isWaitForInput() {
        return false;
    }

    @Override
    public void loadDone(EventCallbackParam param) {
        super.loadDone(param);
//        if (CoreEngine.isLiteLaunch())
//            return;
//        Assets.preloadUI();
//        Assets.preloadHeroes();
//        setLoadingAtlases(true);
//        TextureCache.getInstance().loadAtlases();
//        GdxMaster.setLoadingCursor();
        //TODO animated screen? or TIPS
    }

    @Override
    protected void afterLoad() {
        getOverlayStage().setActive(true);
    }

    protected void back() {
        if (mainMenu != null)
            mainMenu.setVisible(true);
    }

    @Override
    protected SelectionPanel createSelectionPanel(EventCallbackParam p) {
        List<? extends Entity> list = (List<? extends Entity>) p.get();
        if (isLoadGame(list)) {
            return new SaveSelectionPanel(() -> list) {
                @Override
                public void closed(Object selection) {
                    if (selection == null) {
                        if (mainMenu != null)
                            mainMenu.setVisible(true);
                    }
                    super.closed(selection);
                }
            };
        }
        if (isDemoScenario(list)) {
            return new IggActChoicePanel(() -> (List<? extends Entity>) p.get()) {
                @Override
                public void closed(Object selection) {
                    if (selection == null) {
                        if (mainMenu != null)
                            mainMenu.setVisible(true);
                    }
                    super.closed(selection);
                }
            };
        }
        if (isRngScenario(list)) {
            return new RngSelectionPanel(() -> (List<? extends Entity>) p.get()) {
                @Override
                public void closed(Object selection) {
                    if (selection == null) {
                        if (mainMenu != null)
                            mainMenu.setVisible(true);
                    }
                    super.closed(selection);
                }
            };
        }
        return new ScenarioSelectionPanel(() -> (List<? extends Entity>) p.get()) {
            @Override
            public void closed(Object selection) {
                if (selection == null) {
                    if (mainMenu != null)
                        mainMenu.setVisible(true);
                }
                super.closed(selection);
            }
        };
    }

    private boolean isDemoScenario(List<? extends Entity> list) {
        return list.get(0).getGroupingKey().equalsIgnoreCase("Demo");
    }

    private boolean isLoadGame(List<? extends Entity> p) {
        return p.get(0).getOBJ_TYPE_ENUM() == DC_TYPE.CHARS;
    }

    private boolean isRngScenario(List<? extends Entity> p) {
        return p.get(0).getGroupingKey().equalsIgnoreCase("Random");
    }

    @Override
    protected void preLoad() {

        super.preLoad();
//        if (isvo)

        try {
            Assets.preloadMenu();
            setLoadingAtlases(true);
            GdxMaster.setLoadingCursor();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        getOverlayStage().setActive(false);
    }


    @Override
    protected void renderLoader(float delta) {

        if (!MusicMaster.getInstance().isRunning())
        if (VideoMaster.player!=null || !isVideoEnabled())
            try {
                MusicMaster.getInstance().startLoop();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
//        background.draw(getBatch(), 1);

        super.renderLoader(delta);
    }

    @Override
    protected boolean isLoadingWithVideo() {
        return true;
    }


}
