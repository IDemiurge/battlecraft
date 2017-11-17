package main.libgdx.gui.panels.dc.inventory.container;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.ability.InventoryTransactionManager;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.SHOW_LOOT_PANEL;

/**
 * Created by JustMe on 11/16/2017.
 */
public class ContainerPanel extends  TablePanel{

    private final Cell<Actor> takeAllButton;
    private InventorySlotsPanel inventorySlotsPanel;
    private InventorySlotsPanel containerSlotsPanel;

    public ContainerPanel( ) {

        TextureRegion textureRegion = new TextureRegion(getOrCreateR("UI/components/inventory_background.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        inventorySlotsPanel = new InventorySlotsPanel();
        containerSlotsPanel = new InventorySlotsPanel();

        addElement(inventorySlotsPanel)
         .height(340)
         .pad(20, 20, 0, 20)
         .top().expand(1, 0);
        row();
        addElement(containerSlotsPanel)
         .height(340)
         .pad(20, 20, 0, 20)
         .top().expand(1, 0);
        initListeners();

        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 20, 20, 20);


        takeAllButton    = lower.addElement(new TextButton("Take All",
         StyleHolder.getDefaultTextButtonStyle()))
         .fill(false).expand(0, 0).right()
         .pad(20, 10, 20, 10).size(50, 50);
    }

    @Override
    public void clear() {

    }

    private void initListeners() {
        GuiEventManager.bind(SHOW_LOOT_PANEL, (obj) -> {
            final Pair<InventoryDataSource, ContainerDataSource> param = (Pair<InventoryDataSource, ContainerDataSource>) obj.get();
            if (param == null) {
                close();
            } else {
                setVisible(true);
                inventorySlotsPanel. setUserObject(param.getKey());
                containerSlotsPanel. setUserObject(param.getValue());
                if (containerSlotsPanel.getListeners().size>0)
                    inventorySlotsPanel.addListener(containerSlotsPanel.getListeners().first());

                TextButton button = (TextButton) takeAllButton.getActor();
                final ContainerDataSource source =   param.getValue();
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        source.getHandler().takeAllClicked();
                    }  }
                );

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

    public void close() {
        WaitMaster.receiveInput(InventoryTransactionManager.OPERATION, true);
        setVisible(false);
    }

    @Override
    public void updateAct(float delta) {
        clear();
        super.updateAct(delta);



    }

    @Override
    public void afterUpdateAct(float delta) {
        clear();

    }
}
