package eidolons.libgdx.anims.particles;

import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.libgdx.anims.particles.AmbienceDataSource.AMBIENCE_TEMPLATE;
import eidolons.libgdx.gui.generic.GroupX;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.ability.construct.VariableManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.MapEvent;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/8/2017.
 */
public class ParticleManager extends GroupX {
    private static final EMITTER_PRESET FOG_SFX = EMITTER_PRESET.SMOKE_TEST;
    private static boolean ambienceOn = true;
    private static boolean ambienceMoveOn;
    private static Dungeon dungeon_;
    public boolean debugMode;
    List<EmitterMap> emitterMaps = new ArrayList<>();
    List<EmitterActor> dynamicVfx = new ArrayList<>();
    Map<String, EmitterMap> cache = new HashMap<>();
    public ParticleManager() {
        GuiEventManager.bind(MapEvent.PREPARE_TIME_CHANGED, p -> {
            GuiEventManager.trigger(GuiEventType.INIT_AMBIENCE,
             new AmbienceDataSource(getTemplate(dungeon_), (DAY_TIME) p.get()));
        });

        GuiEventManager.bind(GuiEventType.SHOW_CUSTOM_VFX, p -> {
            List args = (List) p.get();
            String preset = (String) args.get(0);
            Vector2 v = (Vector2) args.get(1);
            EmitterActor vfx = new EmitterActor(preset);

            dynamicVfx.add(vfx);
            vfx.setPosition(v.x, v.y);
            addActor(vfx);
            vfx.start();
            vfx.getEffect().allowCompletion();
        });
            GuiEventManager.bind(GuiEventType.SHOW_VFX, p -> {
                List<Object> list = (List<Object>) p.get();
                List<Object> newList = new ArrayList<>();
                EMITTER_PRESET preset= (EMITTER_PRESET) list.get(0);
                newList.add(preset.getPath());
                newList.add(list.get(1));
                GuiEventManager.trigger(GuiEventType.SHOW_CUSTOM_VFX,
                 newList);

            });

        GuiEventManager.bind(GuiEventType.INIT_AMBIENCE, p -> {
            AmbienceDataSource dataSource = (AmbienceDataSource) p.get();
            clearChildren();
            emitterMaps.clear();
            for (String sub : dataSource.getEmitters()) {
                int showChance = dataSource.getShowChance();
                if (!VariableManager.getVarPart(sub).isEmpty()) {
                    sub = VariableManager.removeVarPart(sub);
                    showChance = StringMaster.getInteger(VariableManager.getVarPart(sub));
                }
                EmitterMap emitterMap = cache.get(sub);
                if (emitterMap == null) {
                    emitterMap = new EmitterMap(sub,   showChance, dataSource.getColorHue());
                    cache.put(sub, emitterMap);
                } else
                    emitterMap.setShowChance(showChance);

                emitterMap.update();
                emitterMaps.add(emitterMap);
                addActor(emitterMap);
            }
        });
        GuiEventManager.bind(GuiEventType.UPDATE_AMBIENCE, p -> {

            emitterMaps.forEach(emitterMap -> {
                try {
                    emitterMap.update();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            });

        });
    }

    public static AMBIENCE_TEMPLATE getTemplate(Dungeon dungeon_) {
        if (dungeon_.getDungeonSubtype()!=null )
        switch (dungeon_.getDungeonSubtype()) {
            case CAVE:
            case HIVE:
                return AMBIENCE_TEMPLATE.CAVE;
            case DUNGEON:
            case SEWER:
            case ARCANE:
            case CASTLE:
            case DEN:
            case HOUSE:
                return AMBIENCE_TEMPLATE.DUNGEON;

            case HELL:
                return AMBIENCE_TEMPLATE.CAVE;
            case CRYPT:
            case BARROW:
                return AMBIENCE_TEMPLATE.CRYPT;
        }
        return AMBIENCE_TEMPLATE.SURFACE;
    }

    public static boolean isAmbienceOn() {
        return ambienceOn;
    }

    public static void setAmbienceOn(boolean ambienceOn) {
        ParticleManager.ambienceOn = ambienceOn;
    }

    public static Ambience addFogOn(Vector2 at, EMITTER_PRESET preset) {
        Ambience fog = new Ambience(preset) {
            @Override
            protected boolean isCullingOn() {
                return false;
            }
        };
        fog.setPosition(
         at.x,
         at.y);
        fog.added();
        fog.setVisible(true);
        fog.getEffect().start();
        return fog;
    }

    public static boolean isAmbienceMoveOn() {
        return ambienceMoveOn;
    }

    public static void setAmbienceMoveOn(boolean ambienceMoveOn) {
        ParticleManager.ambienceMoveOn = ambienceMoveOn;
    }

    public static void init(Dungeon dungeon) {
        dungeon_ = dungeon;
    }
}
