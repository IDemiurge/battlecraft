package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import main.system.auxiliary.StringMaster;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    private Map<String, Texture> cache;
    private String imagePath;


    public TextureCache(String imagePath) {
        this.imagePath = imagePath;
        this.cache = new HashMap<>();
    }

    public final Texture getOrCreate(String path) {
        return this.get(path, true);
    }

    public final Texture get(String path) {
        return this.get(path, false);
    }

    public final Texture get(String path, boolean save) {
        String p = File.separator + path;

//        if (!StringsKt.startsWith(p, File.separator + this.imagePath, true)) {
//            p = this.imagePath + p;
//        }
        p = StringMaster.addMissingPathSegments(p, imagePath);

        if (!this.cache.containsKey(p)) {
            Texture t = new Texture(p);
            if (!save) {
                return t;
            }

            this.cache.put(p, t);
        }

        return this.cache.get(p);
    }
}
