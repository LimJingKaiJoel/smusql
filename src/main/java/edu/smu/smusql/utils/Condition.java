package edu.smu.smusql.utils;

public class Condition {
    private Object columnName;
    private String operator;
    private Object value;

    public Condition(Object columnName, String operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    public Object getColumnName() {
        return columnName;
    }

    public void setColumnName(Object columnName) {
        this.columnName = columnName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
        }

    public void setValue(Object value) {
        this.value = value;
    }
}
