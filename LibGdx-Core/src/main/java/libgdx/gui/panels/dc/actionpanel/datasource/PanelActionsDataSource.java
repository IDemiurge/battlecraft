package libgdx.gui.panels.dc.actionpanel.datasource;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.spaces.ActiveSpace;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.unit.Unit;
import libgdx.gui.UiMaster;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.dc.actionpanel.ActionContainer;
import libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import libgdx.gui.panels.dc.unitinfo.datasource.*;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PanelActionsDataSource implements
        ActiveQuickSlotsDataSource, UnitActionsDataSource, ActiveSpaceDataSource,
        EffectsAndAbilitiesSource, ResourceSource,
        MainWeaponDataSource<ValueContainer>, OffWeaponDataSource {

    private final Unit unit;

    private final UnitDataSource unitDataSource;

    public PanelActionsDataSource(Unit unit) {
        this.unit = unit;
        unitDataSource = new UnitDataSource(unit);
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public String getParam(PARAMS param) {
        switch (param) {
            case FOCUS:
                return getFocus();
            case TOUGHNESS:
                return getToughness();
            case ENDURANCE:
                return getEndurance();
            case ESSENCE:
                return getEssence();
        }
        return null;
    }


    @Override
    public List<ValueContainer> getQuickSlotActions() {
        final DequeImpl<DC_QuickItemObj> items = unit.getQuickItems();
        if (items == null)
            return (List<ValueContainer>)
                    ListMaster.fillWithNullElements(new ArrayList<ValueContainer>(), unit.getRemainingQuickSlots());
        List<ValueContainer> list = items.stream()
                .map((DC_QuickItemObj key) -> {
                    boolean valid = key.getActive().canBeManuallyActivated();
                    final ValueContainer valueContainer = new ActionContainer(
                            UiMaster.getBottomQuickItemIconSize(),
                            valid,
                            (key.getImagePath()),
                            key::invokeClicked
                    );
                    ActionCostTooltip tooltip = new ActionCostTooltip(key.getActive());
                    valueContainer.addListener(tooltip.getController());
                    return valueContainer;
                })
                .collect(Collectors.toList());

        // Now via special button!
        //        ObjType type = DataManager.getType(StringMaster.getWellFormattedString(STD_SPEC_ACTIONS.Use_Inventory.name()), DC_TYPE.ACTIONS);
        //        TextureRegion invTexture = TextureCache.getOrCreateR(type.getImagePath());
        //        DC_UnitAction action = unit.getAction(StringMaster.getWellFormattedString(STD_SPEC_ACTIONS.Use_Inventory.name()));
        //        if (action == null)
        //            return list;
        //        boolean valid = action.canBeManuallyActivated();
        //        ValueContainer invButton = new ValueContainer(valid, invTexture, () -> {
        //            action.clicked();
        //        });
        //        list.add(invButton);

        for (int i = 0; i < unit.getRemainingQuickSlots() - 1; i++) {
            list.add(null);
        }

        return list;
    }

    @Override
    public List<ValueContainer> getDisplayedActions() {
        List<ValueContainer> list = new ArrayList<>();
        list.addAll(getActions(ACTION_TYPE.MODE));
        list.addAll(getActions(ACTION_TYPE.SPECIAL_ACTION));
        return list;
    }

    public List<ValueContainer> getActions(ACTION_TYPE type) {
        if (unit.getActionMap().get(type) == null) {
            return new ArrayList<>();
        }
        return unit.getActionMap().get(type).stream()
                .map(getActiveObjValueContainerFunction(UiMaster.getIconSize()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ValueContainer> getActives() {
        List<DC_ActiveObj> actives =
                // Flags.isSafeMode() ? new LinkedList<>(unit.getActionMap().get(ACTION_TYPE.STANDARD)) :
                        unit.getActiveSpaces().getCurrent().getDisplayedActives();
        return actives.stream()
                .map(getActiveObjValueContainerFunction(UiMaster.getBottomActiveIconSize()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<ActiveSpace.ActiveSpaceMeta, List<ValueContainer>> getActiveSpacesExpanded() {
        List<ActiveSpace> spaces = unit.getActiveSpaces().getVisible();
        Map<ActiveSpace.ActiveSpaceMeta, List<ValueContainer>> map= new LinkedHashMap<>();
        for (ActiveSpace space : spaces) {
            List<DC_ActiveObj> actives = space.getDisplayedActives();
            List<ValueContainer> containers = actives.stream()
                    .map(getActiveObjValueContainerFunction(UiMaster.getBottomActiveIconSize()))
                    .collect(Collectors.toList());
            ActiveSpace.ActiveSpaceMeta meta = unit.getGame().getActionManager().
                    getSpaceManager().createMeta(space);
            map.put(meta, containers);
        }
       return map;
    }
    private Function<DC_ActiveObj, ValueContainer> getActiveObjValueContainerFunction(
            int size) {
        return el -> {
            return ActionContainerFactory.getValueContainer(el, size);
        };
    }


    @Override
    public String getToughness() {
        return unitDataSource.getToughness();
    }

    @Override
    public String getEndurance() {
        return unitDataSource.getEndurance();
    }

    @Override
    public String getEssence() {
        return unitDataSource.getEssence();
    }

    @Override
    public String getFocus() {
        return unitDataSource.getFocus();
    }

    @Override
    public ValueContainer getOffWeapon() {
        return unitDataSource.getOffWeapon();
    }

    @Override
    public List<ValueContainer> getOffWeaponDetailInfo() {
        return unitDataSource.getOffWeaponDetailInfo();
    }

    @Override
    public ValueContainer getNaturalOffWeapon() {
        return unitDataSource.getNaturalOffWeapon();
    }

    @Override
    public List<ValueContainer> getNaturalOffWeaponDetailInfo() {
        return unitDataSource.getNaturalOffWeaponDetailInfo();
    }

    @Override
    public ValueContainer getMainWeapon() {
        return null;
    }

    @Override
    public List<ValueContainer> getMainWeaponDetailInfo() {
        return null;
    }

    @Override
    public ValueContainer getNaturalMainWeapon() {
        return null;
    }

    @Override
    public List<ValueContainer> getNaturalMainWeaponDetailInfo() {
        return null;
    }

    @Override
    public List<ValueContainer> getBuffs(boolean body) {
        return unitDataSource.getBuffs(body);
    }

    @Override
    public List<ValueContainer> getAbilities(boolean body) {
        return unitDataSource.getAbilities(body);
    }
}
