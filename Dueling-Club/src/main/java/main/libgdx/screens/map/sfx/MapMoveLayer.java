package main.libgdx.screens.map.sfx;

import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.screens.map.sfx.MapMoveLayers.MAP_AREA;
import main.libgdx.screens.map.sfx.MapMoveLayers.MAP_MOVING_LAYER_TYPE;
import main.system.auxiliary.RandomWizard;

/**
 * Created by JustMe on 2/19/2018.
 */
public class MapMoveLayer extends ImageContainer {
    private float originalX, originalY, maxDistance, speed, shakiness, directionModX, directionModY;
    private MAP_AREA spawnArea;
    MAP_MOVING_LAYER_TYPE type;
    public float rotationMod= RandomWizard.random()? 1: -1;

    public MapMoveLayer(String path, MAP_AREA spawnArea, MAP_MOVING_LAYER_TYPE type) {
        super(path);
        this.spawnArea = spawnArea;
        this.type = type;
        if (type.flipX)
            if (RandomWizard.random())
            setFlipX(RandomWizard.random());
        if (type.flipY)
            if (RandomWizard.random())
            setFlipY(RandomWizard.random());

        if (RandomWizard.random())
        setRotation(RandomWizard.getRandomFloatBetween(0,360* type.rotation));
        setScale(RandomWizard.getRandomFloatBetween(1-type.sizeRange, 1+type.sizeRange));
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public MAP_MOVING_LAYER_TYPE getType() {
        return type;
    }

    public void setType(MAP_MOVING_LAYER_TYPE type) {
        this.type = type;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getShakiness() {
        return shakiness;
    }

    public void setShakiness(float shakiness) {
        this.shakiness = shakiness;
    }

    public float getDirectionModX() {
        return directionModX;
    }

    public void setDirectionModX(float directionModX) {
        this.directionModX = directionModX;
    }

    public float getDirectionModY() {
        return directionModY;
    }

    public void setDirectionModY(float directionModY) {
        this.directionModY = directionModY;
    }

    public float getOriginalX() {
        return originalX;
    }

    public void setOriginalX(float originalX) {
        this.originalX = originalX;
    }

    public float getOriginalY() {
        return originalY;
    }

    public void setOriginalY(float originalY) {
        this.originalY = originalY;
    }

    public MAP_AREA getSpawnArea() {
        return spawnArea;
    }

    public void setSpawnArea(MAP_AREA spawnArea) {
        this.spawnArea = spawnArea;
    }
}
