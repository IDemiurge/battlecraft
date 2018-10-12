package eidolons.game.module.dungeoncrawl.quest;

import eidolons.libgdx.gui.menu.selection.quest.QuestSelectionPanel;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.enums.meta.QuestEnums;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/5/2018.
 * <p>
 * Quest should be chosen before RNG/Scenario is launched, so we can adjust spawning/...
 */
public class QuestSelector extends QuestHandler {


    public QuestSelector(QuestMaster questMaster) {
        super(questMaster);
    }

    public void displayDungeonQuests() {
        //for macro
    }

    public void selectDungeonQuests() {

        String filter = QuestEnums.QUEST_GROUP.SCENARIO + StringMaster.OR + QuestEnums.QUEST_GROUP.RNG;

        List<ObjType> filtered = DataManager.getFilteredTypes(MACRO_OBJ_TYPES.QUEST,
         filter, MACRO_PROPS.QUEST_GROUP);

        GuiEventManager.trigger(GuiEventType.SHOW_QUEST_SELECTION, filtered);
    }

    public List<DungeonQuest> chooseQuests(int n) {
        List<DungeonQuest> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            selectDungeonQuests();

            String result =
             (CoreEngine.isFastMode()) ? DataManager.getRandomType(MACRO_OBJ_TYPES.QUEST).getName()
              : (String) WaitMaster.waitForInput(QuestSelectionPanel.WAIT_OPERATION);
            if (result == null) {
                return list;
            }
            ObjType type = DataManager.getType(result.toString(), MACRO_OBJ_TYPES.QUEST);
            DungeonQuest quest = master.getCreator().create(type);
            list.add(quest);
        }
        if (CoreEngine.isFastMode())
            GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL, null);
        return list;
    }
}
