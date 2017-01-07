package main.libgdx.gui.dialog;

import main.ability.Abilities;
import main.entity.obj.DC_HeroObj;
import main.libgdx.gui.layout.LayoutParser;
import main.libgdx.gui.layout.LayoutParser.LAYOUT;
import main.libgdx.gui.panels.sub.Comp;
import main.libgdx.gui.panels.sub.Container;
import main.libgdx.gui.panels.sub.EntityContainer;
import main.swing.generic.components.G_Panel.VISUALS;

/**
 * Created by JustMe on 1/5/2017.
 */
public class InfoDialog extends Dialog {
    private LayoutParser parser;
    Container top;
    Comp portrait;
    Comp portraitBg;
    Container fxAndAbils;
    Container armor;
    Container armorTraits;
    Container mainWeapon;
    Container offWeapon;
    Container attributes;
    Container dynamicParams;
    Container points;
    Container mainParams;
    Container params;
    Container resistances;

    Container description;
    Container lore;
    String mainLayout = "attributes: 0 0; ";
    public final static String bgPath = "";


    public InfoDialog(DC_HeroObj unit) {
        super(bgPath);
//        VISUALS.DOUBLE_CONTAINER,
        fxAndAbils = new Container( "fxAndAbils") {
            @Override
            public void layout() {

                EntityContainer effects = new EntityContainer("Active Effects", 32, 6, 4,
                 () -> {
                     return unit.getBuffs();
                 });
                EntityContainer abilities = new EntityContainer("Special Abilities", 32, 6, 4,
                 () -> {
                     return unit.getPassives();
                 });
            }
        };


        armor = new Container("", LAYOUT.HORIZONTAL) {
            @Override
            public void layout() {

                EntityContainer buffs = new EntityContainer("Buffs", 32, 2, 2,
                 () -> {
                     return unit.getArmor().getBuffs();
                 });

                EntityContainer traits = new EntityContainer("Traits", 32, 2, 2,
                 () -> {
                     return unit.getArmor().getPassives();
                 });
//                setConstraints("");
//                layout(buffs, armorIcon, new Space(10, 10), traits);

                super.layout();
            }
        };
        parser = new LayoutParser(this);
        parser.parse(this,
         mainLayout, fxAndAbils, attributes, dynamicParams, mainWeapon, description,

         resistances, armor, mainParams, points, portrait,

         params, offWeapon, lore


        );
    }
}














