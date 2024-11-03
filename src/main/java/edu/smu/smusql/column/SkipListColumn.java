package edu.smu.smusql.column;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import edu.smu.smusql.Row;

public class SkipListColumn extends AbstractColumn {
    private ConcurrentSkipListMap<String, List<Row>> values;
    
    public SkipListColumn(String name) {
        super(name);
        initValues();
    }

    @Override
    public void initValues() {
        this.values = new ConcurrentSkipListMap<>();
    }

    @Override
    public void insertRow(String columnValue, Row row) {
        this.values.computeIfAbsent(columnValue, k -> new ArrayList<>()).add(row);
    };

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

}
