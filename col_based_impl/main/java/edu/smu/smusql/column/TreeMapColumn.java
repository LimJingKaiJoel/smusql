package edu.smu.smusql.column;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.smu.smusql.Row;

public class TreeMapColumn extends AbstractColumn {
    private TreeMap<String, List<Row>> values;

    public TreeMapColumn(String name) {
        super(name);
        initValues();
    }

    @Override
    public void initValues() {
        this.values = new TreeMap<>(new CustomComparator());
        // this.values = new TreeMap<>();
    }

    @Override
    public void insertRow(String columnValue, Row row) {
        List<Row> rows = this.values.get(columnValue);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        rows.add(row);
        this.values.put(columnValue, rows);
    }

    @Override
    public void removeRow(String columnValue, Row row) {
        List<Row> rows = this.values.get(columnValue);
        if (rows != null) {
            if (rows.size() == 1) {
                this.values.remove(columnValue);
            } else {
                rows.remove(row);
            }
        }
    }

    @Override
    public List<Row> getRows(String operator, String value) {
        List<Row> result = new ArrayList<>();

        if (operator.equals("=")) {
            if (!this.values.containsKey(value)) {
                return result;
            }
            result.addAll(this.values.get(value));
        } else if (operator.equals(">")) {
            this.values.tailMap(value).forEach((key, mapValue) -> {
                result.addAll(mapValue);
            });
        } else if (operator.equals("<")) {
            this.values.headMap(value).forEach((key, mapValue) -> {
                result.addAll(mapValue);
            });
        } else if (operator.equals(">=")) {
            this.values.tailMap(value, true).forEach((key, mapValue) -> {
                result.addAll(mapValue);
            });
        } else if (operator.equals("<=")) {
            this.values.headMap(value, true).forEach((key, mapValue) -> {
                result.addAll(mapValue);
            });
        } else if (operator.equals("!=")) {
            this.values.forEach((key, mapValue) -> {
                if (!key.equals(value)) {
                    result.addAll(mapValue);
                }
            });
        }

        return result;
    }
}
