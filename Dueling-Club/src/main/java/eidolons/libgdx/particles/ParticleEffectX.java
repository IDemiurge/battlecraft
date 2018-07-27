package eidolons.libgdx.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import eidolons.libgdx.particles.Emitter.EMITTER_VALS_SCALED;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by JustMe on 1/27/2017.
 */
public class ParticleEffectX extends com.badlogic.gdx.graphics.g2d.ParticleEffect {

    public String path;

    public ParticleEffectX(String path) {
        this.path = path;

        if (isEmitterAtlasesOn()) {
            FileHandle presetFile = Gdx.files.internal(  path);
            if (!presetFile.exists())
                presetFile=Gdx.files.internal(PathFinder.getVfxPath()+path);

            try {
                load(presetFile, getEmitterAtlas());
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String imagePath = EmitterPresetMaster.getInstance().getImagePath(path);
        if (StringMaster.isEmpty(imagePath)) {
            return;
        }
        if (FileManager.isImageFile(StringMaster.getLastPathSegment(imagePath))) {
            imagePath = StringMaster.cropLastPathSegment(imagePath);
        }
        load(Gdx.files.internal(
         PathUtils.addMissingPathSegments(
          path, PathFinder.getParticlePresetPath())),
         Gdx.files.internal(imagePath));

    }

    public ParticleEffectX() {
        super();
    }

    public static  boolean isEmitterAtlasesOn() {
        return !CoreEngine.isFastMode();
    }

    private TextureAtlas getEmitterAtlas() {
        return EmitterMaster.getAtlas(path);
    }

    public void offset(String value, String offset) {
        offset(Float.valueOf(offset),
         new EnumMaster<EMITTER_VALS_SCALED>().retrieveEnumConst(EMITTER_VALS_SCALED.class, value));
    }

    public void offset(float offset, EMITTER_VALS_SCALED value) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.offset(offset, value.name().toLowerCase());
        }
    }

    public void offsetAngle(float offset) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.offsetAngle(offset);
        }
    }


    public void set(String valName, String value) {
        for (ParticleEmitter e : getEmitters()) {
            Emitter emitter = (Emitter) e;
            emitter.set(valName, value.toLowerCase());
        }
    }

    private boolean checkSprite(FileHandle effectFile) {
        if (EmitterActor.spriteEmitterTest) {
            return true;
        }
        return
         EmitterPresetMaster.getInstance().getImagePath(effectFile.path()).contains("sprites");
    }

    public void loadEmitters(FileHandle effectFile) {
        InputStream input = effectFile.read();
        getEmitters().clear();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input), 512);
            while (true) {
                ParticleEmitter emitter = (checkSprite(effectFile)) ? new SpriteEmitter(reader
                ) : new Emitter(reader);

                getEmitters().add(emitter);
                if (reader.readLine() == null) {
                    break;
                }
                if (reader.readLine() == null) {
                    break;
                }
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error loading effect: " + effectFile, ex);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
    }

    public void setImagePath(String imagePath) {
        Array<String> array = new Array<>();
        array.add(imagePath);
        for (int i = 0, n = getEmitters().size; i < n; i++) {
            getEmitters().get(i).setImagePaths(array);
        }
    }

    public void modifyParticles() {
        for (int i = 0, n = getEmitters().size; i < n; i++) {
            Emitter e = (Emitter) getEmitters().get(i);
            e.modifyParticles();
        }
    }

    public void toggle(String fieldName) {
        for (int i = 0, n = getEmitters().size; i < n; i++) {
            Emitter e = (Emitter) getEmitters().get(i);
            e.toggle(fieldName);
        }
    }
}
