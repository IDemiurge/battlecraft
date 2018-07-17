package eidolons.game.module.adventure.map.travel.old;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.adventure.map.travel.encounter.Encounter;
import eidolons.game.module.adventure.map.travel.encounter.EncounterMaster;
import eidolons.game.module.dungeoncrawl.dungeon.Location;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.global.Journal;
import eidolons.macro.global.time.TimeMaster;
import eidolons.macro.map.Place;
import eidolons.macro.map.Route;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.CONTENT_CONSTS2.MACRO_STATUS;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.system.auxiliary.RandomWizard;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.HashSet;
import java.util.Set;

public class TravelMasterOld {

    private static final int DEFAULT_TRAVEL_SPEED = 4;
    private static boolean testMode = true;

    public static int calculateRouteLength(Route route) {
        int distance = PositionMaster.getDistance(route.getOrigin().getCoordinates(), route
         .getDestination().getCoordinates());
        float scale = route.getRef().getRegion().getMilePerPixel();

        return Math.round(distance * scale);

    }

    public static Set<Place> getAvailablePlaces(MacroParty party) {
        Set<Place> list = new HashSet<>();
        // via available routes! across Areas...
        Set<Route> routes = getAvailableRoutes(party);
        for (Route r : routes) {
            // if (r.getDestination())
            list.add(r.getDestination());
            list.add(r.getOrigin());
        }

        return list;
    }

    public static Set<Route> getAvailableRoutes(MacroParty party) {
        return getAvailableRoutes(party, null);
    }

    public static Set<Route> getAvailableRoutes(MacroParty party, Place destination) {
        Set<Route> list = new HashSet<>();
        for (Route route : party.getRegion().getRoutes()) {
            boolean result;
            if (destination == null) {
                result = checkAvailableRoute(party, route);
            } else {
                result = checkRouteForDestination(route, destination);
            }
            if (result) {
                list.add(route);
            }
        }
        return list;
    }

    private static boolean checkRouteForDestination(Route route, Place destination) {
        // TODO via linked?
        return route.getOrigin() == destination || route.getDestination() == destination;
    }

    private static boolean checkAvailableRoute(MacroParty party, Route route) {
        if (route.getOrigin() == party.getCurrentLocation()
         || route.getDestination() == party.getCurrentLocation()) {
            return true;
        }
        for (Route r : route.getLinkedRoutes()) {
            if (party.getCurrentRoute() == r) {
                return true;
            }
        }
        for (Place p : route.getLinkedPlaces()) {
            if (party.getCurrentLocation() == p) {
                return true;
            }
        }
        return false;
    }



    public static void enterRoute(MacroParty party, Route destination) {
        party.setCurrentRoute(destination);

    }

    public static void enterPlace(MacroParty party, Place destination) {
        party.setCurrentPlace(destination);
        party.setCurrentDestination(null);
        party.setCurrentRoute(null);
        /*
         * init dialogue
		 * 1) Entering
		 * 2) Info 
		 * 3) Choices 
		 */
        // int outcome = 0;
        String typeName = "";
        // destination.getTopDungeonName();
        // DC_Game.game.getDungeonMaster().buildDungeon(typeName, destination);
        if (destination.getTopDungeon() == null) {
            destination.setTopDungeon(new Location(destination).construct());
        }
//        DC_Game.game.getDungeonMaster().setDungeon(destination.getTopDungeon());
        // other setups? battlemanager...
//    TODO     Launcher.launchDC(party.getName());

    }

    public static Set<Place> getAvailableRoutesAsPlaces(MacroParty party, Place place) {
        Set<Route> routes = getAvailableRoutes(party, place);
        return new HashSet<>(routes);
    }

    public static float getTravelSpeedDynamic(Unit unit) {
        // reset unit? for HC ... maybe that's why those valueIcons are base[]
        // sometimes
        int mod = 100 * unit.getIntParam(PARAMS.CARRYING_CAPACITY) / 2
         / Math.max(1, unit.getCalculator().calculateCarryingWeight());
        int dex_mod = Math.min(mod / 2, unit.getIntParam(PARAMS.DEXTERITY));
        return getTravelSpeed(unit, mod, dex_mod);
    }

    public static float getTravelSpeed(Entity entity) {
        int dex_mod = Math.min(50, entity.getIntParam(PARAMS.BASE_DEXTERITY));
        return getTravelSpeed(entity, 100, dex_mod);
    }

    public static float getTravelSpeed(Entity entity, int mod, int dex_mod) {
        // what about ObjTypes?
        // survival/mobility/special param?
        mod = Math.min(mod, 200);

        boolean flyer = entity.checkProperty(G_PROPS.STANDARD_PASSIVES, ""
         + UnitEnums.STANDARD_PASSIVES.FLYING);
        float speed = new Float(entity.getIntParam(MACRO_PARAMS.TRAVEL_SPEED, true)); //
        if (speed == 0) {
            speed = DEFAULT_TRAVEL_SPEED;
        }

        speed += MathMaster.getFractionValueCentimal(((int) speed), mod);
        speed += MathMaster.getFractionValueCentimal(((int) speed), dex_mod);
        if (flyer) {
            speed = speed * 3 / 2;
        }
        return speed;

    }
}