package edu.smu.smusql.column;

import java.util.AbstractMap;

import edu.smu.smusql.Row;

import java.util.ArrayList;
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
