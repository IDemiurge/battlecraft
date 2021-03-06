package libgdx.gui.panels.headquarters.creation.selection;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import libgdx.StyleHolder;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import libgdx.gui.panels.headquarters.creation.selection.HcDeitySelectionPanel.HcDeityElement;
import libgdx.gui.generic.btn.ButtonStyled;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/4/2018.
 */
public class HcDeitySelectionPanel extends SelectionTable<HcDeityElement> {
    DescriptionScroll description;

    public HcDeitySelectionPanel() {
        super(1, 4);
        EUtils.bind(GuiEventType.HC_DEITY_ASPECT_CHOSEN, p -> {
            setUserObject(p.get());
        });
    }

    @Override
    protected GuiEventType getEvent() {
        return null;
    }

    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.DEITY;
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        if (getUserObject() instanceof GenericEnums.ASPECT){
            List<ObjType> types = DataManager.getFilteredTypes(DC_TYPE.DEITIES, getUserObject().toString(), G_PROPS.ASPECT);
            return types.stream().map(type -> new SelectableItemData(type.getName(), type))
                    .collect(Collectors.toList()).toArray(new SelectableItemData[types.size()]);
        } else {
            Unit hero = (Unit) getUserObject();
            String deities = hero.getProperty(G_PROPS.DEITY);
            deities= StringMaster.replace(true, deities, ";", Strings.OR);
            List<ObjType> types = DataManager.getFilteredTypes(DC_TYPE.DEITIES, getUserObject().toString(), G_PROPS.ASPECT);
            return types.stream().map(type -> new SelectableItemData(type.getName(), type))
                    .collect(Collectors.toList()).toArray(new SelectableItemData[types.size()]);
        }
    }

    private void clicked(Entity entity) {
        HeroCreationMaster.modified(getProperty(), entity.getName());
        description.setUserObject(new SelectableItemData(entity));
    }

    @Override
    protected Class<?> getUserObjectClass() {
        return String.class;
    }

    @Override
    public void init() {
        super.init();
        add(description = new DescriptionScroll());
        for (HcDeityElement item : actors) {
            if (item != null)
                item.getButton().setDisabled(isBlocked(item.getDeity()));
        }
    }

    private boolean isBlocked(Entity deity) {
        return !StringMaster.contains(HeroCreationMaster.getModel().getBackgroundType().getProperty(G_PROPS.DEITY),
         deity.getName(), true, true);
    }

    @Override
    protected HcDeityElement createElement(SelectableItemData datum) {
        return new HcDeityElement(datum.getEntity());
    }

    @Override
    protected HcDeityElement[] initActorArray() {
        return new HcDeityElement[size];
    }

    public class HcDeityElement extends TablePanelX {
        private final Entity deity;
        private final SmartTextButton button;

        public HcDeityElement(Entity entity) {
            deity = entity;
            add(new ImageContainer(entity.getEmblemPath()));

            add(button = new SmartTextButton( entity.getName(),
             StyleHolder.getHqTextButtonStyle(ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT,  26), () -> clicked(entity), ButtonStyled.STD_BUTTON.MENU));

            add(new ImageContainer(entity.getEmblemPath()));
        }

        public Entity getDeity() {
            return deity;
        }

        public SmartTextButton getButton() {
            return button;
        }
    }

}
