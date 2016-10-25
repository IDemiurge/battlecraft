package main.swing.components.obj;

import main.data.XLinkedMap;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.VisionManager;
import main.game.battlefield.XLine;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.components.obj.drawing.DrawHelper;
import main.swing.components.obj.drawing.DrawMaster;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.GuiManager;
import main.system.datatypes.DequeImpl;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//++ better animations! 
public class BfGridComp {
    private static Coordinates stackedHighlightRelativeCoordinates;
    private static Coordinates stackedActiveHighlightRelativeCoordinates;
    private static Map<XLine, Image> overlayMap = new XLinkedMap<>();
    private static Map<XLine, Image> underlayMap = new XLinkedMap<>();
    private static int stackedInfoObjSize;
    private static int stackedActiveObjSize;
    CellComp[][] cells;
    BufferedImage paintImage;
    BufferedImage bufferImage;
    G_Panel panel;
    DC_Game game;
    private Map<Coordinates, CellComp> map;
    private DC_BattleFieldGrid holder;
    private BfMouseListener bfMouseListener;
    private int zoom;
    private boolean editMode;
    // private DC_Game simulation;
    private Integer width;
    private Integer height;
    private int offsetX = 0;
    private int offsetY = 0;
    private MouseListener customMouseListener;
    private int zoomPerRotation = 5;
    private boolean dirty;
    private boolean offsetIsNotReady;
    private int pixelOffsetY;
    private int pixelOffsetX;

    public BfGridComp(DC_Game simulation, int width, int height, int zoom) {
        editMode = true;
        this.game = simulation;
        this.width = width;
        this.height = height;
        this.zoom = zoom;

        initPanel();

    }

    public BfGridComp(DC_BattleFieldGrid holder) {
        game = holder.getDungeon().getGame();
        this.holder = holder;
        zoom = 100;

        initPanel();
    }

    public static Map<XLine, Image> getMap(boolean overOrUnder) {
        if (overOrUnder)
            return getOverlayMap();
        return getUnderlayMap();
    }

    public static Coordinates getStackedHighlightRelativeCoordinates(boolean info) {
        if (info)
            return stackedHighlightRelativeCoordinates;
        return stackedActiveHighlightRelativeCoordinates;
    }

    public static int getSelectedObjSize(boolean infoSelected) {
        return infoSelected ? stackedInfoObjSize : stackedActiveObjSize;
    }

    public static void setSelectedObjSize(boolean infoSelected, int objSize) {
        if (infoSelected)
            stackedInfoObjSize = objSize;
        else
            stackedActiveObjSize = objSize;
    }

    public static void setStackedHighlightRelativeCoordinates(Coordinates coordinates, boolean info) {
        if (info)
            stackedHighlightRelativeCoordinates = coordinates;
        else
            stackedActiveHighlightRelativeCoordinates = coordinates;
    }

    public static void setStackedInfoHighlightRelativeCoordinates(Coordinates coordinates) {
        stackedHighlightRelativeCoordinates = coordinates;
    }

    public static void setStackedActiveHighlightRelativeCoordinates(Coordinates coordinates) {
        stackedActiveHighlightRelativeCoordinates = coordinates;
    }

    public static Map<XLine, Image> getOverlayMap() {
        return overlayMap;
    }

    public static void setOverlayMap(Map<XLine, Image> overlayMap) {
        BfGridComp.overlayMap = overlayMap;
    }

    public static Map<XLine, Image> getUnderlayMap() {
        return underlayMap;
    }

    public static void setUnderlayMap(Map<XLine, Image> underlayMap) {
        BfGridComp.underlayMap = underlayMap;
    }

    private void initPanel() {
        cells = new CellComp[getCellsX()][getCellsY()];
        for (int i = 0; i < getCellsX(); i++)
            for (int j = 0; j < getCellsY(); j++) {
                Coordinates coordinates = new Coordinates(i, j);
                if (isLevelEditor()) {
                    cells[i][j] = new CellComp(game, coordinates, this);

                } else
                    cells[i][j] = new CellComp(holder, coordinates);
                getMap().put(coordinates, cells[i][j]);
            }

        panel = new G_Panel() {
            protected void paintComponent(Graphics g) {
                // Chronos
                try {
                    game.getAnimationManager().paintCalledOnBfGrid();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                g.drawImage(paintImage, 0, 0, null);
                if (!isLevelEditor())
                    game.getAnimationManager().drawAnimations(g);

                // if (isOffsetIsNotReady()) {
                // offsetIsNotReady = false;
                // main.system.auxiliary.LogMaster
                // .log(1,
                // "!!isOffsetIsNotReadyisOffsetIsNotReadyisOffsetIsNotReadyisOffsetIsNotReady ");
                // return;
                // }
                // main.system.auxiliary.LogMaster.log(1,
                // "+++ paintComponent ");

            }

        };

        panel.setPanelSize(new Dimension(getWidth(), getHeight()));
        bfMouseListener = new BfMouseListener(this);
        panel.addMouseListener(bfMouseListener);
        if (customMouseListener != null)
            panel.addMouseListener(customMouseListener);
        panel.addMouseMotionListener(bfMouseListener);
        panel.addMouseWheelListener(bfMouseListener);
        panel.setIgnoreRepaint(true);
    }

    public Boolean isOnEdgeX(Coordinates coordinates) {
        // return isOnEdgeX(coordinates, null );
        // }
        // public boolean isOnEdgeX(Coordinates coordinates, Boolean
        // near_far_both) {
        // if
        // (near_far_both==null )
        // {
        // if ( coordinates.getX() - getOffsetX() == 0) return true;
        // }
        // else if (near_far_both)
        // if ( coordinates.getX() - getOffsetX() == 0) return true;
        int edge = GuiManager.getBF_CompDisplayedCellsX() - 1;
        if (zoom != 100) {
            edge = GuiManager.getBattleFieldWidth() / getCellWidth();
        }
        if (coordinates.getX() - getOffsetX() == 0)
            return true;
        if (coordinates.getX() - getOffsetX() == edge)
            return false;

        return null;
    }

    public Boolean isOnEdgeY(Coordinates coordinates) {
        int edge = GuiManager.getBF_CompDisplayedCellsY() - 1;
        if (zoom != 100) {
            edge = GuiManager.getBattleFieldHeight() / getCellHeight();
        }
        if (coordinates.getY() - getOffsetY() == 0)
            return true;
        if (coordinates.getY() - getOffsetY() == edge)
            return false;

        return null;
    }

    private void drawUnderlays(Graphics g) {
        drawMap(g, false);
    }

    private void drawOverlays(Graphics g) {
        drawMap(g, true);
    }

    private void drawMap(Graphics g, boolean drawOverlays) {
        for (XLine c : getMap(drawOverlays).keySet()) {
            int x = getCellWidth() * (c.getP1().x - getOffsetX()) - offsetX;
            int y = getCellHeight() * (c.getP1().y - getOffsetY()) - offsetY;

            int xOffset = c.getP2().x;
            int yOffset = c.getP2().y;

            g.drawImage(getMap(drawOverlays).get(c), x + xOffset, y + yOffset, null);
        }
    }

    private void drawSelectionGlowOverlay(Graphics g, Obj obj, boolean info) {
        if (!game.isDebugMode())
            if (!VisionManager.checkVisible((DC_Obj) obj))
                return;
        CellComp comp = getCompForObject(obj);
        if (comp == null) {
            return;
        }
        if (comp.getTopObj() != null)
            if (comp.getTopObj().getOutlineType() != null) {
                // VisibilityMaster.getImage(type, unit)

                // return;
            }
        boolean single = !comp.isMultiObj();
        // check overlaying
        Image selectionFrame = info ? getInfoGlowFrame(obj)
                : obj.isMine() ? BORDER.NEO_ACTIVE_SELECT_HIGHLIGHT.getImage()
                : BORDER.NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT.getImage();
        // if (!single)
        // selectionFrame = info ?
        // BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_96.getImage() : obj
        // .isMine() ? BORDER.NEO_ACTIVE_SELECT_HIGHLIGHT_SQUARE_96.getImage()
        // : BORDER.NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT_SQUARE_96.getImage();

        int offsetX = (single ? 9 : 13 + comp.getObjects().size()) * zoom / 100;
        int offsetY = (single ? 9 : 13 + comp.getObjects().size()) * zoom / 100;
        if (single)
            setStackedHighlightRelativeCoordinates(null, info);
        if (getStackedHighlightRelativeCoordinates(info) != null) {
            offsetX -= getStackedHighlightRelativeCoordinates(info).x;
            offsetY -= getStackedHighlightRelativeCoordinates(info).y;
        } // else return;
        // if (zoom != 100) {
        // TODO IF >100, USE 128
        if (comp.isMultiObj() || isLevelEditor()) {
            int size = getSelectedObjSize(info) + getSelectedObjSize(info) * 32 / 150;
            if (isLevelEditor()) {
                size = (getCellWidth() + getCellHeight()) * 23 / 40;
                offsetX -= 6 * zoom / 100;
                offsetY -= 6 * zoom / 100;
            }
            if (size > 0)
                selectionFrame = ImageManager.getSizedVersion(selectionFrame, new Dimension(size,
                        size));
        }
        // }
        int x = getCellWidth() * (obj.getCoordinates().x - getOffsetX()) - offsetX;
        int y = getCellHeight() * (obj.getCoordinates().y - getOffsetY()) - offsetY;
        g.drawImage(selectionFrame, x, y, null);
    }

    private Image getInfoGlowFrame(Obj obj) {
        // if (obj.getGame().isOnline())
        if (!obj.isNeutral())
            return ImageManager.getGlowFrame(obj.getOwner().getFlagColor(), GuiManager
                    .getCellWidth());
        return BORDER.NEO_INFO_SELECT_HIGHLIGHT.getImage();
    }

    public CellComp getCompForObject(Obj obj) {
        if (holder != null)
            return holder.getCellCompMap().get(obj.getCoordinates());

        for (int x = 0; x < getCellsX(); x++) {
            for (int y = 0; y < getCellsY(); y++)
                if (cells[x][y].getObjects().contains(obj))
                    return cells[x][y];
        }
        return null;
    }

    public void repaintComp(Coordinates c) {
        // TODO Auto-generated method stub

    }

    public void refresh() {
        setDirty(true);
        if (isLevelEditor()) {
            getUnderlayMap().clear();
            getOverlayMap().clear();
            Map<Coordinates, DC_HeroObj> wallMap = new HashMap<Coordinates, DC_HeroObj>();

            DequeImpl<DC_HeroObj> units = new DequeImpl<>();
            for (Coordinates c : game.getUnitMap().keySet()) {
                units.addAll(game.getUnitMap().get(c));
                for (DC_HeroObj obj : game.getUnitMap().get(c))
                    if (obj.isWall()) {
                        wallMap.put(c, obj);
                    }
            }
            game.getBattleFieldManager().resetWallMap(wallMap);
        }

        if (getGame().getBattleField() != null)
            panel.setKeyManager(getGame().getBattleField().getKeyListener());
        if (!panel.requestFocusInWindow())
            panel.requestFocus();
        resetBuffer();
        // resetComps();
        repaintToBuffer();
        paintImage = bufferImage;
        setDirty(false);
        if (isLevelEditor())
            getPanel().repaint();
    }

    private boolean isLevelEditor() {
        return holder == null;
    }

    private void repaintToBuffer() {
        Graphics2D g = (Graphics2D) bufferImage.getGraphics();
        // TODO imageManager update
        for (int i = 0; i < getDisplayedCellsX(); i++)
            for (int j = 0; j < getDisplayedCellsY(); j++) {
                if (i + getOffsetX() >= cells.length)
                    return;
                if (j + getOffsetY() >= cells[0].length)
                    continue;
                CellComp cellComp = cells[i + getOffsetX()][j + getOffsetY()];
                if (editMode) {
                    Coordinates c = new Coordinates(i + getOffsetX(), j + getOffsetY());
                    List<DC_HeroObj> objects = getGame().getObjectsOnCoordinate(c);
                    cellComp.setObjects(objects);
                    cellComp.setOverlayingObjects(getGame().getOverlayingObjects(c));
                    cellComp.setSizeFactor(zoom);
                    cellComp.setWidth(getCellWidth());
                    cellComp.setHeight(getCellHeight());
                    cellComp.refresh();
                }
                BufferedImage compImage = cellComp.getPaintImage();

                g.drawImage(compImage, getX(i), getY(j), null);
            }

        DrawMaster.drawDiagonalJoints(zoom, g, getOffsetX(), getOffsetY(), getCellWidth(),
                getCellHeight(), getGame().getBattleFieldManager().getDiagonalJoints());

        DC_HeroObj activeObj = getGame().getManager().getActiveObj();
        if (activeObj != null) {
            // if (!activeObj.isAnimated())
            if (!getGame().getAnimationManager().isStackAnimOverride(activeObj.getCoordinates()))
                drawSelectionGlowOverlay(g, activeObj, false);
        }
        drawUnderlays(g);
        DC_Obj infoObj = getGame().getManager().getInfoObj();
        if (infoObj != null) {
            // if (!activeObj.isAnimated())
            if (!getGame().getAnimationManager().isStackAnimOverride(infoObj.getCoordinates()))
                drawSelectionGlowOverlay(g, infoObj, true);
        }

        drawOverlays(g);
        if (!editMode)
            DrawMaster.drawWatchInfo(zoom, g);
    }

    public void offset(int offset, boolean x) {
        if (holder != null) {
            holder.wheelRotates(offset, x);
        } else {
            if (x) {
                offsetX += offset;
                if (offsetX < 0)
                    offsetX = 0;
                if (offsetX > getCellsX() - getDisplayedCellsX())
                    offsetX = getCellsX() - getDisplayedCellsX();
            } else {
                offsetY += offset;
                if (offsetY < 0)
                    offsetY = 0;
                if (offsetY > getCellsY() - getDisplayedCellsY())
                    offsetY = getCellsY() - getDisplayedCellsY();

            }
            refresh();
            panel.repaint();
        }

    }

    public int getOffsetX() {
        if (holder != null)
            return holder.getOffsetX();
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        if (holder != null)
            return holder.getOffsetY();
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    private void resetBuffer() {
        bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        // background?
    }

    public int getBfCoordinateX(int x) {
        return x / getCellWidth();
    }

    public int getBfX(int x) {
        return x * getCellWidth();
    }

    public int getBfY(int y) {
        return y * getCellHeight();
    }

    public int getCellWidth() {
        if (!DrawHelper.isFramePaintZoom(zoom))
            return GuiManager.getFullObjSize() * zoom / 100;
        return GuiManager.getCellWidth() * zoom / 100;
    }

    public int getBfCoordinateY(int y) {
        return y / getCellHeight();
    }

    public int getCellHeight() {
        if (!DrawHelper.isFramePaintZoom(zoom))
            return GuiManager.getFullObjSize() * zoom / 100;
        return GuiManager.getCellHeight() * zoom / 100;
    }

    public int getY(int j) {
        return getCellHeight() * j + pixelOffsetY;
    }

    public Point getPointForCoordinateWithOffset(Coordinates c) {
        return new Point(getX(c.x - getOffsetX()), getY(c.y - getOffsetY()));
    }

    public int getX(int i) {
        return getCellWidth() * i + pixelOffsetX;
    }

    public int getHeight() {
        if (height != null)
            return height;
        return GuiManager.getBF_CompDisplayedCellsY() * getCellHeight();
    }

    public int getWidth() {
        if (width != null)
            return width;
        return GuiManager.getBF_CompDisplayedCellsX() * getCellWidth();
    }

    public int getCellsY() {
        return GuiManager.getCurrentLevelCellsY();
    }

    public int getCellsX() {
        return GuiManager.getCurrentLevelCellsX();
    }

    public int getDisplayedCellsX() {
        return Math.min(getCellsX(), getWidth() / getCellWidth()
                + (getWidth() % getCellWidth() != 0 ? 1 : 0));
    }

    public int getDisplayedCellsY() {
        return Math.min(getCellsY(), getHeight() / getCellHeight()
                + (getHeight() % getCellHeight() != 0 ? 1 : 0));
    }

    public BufferedImage getPaintImage() {
        return paintImage;
    }

    public void setPaintImage(BufferedImage paintImage) {
        this.paintImage = paintImage;
    }

    public BufferedImage getBufferImage() {
        return bufferImage;
    }

    public void setBufferImage(BufferedImage bufferImage) {
        this.bufferImage = bufferImage;
    }

    public CellComp[][] getCells() {
        return cells;
    }

    public G_Panel getPanel() {
        return panel;
    }

    public Map<Coordinates, CellComp> getMap() {
        if (map == null)
            map = new HashMap<Coordinates, CellComp>();
        return map;
    }

    public void setMap(Map<Coordinates, CellComp> map) {
        this.map = map;
    }

    public DC_Game getGame() {
        return game;
    }

    public DC_BattleFieldGrid getHolder() {
        return holder;
    }

    public BfMouseListener getBfMouseListener() {
        return bfMouseListener;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public void setCustomMouseListener(MouseListener mouseMaster) {
        this.customMouseListener = mouseMaster;
    }

    public Point mapToPoint(Coordinates c) {
        int x = getBfX(c.x - getOffsetX());
        int y = getBfY(c.y - getOffsetY());
        return new Point(x, y);
    }

    public Coordinates mapToCoordinate(Point point) {
        int x = getBfCoordinateX(point.x);
        x += getOffsetX();
        int y = getBfCoordinateY(point.y);
        y += getOffsetY();
        return new Coordinates(x, y);
    }

    public CellComp getCompByPoint(Point point) {
        Coordinates c = mapToCoordinate(point);
        return getMap().get(c);
    }

    public void zoom(int wheelRotation) {
        zoom += wheelRotation * zoomPerRotation;
        refresh();
        panel.repaint();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isOffsetIsNotReady() {
        return offsetIsNotReady;
    }

    public void setOffsetIsNotReady(boolean offsetIsNotReady) {
        this.offsetIsNotReady = offsetIsNotReady;
    }

    public int getPixelOffsetY() {
        return pixelOffsetY;
    }

    public void setPixelOffsetY(int pixelOffsetY) {
        this.pixelOffsetY = pixelOffsetY;
    }

    public int getPixelOffsetX() {
        return pixelOffsetX;
    }

    public void setPixelOffsetX(int pixelOffsetX) {
        this.pixelOffsetX = pixelOffsetX;
    }

}