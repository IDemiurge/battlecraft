package main.libgdx.bf.controls.radial;

import main.entity.Entity;
import main.entity.obj.DC_HeroObj;

import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class EntityNode implements RADIAL_ITEM {
    private Entity spell;

    public EntityNode(Entity s) {
        spell = s;
    }

    @Override
    public List<RADIAL_ITEM> getItems(DC_HeroObj source) {
        return null;
    }

    @Override
    public Object getContents() {
        return spell;
    }

    @Override
    public String getTexturePath() {
        return ((Entity) getContents()).getImagePath();
    }
}