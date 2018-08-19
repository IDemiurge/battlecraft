package eidolons.game.module.dungeoncrawl.generator.model;

import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.EXIT_TEMPLATE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelValidator;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphEdge;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.SortMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.*;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelModelBuilder {

    LevelData data;
    List<LevelGraphNode> unbuiltNodes = new ArrayList<>();
    LevelZone zone; //current zone
    LevelModel model;
    LevelGraph graph;
    RoomAttacher attacher;
    RoomTemplateMaster templateMaster;
    Map<LevelGraphNode, Room> nodeModelMap = new HashMap<>();
    Map<Room, List<Room>> roomLinkMap = new HashMap<>();
    Map<LevelGraphEdge, Room> edgeMap = new HashMap<>();
    private Traverser traverser;
    private int randomExitChance;

    public LevelModelBuilder(LevelData data) {
        this.data = data;
        randomExitChance= data.getIntValue(LEVEL_VALUES.RANDOM_EXIT_CHANCE);
    }

    public LevelModel buildModel(LevelGraph graph) {
        this.graph = graph;
        build();
        model.offsetCoordinates();
        return model;
    }

    public void build() {
        Loop loop = new Loop(10);
        while (loop.continues()) {
            model = new LevelModel(data, this);
            model.setZones(graph.getZones());
            templateMaster = new RoomTemplateMaster(data, model);
            this.attacher = new RoomAttacher(data, model, templateMaster);
            traverser = new Traverser();
            build(false, graph.getNodeById(0), null);

            if (data.isFinalizerOn())
                new ModelFinalizer(templateMaster, attacher, this).finalize(model );
            if (isBuildFromExit()) {
                build(true, graph.getNodeById(1), FacingMaster.getRandomFacing());
            }
            cleanUp();
            if (new LevelValidator().validateModel(graph, model))
                return;
            if (!isCheckTraverse())
                return;
            if (traverser.test(graph, model, data, nodeModelMap, edgeMap)) {
                return;
            }
        }
    }

    private void cleanUp() {

        for (Room room : model.getRoomMap().values()) {
            List<Room> linkedRooms = roomLinkMap.get(room);
            if (linkedRooms == null)
                continue;
            if (room.getExitCoordinates().size() != linkedRooms.size())
                loop:
                 for (Coordinates coordinates : new ArrayList<>(room.getExitCoordinates())) {
                     for (Room linkedRoom : linkedRooms) {
                         if (linkedRoom.getEntranceCoordinates().getOffset(linkedRoom.getCoordinates())
                          .isAdjacent(coordinates.getOffset(room.getCoordinates())))
                             continue loop;
                         room.getExitCoordinates().remove(coordinates);

                     }
                 }
        }

        for (Room room : model.getRoomMap().values()) {
            TileMap tileMap = TileMapper.createTileMap(room);
            List<Coordinates> exits = tileMap.getMap().keySet().stream()
             .filter(c ->
              tileMap.getMap().get(c) == ROOM_CELL.ROOM_EXIT
               ||
               tileMap.getMap().get(c) == ROOM_CELL.DOOR).
              collect(Collectors.toList());

            loop:
            for (Coordinates exit : exits) {
                Coordinates c = room.relative(exit);
                if (!TilesMaster.isEdgeCell(c, room))
                    continue loop;
                ROOM_CELL cleanedCell =getCleanUpCell(room);
                if (room.getEntranceCoordinates() != null && room.getEntranceCoordinates().equals(c))
                    continue loop;
                for (Coordinates coordinates : room.getExitCoordinates()) {
                    if (c.equals(coordinates)) {
                        continue loop;
                    }
                }
                log(1, c + " before clean for Room " + room);
                room.cells[c.x][c.y] = cleanedCell.getSymbol();
                log(1, c + " Cleaned for Room " + room);
            }
        }
        model.rebuildCells();
        log(1, "CLEANED:\n " + model);
    }

    private ROOM_CELL getCleanUpCell(Room room) {
        return RandomWizard.random()? RandomWizard.random()? ROOM_CELL.LIGHT_EMITTER :ROOM_CELL.SPECIAL_CONTAINER :
         ROOM_CELL.ART_OBJ;
    }

    private boolean isCheckTraverse() {
        return false;
    }


    private Coordinates getExitCoordinates() {
        return new AbstractCoordinates(model.getCurrentWidth() / 2, model.getTopMost());
    }

    private boolean isBuildFromExit() {
        if (model.getRoomMap().values().stream().filter(room -> room.getType()
         == ROOM_TYPE.EXIT_ROOM).count() > 0)
            return false;
        return data.isBuildFromExitAllowed();
    }

    private boolean isMergeLinks(LevelGraphNode node, Set<LevelGraphEdge> links,
                                 Set<LevelGraphNode> linkedNodes) {
        //TODO  check edges are uniform
        if (node.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM)
            return false;
        if (node.getRoomType() == ROOM_TYPE.EXIT_ROOM)
            return false;
        if (links.size() == 1)
            return false;
        if (data.isMergeLinksAllowed())
            return RandomWizard.random();
        return false;
    }

    private boolean isShearWalls(Room newRoom) {
        if (newRoom.isSheared())
            return false;
        if (newRoom.getType() == ROOM_TYPE.CORRIDOR)
            return data.isShearLinkWallsAllowed();
        return data.isShearWallsAllowed();
        //       TODO  return RandomWizard.random();
    }


    /**
     * ###E###
     * OOOOOOO
     * OO#O#OO
     * #OOOOO#
     * #O#O#O#
     * entance == NORTH
     * node is not a room yet, so we don't have any real map of it
     *
     * @param node
     * @param entrance
     * @return
     */
    public void build(boolean reverse, LevelGraphNode node,
                      FACING_DIRECTION entrance) { //'parent' in args? point?
        zone = model.getZone(node);

        Set<LevelGraphEdge> links = new LinkedHashSet<>(graph.getAdjList().get(node));
        //sort by relevance?
        links.removeIf(link -> (reverse ? link.getNodeOne() : link.getNodeTwo()) == node);//no duplication!
        Set<LevelGraphNode> linkedNodes = new LinkedHashSet<>();
        for (LevelGraphEdge sub : links) {

            linkedNodes.add(sub.getNodeTwo());
        }

        //into 1 corridor with multiple exits if need be
        boolean mergeLinks = isMergeLinks(node, links, linkedNodes);
        EXIT_TEMPLATE exitTemplate = ExitMaster.getExitTemplateToLinks(links.size(), entrance, node.getRoomType());
        EXIT_TEMPLATE roomExitTemplate = mergeLinks
         ? ExitMaster.getRandomSingleExitTemplate()
         : exitTemplate;
        //recursive build - will it ensure that Main Paths are built?

        Room room = getOrCreateRoomForNode(true, node, null, entrance, roomExitTemplate);
        Set<LevelGraphNode> next = null;
        if (room == null)
            throw new RuntimeException();

        if (!links.isEmpty()) {
            //TODO go over to another node?
            next = buildLinks(room, links, mergeLinks);

            for (LevelGraphNode nextNode : next) {
                room = nodeModelMap.get(nextNode);
                if (room == null)
                    continue;
                build(reverse, nextNode, room.getEntrance());
            }
        }
        return;
    }

    public Room getOrCreateRoomForNode(boolean cache, LevelGraphNode node,
                                       Room parent,
                                       FACING_DIRECTION entrance,
                                       EXIT_TEMPLATE roomExitTemplate) {
        Room room = nodeModelMap.get(node);
        //if we already built this node by following another path ...
       if (cache)
       {
           if (room != null) return room;
       }else {
           if (room != null) return null  ;
       }


        room = findFittingAndAttach(parent, roomExitTemplate, node.getRoomType()
         , entrance);
        if (room == null) {
            return null; //don't break the chain!
        }
        nodeModelMap.put(node, room);
        room.setExitTemplate(roomExitTemplate);
        return room;
    }

    public Set<LevelGraphNode> buildLinks(
     Room room, Set<LevelGraphEdge> links, boolean mergeLinks) {
        Set<LevelGraphNode> nextToBuild = new LinkedHashSet<>();
        Loop loop = new Loop(30);
        loop:
        while (true) {
            if (loop.ended()) {
                break;
            }
            for (LevelGraphEdge edge : new ArrayList<>(links)) {
                if (room.getUsedExits().size() == room.getExits().length) {
                    break loop;
                }
                FACING_DIRECTION roomExit =getExit(room);
                FACING_DIRECTION linkExit = roomExit;
                //exit side?
                //just a boolean on merge links
                Room link = mergeLinks ? getLinkToReuse(edge) : null;
                if (link != null) {
                    linkExit = link.getRandomUnusedExit();
                    if (linkExit != null)
                        if (buildLink(room, edge, roomExit, linkExit, link) != null) {
                            nextToBuild.add(edge.getNodeTwo());
                            links.remove(edge);
                            continue;
                        }
                }
                link = findFittingAndAttach(room,
                 mergeLinks ? ExitMaster.getExitTemplateToLinks(
                  graph.getAdjList().get(edge.getNodeOne()).size(), roomExit.flip(), edge.getNodeTwo().getRoomType())
                  : ExitMaster.getRandomSingleExitTemplate(),
                 ROOM_TYPE.CORRIDOR, linkExit);
                if (buildLink(room, edge, roomExit, linkExit, link) == null) {
                    if (link != null) {
                        //                        if (loop.ended()) {
                        //                            log(1,"LINKING A DEADEND: " +link+ " to " + link.getEntrance());
                        //                            model.shearWallsFromSide(link, link.getEntrance());
                        //                            link.makeExit(link.getEntrance(), false, false);
                        //                            room.makeExit(link.getEntrance().flip(), false, true);
                        //                        } else {
                        log(1, "REMOVING A DEADEND: " + link);
                        model.remove(link);
                        MapMaster.removeFromListMap(roomLinkMap, room, link);
                        //                        }
                    }
                    continue;
                }
                nextToBuild.add(edge.getNodeTwo());
                links.remove(edge);
            }
        }
        return nextToBuild;
    }

    private FACING_DIRECTION getExit(Room room) {
        boolean random = 
        RandomWizard.chance(randomExitChance);
        return random ? room.getRandomUnusedExit()
         : room.getSortedUnusedExit(
         new SortMaster<FACING_DIRECTION>().getSorterByExpression_(
          exit -> ModelMaster.getExitSortValue(exit, room, model)));
    }

    private Room getLinkToReuse(LevelGraphEdge edge) {
        Room link = edgeMap.get(edge);
        if (link == null)
            for (LevelGraphEdge graphEdge : edgeMap.keySet()) {
                if (graphEdge.getNodeOne() == edge.getNodeOne())
                    return edgeMap.get(graphEdge);
                //            if (graphEdge.getNodeTwo() == edge.getNodeOne())
                //                return edgeMap.get(graphEdge);
            }

        return link;
    }

    public Room buildLink(Room room, LevelGraphEdge edge, FACING_DIRECTION roomExit,
                          FACING_DIRECTION linkExit, Room link) {
        boolean door = false;
        if (link == null) {
            //TODO link-less attach, same point... ?!
        } else {
            edgeMap.put(edge, link);
            linkExit =
             new RandomWizard<FACING_DIRECTION>().getRandomArrayItem(link.getExits());
        }
        Room newRoom =
         getOrCreateRoomForNode(false, edge.getNodeTwo(), link == null ? room : link,
          linkExit, ExitMaster.getExitTemplateToLinks(graph.getAdjList().get(edge.getNodeTwo()).size(), roomExit.flip(), edge.getNodeTwo().getRoomType()));
        if (newRoom == null) {
            return null;
        }
        if (!newRoom.getUsedExits().isEmpty()) {
            return null;//already linked that room before...
        }
        if (link != null) {
            door = RandomWizard.chance(data.getDoorChance(room.getType()));

            if (isShearWalls(link)) {
                model.shearWallsFromSide(link, roomExit.flip());
                //                if (!newRoom.isDisplaced())
            }
            //            else
            //                model.offset(newRoom, linkExit.flip());
            //else  ?
            link.makeExit(linkExit, !door ? false : door, true);
        }

        room.makeExit(roomExit, door ? false : door, true);

        makeExits(roomExit, linkExit, room, link, newRoom, door, false);
        return newRoom;
    }

    public void makeExits(FACING_DIRECTION roomExit, FACING_DIRECTION linkExit,
                          Room room, Room link, Room newRoom, boolean mergedLinks) {
        boolean door = RandomWizard.chance(data.getDoorChance(room.getType()));
        makeExits(roomExit, linkExit, room, link, newRoom, door, mergedLinks);

    }

    public void makeExits(FACING_DIRECTION roomExit, FACING_DIRECTION linkExit, Room room, Room link, Room newRoom, boolean door, boolean mergedLinks) {
        if (link == null) {
            if (!door)
                door = RandomWizard.chance(data.getDoorChance(room.getType()));
            else door = false;
            //door only on one end at most; or at none
            room.makeExit(roomExit, door ? false : door, true);
            if (isShearWalls(newRoom)) {
                model.shearWallsFromSide(newRoom, FacingMaster.rotate180(roomExit));
            } else
                newRoom.makeExit(FacingMaster.rotate180(roomExit), !door ? false : door, false);
            if (data.isAlignExitsAllowed())
                try {
                    attacher.alignExits(room, newRoom);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
        } else {
            door = RandomWizard.chance(data.getDoorChance(room.getType()));
            //door only on one end at most; or at none
            if (isShearWalls(newRoom)) {
                model.shearWallsFromSide(newRoom, FacingMaster.rotate180(linkExit));
            } else
                newRoom.makeExit(FacingMaster.rotate180(linkExit), !door ? false : door, false);
            link.makeExit(linkExit, door ? false : door, true);

            if (data.isAlignExitsAllowed())
                try {
                    attacher.alignExits(link, newRoom);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
        }
    }

    public Room findFittingAndAttach(Room room,
                                     EXIT_TEMPLATE exitTemplate, ROOM_TYPE type,
                                     FACING_DIRECTION parentExit, LevelZone zone) {
        this.zone = zone;
        return findFittingAndAttach(room, exitTemplate, type, parentExit);
    }

    public Room findFittingAndAttach(Room parent, EXIT_TEMPLATE roomExitTemplate,
                                     ROOM_TYPE roomType, FACING_DIRECTION parentExit) {
        return findFitting(parent, roomExitTemplate, roomType, parentExit, true);
    }
        public Room findFitting(Room parent, EXIT_TEMPLATE roomExitTemplate,
         ROOM_TYPE roomType, FACING_DIRECTION parentExit, boolean attach) {
        Coordinates entranceCoordinates = null;
        if (parent != null)
            entranceCoordinates = RoomAttacher.adjust(parent.getCoordinates(), parentExit, parent, true);
        else
            entranceCoordinates = roomType == ROOM_TYPE.ENTRANCE_ROOM ? new AbstractCoordinates(0, 0)
             : getExitCoordinates();
        Room room = attacher.findFitting(entranceCoordinates,
         roomExitTemplate, roomType, parentExit, zone);
        if (attach)
        if (room != null) {
            if (parentExit != null) {
                if (isCheckTraverse()) {
                    if (parent != null)
                        if (!traverser.checkTraversable(parent, parentExit))
                            return null;
                    //link?!
                    if (!traverser.checkTraversable(room, parentExit.flip()))
                        return null;
                }
            }
            addRoom(room, parent);
        }
        return room;
    }

    public void addRoom(Room room, Room parent) {
        model.addRoom(room);
        MapMaster.addToListMap(roomLinkMap, parent, room);
    }


}


// if (room == null) {
//  if (rotateParentAllowed) {
//  for (Boolean[] rotations : RotationMaster.getParentRotationsPossible(parent)) {
//  int oldExitIndex = Arrays.asList(parent.getExits()).indexOf(parentExit);
//  parent.setRotations(rotations);
//  parentExit = parent.getExits()[oldExitIndex];
//  entranceCoordinates = RoomAttacher.adjust(parent.getCoordinates(),
//  parentExit, parent, true);
//
//  room = attacher.findFitting(entranceCoordinates,
//  roomExitTemplate, roomType, parentExit, zone);
//  if (room != null) {
//  main.system.auxiliary.log.LogMaster.log(1, "PARENT ROTATES: "
//  + Arrays.deepToString(rotations));
//  break;
//  }
//  }
//
//  }
//  }
//  if (room == null)
//  if (data.isAltExitsAllowed())
//  if (parentExit != null) {
//  List<Coordinates> alternativeExits =
// ModelMaster.getPossibleExits(parentExit, parent);
// //                    alternativeExits.removeIf(p -> p.x == entranceCoordinates.x && p.y == entranceCoordinates.y);
// Collections.shuffle(alternativeExits);
// for (Coordinates exit : alternativeExits) {
// exit = exit.offset(entranceCoordinates);
// room = attacher.findFitting(exit,
// roomExitTemplate, roomType, parentExit, zone);
// if (room != null)
// break;
// }
// }
////        main.system.auxiliary.log.LogMaster.log(1, "Placing  " + room + " at " +
////         p + "; "+ " with parent exit to the " + parentExit);

//    public Set<LevelGraphNode> mergeLinks(
//     Set<LevelGraphNode> linkedNodes, Room room) {
//
//        EXIT_TEMPLATE exitTemplate = ExitMaster.getExitTemplateToLinks(linkedNodes.size());
//        Set<FACING_DIRECTION> exits = new HashSet<>(Arrays.asList(FACING_DIRECTION.normalFacing));
//        Room link = null;
//
//        for (FACING_DIRECTION exitSide : exits) {
//            link = findFittingAndAttach
//             (room, exitTemplate,
//              ROOM_TYPE.CORRIDOR, FacingMaster.rotate180(exitSide));
//            if (link == null)
//                continue;
//            if (isShearWalls())
//                model.shearWallsFromSide(link, (exitSide));
//            else {
//                //??
//            }
//
//            room.makeExit(exitSide, true, true);
//        }
//        if (link == null)
//            return null;
//
//        Set<LevelGraphNode> next = new LinkedHashSet<>();
//        int i = 0;
//        for (LevelGraphNode linkedNode : linkedNodes) {
//            FACING_DIRECTION exit = link.getExits()[i++];
//            Coordinates p = attacher.adjust(link.getCoordinates(), exit, link, true);
//            EXIT_TEMPLATE roomExitTemplate = ExitMaster.getExitTemplateToLinks(
//             graph.getAdjList().get(linkedNode).size());
//            Room newRoom =
//             getOrCreateRoomForNode(linkedNode, link, p, FacingMaster.rotate180(exit), roomExitTemplate);
//            boolean door = RandomWizard.chance(data.getDoorChance(room.getType()));
//
//            makeExits(null, exit, room, link, newRoom, door, true);
//
//            //            DIRECTION dir = entrance.getDirection();
//            //why rotate?
//            //            sub = FacingMaster.getFacingFromDirection(DirectionMaster.rotate90(dir, true));
//            //            buildLinked(linkedNodes.iterator().next(), link, sub);
//        }
//        return next;
//    }