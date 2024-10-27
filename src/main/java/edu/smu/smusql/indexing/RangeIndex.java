package edu.smu.smusql.indexing;

import java.util.*;
import edu.smu.smusql.Row;

public class RangeIndex implements Index {
    private String columnName;
    private NavigableMap<Object, List<Row>> indexMap;
    private int columnIndex;

    public RangeIndex(String columnName, int columnIndex) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.indexMap = new TreeMap<>();
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
        NavigableMap<Object, List<Row>> subMap;
        switch (operator) {
            case ">":
                subMap = indexMap.tailMap(value, false);
                break;
            case ">=":
                subMap = indexMap.tailMap(value, true);
                break;
            case "<":
                subMap = indexMap.headMap(value, false);
                break;
            case "<=":
                subMap = indexMap.headMap(value, true);
                break;
            default:
                return null;
        }
        List<Row> result = new ArrayList<>();
        for (List<Row> rows : subMap.values()) {
            result.addAll(rows);
        }
        return result;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}