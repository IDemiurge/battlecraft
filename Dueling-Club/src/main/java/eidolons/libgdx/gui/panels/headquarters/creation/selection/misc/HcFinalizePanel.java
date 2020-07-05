package eidolons.libgdx.gui.panels.headquarters.creation.selection.misc;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.creation.HcElement;
import eidolons.libgdx.gui.panels.headquarters.creation.HcHeroModel;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.DescriptionScroll;
import eidolons.libgdx.texture.Images;
import main.content.values.properties.PROPERTY;

/**
 * Created by JustMe on 7/6/2018.

 overview
 - xp left
 - stat points left
 fuller preview


 quick changes
 done button
 export button
 */
public class HcFinalizePanel extends HcElement{

    private final DescriptionScroll description;

    public HcFinalizePanel() {
        // getVar sketches relevant to the hero
        // buy items?
        // TRANSIT TO HQ?!
        HcHeroModel model = HeroCreationMaster.getModel();

          add(description= new DescriptionScroll()).row();

        description.setUserObject(new SelectableItemData(model.getName() ,
         getDescription(model),
         getPreviewOne(model), getPreviewTwo(model))) ;

//        add(new TextButtonX("Change Name", STD_BUTTON.MENU, () -> HeroCreationMaster.rename()));
        row();
        add(new SmartTextButton("Export", STD_BUTTON.MENU, () -> HeroCreationMaster.export()));
        row();
        add(new SmartTextButton("Done", STD_BUTTON.MENU, () -> HeroCreationMaster.done()));
    }

    private String getPreviewTwo(HcHeroModel model) {
        return Images.getSketch(model.getDeity());
    }

    private String getPreviewOne(HcHeroModel model) {
        return Images.getSketch(model.getBackground());
    }

    private String getDescription(HcHeroModel model) {
        String p="";
        PROPERTY[] props = new PROPERTY [ ]
        {

        };
        //and all other misc params?
        for (PROPERTY item : props) {
            p +=model.getProperty(item) + "\n";
        }
        return p;
    }


}
