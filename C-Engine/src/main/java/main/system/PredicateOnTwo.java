package main.system;

/**
 * Created by JustMe on 2/19/2017.
 */
@FunctionalInterface
public interface PredicateOnTwo<T, E> {

    boolean is(T t, E e);
}
