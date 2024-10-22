package edu.smu.smusql.index;

import java.util.*;

public interface Index<K extends Comparable<K>, V> {
    void insert (K key, V value);
    void delete (K key, V value);
    List<V> search (K key);
    List<V> rangeSearch (String operation, K key);
}