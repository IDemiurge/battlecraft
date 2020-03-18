package main.level_editor.backend.functions.io;

import eidolons.libgdx.gui.utils.FileChooserX;
import main.data.filesys.PathFinder;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.screen.LE_Screen;
import main.system.auxiliary.data.FileManager;

public class LE_DataHandler extends LE_Handler {
    private Campaign campaign;

    public LE_DataHandler(LE_Manager manager) {
        super(manager);
    }

    public void openCampaign(String path) {

    }
    public void saveFloor() {
        String path = getDefaultSavePath(getFloor());
        String contents = LE_XmlMaster.toXml(getFloor());
        FileManager.write(contents, path);
    }

    private String getDefaultSavePath(Floor floor) {
        String prefix = "";
        if (campaign == null) {
            if (LevelEditor.TEST_MODE) {
                prefix = "test/";
            } else {
                prefix = "scenario/";
                prefix +=  floor.getGame().getDungeon().getGroup()+"/";
            }
        } else {
//            TODO campaign
        }
        return PathFinder.getDungeonLevelFolder() +prefix+ floor.getName()+ ".xml";
    }

    public void open(String floorName) {
        Campaign campaign=getDataHandler().getCampaign();
//        campaign.getFloorPath(floorName);
//        Floor floor = getDataHandler().loadFloor(floorName);
//        LevelEditor.floorSelected(floor);

//        ScreenData data = new ScreenData(SCREEN_TYPE.EDITOR, floorName);
//        data.setParam(new EventCallbackParam(floor));
//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, data);

    }

    public void activateFloorTab(Floor floor) {

    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void openFloor() {
        String file = FileChooserX.chooseFile(PathFinder.getDungeonLevelFolder(),
                "xml", LE_Screen.getInstance().getGuiStage());

        FloorManager.addFloor();
    }
}