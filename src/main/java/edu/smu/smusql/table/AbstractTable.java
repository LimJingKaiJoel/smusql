package edu.smu.smusql.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.smu.smusql.Column;
import edu.smu.smusql.Row;

public abstract class AbstractTable {
    public Collection<Row> rows;
    public Column[] columns;
    public String tableName;

    public AbstractTable(String name, List<String> columns) {
        this.tableName = name;
        this.columns = columns.stream()
                .map(Column::new)
                .toArray(Column[]::new);
        this.rows = new ArrayList<>();
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }

    public Collection<Row> getRows() {
        return rows;
    }

    public void setRows(Collection<Row> rows) {
        this.rows = rows;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void insert(Object[] values) {
        rows.add(new Row(columns.length, values));
    }

    public String select(Column[] selectedColumns, List<String> conditions) {
        StringBuilder result = new StringBuilder();

        // Add header
        for (Column column : selectedColumns) {
            result.append(column.getName()).append("\t");
        }
        result.append("\n");

        // Add rows
        for (Row row : this.rows) {
            if (matchesConditions(row, conditions)) {
                for (Column column : selectedColumns) {
                    int index = Arrays.asList(columns).indexOf(column);
                    String value = (index != -1 && index < row.getDataRow().length) ? (String) row.getDataRow()[index]
                            : "NULL";
                    result.append(value).append("\t");
                }
                result.append("\n");
            }
        }

        return result.toString();
    }

    public int update(Map<String, Object> updates, List<String> conditions) {
        int updatedRows = 0;
        for (Row row : this.rows) {
            if (matchesConditions(row, conditions)) {
                for (Map.Entry<String, Object> entry : updates.entrySet()) {
                    int columnIndex = -1;
                    for (int i = 0; i < columns.length; i++) {
                        if (columns[i].getName().equals(entry.getKey())) {
                            columnIndex = i;
                            break;
                        }
                    }
                    if (columnIndex != -1) {
                        row.getDataRow()[columnIndex] = entry.getValue();
                    }
                }
                updatedRows++;
            }
        }
        return updatedRows;
    }

    private boolean matchesConditions(Row row, List<String> conditions) {
        if (conditions.isEmpty()) {
            return true;
        }

        Stack<Boolean> stack = new Stack<>();
        int i = 0;
        while (i < conditions.size()) {
            if (conditions.get(i).equalsIgnoreCase("AND") || conditions.get(i).equalsIgnoreCase("OR")) {
                boolean right = stack.pop();
                boolean left = stack.pop();
                boolean result = evaluateOperation(left, conditions.get(i), right);
                stack.push(result);
                i += 1;
            } else if (i + 2 < conditions.size()) {
                String columnName = conditions.get(i);
                String operator = conditions.get(i + 2);
                String value = conditions.get(i + 1);

                boolean result = evaluateSingleCondition(row, columnName, operator, value);
                stack.push(result);
                i += 3;
            }
        }
        return stack.pop();
    }

    private boolean evaluateOperation(boolean left, String operator, boolean right) {
        if (operator.equalsIgnoreCase("AND")) {
            return left && right;
        } else if (operator.equalsIgnoreCase("OR")) {
            return left || right;
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }

    private boolean evaluateSingleCondition(Row row, String columnName, String operator, String value) {
        int columnIndex = -1;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].getName().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Unknown column: " + columnName);
        }

        String rowValue = (String) row.getDataRow()[columnIndex];
        return compareValues(rowValue, operator, value);
    }

    private boolean compareValues(String rowValue, String operator, String value) {
        switch (operator) {
            case "=":
                return rowValue.equals(value);
            case "!=":
                return !rowValue.equals(value);
            case ">":
                return rowValue.compareTo(value) > 0;
            case "<":
                return rowValue.compareTo(value) < 0;
            case ">=":
                return rowValue.compareTo(value) >= 0;
            case "<=":
                return rowValue.compareTo(value) <= 0;
            default:
                return false;
        }
    }

    public int delete(List<String> conditions) {
        int initialSize = rows.size();
        rows.removeIf(row -> matchesConditions(row, conditions));
        return initialSize - rows.size();
    }
}
