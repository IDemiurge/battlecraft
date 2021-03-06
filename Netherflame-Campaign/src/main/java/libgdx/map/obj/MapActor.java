package libgdx.map.obj;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.bf.SuperActor;
import libgdx.bf.generic.SuperContainer;
import libgdx.texture.TextureCache;
import eidolons.macro.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 2/7/2018.
 */
public class MapActor extends SuperActor {
    protected final TextureRegion originalTexture;
    protected SuperContainer highlight;
    protected Image portrait;
    MAP_OBJ_INFO_LEVEL infoLevel;

    public MapActor(TextureRegion portraitTexture) {
        addActor(portrait = new Image(portraitTexture));
        originalTexture = portraitTexture;
    }

    public void init() {
        highlight = new SuperContainer(new Image(TextureCache.getOrCreateR(
         StrPathBuilder.build(PathFinder.getShadeCellsPath(), "a new light.png"))), true);
        highlight.setTouchable(Touchable.disabled);
        highlight.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT);
        highlight.setColor(getTeamColor());
//        highlight.setPosition(highlight.getWidth()/2, highlight.getHeight()/2);
        addActor(highlight);
//        highlight.setSize(getWidth()*1.2f,getWidth()*1.2f);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void move(Vector2 destination) {

    }

    @Override
    public float getWidth() {
        return portrait.getWidth();
    }

    @Override
    public float getHeight() {
        return portrait.getHeight();
    }

    public Image getPortrait() {
        return portrait;
    }
}
