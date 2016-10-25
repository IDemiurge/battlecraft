package main.swing.components.obj.drawing;

import main.content.CONTENT_CONSTS.BF_OBJECT_SIZE;
import main.content.CONTENT_CONSTS.BF_OBJECT_TAGS;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.content.ContentManager;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.XLinkedMap;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.XLine;
import main.game.logic.dungeon.Dungeon;
import main.game.player.Player;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.rules.mechanics.WatchRule;
import main.swing.components.obj.BfGridComp;
import main.swing.components.obj.BfMouseListener.INTERACTIVE_ELEMENT;
import main.swing.components.obj.CellComp;
import main.swing.components.obj.drawing.VisibilityMaster.OUTLINE_TYPE;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.renderers.SmartTextManager;
import main.swing.renderers.SmartTextManager.VALUE_CASES;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.StringMaster;
import main.system.graphics.DC_ImageMaster;
import main.system.graphics.ImageTransformer;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;
import main.system.text.SmartText;
import main.test.debug.DebugMaster;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class DrawMaster {
    public static boolean GRAPHICS_TEST_MODE = false;
    public static boolean FULL_GRAPHICS_TEST_MODE = false;
    private static Map<Obj, BufferedImage> objImageCache = new HashMap<Obj, BufferedImage>();
    private static boolean sightVisualsOn = true;
    BufferedImage bufferImage;
    /*
     * Multi-threaded? Multiple instances?
     *
     * display actions (e.g. unlock) buffs (new/top)
     *
     */
    private int offsetX;
    private int offsetY;
    private boolean unitEmblemDrawn;
    private BufferedImage emptyBufferImage;
    private CellComp cellComp;
    private Graphics compGraphics;
    private int objSize;
    private Dimension compSize;
    private boolean emblemDrawn;
    private int zoom;
    private boolean stackedIconMode;
    private int overlayingSize;
    private int width;
    private int height;
    private Integer stackOffsetX;
    private Integer stackOffsetY;
    private int topObjX;
    private int topObjY;
    private BufferedImage infoObjImage;
    private BufferedImage activeObjImage;
    private Map<Obj, Point> offsetMap;

    public static boolean isEditorMode() {
        return CoreEngine.isLevelEditor();
    }

    public static boolean checkWallTresspassed(Coordinates origin, Coordinates destination) {
        // how did we do that ClearShotCondition?
        // it just allows walking on the walled cells as long as you walk
        // *alongside* it
        // totally two-sided, so we just have Boolean3, yet there are also
        // CORNERS...
        // if contains null! then need to check the SIDES of other adjacent
        // WALLS to know
        // what if an alternative path is there via a broken section?
        return false;

    }

    public static void drawWatchInfo(int zoom, Graphics g) {
        DC_Obj info = DC_Game.game.getManager().getInfoObj();
        if (info instanceof DC_HeroObj) {
            List<DC_Obj> watchedObjects = WatchRule.getWatchersMap().get(info);
            if (watchedObjects != null) {
                drawWatchVisuals(watchedObjects, (DC_HeroObj) info, g, zoom, watchedObjects, false,
                        true);
                drawWatchVisuals(watchedObjects, (DC_HeroObj) info, g, zoom, watchedObjects, false,
                        false);
            }
        } else {
            DC_HeroObj active = DC_Game.game.getManager().getActiveObj();
            if (active.isMine()) {
                List<DC_Obj> watchedObjects = WatchRule.getWatchersMap().get(active);
                if (watchedObjects != null) {
                    drawWatchVisuals(watchedObjects, active, g, zoom, watchedObjects, true, true);
                    drawWatchVisuals(watchedObjects, active, g, zoom, watchedObjects, true, false);
                }
            }
        }
    }

    private static void drawWatchVisuals(List<DC_Obj> watchedObjects, DC_HeroObj watcher,
                                         Graphics g, int zoom, List<DC_Obj> list, boolean active, boolean lines_images) {
        int w = STD_IMAGES.WATCHER.getWidth() * zoom / 100;
        int h = STD_IMAGES.WATCHER.getHeight() * zoom / 100;
        Point p = getPointFromCoordinates(lines_images, w, h, watcher.getCoordinates(), watcher
                .getFacing().getDirection(), zoom);
        for (DC_Obj watched : watchedObjects) {
            Point p2 = getPointFromCoordinates(lines_images, w, h, watched.getCoordinates(),
                    DirectionMaster.rotate180(DirectionMaster
                            .getRelativeDirection(watcher, watched)), zoom);
            if (lines_images) {
                g.setColor(watcher.isMine() ? ColorManager.CYAN : ColorManager.CRIMSON);
                int offsetX = 0;
                int offsetY = 0;
                if (!watcher.getFacing().isVertical())
                    offsetX = !watcher.getFacing().isCloserToZero() ? 3 : -3;
                else
                    offsetY = !watcher.getFacing().isCloserToZero() ? 3 : -3;
                g.drawLine(p.x + offsetX, p.y + offsetY, p2.x, p2.y); // TODO
                // punctir...
                // visualize clear shot too?
                // Painter.paintImagesInLine(g, new XLine(p, p2),
                // STD_IMAGES.EYE.getImage(),
                // 40 * zoom / 100);
            } else {
                Image image = STD_IMAGES.WATCHER.getImage();
                drawImage(g, image, p.x, p.y);
                image = STD_IMAGES.WATCHED.getImage();
                drawImage(g, image, p2.x, p2.y);
            }
        }

    }

    private static Point getPointFromCoordinates(boolean lines_images, int w, int h,
                                                 Coordinates coordinates, DIRECTION d, int zoom) {
        Point p = DC_Game.game.getBattleField().getGrid().getGridComp().mapToPoint(coordinates);
        int cellWidth = GuiManager.getCellWidth() * zoom / 100;
        int cellHeight = GuiManager.getCellHeight() * zoom / 100;
        int xOffset = cellWidth / 2 - (lines_images ? 0 : w / 2);
        int yOffset = cellHeight / 2 - (lines_images ? 0 : h / 2);

        if (d.isGrowX() != null) {
            xOffset = (d.isGrowX()) ? cellWidth - w : 0;
        }
        if (d.isGrowY() != null) {
            yOffset = (d.isGrowY()) ? cellHeight - h : 0;
        }

        return new Point(p.x + xOffset, p.y + yOffset);
    }

    public static void drawDiagonalJoints(int zoom, Graphics g, int offsetX, int offsetY, int w,
                                          int h, Map<Coordinates, List<DIRECTION>> diagonalJoints) {
        for (Coordinates c : diagonalJoints.keySet()) {
            boolean darken = false;
            DC_HeroObj obj = DC_Game.game.getUnitByCoordinate(c);
            // TODO CHECK WALL
            if (obj == null) {
                obj = DC_Game.game.getUnitByCoordinate(c);
                continue;
            }
            if (!CoreEngine.isLevelEditor())
                if (!obj.isDetected())
                    if (!DebugMaster.isOmnivisionOn())
                        // obj.getPlayerVisionStatus(false) !=
                        // UNIT_TO_PLAYER_VISION.DETECTED
                        continue;
            if (obj != null)
                darken = obj.getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT;
            String prefix = darken ? "dark" : "";
            int x = w * (c.x - offsetX);
            int y = h * (c.y - offsetY);
            if (x < 0)
                continue;
            if (x > GuiManager.getBattleFieldWidth())
                continue;

            if (y < 0)
                continue;
            if (y > GuiManager.getBattleFieldHeight())
                continue;
            List<DIRECTION> list = diagonalJoints.get(c);

            for (DIRECTION side : list) {
                int x1 = x;
                int y1 = y;
                boolean flipped = false;
                if (side == DIRECTION.DOWN_LEFT) {
                    y1 += h;
                } else if (side == DIRECTION.DOWN_RIGHT) {
                    x1 += w;
                    y1 += h;
                    flipped = true;
                } else if (side == DIRECTION.UP_LEFT) {
                    flipped = true;
                } else if (side == DIRECTION.UP_RIGHT) {
                    x1 += w;
                }
                // if (list.indexOf(side) > 0) {
                // flipped = !flipped;
                // }
                // get the corner for this side
                // add offset if necessary

                Image image = STD_IMAGES.WALL_CORNER_ALMOND.getPathPrefixedImage(prefix);

                if (zoom != 100) {
                    image = ImageManager.getSizedVersion(image, zoom, true);
                }
                if (flipped)
                    image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
                // TODO scale them!
                drawImage(g, image, x1 - image.getWidth(null) / 2, y1 - image.getHeight(null) / 2);

            }

        }
    }

    public static void drawImageCentered(Graphics g, Image image, int w, int h) {
        int y = (h - image.getHeight(null)) / 2;
        int x = (w - image.getWidth(null)) / 2;
        drawImage(g, image, x, y);
    }

    public static void drawImage(Graphics g, Image image, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    public static boolean isSightVisualsOn() {
        if (isEditorMode())
            return false;
        return false;
        // return sightVisualsOn;
    }

    public static void setSightVisualsOn(boolean sightVisualsOn) {
        DrawMaster.sightVisualsOn = sightVisualsOn;
    }

    public static Map<Obj, BufferedImage> getObjImageCache() {
        return objImageCache;
    }

    public static void setObjImageCache(Map<Obj, BufferedImage> objImageCache) {
        DrawMaster.objImageCache = objImageCache;
    }

    public boolean isStackedPaintZoom() {
        return zoom >= 50;
    }

    public boolean isAnimationPaintZoom() {
        return zoom >= 65;
    }

    public int getMinOverlayingZoom() {
        return 65;
    }

    public boolean isTextPaintZoom() {
        return zoom >= 75;
    }

    public boolean isFacingPaintZoom() {
        return zoom >= 45;
    }

    public boolean isFramePaintZoom() {
        if (isEditorMode())
            return zoom >= 75;
        return zoom >= 65;
    }

    public boolean isMinimapZoom() {
        return zoom <= 50;
        /*
		 * squares w/o frames no anims no overlays except emblems (facing?) no
		 * text
		 *
		 * special icons for visibility
		 *
		 * general zoom - (later) many things to scale... or do we now?
		 *
		 * LE mode - ? :: corpses, items, emblems
		 *
		 * Overlaying :: not always scale down?
		 *
		 * Stacked :: Always via icon? For now... But not forever!
		 *
		 *
		 */
    }

    public void draw(CellComp cellComp, Graphics g) {
        draw(cellComp, g, 100, false);
    }

    public void draw(CellComp cellComp, Graphics g, int zoom, boolean editorMode) {
        this.cellComp = cellComp;
        this.compGraphics = g;
        this.zoom = zoom;
        initSizes();
        Map<Rectangle, Object> mouseMap = new XLinkedMap<Rectangle, Object>();
        cellComp.setMouseMap(mouseMap);
        DC_HeroObj topObj = cellComp.getTopObj();
        if (isSingleObj()) {
            Image image = VisibilityMaster.getDisplayImageForUnit(topObj);
            if (image != null) {
                g.drawImage(image, 0, 0, null);
                if (topObj.isWall()) {
                    drawWallOverlays(topObj, g, cellComp.getCoordinates());
                }
                if (GRAPHICS_TEST_MODE) {
                    DC_Obj obj = cellComp.getTopObjOrCell();
                    drawCI(obj, g);

                    if (FULL_GRAPHICS_TEST_MODE) {
                        g.drawString("" + obj.getOutlineType(), 0, GuiManager.getCellHeight() - 40);

                        String tooltip = VisibilityMaster.getTooltip(obj);
                        if (tooltip != null)
                            g.drawString("" + tooltip, 0, GuiManager.getCellHeight() - 20);

                        drawSightBlockInfo(cellComp, g, zoom);
                    }
                }
                drawSpecialOverlays(cellComp, g, zoom);
                return;
            }
        }

        // no overlays at all? Animations at least?
        if (isFramePaintZoom())
            if (!isMultiObj())
                try {
                    drawCellFrame(g);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        if (!cellComp.isTerrain())
            try {
                drawObjComps(g);

            } catch (Exception e) {
                e.printStackTrace();
            }
        else {
            try {
                drawTerrainObj(g);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!cellComp.isTerrain()) {
            if (topObj.isInfoSelected() || topObj.isActiveSelected()) {
                BufferedImage compOverlayImage = ImageManager.getNewBufferedImage(getCompWidth(),
                        getCompHeight());
                drawComponentOverlays(compOverlayImage.getGraphics());
                BfGridComp.getOverlayMap().put(
                        new XLine(new Point(topObj.getX(), topObj.getY()), new Point(
                                5 - getObjCount() * 2, 0)), compOverlayImage);
            } else
                drawComponentOverlays(compGraphics);

        }
        drawOverlayingObjects(g, cellComp);
        // perhaps should work with Obj instead?
        if (isAnimationPaintZoom())
            if (cellComp.isAnimated())
                try {
                    drawAnimations(cellComp, g);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        if (GRAPHICS_TEST_MODE) {
            DC_Obj obj = topObj;
            drawCI(obj, g);
        }

        if (GRAPHICS_TEST_MODE || isEditorMode() || DC_Game.game.isDebugMode()) {
            Coordinates coordinates = cellComp.getCoordinates();
            BfGridComp grid = cellComp.getGrid();
            if (grid == null)
                grid = cellComp.getGame().getBattleField().getGrid().getGridComp();
            if (coordinates.x - grid.getOffsetX() == 0)
                drawCoordinateMarkings(coordinates.y, g, false);
            if (coordinates.y - grid.getOffsetY() == 0)
                drawCoordinateMarkings(coordinates.x, g, true);

            if (cellComp.isInfoSelected()) {
                drawCoordinateMarkings(coordinates.x, g, true);
                drawCoordinateMarkings(coordinates.y, g, false);
            }
        }
        if (isSightVisualsOn()) {
            DC_HeroObj obj = cellComp.getGame().getManager().getActiveObj();
            // if (!obj.isMine()) return ;
            boolean extended = !obj.getSightSpectrumCoordinates(false).contains(
                    cellComp.getCoordinates());
            if (!extended) {
                extended = obj.getSightSpectrumCoordinates(true)
                        .contains(cellComp.getCoordinates());
                if (!extended)
                    return;
            }

            drawSightVisualsOnCell((Graphics2D) g, extended, cellComp.getObjects().size() > 0);
            drawSightBlockInfo(cellComp, g, zoom);
        }

        drawSpecialOverlays(cellComp, g, zoom);
    }

    private void drawSpecialOverlays(CellComp cellComp, Graphics g, int zoom2) {
        zoom2 = MathMaster.getMinMax(zoom * 2, 50, 100);
        if (isEditorMode() || cellComp.getGame().isDebugMode()) {
            Dungeon dungeon = cellComp.getGame().getDungeon();
            if (dungeon.checkProperty(PROPS.PARTY_SPAWN_COORDINATES))
                if (cellComp.getCoordinates().equals(
                        new Coordinates(dungeon.getProperty(PROPS.PARTY_SPAWN_COORDINATES)))) {
                    Image img = ImageManager.getSizedVersion(STD_IMAGES.ENGAGER.getImage(), zoom2);
                    drawImageCentered(g, img);
                }
            if (dungeon.checkProperty(PROPS.ENEMY_SPAWN_COORDINATES))
                if (cellComp.getCoordinates().equals(
                        new Coordinates(dungeon.getProperty(PROPS.ENEMY_SPAWN_COORDINATES)))) {
                    Image img = ImageManager.getSizedVersion(STD_IMAGES.ENGAGER.getImage(), zoom2);
                    img = ImageTransformer.flipVertically(ImageManager.getBufferedImage(img));
                    drawImageCentered(g, img);
                }
            for (String substring : StringMaster.openContainer(dungeon
                    .getProperty(PROPS.ENCOUNTER_SPAWN_POINTS))) {
                if (!cellComp.getCoordinates().equals(new Coordinates(substring)))
                    continue;
                Image img = ImageManager.getSizedVersion(STD_IMAGES.FLAG.getImage(), zoom2);
                drawImageCentered(g, img);
                // TODO OR STRING/CHAR
            }
            for (String substring : StringMaster.openContainer(dungeon
                    .getProperty(PROPS.ENCOUNTER_BOSS_SPAWN_POINTS))) {
                if (!cellComp.getCoordinates().equals(new Coordinates(substring)))
                    continue;
                Image img = ImageManager.getSizedVersion(STD_IMAGES.ENGAGEMENT_TARGET.getImage(),
                        zoom2);
                drawImageCentered(g, img);
                // TODO OR STRING/CHAR
            }
        }

    }

    private void drawSightBlockInfo(CellComp cellComp, Graphics g, int zoom) {

        if (cellComp.getTopObjOrCell().getOutlineType() == OUTLINE_TYPE.BLOCKED_OUTLINE) {
            if (!GRAPHICS_TEST_MODE && !cellComp.getGame().isDebugMode()) {
                Image image = STD_IMAGES.BLOCKED_SIGHT.getImage();
                int percentage = zoom;
                try {
                    int distance = PositionMaster.getDistance(cellComp.getGame().getManager()
                            .getActiveObj().getCoordinates(), cellComp.getCoordinates());
                    percentage = (int) Math.round(percentage / Math.sqrt(distance));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                image = ImageManager.getSizedVersion(image, percentage);
                drawImage(g, image, getCompWidth() - image.getWidth(null), 0);
                return;
            }

            if (cellComp.getTopObjOrCell().getBlockingCoordinate() == null)
                return;
            Font font = FontMaster.getFont(FONT.NYALA, 10 + 20 * zoom / 100, Font.PLAIN);
            g.setFont(font);
            g.setColor(isEditorMode() ? ColorManager.GOLDEN_WHITE : ColorManager.FOCUS);

            g.drawString("" + cellComp.getTopObjOrCell().getBlockingCoordinate(),
                    getCompWidth() / 2, getCompHeight() / 2);

            drawCoordinateMarkings(cellComp.getCoordinates().x, g, true);
            drawCoordinateMarkings(cellComp.getCoordinates().y, g, false);
            // if (!FULL_GRAPHICS_TEST_MODE)
            // return;
            g.setFont(FontMaster.getDefaultFont(18));
            g.setColor(ColorManager.GOLDEN_WHITE);

            // if (isDrawFullBlockingInfo()){
            if (cellComp.getTopObjOrCell().getBlockingWallCoordinate() != null)
                g.drawString("" + cellComp.getTopObjOrCell().getBlockingWallCoordinate(),
                        getCompWidth() * 4 / 5, getCompHeight() * 4 / 5);
            if (cellComp.getTopObjOrCell().getBlockingWallDirection() != null)
                g.drawString("" + cellComp.getTopObjOrCell().getBlockingWallDirection(),
                        getCompWidth() * 2 / 5, getCompHeight() * 3 / 5);
            if (cellComp.getTopObjOrCell().isBlockingDiagonalSide())
                g.drawString("left", getCompWidth() * 4 / 5, getCompHeight() * 2 / 5);
        }
    }

    private void drawCoordinateMarkings(int n, Graphics g, boolean xOrY) {
        Font font = FontMaster.getFont(FONT.NYALA, 10 + 20 * zoom / 100, Font.PLAIN);
        g.setFont(font);
        String string = n + "";
        g.setColor(isEditorMode() ? ColorManager.GOLDEN_WHITE : ColorManager.PURPLE);
        int x = getCompWidth() / 2 - FontMaster.getStringWidth(font, string) / 2;
        int y = 5 + 10 * zoom / 100; // getCompHeight() / 2 -
        // FontMaster.getFontHeight(font) / 2;
        if (!xOrY) {
            x = 2 + 10 * zoom / 100; // FontMaster.getStringWidth(font, string);
            y += getCompHeight() / 2 - 5;
        }
        g.drawString(string, x, y);

    }

    private void drawOverlayingObjects(Graphics g, CellComp cellComp) {
        // if (cellComp.getOverlayingObjects().size() == 1) {
        // DC_HeroObj obj = cellComp.getOverlayingObjects().get(0);
        // int x = MigMaster.getCenteredPosition(getWidth(), overlayingSize);
        // int y = MigMaster.getCenteredPosition(getHeight(), overlayingSize);
        // drawOverlayingObj(g, cellComp, obj, x, y, overlayingSize);
        // } else {

        for (DC_HeroObj obj : cellComp.getOverlayingObjects()) {
            if (!isEditorMode())
                if (VisibilityMaster.isZeroVisibility(obj))
                    continue;

            DIRECTION d = obj.getDirection();
            // if (CoreEngine.isLevelEditor()) {
            if (d == null) {
                Map<DC_HeroObj, DIRECTION> map = obj.getGame().getDirectionMap().get(
                        cellComp.getCoordinates());
                if (map != null)
                    d = map.get(obj);
                // }
            }
            int size = overlayingSize;

            int x = MigMaster.getCenteredPosition(getObjCompWidth(), size);
            if (d != null)
                if (d.isGrowX() == null)
                    x = MigMaster.getCenteredPosition(getObjCompWidth(), size);
                else
                    x = (d.isGrowX()) ? getObjCompWidth() - size : 0;

            int y = MigMaster.getCenteredPosition(getObjCompHeight(), size);
            if (d != null)
                if (d.isGrowY() == null)
                    y = MigMaster.getCenteredPosition(getObjCompHeight(), size);
                else
                    y = (d.isGrowY()) ? getObjCompHeight() - size : 0;

            drawOverlayingObj(g, cellComp, obj, x, y, size);

            // }
            // NESW
        }

    }

    private void drawOverlayingObj(Graphics g, CellComp cellComp, DC_HeroObj obj, int x, int y,
                                   int size) {
        Image img = VisibilityMaster.getDisplayImageForUnit(obj);
        if (img != null) {
            img = ImageManager.getSizedVersion(img, size);
        } else {

            if (obj.checkProperty(PROPS.BF_OBJECT_SIZE, BF_OBJECT_SIZE.LARGE.name())) {
                size += size / 2;
            } else if (obj.checkProperty(PROPS.BF_OBJECT_SIZE, BF_OBJECT_SIZE.HUGE.name())) {
                size += size;
            } else if (obj.checkProperty(G_PROPS.BF_OBJECT_TAGS, BF_OBJECT_TAGS.LARGE.name())) {
                size += size;
            } else if (obj.checkProperty(G_PROPS.BF_OBJECT_TAGS, BF_OBJECT_TAGS.HUGE.name())) {
                size += size;
            }
            int h = size;
            int w = size;
            // if (obj.getIntParam(PARAMS.HEIGHT) > 0) {
            // h = obj.getIntParam(PARAMS.HEIGHT) * zoom / 100;
            // }
            // if (obj.getIntParam(PARAMS.WIDTH) > 0) {
            // w = obj.getIntParam(PARAMS.WIDTH) * zoom / 100;
            // }
            Dimension d = new Dimension(w, h);
            img = ImageManager.getSizedIcon(obj.getImagePath(), d).getImage();
        }
        drawImage(g, img, x, y);
        Rectangle rect = new Rectangle(x, y, size, size);
        cellComp.getMouseMap().put(rect, obj);
        if (obj.isInfoSelected()) {
            BfGridComp.setStackedHighlightRelativeCoordinates(new Coordinates(true, x, y), true);
        }
    }

    private void drawAnimations(CellComp cellComp, Graphics g) {
        // TODO must specify which object is affected if there are multiple on
        // this cell;

        for (Image img : cellComp.getAnimOverlayingImages().keySet()) {
            Point p = cellComp.getAnimOverlayingImages().get(img);
            g.drawImage(img, p.x, p.y, null);
            // main.system.auxiliary.LogMaster.log(1, cellComp +
            // "'s custom animation drawn at " + p);
        }
        Image centerOverlayingImage = cellComp.getCenterOverlayingImage();
        if (centerOverlayingImage != null) {
            int x = (getCompWidth() - centerOverlayingImage.getWidth(null)) / 2;
            int y = (getCompHeight() - centerOverlayingImage.getHeight(null)) / 2;
            drawImage(g, centerOverlayingImage, x, y);
            // main.system.auxiliary.LogMaster.log(1, cellComp +
            // "'s center animation drawn at "
            // + new Point(x, y));
        }
        for (SmartText text : cellComp.getAnimOverlayingStrings().keySet()) {
            Point p = cellComp.getAnimOverlayingStrings().get(text);
            g.setColor(text.getColor());
            g.setFont(text.getFont());
            g.drawString(text.getText(), p.x, p.y);
        }
    }

    private void drawCellFrame(Graphics g) {
        if (cellComp.isMiniCellFrame() == null)
            return;
        Image cellImage = null;
        if (!cellComp.isTerrain()) {
            if ((cellComp.getTerrainObj().isTargetHighlighted())) {
                Rectangle rect = new Rectangle(getStackOffsetX(), getStackOffsetY(), objSize,
                        objSize);
                cellComp.getMouseMap().put(rect, cellComp.getTopObj());
            }
            if (isSingleObj())
                if ((cellComp.getTerrainObj().isTargetHighlighted())) {
                    Rectangle rect = new Rectangle(0, 0, getCompWidth(), getCompHeight());
                    cellComp.getMouseMap().put(rect, cellComp.getTerrainObj());
                    // TODO HIGHLIGHT FRAME?
                }

            cellImage = ImageManager.getCellBorderForBfObj(cellComp.isMiniCellFrame(),
                    cellComp.getObjects().get(0).getActivePlayerVisionStatus(),
                    cellComp.getObjects().get(0).getUnitVisionStatus()
                    // cellComp.getObjects().get(0).getVisibilityLevel()
                    // getVisibility(cellComp.getObjects().get(0))
            ).getImage();
        } else {

        }
        g.drawImage(cellImage, 0, 0, null);
    }

    private int getStackOffsetX() {
        DC_HeroObj obj = cellComp.getTopObj();
        if (!obj.getFacing().isVertical() && obj.getFacing().isCloserToZero())
            stackOffsetX = getObjCount() * getXOffsetPerObj(getObjCount());
        else
            stackOffsetX = 0;
        return stackOffsetX;
    }

    private int getStackOffsetY() {
        DC_HeroObj obj = cellComp.getTopObj();
        if (obj.getFacing().isVertical() && !obj.getFacing().isCloserToZero())
            stackOffsetY = getYOffsetPerObj(getObjCount());
        else
            stackOffsetY = 0;
        return stackOffsetY;
    }

    private void drawTerrainObj(Graphics g) {
        drawCellImage(g); // TODO checkGraves() => tooltip+selectable skulls!
        drawCorpses(g);
        drawItems(g);
        if (GRAPHICS_TEST_MODE) {
            DC_Obj obj = cellComp.getTerrainObj();
            drawCI(obj, g);
        }
    }

    private void drawCI(DC_Obj obj, Graphics g) {
        // TODO LIGHT SPELLS SHOULD ADD "LIGHT_EMIT" PARAM AND SHADOW REDUCE IT
        if (obj == null)
            // "null obj"
            return;
        SmartText text = new SmartText(obj.getIntParam(PARAMS.LIGHT_EMISSION) + "",
                ColorManager.GOLDEN_WHITE);
        int w = text.getWidth();
        int h = text.getHeight();
        // text.setFont(font)
        Point p = new Point(getObjCompWidth() - w, h);
        drawText(g, text, p);
        text = new SmartText(obj.getIntParam(PARAMS.CONCEALMENT) + "", ColorManager.LILAC);
        w = text.getWidth();
        p = new Point(getObjCompWidth() - w, p.y + h);
        drawText(g, text, p);

        text = new SmartText(obj.getIntParam(PARAMS.ILLUMINATION) + "", ColorManager.WHITE); // silver
        w = text.getWidth();
        p = new Point(getObjCompWidth() - w, p.y + h);
        drawText(g, text, p);

        text = new SmartText(obj.getGamma() + "", ColorManager.ORANGE); // silver
        w = text.getWidth();
        p = new Point(w, getCompHeight() - h * 2);
        drawText(g, text, p);

        // if (obj instanceof DC_Cell)
        if (!FULL_GRAPHICS_TEST_MODE)
            return;
        if (GRAPHICS_TEST_MODE) {
            g.setFont(FontMaster.getDefaultFont(15));
            g.drawString(obj.getVisibilityLevel().toString(), 25, 15);
            g.drawString(obj.getActivePlayerVisionStatus().toString(), 26, 27);
        }
    }

    private void drawItems(Graphics g) {
        if (isEditorMode())
            return;
        Collection<? extends Obj> droppedItems = cellComp.getGame().getDroppedItemManager()
                .getDroppedItems(cellComp.getTerrainObj());
        if (!droppedItems.isEmpty()) {
            Image img = STD_IMAGES.BAG.getImage();
            int x = getCompWidth() - img.getWidth(null) - 10;
            int y = getCompHeight() - img.getHeight(null) - 10;
            drawImage(g, img, x, y);

            Rectangle rect = new Rectangle(x, y, img.getWidth(null), img.getHeight(null));
            cellComp.getMouseMap().put(rect, INTERACTIVE_ELEMENT.ITEMS);
        }
    }

    private void drawCorpses(Graphics g) {
        if (isEditorMode())
            return;
        Coordinates coordinates = cellComp.getCoordinates();
        List<Obj> deadUnits = cellComp.getGame().getGraveyardManager().getDeadUnits(coordinates);
        if (deadUnits != null) {
            Image img = STD_IMAGES.DEATH.getImage();
            // ImageManager.getSizedVersion(img, new dimension());
            int x = 0;
            int y = 0;
            if (emblemDrawn || isMultiObj()) {
                y = getCompHeight() - img.getHeight(null);
            }
            for (Obj corpse : deadUnits) {
                drawImage(g, img, x, y);
                Rectangle rect = new Rectangle(x, y, img.getWidth(null), img.getHeight(null));
                if (isSingleObj() || cellComp.isTerrain())
                    cellComp.getMouseMap().put(rect, corpse);
                else {
                    cellComp.getMouseMap().put(rect, INTERACTIVE_ELEMENT.CORPSES);
                    return;
                }

                y += img.getHeight(null) + 2;
                if (y >= getCompHeight() - img.getHeight(null)) {
                    y = 0;
                    x += img.getWidth(null) + 2;
                }

            }
        }
    }

    private boolean isMultiObj() {
        return !isSingleObj() && !cellComp.isTerrain();
    }

    private void drawSightVisualsOnCell(Graphics2D g, boolean extended, boolean occupied) {
        // a full-size image of an eye with 50 or 25% opacity ...
        float alpha = extended ? 0.25f : 0.5f;
        // TODO blocked version? non-normal sight... C/I numbers?

        // how to activate this view? toggle eye button...
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, alpha));
        Image image = VISUALS.SIGHT_CELL_IMAGE.getImage();
        image = ImageManager.getSizedVersion(image, zoom);
        drawImage(g, image, 0, 0);
        DC_HeroObj watcher = cellComp.getGame().getManager().getActiveObj();
    }

    private void drawCellImage(Graphics g) {
        Image img = VisibilityMaster.getDisplayImageForUnit(cellComp.getTerrainObj());
        if (img == null)
            if (cellComp.getTerrainObj().isTargetHighlighted()) {
                img = ImageManager.getHighlightedCellIcon().getImage();
            } else {
                VISIBILITY_LEVEL vl = cellComp.getTerrainObj().getVisibilityLevel(false);
                UNIT_TO_PLAYER_VISION ds = cellComp.getTerrainObj().getPlayerVisionStatus(false);

                // if (vl==VISIBILITY_LEVEL.CONCEALED){
                // VisibilityMaster.getDisplayImageForUnit(type,
                // cellComp.getTerrainObj())
                // }
                // int gammaMod = 10 + ConcealmentRule.getGamma(null,
                // cellComp.getTerrainObj(), null);
                // + for In Sight
                // if (GRAPHICS_TEST_MODE)
                // main.system.auxiliary.LogMaster.log(1, vl + " and " + ds +
                // " for "
                // + cellComp.getTerrainObj().getNameAndCoordinate());
                if (VisibilityMaster.isZeroVisibility(cellComp.getTerrainObj()))
                    if (ds == UNIT_TO_PLAYER_VISION.KNOWN || ds == UNIT_TO_PLAYER_VISION.DETECTED)
                        img = (ImageManager.getHiddenCellIcon().getImage());
                    else
                        img = ImageManager.getUnknownCellIcon().getImage();
                else if (vl == VISIBILITY_LEVEL.CLEAR_SIGHT)
                    // TODO darken image based on CvsIl
                    // if (cellComp.getTerrainObj().getUnitVisionStatus() ==
                    // UNIT_TO_UNIT_VISION.BEYOND_SIGHT) {
                    // if (ds == UNIT_TO_PLAYER_VISION.DETECTED) {
                    // img = (ImageManager.getHiddenCellIcon().getImage());
                    // } else
                    // img = ImageManager.getUnknownCellIcon().getImage();
                    // } else
                    img = ImageManager.getEmptyCellIcon().getImage();
                else
                    img = ImageManager.getHiddenCellIcon().getImage();
                if (zoom != 100 || !isFramePaintZoom()) {
                    img = ImageManager.getSizedVersion(img, new Dimension(getObjCompWidth(),
                            getObjCompHeight()), true);
                }

                // float factor = new Float(gammaMod) / 100;
                // if (factor != 0.1f) {
                // main.system.auxiliary.LogMaster.log(1,
                // cellComp.getCoordinates()
                // + " blending with " + factor);
                // }
                // main.system.auxiliary.LogMaster.log(1,
                // cellComp.getCoordinates()
                // + " getTransparent "
                // + gammaMod);

                // TODO new Image()
                // g.drawImage(ImageTransformer.getTransparent(ImageManager.getUnknownCellIcon()
                // .getImage(), gammaMod), 0, 0, null);
                // another layer perhaps? EmptyCell?

                // TODO Colored lighting !
                // Image blended = ImageTransformer.blendImages(
                // ImageManager.getEmptyCellIcon().getImage(),
                // ImageManager.getUnknownCellIcon()
                // .getImage(), factor, 100
                // new Random().nextInt(100) // alpha
                // doesn't
                // work
                // );
                // g.drawImage(blended, 0, 0, null);
                // use same method as for coloring obj pics!
                // g.drawImage(coloredCenter, 4, 4, null);

                // g.drawImage(ImageManager.getEmptyCellIcon().getImage(), 0, 0,
                // null);
                // Image darkeningOverlay =
                // ImageTransformer.getTransparent(ImageManager
                // .getUnknownCellIcon().getImage(), 255 * gammaMod / 100);
                // g.drawImage(darkeningOverlay, 0, 0, null);

                // BufferedImage bufferedImage =
                // ImageManager.getBufferedImage(ImageManager
                // .getEmptyCellIcon().getImage());
                // ImageTransformer.gammaScale(bufferedImage, factor);
                // Image scaled = bufferedImage;
                // g.drawImage(scaled, 0, 0, null);

                if (vl == VISIBILITY_LEVEL.VAGUE_OUTLINE) {
                    // g.drawImage(img, 0, 0, null);
                }
            }

        g.drawImage(img, 0, 0, null);

    }

    private void drawObjComps(Graphics g) {

        if (isSingleObj() || !isStackedPaintZoom() || isStackedIconMode()) {
            Image image = getObjDrawImage(cellComp.getObjects().get(0), false);
            image = ImageManager.getSizedVersion(image, new Dimension(objSize, objSize));
            g.drawImage(image, offsetX, offsetY, null);
            if (!isSingleObj()) {
                drawStackedIcon(g);
            }
        } else {
            // offset !
            if (cellComp.getGame().getAnimationManager().isStackAnimOverride(
                    cellComp.getCoordinates()))
                return;
            List<DC_HeroObj> objects = new LinkedList<>(cellComp.getObjects());
            int stackOffsetX = 0;
            int stackOffsetY = 0;
            if (!(cellComp.isWall() || cellComp.isLandscape()))
                drawCellImage(g);
            else {
                DC_HeroObj obj = cellComp.getWallObj();
                if (obj == null)
                    obj = cellComp.getLandscapeObj();
                objects.remove(obj);
                // if (objects.size() == 1) {
                // stackOffsetX = getStackOffsetX();
                // //getXOffsetPerObj(getObjCount());
                // stackOffsetY = getStackOffsetY();
                // // getYOffsetPerObj(getObjCount());
                // }
                Image image = getObjDrawImage(obj, false);
                drawImage(g, image, 0, 0);

                Rectangle rect = new Rectangle(0, 0, getCompWidth(), getCompHeight());
                cellComp.getMouseMap().put(rect, obj);

            }

            // sort cellComp.getObjects() by Z-Order
            for (DC_HeroObj obj : objects) {
                Image image = getObjDrawImage(obj, true); // TODO selective! Not
                // all of them...
                image = ImageManager.getSizedVersion(image, new Dimension(objSize, objSize));
                int x = stackOffsetX + getStackOffsetX();
                int y = stackOffsetY + getStackOffsetY();
                if (obj.isInfoSelected() || obj.isActiveSelected()) {
                    BfGridComp.setSelectedObjSize(obj.isInfoSelected(), objSize - getObjCount());
                    Coordinates c = new Coordinates(true, x, y + getObjCount() * 3);
                    XLine xLine = new XLine(new Point(obj.getX(), obj.getY()), new Point(x, y));

                    if (obj.isActiveSelected())
                        BfGridComp.setStackedActiveHighlightRelativeCoordinates(c);
                    if (obj.isInfoSelected())
                        BfGridComp.setStackedInfoHighlightRelativeCoordinates(c);
                    // BfGridComp.getMap(obj.isInfoSelected()).put(xLine,
                    // image);
                    BfGridComp.getUnderlayMap().put(xLine, image);
                    BfGridComp.getOverlayMap().put(xLine, infoObjImage);

                } else
                    drawImage(g, image, x, y);
                getOffsetMap().put(obj, new Point(x, y));
                if (obj == cellComp.getTopObj()) {
                    topObjX = x;
                    topObjY = y;
                }
                Rectangle rect = new Rectangle(x, y, objSize, objSize);
                cellComp.getMouseMap().put(rect, obj);

                if (getStackOffsetX() > getXOffsetPerObj(getObjCount()))
                    stackOffsetX -= 3 * getXOffsetPerObj(getObjCount()) / 2;
                else
                    stackOffsetX += 3 * getXOffsetPerObj(getObjCount()) / 2;
                if (getStackOffsetY() != 0) // TODO should be double or 'triple'
                    // if I want the top obj to end up
                    // with the right gap!
                    // lower objects can be above, below, ... what's the
                    // difference for us?
                    // currently if we go with 0, images will overlap!
                    stackOffsetY -= getYOffsetPerObj(getObjCount());
                else
                    stackOffsetY += getYOffsetPerObj(getObjCount());

                // spread evenly instead based on size...

            }
        }
        DC_HeroObj obj = cellComp.getWallObj();
        if (obj == null)
            obj = cellComp.getLandscapeObj();
        if (obj != null)
            if (cellComp.isWall()) {
                drawWallOverlays(obj, g, cellComp.getCoordinates());
            }

    }

    public Map<Obj, Point> getOffsetMap() {
        if (offsetMap == null)
            offsetMap = new HashMap<Obj, Point>();
        return offsetMap;
    }

    private void drawWallOverlays(DC_HeroObj obj, Graphics g, Coordinates coordinates) {
        if (VisibilityMaster.isZeroVisibility(obj)) {
            if (obj.getActivePlayerVisionStatus() == UNIT_TO_PLAYER_VISION.UNKNOWN)
                return;
        }
        List<DIRECTION> list = obj.getGame().getBattleFieldManager().getWallMap().get(coordinates);// .getCoordinates()
        boolean hasVertical = false;
        boolean hasHorizontal = false;
        boolean hasDiagonal = false;
        boolean drawCorner = true;
        boolean diagonalCorner = false;
        boolean round = false;
        boolean diamond = false;
        boolean mesh = false;

        boolean darken = obj.getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT;
        String prefix = darken ? "dark" : "";
        if (list == null)
            diamond = true;
        else
            for (DIRECTION side : list) {
                Image image = getWallImageFromSide(side, prefix);
                if (side.isDiagonal()) {
                    if (list.size() == 1)
                        round = true;

                    drawCorner = true;
                    if (diagonalCorner) // doens't do anything?
                        diagonalCorner = false;
                    else if (hasDiagonal)
                        diagonalCorner = true;
                    else
                        hasDiagonal = true;
                } else {
                    if (list.size() > 1)
                        mesh = true;

                    if (side.isVertical()) {
                        if (hasVertical)
                            drawCorner = false;
                        hasVertical = true;
                    } else {
                        if (hasHorizontal)
                            drawCorner = false;
                        hasHorizontal = true;
                    }
                }

                if (zoom != 100)
                    image = ImageManager.getSizedVersion(image, zoom);
                drawImage(g, image, 0, 0);
            }

        if (hasHorizontal && hasVertical)
            drawCorner = true;
        if (diagonalCorner) {
            drawCorner = false;
            Boolean x = null;
            Boolean y = null;
            boolean vertical = false;
            for (DIRECTION side : list) {
                if (!side.isDiagonal()) {
                    drawCorner = true;
                    mesh = true;
                } else {
                    if (y != null)
                        if (y == side.isGrowY()) {
                            vertical = true;
                            break;
                        } else {
                            if (x == side.isGrowX())
                                break;
                            return;
                        }
                    y = side.isGrowY();
                    x = side.isGrowX();
                }
            }
            // return;
            if (!mesh) {
                Image image = STD_IMAGES.WALL_CORNER_ALMOND_H.getImage();
                if (vertical)
                    image = STD_IMAGES.WALL_CORNER_ALMOND_V.getImage();
                if (zoom != 100)
                    image = ImageManager.getSizedVersion(image, zoom);
                drawImageCentered(g, image);
            }
        }
        if (drawCorner) {
            Image image = STD_IMAGES.WALL_CORNER.getPathPrefixedImage(prefix);
            if (diamond)
                image = STD_IMAGES.WALL_CORNER_DIAMOND.getPathPrefixedImage(prefix);
            else if (mesh && hasDiagonal)
                image = STD_IMAGES.WALL_CORNER_MESH.getPathPrefixedImage(prefix);
            else if (round)
                image = STD_IMAGES.WALL_CORNER_ROUND.getPathPrefixedImage(prefix);

            if (zoom != 100)
                image = ImageManager.getSizedVersion(image, zoom);

            drawImageCentered(g, image);
        }
    }

    private void drawImageCentered(Graphics g, Image image) {
        drawImageCentered(g, image, getCompWidth(), getCompHeight());
    }

    private Image getWallImageFromSide(DIRECTION side, String prefix) {
        switch (side) {
            case DOWN_LEFT:
                return STD_IMAGES.WALL_DIAGONAL_DOWN_LEFT.getPathPrefixedImage(prefix);
            case DOWN_RIGHT:
                return STD_IMAGES.WALL_DIAGONAL_DOWN_RIGHT.getPathPrefixedImage(prefix);
            case UP_LEFT:
                return STD_IMAGES.WALL_DIAGONAL_UP_LEFT.getPathPrefixedImage(prefix);
            case UP_RIGHT:
                return STD_IMAGES.WALL_DIAGONAL_UP_RIGHT.getPathPrefixedImage(prefix);
            case LEFT:
                return STD_IMAGES.WALL_HORIZONTAL_LEFT.getPathPrefixedImage(prefix);
            case RIGHT:
                return STD_IMAGES.WALL_HORIZONTAL_RIGHT.getPathPrefixedImage(prefix);
            case UP:
                return STD_IMAGES.WALL_VERTICAL_UP.getPathPrefixedImage(prefix);
            case DOWN:
                return STD_IMAGES.WALL_VERTICAL_DOWN.getPathPrefixedImage(prefix);
        }
        return null;
    }

    private void drawStackedIcon(Graphics g) {
        Image image = STD_IMAGES.GUARD.getImage();
        int w = image.getWidth(null) * zoom / 100;
        int x = getObjCompWidth() - w; // MigMaster.getCenteredPosition(getWidth(),
        // w);
        int h = image.getHeight(null) * zoom / 100;
        int y = 0; // MigMaster.getCenteredPosition(getHeight(), h);
        image = ImageManager.getSizedVersion(image, new Dimension(w, h));
        drawImage(g, image, x, y);
        cellComp.getMouseMap().put(new Rectangle(x, y, w, h), INTERACTIVE_ELEMENT.STACK);
    }

    private int getXOffsetPerObj(int size) {
        if (size <= 1)
            return 0;
        if (!isFramePaintZoom())
            return 2 * (getCompWidth() - 2 * offsetX - objSize) / (size - 1) * 100 / zoom;
        return (getCompWidth() - 2 * offsetX - objSize) / (size - 1) * 100 / zoom;
        // return 28 - size * 4;
    }

    private int getYOffsetPerObj(int size) {
        if (size <= 1)
            return 0;
        if (!isFramePaintZoom())
            return 2 * (getCompHeight() - 2 * offsetY - objSize) / (size - 1) * 100 / zoom;
        return (getCompHeight() - 2 * offsetY - objSize) / (size - 1) * 100 / zoom;
        // return 12 - size * 2;
    }

    private void initSizes() {
        offsetX = 18;
        // DrawHelper.BORDER_WIDTH;
        offsetY = 15;
        // DrawHelper.BORDER_HEIGHT;
        objSize = GuiManager.getObjSize();
        if (!cellComp.isTerrain())
            if (!isSingleObj()) {
                objSize += 14 - Math.round(10 * Math.sqrt(getObjCount()));
                // Math.sqrt(cellComp.getObjects().size()
                // * getXOffsetPerObj(cellComp.getObjects().size())) / 2;

                if (cellComp.isMiniCellFrame(true)) {
                    offsetX = (GuiManager.getCellWidth() - GuiManager.getCellHeight()) / 2;
                    offsetY = 0;
                }

            } else if (cellComp.isMiniCellFrame(false)) {
                offsetX = (GuiManager.getCellWidth() - GuiManager.getCellHeight()) / 2;
                offsetY = 0;
                objSize = GuiManager.getCellHeight();
            }
        if (isFramePaintZoom()) {
            width = GuiManager.getCellWidth();
            height = GuiManager.getCellHeight();
        } else {
            width = GuiManager.getFullObjSize();
            height = GuiManager.getFullObjSize();
            objSize = GuiManager.getFullObjSize();
            offsetX = 0;
            offsetY = 0;
            if (!isStackedIconMode())
                if (isStackedPaintZoom())
                    if (!cellComp.isTerrain())
                        if (!isSingleObj()) {

                            objSize -= 2 * Math.round(getObjCount() * 3 * zoom / 100);
                        }
        }
        compSize = new Dimension(width * zoom / 100, height * zoom / 100);

        objSize = objSize * zoom / 100;
        overlayingSize = DrawHelper.OVERLAYING_OBJ_SIZE * zoom / 100;
        if (zoom < getMinOverlayingZoom()) {
            // TODO use info icon!
        }
    }

    // private void initSizes() {
    // offsetX = 18;
    // // DrawHelper.BORDER_WIDTH;
    // offsetY = 15;
    // // DrawHelper.BORDER_HEIGHT;
    // if (isFramePaintZoom()) {
    // width = GuiManager.getCellWidth();
    // height = GuiManager.getCellHeight();
    // objSize = GuiManager.getObjSize();
    // if (isSingleObj())
    // if (cellComp.isMiniCellFrame(false)) {
    // offsetX = (GuiManager.getCellWidth() - GuiManager.getCellHeight()) / 2;
    // offsetY = 0;
    // objSize = GuiManager.getCellHeight();
    // } else if (cellComp.isMiniCellFrame(true)) {
    // offsetX = (GuiManager.getCellWidth() - GuiManager.getCellHeight()) / 2;
    // offsetY = 0;
    // }
    // } else {
    // width = GuiManager.getFullObjSize();
    // height = GuiManager.getFullObjSize();
    // objSize = GuiManager.getFullObjSize();
    // offsetX = 0;
    // offsetY = 0;
    // }
    // objSize = objSize * zoom / 100;
    // if (!cellComp.isTerrain())
    // if (!isSingleObj()) {
    // int size = getObjCount();
    // objSize += 14 - Math.round(10 * Math.sqrt(size));
    // // Math.sqrt(cellComp.getObjects().size()
    // // * getXOffsetPerObj(cellComp.getObjects().size())) / 2;
    // }
    // compSize = new Dimension(width * zoom / 100, height * zoom / 100);
    // overlayingSize = DrawHelper.OVERLAYING_OBJ_SIZE * zoom / 100;
    // if (zoom < getMinOverlayingZoom()) {
    // // TODO use info icon!
    // }
    // }
    private boolean isSingleObj() {
        return cellComp.isSingleObj();
    }

    private void drawComponentOverlays(Graphics g) {
        if (cellComp.isAnimated())
            return;
        DC_HeroObj obj = cellComp.getTopObj();
        if (obj.isActiveSelected()) {
            drawUnitEmblem(g, obj);
            if (!isEditorMode())
                if (!obj.isUnconscious())
                    drawActiveIcons(g, obj);
        } else if (obj.isInfoSelected()) {
            drawUnitEmblem(g, obj);
            if (!isEditorMode())
                if (!obj.isUnconscious())
                    drawInfoIcons(g, obj);
        }

        // adjust pos for stacked! TODO
        if (!obj.isActiveSelected()) {
            if (!obj.isInfoSelected()) {

                drawPlayerEmblem(g, obj);
                drawItems(g);
                drawTraps(g, cellComp);
            }
        }

        if (isEditorMode())
            return;

        if (!obj.isBfObj() && cellComp.isTop(obj))
            drawParamsInfo(g, obj);

        if (cellComp.getTopObj() == obj) {
            drawUnitEmblem(g, obj);
            // TODO stacked positioning adjusted to put facing comp outside
            if (!obj.isUnconscious())
                if (DrawHelper.isFacingDrawn(cellComp, obj)
                        || (!obj.isBfObj() && DC_Game.game.isDebugMode())) {
                    drawFacing(g, (DC_HeroObj) obj);
                }
        }
        // if (!isEditorMode())
        // drawSpecialIcons(g, obj); // ?

        drawCorpses(g);

    }

    private void drawObjectOverlays(Graphics g, DC_HeroObj obj) {
        // if (!isEditorMode())
        // drawSpecialIcons(obj);

        // if (obj.isInfoSelected())
        // drawUnitEmblem(g, obj);
        //
        // drawPlayerEmblem(g, obj);

        // TODO VISIBILITY
        // if (!isEditorMode())
        // if (cellComp.isTop(obj)) {
        // drawPlayerEmblem(g, obj);
        // if (!cellComp.isBfObj(obj))
        // if (isSingleObj())
        // drawParamsInfo(g, obj);
        // }

        // if (obj.isInfoSelected()) {
        // drawUnitEmblem(g, obj);
        // if (isEditorMode())
        // return;
        // drawInfoIcons(g, obj);
        // drawActiveIcons(g, obj);
        // if (isSingleObj())
        // if (cellComp.isBfObj(obj))
        // drawParamsInfo(g, obj);
        // }
    }

    private void drawSpecialIcons(DC_HeroObj obj) {
        // obj.getEngagementTarget();
		/*
		 * when drawing stacked objects, draw a red arrow/sword towards the ET
		 */

        DC_HeroObj engageShowUnit = obj.getGame().getManager().getInfoUnit();
        if (engageShowUnit == null)
            engageShowUnit = obj.getGame().getManager().getActiveObj();
        else if (engageShowUnit.isBfObj())
            engageShowUnit = obj.getGame().getManager().getActiveObj();

        Image overlay = null;
        if (engageShowUnit.getEngagementTarget() == obj) {
            overlay = STD_IMAGES.ENGAGEMENT_TARGET.getImage();
        }
        if (obj.getGame().getRules().getEngagedRule().getEngagers(engageShowUnit).contains(obj)) {
            {
                overlay = STD_IMAGES.ENGAGER.getImage();
            }
        }
        if (overlay != null) {
            BufferedImage image = ImageManager.getNewBufferedImage(getObjCompWidth(),
                    getObjCompHeight());
            Graphics g = image.getGraphics();
            drawImage(g, overlay, 24, 24);
            BfGridComp.getOverlayMap().put(
                    new XLine(obj.getCoordinates(), new Coordinates(getOffsetX(obj),
                            getOffsetY(obj))), image);

        }
        if (obj.isUnconscious()) {
            // drawImage(g,
            // ImageManager.getRandomBloodOverlay(GuiManager.getObjSize()), 0,
            // 0);

            // darkening/bloody overlay! perhaps on the cell frame as well,
            // blood stains/dripping
            // drawImage(g, image, x, y);
            // black'n'white?
        }

    }

    private int getOffsetY(DC_HeroObj obj) {
        return (int) getOffsetMap().get(obj).getY();
    }

    private int getOffsetX(DC_HeroObj obj) {
        return (int) getOffsetMap().get(obj).getX();
    }

    private void drawTraps(Graphics g, CellComp cellComp) {
        // TODO TrapMaster.getTraps(c);

    }

    private int getObjCompWidth() {
        return isSingleObj() ? getCompWidth() : objSize;
    }

    private int getObjCompHeight() {
        return isSingleObj() ? getCompHeight() : objSize;
    }

    private void drawFacing(Graphics g, DC_HeroObj obj) {
        FACING_DIRECTION facing = obj.getFacing();
        Image img = ImageManager.getFacingImage(facing);
        int x = 0;
        int y = 0;
        int h = img.getHeight(null) * zoom / 100;
        int w = img.getWidth(null) * zoom / 100;
        int offset = obj.isSelected() ? 6 : 2;
        switch (facing) {
            case WEST:
                x = offset;
                y = getObjCompHeight() / 2 - h / 2;
                break;
            case NORTH:
                x = getObjCompWidth() / 2 - w / 2;
                y = +offset;
                break;
            case SOUTH:
                x = getObjCompWidth() / 2 - w / 2;
                y = getObjCompHeight() - img.getHeight(null) - offset;
                break;
            case EAST:
                x = getObjCompWidth() - w - offset;
                y = getObjCompHeight() / 2 - img.getHeight(null) / 2;
                break;
            case NONE:
                break;

        }
        if (!isSingleObj()) {
            x += topObjX;
            y += topObjY;
            switch (cellComp.getTopObj().getFacing()) {
                case SOUTH:
                    y = Math.min(getCompHeight() - h, y + h); // topObjY?
                    break;
                case NORTH:
                    y = Math.max(0, y - h);
                    break;
                case EAST:
                    x = Math.min(getCompWidth() - w, x + w);
                    break;
                case WEST:
                    x = Math.max(0, x - w);
                    break;
            }

        }
        drawImage(g, img, x, y);
    }

    private BufferedImage getObjDrawImage(DC_HeroObj obj, boolean drawOverlays) {
        BufferedImage image = objImageCache.get(obj);
        // if (image != null)
        // return image;
        image = new BufferedImage(GuiManager.getFullObjSize(), GuiManager.getFullObjSize(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        // Image img = ImageManager.getSizedVersion(obj.getIcon().getImage(),
        // new Dimension(objSize,
        // objSize));

        Image outlineImage = VisibilityMaster.getDisplayImageForUnit(obj);
        if (outlineImage != null) {
            g.drawImage(outlineImage, 0, 0, null);
            if (obj.isWall()) {
                drawWallOverlays(obj, g, cellComp.getCoordinates());
            }
        } else {
            Image objImage = obj.getIcon().getImage();
            g.drawImage(objImage, 0, 0, null);
        }
        if (obj.isUnconscious())
            image = ImageTransformer.getGrayScale(image);

        FLIP flip = obj.getFlip();
        if (flip == null) {
            Map<DC_HeroObj, FLIP> map = obj.getGame().getFlipMap().get(cellComp.getCoordinates());
            if (map != null)
                flip = map.get(obj);
        }
        if (flip != null)
            image = ImageTransformer.flip(flip, image);

        applyVisibility(image, obj);

        applyHighlights(image, obj);

        // if (drawOverlays)
        if (outlineImage == null)
            try {
                if (obj.isInfoSelected()) {
                    infoObjImage = new BufferedImage(GuiManager.getFullObjSize(), GuiManager
                            .getFullObjSize(), BufferedImage.TYPE_INT_ARGB);
                    g = infoObjImage.getGraphics();
                    drawObjectOverlays(g, obj);

                } else if (obj.isActiveSelected()) {
                    activeObjImage = new BufferedImage(GuiManager.getFullObjSize(), GuiManager
                            .getFullObjSize(), BufferedImage.TYPE_INT_ARGB);
                    g = activeObjImage.getGraphics();
                    drawObjectOverlays(g, obj);

                } else

                    // BfGridComp.getOverlayMap().put(key, value);

                    drawObjectOverlays(g, obj);

            } catch (Exception e) {
                e.printStackTrace();
            }
        drawHeight(obj, g);
        g.dispose();
        objImageCache.put(obj, image);
        return image;
    }

    private void drawHeight(DC_HeroObj obj, Graphics g) {
        if (isEditorMode())
            return;
        if (!obj.getGame().isDebugMode())
            return;
        // if (obj.isDetected())
        Float f = new Float(obj.getIntParam(PARAMS.HEIGHT)) / 30;
        float quotient = f % 0.1f;
        if (quotient < 0.1)
            if (!StringMaster.isInteger("" + f))
                f = f - quotient;
        String str = f + " ft.";

        int fontSize = 8 + 10 * zoom / 100;
        g.setFont(FontMaster.getFont(FONT.NYALA, fontSize, Font.ITALIC));
        drawText(g, new SmartText(str, ColorManager.GOLDEN_WHITE), new Point(fontSize, fontSize));
    }

    private void applyVisibility(BufferedImage image, DC_HeroObj obj) {
        // if (GRAPHICS_TEST_MODE)
        // main.system.auxiliary.LogMaster.log(1, obj.getVisibilityLevel() +
        // " and "
        // + obj.getPlayerVisionStatus() + " for "
        // + obj.getNameAndCoordinate());

		/*
		 * if an ally has unit in clear_sight, it is DETECTED then instead of an
		 * outline, darken it according to active unit's vision here...
		 *
		 */
        if (obj.getVisibilityLevel() == null) {
            return;
        }
        switch (obj.getVisibilityLevel()) {
            case CLEAR_SIGHT: // perhaps additional luminosity for ILL?
                break;
            case BLOCKED:
            case CONCEALED:
                applyBeyondSight(image, obj); // twice?
                image.getGraphics().drawImage(BORDER.HIDDEN.getImage(), 0, 0, null);
                break;
            case OUTLINE:
                // 99% opaque :)
                image.getGraphics().drawImage(BORDER.CONCEALED.getImage(), 0, 0, null);
                break;
            case VAGUE_OUTLINE:
                image.getGraphics().drawImage(BORDER.HIDDEN.getImage(), 0, 0, null);
                break;

        }

    }

    protected void applyBeyondSight(BufferedImage image, DC_HeroObj obj) {
        if (obj.getActivePlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED) {
            image.getGraphics().drawImage(BORDER.CONCEALED.getImage(), 0, 0, null);
        } else
            image.getGraphics().drawImage(BORDER.HIDDEN.getImage(), 0, 0, null);

    }

    // switch (obj.getUnitVisionStatus()) {
    // case BEYOND_SIGHT:
    // applyBeyondSight(image, obj);
    // image.getGraphics().drawImage(ImageManager.BORDER_BEYOND_SIGHT.getImage(),
    // 0, 0,
    // null);
    // break;
    // case CONCEALED:
    // image.getGraphics().drawImage(BORDER.CONCEALED.getImage(), 0, 0, null);
    // break; // darken
    // case IN_SIGHT:
    // break; // highlight
    // }
    // }
    private void applyHighlights(BufferedImage image, Obj obj) {
        Graphics g = image.getGraphics();
        if (isSingleObj() && cellComp.getTerrainObj().isTargetHighlighted()
                || (obj.isTargetHighlighted())) {
            BORDER border = obj.isMine() ? ImageManager.BORDER_ALLY_HIGHLIGHT
                    : ImageManager.BORDER_ENEMY_HIGHLIGHT;
            g.drawImage(
                    // ImageManager.getSizedVersion(border.getImage(), new
                    // Dimension(objSize,
                    // objSize))
                    border.getImage(), 0, 0, null);
        } else
        // if (obj.isInfoSelected()) {
        // if (obj.getGame().getManager().getInfoObj() == obj) {
        // BORDER border = BORDER.HIGHLIGHTED;
        // g.drawImage(border.getImage(), 0, 0, null);
        // } else
        {
            if (obj.isActiveSelected()) {
                // BORDER border = obj.isMine() ? BORDER.HIGHLIGHTED_GREEN
                // : ImageManager.BORDER_ENEMY_HIGHLIGHT;
                // g.drawImage(border.getImage(), 0, 0, null);
            }
        }

    }

    private void drawParamsInfo(Graphics g, Obj obj) {
        drawParamInfo(g, obj, PARAMS.TOUGHNESS);
        drawParamInfo(g, obj, PARAMS.ENDURANCE);
    }

    private void drawParamInfo(Graphics g, Obj obj, PARAMETER p) {
        if (obj.isFull(p))
            return;
        PARAMETER c_p = ContentManager.getCurrentParam(p);
        VALUE_CASES CASE = SmartTextManager.getParamCase(c_p, obj);
        String string = obj.getIntParam(c_p) + "/" + obj.getIntParam(p);
        SmartText text = new SmartText(string, CASE.getColor());
        Point c = DrawHelper.getPointForDisplayedParameter(p, text, compSize);
        drawText(g, text, c);

    }

    private void drawText(Graphics g, SmartText text, Point c) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(text.getColor());
        g.setFont(text.getFont());
        g.drawString(text.getText(), c.x, c.y);
    }

    private void drawInfoIcons(Graphics g, DC_HeroObj obj) {
        if (obj.isBfObj())
            return;
        int size = 28; // DrawHelper.AP_ICON_SIZE * objSize
        Image image = ImageManager.getSizedVersion(STD_IMAGES.COUNTERS.getPath(), size).getImage();
        // if (isMultiObj())
        // size = size * getMultiObjSizeMod() / 100;
        int x = getCompWidth() - size; // DrawHelper.AP_ICON_X *
        int y = size; // DrawHelper.AP_ICON_Y *
        drawImage(g, image, x, y);
        VALUE_CASES CASE = SmartTextManager.getParamCase(PARAMS.C_N_OF_COUNTERS, obj);
        String string = obj.getParam(PARAMS.C_N_OF_COUNTERS);
        SmartText text = new SmartText(string, CASE.getColor());
        Point c =
                // DrawHelper.getPointForCounters(size, text, getWidth(),
                // getHeight());
                new Point(getCompWidth() - FontMaster.getStringWidth(text.getFont(), text.getText()), size
                        + FontMaster.getFontHeight(text.getFont()));
        drawText(g, text, c);
    }

    private int getCompWidth() {
        return compSize.width;
    }

    private void drawActiveIcons(Graphics g, Obj obj) {
        int size = 28; // DrawHelper.AP_ICON_SIZE * objSize
        Image image = ImageManager.getSizedVersion(STD_IMAGES.ACTIONS.getPath(), size).getImage();
        // if (isMultiObj())
        // size = size * getMultiObjSizeMod() / 100; // image itself will be
        // sized along with the
        // rest
        int x = getCompWidth() - size; // DrawHelper.AP_ICON_X *
        int y = getCompHeight() - size; // DrawHelper.AP_ICON_Y *
        Point p = new Point(x, y);
        g.drawImage(image, p.x, p.y, null);
        PARAMETER c_p = ContentManager.getCurrentParam(PARAMS.N_OF_ACTIONS);
        VALUE_CASES CASE = SmartTextManager.getParamCase(c_p, obj);
        String string = obj.getParam(c_p);
        SmartText text = new SmartText(string, CASE.getColor());
        int width = getCompWidth();
        int height = getCompHeight();
        Point c = new Point(width - FontMaster.getStringWidth(text.getFont(), text.getText()),
                height - FontMaster.getFontHeight(text.getFont()) / 2);
        drawText(g, text, c);
        // TODO after sizing down stacked objects, this becomes invalid!!!
        Rectangle rect = new Rectangle(x, y, size, size);
        cellComp.getMouseMap().put(rect, INTERACTIVE_ELEMENT.AP);
    }

    private int getCompHeight() {
        return compSize.height;
    }

    private int getMultiObjSizeMod() {
        return objSize * 100 / GuiManager.getObjSize();
    }

    private int getObjCount() {
        return cellComp.getObjects().size();
    }

    private void drawUnitEmblem(Graphics g, DC_HeroObj obj) {
        if (isEditorMode())
            return;
        unitEmblemDrawn = true;
        int size = DrawHelper.UNIT_EMBLEM_SIZE_PERC; // * objSize / 100
        if (cellComp.isBfObj(obj))
            return;
        if (obj.getOwner() == Player.NEUTRAL)
            return;
        if (!obj.isDetected() && !obj.isMine())
            return;
        Image unitEmblem = DC_ImageMaster.getUnitEmblem(obj, size, false);

        if (unitEmblem == null) {
            return;
            // unitEmblem=
            // ImageManager.getSizedVersion(
            // ImageManager.getEmptyEmblem().getImage(), zoom);
        }
        // if (obj.isActiveSelected()) {
        // if (obj.isMine())
        // unitEmblem =
        // DC_ImageMaster.generateGlowIcon(BORDER.NEO_ACTIVE_SELECT_HIGHLIGHT_SQUARE_64,
        // unitEmblem);
        // else
        // unitEmblem =
        // DC_ImageMaster.generateGlowIcon(BORDER.NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT_SQUARE_64,
        // unitEmblem);
        // } else if (obj.isInfoSelected()) {
        // unitEmblem =
        // DC_ImageMaster.generateGlowIcon(BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64,
        // unitEmblem);
        // }
        // unitEmblem = ImageManager.getGlowFrame(obj.getOwner().getFlagColor(),
        // 32); TODO

        g.drawImage(unitEmblem, 0, 0, null);
        // Rectangle rect = new Rectangle(x, y, x+size, y+size);
        // cellComp.getMouseMap().put(rect,
        // INTERACTIVE_ELEMENT.UNIT_EMBLEM);
        emblemDrawn = true;
    }

    private void drawPlayerEmblem(Graphics g, DC_Obj obj) {
        if (cellComp.isBfObj(obj)) // they *may* have an owner though, right?
            return;
        if (obj.getOwner() == Player.NEUTRAL)
            return;
        if (!obj.isDetected())
            return;
        int size = DrawHelper.PLAYER_EMBLEM_SIZE_PERC * objSize / 100;
        Image playerEmblem = DC_ImageMaster.getUnitEmblem((DC_HeroObj) obj, size, true);

        if (playerEmblem == null) {
            return;
        }
        emblemDrawn = true;
        g.drawImage(playerEmblem, topObjX, topObjY, null);
    }

    public boolean isStackedIconMode() {
        return stackedIconMode;
    }

    public void setStackedIconMode(boolean stackedIconMode) {
        this.stackedIconMode = stackedIconMode;
    }

    public void setZoom(int zoom2) {
        zoom = zoom2;
    }

}