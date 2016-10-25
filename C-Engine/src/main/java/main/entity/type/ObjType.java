package main.entity.type;

import main.content.OBJ_TYPE;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.entity.Ref;
import main.game.Game;

import javax.swing.*;

public class ObjType extends Entity {
    private boolean generated;
    private boolean model;

    public ObjType() {
        this(Game.game);
    }

    public ObjType(ObjType type) {
        this.var = true;
        this.setType(type);
        setOBJ_TYPE_ENUM(type.getOBJ_TYPE_ENUM());
        cloneMaps(type);
        setRef(type.getRef());
        setGame(type.getGame());
        setGenerated(true);
        type.getGame().initType(this);
        // Weaver.inNewThread(this, "initType");
    }

    public ObjType(String typeName) {
        setProperty(G_PROPS.NAME, typeName);
    }

    public ObjType(ObjType type, boolean model) {
        this(type);
        this.setModel(model);
    }

    public ObjType(Game game) {
        this.game = game;
    }

    public ObjType(Ref ref) {
        setRef(ref);
        initType();
    }

    public ObjType(String typeName, OBJ_TYPE TYPE) {
        this((typeName));
        setOBJ_TYPE_ENUM(TYPE);
    }

    @Override
    public void setProperty(PROPERTY name, String value, boolean base) {
        super.setProperty(name, value);
    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

    }

    public ObjType getType() {
        return (type != null) ? type : this;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
        if (ref != null) {
            ref.setSource(id);
            this.game = ref.getGame();
        }

    }

    public ImageIcon getDefaultIcon() {
        return getIcon();
    }

    @Override
    public void toBase() {
        // TODO return to BASE TYPE?

    }

    public void initType() {
        if (game != null && !isInitialized())
            game.initType(this);
    }

    @Override
    public void init() {
    }

    @Override
    public void clicked() {

    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public boolean isModel() {
        return model;
    }

    public void setModel(boolean model) {
        this.model = model;
    }

}