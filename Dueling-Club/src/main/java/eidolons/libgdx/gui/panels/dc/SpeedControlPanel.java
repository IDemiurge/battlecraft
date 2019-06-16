package eidolons.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.system.options.AnimationOptions;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;
import main.system.math.MathMaster;

public class SpeedControlPanel extends TablePanelX {

    private static final String BG_PATH = "ui/components/dc/queue/speed ctrl bg.png";
    private final VisSlider slider;
    private ImageContainer background;
    private final VisCheckBox checkBox;

    ButtonGroup speedButtons;


    public SpeedControlPanel() {
//        addActor(background = new ImageContainer(BG_PATH));
        if (!VisUI.isLoaded()) {
            VisUI.load(PathFinder.getSkinPath());
        }
        addActor(slider = new VisSlider(10, 150, 10, true));
//        setSize(background.getWidth(), background.getHeight());
        GdxMaster.center(slider);
//        slider.setValue(OptionsMaster.getGameplayOptions().
//                getIntValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED));

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = slider.getValue();
                if (ExplorationMaster.isExplorationOn()) {
                    OptionsMaster.getAnimOptions().setValue(AnimationOptions.ANIMATION_OPTION.SPEED, value * 3);
                    OptionsMaster.getGameplayOptions().setValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED, value);
                    OptionsMaster.applyAnimOptions();
                    OptionsMaster.applyGameplayOptions();
                } else {
                    OptionsMaster.getGameplayOptions().setValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED, value);
                    OptionsMaster.applyGameplayOptions();
                }
//                Gdx.app.log("Options", option + " -> " + value);
            }
        });
        addActor(checkBox = new VisCheckBox(StringMaster.getWellFormattedString(GameplayOptions.GAMEPLAY_OPTION.TURN_CONTROL.getName())));
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                OptionsMaster.getGameplayOptions().setValue(
                        GameplayOptions.GAMEPLAY_OPTION.TURN_CONTROL, checkBox.isChecked());
                OptionsMaster.applyGameplayOptions();
            }
        });
        GdxMaster.centerWidth(checkBox);
        VisCheckBox.VisCheckBoxStyle style = checkBox.getStyle();
        style.font = StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 17).font;
        checkBox.setStyle(style);
        checkBox.setScale(1.5f);
        slider.addListener(new DynamicTooltip(()->"Adjust speed; current = " + getSpeed()).getController());
        checkBox.addListener(new ValueTooltip("Toggle auto-pause between enemy turns").getController());
        setSize(140, 200);
        GdxMaster.center(slider);
        checkBox.setY(10);
        checkBox.setX(10);
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
//        addActor(checkBox2 = new VisCheckBox(
//                StringMaster.getWellFormattedString(GameplayOptions.GAMEPLAY_OPTION.WAIT_BETWEEN_TURNS.getName())));
//        checkBox2.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                OptionsMaster.getGameplayOptions().setValue(
//                        GameplayOptions.GAMEPLAY_OPTION.TURN_CONTROL, checkBox.isChecked());
//                OptionsMaster.applyGameplayOptions();
//            }
//        });
//        GdxMaster.centerWidth(checkBox2);
    }

    private String getSpeed() {
        if (ExplorationMaster.isExplorationOn())
            return ""+ MathMaster.round((OptionsMaster.getAnimOptions().getFloatValue(
                    AnimationOptions.ANIMATION_OPTION.SPEED))/ 3);
        return OptionsMaster.getGameplayOptions().
                getValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED);
    }

//    @Override
//    public void act(float delta) {
//        super.act(delta);
//    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (ExplorationMaster.isExplorationOn()) {
            slider.setValue(OptionsMaster.getAnimOptions().getFloatValue(
                    AnimationOptions.ANIMATION_OPTION.SPEED) / 3);
        } else {
            checkBox.setChecked(OptionsMaster.getGameplayOptions().
                    getBooleanValue(GameplayOptions.GAMEPLAY_OPTION.TURN_CONTROL));
            slider.setValue(OptionsMaster.getGameplayOptions().
                    getFloatValue(GameplayOptions.GAMEPLAY_OPTION.GAME_SPEED));
        }
        super.draw(batch, parentAlpha);
    }
}