package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.screens.map.editor.EditorControlPanel.MAP_EDITOR_MOUSE_MODE;
import main.libgdx.screens.map.editor.EditorManager;
import main.libgdx.screens.map.obj.*;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

import static main.system.MapEvent.*;

/**
 * Created by JustMe on 3/11/2018.
 */
public class MapObjStage extends Stage {
    private MacroParty mainParty;
    private PartyActor mainPartyActor;
    private Group pointsGroup = new Group();
    private List<PlaceActor> places=    new ArrayList<>() ;
    private List<PartyActor> parties=    new ArrayList<>() ;


    public Group getPointsGroup() {
        return pointsGroup;
    }

    @Override
    public void act() {
        super.act();
        if (CoreEngine.isMapEditor())
            if (EditorManager.getMode() == MAP_EDITOR_MOUSE_MODE.POINT) {
                pointsGroup.setVisible(true);
            } else
                pointsGroup.setVisible(false);

        for (PlaceActor place : places) {
            switch (place.getPlace().getInfoLevel()) {
                case VISIBLE:
                case KNOWN:
                    if (place.getColor().a ==0) {
                        ActorMaster.addFadeInAction(place, 0.84f);
                    }
                    break;
                case CONCEALED:
                case UNKNOWN:
                case INVISIBLE:
                    if (place.getColor().a > 0) {
                        ActorMaster.addFadeOutAction(place, 0.84f);
                    }
                    break;
            }
        }
    }
    public MapObjStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1)
                    if (mainParty != null)
                        mainPartyActor.moveTo(x, y);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        bindEvents();
    }

    protected void bindEvents() {
        GuiEventManager.bind(CREATE_PARTY, param -> {
            MacroParty party = (MacroParty) param.get();
            if (party == null) {
                return;
            }
            PartyActor partyActor = PartyActorFactory.getParty(party);
            addActor(partyActor);

            if (party.isMine()) {
                setMainParty(party);
                setMainPartyActor(partyActor);
            }
            parties.add(partyActor);
        });
        GuiEventManager.bind(CREATE_PLACE, param -> {
            Place place = (Place) param.get();
            PlaceActor placeActor = PlaceActorFactory.getPlace(place);
            addActor(placeActor);
            places.add(placeActor);
        });
        GuiEventManager.bind(REMOVE_MAP_OBJ, param -> {
            MapActor actor = (MapActor) param.get();
            actor.remove();
        });
    }

    public MacroParty getMainParty() {
        return mainParty;
    }

    public void setMainParty(MacroParty mainParty) {
        this.mainParty = mainParty;
    }

    public PartyActor getMainPartyActor() {
        return mainPartyActor;
    }

    public void setMainPartyActor(PartyActor mainPartyActor) {
        this.mainPartyActor = mainPartyActor;
    }

    public void removeClosest(int x, int y) {
        float minDistance = Float.MAX_VALUE;
        Actor actor = null;
        for (Actor sub : getRoot().getChildren()) {
            if (sub instanceof EmitterActor) {
                float distance = new Vector2(x, y).dst(new Vector2(sub.getX(), sub.getY()));
                if (distance < minDistance) {
                    minDistance = distance;
                    actor = sub;
                }
            }
        }
        GuiEventManager.trigger(MapEvent.REMOVE_MAP_OBJ, actor);
    }


    public void removeLast() {
        GuiEventManager.trigger(MapEvent.REMOVE_MAP_OBJ, getRoot().getChildren().peek() );
    }
}