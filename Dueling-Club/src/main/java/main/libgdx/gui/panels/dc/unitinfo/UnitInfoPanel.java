package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class UnitInfoPanel extends Container<TablePanel> {
    private Actor outside;

    public UnitInfoPanel() {
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/background.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(Gdx.graphics.getHeight());

        outside = new Actor();
        outside.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        outside.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UnitInfoPanel.this.setVisible(false);
                outside.setTouchable(Touchable.disabled);
                return false;
            }
        });

        TablePanel tablePanel = new TablePanel();
        fill().left().bottom().pad(20);
        setActor(tablePanel);

        initInnerPanels();

        initListeners();

        setVisible(false);
        setClip(true);
        setTouchable(Touchable.enabled);
    }

    private void initInnerPanels() {
        TablePanel left = new TablePanel();
        MainWeaponPanel mainWeaponPanel = new MainWeaponPanel();
        left.addElement(mainWeaponPanel).height(184);
        left.row();

        ResourcePanel resourcePanel = new ResourcePanel();
        left.addElement(resourcePanel).minHeight(210);
        left.row();

        MainParamPanel mainParamPanel = new MainParamPanel();
        left.addElement(mainParamPanel).minHeight(214);
        left.row();

        EffectAndAbilitiesPanel effectAndAbilitiesPanel = new EffectAndAbilitiesPanel();
        left.addElement(effectAndAbilitiesPanel);
        left.row();

        addElement(left).maxWidth(360);

        TablePanel mid = new TablePanel();

        AvatarPanel avatarPanel = new AvatarPanel();
        mid.addElement(avatarPanel);
        mid.row();

        CounterAndActionPointsPanel pointsPanel = new CounterAndActionPointsPanel();
        mid.addElement(pointsPanel);
        mid.row();

        mid.addElement(getPanelSeparator()).expand().center();
        mid.row();

        MainAttributesPanel attributesPanel = new MainAttributesPanel();
        mid.addElement(attributesPanel).expand().center();
        mid.row();

        ArmorPanel armorPanel = new ArmorPanel();
        mid.addElement(armorPanel);
        mid.row();

        mid.addElement(getPanelSeparator()).expand().center();
        mid.row();

        ResistInfoTabsPanel resistTabs = new ResistInfoTabsPanel();
        mid.addElement(resistTabs);
        mid.row();
        addElement(mid);

        TablePanel right = new TablePanel();

        OffWeaponPanel offWeaponPanel = new OffWeaponPanel();
        right.add(offWeaponPanel).height(184).top();
        right.row();

        StatsTabsPanel statsTabsPanel = new StatsTabsPanel();
        right.addElement(statsTabsPanel).grow().fill(1, 0).top();
        right.row();

        right.addElement(null);
        addElement(right).maxWidth(360);
    }

    private <T extends Actor> Cell addElement(T panel) {
        return getActor().addElement(panel).expand().left().bottom();
    }

    private void initListeners() {
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

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                //System.out.println("mouse exit form");
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam(null));
            }
        });

        GuiEventManager.bind(GuiEventType.SHOW_UNIT_INFO_PANEL, (obj) -> {
            setUserObject(obj.get());
            outside.setTouchable(Touchable.enabled);
        });
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        //todo replace this with child.forEach
        getChildren().forEach(ch -> ch.setUserObject(userObject));
        if (userObject != null) {
            setVisible(true);
        }
    }

    private Image getPanelSeparator() {
        return new Image(TextureCache.getOrCreateR("/UI/components/infopanel/panel_separator.png"));
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            actor = outside;
        }
        return actor;
    }
}
