package main.libgdx.gui.radial;

import main.game.DC_Game;
import main.game.Game;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.test.debug.DebugMaster;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/28/2016.
 */
public class DebugRadialManager {

    static {
        DEBUG_CONTROL.FUNC_OTHER.objects = getUnlistedFunctions().toArray();
    }

    public static List<RadialMenu.CreatorNode> getDebugNodes() {
        List<RadialMenu.CreatorNode> list = new LinkedList<>();

        Arrays.stream(DEBUG_CONTROL.values()).forEach(c -> {
            if (c.isRoot()) {
               list.add(createNodeBranch(c));
            }
        });

        return list;
    }

    private static RadialMenu.CreatorNode createNodeBranch(Object object) {
        RadialMenu.CreatorNode node = new RadialMenu.CreatorNode();
        node.name = StringMaster.getWellFormattedString(object.toString());

        boolean leaf = true;
        if (object instanceof DEBUG_CONTROL) {
            DEBUG_CONTROL c = (DEBUG_CONTROL) object;

            if (c.getChildObjects() != null) {
                node.childNodes = new LinkedList<>();
                for (Object o : c.getChildObjects()) {
                    node.childNodes.add(createNodeBranch(o));
                    leaf = false;
                }
            } else if (c.getChildren() != null) {
                node.childNodes = new LinkedList<>();
                for (Object o : c.getChildren()) {
                    node.childNodes.add(createNodeBranch(o));
                    leaf = false;
                }
            }

        }
        if (leaf) {
            node.action = () -> {
                try {
                    if (object instanceof DEBUG_CONTROL) {
                        handleDebugControl((DEBUG_CONTROL) object);
                    } else
                        handleDebugControl(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }
        return node;
    }

    private static List<DEBUG_FUNCTIONS> getUnlistedFunctions() {
        return Arrays.stream(DEBUG_FUNCTIONS.values())
                .filter(func -> !isListed(func)).collect(Collectors.toList());
    }

    private static boolean isListed(DEBUG_FUNCTIONS func) {
        for (DEBUG_CONTROL c : DEBUG_CONTROL.values()) {
            if (c.getChildObjects() != null) {
                if (Arrays.asList(c.getChildObjects()).contains(func)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void handleDebugControl(Object control) {
        if (control instanceof DebugMaster.DEBUG_FUNCTIONS) {
            DebugMaster.DEBUG_FUNCTIONS func_control = ((DebugMaster.DEBUG_FUNCTIONS) control);
            DC_Game.game.getDebugMaster().executeDebugFunctionNewThread(func_control);
        }
    }

    public static void handleDebugControl(DEBUG_CONTROL control) {
        switch (control) {
            case SET_VALUE:
                Game.game.getValueHelper().promptSetValue();
                break;
//            case DYNAMIC_PARAMETER:
//                break;
//            case PARAMETER:
//                break;
//            case PROPERTY:
//                break;
//            case POSITION:
//                break;
//            case SET:
//                break;
            case REF:
                break;
            case INFO:
                break;
            case SHOW:

                break;


            case PICK:

                DC_Game.game.getDebugMaster().executeDebugFunction(new EnumMaster<DebugMaster.DEBUG_FUNCTIONS>()
                        .retrieveEnumConst(DebugMaster.DEBUG_FUNCTIONS.class,
                                ListChooser.chooseEnum(DebugMaster.DEBUG_FUNCTIONS.class)));
                break;
            case TYPE:
                DC_Game.game.getDebugMaster().promptFunctionToExecute();
                break;
            case PICK_HIDDEN:
                DC_Game.game.getDebugMaster().executeHiddenDebugFunction(new EnumMaster<DebugMaster.HIDDEN_DEBUG_FUNCTIONS>()
                        .retrieveEnumConst(DebugMaster.HIDDEN_DEBUG_FUNCTIONS.class,
                                ListChooser.chooseEnum(DebugMaster.HIDDEN_DEBUG_FUNCTIONS.class)));
                break;
            case FUNCTION:
                break;
        }
    }

    public enum DEBUG_CONTROL {
        //    DYNAMIC_PARAMETER(),
//    PARAMETER(),
//    PROPERTY(),
//    POSITION(),
//
//
//    SET(PROPERTY,PARAMETER,DYNAMIC_PARAMETER, POSITION){
//        @Override
//        boolean isRoot() {
//            return true;
//        }
//    },
        SET_VALUE {
            @Override
            public boolean isRoot() {
                return true;
            }
        },

        REF(),
        INFO(),
        SHOW(REF, INFO) {
            @Override
            public boolean isRoot() {
                return true;
            }
        },

        FUNC_STANDARD(DebugMaster.group_basic),
        FUNC_ADD_BF(DebugMaster.group_add_bf_obj),
        FUNC_ADD_NON_BF(DebugMaster.group_add),
        FUNC_GLOBAL(DebugMaster.group_basic),
        FUNC_GRAPHICS(DebugMaster.group_graphics),
        FUNC_OTHER(
        ),
        PICK(),
        TYPE(),
        PICK_HIDDEN(),
        FUNCTION(FUNC_STANDARD, FUNC_ADD_BF, FUNC_ADD_NON_BF, FUNC_GLOBAL,
                FUNC_OTHER,
                PICK, TYPE, PICK_HIDDEN) {
            @Override
            public boolean isRoot() {
                return true;
            }
        },;


        public Object[] objects;
        public DEBUG_CONTROL[] children;

        DEBUG_CONTROL(Object... children) {
            this.objects = children;

        }

        DEBUG_CONTROL(DEBUG_CONTROL... children) {
            this.children = children;

        }

        public boolean
        isRoot() {
            return false;
        }

        public DEBUG_CONTROL[] getChildren() {
            return children;
        }

        public Object[] getChildObjects() {
            return objects;
        }
    }


}
