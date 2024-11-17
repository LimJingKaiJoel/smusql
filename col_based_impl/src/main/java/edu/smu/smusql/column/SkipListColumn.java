package edu.smu.smusql.column;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.AbstractMap;

import edu.smu.smusql.Row;

public class SkipListColumn extends AbstractColumn {
    private ConcurrentSkipListMap<String, List<Row>> values;
    
    public SkipListColumn(String name) {
        super(name);
        initValues();
    }

    @Override
    public void initValues() {
        this.values = new ConcurrentSkipListMap<>(new CustomComparator());
        // this.values = new ConcurrentSkipListMap<>();
    }

    @Override
    public AbstractMap<String, List<Row>> getValues() {
        return this.values;
    }

    @Override
    public void insertRow(String columnValue, Row row) {
        this.values.computeIfAbsent(columnValue, k -> new ArrayList<>()).add(row);
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
            this.values.tailMap(value, false).forEach((key, mapValue) -> {
                result.addAll(mapValue);
            });
        } else if (operator.equals("<")) {
            this.values.headMap(value, false).forEach((key, mapValue) -> {
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

    @Override
    public List<Row> getRowsRange(String operator1, Object value1, String operator2, Object value2) {
        List<Row> result = new ArrayList<>();

        if (operator1.equals("=") && operator2.equals("=")) {
            if (!this.values.containsKey(value1.toString()) || !this.values.containsKey(value2.toString())) {
                return result;
            }
            result.addAll(this.values.get(value1.toString()));
            result.retainAll(this.values.get(value2.toString()));
        } else {
            this.values.subMap(value1.toString(), operator1.equals("<="), value2.toString(), operator2.equals(">=")).forEach(
                    (key, mapValue) -> {
                        result.addAll(mapValue);
                    });
        }
        return result;
    }
}
