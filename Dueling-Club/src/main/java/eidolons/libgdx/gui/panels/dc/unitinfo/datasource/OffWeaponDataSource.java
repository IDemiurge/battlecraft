package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.libgdx.gui.generic.ValueContainer;

import java.util.List;

public interface OffWeaponDataSource {
    ValueContainer getOffWeapon();

    List<ValueContainer> getOffWeaponDetailInfo();

    ValueContainer getNaturalOffWeapon();

    List<ValueContainer> getNaturalOffWeaponDetailInfo();
}
