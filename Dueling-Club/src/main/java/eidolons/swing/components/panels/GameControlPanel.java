package eidolons.swing.components.panels;

import eidolons.client.dc.Launcher.VIEWS;
import eidolons.swing.builders.DC_Builder;
import eidolons.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;

public class GameControlPanel extends G_Panel {
    private VIEWS prevView;

    public GameControlPanel(DC_Builder bfBuilder) {
        super("fill, ins 1 17 0 0");
        setVisuals(VISUALS.CONTROL_PANEL_HORIZONTAL);
        addButton(GAME_CONTROL.CHAR);
        addButton(GAME_CONTROL.LOG);
        addMinimapButton(true);
        addMinimapButton(false);
        addButton(GAME_CONTROL.OPTIONS);
    }

    private void addButton(final GAME_CONTROL c) {
        add(new CustomButton(c.getVISUALS()) {
            public void handleClick() {
                buttonClicked(c);
            }
        });
    }

    private void addMinimapButton(final boolean viewMode) {
        add(new CustomButton(viewMode ? VISUALS.MAP_VIEW_BUTTON : VISUALS.MINIMAP_GRID_BUTTON) {
            public void handleClick() {
                minimapButtonClicked(viewMode);
            }
        });

    }

    public void buttonClicked(GAME_CONTROL button) {
        switch (button) {
            case CHAR:
                break;
            case LOG:
                break;
            case OPTIONS:
                break;

        }
    }

    public void minimapButtonClicked(boolean viewMode) {

//            List<String> convertToNameIntList = StringMaster.toNameList(true,
//                    DungeonMaster.getDungeons());
//            String result = new ListChooser(SELECTION_MODE.SINGLE, convertToNameIntList,
//                    DC_TYPE.DUNGEONS).choose();
//            DC_SoundMaster.playStandardSound(STD_SOUNDS.SLING);
//            for (Dungeon d : DungeonMaster.getDungeons()) {
//                if (d.getType().getName().equals(result)) {
//                    DungeonMaster.goToDungeon(d);
//                    return;
//                }
//            }// bfBuilder.toggleDungeonsPanel();

        // if (Launcher.getView() != VIEWS.MINI_MAP) {
        // prevView = Launcher.getView();
        // Launcher.setView(VIEWS.MINI_MAP);
        // } else {
        // Launcher.resetView(prevView);
        // }
    }

    public enum GAME_CONTROL {
        CHAR(VISUALS.CHAR_BUTTON),
        LOG(VISUALS.LOG_BUTTON),
        MAP_VIEW(VISUALS.MAP_VIEW_BUTTON),
        MINIMAP_GRID(VISUALS.MINIMAP_GRID_BUTTON),
        OPTIONS(VISUALS.OPTIONS_BUTTON);
        private VISUALS V;

        GAME_CONTROL(VISUALS v) {
            this.V = (v);
        }

        public VISUALS getVISUALS() {
            return V;
        }

    }
}