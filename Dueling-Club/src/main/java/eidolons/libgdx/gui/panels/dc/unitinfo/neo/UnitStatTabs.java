package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.hero.HqScrolledValuePanel;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqMasteryTable;

/**
 * Created by JustMe on 11/15/2018.
 */
public class UnitStatTabs extends TabbedPanel {
    private final HqScrolledValuePanel scrolledValuePanel;
    private HqAttributeTable attributeTable;
    private HqMasteryTable masteryTable;


    public UnitStatTabs(float w, float h) {
        final float height = h * getHeightCoef();
        final float width = w * getWidthCoef();

        scrolledValuePanel = new HqScrolledValuePanel(
         GdxMaster.adjustWidth(width), GdxMaster.adjustHeight(height));

        masteryTable = new HqMasteryTable();
        attributeTable = new HqAttributeTable();
        attributeTable.setEditable(false);
        attributeTable.setSize(width,height);
        masteryTable.setSize(width,height);

        addTab(attributeTable, "Attributes");
        addTab(masteryTable, "Mastery Scores");
        addTab(scrolledValuePanel, "Stats");
        //        main.setSize(width,height);
        tabSelected("Attributes");
        setSize(GdxMaster.adjustWidth(width), GdxMaster.adjustHeight(height));
    }

    private float getWidthCoef() {
        return 1.15f;
    }

    private float getHeightCoef() {
        return 1f;
    }
    protected TablePanelX createContentsTable() {
        return new TablePanelX<>(
         getWidth()*getWidthCoef(),getHeight()* getHeightCoef());
    }
    @Override
    protected Cell setDisplayedActor(Actor actor) {
        Cell cell = super.setDisplayedActor(actor);
//            if (actor == scrolledValuePanel)
            {
                scrolledValuePanel.setUserObject(getUserObject());
                scrolledValuePanel.updateAct(0);
            }
        return cell;
    }

    @Override
    protected int getDefaultAlignment() {
        return Align.bottomLeft;
    }


    @Override
    protected Cell createContentsCell() {
        return super.createContentsCell().padTop(20);
    }


    @Override
    protected Cell<TextButton> addTabActor(TextButton b) {
        return super.addTabActor(b);
    }
}

