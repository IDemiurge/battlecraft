package main.game.battlecraft.ai;

import main.client.cc.logic.party.PartyObj;
import main.content.enums.EncounterEnums.ENCOUNTER_TYPE;
import main.data.XStack;
import main.entity.obj.MicroObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import main.game.battlecraft.ai.advanced.behavior.BehaviorMaster;
import main.game.module.dungeoncrawl.ai.Patrol;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.battlecraft.logic.battle.arena.Wave;
import main.game.module.dungeoncrawl.ai.DungeonCrawler.ENGAGEMENT_LEVEL;
import main.game.battlecraft.logic.dungeon.building.MapBlock;
import main.system.auxiliary.data.ListMaster;

import java.util.*;

public class GroupAI {
    private ENCOUNTER_TYPE encounterType;
    private Unit leader;
    private List<Unit> members;
    private PartyObj party;
    private Wave creepGroup;
    private ENGAGEMENT_LEVEL engagementLevel;
    private AI_BEHAVIOR_MODE behaviorPref;
    private DIRECTION wanderDirection;
    private boolean followLeader;
    private boolean forceBehavior;
    private List<MapBlock> permittedBlocks;
    private int wanderDistance;
    private List<Coordinates> knownEnemyCoordinates;
    private Map<Unit, Coordinates> knownEnemyCoordinatesMap;
    private Map<Unit, List<Coordinates>> suspectedEnemyCoordinatesMap;
    private Coordinates originCoordinates;
    private Stack<Coordinates> wanderStepCoordinateStack;
    private boolean clockwisePatrol;
    private boolean backAndForth;
    private Patrol patrol;

    public GroupAI(Wave creepGroup) {
        this.creepGroup = creepGroup;
        members = new LinkedList<>();
        if (creepGroup != null) {
            encounterType = creepGroup.getWaveType();
            if (creepGroup == null) {
                return;
            }
            this.party = creepGroup.getParty();
            leader = party.getLeader();
            for (Unit m : party.getMembers()) {
                add(m);
            }

            originCoordinates = creepGroup.getCoordinates();
            if (originCoordinates == null) {
                originCoordinates = leader.getCoordinates();
            }
        }
    }

    @Override
    public String toString() {
        return "AI_Group: " + ListMaster.toNameList(new LinkedList<>(getMembers())) + "; leader: "
                + leader;
    }

    public void add(MicroObj obj) {
        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            if (leader == null) {
                leader = unit;
            }
            UnitAI unitAI = unit.getUnitAI();
            if (!members.contains(unit)) {
                members.add(unit);
            }
            unitAI.setGroupAI(this);

            if (originCoordinates == null) {
                originCoordinates = leader.getCoordinates();
            }
        }

    }

    public Unit getLeader() {
        if (!leader.canAct()) {
            for (Unit member : getMembers()) {
                if (member.canAct()) {
                    leader = member;
                }
            }
        }
        return leader;
    }

    public void setLeader(Unit leader) {
        this.leader = leader;
    }

    public Patrol getPatrol() {
        return patrol;
    }

    public void setPatrol(Patrol patrol) {
        this.patrol = patrol;
    }

    public Stack<Coordinates> getWanderStepCoordinateStack() {
        if (wanderStepCoordinateStack == null) {
            wanderStepCoordinateStack = new XStack<>();
        }
        return wanderStepCoordinateStack;
    }

    public List<Unit> getMembers() {
        return members;
    }

    public AI_BEHAVIOR_MODE getBehaviorPref() {
        if (behaviorPref == null) {
            behaviorPref = BehaviorMaster.initGroupPref(this);
        }
        return behaviorPref;
    }

    public void setBehaviorPref(AI_BEHAVIOR_MODE behaviorPref) {
        this.behaviorPref = behaviorPref;
    }

    public DIRECTION getWanderDirection() {
        if (wanderDirection == null) {
            wanderDirection = getLeader().getFacing().getDirection();
        }
        return wanderDirection;
    }

    public void setWanderDirection(DIRECTION wanderDirection) {
        this.wanderDirection = wanderDirection;
    }

    public boolean isFollowLeader() {
        return followLeader;
    }

    public void setFollowLeader(boolean followLeader) {
        this.followLeader = followLeader;
    }

    public boolean isForceBehavior() {
        return forceBehavior;
    }

    public void setForceBehavior(boolean forceBehavior) {
        this.forceBehavior = forceBehavior;
    }

    public List<MapBlock> getPermittedBlocks() {
        return permittedBlocks;
    }

    public void setPermittedBlocks(List<MapBlock> permittedBlocks) {
        this.permittedBlocks = permittedBlocks;
    }

    public int getWanderDistance() {
        return wanderDistance;
    }

    public void setWanderDistance(int wanderDistance) {
        this.wanderDistance = wanderDistance;
    }

    public PartyObj getParty() {
        return party;
    }

    public Wave getCreepGroup() {
        return creepGroup;
    }

    public ENGAGEMENT_LEVEL getEngagementLevel() {
        if (engagementLevel == null) {
            engagementLevel = ENGAGEMENT_LEVEL.UNSUSPECTING;
        }
        return engagementLevel;
    }

    public void setEngagementLevel(ENGAGEMENT_LEVEL engagementLevel) {
        this.engagementLevel = engagementLevel;
    }

    public void addEnemyKnownCoordinates(Coordinates... coordinates) {
        getKnownEnemyCoordinates().addAll(Arrays.asList(coordinates));
        // knownEnemyCoordinatesMap
    }

    private List<Coordinates> getKnownEnemyCoordinates() {
        return knownEnemyCoordinates;
    }

    public Map<Unit, Coordinates> getKnownEnemyCoordinatesMap() {
        return knownEnemyCoordinatesMap;
    }

    public void setKnownEnemyCoordinatesMap(Map<Unit, Coordinates> knownEnemyCoordinatesMap) {
        this.knownEnemyCoordinatesMap = knownEnemyCoordinatesMap;
    }

    public Coordinates getOriginCoordinates() {

        return originCoordinates;
    }

    public void setOriginCoordinates(Coordinates originCoordinates) {
        this.originCoordinates = originCoordinates;
    }

    public boolean isClockwisePatrol() {
        return clockwisePatrol;
    }

    public void setClockwisePatrol(boolean clockwisePatrol) {
        this.clockwisePatrol = clockwisePatrol;
    }

    public boolean isBackAndForth() {
        return backAndForth;
    }

    public void setBackAndForth(boolean backAndForth) {
        this.backAndForth = backAndForth;
    }

    public Map<Unit, List<Coordinates>> getSuspectedEnemyCoordinatesMap() {
        return suspectedEnemyCoordinatesMap;
    }

    public void setSuspectedEnemyCoordinatesMap(
            Map<Unit, List<Coordinates>> suspectedEnemyCoordinatesMap) {
        this.suspectedEnemyCoordinatesMap = suspectedEnemyCoordinatesMap;
    }

    public ENCOUNTER_TYPE getEncounterType() {
        return encounterType;
    }

}