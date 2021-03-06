package main.level_editor.backend.struct.module;

import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.ObjNode;
import main.data.tree.LayeredData;
import main.level_editor.LevelEditor;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LayerNode implements LayeredData<LayeredData> {
    private final Set<LayeredData> objs;

    public LayerNode(Layer layer) {
        objs =
        layer.getIds().stream().map(id ->
                new ObjNode(LevelEditor.getCurrent().getManager().getIdManager().getObjectById(id))).
                collect(Collectors.toCollection(LinkedHashSet::new) );


//        layer.getScripts().stream().map(id ->
//                new ObjNode(LevelEditor.getCurrent().getManager().getIdManager().getObjectById(id))).
//                collect(Collectors.toSet());
    }

    @Override
    public Collection<LayeredData> getChildren() {
        return objs;
    }
}
