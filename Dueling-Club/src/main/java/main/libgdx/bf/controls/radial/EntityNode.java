package main.libgdx.bf.controls.radial;

import main.entity.Entity;
import main.entity.obj.unit.DC_HeroObj;

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

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }
}
