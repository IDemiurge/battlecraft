package eidolons.client.cc.gui.neo.choice;

import eidolons.client.cc.gui.misc.PoolComp;
import eidolons.client.cc.gui.neo.points.HC_PointComp;
import eidolons.content.DC_ContentManager;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.entity.type.ObjType;

import javax.swing.*;
import java.awt.*;

public class IdentificationChoiceView extends PrincipleChoiceView {

    public IdentificationChoiceView(ChoiceSequence sequence, Unit hero) {
        super(sequence, hero);

    }

    @Override
    protected void addInfoPanels() {
        super.addInfoPanels();

        new PoolComp(hero, PARAMS.IDENTITY_POINTS, PARAMS.IDENTITY_POINTS.getName()
         + " to spend", true);

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PRINCIPLES> list,
                                                  PRINCIPLES value, int index, boolean isSelected, boolean cellHasFocus) {

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    public class PrincipleIdentificationComp extends HC_PointComp {

        public PrincipleIdentificationComp(boolean editable, Unit hero, ObjType buffer,
                                           PRINCIPLES principle) {
            // if using Renderer technique, gonna have to cache model to support
            // down()
            super(editable, hero, buffer, DC_ContentManager
              .getIdentityParamForPrinciple(principle), PARAMS.IDENTITY_POINTS,
             VISUALS.ENUM_CHOICE_COMP, true);
            // VISUALS.ENUM_CHOICE_COMP_SELECTED : VISUALS.ENUM_CHOICE_COMP;
            // DC_ContentManager.getAlignmentForPrinciple(principle) ;

        }

    }

	/*
     * 5x2
	 * big arrows up/down? 
	 * 
	 * pointView? 
	 * spinnerModel? 
	 * 
	 * ID points param
	 * 
	 * 
	 * 
	 * 
	 */

}
