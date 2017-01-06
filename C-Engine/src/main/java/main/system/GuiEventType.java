package main.system;

import main.game.event.Event.STANDARD_EVENT_TYPE;

public enum GuiEventType {
    GRID_CREATED,
    CREATE_RADIAL_MENU,
    SHOW_PHASE_ANIM,
    UPDATE_PHASE_ANIM,
    UPDATE_PHASE_ANIMS,
    SHOW_GREEN_BORDER,
    SHOW_RED_BORDER,
    SHOW_BLUE_BORDERS,
    SELECT_MULTI_OBJECTS,
    INGAME_EVENT_TRIGGERED,
    ACTIVE_UNIT_SELECTED,
    @Deprecated
    CELL_UPDATE,
    SHOW_TOOLTIP,
    CREATE_UNITS_MODEL,
    DESTROY_UNIT_MODEL(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) ,

    ;
    public STANDARD_EVENT_TYPE[]    boundEvents;
    GuiEventType(STANDARD_EVENT_TYPE... boundEvents){
       this. boundEvents=boundEvents;
    }
}
