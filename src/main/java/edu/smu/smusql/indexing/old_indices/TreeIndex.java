package edu.smu.smusql.indexing.old_indices;

import java.util.*;

// K is the thing we are looking for, V is the row
public class TreeIndex<K extends Comparable<K>, V> implements Index<K, V> {
    private final TreeMap<K, List<V>> treeMap;

    public TreeIndex() {
        this.treeMap = new TreeMap<>();
    }

    /**
     * Inserts a key-value pair into the TreeIndex.
     *
     * @param key   The key to insert.
     * @param value The row reference associated with the key.
     */
    @Override
    public void insert(K key, V value) {
        treeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Deletes a key-value pair from the TreeIndex.
     *
     * @param key   The key of the pair to delete.
     * @param value The row reference associated with the key.
     */
    @Override
    public void delete(K key, V value) {
        List<V> values = treeMap.get(key);
        if (values != null) {
            values.remove(value);
            if (values.isEmpty()) {
                treeMap.remove(key);
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
        List<V> values = treeMap.get(key);
        return values != null ? new ArrayList<V>(values) : new ArrayList<V>();
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
                Map<K, List<V>> tailMapGreater = treeMap.tailMap(value, false);
                Collection<List<V>> greaterValues = tailMapGreater.values();
                for (List<V> list : greaterValues) {
                    result.addAll(list);
                }
                break;
            case ">=":
                Map<K, List<V>> tailMapGreaterOrEqual = treeMap.tailMap(value, true);
                Collection<List<V>> greaterOrEqualValues = tailMapGreaterOrEqual.values();
                for (List<V> list : greaterOrEqualValues) {
                    result.addAll(list);
                }
                break;
            case "<":
                Map<K, List<V>> headMapLess = treeMap.headMap(value, false);
                Collection<List<V>> lessValues = headMapLess.values();
                for (List<V> list : lessValues) {
                    result.addAll(list);
                }
                break;
            case "<=":
                Map<K, List<V>> headMapLessOrEqual = treeMap.headMap(value, true);
                Collection<List<V>> lessOrEqualValues = headMapLessOrEqual.values();
                for (List<V> list : lessOrEqualValues) {
                    result.addAll(list);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }

        return result;
    }

    // Debug method because I can't test it out yet
    public void traverse() {
        for (Map.Entry<K, List<V>> entry : treeMap.entrySet()) {
            System.out.print("Key: " + entry.getKey() + " MAPPING TO THESE Rows: ");
            for (V row : entry.getValue()) {
                System.out.print(row + " ");
            }
            System.out.println();
        }
    }
}