package com.zhenxi.Superappium.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Zhenxi on 2020-07-05
 */
public class Lists {
    public static <E> ArrayList<E> newArrayList(E... elements) {
        // Avoid integer overflow when a large array is passed in
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    static int computeArrayListCapacity(int arraySize) {

        return saturatedCast(5L + arraySize + (arraySize / 10));
    }


    /**
     * Returns the {@code int} nearest in value to {@code value}.
     *
     * @param value any {@code long} value
     * @return the same value cast to {@code int} if it is in the range of the
     * {@code int} type, {@link Integer#MAX_VALUE} if it is too large,
     * or {@link Integer#MIN_VALUE} if it is too small
     */
    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }

    static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }


    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(
            Iterable<? extends E> elements) {
        // We copy elements to an ArrayList first, rather than incurring the
        // quadratic cost of adding them to the COWAL directly.
        Collection<? extends E> elementsCollection =
                (elements instanceof Collection)
                        ? cast(elements)
                        : newArrayList(elements);
        return new CopyOnWriteArrayList<E>(elementsCollection);
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        // Let ArrayList's sizing logic work, if possible
        return (elements instanceof Collection)
                ? new ArrayList<E>(cast(elements))
                : newArrayList(elements.iterator());
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = newArrayList();
        addAll(list, elements);
        return list;
    }

    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add(iterator.next());
        }
        return wasModified;
    }

    public static <T> List<T> newArrayListWithCapacity(int length) {
        return new ArrayList<>(length);
    }
}
