package main.libgdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.data.filesys.PathFinder;
import main.system.graphics.ColorManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class StyleHolder {
    private static final String DISABLED = "_disabled";
    private static final String OVER = "_over";
    private static final String DOWN = "_down";
    private static final String UP = "_up";
    private static final String CHECKED = "_down";
    private static Label.LabelStyle defaultLabelStyle;
    private static Label.LabelStyle avqLabelStyle;
    private static Color defaultColor = new Color(ColorManager.GOLDEN_WHITE.getRGB());
    private static TextButton.TextButtonStyle defaultTextButtonStyle;
    private static Map<Color, Label.LabelStyle> colorLabelStyleMap = new HashMap<>();


    public static Label.LabelStyle getDefaultLabelStyle(Color color) {
        if (!colorLabelStyleMap.containsKey(color)) {
            Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), color);
            style.font.getData().markupEnabled = true;
            colorLabelStyleMap.put(color, style);
        }
        return colorLabelStyleMap.get(color);
    }

    private static BitmapFont getDiablo2Font(Color color) {
        final String path = PathFinder.getFontPath() + "/DIABLO_H.ttf";

        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = color;

        final BitmapFont bitmapFont = generator.generateFont(parameter);
        generator.dispose();
        return bitmapFont;
    }

    public static Label.LabelStyle getDefaultLabelStyle() {
        return getDefaultLabelStyle(defaultColor);
    }

    public static Label.LabelStyle getAVQLabelStyle() {
        if (avqLabelStyle == null) {
            avqLabelStyle = new Label.LabelStyle(new BitmapFont(
//             new FileHandle(
//              PathFinder.getFontPath()+ FONT.AVQ.path
//             )
            ),
                    defaultColor);
        }
        return avqLabelStyle;
    }

    public static TextButton.TextButtonStyle getTextButtonStyle() {
        if (defaultTextButtonStyle == null) {
            defaultTextButtonStyle = new TextButton.TextButtonStyle();
            defaultTextButtonStyle.font = new BitmapFont();
            defaultTextButtonStyle.fontColor = defaultColor;
            defaultTextButtonStyle.overFontColor = new Color(defaultColor).add(50, 50, 50, 0);
            defaultTextButtonStyle.checkedFontColor = new Color(0xFF_00_00_FF);
        }

        return defaultTextButtonStyle;
    }

    public static TextButton.TextButtonStyle getCustomButtonStyle(String baseImagePath) {
        final int jpgEnd = baseImagePath.indexOf(".jpg");
        final int pngEnd = baseImagePath.indexOf(".png");
        String endString = null;
        String baseString = null;

        if (jpgEnd > 0) {
            endString = ".jpg";
            baseString = baseImagePath.replace(endString, "");
        }
        if (pngEnd > 0) {
            endString = ".png";
            baseString = baseImagePath.replace(endString, "");
        }

        final String disabledPath = baseString + DISABLED + endString;
        final String overPath = baseString + OVER + endString;
        final String downPath = baseString + DOWN + endString;
        final String upPath = baseString + UP + endString;
        final String checkedPath = baseString + CHECKED + endString;

        TextButton.TextButtonStyle style = getTextButtonStyle();

        File f = new File(disabledPath);
        boolean isExists = false;
        if (f.exists()) {
            isExists = true;
            style.disabled = new TextureRegionDrawable(getOrCreateR(disabledPath));
        }
        f = new File(overPath);
        if (f.exists()) {
            isExists = true;
            style.over = new TextureRegionDrawable(getOrCreateR(overPath));
        }
        f = new File(downPath);
        if (f.exists()) {
            isExists = true;
            style.down = new TextureRegionDrawable(getOrCreateR(downPath));
        }
        f = new File(upPath);
        if (f.exists()) {
            isExists = true;
            style.up = new TextureRegionDrawable(getOrCreateR(upPath));
        }
        f = new File(checkedPath);
        if (f.exists()) {
            isExists = true;
            style.checked = new TextureRegionDrawable(getOrCreateR(checkedPath));
        }

        if (!isExists) {
            style.up = new TextureRegionDrawable(getOrCreateR(baseImagePath));
        }

        return style;
    }
}
