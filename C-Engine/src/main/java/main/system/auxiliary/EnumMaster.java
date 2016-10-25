package main.system.auxiliary;

import main.content.*;
import main.content.CONTENT_CONSTS.CUSTOM_HERO_GROUP;
import main.content.parameters.PARAMETER;
import main.content.parameters.Param;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.content.properties.Prop;
import main.data.DataManager;
import main.data.ability.construct.VariableManager.VARIABLE_TYPES;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.launch.CoreEngine;

import java.util.*;

public class EnumMaster<T> {
    public static final Class<?> CONSTS_CLASS = CONTENT_CONSTS.class;
    public static final Class<?> CONSTS_CLASS2 = CONTENT_CONSTS2.class;
    private static final Class<?>[] STD_ENUM_CLASSES = {KEYS.class, VARIABLE_TYPES.class,
            OBJ_TYPES.class, MACRO_OBJ_TYPES.class

    };
    public static Class<?> ALT_CONSTS_CLASS; // set dynamically
    private static Map<Class, Map<String, Object>> enumCache = new HashMap<>();
    private static Map<Class, Map<String, Integer>> enumIndexCache = new HashMap<>();

    // private static final Logger = Logger.getLogger(EnumMaster.class);
    public static Class<?> getEnumClass(String name) {
        Class<?> CLASS = getContentConstantEnum(name);
        if (CLASS == null) {

        }
        return CLASS;

    }

    private static Class<?> getContentConstantEnum(String name) {
        Class<?> CLASS = checkStdEnumClasses(name);
        if (CLASS != null)
            return CLASS;

        CLASS = getEnumClass(name, CONSTS_CLASS);
        if (CLASS != null)
            return CLASS;
        CLASS = getEnumClass(name, CONSTS_CLASS2);
        if (CLASS != null)
            return CLASS;

        CLASS = getEnumClass(name, getALT_CONSTS_CLASS());

        return CLASS;
    }

    private static Class<?> checkStdEnumClasses(String name) {
        for (Class<?> CLASS : STD_ENUM_CLASSES) {
            if (CLASS.isEnum())
                if (StringMaster.compare(CLASS.getSimpleName(), name, true))
                    return CLASS;
        }
        return null;
    }

    public static Class<?> getEnumClass(String name, Class<?> CONSTS_CLASS) {
        return getEnumClass(name, CONSTS_CLASS, false);
    }

    public static Class<?> getEnumClass(String name, Class<?> CONSTS_CLASS, boolean closest) {
        if (CONSTS_CLASS == null)
            return null;
        for (Class<?> CLASS : CONSTS_CLASS.getDeclaredClasses()) {
            if (CLASS.isEnum())
                if (CLASS.getSimpleName().equals(name))
                    return CLASS;
        }
        for (Class<?> CLASS : CONSTS_CLASS.getDeclaredClasses()) {
            if (CLASS.isEnum())
                if (CLASS.getSimpleName().equalsIgnoreCase(name))
                    return CLASS;
        }
        for (Class<?> CLASS : CONSTS_CLASS.getDeclaredClasses()) {
            if (CLASS.isEnum())
                if (StringMaster.compare(name, CLASS.getSimpleName(), true))
                    return CLASS;
        }
        for (Class<?> CLASS : CONSTS_CLASS.getDeclaredClasses()) {
            if (CLASS.isEnum())
                if (StringMaster.compare(name, CLASS.getSimpleName(), false))
                    return CLASS;
        }
        if (closest)
            return new SearchMaster<Class<?>>().findClosest(name, Arrays.asList(CONSTS_CLASS
                    .getDeclaredClasses()));

        return null;
    }

    public static List<Object> getEnumConstants(Class<?> enumClass) {
        if (!enumClass.isEnum())
            return null;
        List<Object> list = new LinkedList<Object>();
        for (Object obj : enumClass.getEnumConstants())
            list.add(obj);
        return list;
    }

    public static List<String> getEnumConstantNames(Class<?> enumClass) {
        if (!enumClass.isEnum()) {
            if (enumClass == PROPERTY.class) {
                return getPropEnumConstantList();
            }
            if (enumClass == PARAMETER.class) {
                return getParamEnumConstantList();
            }
            return null;
        }
        List<String> list = new LinkedList<String>();
        for (Object obj : enumClass.getEnumConstants())
            list.add(obj.toString());
        return list;
    }

    public static Object getEnumConst(Class<?> class1, String name) {
        if (!CoreEngine.isEnumCachingOn())
            return new EnumMaster<>().retrieveEnumConst(class1, name);
        Map<String, Object> cache = enumCache.get(class1);
        Object enumConst = null;
        if (cache == null) {
            cache = new HashMap<>();
            enumCache.put(class1, cache);
        } else
            enumConst = cache.get(name);
        if (enumConst != null)
            return enumConst;
        enumConst = new EnumMaster<>().retrieveEnumConst(class1, name);
        cache.put(name, enumConst);
        return enumConst;
    }

    public static int getEnumConstIndex(Class<?> enumClass, Object CONST) {
        if (CONST == null)
            return -1;
        return getEnumConstIndex(enumClass, CONST.toString());
    }

    public static int getEnumConstIndex(Class<?> enumClass, String constName) {
        Integer index = null;
        Map<String, Integer> cache = null;
        if (CoreEngine.isEnumCachingOn()) {
            cache = enumIndexCache.get(enumClass);

            if (cache == null) {
                cache = new HashMap<>();
                enumIndexCache.put(enumClass, cache);
            } else
                index = cache.get(constName);
            if (index != null)
                return index;
        }

        if (!enumClass.isEnum()) {
            if (enumClass == Param.class || enumClass == PARAMETER.class) {
                index = ContentManager.getParamList().indexOf(ContentManager.getPARAM(constName));
            } else if (enumClass == Prop.class || enumClass == PROPERTY.class) {
                index = ContentManager.getPropList().indexOf(ContentManager.getPROP(constName));
            } else
                return 0;
        } else {
            Object[] ENUMS = enumClass.getEnumConstants();
            int i = -1;
            for (Object obj : ENUMS) {
                i++;
                if (StringMaster.compareByChar(obj.toString(), (constName))) {
                    index = i;
                    break;
                }
            }
        }
        if (cache != null)
            cache.put(constName, index);
        if (index == -1)
            main.system.auxiliary.LogMaster.log(1, "ENUM CONST NOT FOUND! : " + enumClass + " : "
                    + constName);

        return index;
    }

    public static Object[] getParamEnumConstants() {

        return ContentManager.getParamList().toArray();
    }

    public static List<String> getParamEnumConstantList() {

        return StringMaster.convertToStringList(ContentManager.getParamList());
    }

    public static List<String> getPropEnumConstantList() {

        return StringMaster.convertToStringList(ContentManager.getPropList());
    }

    public static Object[] getPropEnumConstants() {

        return ContentManager.getPropList().toArray();
    }

    public static List<String> findEnumConstantNames(String subgroup) {
        Class<?> names = EnumMaster.getEnumClass(subgroup);
        if (names == null)
            names = EnumMaster.getEnumClass(subgroup, MACRO_CONTENT_CONSTS.class);
        if (names == null)
            return new LinkedList<String>();
        try {
            return getEnumConstantNames(names);
        } catch (Exception e) {
            return new LinkedList<String>();
        }
    }

    public static Class<?> getALT_CONSTS_CLASS() {
        return ALT_CONSTS_CLASS;
    }

    public static void setALT_CONSTS_CLASS(Class<?> aLT_CONSTS_CLASS) {
        ALT_CONSTS_CLASS = aLT_CONSTS_CLASS;
    }

    public T retrieveEnumConst(Class<? extends T> class1, String name) {
        return retrieveEnumConst(class1, name, false);
    }

    @SuppressWarnings("unchecked")
    public T retrieveEnumConst(Class<? extends T> class1, String name, boolean findClosest) {
        if (name == null)
            return null;
        if (name.equals(""))
            return null;

        T[] array = class1.getEnumConstants();
        Collection<T> list;
        if (array == null) {
            list = new LinkedList<>();
            if (class1 == VALUE.class) {
                list = (Collection<T>) new LinkedList<>(Arrays.asList(ContentManager.getPropList()
                        .toArray(new VALUE[ContentManager.getPropList().size()])));
                list.addAll((Collection<T>) Arrays.asList(ContentManager.getParamList().toArray(
                        new VALUE[ContentManager.getParamList().size()])));
            }
            if (class1 == PROPERTY.class) {
                list = (Collection<T>) ContentManager.getPropList();
            }
            if (class1 == PARAMETER.class) {
                list = (Collection<T>) ContentManager.getParamList();
            }
        } else
            list = Arrays.asList(array);
        // name = StringMaster.getEnumFormat(name);
        T t = null;
        try {
            t = new SearchMaster<T>().find(name, list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (t != null)
            return t;
        if (findClosest)
            try {
                t = new SearchMaster<T>().findClosest(name, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (t == null)
            main.system.auxiliary.LogMaster.log(0, "ENUM NOT FOUND: " + name);
        return t;
    }

    public Object getEnum(String content, Object[] enumConstants) {
        for (Object CONST : enumConstants) {
            if (CONST.toString().equals(content))
                return CONST;
        }
        return null;
    }

    public T getEnumOfType(String content, T[] enumConstants) {
        for (T CONST : enumConstants) {
            if (CONST.toString().equals(content))
                return CONST;
        }
        return null;
    }

    public T getRandomEnumConst(Class<T> CLASS) {
        return CLASS.getEnumConstants()[RandomWizard.getRandomInt(CLASS.getEnumConstants().length)];
    }

    public List<T> getEnumList(Class<T> CLASS) {
        return new LinkedList<>(Arrays.asList(CLASS.getEnumConstants()));
    }

    public List<T> getEnumList(Class<T> CLASS, String property) {
        return getEnumList(CLASS, property, StringMaster.CONTAINER_SEPARATOR);
    }

    public List<T> getEnumList(Class<T> CLASS, String property, String separator) {
        List<T> list = new LinkedList<>();
        for (String subString : StringMaster.openContainer(property, separator)) {
            T ENUM = retrieveEnumConst(CLASS, subString);
            if (ENUM != null)
                list.add(ENUM);
        }
        return list;
    }

    public Comparator<T> getEnumSorter(final Class<?> ENUM) {
        return new Comparator<T>() {
            public int compare(T o1, T o2) {
                // Chronos.mark(ENUM + " sorting of " + o1 + " & " + o2 + " ");
                int result = compare(ENUM, o1, o2);
                // Chronos.logTimeElapsedForMark(ENUM + " sorting of " + o1 +
                // " & " + o2 + " ");
                return result;
            }

            private int compare(final Class<?> ENUM, T o1, T o2) {
                if (ENUM == null)
                    return 0;
                if (o1 == null)
                    return 1;
                if (o2 == null)
                    return -1;
                T enumConst = (T) EnumMaster.getEnumConst(ENUM, o1.toString());

                int index = getEnumConstIndex(ENUM, enumConst);
                if (index == -1)
                    return 1;
                enumConst = (T) EnumMaster.getEnumConst(ENUM, o2.toString());
                int index2 = getEnumConstIndex(ENUM, enumConst);
                if (index2 == -1)
                    return -1;
                if (index > index2)
                    return 1;
                if (index < index2)
                    return -1;
                return 0;
            }
        };
    }

    public Comparator<? super String> getEnumTypesSorter(final boolean subgroup, final OBJ_TYPE TYPE) {
        String name = subgroup ? TYPE.getSubGroupingKey().getName() : TYPE.getGroupingKey()
                .getName();
        final Class<?> ENUM = EnumMaster.getEnumClass(name);
        // OBJ_TYPES ST = new EnumMaster<OBJ_TYPES>().retrieveEnumConst(
        // OBJ_TYPES.class, name, true);
        // would be nice! But where other than in Unit-Deity is it really used?
        return new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (ENUM == null)
                    return 0;
                ObjType type1 = DataManager.getType(o1, TYPE);
                ObjType type2 = DataManager.getType(o2, TYPE);

                if (TYPE == OBJ_TYPES.CHARS) {
                    CUSTOM_HERO_GROUP group = new EnumMaster<CUSTOM_HERO_GROUP>()
                            .retrieveEnumConst(CUSTOM_HERO_GROUP.class, type1
                                    .getProperty(G_PROPS.CUSTOM_HERO_GROUP));
                    CUSTOM_HERO_GROUP group2 = new EnumMaster<CUSTOM_HERO_GROUP>()
                            .retrieveEnumConst(CUSTOM_HERO_GROUP.class, type2
                                    .getProperty(G_PROPS.CUSTOM_HERO_GROUP));
                    int index = EnumMaster.getEnumConstIndex(CUSTOM_HERO_GROUP.class, group);
                    int index2 = EnumMaster.getEnumConstIndex(CUSTOM_HERO_GROUP.class, group2);
                    if (index == -1)
                        index = Integer.MAX_VALUE;
                    if (index2 == -1)
                        index2 = Integer.MAX_VALUE;

                    if (index != index2) {
                        if (index < index2)
                            return -1;
                        return 1;
                    }

                }

                Object enumConst = EnumMaster.getEnumConst(ENUM, subgroup ? type1
                        .getSubGroupingKey() : type1.getGroupingKey());
                String name = "" + enumConst;
                int index = ListMaster.getIndexString(ListMaster.toStringList(ENUM
                        .getEnumConstants()), name, true);
                if (index == -1)
                    return 1;
                enumConst = ""
                        + EnumMaster.getEnumConst(ENUM, subgroup ? type2.getSubGroupingKey()
                        : type2.getGroupingKey());
                name = "" + enumConst;
                int index2 = ListMaster.getIndexString(ListMaster.toStringList(ENUM
                        .getEnumConstants()), name, true);
                if (index == index2) {
                    index = StringMaster.getInteger(type1.getProperty(G_PROPS.ID));
                    index2 = StringMaster.getInteger(type2.getProperty(G_PROPS.ID));

                }
                if (index2 == -1)
                    return -1;
                if (index > index2)
                    return 1;
                if (index < index2)
                    return -1;

                return 0;
            }
        };
    }

    public T selectEnum(Class<T> class1) {
        return retrieveEnumConst(class1, ListChooser.chooseEnum(class1));
    }

    // public T selectEnum() {
    // new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, string );
    // return null;
    // }

    public T retrieveEnumFromEntityProp(Class<T> CLASS, Entity entity) {
        String string = entity.getProperty(ContentManager.findPROP(CLASS.getSimpleName()));
        return retrieveEnumConst(CLASS, string);
    }

    public int getEnumConstIndex(T constant) {
        if (constant == null)
            return 0;
        if (constant.getClass().getEnumConstants() == null)
            return 0;
        int i = 0;
        for (Object o : constant.getClass().getEnumConstants()) {
            if (o == constant)
                return i;
            i++;
        }
        return -1;
    }

}