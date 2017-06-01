package main.game.battlecraft.logic.meta.scenario.script;

import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.entity.ConditionMaster.CONDITION_TEMPLATES;

/**
 * Created by JustMe on 5/31/2017.
 */
public class ScriptMaster {
    public static final  String scripts_path = PathFinder.getTextPath()+"scripts\\";
    public static final  String generated_scripts_path =
     scripts_path+"generated_scripts.xml";
    public static final  String dev_scripts_path =
     scripts_path+"dev_scripts.xml";

    public String getScriptByName(String name){
        String vars = VariableManager.getVars(name);
        if (vars.isEmpty())
            return getScriptByName(name, false);
        return getScriptByName(name, true, vars.split(StringMaster.SEPARATOR));
    }
        public String getScriptByName(String name, boolean variables, String...vars){
        // use some official data format already!!!
        String text= null ;
        FileManager.readFile(generated_scripts_path);
        VariableManager.getVarText(text, true, true, vars);
        return name;
    }
    public void generateScripts(){
        //from missions? use ^VARs?
    }

    public CONDITION_TEMPLATES getDefaultConditionForEvent(STANDARD_EVENT_TYPE event_type) {
        switch (event_type) {
            case ROUND_ENDS:
            case NEW_ROUND:
                return CONDITION_TEMPLATES.NUMERIC_EQUAL;
        }
        return null;
    }

    public enum SCRIPT_EVENT_SHORTCUT {
        ROUND(STANDARD_EVENT_TYPE.NEW_ROUND), DIES(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED),
        ENTERS(STANDARD_EVENT_TYPE.UNIT_HAS_ENTERED), ENGAGED(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ENGAGED),;
        STANDARD_EVENT_TYPE event_type;

        SCRIPT_EVENT_SHORTCUT(STANDARD_EVENT_TYPE event_type) {
            this.event_type = event_type;
        }
    }

}