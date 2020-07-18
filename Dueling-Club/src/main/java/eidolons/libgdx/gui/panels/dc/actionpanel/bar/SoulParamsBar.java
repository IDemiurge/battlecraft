package eidolons.libgdx.gui.panels.dc.actionpanel.bar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.GenericEnums;
import main.content.values.parameters.PARAMETER;
import main.system.launch.Flags;

import java.util.function.Supplier;

public class SoulParamsBar extends SpriteParamBar {

    public SoulParamsBar(Supplier<BattleFieldObject> supplier) {
        super(supplier);
    }

    @Override
    protected String getTooltipText() {
        // BattleFieldObject unit = supplier.get();
        return "Focus: "+label2.getText()+"\nEssence: "+label1.getText();
    }

    protected void resetLabelPos() {
        int y = getLabelY();
        label1.setPosition(257, y);
        label2.setPosition(31, y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (isColored()){
            ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
            super.draw(batch, parentAlpha);
            ((CustomSpriteBatch) batch).resetBlending( );
        }
    }

    private boolean isColored() {
        if (Flags.isLiteLaunch()) {
            return false;
        }
        return false;
    }

    @Override
    protected void initColors() {
        super.initColors();
        label2.setColor(GdxColorMaster.FOCUS);
        label1.setColor(GdxColorMaster.ESSENCE);
    }

    @Override
    protected Color getColor(boolean over) {
        if (!isColored())
            return GdxColorMaster.WHITE;
        return over ? GdxColorMaster.FOCUS : GdxColorMaster.ESSENCE;
    }

    @Override
    protected String getBarImagePath(boolean over) {
        if (!isColored())
            return over ? Sprites.SOULFORCE_BAR : Sprites.SOULFORCE_BAR_BG;
        return over ? Sprites.SOULFORCE_BAR_WHITE : Sprites.SOULFORCE_BAR_BG_WHITE;
    }

    @Override
    protected PARAMETER getOverParam(boolean current) {
        return current ? PARAMS.C_FOCUS : PARAMS.FOCUS;
    }

    @Override
    protected PARAMETER getUnderParam(boolean current) {
        return current ? PARAMS.C_ESSENCE : PARAMS.ESSENCE;
    }
}
