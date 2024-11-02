package edu.smu.smusql.column;
import java.util.*;

public class CustomHashMapPseudorandomProbing<K, V> extends AbstractMap<K, V> {
    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private final float loadFactor;
    private final float resizingFactor;
    private int threshold;

    final long LCG_MULTIPLIER = 1664525;
    final long LCG_INCREMENT = 1013904223;
    final long LCG_MODULUS = ((long) Math.pow(2, 32)) - 1;

    static class Entry<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        boolean isDeleted;

        Entry(K k, V v) {
            key = k;
            value = v;
            isDeleted = false;
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

    public CustomHashMapPseudorandomProbing(int initialCapacity, float loadFactor, float resizingFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        } 
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        } 
        if (resizingFactor <= 1.0f) {
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
        long seed = Math.abs(hash(key));
        int firstDeletedIndex = -1;

        for (int i = 0; i < capacity; i++) {
            seed = (seed * LCG_MULTIPLIER + LCG_INCREMENT) & LCG_MODULUS;
            int probeIndex = (int)(seed % capacity);
            Entry<K, V> e = table[probeIndex];

            if (e == null) {
                if (firstDeletedIndex != -1) {
                    table[firstDeletedIndex] = new Entry<>(key, value);
                } else {
                    table[probeIndex] = new Entry<>(key, value);
                }
                size++;
                if (size >= threshold) {
                    resize();
                }
                return null;
            } else if (e.isDeleted) {
                if (firstDeletedIndex == -1) {
                    firstDeletedIndex = probeIndex;
                }
            } else if ((key == null && e.key == null) || (key != null && key.equals(e.key))) {
                V oldVal = e.value;
                e.value = value;
                return oldVal;
            }
        }
        throw new IllegalStateException("HashMap is full");
    }

    @Override
    public V get(Object key) {
        long seed = Math.abs(hash(key));

        for (int i = 0; i < capacity; i++) {
            seed = (seed * LCG_MULTIPLIER + LCG_INCREMENT) & LCG_MODULUS;
            int probeIndex = (int)(seed % capacity);
            Entry<K, V> e = table[probeIndex];

            if (e == null) {
                return null;
            } else if (!e.isDeleted && ((key == null && e.key == null) || (key != null && key.equals(e.key)))) {
                return e.value;
            }
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        long seed = Math.abs(hash(key));

        for (int i = 0; i < capacity; i++) {
            seed = (seed * LCG_MULTIPLIER + LCG_INCREMENT) & LCG_MODULUS;
            int probeIndex = (int)(seed % capacity);
            Entry<K, V> e = table[probeIndex];

            if (e == null) {
                return null;
            } else if (!e.isDeleted && ((key == null && e.key == null) || (key != null && key.equals(e.key)))) {
                V oldValue = e.value;
                e.isDeleted = true;
                size--;
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    private void resize() {
        int newCapacity = (int) (capacity * resizingFactor);
        Entry<K, V>[] oldTable = table;
        table = new Entry[newCapacity];
        capacity = newCapacity;
        threshold = (int) (capacity * loadFactor);
        size = 0;

        for (Entry<K, V> e : oldTable) {
            if (e != null && !e.isDeleted) {
                put(e.key, e.value);
            }
        }
    }

    private int hash(Object key) {
        return (key == null) ? 0 : key.hashCode();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = new HashSet<>();
        for (Entry<K, V> e : table) {
            if (e != null && !e.isDeleted) {
                es.add(e);
            }
        }
        return es;
    }

    @Override
    public Set<K> keySet() {
        Set<K> ks = new HashSet<>();
        for (Entry<K, V> e : table) {
            if (e != null && !e.isDeleted) {
                ks.add(e.key);
            }
        }
        return ks;
    }
}