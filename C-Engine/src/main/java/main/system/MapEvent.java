package main.system;

/**
 * Created by JustMe on 2/7/2018.
 */
public enum MapEvent implements EventType  {
    UPDATE_MAP_BACKGROUND,
    MAP_READY,
    CREATE_PLACE,
    REMOVE_PLACE,
    CREATE_PARTY,
    REMOVE_PARTY,
    REMOVE_MAP_OBJ, EMITTER_REMOVED,EMITTER_CREATED, PLACE_HOVER, LOCATION_ADDED, ROUTE_ADDED,

}
