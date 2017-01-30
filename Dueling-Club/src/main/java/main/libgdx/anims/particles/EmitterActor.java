package main.libgdx.anims.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.content.CONTENT_CONSTS2.SFX;
import main.data.filesys.PathFinder;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 1/10/2017.
 */
public class EmitterActor extends Actor implements ParticleInterface {

    private final int defaultCapacity = 12;
    private final int defaultMaxCapacity = 24;
  static public boolean spriteEmitterTest=false;
    public String path;
    protected ParticleEffect effect;
    protected ParticleEffectPool pool;
    protected SFX sfx;
    boolean flipX;
    boolean flipY;
    private Sprite sprite;
    private boolean attached=true;
    private boolean generated;
    private Coordinates target;


    public EmitterActor(SFX fx) {
        this(fx.path);
        this.sfx = fx;
//        effect.setFlip(flipX, flipY);
//        effect.getEmitters().get(0).setSprite();
    }

    public EmitterActor(String path, boolean test) {
        this.path=path;
        effect = new ParticleEffect();
        String imagePath =
         PathFinder.getParticleImagePath();
            effect.load(Gdx.files.internal(
             StringMaster.addMissingPathSegments(
              path, PathFinder.getParticlePresetPath())),
             Gdx.files.internal(imagePath));

    }
    public EmitterActor(String path) {
//        path =PathFinder.getSfxPath() + "templates\\sprite test";
        this.path=path;

        effect = new ParticleEffect();
//        pool = new ParticleEffectPool(effect, defaultCapacity, defaultMaxCapacity);
//        pool.obtain() ; TODO
        effect = new ParticleEffect();
        String imagePath =
         PathFinder.getParticleImagePath();

        try {
            effect.load(Gdx.files.internal(
             StringMaster.addMissingPathSegments(
              path, PathFinder.getParticlePresetPath())),
             Gdx.files.internal(imagePath));

        } catch (Exception e) {
            String suffix = StringMaster.replaceFirst(path, PathFinder.getParticlePresetPath(), "");
            suffix = StringMaster.cropLastPathSegment(suffix);
            imagePath += suffix;
        try {
            effect.load(Gdx.files.internal(
             StringMaster.addMissingPathSegments(
              path, PathFinder.getParticlePresetPath())),
             Gdx.files.internal(imagePath));

        } catch (Exception e0) {
            imagePath += "particles\\";
            try {
                effect.load(Gdx.files.internal(
                 StringMaster.addMissingPathSegments(
                  path, PathFinder.getParticlePresetPath())),
                 Gdx.files.internal(imagePath));
            } catch (Exception e1) {
                imagePath =
                 PathFinder.removeSpecificPcPrefix(
                  EmitterPresetMaster.getInstance().getImagePath(path));
                imagePath = StringMaster.cropLastPathSegment(imagePath);
                try {
                    effect.load(Gdx.files.internal(
                     StringMaster.addMissingPathSegments(
                      path, PathFinder.getParticlePresetPath())),
                     Gdx.files.internal(imagePath));
                } catch (Exception e2) {
                    main.system.auxiliary.LogMaster.log(1, imagePath + " - NO IMAGE FOUND FOR SFX: " + path);
                    e2.printStackTrace();
                }


            }
        }


        }
if ( spriteEmitterTest)
        effect.getEmitters().forEach(e->{
            String randomPath = FileManager.getRandomFile(PathFinder.getSpritesPath() +
             "impact\\").getPath();
            ((Emitter)e).offset(20, "scale");
            e.setImagePath(randomPath);
            e.setPremultipliedAlpha(false);
        });
    }


    public void act(float delta) {
        super.act(delta);
//        effect.setPosition(x, y);
//        effect.update(delta); TODO now drawing with alpha!

    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public void updatePosition(float x, float y) {
        setPosition(x, y);
    }

    @Override
    public SFX getTemplate() {
        return sfx;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        effect.setPosition(getX(), getY());
//        sprite = effect.getEmitters().first().getSprite();
//        sprite.setRotation(new Random().nextInt(360));

        effect.draw(batch,   Gdx.graphics.getDeltaTime());

    }

    @Override
    public ParticleEffect getEffect() {
        return effect;
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }


    @Override
    public void start() {
        effect.start();
    }

    public SFX getSfx() {
        return sfx;
    }

    public void setSfx(SFX sfx) {
        this.sfx = sfx;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setTarget(Coordinates target) {
        this.target = target;
    }

    public Coordinates getTarget() {
        return target;
    }
}