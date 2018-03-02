package main.libgdx.screens.map.ui.time;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.global.GameDate;
import main.game.module.adventure.global.TimeMaster;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.TextButtonX;
import main.libgdx.gui.tooltips.DynamicTooltip;
import main.libgdx.texture.TextureCache;
import main.swing.PointX;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.FontMaster.FONT;

import java.util.HashMap;
import java.util.Map;

import static main.libgdx.screens.map.ui.time.MapTimePanel.MOON.*;


/**
 * Created by JustMe on 2/9/2018.
 * rotate moon circle?
 * control the middle-'sun' brightness
 * cut the circle in two, don't show the upper part...
 * tooltip
 * <p>
 * Month
 * where to show date?
 * perhaps I will now display what later will become a tooltip?
 * date could be displayed in a corner, classic fashion
 * <p>
 * dawn/noon/dusk/night
 */
public class MapTimePanel extends Group {

    private static final float SIZE = 64;
    ImageContainer sun;
    ImageContainer undersun;
    Map<MOON, MoonActor> moonMap = new HashMap<>();
    MoonActor[] displayedMoons = new MoonActor[3];
    Label timeLabel;
    Image stoneCircle = new Image(TextureCache.getOrCreateR(
     StrPathBuilder.build(PathFinder.getMacroUiPath(), "component", "time panel", "stone circle.png")));
    //    Image stoneCircleHighlight = new Image(TextureCache.getOrCreateR(StrPathBuilder.build(PathFinder.getMacroUiPath()
//     , "component", "time panel", "stone circle hl.png")));
    PointX sunPoint = new PointX(96, 128);
    PointX[] points = {new PointX(47, 122), new PointX(128, 91), new PointX(208, 127)};

    boolean movingUp;
    boolean moving;
    PointX timeLabelPoint = new PointX(138, 80);
    PointX pauseBtnPoint = new PointX(118, 25);
    PointX speedUpBtnPoint = new PointX(162, 59);
    PointX speedDownBtnPoint = new PointX(98, 59);
    TextButtonX pauseButton;
    MoonActor activeMoon;
    ImageContainer activeMoonCircle;
    private TextButtonX speedUpBtn;
    private TextButtonX speedDownBtn;

    public MapTimePanel() {
        init();
    }

    public void init() {

        speedUpBtn =
         new TextButtonX(
          "", STD_BUTTON.SPEED_UP, () -> {
             MacroGame.getGame().getLoop().getTimeMaster().speedUp();
         });
        speedDownBtn =
         new TextButtonX(
          "", STD_BUTTON.SPEED_DOWN, () -> {
             MacroGame.getGame().getLoop().getTimeMaster().speedDown();
         });
        pauseButton =
         new TextButtonX(
          "", STD_BUTTON.PAUSE, () -> {
             MacroGame.getGame().getLoop().togglePaused();
             MacroGame.getGame().getLoop().getTimeMaster().resetSpeed();
         });
        pauseButton.setPosition(GdxMaster.adjustPos(true, pauseBtnPoint.x),
         GdxMaster.adjustPos(false, pauseBtnPoint.y));
        speedUpBtn.setPosition(GdxMaster.adjustPos(true, speedUpBtnPoint.x),
         GdxMaster.adjustPos(false, speedUpBtnPoint.y));
        speedDownBtn.setPosition(GdxMaster.adjustPos(true, speedDownBtnPoint.x),
         GdxMaster.adjustPos(false, speedDownBtnPoint.y));


        float moonSize = GdxMaster.adjustSize(SIZE);
        addListener(new DynamicTooltip(() -> getDateString()).getController());
        //display DatePanel?
        for (MOON moon : MOON.values()) {
            MoonActor container = new MoonActor(moon);
            moonMap.put(moon, container);
        }
        float sunSize = moonSize * 2f;
        sun = new ImageContainer(StrPathBuilder.build(PathFinder.getMacroUiPath()
         , "component", "time panel", "sun.png"));
        sun.setAlphaTemplate(ALPHA_TEMPLATE.SUN);
        sun.setSize(sunSize, sunSize);
        sun.setPosition(GdxMaster.adjustPos(true, sunPoint.x),
         GdxMaster.adjustPos(false, sunPoint.y));
        sun.setVisible(false);
        undersun = new ImageContainer(StrPathBuilder.build(PathFinder.getMacroUiPath()
         , "component", "time panel", "undersun.png"));
        undersun.setAlphaTemplate(ALPHA_TEMPLATE.SUN);
        undersun.setSize(sunSize, sunSize);
        undersun.setPosition(GdxMaster.adjustPos(true, sunPoint.x),
         GdxMaster.adjustPos(false, sunPoint.y));
        undersun.setVisible(false);

        timeLabel = new Label("", StyleHolder.getSizedLabelStyle(FONT.NYALA, 18));

        addActor(stoneCircle);
        stoneCircle.layout();
        setSize(stoneCircle.getImageWidth(), stoneCircle.getImageHeight());
        addActor(sun);
        addActor(undersun);

        addActor(timeLabel);

        addActor(pauseButton);
        addActor(speedUpBtn);
        addActor(speedDownBtn);
        timeLabel.setPosition(GdxMaster.adjustPos(true, timeLabelPoint.x),
         GdxMaster.adjustPos(false, timeLabelPoint.y));

        GuiEventManager.bind(MapEvent.TIME_CHANGED, p -> {
            update((DAY_TIME) p.get());
        });
        sun.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                MacroGame.getGame().getLoop().getTimeMaster().newMonth();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        undersun.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                MacroGame.getGame().getLoop().getTimeMaster().newMonth();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        stoneCircle.setOrigin(stoneCircle.getImageWidth()/2, stoneCircle.getImageHeight()*2);
    }

    private void resetZIndices() {
        stoneCircle.setZIndex(0);
        pauseButton.setZIndex(Integer.MAX_VALUE);
        speedUpBtn.setZIndex(Integer.MAX_VALUE);
        speedDownBtn.setZIndex(Integer.MAX_VALUE);
    }

    public void update(DAY_TIME time) {
        undersun.setVisible(time.isUndersunVisible());
        sun.setVisible(time.isSunVisible());
        MOON[] moons = getDisplayedMoons();
        main.system.auxiliary.log.LogMaster.log(1, "update: " + time);
        int i = 0;
        for (MoonActor sub : displayedMoons) {
            if (sub != null) sub.remove();
        }
        for (MOON sub : moons) {
            main.system.auxiliary.log.LogMaster.log(1, "added: " + sub);
            //smooth?!
            //or black out on update()?
            displayedMoons[i] = moonMap.get(sub);
            displayedMoons[i].setVisible(true);
            addActor(displayedMoons[i]);
            int offset = 7;
            displayedMoons[i].setPosition(GdxMaster.adjustPos(true, points[i].x - offset),
             GdxMaster.adjustPos(false, points[i].y - offset));
            i++;
        }
        MOON m = TimeMaster.getDate().getMonth().getActiveMoon(time.isNight());
        if (activeMoon != null)
            activeMoon.setActive(false);
        activeMoon = moonMap.get(m);
        activeMoon.setActive(true);
    }

    @Override
    public void act(float delta) {
        String text=TimeMaster.getDate().getHour()+"";
        int minutes = (int) MacroGame.getGame().getLoop().getTimeMaster().getMinuteCounter();
        if (minutes / 10 == 0) {
            text += ":0" + minutes;
        } else
            text +=   ":" +minutes;
        timeLabel.setText(text);

        delta = delta * MacroGame.getGame().getLoop().getTimeMaster().getSpeed();
        super.act(delta);
        moveSun();
        resetZIndices();

        float r = stoneCircle.getRotation();
        float dx = 0.5f * delta;
        if (r>180)
            movingUp=true;
        if (movingUp)
            if (r<=0)
                movingUp=false;

        if (movingUp)
            dx=-dx;
//        stoneCircle.setRotation(r+dx);
    }

    private void moveSun() {
        // in time X, it must go from -H to H; maybe LEFT TO RIGHT INSTEAD??


    }


    @Override
    public float getHeight() {
        return super.getHeight();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
    }

    private String getDateString() {
        //show exact time on tooltip?

        GameDate date;
//        getPhase();
        String string = "";
        return string;
    }

    private MOON[] getDisplayedMoons() {
        switch (MacroGame.getGame().getTime()) {
            case MORNING:
                return new MOON[]{
                 HAVEN, TEMPEST, RIME,
                };
            case NOON:
                return new MOON[]{
                 FAE, HAVEN, TEMPEST,
                };
            case DUSK:
                return new MOON[]{
                 FEL, FAE, HAVEN,
                };
            case NIGHTFALL:
                return new MOON[]{
                 SHADE, FEL, FAE,
                };
            case MIDNIGHT:
                return new MOON[]{
                 RIME, SHADE, FEL,
                };
            case DAWN:
                return new MOON[]{
                 TEMPEST, RIME, SHADE
                };
        }
        return new MOON[0];
    }

    public enum MOON {
        FAE, TEMPEST, HAVEN, RIME, FEL, SHADE
    }
}