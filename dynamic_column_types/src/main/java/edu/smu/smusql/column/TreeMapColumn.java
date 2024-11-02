package edu.smu.smusql.column;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.smu.smusql.Row;

public class TreeMapColumn extends AbstractColumn {
    private TreeMap<Double, List<Row>> values;

    public TreeMapColumn(String name) {
        super(name);
        initValues();
    }

    public TreeMap<Double, List<Row>> getValues() {
        return values;
    }

    public void setValues(TreeMap<Double, List<Row>> values) {
        this.values = values;
    }

    public void initValues() {
        this.values = new TreeMap<>();
    }

    public void insertRow(Double columnValue, Row row) {
        List<Row> rows = this.values.get(columnValue);
        if (rows == null) {
            rows = new ArrayList<>();
        }
        rows.add(row);
        this.values.put(columnValue, rows);
    }

    public List<Row> getRows(String operator, Object val) {
        try {
            Double value = Double.parseDouble((String) val);
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
        } catch (NumberFormatException ex) {
            System.out.println("Where condition is expecting a numeric");
            return new ArrayList<>();
        }
        
    }

}
