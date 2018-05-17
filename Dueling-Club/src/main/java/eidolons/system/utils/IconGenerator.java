package eidolons.system.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.io.File;

/**
 * Created by JustMe on 5/13/2018.
 */
public class IconGenerator extends GdxUtil {
    String root;
    String underlay;
    String output;
    private boolean subdirs;

    public IconGenerator(String root, String underlay, String output, boolean subdirs) {
        this.root = root;
        this.underlay = underlay;
        this.output = output;
        this.subdirs = subdirs;
        start();
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
        String path = StrPathBuilder.build(PathFinder.getImagePath(),
         ImageManager.getValueIconsPath());
        String output = StrPathBuilder.build(path, "generated") + StringMaster.getPathSeparator();
        new IconGenerator(path, Images.EMPTY_RANK_SLOT, output,true);

    }

    public static void generateOverlaidIcon(Texture underlayTexture, String path, FileHandle handle, boolean b) {
        Pixmap pixMap = GdxImageMaster.getPixmap(underlayTexture);
        Texture texture = TextureCache.getOrCreate(path);
        int w = texture.getWidth();
        int h = texture.getHeight();
        int x = (underlayTexture.getWidth() - w) / 2;
        int y = (underlayTexture.getHeight() - h) / 2;
        GdxImageMaster.drawTextureRegion(x, y, texture, w, h, pixMap);
        GdxImageMaster.writeImage(handle, pixMap);

//        pixMap.setBlending();
//    pixMap.setFilter();

    }

    public void generate(String root, String underlay, String output) {
        Texture underlayTexture = TextureCache.getOrCreate(underlay);
        for (File sub : FileManager.getFilesFromDirectory(root, false, subdirs)) {
            if (!FileManager.isImageFile(sub.getName()))
                continue;
            FileHandle handle = new FileHandle(output + (sub.getName()));
            String path = sub.getPath().replace(PathFinder.getImagePath(), "");
            generateOverlaidIcon(underlayTexture, path, handle, true);

        }
    }

    @Override
    protected void execute() {
        generate(root, underlay, output);
    }
}