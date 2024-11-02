package edu.smu.smusql.noindex;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class CustomDynamicArray<E> implements List<E> {

    private Object[] elementData;
    private int size;
    private double growthFactor;

    private static final int DEFAULT_CAPACITY = 10000;
    private static final double DEFAULT_GROWTH_FACTOR = 3.5;

    public CustomDynamicArray() {
        this(DEFAULT_CAPACITY, DEFAULT_GROWTH_FACTOR);
    }

    public CustomDynamicArray(int initialCapacity) {
        this(initialCapacity, DEFAULT_GROWTH_FACTOR);
    }

    public CustomDynamicArray(int initialCapacity, double growthFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        if (growthFactor <= 1.0)
            throw new IllegalArgumentException("Growth factor must be greater than 1.0");
        this.elementData = new Object[initialCapacity];
        this.size = 0;
        this.growthFactor = growthFactor;
    }

    public CustomDynamicArray(Collection<? extends E> c) {
        this(Math.max(DEFAULT_CAPACITY, c.size()), DEFAULT_GROWTH_FACTOR);
        addAll(c);
    }

    /** Ensures capacity for adding new elements */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length) {
            int newCapacity = (int) (elementData.length * growthFactor);
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }

    @Override
    public boolean add(E e) {
        ensureCapacity(size + 1);
        elementData[size++] = e;
        return true;
    }

    @Override
    public E get(int index) {
        rangeCheck(index);
        return elementAt(index);
    }

    @Override
    public E set(int index, E element) {
        rangeCheck(index);
        E oldValue = elementAt(index);
        elementData[index] = element;
        return oldValue;
    }

    @Override
    public E remove(int index) {
        rangeCheck(index);
        E oldValue = elementAt(index);
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        elementData[--size] = null; 
        return oldValue;
    }

    @Override
    public boolean remove(Object o) {
        for (int index = 0; index < size; index++)
            if (Objects.equals(o, elementData[index])) {
                fastRemove(index);
                return true;
            }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    private void rangeCheck(int index) {
        if (index >= size || index < 0)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size + " | Index is out of bounds!!");
    }

    @SuppressWarnings("unchecked")
    E elementAt(int index) {
        return (E) elementData[index];
    }

    private void fastRemove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        elementData[--size] = null;
    }

    @Override
    public Iterator<E> iterator() {
        return new CustomIterator();
    }

    private class CustomIterator implements Iterator<E> {
        int cursor; // index of next element to return
        int lastRet = -1; // index of last element returned

        public boolean hasNext() {
            return cursor != size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = CustomDynamicArray.this.elementData;
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            CustomDynamicArray.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll is not supported.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll is not supported.");
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException("listIterator is not supported.");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException("listIterator at index is not supported.");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList is not supported.");
    }

        @Override
    public void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException("replaceAll is not supported.");
    }

    @Override
    public void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException("sort is not supported.");
    }

    @Override
    public Spliterator<E> spliterator() {
        throw new UnsupportedOperationException("spliterator is not supported.");
    }

    @Override
    public boolean removeIf(java.util.function.Predicate<? super E> filter) {
        throw new UnsupportedOperationException("removeIf is not supported.");
    }

    @Override
    public Stream<E> stream() {
        throw new UnsupportedOperationException("stream is not supported.");
    }

    @Override
    public Stream<E> parallelStream() {
        throw new UnsupportedOperationException("parallelStream is not supported.");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("toArray is not supported.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray is not supported.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear is not supported.");
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("add at index is not supported.");
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("addAll is not supported.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("addAll at index is not supported.");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("indexOf is not supported.");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("lastIndexOf is not supported.");
    }
    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("containsAll is not supported.");
    }
}