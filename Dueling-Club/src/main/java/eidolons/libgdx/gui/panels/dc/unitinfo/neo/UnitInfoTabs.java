package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.hero.HqParamPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqScrollPropPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqScrolledValuePanel;

/**
 * Created by JustMe on 11/14/2018.
 */
public class UnitInfoTabs extends TabbedPanel {
    private final UnitDescriptionPanel descriptionPanel;
    private HqScrollPropPanel propPanel;
    private final TablePanelX main;


    public UnitInfoTabs(float w, float h) {
        main = new TablePanelX();
        main.add(new ArmorPanel()).row();
        main.add(new HqParamPanel(true)).row();
        main.add(new HqParamPanel(false)).row();
        final float height = h * getHeightCoef();
        final float width = w * getWidthCoef();
        descriptionPanel = new UnitDescriptionPanel() {
            @Override
            protected float getDefaultHeight() {
                return (height);
            }

            @Override
            protected float getDefaultWidth() {
                return (width);
            }
        };
        descriptionPanel.setSize(
                (width), (height));

        propPanel = new HqScrollPropPanel(
                (width), (height));

        addTab(main, "Main");
        addTab(descriptionPanel, "Lore");
//        addTab(propPanel, "Other");

        scrolledValuePanel = new HqScrolledValuePanel(
                GdxMaster.adjustWidth(width), GdxMaster.adjustHeight(height));

        addTab(scrolledValuePanel, "Stats");
//        main.setSize(width,height);
        descriptionPanel.setSize(width, height);
        tabSelected("Main");
        setSize(GdxMaster.adjustWidth(width), (height));
    }

    private final HqScrolledValuePanel scrolledValuePanel;


    private float getWidthCoef() {
        return 1.15f;
    }

    private float getHeightCoef() {
        return 1f;
    }

    protected TablePanelX createContentsTable() {
        return new TablePanelX<>(
                getWidth() * getWidthCoef(), getHeight() * getHeightCoef());
    }

    @Override
    protected Cell setDisplayedActor(Actor actor) {
        Cell cell = super.setDisplayedActor(actor);
        if (actor == descriptionPanel) {// ye know  scrolledValuePanel) {
            cell.setActorY(0);
            cell.setActorX(0);
            cell.padTop(100);
            return cell;
        }
        if (actor == scrolledValuePanel) {
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
