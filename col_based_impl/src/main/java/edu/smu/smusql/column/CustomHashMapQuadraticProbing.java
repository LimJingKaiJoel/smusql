package edu.smu.smusql.column;
import java.util.*;

public class CustomHashMapQuadraticProbing<K, V> extends AbstractMap<K, V> {
    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private final float loadFactor;
    private final float resizingFactor;
    private int threshold;

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

    public CustomHashMapQuadraticProbing(int initialCapacity, float loadFactor, float resizingFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        } 
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        } 
        if (resizingFactor <= 1.0f) {
            throw new IllegalArgumentException("Resizing factor must be greater than 1!!");
        }
        
        this.capacity = nextPrime(initialCapacity);
        this.loadFactor = loadFactor;
        this.resizingFactor = resizingFactor;
        this.threshold = (int) (capacity * loadFactor);
        table = new Entry[capacity];
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, capacity);
        int firstDeletedIndex = -1;

        for (int i = 0; i < capacity; i++) {
            int probeIndex = (index + i * i) % capacity;
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
        int hash = hash(key);
        int index = indexFor(hash, capacity);

        for (int i = 0; i < capacity; i++) {
            int probeIndex = (index + i * i) % capacity;
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
        int hash = hash(key);
        int index = indexFor(hash, capacity);

        for (int i = 0; i < capacity; i++) {
            int probeIndex = (index + i * i) % capacity;
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
        int newCapacity = nextPrime((int) (capacity * resizingFactor));
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

    private int indexFor(int hash, int capacity) {
        return Math.abs(hash) % capacity;
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

    private int nextPrime(int n) {
        while (true) {
            if (isPrime(n)) return n;
            n++;
        }
    }
    
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}