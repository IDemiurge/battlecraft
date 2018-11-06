package eidolons.libgdx.gui.panels.quest;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestProgressPanel extends TablePanelX {
    public static final int WIDTH = 315;
    public QuestProgressPanel() {
        super(GdxMaster.adjustWidth(WIDTH), GdxMaster.adjustHeight(800));
        top();
        left();
        add(new LabelX("Quests: ", StyleHolder.getHqLabelStyle(20))).center().row();

        GuiEventManager.bind(GuiEventType.QUEST_UPDATE,
         p -> {
             for (Actor actor : getChildren()) {
                 if (actor instanceof QuestElement) {
                     ((QuestElement) actor).setUpdateRequired(true);
                 }
                 }
         });

        GuiEventManager.bind(GuiEventType.QUEST_ENDED,
         p -> {
             updateRequired = true;

         });
        GuiEventManager.bind(GuiEventType.QUEST_STARTED,
         p -> {
             DungeonQuest quest = (DungeonQuest) p.get();
             for (Actor actor : getChildren()) {
                 if (actor instanceof QuestElement) {
                     if (((QuestElement) actor).quest==quest) {
                         return;
                     }
                 }
             }
             QuestElement element = new QuestElement(quest);
             add(element);
             row();
             element.fadeIn();
         });
        GuiEventManager.bind(GuiEventType.GAME_STARTED,
         p -> {
            update();
         });
    }

    @Override
    public void update() {
        GuiEventManager.trigger(GuiEventType.QUESTS_UPDATE_REQUIRED);
    }
}