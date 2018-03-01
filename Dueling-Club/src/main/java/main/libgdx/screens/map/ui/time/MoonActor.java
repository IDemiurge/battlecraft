package main.libgdx.screens.map.ui.time;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import main.data.filesys.PathFinder;
import main.game.module.adventure.MacroGame;
import main.libgdx.GdxMaster;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.screens.map.ui.time.MapTimePanel.MOON;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/27/2018.
 */
public class MoonActor extends Group {

    private static final float FULL_SIZE = 78;//64*356/256;
    MOON moon;
    ImageContainer underlay;
    ImageContainer overlay;
    ImageContainer circle;
    ImageContainer main;
    boolean active;
    private EmitterActor emitter;
    private float emitterScale;
    private EmitterActor circleEmitter;

    public MoonActor(MOON moon) {
        this.moon = moon;
        float fullSize = GdxMaster.adjustSize(FULL_SIZE);
        setSize(fullSize,fullSize);

        main = new ImageContainer(getPath()+ moon.name() + ".png" );
        circle = new ImageContainer(getPath()+
//         moon.name() +
         "circle.png" );
        underlay = new ImageContainer(getPath()+"under" + StringMaster.getPathSeparator()+
         moon.name() + ".png") ;
        overlay = new ImageContainer(getPath()+"over" + StringMaster.getPathSeparator()+
         moon.name() + ".png") ;

        circle.setAlphaTemplate(ALPHA_TEMPLATE.MOON);
        overlay.setAlphaTemplate(ALPHA_TEMPLATE.MOON);
        underlay.setAlphaTemplate(ALPHA_TEMPLATE.MOON);

        float moonSize = fullSize * 1f;
        main.setSize(moonSize, moonSize);
        circle.setSize(fullSize* 1.2f, fullSize* 1.2f);
        circle.setPosition(-fullSize*0.1f,-fullSize*0.1f );
        overlay.setSize(moonSize, moonSize );
        underlay.setSize(fullSize, fullSize);
        overlay.getContent(). setOrigin(Align.center);
        circle.getContent(). setOrigin(Align.center);

        initEmitters();
//        addActor(underlay);
        addActor(circle);
        addActor(main);
        addActor(overlay);

        GdxMaster.centerAndAdjust(main);
        GdxMaster.centerAndAdjust(overlay);

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                MacroGame.getGame().getLoop().getTimeMaster().nextPeriod();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    private void initEmitters() {
        emitter = new EmitterActor(StrPathBuilder.build(PathFinder.getSfxPath(), "moons", moon.name()));
        addActor(emitter);
        float offsetX = getOffset();
        float offsetY = getOffset();
        emitter.start();
        emitter.setSpeed(0.12f);
        float size = getEmitterScale();
        emitter.getEffect().getEmitters().get(0).scaleSize(size, size);
        emitter.setPosition((FULL_SIZE / 2 + offsetX) , (FULL_SIZE / 2 + offsetY) );

        circleEmitter = new EmitterActor(StrPathBuilder.build(PathFinder.getSfxPath(),
         "moons",  "circle "+moon.name()));
        addActor(circleEmitter);
          offsetX = getOffset();
          offsetY = getOffset();
        circleEmitter.start();
        circleEmitter.setSpeed(0.12f);
          size = 1.4f;
//        size =1/(size+2)/3;
        circleEmitter.getEffect().getEmitters().get(0).scaleSize(size, size);
        circleEmitter.setPosition((FULL_SIZE / 2 + offsetX) , (FULL_SIZE / 2 + offsetY) );

    }

    private float getOffset() {
        switch (moon) {
            case SHADE:
                return 6;
        }
        return 2.5f;
    }

    @Override
    public void act(float delta) {

        circle.setPosition(-FULL_SIZE*0.1f,-FULL_SIZE*0.1f );
        super.act(delta);
        float r = overlay.getContent().getRotation();
        float dx = getRotationSpeedOverlay() * delta;
        overlay.getContent().setRotation(r+dx);
        circleEmitter.setVisible(active);
        circle.setVisible(false);
        if (active) {
            r=circle.getRotation(); dx=-(Math.abs(dx)+1);
            circle.setRotation(r+dx);
        }

//        ActorMaster.addMoveToAction(overlay.getContent(), );
    }

    private float getRotationSpeedOverlay() {
        switch (moon) {
            case FAE:
                return 0.5f;
            case TEMPEST:
                break;
            case HAVEN:
                break;
            case RIME:
                break;
            case FEL:
                break;
            case SHADE:
                break;
        }
        return 1;
    }

    private String getPath() {
        return         StrPathBuilder.build(PathFinder.getMacroUiPath()
         , "component", "time panel", "moons")+ StringMaster.getPathSeparator();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getEmitterScale() {
        switch (moon) {
            case TEMPEST:
                return 1.1f;
            case HAVEN:
            case FAE:
            case RIME:
                return 1.2f;
            case FEL:
            case SHADE:
                return 1.0f;
        }
        return 1.1f;
    }
}
