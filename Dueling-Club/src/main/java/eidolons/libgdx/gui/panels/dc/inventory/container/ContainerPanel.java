package eidolons.libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.ability.InventoryTransactionManager;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.AdjustingVerticalGroup;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerPanel extends TablePanel implements Blocking {

    protected TablePanel inventory;
    protected InventorySlotsPanel containerSlotsPanel;
    protected FadeImageContainer portrait;
    protected FadeImageContainer container;
    protected SmartButton mainButton;
    protected LabelX heroLabel;
    protected LabelX containerLabel;
    protected ValueContainer weightLabel;
    protected ValueContainer goldLabel;
    protected ValueContainer weightLabel2;
    protected ValueContainer goldLabel2;

    public ContainerPanel() {
        init();
    }

    protected void init() {
        setBackground(new TextureRegionDrawable(new TextureRegion(
         TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.SAURON,
          BACKGROUND_NINE_PATCH.PATTERN, getDefaultWidth(), getDefaultHeight())
        )));

        pad(NINE_PATCH_PADDING.SAURON);

        inventory = createInventory();
        containerSlotsPanel = new InventorySlotsPanel(getContainerRowCount(),
         getContainerColumnCount());

        initUpperTable().row();
        initMiddleTable().row();
        initLowerTable().row();

        if (getGuiEvent() != null)
            GuiEventManager.bind(getGuiEvent(), (obj) -> {
                final Pair<InventoryDataSource, ContainerDataSource> param = (Pair<InventoryDataSource, ContainerDataSource>) obj.get();
                if (param == null) {
                    close();
                } else {
                    open();
                    update(param.getKey(), param.getValue());
                }
            });

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                event.stop();
                return true;
            }
        });
    }

    protected TablePanel createInventory() {
        return new InventorySlotsPanel(getInvRowCount(), getInvColumnCount());
    }

    @Override
    public void clear() {

    }

    protected int getInvRowCount() {
        return 4;
    }

    protected int getInvColumnCount() {
        return 6;
    }

    protected int getContainerRowCount() {
        return 4;
    }

    protected int getContainerColumnCount() {
        return 6;
    }

    protected int getDefaultHeight() {
        return (int) GdxMaster.adjustSizeBySquareRoot(600);
    }

    protected int getDefaultWidth() {
        return (int) GdxMaster.adjustSizeBySquareRoot(900);
    }


    protected Cell initLowerTable() {
        TablePanelX lower = new TablePanelX<>();
        TablePanelX lowerLeft = new TablePanelX<>();
        TablePanelX lowerRight = new TablePanelX<>();

        lowerLeft.add(weightLabel = new ValueContainer(
         TextureCache.getOrCreateR(Images.TINY_CHEST), "")).left().fillX().growX()
         .row();
        weightLabel.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));
        lowerLeft.add(goldLabel = new ValueContainer(
         TextureCache.getOrCreateR(Images.TINY_GOLD), "")).left().fillX().growX();
        goldLabel.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));


        lowerRight.add(weightLabel2 = new ValueContainer(
         TextureCache.getOrCreateR(Images.TINY_CHEST), "")).left().fillX().growX()
         .row();
        weightLabel2.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));
        lowerRight.add(goldLabel2 = new ValueContainer(
         TextureCache.getOrCreateR(Images.TINY_GOLD), "")).left().fillX().growX();
        goldLabel2.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 18));

        lower.add(lowerLeft).left();
        if (!isButtonRequired())
            lower.add(new AdjustingVerticalGroup(400, 0.5f)).growX();
        lower.add(lowerRight).right();
        if (isButtonRequired()) {
            lower.row();
            mainButton = new SmartButton(
             getButtonText(), StyleHolder.getHqTextButtonStyle(20), () -> mainButton());
            lower.add(mainButton).colspan(2).right().fillX().growX();
        }
        return addElement(lower).pad(0, 30, 20, 20);
    }

    protected Cell initMiddleTable() {
        TablePanelX middle = new TablePanelX<>();

        TablePanelX filters = new TablePanelX<>();
        for (ITEM_FILTERS filter : ITEM_FILTERS.values()) {
            filters.add(new SmartButton(getButtonStyle(filter),
             () -> {
                 try {
                     applyFilter(filter);
                 } catch (Exception e) {
                     main.system.ExceptionMaster.printStackTrace(e);
                 }
             })).row();
        }

        middle.addElement(inventory).left();
        middle.addElement(filters).center().width(GdxMaster.adjustWidth(100));
        middle.addElement(containerSlotsPanel).right();

        return addElement(middle).pad(0, 30, 20, 20);
    }

    protected Cell initUpperTable() {
        TablePanelX upper = new TablePanelX<>();
        TablePanelX upperLeft = new TablePanelX<>();
        TablePanelX upperRight = new TablePanelX<>();

        upper.add(upperLeft);
        upper.addEmpty((int) GdxMaster.adjustWidth(100), 100);
        upper.add(upperRight);

        upperLeft.add(portrait = new FadeImageContainer()).left().left().top();
        upperLeft.add(heroLabel = new LabelX("", 20)).top().right().colspan(2);

        upperRight.add(containerLabel = new LabelX("", 20)).top().left().colspan(2);
        upperRight.add(container = new FadeImageContainer()).right().top();

        return addElement(upper).pad(0, 30, 20, 20);
    }

    protected boolean isButtonRequired() {
        return true;
    }

    protected void mainButton() {
        takeAll();
    }

    protected void update(InventoryDataSource invData, ContainerDataSource containerData) {
        Pair<InventoryDataSource, ContainerDataSource> param = new ImmutablePair<>(invData, containerData);
        setUserObject(containerData);
        inventory.setUserObject(invData);

        if (containerSlotsPanel.getListeners().size > 0)
            inventory.addListener(containerSlotsPanel.getListeners().first());

        updateUpperTable(param);
        updateLowerTable(param);
    }

    protected void updateUpperTable(Pair<InventoryDataSource, ? extends ContainerDataSource> param) {
        ContainerDataSource dataSource = param.getValue();
        container.setImage(StringMaster.getAppendedImageFile(
         dataSource.getHandler().getContainerImagePath(), ContainerMaster.OPEN));
        containerLabel.setText(dataSource.getHandler().getContainerName());
        portrait.setImage(param.getKey().getUnit().getImagePath());
        heroLabel.setText(param.getKey().getUnit().getName());
    }

    protected void updateLowerTable(Pair<InventoryDataSource, ? extends ContainerDataSource> param) {
        weightLabel.setValueText(param.getKey().getWeightInfo());
        goldLabel.setValueText(param.getKey().getGoldInfo());
        weightLabel2.setValueText(param.getValue().getWeightInfo());
        goldLabel2.setValueText(param.getValue().getGoldInfo());
    }

    protected String getButtonText() {
        return "Take All";
    }

    protected EventType getGuiEvent() {
        return SHOW_LOOT_PANEL;
    }

    protected void applyFilter(ITEM_FILTERS filter) {

        InventoryDataSource dataSource =
         (InventoryDataSource) getUserObject();
        ContainerDataSource dataSource2 = (ContainerDataSource)
         containerSlotsPanel.getUserObject();
        final Pair<InventoryDataSource, ContainerDataSource> param =
         new ImmutablePair<>(
          dataSource, dataSource2);

        dataSource.setFilter(filter);
        dataSource2.setFilter(filter);

        setUserObject(dataSource);
    }

    protected STD_BUTTON getButtonStyle(ITEM_FILTERS filter) {
        return new EnumMaster<STD_BUTTON>().retrieveEnumConst(STD_BUTTON.class, "ITEM_" + filter.name());
    }

    protected void takeAll() {
        ContainerDataSource dataSource =
         (ContainerDataSource) containerSlotsPanel.getUserObject();
        dataSource.getHandler().takeAllClicked();
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) super.getStage();
    }

    public void close() {
        getStageWithClosable().closeClosable(this);
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, true);


    }


    public enum ITEM_FILTERS {
        ALL,
        WEAPONS,
        ARMOR,
        USABLE,
        JEWELRY,
        QUEST
    }
}
