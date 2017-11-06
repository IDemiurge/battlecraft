package main.system.text;

import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.advanced.companion.Order;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DC_LogManager extends LogManager {


    private StringBuilder combatActionLogBuilder;

    public DC_LogManager(Game game) {
        super(game);
    }

    /*
     * Do I need to have information levels? What is the best way to access the
     * log and control its filtering?
     */

    public void logOrderFailed(Order order, Unit unit) {
        String entry = unit.getName() + " has failed to obey " + order.toString();
        entry = StringMaster.MESSAGE_PREFIX_PROCEEDING + entry;
        LogMaster.log(1, entry);
    }

    public void logMovement(DC_Obj obj, Coordinates c) {
        if (obj.getActivePlayerVisionStatus() == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE) {
            return;
        }
        String name = obj.getName();
        // if (obj.getActivePlayerVisionStatus() ==
        // UNIT_TO_PLAYER_VISION.UNKNOWN)
        // name = "A unit";

        String entry = name + " has moved to a new position at " + c.toString();

        entry = StringMaster.MESSAGE_PREFIX_PROCEEDING + entry;
        LogMaster.log(1, entry);
    }

    public boolean logMovement(Ref ref) {
        logMovement((DC_Obj) ref.getSourceObj(), ref.getTargetObj().getCoordinates());
        return true;
    }

    @Override
    public void combatActionLog(String string) {
         getCombatActionLogBuilder().append(string+"\n");
    }

    public void combatEndLog(String string) {
        getCombatActionLogBuilder().append( string+"\n" );
    }
    public void combatStartLog(String string) {
        getCombatActionLogBuilder().append("\n" +string);
    }
    public void logBattleEnds() {
        logBattle(false);
    }

    public void logBattleStarts() {
        logBattle(true);
    }

    public void logBattle(boolean start) {
        StringBuilder text = new StringBuilder();
        if (start)
            text.append("Combat started, opponents: ");
        else
            text.append("Combat ended, enemies slain: ");
//TODO + "N"
        Map<String, Integer> map = new LinkedHashMap<>();
        getGame().getUnits().forEach(unit -> {
            if (unit.isHostileTo(getGame().getPlayer(true)))
                if (!unit.getAI().isOutsideCombat())
                    if (
                     (!start && unit.isDead()) ||
                      (start && unit.getPlayerVisionStatus(false)!= UNIT_TO_PLAYER_VISION.INVISIBLE))
            {
                String name = unit.getNameIfKnown();
                if (map.containsKey(name))
                    MapMaster.addToIntegerMap(map, name, 1);
                else {
                    map.put(name, 1);
                }
            }
        });
        map.keySet().forEach(unit -> {
            int i = map.get(unit);
            if (i > 1) {
                unit = unit + StringMaster.wrapInParenthesis(i + "");
            }
            text.append(unit + ", ");
        });
        text.delete(text.length() - 2, text.length()  );
        log(text.toString());
    }

    public void logBattleJoined(List<Unit> newUnits) {
        StringBuilder text = new StringBuilder();
            text.append("Battle joined by: ");
        newUnits.forEach(unit -> {
            text.append(unit.getNameIfKnown() + ", ");
        });
        text.delete(text.length() - 2, text.length()  );
        log(text.toString());
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    @Override
    protected void addTextToDisplayed(String entry) {
        super.addTextToDisplayed(entry);
        GuiEventManager.trigger(GuiEventType.LOG_ENTRY_ADDED, entry);
    }

    @Override
    public boolean log(LOG log, String entry, ENTRY_TYPE enclosingEntryType) {
        return super.log(log, entry, enclosingEntryType);
    }


    public StringBuilder getCombatActionLogBuilder() {
        if (combatActionLogBuilder == null) {
            combatActionLogBuilder = new StringBuilder();
        }
        return combatActionLogBuilder;
    }

    public void logCombatLog() {
        game.getLogManager().log(getCombatActionLogBuilder().toString());

        FileManager.write(getCombatActionLogBuilder().toString(),
         PathFinder.getLogPath()+"combat log from"  +
          TimeMaster.getFormattedDate(true) +
          " " +
          TimeMaster.getFormattedTime(false, true) +
          ".txt");
    }
}
