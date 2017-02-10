package main.test.libgdx.prototype;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import main.data.filesys.PathFinder;
import main.libgdx.bf.Background;
import main.libgdx.old.ActionGroup;
import main.libgdx.old.ActiveUnitInfoPanel;
import main.libgdx.old.TargetUnitInfoPanel;
import main.libgdx.old.TopPanel;

/**
 * Created by PC on 07.11.2016.
 */
public class GUIStage extends Stage {

   static float viewport_width = 1600;
    static float viewport_height = 900;
    private Background background;
    private TopPanel topPanel;

    private TargetUnitInfoPanel unitInfoPanel;
    private ActiveUnitInfoPanel activeUnitInfoPanel;
    private ActionGroup actionGroup;


    public GUIStage() {

        super(new FitViewport(viewport_width,viewport_height));
        setDebugAll(true);
//        background = new DC_GDX_Background(PathFinder.getImagePath()).init();
        topPanel = new TopPanel(PathFinder.getImagePath()).init();
        topPanel.setX(viewport_width/2-topPanel.getWidth()/2);
        topPanel.setY(viewport_height - topPanel.getHeight());

        unitInfoPanel = new TargetUnitInfoPanel(PathFinder.getImagePath()).init();
        unitInfoPanel.setX(viewport_width - unitInfoPanel.getWidth());
        unitInfoPanel.setY(viewport_height - unitInfoPanel.getHeight());
//        unitInfoPanel.setX(Gdx.graphics.getWidth() - unitInfoPanel.getWidth());
//        unitInfoPanel.setY(Gdx.graphics.getHeight() - unitInfoPanel.getHeight());

        activeUnitInfoPanel = new ActiveUnitInfoPanel(PathFinder.getImagePath()).init();
        activeUnitInfoPanel.setX(0);
//        activeUnitInfoPanel.setY(Gdx.graphics.getHeight() - activeUnitInfoPanel.getHeight());
        activeUnitInfoPanel.setY(viewport_height - activeUnitInfoPanel.getHeight());

        actionGroup = new ActionGroup(PathFinder.getImagePath()).init();
        actionGroup.setY(10);
//        actionGroup.setX(Gdx.graphics.getWidth() / 2 - actionGroup.getWidth() / 2);
        actionGroup.setX(viewport_width / 2 - actionGroup.getWidth() / 2);
    }

    @Override
    public void draw() {
        super.draw();
//        getBatch().setTransformMatrix(new Matrix4(new Quaternion(new Vector3(15,60,0),15)));
        getBatch().begin();







//        background.draw(getBatch(),1);
        topPanel.draw(getBatch(), 1);
        unitInfoPanel.draw(getBatch(), 1);
        activeUnitInfoPanel.draw(getBatch(), 1);
        actionGroup.draw(getBatch(), 1);
//        Res_holder.font.draw(getBatch(),"Test",400,300);
        getBatch().end();

    }

}
