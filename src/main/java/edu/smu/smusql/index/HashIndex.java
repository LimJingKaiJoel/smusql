package edu.smu.smusql.index;

import java.util.*;

// Optimises for equality queries! 
public class HashIndex<K extends Comparable<K>, V> implements Index<K, V> {
    private final HashMap<K, List<V>> hashMap;

    public HashIndex() {
        this.hashMap = new HashMap<>();
    }

    /**
     * Inserts a key-value pair into the HashIndex.
     *
     * @param key   The key to insert.
     * @param value The row reference associated with the key.
     */
    @Override
    public void insert(K key, V value) {
        hashMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Deletes a key-value pair from the HashIndex.
     *
     * @param key   The key of the pair to delete.
     * @param value The row reference associated with the key.
     */
    @Override
    public void delete(K key, V value) {
        List<V> values = hashMap.get(key);
        if (values != null) {
            values.remove(value);
            if (values.isEmpty()) {
                hashMap.remove(key);
            }
        }
    }

    /**
     * Searches for rows matching the exact key.
     *
     * @param key The key to search for.
     * @return A list of rows matching the key, or an empty list if none found.
     */
    @Override
    public List<V> search(K key) {
        List<V> values = hashMap.get(key);
        return values != null ? new ArrayList<>(values) : Collections.emptyList();
    }

    /**
     * Performs a range search based on the operator and value.
     * This is bad because HashIndex is not really for rangeSearch, but have to implement
     * By right we should not use this
     *
     * @param operator The comparison operator (e.g., ">", ">=", "<", "<=").
     * @param value    The value to compare against.
     * @return A list of rows satisfying the range condition.
     * @throws IllegalArgumentException If a range search is attempted.
     */
    @Override
    public List<V> rangeSearch(String operator, K value) {
        List<V> result = new ArrayList<>();

        for (Map.Entry<K, List<V>> entry : hashMap.entrySet()) {
            K key = entry.getKey();
            boolean matches = false;
            switch (operator) {
                case ">":
                    matches = key.compareTo(value) > 0;
                    break;
                case ">=":
                    matches = key.compareTo(value) >= 0;
                    break;
                case "<":
                    matches = key.compareTo(value) < 0;
                    break;
                case "<=":
                    matches = key.compareTo(value) <= 0;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
            
            if (matches) {
                result.addAll(entry.getValue());
            }
        }

        return result;
    }

    // debug method
    public void traverse() {
        for (Map.Entry<K, List<V>> entry : hashMap.entrySet()) {
            System.out.print("Key: " + entry.getKey() + " -> Rows: ");
            for (V row : entry.getValue()) {
                System.out.print(row + " ");
            }
            System.out.println();
        }
    }
}