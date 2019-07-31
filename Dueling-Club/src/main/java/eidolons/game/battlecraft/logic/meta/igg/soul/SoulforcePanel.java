package eidolons.game.battlecraft.logic.meta.igg.soul;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.libgdx.texture.Images;
import main.game.bf.directions.FACING_DIRECTION;

public class SoulforcePanel extends GroupX {
    private final FadeImageContainer bg;
    private final SmartButton lordBtn;
    private final SmartButton paleBtn;
    SoulforceBar bar;

    public SoulforcePanel() {
//        RollDecorator.RollableGroup decorated;
//        addActor(decorated = RollDecorator.decorate(
//                bg = new FadeImageContainer(Images.COLUMNS), FACING_DIRECTION.NORTH, true));
//       decorated.setRollPercentage(0.78f);
//       decorated.setRollIsLessWhenOpen(true);
//        decorated.toggle(false);
        addActor(bg = new FadeImageContainer("ui/components/dc/soulforce/background.png"));
        addActor(bar = new SoulforceBar( ));
        addActor(lordBtn = new SmartButton(ButtonStyled.STD_BUTTON.LORD_BTN, ()-> openLordPanel()));
        addActor(paleBtn = new SmartButton(ButtonStyled.STD_BUTTON.PALE_BTN, ()-> openLordPanel()));

        bar.addListener(new DynamicTooltip(() -> "Current Soulforce: " + bar.getTooltip()).getController());
        lordBtn.addListener(new DynamicTooltip(() -> "Eidolon Arts").getController());
        paleBtn.addListener(new DynamicTooltip(() -> "Enter Pale Aspect").getController());

        setSize(bg.getWidth(), bg.getHeight());
        GdxMaster.center(bar);
        GdxMaster.top(bar);
        bar.setY(bar.getY()+17);

        lordBtn.setX(getWidth()/3-36);
        paleBtn.setX(getWidth()/3*2-1);
        lordBtn.setY(bar.getY()-bar.getHeight()/2+28);
        paleBtn.setY(bar.getY()-bar.getHeight()/2+28);

//        soulCounter= new SoulCounter();

        /**
         *
         *sf  label
         *
         */
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private void openLordPanel() {
    }
}
