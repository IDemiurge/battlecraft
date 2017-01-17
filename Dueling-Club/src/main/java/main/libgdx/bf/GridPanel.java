package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.PointX;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.GameScreen;
import main.libgdx.anims.particles.lighting.LightMap;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.libgdx.bf.mouse.GridMouseListener;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.texture.TextureManager;
import main.system.EventCallbackParam;
import main.system.GuiEventType;
import main.system.GuiEventManager;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class GridPanel extends Group {
    private static final String backgroundPath = "UI/custom/grid/GRID_BG_WIDE.png";
    private static final String emptyCellPath = "UI/cells/Empty Cell v3.png";
    private static final String hiddenCellPath = "UI/cells/Hidden Cell v2.png";
    private static final String highlightCellPath = "UI/cells/Highlight Green Cell v3.png";
    private static final String unknownCellPath = "UI/cells/Unknown Cell v2.png";
    private static final String cellBorderPath = "UI\\CELL for 96.png";
    protected Texture emptyImage;
    protected Texture hiddenImage;
    protected Texture highlightImage;
    protected Texture unknownImage;
    protected Texture cellBorderTexture;
    protected DequeImpl<DC_HeroObj> units;
    protected GridCell[][] cells;
    private LightMap lightMap;
    private CellBorderManager cellBorderManager;
    private Map<DC_HeroObj, BaseView> unitMap;
    private int cols;
    private int rows;
    private LightingManager lightingManager;

    public GridPanel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public int getCellWidth() {
        return cellBorderTexture.getWidth();
    }

    public int getCellHeight() {
        return cellBorderTexture.getHeight();
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public Point getPointForCoordinateWithOffset(Coordinates sourceCoordinates) {
        InputController controller = GameScreen.getInstance().getController();
        int x = (int) (
//         controller.getX_cam_pos()/2
         +sourceCoordinates.getX() * getCellWidth() / controller.getZoom());
        int y = (int) (
//         controller.getY_cam_pos()/2
         +(rows - sourceCoordinates.getY()) * getCellHeight() / controller.getZoom());
        return new PointX(x, y);
    }

    public Vector2 getVectorForCoordinateWithOffset(Coordinates sourceCoordinates
    ) {
        return getVectorForCoordinateWithOffset(sourceCoordinates, true);
    }

    public Vector2 getVectorForCoordinateWithOffset(Coordinates sourceCoordinates
     , boolean centered) {
        InputController controller = GameScreen.getInstance().getController();
        float x = sourceCoordinates.getX() * getCellWidth() / controller.getZoom();
        float y = (rows - sourceCoordinates.getY()) * getCellHeight() / controller.getZoom();
        if (centered) {
            x+=getCellWidth()/2;
            y-=getCellHeight()/2;
        }
        return new Vector2(
         x, y);
    }

    public GridPanel init() {
        setUnitMap(new HashMap<>());
        emptyImage = TextureManager.getOrCreate(emptyCellPath);
        hiddenImage = TextureManager.getOrCreate(hiddenCellPath);
        highlightImage = TextureManager.getOrCreate(highlightCellPath);
        unknownImage = TextureManager.getOrCreate(unknownCellPath);
        cellBorderTexture = TextureManager.getOrCreate(cellBorderPath);

        cells = (new GridCell[cols][rows]);

        setCellBorderManager(new CellBorderManager(emptyImage.getWidth(), emptyImage.getHeight()));
        int rows1 = rows - 1;
        int cols1 = cols - 1;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new GridCell(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * emptyImage.getWidth());
                cells[x][y].setY(y * emptyImage.getHeight());
                addActor(cells[x][y].init());
            }
        }
        bindEvents();

        /*
        LIGHT_EMISSION
         ILLUMINATION
           CONCEALMENT*/

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);


        addListener(new GridMouseListener(this, cells, unitMap));
        return this;
    }

    private void bindEvents() {
        GuiEventManager.bind(SELECT_MULTI_OBJECTS, obj -> {
            Pair<Set<DC_Obj>, TargetRunnable> p =
             (Pair<Set<DC_Obj>, TargetRunnable>) obj.get();
            Map<Borderable, Runnable> map = new HashMap<>();
            for (DC_Obj obj1 : p.getLeft()) {
                Borderable b = unitMap.get(obj1);
                if (b == null)
                    b = cells[obj1.getX()][rows - 1 - obj1.getY()];
                map.put(b, () -> p.getRight().run(obj1));
            }
            GuiEventManager.trigger(SHOW_BLUE_BORDERS, new EventCallbackParam(map));
        });
        GuiEventManager.bind(UNIT_MOVED, param -> {
                moveUnitView((DC_HeroObj) param.get());
         });

        GuiEventManager.bind(INGAME_EVENT_TRIGGERED, param -> {
            main.game.event.Event event = (main.game.event.Event) param.get();
            Ref r = event.getRef();
            boolean caught = false;
            if (event.getType() == STANDARD_EVENT_TYPE.EFFECT_APPLIES)
            {
                GuiEventManager.trigger (GuiEventType.EFFECT_APPLIED,
             new EventCallbackParam<>(event.getRef().getEffect())) ;
                caught=true;
            }


                    if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED
                     || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE
                     || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE)
//                (r.getEffect() instanceof ChangeFacingEffect) nice try
                    {
                        DC_HeroObj hero = (DC_HeroObj) r.getObj(KEYS.TARGET
                        );
                        BaseView view = unitMap.get(hero);
                        if (view instanceof UnitView) {
                            UnitView unitView = ((UnitView) view);
                            unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
                        }
                        caught = true;
                    }






                    if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED
                     || event.getType() == STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                        removeUnitView((DC_HeroObj) r.getSourceObj());
                        caught = true;
                    }

                    if (event.getType() == STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
                        moveUnitView((DC_HeroObj) r.getSourceObj());
                        caught = true;
                    }
                    if (event.getType() == STANDARD_EVENT_TYPE.UNIT_SUMMONED) {
                        addUnitView((DC_HeroObj) r.getObj(KEYS.SUMMONED));
                        caught = true;
                    }

                    if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                        caught = true;
                /*switch (event.getType().getArg()) {
                    case "Spellpower":
                        int a = 10;
                        break;
                }*/
                    }
                    if (event.getType().name().startsWith("PROP_"))
                        caught = true;
                    if (event.getType().name().startsWith("ABILITY_"))
                        caught = true;
                    if (event.getType().name().startsWith("EFFECT_"))
                        caught = true;

                    if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                        switch (event.getType().getArg()) {
                            case "Illumination":
                                if (getLightMap() != null) {
                                    Obj o = event.getRef().getTargetObj();
                                    if (o instanceof DC_HeroObj) {
                                        getLightMap().updateObject((DC_HeroObj) event.getRef().getTargetObj());
                                    }
                                }
                                caught = true;
                                break;
                        }

                        caught = true;
                    }

                    if (!caught) {
                        System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
                    }
                });


        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            DC_HeroObj hero = (DC_HeroObj) obj.get();
            BaseView view = unitMap.get(hero);
            if (view.getParent() != null) {
                ((GridCellContainer) view.getParent()).popupUnitView(view);
            }
            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_GREEN_BORDER, new EventCallbackParam(view));
            } else {
                GuiEventManager.trigger(SHOW_RED_BORDER, new EventCallbackParam(view));
            }
        });

        GuiEventManager.bind(CREATE_UNITS_MODEL, param -> {

            units = (DequeImpl<DC_HeroObj>) param.get();
            LightMap lightMap = new LightMap(units, cells[0][0].getWidth(), cells[0][0].getHeight(), rows, cols);
            lightingManager = new LightingManager(lightMap, this);
            if (lightMap.isValid()) {
                setLightMap(lightMap);
            }


            Map<Coordinates, List<DC_HeroObj>> map = new HashMap<>();
            for (DC_HeroObj object : units) {
                Coordinates c = object.getCoordinates();
                if (!map.containsKey(c)) {
                    map.put(c, new ArrayList<>());
                }
                List<DC_HeroObj> list = map.get(c);
                list.add(object);
            }

            for (Coordinates coordinates : map.keySet()) {
                List<UnitViewOptions> options = new ArrayList<>();
                List<UnitViewOptions> overlays = new ArrayList<>();

                for (DC_HeroObj object : map.get(coordinates)) {
                    if (!object.isOverlaying()) {
                        options.add(new UnitViewOptions(object, unitMap));
                    } else {
                        overlays.add(new UnitViewOptions(object, unitMap));
                    }
                }

                GridCellContainer cellContainer = new GridCellContainer(emptyImage, coordinates.getX(), coordinates.getY()).init();
                cellContainer.setObjects(options);
                cellContainer.setOverlays(overlays);

                cells[coordinates.getX()][rows - 1 - coordinates.getY()].addInnerDrawable(cellContainer);
            }
        });

       /* GuiEventManager.bind(CELL_UPDATE, param -> {
            Coordinates cords = (Coordinates) param.get();

            List<DC_HeroObj> objList = units.stream()
                    .filter(microObj -> microObj.getCoordinates().equals(cords))
                    .collect(Collectors.toList());

            List<UnitViewOptions> options = new ArrayList<>();
            for (DC_HeroObj microObj : objList) {
                options.add(new UnitViewOptions(microObj, unitMap));
            }

            if (options.size() == 0) {
                cells[cords.getX()][cords.getY()].addInnerDrawable(null);
            } else {
                GridCellContainer cellContainer = new GridCellContainer(cellBorderTexture, cords.getX(), cords.getY()).init();
                cellContainer.setObjects(options);

                if (cells[cords.getX()][cords.getY()].getInnerDrawable() != null) {
                    cells[cords.getX()][cords.getY()].addInnerDrawable(cellContainer);
                } else {
                    cells[cords.getX()][cords.getY()].updateInnerDrawable(cellContainer);
                }
            }

        });
*/

    }

    private void moveUnitView(DC_HeroObj heroObj) {
        int rows1 = rows - 1;
        BaseView uv = unitMap.get(heroObj);
        Coordinates c = heroObj.getCoordinates();
        if (cells[c.x][rows1 - c.y].getInnerDrawable() == null) {
            GridCellContainer cellContainer = new GridCellContainer(cells[c.x][rows1 - c.y]).init();
            cells[c.x][rows1 - c.y].addInnerDrawable(cellContainer);
        }
        uv.setVisible(true);
        cells[c.x][rows1 - c.y].getInnerDrawable().addActor(uv);

        getCellBorderManager().updateBorderSize();

        if (getLightMap() != null) {
            getLightMap().updatePos(heroObj);
        }
        GuiEventManager.trigger(UPDATE_LIGHT, null );
    }

    private void addUnitView(DC_HeroObj heroObj) {
        UnitViewOptions uvo = new UnitViewOptions(heroObj, unitMap);
        new UnitView(uvo);
        moveUnitView(heroObj);
    }

    private void removeUnitView(DC_HeroObj obj) {
        BaseView uv = unitMap.get(obj);
        GridCellContainer gridCellContainer = (GridCellContainer) uv.getParent();
        gridCellContainer.removeActor(uv);
        uv.setVisible(false);
        GuiEventManager.trigger(UPDATE_LIGHT, null );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //physx.getUnit(unit).setTransform(getX(),getY());
    }

    public Actor hitChildren(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y].draw(batch, parentAlpha);
            }
        }

        getCellBorderManager().draw(batch, parentAlpha);

        if (getLightMap() != null) {
            getLightMap().updateLight();
        }
    }


    public LightMap getLightMap() {
        return lightMap;
    }

    public void setLightMap(LightMap lightMap) {
        this.lightMap = lightMap;
    }

    public CellBorderManager getCellBorderManager() {
        return cellBorderManager;
    }

    public void setCellBorderManager(CellBorderManager cellBorderManager) {
        this.cellBorderManager = cellBorderManager;
    }

    public Map<DC_HeroObj, BaseView> getUnitMap() {
        return unitMap;
    }

    public void setUnitMap(Map<DC_HeroObj, BaseView> unitMap) {
        this.unitMap = unitMap;
    }
}
