package edu.smu.smusql.column;
import java.util.*;

public class CustomHashMap<K, V> extends AbstractMap<K, V> {
    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private final float loadFactor;
    private final float resizingFactor;
    private int threshold;

    // final float DEFAULT_LOAD_FACTOR = 0.75f;
    // final float DEFAULT_RESIZING_FACTOR = 2.0f;
    // final int DEFAULT_INITIAL_CAPACITY = 16;

    static class Entry<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K k, V v, Entry<K, V> n) {
            key = k;
            value = v;
            next = n;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V oldVal = value;
            value = newValue;
            return oldVal;
        }
    }

    // public CustomHashMap() {
    //     this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_RESIZING_FACTOR);
    // }

    public CustomHashMap(int initialCapacity, float loadFactor, float resizingFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        } if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        } if (resizingFactor <= 1.0f) {
            throw new IllegalArgumentException("Resizing factor must be greater than 1!!");
        }

        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.resizingFactor = resizingFactor;
        this.threshold = (int) (capacity * loadFactor);
        table = new Entry[capacity];
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);

        // this gets the index it's stored inbased on the hash
        int index = indexFor(hash, capacity);

        // iterate through the linked list at the index -> find the key and update the value if present
        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if ((key == null && e.key == null) || (key != null && key.equals(e.key))) {
                V oldVal = e.value;
                e.value = value;
                return oldVal;
            }
        }

        // else if key not found, add a new entry
        addEntry(hash, key, value, index);
        return null;
    }

    private void addEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K, V> newEntry = new Entry<>(key, value, table[bucketIndex]);
        table[bucketIndex] = newEntry;
        size++;

        if (size >= threshold) {
            resize();
        }

        return;
    }

    private void resize() {
        int newCapacity = (int) (capacity * resizingFactor);
        Entry<K, V>[] oldTable = table;
        table = new Entry[newCapacity];
        capacity = newCapacity;
        threshold = (int) (capacity * loadFactor);

        for (Entry<K, V> e : oldTable) {
            while (e != null) {
                Entry<K, V> next = e.next;
                int index = indexFor(hash(e.key), capacity);
                e.next = table[index];
                table[index] = e;
                e = next;
            }
        }

        return;
    }

    @Override
    public V get(Object key) {
        int hash = hash(key);
        int index = indexFor(hash, capacity);

        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if ((key == null && e.key == null) || (key != null && key.equals(e.key)))
                return e.value;
        }

        return null;
    }

    @Override
    public V remove(Object key) {
        int hash = hash(key);
        int index = indexFor(hash, capacity);
        Entry<K, V> prev = null;
        Entry<K, V> e = table[index];

        while (e != null) {
            if ((key == null && e.key == null) || (key != null && key.equals(e.key))) {
                if (prev == null)
                    table[index] = e.next;
                else
                    prev.next = e.next;
                size--;
                return e.value;
            }
            prev = e;
            e = e.next;
        }

        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    private int hash(Object key) {
        return (key == null) ? 0 : key.hashCode();
    }

    private int indexFor(int hash, int capacity) {
        return Math.abs(hash) % capacity;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = new HashSet<>();
        for (Entry<K, V> e : table) {
            while (e != null) {
                es.add(e);
                e = e.next;
            }
        }
        return es;
    }

    @Override
    public Set<K> keySet() {
        Set<K> ks = new HashSet<>();
        for (Entry<K, V> e : table) {
            while (e != null) {
                ks.add(e.key);
                e = e.next;
            }
        }
        return ks;
    }
}