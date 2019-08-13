package eidolons.system.controls;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Launcher;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.logic.meta.igg.soul.SoulforceMaster;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.LordPanel;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.grid.GenericGridView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.libgdx.stage.GuiStage;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.OptionsWindow;
import eidolons.system.test.TestDialogMaster;
import main.content.enums.GenericEnums;
import main.entity.Ref;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/2/2017.
 */
public class GlobalController implements Controller {
    private static final boolean TEST_MODE = true;
    private static boolean controlPause;
    private boolean active;
    private String lastScript;

    public static void setControlPause(boolean controlPause) {
        GlobalController.controlPause = controlPause;
    }

    public static boolean getControlPause() {
        return controlPause;
    }

    public static void cellClicked(InputEvent event, float x, float y) {
        if (controlPause) {
//            if (Gdx.input.isCatchMenuKey()) TODO how to disable on drag?
            Eidolons.getGame().getLoop().setPaused(false);
            setControlPause(false);
        }
    }
    /*
        toggle dummy?

         */

    @Override
    public boolean keyDown(int keyCode) {

        if (controlPause) {
            Eidolons.getGame().getLoop().setPaused(false);
            setControlPause(false);
            return true;
        }
        if (TEST_MODE)
            if (CoreEngine.isIDE()) {
                try {
                    if (doTest(keyCode))
                        return false;
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            }

        switch (keyCode) {
            case Keys.ESCAPE:
                return escape();

            case Keys.ENTER:
                return enter();

            case Keys.SPACE:
                return space();

        }
        if (isDisabled())
            return false;

        switch (keyCode) {
            case Keys.F2:
                if (EidolonsGame.BRIDGE) {
                    if (CoreEngine.isIDE()) {
                        EidolonLord.lord.soulforceLost(50);
                    }
                    return false;
                }
                if (CoreEngine.isIDE())
                    LordPanel.getInstance().init();
                GuiEventManager.trigger(GuiEventType.TOGGLE_LORD_PANEL);
                return true;
            case Keys.F1:
                if (!CoreEngine.isIDE())
                    if (!EidolonsGame.isHqEnabled()) {
                        return false;
                    }
                if (ShadowMaster.isShadowAlive()) {
                    EUtils.showInfoText("Cannot do this now");
                    return false;
                }
                if (Eidolons.getGame().isBossFight()) {
                    EUtils.showInfoText("There is no time...");
                    return false;
                }
                HqMaster.toggleHqPanel();
                return true;
            case Keys.F4:
                if (CoreEngine.isIDE())
                    if (Eidolons.getScope() != SCOPE.MENU) {
                        Eidolons.exitToMenu();
                        return true;
                    }
                return false;


            case Keys.TAB:
                try {
                    return tab();
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
                return false;

        }
        return false;
    }

    private boolean isDisabled() {
        if (DialogueManager.isRunning()) {
            return true;
        }
        return false;
    }

    private boolean doTest(int keyCode) {
        switch (keyCode) {
            case Keys.F11:
                GuiEventManager.trigger(GuiEventType.BLACKOUT_AND_BACK);
                break;
            case Keys.F10:
                GuiEventManager.trigger(GuiEventType.SHOW_COMMENT_PORTRAIT, Eidolons.getMainHero(), "TEEEEST!");
                break;
            case Keys.F9:
                Eidolons.onNonGdxThread(()-> {
                String text = DialogMaster.inputText("Your script...", lastScript);
                if (!StringMaster.isEmpty(text)) {
                    lastScript = text;
//                    DialogueManager.afterDialogue();
                    try {
                        Eidolons.getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(text);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
                });
                return true;
            case Keys.F8:

                if (CoreEngine.isIDE()) {
                    EidolonLord.lord.soulforceGained(110);
                }
                PaleAspect.togglePale();
                EidolonsGame.BRIDGE = !EidolonsGame.BRIDGE;
                return true;
            case Keys.F6:
                new Thread(() -> IGG_Launcher.introBriefing(), " thread").start();

                return true;
            case Keys.F7:
                DC_Game.game.getMetaMaster().getDialogueManager().test();

                return true;
            case Keys.F3:
                new Thread(() -> {
                    DC_Game.game.getMetaMaster().getDefeatHandler().isEnded(true, true);
                }, " thread").start();

                return true;
            case Keys.F4:
                Eidolons.getMainHero().kill(Eidolons.getMainHero(), true, true);
//                WeaveMaster.openWeave();
                return true;
            case Keys.F5:
                new DealDamageEffect(new Formula("1000"), GenericEnums.DAMAGE_TYPE.PURE)
                        .apply(Ref.getSelfTargetingRefCopy(Eidolons.getMainHero()));

//                Eidolons.getMainHero().setParam(PARAMS.C_TOUGHNESS, 0);
                Eidolons.getMainHero().getGame().getManager().reset();
//                WeaveMaster.openWeave();
                return true;
            //            case Keys.F4: already implemented?
            //                Eidolons.exitToMenu();
            //                break;
        }
        return false;
    }

    private boolean space() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMaster.click(activeButton);
            activeButton = null;
            return true;
        }
        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().ok();
            return true;
        }
        return false;
    }

    private boolean enter() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMaster.click(activeButton);
            activeButton = null;
            return true;
        }

        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().ok();
            return true;
        }
        if (TownPanel.getActiveInstance() != null) {
            TownPanel.getActiveInstance().done();
        }
        if (Eidolons.getScope() == SCOPE.MENU) {
            MainMenu.getInstance().getHandler().handle(MAIN_MENU_ITEM.PLAY);
            return true;
        }
        return false;
    }

    static SmartButton activeButton;

    public static void setActiveButton(SmartButton activeButton) {
        GlobalController.activeButton = activeButton;
    }

    private boolean tab() {
        GridUnitView hovered = DungeonScreen.getInstance().getGridPanel().getHoverObj();
        GridCellContainer cell = (GridCellContainer) hovered.getParent();

        List<GenericGridView> list = new ArrayList<>(cell.getUnitViewsVisible());
        if (list.size() == 1)
            return false;
        SortMaster.sortByExpression(list, view -> view.hashCode());
        int index = list.indexOf(hovered);
        index++;
        if (list.size() <= index)
            index = 0;
        int finalIndex = index;
        Eidolons.onNonGdxThread(() -> {
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, hovered);
            GenericGridView newFocus = list.get(finalIndex);
            WaitMaster.WAIT(100);
            cell.popupUnitView(newFocus);
            WaitMaster.WAIT(100);
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, newFocus);
            WaitMaster.WAIT(100);
            GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, newFocus.getTooltip());
        });


        return true;
    }

    private boolean escape() {

        if (Cinematics.ON){
            return false;
        }

        DungeonScreen.getInstance().cameraStop(true);

        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMaster.click(activeButton);
            activeButton = null;
            return true;
        }

        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().cancel();
            return true;
        }
        if (DungeonScreen.getInstance().getGuiStage().isDialogueMode()) {
            DungeonScreen.getInstance().getGuiStage().dialogueDone();
            FileLogManager.streamMain("Dialogue escaped");
            return true;
        }
        if (DC_Game.game.getManager().isSelecting()
            //         DungeonScreen.getInstance().getGridPanel().isSelecting()
        ) {
            DungeonScreen.getInstance().getGridPanel().clearSelection();
            return true;
        }
        GuiStage guiStage = Eidolons.getScreen().getGuiStage();
        if (guiStage.getDraggedEntity() != null) {
            guiStage.setDraggedEntity(null);
            return true;
        }
        if (OptionsWindow.getInstance().isVisible()) {
            OptionsWindow.getInstance().forceClose();
            return true;
        }
        if (guiStage.closeDisplayed())
            return true;

        guiStage.getTooltips().getStackMaster().stackOff();

        guiStage.getGameMenu().open();
        return true;
    }

    @Override
    public boolean charTyped(char c) {
        if (active) {
            active = true;
            return true;
        }
        if (CoreEngine.isContentTestMode()) {
            if (TestDialogMaster.key(c))
                return false;
        }
        if (DungeonScreen.getInstance().isBlocked())
            return true;
        switch (c) {
            case 'i':
            case 'I':
                if (ExplorationMaster.isExplorationOn()) {
                    GuiEventManager.trigger(GuiEventType.TOGGLE_INVENTORY,
                            new InventoryDataSource(DC_Game.game.getLoop().getActiveUnit()));
                }
                break;
            case ' ':
                if (DungeonScreen.getInstance() == null)
                    return false;
                if (DungeonScreen.getInstance().isWaitingForInput())
                    return true;
                if (Eidolons.getScreen().getGuiStage().getDisplayedClosable()
                        instanceof Blocking)
                    return false;
                Eidolons.game.getLoop().togglePaused();
                //                Eidolons.game.getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.PAUSE);
                return true;
            case 'D':
                Eidolons.game.getDebugMaster().showDebugWindow();
                return true;
            case 'W': //TODO custom hotkeys
                Eidolons.game.getDungeonMaster().getExplorationMaster().getTimeMaster()
                        .playerWaits();
                return true;
            case 'O': {
                OptionsMaster.openMenu();
                break;
            }
            case 'S': {
                //                if (!Gdx.input.isKeyPressed(Keys.ALT_LEFT))
                //                    break;
                SpecialLogger.getInstance().writeLogs();
                break;
            }
        }

        return false;
    }
}
