package edu.smu.smusql;

import java.util.*;

public class Engine {
    private Map<String, Table> database = new HashMap<>();

    public String executeSQL(String query) {
        String[] tokens = query.trim().split("\\s+");
        String command = tokens[0].toUpperCase();

        switch (command) {
            case "CREATE":
                return create(tokens);
            case "INSERT":
                return insert(tokens);
            case "SELECT":
                return select(tokens);
            case "UPDATE":
                return update(tokens);
            case "DELETE":
                return delete(tokens);
            default:
                return "ERROR: Unknown command";
        }
    }

    public String create(String[] tokens) {
        if (tokens.length < 4 || !tokens[1].equalsIgnoreCase("TABLE")) {
            return "ERROR: Invalid CREATE statement";
        }
        String tableName = tokens[2];
        List<String> columns = new ArrayList<>();

        if (!tokens[3].substring(0, 1).equals("(") || !tokens[3].substring(tokens[3].length() - 1).equals(")")) {
            return "ERROR: Invalid CREATE statement";
        }

        String columnNames = tokens[3].substring(1, tokens[3].length() - 1);
        String[] columnNamesArray = columnNames.split(",");
        for (String columnName : columnNamesArray) {
            columnName = columnName.trim();
            columns.add(columnName);
        }
        database.put(tableName, new Table(columns));
        return "Table " + tableName + " created with columns: " + String.join(", ", columns);
    }

    public String insert(String[] tokens) {
        if (tokens.length < 5 || !tokens[1].equalsIgnoreCase("INTO") || !tokens[3].equalsIgnoreCase("VALUES")) {
            return "ERROR: Invalid INSERT statement";
        }
        String tableName = tokens[2];
        Table table = database.get(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }

        if (!tokens[4].substring(0, 1).equals("(") || !tokens[4].substring(tokens[4].length() - 1).equals(")")) {
            return "ERROR: Invalid INSERT statement";
        }

        String values = tokens[4].substring(1, tokens[4].length() - 1);
        String[] valuesArray = values.split(",");
        List<String> valuesList = new ArrayList<>();
        for (String value : valuesArray) {
            value = value.trim();
            valuesList.add(value);
        }
        table.insert(valuesList);
        return "1 row inserted into " + tableName;
    }

    public String select(String[] tokens) {
        if (tokens.length < 4 || !tokens[2].equalsIgnoreCase("FROM")) {
            return "ERROR: Invalid SELECT statement";
        }
        String tableName = tokens[3];

        Table table = database.get(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }
        List<String> columns = new ArrayList<>();
        if (tokens[1].equals("*")) {
            columns = table.getColumns();
        } else {
            for (int i = 1; i < tokens.length && !tokens[i].equalsIgnoreCase("FROM"); i++) {
                columns.add(tokens[i]);
            }
        }
        return table.select(columns);
    }

    public String update(String[] tokens) {
        if (tokens.length < 6 || !tokens[2].equalsIgnoreCase("SET")) {
            return "ERROR: Invalid UPDATE statement";
        }
        String tableName = tokens[1];
        Table table = database.get(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }

        Map<String, String> updates = new HashMap<>();
        List<String> conditions = new ArrayList<>();
        boolean inWhere = false;

        for (int i = 3; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("WHERE")) {
                inWhere = true;
                continue;
            }
            if (!inWhere) {
                if (i + 2 < tokens.length && tokens[i + 1].equals("=")) {
                    System.out.println(tokens[i] + " " + tokens[i + 2]);
                    updates.put(tokens[i], tokens[i + 2]);
                    i += 2;
                }
            } else {
                conditions.add(tokens[i]);
            }
        }

        System.out.println(updates);
        System.out.println(conditions);

        int updatedRows = table.update(updates, conditions);
        return updatedRows + " row(s) updated in " + tableName;
    }

    public String delete(String[] tokens) {
        // TODO: Handle properly
        if (tokens.length < 3 || !tokens[1].equalsIgnoreCase("FROM")) {
            return "ERROR: Invalid DELETE statement";
        }
        String tableName = tokens[2];
        Table table = database.get(tableName);
        if (table == null) {
            return "ERROR: Table " + tableName + " does not exist";
        }
        int deletedRows = table.delete();
        return deletedRows + " row(s) deleted from " + tableName;
    }

    private static class Table {
        private List<String> columns;
        private List<List<String>> rows;

        public Table(List<String> columns) {
            this.columns = columns;
            this.rows = new ArrayList<>();
        }

        public void insert(List<String> values) {
            rows.add(values);
        }

        public String select(List<String> selectedColumns) {
            if (rows.isEmpty()) {
                return "No data available.\n";
            }

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
            // Simple condition parsing (assumes AND logic)
            for (int i = 0; i < conditions.size(); i += 3) {
                String column = conditions.get(i);
                String operator = conditions.get(i + 1);
                String value = conditions.get(i + 2);
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
                default:
                    return false;
            }
        }

        public int delete() {
            int deletedRows = rows.size();
            rows.clear();
            return deletedRows;
        }

        public List<String> getColumns() {
            return columns;
        }
    }
}
