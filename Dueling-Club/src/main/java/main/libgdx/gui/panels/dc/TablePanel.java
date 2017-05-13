package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.function.Supplier;

public class TablePanel<T extends Actor> extends Table {

    protected boolean updateRequired;

    public TablePanel() {
    }

    public Cell<T> addElement(T el) {
        return add(el).grow();
    }

    public void addEmptyRow(int w, int h) {
        addEmpty(w, h);
        row();
    }

    public void addEmpty(int w, int h) {
        Actor actor = new Actor();
        actor.setSize(w, h);
        add(actor);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setBackground(Drawable background) {
        if (background instanceof TextureRegionDrawable) {
            final TextureRegionDrawable drawable = ((TextureRegionDrawable) background);
            final TextureRegion region = drawable.getRegion();
            setSize(region.getRegionWidth(), region.getRegionHeight());
        }
        super.setBackground(background);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (updateRequired) {
            updateAct(delta);
            invalidate();
            afterUpdateAct(delta);
            updateRequired = false;
        }
    }

    public void afterUpdateAct(float delta) {

    }

    public void updateAct(float delta) {

    }

    @Override
    public Object getUserObject() {
        final Object userObject = super.getUserObject();
        if (userObject instanceof Supplier) {
            return ((Supplier) userObject).get();
        } else {
            return userObject;
        }
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        getChildren().forEach(ch -> ch.setUserObject(userObject));
        updateRequired = true;
    }
}
