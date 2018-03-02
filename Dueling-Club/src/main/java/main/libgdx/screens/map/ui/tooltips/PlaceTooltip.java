package main.libgdx.screens.map.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Route;
import main.libgdx.GdxMaster;
import main.libgdx.gui.NinePatchFactory;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.screens.map.MapScreen;
import main.libgdx.screens.map.obj.PlaceActor;
import main.libgdx.screens.map.sfx.LightLayer;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.threading.WaitMaster;

import javax.swing.*;

/**
 * Created by JustMe on 2/23/2018.
 */
public class PlaceTooltip extends ToolTip {
    Place place;
    PlaceActor actor;

    public PlaceTooltip(Place place, PlaceActor actor) {
        this.place = place;
        this.actor = actor;

        GuiEventManager.bind(MapEvent.ROUTES_PANEL_HOVER_OFF, r -> {
            super.onMouseExit(null, 0, 0, 0, null);
            GuiEventManager.trigger(MapEvent.PLACE_HOVER, null);
            main.system.auxiliary.log.LogMaster.log(1, "ROUTES_PANEL_HOVER_OFF");
        });
//        GuiEventManager.bind(MapEvent.PLACE_HOVER, r -> {
//            if (r.get()==null ){
//                main.system.auxiliary.log.LogMaster.log(1,"NULL PLACE_HOVERED!!! " );
//                super.onMouseExit(null , 0, 0, 0, null );
//            }
//        });
//            GuiEventManager.bind(MapEvent.ROUTE_HOVERED, r->{
//            if (r.get()==null ){
//                GuiEventManager.trigger(MapEvent.PLACE_HOVER, null);
//                main.system.auxiliary.log.LogMaster.log(1,"NULL ROUTE_HOVERED!!! " );
//            }
//        });
    }

    @Override
    protected void onMouseMoved(InputEvent event, float x, float y) {
//        super.onMouseMoved(event, x, y);
    }

    @Override
    protected void onMouseExit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        SwingUtilities.invokeLater(() -> {
            main.system.auxiliary.log.LogMaster.log(1, "Waiting!!! ");
            WaitMaster.WAIT(500);
            if (!MapScreen.getInstance().getMapStage().getRoutes().isRouteHighlighted()) {
                GuiEventManager.trigger(MapEvent.PLACE_HOVER, null);
                super.onMouseExit(event, 0, 0, 0, toActor);
                main.system.auxiliary.log.LogMaster.log(1, "REMOVED!!! ");
            } else {
                main.system.auxiliary.log.LogMaster.log(1, "Nothing!!! ");
            }
        });
//        new Thread(() -> {
//            WaitMaster.WAIT(500);
//            if (!MapScreen.getInstance().getMapStage().getRoutes().isRouteHighlighted())
//                //trigger something on route off
//                GuiEventManager.trigger(MapEvent.PLACE_HOVER, null);
////            super.onMouseExit(event, x, y, pointer, toActor);
//        }, " thread").start();
    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        GuiEventManager.trigger(MapEvent.PLACE_HOVER, place);
        setUpdateRequired(true);
        super.onMouseEnter(event, x, y, pointer, fromActor);
    }

    @Override
    public boolean isTouchable() {
        return true;
    }

    @Override
    public void updateAct(float delta) {
        clearChildren();

        TextureRegion r = TextureCache.getOrCreateR(place.getImagePath());
        add(new ValueContainer(r, place.getName()));
        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
//        place.getTopDungeon()
//QuestMaster.getQuest(place);

//        if (!displayRoutes)
//            return ;
        if (place.getRoutes().isEmpty()) {
            return;
        }
        row();

        TablePanel<ValueContainer> routesInfo = new TablePanel<>();
        routesInfo.defaults().space(5);
        add(routesInfo);
        routesInfo.addListener(new ClickListener() {


                                   @Override
                                   public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                                       if (toActor != routesInfo.getParent())
                                           if (toActor.getParent().getParent() == routesInfo)
                                               if (toActor.getParent() == routesInfo)
                                                   return;

                                       if (toActor == routesInfo)
                                           return;
                                       if (GdxMaster.getAncestors(toActor).contains(routesInfo))
                                           return;
                                       if (!checkActorExitRemoves(toActor))
                                           return;
                                       super.exit(event, x, y, pointer, toActor);
                                       GuiEventManager.trigger(MapEvent.ROUTES_PANEL_HOVER_OFF);
                                   }
                               }

        );
        int i = 0;
        for (Route sub : place.getRoutes()) {
            //reverse pic pos
            TextureRegion tex = TextureCache.getOrCreateR(sub.getImagePath());
            ValueContainer routeInfo = new ValueContainer(tex, sub.getName(),
             sub.getLength() + " leagues");
            routeInfo.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
            routesInfo.add(routeInfo).left().padLeft(5).uniform(true, false);
            routeInfo.setUserObject(sub);
            routeInfo.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                     getTapCount()
                    GuiEventManager.trigger(MapEvent.ROUTE_HOVERED, sub);
                    return super.touchDown(event, x, y, pointer, button);

                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    GuiEventManager.trigger(MapEvent.ROUTE_HOVERED, null);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    GuiEventManager.trigger(MapEvent.ROUTE_HOVERED, sub);
                }
            });
            if (i % 2 == 1)
                routesInfo.row();
            i++;
        }
    }

    @Override
    protected boolean checkActorExitRemoves(Actor toActor) {
        if (toActor instanceof LightLayer)
            return false;
        if ( MapScreen.getInstance().getGuiStage().getVignette().getContent() .equals(toActor ))
            return false;
        if (toActor == MapScreen.getInstance().getGuiStage().getBlackout())
            return false;
        return super.checkActorExitRemoves(toActor);
    }
}