package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.ActionPanel;
import eidolons.libgdx.gui.panels.dc.inventory.CombatInventory;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.dc.menus.outcome.OutcomeDatasource;
import eidolons.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.neo.UnitInfoPanelNew;
import eidolons.libgdx.gui.panels.dc.unitinfo.old.UnitInfoPanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 3/31/2017.
 */
public class BattleGuiStage extends GuiStage {

    private final InitiativePanel initiativePanel;
    private final ActionPanel bottomPanel;
    private final GuiVisualEffects guiVisualEffects;
    private final CombatInventory combatInventory;
    public static  OrthographicCamera camera;

    public BattleGuiStage(ScreenViewport viewport, Batch batch) {
        super(viewport == null ?
//         new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
//          GdxMaster.getHeight(), new OrthographicCamera())
          new FillViewport(GdxMaster.getWidth(),
           GdxMaster.getHeight(),  new OrthographicCamera())
//        new ScreenViewport( new OrthographicCamera())
        : viewport,
         batch == null ? new SpriteBatch() :
          batch);
        addActor(guiVisualEffects = new GuiVisualEffects());
        initiativePanel = new InitiativePanel();
        initiativePanel.setPosition(0, GdxMaster.getHeight() - initiativePanel.getHeight());
        addActor(initiativePanel);
        bottomPanel = new ActionPanel(0, 0);
        addActor(bottomPanel);

        addActor(
         isNewUnitInfoPanel()?   UnitInfoPanelNew.getInstance():
         new UnitInfoPanel(0, 0));
        init();
        combatInventory = new CombatInventory();
        combatInventory.setPosition(0, GdxMaster.getHeight() - combatInventory.getHeight());
        this.addActor(combatInventory);


    }

    private boolean isNewUnitInfoPanel() {
        return false;
    }

    @Override
    public void outsideClick() {
        setDraggedEntity(null);
        super.outsideClick();
        if (combatInventory.isVisible()) {
//            combatInventory.close(ExplorationMaster.isExplorationOn());
            InventoryDataSource dataSource = (InventoryDataSource) combatInventory.getUserObject();
            if (ExplorationMaster.isExplorationOn()) {
                dataSource.getDoneHandler().run();
            } else
                dataSource.getCancelHandler().run();
        }
    }

    protected void bindEvents() {
        super.bindEvents();

        GuiEventManager.bind(GuiEventType.GAME_FINISHED, p -> {
            if (outcomePanel != null)
                outcomePanel.remove();
            outcomePanel = new OutcomePanel(new OutcomeDatasource((DC_Game) p.get()));
            addActor(outcomePanel);
            outcomePanel.setZIndex(getActors().size);
//            outcomePanel.setColor(new Color(1, 1, 1, 0));
//            ActorMaster.addFadeInOrOut(outcomePanel, 2.5f);
            float y = GdxMaster.getHeight() -
             (GdxMaster.getHeight() - outcomePanel.getHeight() / 2);
            float x = (GdxMaster.getWidth() - outcomePanel.getWidth()) / 2;
            outcomePanel.setPosition(x, y + outcomePanel.getHeight());
            ActorMaster.addMoveToAction(outcomePanel, x, y, 2.5f);
        });

    }


    @Override
    public void act() {
        super.act();
        if (outcomePanel != null)
            outcomePanel.setZIndex(Integer.MAX_VALUE);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }


/*    @Override
    public void draw() {
        final Matrix4 combined = getCamera().combined.cpy();
        getCamera().update();

        final Group root = getRoot();

        if (!root.isVisible()) return;

        combined.setToOrtho2D(0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(combined);
        batch.begin();
        root.draw(batch, 1);
        batch.end();
    }*/

    public InitiativePanel getInitiativePanel() {
        return initiativePanel;
    }

    public ActionPanel getBottomPanel() {
        return bottomPanel;
    }

    public GuiVisualEffects getGuiVisuals() {
        return guiVisualEffects;
    }
}
