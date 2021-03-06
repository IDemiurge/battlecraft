package libgdx.gui.panels.lord.sub.imbue;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.netherflame.main.death.ChainHero;
import eidolons.game.netherflame.main.soul.eidola.EidolonImbuer;
import libgdx.gui.panels.lord.LordPanel;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.headquarters.ValueTable;
import libgdx.gui.panels.headquarters.tabs.inv.ItemActor;
import libgdx.gui.tooltips.SmartClickListener;

import java.util.ArrayList;

public class ImbueItems extends TablePanelX {
    private final ImbuePanel imbuePanel;
    private DC_HeroItemObj selected;
    ArrayList<DC_HeroItemObj> items = new ArrayList<>();
    ScrollPane scrollPane;
    ValueTable<DC_HeroItemObj, ImbueItemActor  > table;

    public ImbueItems(ImbuePanel imbuePanel) {
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        this.imbuePanel = imbuePanel;

        table = new ValueTable<DC_HeroItemObj, ImbueItemActor>(8, 32) {
            @Override
            protected ImbueItemActor createElement(DC_HeroItemObj item) {
                 ImbueItemActor itemActor = new ImbueItemActor(item);

                itemActor.addListener(new SmartClickListener(itemActor) {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        setSelected(item);
                    }

                    @Override
                    protected void entered() {
                        super.entered();
                        //info
                    }
                });
                return itemActor;
            }

            @Override
            protected ImbueItemActor[] initActorArray() {
                return new ImbueItemActor[items.size()];
            }

            @Override
            protected DC_HeroItemObj[] initDataArray() {
                return items.toArray(new DC_HeroItemObj[0]);
            }

            @Override
            public float getMaxWidth() {
                return 592;
            }

            @Override
            public float getMaxHeight() {
                return 256;
            }

            @Override
            public float getWidth() {
                return 592;
            }

            @Override
            public float getHeight() {
                return 256;
            }
        };
//        add(new LabelX("Valid Items"));
        add((table));
//        add(scrollPane = new ScrollPaneX(table));

    }

    @Override
    public void updateAct(float delta) {
        LordPanel.LordDataSource data = (LordPanel.LordDataSource) getUserObject();
        items.clear();
        for (ChainHero chainHero : data.getChain().getHeroes()) {
            items.addAll(EidolonImbuer.getValidItems(chainHero.getUnit()));
        }

        table.updateAct(delta);
    }

    public void setSelected(DC_HeroItemObj selected) {
        this.selected = selected;
        imbuePanel.getItemInfo().setUserObject(selected);
//        imbuePanel.update();
//        GuiEventManager.trigger(GuiEventType.UPDATE_LORD_PANEL);
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
//        table.setX(0);
//        table.setY(0);
//        for (int i = 0; i < 500; i+=100) {
//            setY(i);
//            setX(0);
//            super.draw(batch, parentAlpha);
//            setY(0);
//            setX(i);
//            super.draw(batch, parentAlpha);
//        }
        super.draw(batch, parentAlpha);
    }

    public DC_HeroItemObj getSelected() {
        return selected;
    }


    private static class ImbueItemActor extends ItemActor {

        public ImbueItemActor(DC_HeroItemObj model) {
            super(model);
        }
    }
}
