package edu.smu.smusql.column;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.smu.smusql.Row;

public class HashMapNumericColumn extends AbstractColumn {
    HashMap<Double, List<Row>> values;
    public HashMapNumericColumn(String name) {
        super(name);
        initValues();
    }

    public void initValues() {
        this.values = new HashMap<>();
    }

    public void insertRow(Double columnValue, Row row) {
        List<Row> rows = this.getValues().get(columnValue);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        rows.add(row);
        this.getValues().put(columnValue, rows);
    }

    public List<Row> getRows(String operator, Object val) {
        String value = (String) val;
        List<Row> result = new ArrayList<>();

        if (operator.equals("=")) {
            if (this.values.get(value) == null) {
                return result;
            }
            result.addAll(this.values.get(value));
        } else {
            for (Double key : this.values.keySet()) {
                if (this.evaluateCondition(key, operator, value)) {
                    result.addAll(this.values.get(key));
                }
            }
        }
        return result;
    }

    public HashMap<Double, List<Row>> getValues() {
        return values;
    }

    public void setValues(HashMap<Double, List<Row>> values) {
        this.values = values;
    }
}
