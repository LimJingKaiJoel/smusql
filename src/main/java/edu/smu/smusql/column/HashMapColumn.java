package edu.smu.smusql.column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.smu.smusql.Row;

public class HashMapColumn extends AbstractColumn {
    public HashMapColumn(String name) {
        super(name);
        initValues();
    }

    @Override
    public void initValues() {
        this.values = new HashMap<>();
    }

    @Override
    public void insertRow(String columnValue, Row row) {
        List<Row> rows = this.getValues().get(columnValue);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        rows.add(row);
        this.getValues().put(columnValue, rows);
    };

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
            if (this.values.get(value) == null) {
                return result;
            }
            result.addAll(this.values.get(value));
        } else {
            for (String key : this.values.keySet()) {
                if (this.evaluateCondition(key, operator, value)) {
                    result.addAll(this.values.get(key));
                }
            }
        }

        return result;
    }
}
