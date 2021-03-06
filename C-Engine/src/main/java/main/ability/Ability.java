package main.ability;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.data.xml.XmlConvertable;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Referred;
import main.entity.obj.Active;

/**
 * @author JustMe
 */
public interface Ability extends Referred, Interruptable,
 Active, XmlConvertable {

    boolean resolve();

    void addEffect(Effect effect);

    Effects getEffects();

    void setEffects(Effects effects);

    Targeting getTargeting();

    void setTargeting(Targeting targeting);

    boolean activatedOn(Ref ref);

    void setForceTargeting(boolean forceTargeting);

    boolean isForcePresetTargeting();

    void setForcePresetTargeting(boolean forcePresetTargeting);


    void setXml(String stringFromXML);
}
