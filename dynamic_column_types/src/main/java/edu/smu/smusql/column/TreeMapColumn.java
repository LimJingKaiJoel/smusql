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

    public List<Row> getRowsRange(String operator1, Object value1, String operator2, Object value2) {
        Double value1Double = Double.parseDouble((String) value1);
        Double value2Double = Double.parseDouble((String) value2);

        if ((operator1.equals("<") || operator1.equals("<=")) && operator2.equals(">") || operator2.equals(">=")) {
            // swap the values and operators
            Double temp = value1Double;
            value1Double = value2Double;
            value2Double = temp;
            String tempOperator = operator1;
            operator1 = operator2;
            operator2 = tempOperator;
        }

        List<Row> result = new ArrayList<>();

        if (operator1.equals("=") && operator2.equals("=")) {
            if (!this.values.containsKey(value1Double) || !this.values.containsKey(value2Double)) {
                return result;
            }
            result.addAll(this.values.get(value1Double));
            result.retainAll(this.values.get(value2Double));
        } else {
            this.values.subMap(value1Double, operator1.equals("<="), value2Double, operator2.equals(">=")).forEach(
                    (key, mapValue) -> {
                        result.addAll(mapValue);
                    });
        }
        return result;
    }

}
