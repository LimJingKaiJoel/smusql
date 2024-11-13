package edu.smu.smusql.column;

import edu.smu.smusql.Row;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class BPlusTreeMapColumn extends AbstractColumn {
    private BPlusTreeMap<String, List<Row>> values;

    public BPlusTreeMapColumn(String name) {
        super(name);
        initValues();
    }

    // Change the order here for testing around - order >= 3
    public BPlusTreeMapColumn(String name, int order) {
        super(name);
        initValues(order);
    }

    @Override
    public void initValues() {
        // Change the order here for testing around - order >= 3
        this.values = new BPlusTreeMap<String, List<Row>>(3);
    }

    public void initValues(int order) {
        this.values = new BPlusTreeMap<String, List<Row>>(order);
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

