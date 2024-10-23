package edu.smu.smusql.index.datastruc;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import edu.smu.smusql.index.Index;

/**
 * SkipListIndex uses Java's ConcurrentSkipListMap.
 * 
 *
 * @param <K> The type of the key (must be Comparable).
 * @param <V> The type of the value (typically Row).
 */
public class SkipListIndex<K extends Comparable<K>, V> implements Index<K, V> {
    private final ConcurrentSkipListMap<K, List<V>> skipListMap;

    /**
     * Constructor to initialize the SkipListIndex.
     */
    public SkipListIndex() {
        this.skipListMap = new ConcurrentSkipListMap<>();
    }

    /**
     * Inserts a key-value pair into the SkipListIndex.
     *
     * @param key   The key to insert.
     * @param value The row reference associated with the key.
     */
    @Override
    public void insert(K key, V value) {
        skipListMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Deletes a key-value pair from the SkipListIndex.
     *
     * @param key   The key of the pair to delete.
     * @param value The row reference associated with the key.
     */
    @Override
    public void delete(K key, V value) {
        List<V> values = skipListMap.get(key);
        if (values != null) {
            values.remove(value);
            if (values.isEmpty()) {
                skipListMap.remove(key);
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
        List<V> values = skipListMap.get(key);
        return values != null ? new ArrayList<>(values) : new ArrayList<>();
    }

    /**
     * Performs a range search based on the operator and value.
     *
     * @param operator The comparison operator (e.g., ">", ">=", "<", "<=").
     * @param value    The value to compare against.
     * @return A list of rows satisfying the range condition.
     */
    @Override
    public List<V> rangeSearch(String operator, K value) {
        List<V> result = new ArrayList<>();

        switch (operator) {
            case ">":
                Map<K, List<V>> tailMapGreater = skipListMap.tailMap(value, false);
                Collection<List<V>> greaterValues = tailMapGreater.values();
                for (List<V> list : greaterValues) {
                    for (V row : list) {
                        result.add(row);
                    }
                }
                break;
    
            case ">=":
                Map<K, List<V>> tailMapGreaterOrEqual = skipListMap.tailMap(value, true);
                Collection<List<V>> greaterOrEqualValues = tailMapGreaterOrEqual.values();
                for (List<V> list : greaterOrEqualValues) {
                    for (V row : list) {
                        result.add(row);
                    }
                }
                break;
    
            case "<":
                Map<K, List<V>> headMapLess = skipListMap.headMap(value, false);
                Collection<List<V>> lessValues = headMapLess.values();
                for (List<V> list : lessValues) {
                    for (V row : list) {
                        result.add(row);
                    }
                }
                break;
    
            case "<=":
                Map<K, List<V>> headMapLessOrEqual = skipListMap.headMap(value, true);
                Collection<List<V>> lessOrEqualValues = headMapLessOrEqual.values();
                for (List<V> list : lessOrEqualValues) {
                    for (V row : list) {
                        result.add(row);
                    }
                }
                break;
    
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }

        return result;
    }

    // debug
    public void traverse() {
        for (Map.Entry<K, List<V>> entry : skipListMap.entrySet()) {
            System.out.print("Key: " + entry.getKey() + " -> Rows: ");
            for (V row : entry.getValue()) {
                System.out.print(row + " ");
            }
            System.out.println();
        }
    }
}