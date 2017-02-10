package main.elements;

import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.FilteringCondition;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.obj.Obj;
import main.game.Game;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;

public class Filter<T extends Entity> extends ReferredElement {
    Conditions conditions;
    FilteringCondition filteringConditions;
    Integer match;
    private OBJ_TYPE TYPE;
    private Collection<Obj> objPool;
    private LinkedList<OBJ_TYPE> TYPES;
    private Collection<Integer> dynamicExceptions;
    private HashSet<Obj> filteredSet;
    private Map<Integer, Condition> conditionDebugCache = new XLinkedMap<>();

    public Filter() {
    }

    public Filter(Ref ref, Condition conditions, OBJ_TYPE TYPE) {
        this(ref, conditions);
        this.TYPE = TYPE;
    }

    public Filter(Game game, Condition conditions) {
        if (conditions instanceof Conditions) {
            this.conditions = (Conditions) conditions;
        } else {
            this.conditions = new Conditions(conditions);
        }
        this.game = game;
    }

    public Filter(Ref ref, Condition conditions) {
        if (conditions instanceof Conditions) {
            this.conditions = (Conditions) conditions;
        } else {
            this.conditions = new Conditions(conditions);
        }
        setRef(ref);
    }

    public Filter(Collection<Obj> list, Ref ref, Condition condition) {
        this(ref, condition);
        this.objPool = list;
    }

    public Filter(Collection<Obj> objects, Ref ref, OBJ_TYPE targetType) {
        this(objects, ref, new Conditions());
        this.TYPE = targetType;
    }

    public static void filter(Collection<? extends Entity> objects, OBJ_TYPE TYPE) {
        for (Entity obj : objects) {
            if (!TYPE.equals(obj.getOBJ_TYPE_ENUM())) {
                objects.remove(obj);
            }
        }
    }

    @Override
    public String toString() {
        return "Filter: " + conditions;
    }

    public boolean match(Entity entity) {
        if (ref != null) {
            return match(getConditions(), entity.getId(), ref);
        }
        return match(getConditions(), entity.getId(), entity.getRef());
    }

    public boolean match(Integer id) {
        return match(getConditions(), id);
    }

    public boolean match(Condition c, Integer id) {
        return match(c, id, ref);
    }

    public boolean match(Condition c, Integer id, Ref REF) {
        if (dynamicExceptions != null) {
            if (dynamicExceptions.contains(id)) {
                return false;
            }
        }
        if (c == null) {
            return true;
        }
        match = id;

        REF.setID(KEYS.MATCH.name(), id);
        return c.check(REF);

    }

    public void addCondition(Condition c) {
        if (!(conditions instanceof Conditions)) {
            conditions = new Conditions(conditions, c);
        } else {
            conditions.add(c);
        }
    }

    public Set<T> getObjects(Ref ref) {
        setRef(ref);
        return getObjects();
    }

    public Set<T> getObjects() {
        if (isDebug()) {
            LinkedList<Obj> list = new LinkedList<>(getFilteredObjectPool());
            Set<Obj> set = new HashSet<>();
            loop:
            for (Obj obj : list) {
                for (Condition c : getConditions()) {
                    if (!match(c, obj.getId())) {
                        conditionDebugCache.put(obj.getId(), c);
                        continue loop;
                    }
                }
                set.add(obj);
            }
            return (Set<T>) new HashSet<>(set);
        }
        Collection<Obj> pool = getFilteredObjectPool();
        Set<T> filteredSet = (Set<T>) new HashSet<>(pool);
        LinkedList<Obj> list = new LinkedList<>(pool);
        for (Condition c : getConditions()) {
            for (Obj obj : list) {
                if (!match(c, obj.getId())) {
                    filteredSet.remove(obj);
                }
            }
        }
        return filteredSet;
        // TODO sort out; use cache
    }

    private boolean isDebug() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * Only if T==ObjTypes.class
     *
     * @return
     */
    public List<T> getTypes() {
        List<T> list;
        if (TYPE != null) {
            list = (List<T>) DataManager.getTypes(TYPE);
        } else {
            list = (List<T>) DataManager.getTypes();
        }
        return filter(list);
    }

    private Collection<Obj> getObjectPool() {
        if (objPool == null) {
            objPool = game.getObjects();
        }
        return objPool;
    }

    public Set<Integer> getObjectIds() {
        // TYPE!
        Set<Integer> resultset;
        resultset = new HashSet<>();
        if (game == null) {
            game = Game.game;
        }
        Collection<Integer> filteredIdPool = getFilteredIdSet(Game.game.getObjectIds());

        for (Integer id : filteredIdPool) {
            if (match(id)) {
                resultset.add(id);
            }
        }
        return resultset;

    }

    private Collection<Obj> getFilteredObjectPool() {
        if (TYPE == null && TYPES == null) {
            return getObjectPool();
        }
        if (TYPE != null) {
            return game.getObjects(TYPE);
        }

        Set<Obj> filteredSet = new HashSet<>();
        for (OBJ_TYPE TYPE : TYPES) {
            filteredSet.addAll(game.getObjects(TYPE));
        }
        return filteredSet;
    }

    private boolean matchTYPE(Obj obj) {
        if (TYPES != null) {
            return TYPES.contains(obj.getOBJ_TYPE_ENUM());
        }
        return (TYPE.equals(obj.getOBJ_TYPE_ENUM()));
    }

    private Collection<Integer> getFilteredIdSet(Collection<Integer> pool) {
        Collection<Obj> objects = getFilteredObjectPool();
        return StringMaster.convertToIdIntList(objects);
    }

    public Conditions getConditions() {
        return conditions;
    }

    // @Override
    // public void setRef(Ref ref) {
    // this.ref = ref;
    // this.game = ref.getGame();
    // ref.getSource();
    // ref.getTarget();
    // this.match = ref.getMatch();
    // }

    public void setConditions(Condition conditions) {
        if (conditions instanceof Conditions) {
            this.conditions = (Conditions) conditions;
        } else {
            this.conditions = new Conditions(conditions);
        }
    }

    public GroupImpl getGroup() {

        GroupImpl group = new GroupImpl((Collection<Obj>) getObjects());

        return group;
    }

    public void setTYPES(LinkedList<OBJ_TYPE> types2) {
        this.TYPES = types2;

    }

    public List<T> filter(Collection<T> objects) {
        List<T> resultset = new LinkedList<>();
        for (T obj : objects) {
            if (match(obj)) {
                resultset.add(obj);
            } else {
                this.TYPE = TYPE; // for debugging :)
            }
        }
        return resultset;
    }

    public Collection<Integer> getDynamicExceptions() {
        if (dynamicExceptions == null) {
            dynamicExceptions = new DequeImpl<>();
        }
        return dynamicExceptions;
    }

    public void setDynamicExceptions(Collection<Integer> dynamicExceptions) {
        this.dynamicExceptions = dynamicExceptions;
    }

    public OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(OBJ_TYPE type) {
        this.TYPE = type;
    }

    public List<T> filter(List<T> typeList, VALUE filterValue, String value) {
        List<T> list = new LinkedList<>();

        for (T t : typeList) {
            if (t == null) {
                continue;
            }
            if (filterValue instanceof PROPERTY) {
                if (StringMaster.compareByChar(t.getValue(filterValue), value, false)) {
                    list.add(t);
                }
            }

            if (filterValue instanceof PARAMETER) {
                if (t.getIntParam((PARAMETER) filterValue) == StringMaster.getInteger(value)) {
                    list.add(t);
                }
            }
        }

        return list;
    }

    public enum FILTERS {
        ALLY, SELF, ENEMY, ALIVE, NOT_SELF,
    }

}
