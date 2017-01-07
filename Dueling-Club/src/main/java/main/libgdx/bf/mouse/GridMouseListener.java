package main.libgdx.bf.mouse;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.libgdx.bf.*;
import main.libgdx.bf.mouse.ToolTipManager.ToolTipRecordOption;
import main.libgdx.texture.TextureManager;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.SHOW_TOOLTIP;

/**
 * Created by JustMe on 1/7/2017.
 */
public class GridMouseListener extends InputListener {
    private GridPanel gridPanel;
    private GridCell[][] cells;

    public GridMouseListener(GridPanel gridPanel, GridCell[][] cells) {
        this.gridPanel = gridPanel;
        this.cells = cells;
    }


    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        int cell = (int) (x / cells[0][0].getWidth());
        int row = (int) (y / cells[0][0].getHeight());
        GridCell gridCell = cells[cell][row];
        if (gridCell.getInnerDrawable() != null) {
            GridCellContainer innerDrawable = (GridCellContainer) gridCell.getInnerDrawable();
            Actor a = innerDrawable.hit(x, y, true);
            if (a != null && a instanceof UnitView) {
                UnitView uv = (UnitView) a;
                DC_HeroObj hero = gridPanel.getUnitMap().entrySet().stream()
                        .filter(entry -> entry.getValue() == uv).findFirst()
                        .get().getKey();

                Map<String, String> tooltipStatMap = new HashMap<>();
                tooltipStatMap.put(PARAMS.C_TOUGHNESS.getName(), "Toughness");
                tooltipStatMap.put("C_Endurance", "Endurance");
                tooltipStatMap.put("C_N_Of_Actions", "N_Of_Actions");
                List<ToolTipRecordOption> recordOptions = new ArrayList<>();
                tooltipStatMap.entrySet().forEach(entry -> {
                    ToolTipManager.ToolTipRecordOption recordOption = new ToolTipManager.ToolTipRecordOption();
                    recordOption.curVal = hero.getIntParam(entry.getKey());
                    recordOption.maxVal = hero.getIntParam(entry.getValue());
                    recordOption.name = entry.getValue();
                    recordOption.recordImage = TextureManager.getOrCreate("UI\\value icons\\" + entry.getValue().replaceAll("_", " ") + ".png");
                    recordOptions.add(recordOption);
                });

                ToolTipManager.ToolTipRecordOption recordOption = new ToolTipManager.ToolTipRecordOption();
                recordOption.name = hero.getName();
                recordOptions.add(0, recordOption);

                recordOption = new ToolTipManager.ToolTipRecordOption();
                recordOption.name = hero.getCoordinates().toString();
                recordOptions.add(recordOption);

                recordOption = new ToolTipManager.ToolTipRecordOption();
                recordOption.name = "direction: " + hero.getFacing().getDirection();
                recordOptions.add(recordOption);

                if (hero.isOverlaying()) {
                    recordOption = new ToolTipManager.ToolTipRecordOption();
                    recordOption.name = "direction O: " + hero.getDirection();
                    recordOptions.add(recordOption);
                }

                GuiEventManager.trigger(SHOW_TOOLTIP, new EventCallbackParam(recordOptions));
                return true;
            }
        }
        GuiEventManager.trigger(SHOW_TOOLTIP, new EventCallbackParam(null));
        return true;
    }

            /*
            Entity e = ((Entity) obj);
            obj.isAttack();
            obj.getTargeting() instanceof SelectiveTargeting;
            obj.getTargeting().getFilter().getObjects().contains(Game.game.getCellByCoordinate(new Coordinates(0, 0)));
            obj.isMove();
            obj.isTurn();
            ((Entity) obj).getImagePath();*/

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        try {
            return handleTouchDown(event, x, y, pointer, button);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean handleTouchDown(InputEvent event, float x, float y, int pointer, int button) {
        Actor a;
        a = gridPanel.hitChildren(x, y, true);
        if (a != null && a instanceof GridCell) {
            GridCell cell = (GridCell) a;
            if (gridPanel.getCellBorderManager().isBlueBorderActive() && event.getButton() == 0) {
                Borderable b = cell;
                if (cell.getInnerDrawable() != null) {
                    Actor unit = cell.getInnerDrawable().hit(x, y, true);
                    if (unit != null && unit instanceof Borderable) {
                        b = (Borderable) unit;
                    }
                }
                gridPanel.getCellBorderManager().hitAndCall(b);
            }


            if (cell.getInnerDrawable() != null) {
                Actor unit = cell.getInnerDrawable().hit(x, y, true);
                if (unit != null && unit instanceof UnitView) {
                    if (event.getButton() == 1) {
                        DC_HeroObj heroObj = gridPanel.getUnitMap().entrySet()
                                .stream().filter(entry -> entry.getValue() == unit).findFirst()
                                .get().getKey();
                        Triple<DC_Obj, Float, Float> container = new ImmutableTriple<>(heroObj, x, y);
                        GuiEventManager.trigger(CREATE_RADIAL_MENU, new EventCallbackParam(container));
                    }
                }
            } else if (event.getButton() == 1) {
                DC_Obj dc_cell = DC_Game.game.getCellByCoordinate(
                        new Coordinates(cell.getGridX(), cell.getGridY()));
                Triple<DC_Obj, Float, Float> container = new ImmutableTriple<>(dc_cell, x, y);
                GuiEventManager.trigger(CREATE_RADIAL_MENU, new EventCallbackParam(container));
            }
        }
        return false;
    }
}
