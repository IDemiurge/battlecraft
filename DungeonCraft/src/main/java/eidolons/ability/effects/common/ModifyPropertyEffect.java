package eidolons.ability.effects.common;

import eidolons.ability.effects.DC_Effect;
import main.ability.effects.Effect;
import main.ability.effects.ResistibleEffect;
import main.content.ContentValsManager;
import main.content.values.properties.PROPERTY;
import main.data.XLinkedMap;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.auxiliary.Strings;

import java.util.List;
import java.util.Map;

public class ModifyPropertyEffect extends DC_Effect implements ResistibleEffect {
    Map<PROPERTY, List<Object>> map;
    private PROPERTY prop;
    private MOD_PROP_TYPE modtype;
    private String value;

    public ModifyPropertyEffect(String prop, MOD_PROP_TYPE modtype, String value) {
        this(ContentValsManager.getPROP(prop), modtype, value);
    }

    public ModifyPropertyEffect(PROPERTY prop, MOD_PROP_TYPE modtype, String value) {
        this.prop = prop;
        this.value = value.replace(Strings.AND_PROPERTY_SEPARATOR,
         Strings.CONTAINER_SEPARATOR);
        this.modtype = modtype;
    }

    @Override
    public void initLayer() {
        // setLayer(Effect.ZERO_LAYER);
        setLayer(Effect.BASE_LAYER);
        // if (modtype == MOD_PROP_TYPE.SET)
        // setLayer(Effect.SECOND_LAYER);
    }

    @Override
    public String getTooltip() {
        // TODO Auto-generated method stub
        return super.getTooltip();
    }

    @Override
    public String toString() {

        return "Property modifying effect (" + prop + " " + modtype + " " + value + "), Layer "
         + getLayer();
    }

    @Override
    public boolean applyThis() {
        map = new XLinkedMap<>();
        boolean result = true;
        Obj obj = ref.getObj(KEYS.TARGET);
        if (obj == null) {
            return false;
        }

        switch (modtype) {
            case ADD:
                result = obj.addProperty(prop, value);
                break;
            case REMOVE:
                result = obj.removeProperty(prop, value);
                break;
            case SET:
                obj.setProperty(prop, value);
                break;
            default:
                break;

        }

        return true;
    }

    public PROPERTY getProp() {
        return prop;
    }

    public void setProp(PROPERTY prop) {
        this.prop = prop;
    }

    public MOD_PROP_TYPE getModtype() {
        return modtype;
    }

    public void setModtype(MOD_PROP_TYPE modtype) {
        this.modtype = modtype;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
