package main.data.dialogue;

import main.data.ability.OmittedConstructor;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DataString {
    public enum DATA_TYPE{
        SFX,
        SOUND,
        MUSIC,
        ACTOR,
        ACTORS,

    }
    DATA_TYPE type;
    //idea - use it in generic way, Object type and any n of constructors
    String data;

    public DataString(DATA_TYPE type, String data) {
        this.type = type;
        this.data = data;
    }
@OmittedConstructor
    public DataString() {
    }
}
