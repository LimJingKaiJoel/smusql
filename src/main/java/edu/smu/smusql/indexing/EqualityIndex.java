package edu.smu.smusql.indexing;

import java.util.*;
import edu.smu.smusql.Row;

public class EqualityIndex implements Index {
    private String columnName;
    private Map<Object, List<Row>> indexMap;
    private int columnIndex;

    public EqualityIndex(String columnName, int columnIndex) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.indexMap = new HashMap<>();
    }

    @Override
    public void addRow(Row row) {
        Object key = row.getDataRow()[columnIndex];
        indexMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
    }

    @Override
    public void removeRow(Row row) {
        Object key = row.getDataRow()[columnIndex];
        List<Row> rows = indexMap.get(key);
        if (rows != null) {
            rows.remove(row);
            if (rows.isEmpty()) {
                indexMap.remove(key);
            }
        }
    }

    @Override
    public List<Row> search(String operator, Object value) {
        switch (operator) {
            case "=":
                return indexMap.getOrDefault(value, Collections.emptyList());
            case "!=":
                // Return all rows except those matching the value
                List<Row> result = new ArrayList<>();
                for (Map.Entry<Object, List<Row>> entry : indexMap.entrySet()) {
                    if (!entry.getKey().equals(value)) {
                        result.addAll(entry.getValue());
                    }
                }
                return result;
            default:
                return null;
        }
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}