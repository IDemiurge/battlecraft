package main.level_editor.gui.tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;
import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.ai.AiData;
import main.level_editor.backend.struct.boss.BossDungeon;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.backend.struct.module.LE_Block;
import main.level_editor.backend.struct.module.LE_Module;
import main.level_editor.backend.struct.module.ObjNode;
import main.level_editor.gui.components.TreeX;
import main.level_editor.gui.tree.data.LE_DataNode;
import main.level_editor.gui.tree.data.LayeredData;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;

public class LE_TreeView extends TreeX<LE_DataNode> {


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected void doubleClick(LE_DataNode node) {
        Coordinates c = null;
        if (node.getData() instanceof ObjNode) {
            c = ((ObjNode) node.getData()).getObj().getCoordinates();
            LevelEditor.getManager().getSelectionHandler().
                    select(((ObjNode) node.getData()).getObj());
        } else {
            //center coordinate

        }
        Vector2 v = GridMaster.getCenteredPos(c);
        CameraMan.MotionData data = new CameraMan.MotionData(v, 0.5f, null);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, data);

//        data =  new CameraMan.MotionData(z, 0.5f, null);
//        GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, data);
    }

    @Override
    protected void selected(LE_DataNode node) {
        if (node.getData() instanceof ObjNode) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                LevelEditor.getManager().getSelectionHandler().addToSelected(
                        ((ObjNode) node.getData()).getObj());
            } else {
                LevelEditor.getManager().getSelectionHandler().select(((ObjNode) node.getData()).getObj());
            }
        } else if (node.getData() instanceof LE_Block) {
            LevelBlock block = ((LE_Block) node.getData()).getBlock();
            LevelEditor.getModel().setBlock(block);
            LevelEditor.getModel().setCurrentZone(block.getZone());
        } else if (node.getData() instanceof LevelZone) {
            LevelEditor.getModel().setCurrentZone(((LevelZone) node.getData()));
            LevelEditor.getModel().setBlock(((LevelZone) node.getData()).getSubParts().get(0));
            //camera!
        } else if (node.getData() instanceof Module) {

        } else if (node.getData() instanceof Layer) {
            //select all?


            // if on > select
            // if off > preview
            //
        }
    }

    @Override
    protected Actor createNodeComp(LE_DataNode node) {

        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 16);
        TextureRegion texture = null;
        if (node == null) {
            return new Label("NULL", VisUI.getSkin());
        }
        if (node.getData() == null) {
            return new Label("NULL", VisUI.getSkin());
        }
        String name = node.getData().toString();
        String val = null;
        float w = 64;
        float h = 64;

        Color c = null;
        if (node.getData() instanceof Campaign) {
            Campaign campaign = ((Campaign) node.getData());
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 16);
            texture = TextureCache.getOrCreateR(campaign.getImagePath());
        }
        if (node.getData() instanceof BossDungeon) {
            BossDungeon dungeon = ((BossDungeon) node.getData());
        }
        if (node.getData() instanceof Floor) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 21);
            Floor floor = ((Floor) node.getData());
            texture = TextureCache.getOrCreateR(floor.getGame().getDungeon().getImagePath());
        }
        if (node.getData() instanceof LE_Module) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 21);
            LE_Module module = ((LE_Module) node.getData());
            name = "--- " + module.toString();
            texture = TextureCache.getOrCreateR(Images.ITEM_BACKGROUND_STONE);
        }
        if (node.getData() instanceof LevelZone) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 19);
            LevelZone zone = ((LevelZone) node.getData());
            name = "-- " + "Zone " + zone.toString();
//            val = zone.getSubParts().size();
            zone.getTemplateGroup();
            zone.getStyle();
        }
        if (node.getData() instanceof LevelBlock) {
            LevelBlock block = ((LevelBlock) node.getData());
            name = "- " + (block.isTemplate() ? "Template " : "Custom ") + block.getRoomType() + " [" +
                    block.getSquare() +
                    "]";
            c = LevelEditor.getCurrent().getManager().getStructureManager().getColorForBlock(block);

            //img per room type
//            texture = TextureCache.getOrCreateR(Images.ITEM_BACKGROUND_STONE);
            texture = TextureCache.getOrCreateR(Images.COLOR_EMBLEM);
        }
        if (node.getData() instanceof ObjNode) { // or id?
            ObjNode dcObj = ((ObjNode) node.getData());
            texture = TextureCache.getOrCreateR(dcObj.getObj().getImagePath());
            if (dcObj.getObj().getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
                name = "[Encounter]" + name;
                //get ai info!
            }
            AiData ai = LevelEditor.getManager().getAiHandler().getAiForEncounter(dcObj.getObj());
        }
//        if (node.getData() instanceof String) {
//other stuff...
//        }
        ValueContainer actor = new ValueContainer(style, texture, name, val);
        if (c != null) {
            actor.getImageContainer().getActor().setColor(c);
        }

        if (actor.getImageContainer().getActor() != null) {
            actor.getImageContainer().size(w, h);
            actor.getImageContainer().getActor().setSize(w, h);
        }
        return (actor);
    }

    public String toString(LayeredData data) {
        switch (data.getClass().getSimpleName()) {

        }
        return super.toString();
    }
}
