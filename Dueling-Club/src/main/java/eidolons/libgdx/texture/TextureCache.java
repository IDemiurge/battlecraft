package eidolons.libgdx.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.system.graphics.GreyscaleUtils;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextureCache {
    private static final boolean atlasesOn = false;
    private static Boolean uiAtlasesOn = false;
    private static final boolean tryCompressedFormat = true;
    private static TextureCache instance;
    private static Lock creationLock = new ReentrantLock();
    private static AtomicInteger counter = new AtomicInteger(0);
    private static boolean altTexturesOn = true;
    private static Texture missingTexture;
    private static Texture empty;

    private static Map<String, TextureRegion> regionCache = new HashMap<>(300);
    private static Map<TextureRegion, TextureRegionDrawable> drawableMap = new HashMap<>(300);
    private static boolean returnEmptyOnFail = true;
    private static List<String> missingTextures = new LinkedList<>();

    private Map<String, Texture> cache;
    private Map<Texture, Texture> greyscaleCache;
    private String imagePath;
    private SmartTextureAtlas uiAtlas;
    private SmartTextureAtlas mainAtlas;
    private SmartTextureAtlas genAtlas;
    private Pattern pattern;
    private boolean silent;

    public static TextureRegion fromAtlas(String atlasPath, String light) {
        if (Assets.get().getManager().isLoaded(atlasPath)) {
            SmartTextureAtlas atlas = Assets.get().getManager().get(atlasPath);
            return atlas.findRegion(light);
        }
        return new TextureRegion(getMissingTexture());
    }

    public static boolean isCached(String s) {
        if (missingTextures.contains(s)) {
            return false;
        }
        Texture texture = getInstance().cache.get(s);
        if (texture != null) {
            return true;
        }
        TextureRegion textureRegion = getOrCreateR(s);
        if (isEmptyTexture(textureRegion)) {
            missingTextures.add(s);
            return false;
        }
        return true;
    }

    public static TextureRegion getFlippedRegion(boolean x, boolean y, String path) {
        Texture texture = GdxImageMaster.flip(path, x, y, CoreEngine.isIDE());
           return getRegion(GdxImageMaster.getFlippedPath(path, x,y), texture);
    }

    public void loadAtlases() {
        uiAtlas = new SmartTextureAtlas(imagePath + "/ui/ui.txt");
//        mainAtlas = new SmartTextureAtlas(imagePath + "/main//main.txt");
//            genAtlas = new SmartTextureAtlas(imagePath + "/gen//gen.txt");
    }

    private TextureCache() {
//            uiAtlasesOn = CoreEngine.isJarlike() || !CoreEngine.isIDE() ;

        this.imagePath = PathFinder.getImagePath();
        this.cache = new HashMap<>();
        this.greyscaleCache = new HashMap<>();
        if (atlasesOn) {
            loadAtlases();
        } else {
            if (uiAtlasesOn) {
                uiAtlas = new SmartTextureAtlas(imagePath + "/ui/ui.txt");
            }
        }

        pattern = Pattern.compile("^.*[///]([a-z _/-0-9]*)/..*$");

        GuiEventManager.bind(GuiEventType.DISPOSE_TEXTURES, p -> {
            dispose();
        });
    }

    public void logDiagnostics() {
        main.system.auxiliary.log.LogMaster.important(
                "cache.size " + cache.size() +
                        "regionCache.size " + regionCache.size() +
                        "drawableMap.size " + drawableMap.size() +
                        "greyscaleCache.size " + greyscaleCache.size()
        );
    }

    private static boolean checkRetainTexture(String s) {
        String prefix = PathUtils.getPathSegments(s).get(0).toLowerCase();
        switch (prefix) {
            case "ui":
            case "vfx":
            case "sprites":
            case "demo":
                return true;
        }
        //        prefix = PathUtils.getPathSegments(s).getVar(1).toLowerCase();
        return false;
    }

    private static void init() {
        try {
            creationLock.lock();
            if (instance == null) {
                instance = new TextureCache();
            }
            SpriteAnimationFactory.init();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            creationLock.unlock();
        }
    }

    public static Texture getOrCreateGrayscale(String path) {
        return getInstance()._getOrCreateGrayScale(path);
    }

    public static Texture getOrCreateNonEmpty(String path) {
        Texture texture = getOrCreate(path);
        if (texture == missingTexture)
            return null;
        return texture;
    }

    public static Texture getOrCreate(String path, boolean silent) {
        getInstance().setSilent(silent);
        Texture texture = null;
        try {
            texture = getInstance()._getOrCreate(path);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            texture = null;
        }
        getInstance().setSilent(false);
        return texture;
    }

    public static Texture getOrCreate(String path) {
        return getInstance()._getOrCreate(path);
    }

    public static TextureRegion getOrCreateRoundedRegion(String path) {
        return getOrCreateRoundedRegion(path, true);
    }

    public static TextureRegion getOrCreateRoundedRegion(String path, boolean write) {
        TextureRegion region = getOrCreateR(GdxImageMaster.getRoundedPathNew(path));
        if (!region.getTexture().equals(missingTexture)) {
            return region;
        }
        region = getOrCreateR(GdxImageMaster.getRoundedPath(path));
        if (!region.getTexture().equals(missingTexture)) {
            return region;
        }
        return GdxImageMaster.round(path, write);
    }

    public static TextureRegion getOrCreateR(String path) {
        return getOrCreateR(path, false);
    }

    public static TextureRegion getOrCreateR(String path, boolean overrideNoAtlas) {

            if (path.contains(":")) {
                try {
                    return getOrCreateR(path.split("img")[1]);
                } catch (Exception e) {
                    main.system.auxiliary.log.LogMaster.log(1,"invalid  TEXTURE  path ! "  + path);
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        if (path == null) {
            main.system.auxiliary.log.LogMaster.log(1, "EMPTY TEXTURE REGION REQUEST!");
            return new TextureRegion(missingTexture);
        }

        TextureRegion region = regionCache.get(path);
        if (region != null)
            return region;

        final Matcher matcher = getInstance().pattern.matcher(path);

        if (matcher.matches()) {
            String name = path.substring(
                    //          1+path.indexOf(StringMaster.getPathSeparator())
                    3
                    , path.lastIndexOf("."));// matcher.group(1);
            name = ContainerUtils.constructStringContainer
                    (PathUtils.getPathSegments(name), "/");
            name = name.substring(0, name.length() - 1);

            if (!overrideNoAtlas) {
                if (getInstance().uiAtlas != null) {
                    region = getInstance().uiAtlas.findRegion(name);
                }
                if (region == null) {
                    if (getInstance().mainAtlas != null) {
                        region = getInstance().mainAtlas.findRegion(name);
                    }
                }
            } else {
                if (getInstance().uiAtlas != null) {
                    region = getInstance().uiAtlas.findRegion(name);
                }
            }
            if (region != null) {

                regionCache.put(path, region);
                counter.incrementAndGet();
                overrideNoAtlas = checkOverrideNoAtlas(region, path);
                if (!overrideNoAtlas)
                    return region;
            }
        }

        region = new TextureRegion(getInstance()._getOrCreate(path));
        if (region.getTexture() != missingTexture)
            regionCache.put(path, region);
        return region;
    }

    private static boolean checkOverrideNoAtlas(TextureRegion region, String path) {
        if (region.getRegionWidth() > 2000) {
            return true;
        }
        if (region.getRegionHeight() > 2000) {
            return true;
        }
        String name = PathUtils.getLastPathSegment(path).toLowerCase();
        switch (name) {
            //                imgPath = outcome ? "ui/big/victory.png" : "ui/big/defeat.jpg";
            case "logo fullscreen.png":
            case "MAIN_MENU.png":
            case "defeat.png":
            case "defeat.jpg":
            case "defeat2.jpg":
            case "victory.jpg":
            case "victory.png":
            case "weapon background.png":
                return true;
        }
        return false;
    }

    private static boolean checkAltTexture(String path) {
        if (altTexturesOn) {
            return path.contains(StrPathBuilder.build("gen"));
        }
        return false;
    }

    public static TextureRegion getOrCreateGrayscaleR(String path) {
        return new TextureRegion(getOrCreateGrayscale(path));
    }

    public static void setAltTexturesOn(boolean altTexturesOn) {
        TextureCache.altTexturesOn = altTexturesOn;
    }

    public static Texture getMissingTexture() {
        if (missingTexture == null)
            missingTexture = new Texture(getMissingPath());

        return missingTexture;
    }
    public static Texture getEmptyTexture() {
        if (empty == null)
            empty = new Texture(getMissingPath());

        return empty;
    }

    public static String getMissingPath() {
        return
                ImageManager.getImageFolderPath() +
                        Images.MISSING_TEXTURE;
    }
    public static String getEmptyPath() {
        return
                ImageManager.getImageFolderPath() +
                        Images.REALLY_EMPTY_32;
    }

    public static Texture createTexture(String path) {
        return createTexture(path, true);
    }

    public static Texture createTexture(String path, boolean putIntoCache) {

        return getInstance()._createTexture(path, putIntoCache);
    }

    public static TextureRegionDrawable getOrCreateTextureRegionDrawable(TextureRegion originalTexture) {
        TextureRegionDrawable drawable = drawableMap.get(originalTexture);
        if (drawable == null) {
            drawable = new TextureRegionDrawable(originalTexture);
            drawableMap.put(originalTexture, drawable);
        }

        return drawable;
    }

    public static TextureRegionDrawable getOrCreateTextureRegionDrawable(String imagePath) {
        if (imagePath == null)
            return null;
        return getOrCreateTextureRegionDrawable(getOrCreateR(imagePath));
    }

    public static String formatTexturePath(String path) {
        return FileManager.formatPath(path, true, true);
//        path = path.toLowerCase()
//                .replace("\\", "/").replace("//", "/");
//        if (path.endsWith("/"))
//            return path.substring(0, path.length() - 1);
//        return path;
    }

    public static TextureRegion getOrCreateSizedRegion(int iconSize, String path) {
        Texture sized = GdxImageMaster.size(path, iconSize, true);
        if (sized == null)
            return new TextureRegion(getMissingTexture());
        return new TextureRegion(sized);
    }

    public static TextureRegion getRegion(String path, Texture texture) {
        TextureRegion region = regionCache.get(path);
        if (region == null) {
            region = new TextureRegion(texture);
            regionCache.put(path, region);
        }
        return region;
    }

    public static TextureRegion getSizedRegion(int imageSize, String path) {
        if (path == null)
            return null;
        String sized = StringMaster.getAppendedImageFile(path, " " + imageSize);
        return getRegion(sized, GdxImageMaster.size(sized, imageSize, true));
    }

    public static boolean isReturnEmptyOnFail() {
        return returnEmptyOnFail;
    }

    public static void setReturnEmptyOnFail(boolean returnEmptyOnFail) {
        TextureCache.returnEmptyOnFail = returnEmptyOnFail;
    }

    public static TextureCache getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public static boolean isImage(String property) {
        if (!GdxMaster.isLwjglThread())
            return ImageManager.isImage(property);
        if (!isReturnEmptyOnFail())
            return getOrCreate(property) != null;
        Texture t = null;
        try {
            t = getOrCreate(property);
        } catch (Exception e) {
        }
        return t != missingTexture;
    }

    public static boolean isEmptyTexture(Texture texture) {
        if (texture == null) {
            return true;
        }
        return missingTexture == texture;
    }

    public static boolean isEmptyTexture(TextureRegion region) {
        return isEmptyTexture(region.getTexture());
    }


    public void dispose() {
        for (String s : cache.keySet()) {
            if (checkRetainTexture(s))
                continue;

            cache.get(s).dispose();
            cache.remove(s);

        }
    }

    private String getAltTexturePath(String filePath) {
        return filePath.replace(StrPathBuilder.build("gen", "entity"), StrPathBuilder.build("main"));
    }

    private Texture _getOrCreateGrayScale(String path) {
        Texture normal = _getOrCreate(path);
        if (!greyscaleCache.containsKey(normal)) {
            if (!normal.getTextureData().isPrepared()) {
                normal.getTextureData().prepare();
            }
            //            Gdx2DPixmap. GDX2D_FORMAT_RGBA8888
            Pixmap pixmap2 = new Pixmap(normal.getWidth(), normal.getHeight(), Format.RGBA8888);
            Pixmap pixmap = normal.getTextureData().consumePixmap();

            for (int i = 0; i < normal.getWidth(); i++) {
                for (int j = 0; j < normal.getHeight(); j++) {
                    int rgba = pixmap.getPixel(i, j);

                    if (rgba == 0)
                        continue;
                    pixmap2.drawPixel(i, j, GreyscaleUtils.luminosity(rgba));
                }
            }

            greyscaleCache.put(normal, new Texture(pixmap2));
        }

        return greyscaleCache.get(normal);
    }

    private Texture _getOrCreate(String path) {
        if (path == null) {
            main.system.auxiliary.log.LogMaster.log(1, "EMPTY TEXTURE REQUEST!");
            return missingTexture;
        }
        path = getPathForCache(path);

        if (!this.cache.containsKey(path)) {
            Texture x = _createTexture(path);
            if (x != null)
                return x;
        }

        return this.cache.get(path);
    }

    private String getPathForCache(String path) {
        path = path
                .toLowerCase();
        if (path.startsWith("img")) {
            path = path.replaceFirst("img", "");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    public Texture _createTexture(String path) {
        return _createTexture(path, true);
    }

    public Texture _createTexture(String path, boolean putIntoCache) {
        return _createTexture(path, putIntoCache, false);
    }

    public Texture _createTexture(String path, boolean putIntoCache, boolean recursion) {
        Path p = Paths.get(imagePath, path);
        String filePath = p.toString();
        Texture t = null;
        if (checkAltTexture(filePath)) //TODO remove this already
            try {
                t = new Texture(GDX.file(getAltTexturePath(filePath)),
                        Pixmap.Format.RGBA8888, false);
                if (putIntoCache)
                    cache.put(path, t);
            } catch (Exception e) {

            }
        if (t == null) {
            FileHandle fullPath = GDX.file(filePath);
            if (fullPath.exists())
                try {
                    t = new Texture(fullPath, Pixmap.Format.RGBA8888, false);
                    if (putIntoCache) {
                        cache.put(path, t);
                    }
                } catch (Exception e) {
                }
            if (t == null) {
                if (!recursion) {
                    if (path.contains(".png"))
                        return _createTexture(path.replace(".png", ".jpg"), putIntoCache, true);
                    if (path.contains(".jpg"))
                        return _createTexture(path.replace(".jpg", ".png"), putIntoCache, true);
                }
                if (!silent)
                    main.system.auxiliary.log.LogMaster.verbose("No texture for " + fullPath);
                if (!isReturnEmptyOnFail())
                    return null;
                if (!cache.containsKey(getMissingPath())) {
                    if (putIntoCache)
                        cache.put(getMissingPath(), getMissingTexture());
                    return getMissingTexture();
                }
                return cache.get(getMissingPath());

            }
        }
        return t;
    }

    public Texture createAndCacheTexture(String path, Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        cache.put(path, texture);
        return texture;
    }

    public TextureRegion createAndCacheRegion(String path, Pixmap pixmap) {
        path = getPathForCache(path);
        Texture texture = new Texture(pixmap);
        cache.put(path, texture);
        TextureRegion r = new TextureRegion(texture);
        regionCache.put(path, r);
        return r;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }
}

