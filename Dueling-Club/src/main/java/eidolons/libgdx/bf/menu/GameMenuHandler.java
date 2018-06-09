package eidolons.libgdx.bf.menu;

import eidolons.game.core.Eidolons;
import eidolons.game.module.adventure.global.persist.Saver;
import eidolons.libgdx.bf.menu.GameMenu.GAME_MENU_ITEM;
import eidolons.system.text.HelpMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 11/24/2017.
 */
public class GameMenuHandler {
    private final GameMenu menu;

    public GameMenuHandler(GameMenu gameMenu) {
        menu = gameMenu;
    }

    public Boolean clicked(GAME_MENU_ITEM sub) {
        switch (sub) {
            case QUICK_HELP:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getHelpText());
                break;
            case MANUAL:
                GuiEventManager.trigger(GuiEventType.SHOW_MANUAL_PANEL,
                 HelpMaster.getHelpText());
                break;
            case HERO_INFO:
                GuiEventManager.trigger(GuiEventType.SHOW_TEXT_CENTERED,
                 HelpMaster.getWelcomeText());
                break;
            case OUTER_WORLD:
                //TODO save and send log!
                Eidolons.exitGame();
            case EXIT:
            case MAIN_MENU:
//                DC_Game.game.getBattleMaster().getOutcomeManager().next();
//                DC_Game.game.exit(true);
                Eidolons.  exitToMenu();
                return false;
            case RESTART:
                Eidolons.restart();
                break;
            case PASS_TIME:
                Eidolons.getGame().getDungeonMaster().getExplorationMaster()
                 .getTimeMaster().playerWaits();
                break;


            case SAVE:
                GameMenu.menuOpen = false;
                Saver.save();
                break;
            case RUN:
                GameMenu.menuOpen = false;
                GuiEventManager.trigger(GuiEventType.BATTLE_FINISHED);
                break;
            case RESUME:
                break;
            case OPTIONS:
//                OptionsMaster.openMenu();
                menu.openOptionsMenu();
//                GuiEventManager.trigger(GuiEventType.OPEN_OPTIONS, MainMenuStage.class);
                return null ;
            case INFO:
                break;
            case WEBSITE:
                break;
            case ABOUT:
                break;
            case LAUNCH_GAME:
                break;
        }
        return null;
    }
}
