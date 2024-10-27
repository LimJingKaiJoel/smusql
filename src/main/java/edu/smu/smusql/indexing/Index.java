// Index.java
package edu.smu.smusql.indexing;

import java.util.*;

import edu.smu.smusql.Row;

public class Index {
    private String columnName;
    private Map<Object, List<Row>> indexMap;

    public Index(String columnName) {
        this.columnName = columnName;
        this.indexMap = new HashMap<>();
    }

    public void addToIndex(Object key, Row row) {
        indexMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
    }

    public void removeFromIndex(Object key, Row row) {
        List<Row> rows = indexMap.get(key);
        if (rows != null) {
            rows.remove(row);
            if (rows.isEmpty()) {
                indexMap.remove(key);
            }
        }
    }

    public List<Row> getRows(Object key) {
        return indexMap.getOrDefault(key, Collections.emptyList());
    }

    public String getColumnName() {
        return columnName;
    }
}