package eidolons.ability.effects.common;

import main.content.enums.entity.UnitEnums.STATUS;
import main.content.values.properties.G_PROPS;

public class ModifyStatusEffect extends ModifyPropertyEffect {

    public ModifyStatusEffect(MOD_PROP_TYPE modtype, STATUS status) {
        super(G_PROPS.STATUS, modtype, status.name());
    }

    @Override
    public boolean applyThis() {
        return super.applyThis();
    }
}
