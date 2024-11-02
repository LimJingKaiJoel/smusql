package edu.smu.smusql.column;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.smu.smusql.Row;

public class CustomHashMapColumn extends AbstractColumn {
    public CustomHashMapColumn(String name) {
        super(name);
        initValues();
    }

    @Override
    public void initValues() {
        // CustomHashMap with initial capacity, load factor, and resizing factor
        // CHANGE HERE FOR TESTING AROUND
        this.values = new CustomHashMap<String, List<Row>>(16,0.5f, 3.0f);
    }

    @Override
    public void insertRow(String columnValue, Row row) {
        List<Row> rows = this.values.get(columnValue);
        if (rows == null) {
            rows = new ArrayList<>();
            this.values.put(columnValue, rows);
        }
        rows.add(row);
    }

    @Override
    public List<Row> getRows(String operator, String value) {
        List<Row> result = new ArrayList<>();

        if (operator.equals("=")) {
            List<Row> rows = this.values.get(value);
            if (rows != null) {
                result.addAll(rows);
            }
        } else {
            Set<String> keys = this.values.keySet();
            for (String key : keys) {
                if (this.evaluateCondition(key, operator, value)) {
                    result.addAll(this.values.get(key));
                }
            }
        }

        return result;
    }
}