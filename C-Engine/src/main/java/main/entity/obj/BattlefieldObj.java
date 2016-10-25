package main.entity.obj;

import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.content.CONTENT_CONSTS.UNIT_TO_UNIT_VISION;
import main.entity.OBJ;
import main.game.battlefield.Coordinates;

public interface BattlefieldObj extends OBJ {

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    Coordinates getCoordinates();

    void setCoordinates(Coordinates coordinates);

    UNIT_TO_UNIT_VISION getUnitVisionStatus();

    UNIT_TO_PLAYER_VISION getActivePlayerVisionStatus();

}
