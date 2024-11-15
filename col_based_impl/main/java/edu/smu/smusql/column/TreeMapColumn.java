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

    public List<Row> getRowsRange(String operator1, Object value1, String operator2, Object value2) {
        String value1String = value1.toString();
        String value2String = value2.toString();

        if ((operator1.equals("<") || operator1.equals("<=")) && operator2.equals(">") || operator2.equals(">=")) {
            // swap the values and operators
            String temp = value1String;
            value1String = value2String;
            value2String = temp;
            String tempOperator = operator1;
            operator1 = operator2;
            operator2 = tempOperator;
        }

        List<Row> result = new ArrayList<>();

        if (operator1.equals("=") && operator2.equals("=")) {
            if (!this.values.containsKey(value1String) || !this.values.containsKey(value2String)) {
                return result;
            }
            result.addAll(this.values.get(value1String));
            result.retainAll(this.values.get(value2String));
        } else {
            this.values.subMap(value1String, operator1.equals("<="), value2String, operator2.equals(">=")).forEach(
                    (key, mapValue) -> {
                        result.addAll(mapValue);
                    });
        }
        return result;
    }
}
