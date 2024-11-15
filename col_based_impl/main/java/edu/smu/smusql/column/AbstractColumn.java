package edu.smu.smusql.column;

import java.util.AbstractMap;

import edu.smu.smusql.Row;

import java.util.List;

public abstract class AbstractColumn {
    protected String name;
    protected char type;
    AbstractMap<String, List<Row>> values;

    public AbstractColumn(String name) {
        this.name = name;
        this.type = 's';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public abstract void initValues();

    public AbstractMap<String, List<Row>> getValues() {
        return values;
    }

    public void setValues(AbstractMap<String, List<Row>> values) {
        this.values = values;
    }

    public abstract void insertRow(String columnValue, Row row);

    public abstract List<Row> getRows(String operator, String value);

    public abstract void removeRow(String columnValue, Row row);

    public List<Row> getRowsRange(String operator1, Object value1, String operator2, Object value2) {
        // Default implementation falls back to doing the intersection manually
        List<Row> rows1 = getRows(operator1, value1.toString());
        List<Row> rows2 = getRows(operator2, value2.toString());
        rows1.retainAll(rows2);
        return rows1;
    }

    protected boolean evaluateCondition(String columnValue, String operator, String value) {
        if (operator.equals("=")) {
            return columnValue.equals(value);
        }

        Double columnValueDouble = Double.parseDouble(columnValue);
        Double valueDouble = Double.parseDouble(value);

        switch (operator) {
            case ">":
                return columnValueDouble > valueDouble;
            case ">=":
                return columnValueDouble >= valueDouble;
            case "<":
                return columnValueDouble < valueDouble;
            case "<=":
                return columnValueDouble <= valueDouble;
            case "!=":
                return !columnValue.equals(value);
        }
        return false;
    }
}
