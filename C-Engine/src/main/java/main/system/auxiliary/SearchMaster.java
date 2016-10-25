package main.system.auxiliary;

import java.util.Arrays;
import java.util.Collection;

public class SearchMaster<T> {
    public static Object findClosest(String name, Object... objects) {
        return new SearchMaster<>().findClosest(name, Arrays.asList(objects));
    }

    public int getIndex(String name, Collection<T> list) {
        return getIndex(name, list, false);
    }

    public int getIndex(String name, Collection<T> list, boolean strict) {
        int i = 0;
        for (T t : list) {
            if (StringMaster.compare(t.toString(), name, true))
                return i;
            i++;
        }
        if (strict)
            return -1;
        i = 0;
        for (T t : list) {
            if (StringMaster.compare(t.toString(), name, false))
                return i;
            i++;
        }

        return -1;
    }

    public T find(String name, Collection<T> list) {
        return find(name, list, false);
    }

    public T findClosest(String name, Collection<T> list) {
        int max_weight = Integer.MIN_VALUE;
        T item = null;
        for (T t : list) {
            int weight = (StringMaster.compareSimilar(StringMaster.toStringForm(t).toLowerCase(),
                    name.toLowerCase()));
            if (weight > max_weight) {
                max_weight = weight;
                item = t;
            }
        }
        return item;
    }

    public T find(String name, Collection<T> list, boolean strict) {

        for (T t : list) {
            if (StringMaster.compareByChar(t.toString(), name, true))
                return t;
        }
        if (strict)
            return null;
        for (T t : list) {
            if (StringMaster.compareByChar(t.toString(), name, false))
                return t;
        }
        for (T t : list) {
            if (StringMaster.compare(t.toString(), name, true))
                return t;
        }
        for (T t : list) {
            if (StringMaster.compare(t.toString(), name, false))
                return t;
        }
        return null;
    }

}