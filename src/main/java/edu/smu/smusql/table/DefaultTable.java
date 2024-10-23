package edu.smu.smusql.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTable {
    private List<String> columns;
    private List<List<String>> rows;

    public DefaultTable(List<String> columns) {
        this.columns = columns;
        this.rows = new ArrayList<>();
    }

    public void insert(List<String> values) {
        rows.add(values);
    }

    public String select(List<String> selectedColumns, String conditions) {
        // Calculate column widths
        Map<String, Integer> columnWidths = new HashMap<>();
        for (String column : selectedColumns) {
            columnWidths.put(column, column.length());
        }

        for (List<String> row : rows) {
            for (String column : selectedColumns) {
                int index = columns.indexOf(column);
                if (index != -1 && index < row.size()) {
                    String value = row.get(index);
                    columnWidths.put(column, Math.max(columnWidths.get(column), value.length()));
                }
            }
        }

        StringBuilder result = new StringBuilder();

        // Print header
        result.append("+");
        for (String column : selectedColumns) {
            result.append("-".repeat(columnWidths.get(column) + 2)).append("+");
        }
        result.append("\n");

        result.append("|");
        for (String column : selectedColumns) {
            result.append(" ").append(String.format("%-" + columnWidths.get(column) + "s", column)).append(" |");
        }
        result.append("\n");

        result.append("+");
        for (String column : selectedColumns) {
            result.append("-".repeat(columnWidths.get(column) + 2)).append("+");
        }
        result.append("\n");

        // Print rows
        for (List<String> row : rows) {
            result.append("|");
            for (String column : selectedColumns) {
                int index = columns.indexOf(column);
                String value = (index != -1 && index < row.size()) ? row.get(index) : "NULL";
                result.append(" ").append(String.format("%-" + columnWidths.get(column) + "s", value)).append(" |");
            }
            result.append("\n");
        }

        // Print footer
        result.append("+");
        for (String column : selectedColumns) {
            result.append("-".repeat(columnWidths.get(column) + 2)).append("+");
        }
        result.append("\n");

        return result.toString();
    }

    public int update(Map<String, String> updates, List<String> conditions) {
        int updatedRows = 0;
        for (List<String> row : rows) {
            if (matchesConditions(row, conditions)) {
                for (Map.Entry<String, String> entry : updates.entrySet()) {
                    int columnIndex = columns.indexOf(entry.getKey());
                    if (columnIndex != -1) {
                        row.set(columnIndex, entry.getValue());
                    }
                }
                updatedRows++;
            }
        }
        return updatedRows;
    }

    private boolean matchesConditions(List<String> row, List<String> conditions) {
        if (conditions.isEmpty()) {
            return true;
        }

        // Split conditions into AND groups
        List<List<String>> andGroups = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();
        for (String condition : conditions) {
            if (condition.equalsIgnoreCase("OR")) {
                if (!currentGroup.isEmpty()) {
                    andGroups.add(currentGroup);
                    currentGroup = new ArrayList<>();
                }
            } else {
                currentGroup.add(condition);
            }
        }
        if (!currentGroup.isEmpty()) {
            andGroups.add(currentGroup);
        }

        // Evaluate OR groups
        for (List<String> andGroup : andGroups) {
            if (evaluateAndGroup(row, andGroup)) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluateAndGroup(List<String> row, List<String> andGroup) {
        for (int i = 0; i < andGroup.size(); i += 3) {
            String column = andGroup.get(i);
            String operator = andGroup.get(i + 1);
            String value = andGroup.get(i + 2);
            int columnIndex = columns.indexOf(column);
            if (columnIndex != -1) {
                String rowValue = row.get(columnIndex);
                if (!compareValues(rowValue, operator, value)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compareValues(String rowValue, String operator, String value) {
        switch (operator) {
            case "=":
                return rowValue.equals(value);
            case "!=":
                return !rowValue.equals(value);
            // Add more operators as needed
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

    public List<String> getColumns() {
        return columns;
    }
}
