package main.game.bf.directions;

/**
 * Created by Giskard on 6/9/2018.
 */
public enum DIRECTION {
    UP(false, 90, true, null, false),
    DOWN(false, 270, true, null, true),
    LEFT(false, 180, false, false, null),
    RIGHT(false, 360, false, true, null),
    UP_LEFT(true, 135, true, false, false),
    UP_RIGHT(true, 45, true, true, false),
    DOWN_RIGHT(true, 225, true, true, true),
    DOWN_LEFT(true, 315, true, false, true),;

    public static final DIRECTION[] values = DIRECTION.values();
    public Boolean growX;
    public Boolean growY;
    private boolean vertical;
    private boolean diagonal;
    private int degrees;

    DIRECTION(boolean diagonal, int degrees, boolean vertical,
              Boolean growX, Boolean growY) {
        this.vertical = vertical;
        this.growX = growX;
        this.growY = growY;
        this.diagonal = diagonal;
        this.degrees = degrees;
    }

    public DIRECTION getXDirection() {
        if (this == RIGHT || this == UP_RIGHT || this == DIRECTION.DOWN_RIGHT) {
            return RIGHT;
        }
        if (this == LEFT || this == DOWN_LEFT || this == DIRECTION.UP_LEFT) {
            return LEFT;
        }
        return null;
    }

    public DIRECTION getYDirection() {
        if (this == UP || this == UP_RIGHT || this == DIRECTION.UP_LEFT) {
            return UP;
        }
        if (this == DOWN || this == DOWN_RIGHT || this == DIRECTION.DOWN_LEFT) {
            return DOWN;
        }
        return null;
    }

    public boolean isDiagonal() {
        return diagonal;
    }

    public void setDiagonal(boolean diagonal) {
        this.diagonal = diagonal;
    }

    public int getDegrees() {
        return degrees;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    public Boolean isGrowX() {
        return growX;
    }

    public Boolean isGrowY() {
        return growY;
    }

    public boolean isVertical() {
        return vertical;
    }

    public DIRECTION rotate90(boolean clockwise) {
        return DirectionMaster.rotate90(this, clockwise);
    }

    public DIRECTION rotate45(boolean clockwise) {
        return DirectionMaster.rotate45(this, clockwise);
    }

    public DIRECTION rotate180() {
        return DirectionMaster.rotate180(this);
    }

    public DIRECTION flip() {
        return DirectionMaster.flip(this);
    }

}
