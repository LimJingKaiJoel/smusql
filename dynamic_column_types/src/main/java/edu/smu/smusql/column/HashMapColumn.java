package edu.smu.smusql.column;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.smu.smusql.Row;

public class HashMapColumn extends AbstractColumn {
    HashMap<String, List<Row>> values;
    public HashMapColumn(String name) {
        super(name);
        // initValues();
    }

    public void initValues() {
        this.values = new HashMap<>();
    }

    public void insertRow(String columnValue, Row row) {
        if (this.values == null) {
            this.initValues();
        }
        List<Row> rows = this.getValues().get(columnValue);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        rows.add(row);
        this.getValues().put(columnValue, rows);
    }

    public List<Row> getRows(String operator, Object val) {
        if (this.values == null) {
            return new ArrayList<>();
        }
        String value = (String) val;
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

    public HashMap<String, List<Row>> getValues() {
        if (this.values == null) {
            this.initValues();
        }
        return values;
    }

    public void setValues(HashMap<String, List<Row>> values) {
        this.values = values;
    }
}
