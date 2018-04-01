package eidolons.libgdx.screens.map.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import main.data.filesys.PathFinder;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.dc.TabbedPanel;
import eidolons.libgdx.gui.panels.dc.ValueContainer;
import eidolons.libgdx.screens.map.editor.EditorControlPanel.MAP_EDITOR_MOUSE_MODE;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.graphics.FontMaster.FONT;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/20/2018.
 */
public class EmitterPalette extends TabbedPanel {

    private String selected;
    private ValueContainer selectedLabel;

    public EmitterPalette() {
        updateRequired = true;
        GuiEventManager.bind(MapEvent.TIME_CHANGED, p -> setUpdateRequired(true));
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        init();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public String getSelectedEmitterPath() {
        return selected;
    }

    public void init() {
        clear();
        setSize(GdxMaster.getWidth() - 300, 256);
//        int columns = (int) (getWidth() / 64);
        defaults().padLeft(200).top().right().width(GdxMaster.getWidth() - 300);
        Map<String, List<File>> presets = new LinkedHashMap<>();
        List<File> subfolders = FileManager.getFilesFromDirectory(
         PathFinder.getSfxPath(), true);
        subfolders.forEach(file -> {
            if (!file.isDirectory()) {
                MapMaster.addToListMap(presets, "main", file);
            } else
                presets.put(file.getName(), FileManager.getFilesFromDirectory(
                 file.getPath(), false));
        });
        LabelStyle style = StyleHolder.getSizedLabelStyle(FONT.MAIN, 15);
        for (String sub : presets.keySet()) {
            HorizontalFlowGroup table = new HorizontalFlowGroup(0);
            table.setWidth(getWidth() - 100);
            boolean bg = presets.get(sub).size() < 55;
            for (File preset : presets.get(sub)) {
                ValueContainer label = //textButton?
                 new ValueContainer(new Label(preset.getName(), style));
                NinePatch patch = NinePatchFactory.getTooltip();
                patch.scale(0.7f, 0.7f);
                if (bg)
                    label.setBackground(new NinePatchDrawable(patch));
                label.addListener(new ClickListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
//                            EmitterMaster.

                        }
                        EditorManager.setMode(MAP_EDITOR_MOUSE_MODE.EMITTER);
                        if (selectedLabel != null)
                            selectedLabel.setColor(1, 1, 1, 1);

                        if (sub.equals("main"))
                            selected = preset.getName();
                        else selected = StrPathBuilder.build(sub, preset.getName());
                        selectedLabel = label;
                        label.setColor(1, 0.3f, 0.8f, 1);
                        return super.touchDown(event, x, y, pointer, button);
                    }
                });
                table.addActor(label);
            }
            addTab(table, sub);

        }

    }


    public enum EMITTER_PALETTE_TAB {
        MIST,
        SMOKE,
        FIRE,
        MAGIC,
        COLD,
        WATER,

    }
}