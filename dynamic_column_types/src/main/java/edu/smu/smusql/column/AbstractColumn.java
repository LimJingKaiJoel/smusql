package edu.smu.smusql.column;

import java.util.AbstractMap;

import edu.smu.smusql.Row;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractColumn {
    protected String name;
    protected char type;

    public AbstractColumn(String name) {
        this.name = name;
        this.type = '0'; // represent undetermined data type 
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

    // public abstract void initValues();


    // public abstract void insertRow(Object columnValue, Row row);

    public abstract List<Row> getRows(String operator, Object value);

    public List<Row> getRowsRange(String operator1, Object value1, String operator2, Object value2) {
        // Default implementation falls back to doing the intersection manually
        List<Row> rows1 = getRows(operator1, value1);
        List<Row> rows2 = getRows(operator2, value2);
        rows1.retainAll(rows2);
        return rows1;
    }

    protected boolean evaluateCondition(Object columnValue, String operator, Object value) {
        if (operator.equals("=")) {
            return (columnValue).equals(value);
        } else if (operator.equals("!=")) {
            return !(columnValue).equals(value);
        }

        
        try {
            Double columnValueDouble = (Double) columnValue;
            Double valueDouble = Double.parseDouble(value.toString());
            switch (operator) {
                case ">":
                    return columnValueDouble > valueDouble;
                case ">=":
                    return columnValueDouble >= valueDouble;
                case "<":
                    return columnValueDouble < valueDouble;
                case "<=":
                    return columnValueDouble <= valueDouble;
                // case "!=":
                //     return !columnValue.equals(value);
            }
        } catch (Exception e) {
            return false;
        }

        
        return false;
    }
}
