package eidolons.libgdx.bf.overlays;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.libgdx.bf.grid.OverlayView;
import main.content.enums.entity.UnitEnums;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.MigMaster;

import java.awt.*;

public class OverlayingMaster {
    public static Vector2 getOffset(DIRECTION direction, DIRECTION direction2) {

        Dimension dim1 = getOffsetsForOverlaying(direction, 64, 64);
        Dimension dim2 = getOffsetsForOverlaying(direction2, 64, 64);

//    a graceful mathematical solution was coming...
//    int diff = direction.getDegrees() - direction1.getDegrees();
//        int middle = (direction.getDegrees() + direction1.getDegrees()) / 2;
        int x = dim1.width - dim2.width;
        int y = dim1.height - dim2.height;

//        if (Math.abs(diff) == 45) {
////        int dist = Math
//            if (middle>180){
//        }
//        }

        return new Vector2(x, y);
    }

    public static void moveOverlaying(BattleFieldObject target, Unit source, boolean push) {
        Boolean clockwise = true;
        DIRECTION d = target.getDirection();
        DIRECTION relative = DirectionMaster.getRelativeDirection(target, source  );
        FACING_DIRECTION facing = source.getFacing();
        if (d == null) {
            clockwise=null;
        } else
        switch (relative) {
            case UP:
                if (d.isDiagonal()) {
                    clockwise = d.isGrowX();
                } else
                    clockwise = null;
                break;
            case UP_RIGHT:
                if (!facing.isVertical()) {
                    clockwise = false;
                }
                break;
            case UP_LEFT:
                if (facing.isVertical()) {
                    clockwise = false;
                }
                break;

            case DOWN:
                if (d.isDiagonal()) {
                    clockwise = !d.isGrowX();
                } else
                    clockwise = null;
                break;
            case DOWN_RIGHT:
                if (facing.isVertical()) {
                    clockwise = false;
                }
                break;
            case DOWN_LEFT:
                if (!facing.isVertical()) {
                    clockwise = false;
                }
                break;

            case LEFT:
                if (d.isDiagonal()) {
                    clockwise = !d.isGrowY();
                } else
                    clockwise = null;
                break;
            case RIGHT:
                if (d.isDiagonal()) {
                    clockwise =  d.isGrowY();
                } else
                    clockwise = null;
                break;
        }
        if (clockwise == null) {
            if (d==null ){
                d = facing.getDirection();
                if (!push) {
                    d= d.flip();
                }
            } else {
                if (push) {
                    d = null;
                } else {
                    //try to take/break?
                }
            }
        } else
        {
            if (!push) {
                clockwise = !clockwise;
            }
            d = d.rotate45(clockwise);
        }

        target.setDirection(d);

        GuiEventManager.trigger(GuiEventType.MOVE_OVERLAYING, target);
    }

    public static Dimension getOffsetsForOverlaying(DIRECTION direction,
                                                    int width, int height) {
        return getOffsetsForOverlaying(direction, width, height, null);
    }

    public static Dimension getOffsetsForOverlaying(DIRECTION direction,
                                                    int width, int height, OverlayView view) {

        float scale = view == null ? 0.5f : view.getScale();
        int w = (int) (width / scale);
        int h = (int) (height / scale);
        int calcXOffset = 0;
        int calcYOffset = 0;
        if (direction == null) {
            calcXOffset += (w - width) * (view == null ? OverlayView.SCALE : scale);
            calcYOffset += (h - height) * (view == null ? OverlayView.SCALE : scale);
        } else {
            int size = width;
            int x = MigMaster.getCenteredPosition(w, size);

            if (direction.growX != null)
                x = (direction.growX) ? w - size : 0;


            int y = MigMaster.getCenteredPosition(h, size);

            if (direction.growY != null)
                y = (!direction.growY) ? h - size : 0;


            calcXOffset += x;
            calcYOffset += y;
        }
        return new Dimension(calcXOffset, calcYOffset);
    }
}
