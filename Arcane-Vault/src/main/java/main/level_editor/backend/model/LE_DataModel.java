package main.level_editor.backend.model;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.level_editor.backend.brush.LE_Brush;
import main.level_editor.backend.display.LE_DisplayMode;
import main.level_editor.backend.functions.mouse.MouseMode;
import main.level_editor.backend.functions.selection.LE_Selection;
import main.level_editor.backend.functions.selection.PaletteSelection;
import main.level_editor.gui.tree.data.LE_DataNode;
import main.level_editor.gui.tree.data.LE_TreeBuilder;
import main.level_editor.gui.tree.data.LayeredData;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_DataModel {

    MouseMode mouseMode;
    LE_DisplayMode displayMode;
    LE_Selection selection;
    LE_Brush brush;
    PaletteSelection paletteSelection;

    LE_DataNode treeModel;
    private LevelZone currentZone;
    private Module module;
    private LevelBlock block;
    private ObjType defaultWallType;

    public LE_DataModel() {
    }

    public LE_DataNode getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(LayeredData data) {
        this.treeModel = new LE_TreeBuilder(data).getRoot();
        GuiEventManager.trigger(GuiEventType.LE_TREE_RESET, treeModel);
    }

    public MouseMode getMouseMode() {
        return mouseMode;
    }

    public void setMouseMode(MouseMode mouseMode) {
        this.mouseMode = mouseMode;
    }

    public LE_DisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(LE_DisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    public LE_Selection getSelection() {
        return selection;
    }

    public void setSelection(LE_Selection selection) {
        this.selection = selection;
    }

    public PaletteSelection getPaletteSelection() {
        return paletteSelection;
    }

    public void setPaletteSelection(PaletteSelection paletteSelection) {
        this.paletteSelection = paletteSelection;
    }

    public LevelZone getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(LevelZone currentZone) {
        this.currentZone = currentZone;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public LevelBlock getBlock() {
        return block;
    }

    public void setBlock(LevelBlock block) {
        this.block = block;
    }

    public ObjType getDefaultWallType() {
        if (defaultWallType == null) {
            defaultWallType = DataManager.getType("Stone Wall");
        }
        return defaultWallType;
    }

    public void setDefaultWallType(ObjType defaultWallType) {
        this.defaultWallType = defaultWallType;
    }
}
