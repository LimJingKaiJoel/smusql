package edu.smu.smusql;

import java.util.*;
import java.util.regex.*;

// IMPORTANT: the package here determines which table we use 
import edu.smu.smusql.noindex.TableArrayList;
import edu.smu.smusql.column.AbstractColumn;
import edu.smu.smusql.column.HashMapColumn;
import edu.smu.smusql.noindex.AbstractTable;
import edu.smu.smusql.utils.Condition;
import edu.smu.smusql.utils.Helper;
import edu.smu.smusql.utils.WhereCondition;

public class Engine {
    private Map<String, AbstractTable> database = new HashMap<>();
    private static final Pattern CREATE_PATTERN = Pattern.compile("(?i)CREATE\\s+TABLE\\s+(\\w+)\\s*\\((.+)\\)");
    private static final Pattern INSERT_PATTERN = Pattern
            .compile("(?i)INSERT\\s+INTO\\s+(\\w+)\\s+VALUES\\s*\\((.+)\\)");
    private static final Pattern SELECT_PATTERN = Pattern
            .compile("(?i)SELECT\\s+(.+?)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?");
    private static final Pattern UPDATE_PATTERN = Pattern
            .compile("(?i)UPDATE\\s+(\\w+)\\s+SET\\s+(.+?)(?:\\s+WHERE\\s+(.+))?$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("(?i)DELETE\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?");

    public String executeSQL(String query) {
        if (CREATE_PATTERN.matcher(query).matches()) {
            return create(query);
        } else if (INSERT_PATTERN.matcher(query).matches()) {
            return insert(query);
        } else if (SELECT_PATTERN.matcher(query).matches()) {
            return select(query);
        } else if (UPDATE_PATTERN.matcher(query).matches()) {
            return update(query);
        } else if (DELETE_PATTERN.matcher(query).matches()) {
            return delete(query);
        } else {
            return "ERROR: Unknown command";
        }
    }

    private AbstractTable getTable(String tableName) {
        AbstractTable table = database.get(tableName);
        return table;
    }

    protected AbstractTable createTable(String name, String[] columns) {
        // TODO: CHANGE THIS TABLE AS NEEDED
        return new TableArrayList(name, columns);
    }

    public String create(String query) {
        Matcher matcher = CREATE_PATTERN.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String[] columns = Helper.trimQuotes(matcher.group(2).split("\\s*,\\s*"));
            database.put(tableName, createTable(tableName, columns));
            return "Table " + tableName + " created with columns: " + String.join(", ", columns);
        }
        return "ERROR: Invalid CREATE statement";
    }

    public String insert(String query) {
        Matcher matcher = INSERT_PATTERN.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String[] values = Helper.trimQuotes(matcher.group(2).split("\\s*,\\s*"));
            AbstractTable table = getTable(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }
            table.insert(values);
            return "1 row inserted into " + tableName;
        }
        return "ERROR: Invalid INSERT statement";
    }

    public String select(String query) {
        Matcher matcher = SELECT_PATTERN.matcher(query);
        if (matcher.find()) {
            String columns = matcher.group(1);
            String tableName = matcher.group(2);
            String whereClause = matcher.group(3);

            AbstractTable table = getTable(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }
            AbstractColumn[] selectedColumns = columns.equals("*") ? table.getColumns()
                    : Arrays.stream(columns.split("\\s*,\\s*"))
                            .map(HashMapColumn::new)
                            .toArray(AbstractColumn[]::new);

            WhereCondition conditions = parseWhereConditions(whereClause);
            // List<Row> result = table.select(selectedColumns, conditions);
            return table.select(selectedColumns, conditions);
            // return formatResult(result, selectedColumns);
        }
        return "ERROR: Invalid SELECT statement";
    }

    public String update(String query) {
        Matcher matcher = UPDATE_PATTERN.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String updates = matcher.group(2);
            String whereClause = matcher.group(3);

            AbstractTable table = getTable(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }
            Map<String, String> updateMap = new HashMap<>();
            for (String update : updates.split("\\s*,\\s*")) {
                String[] parts = update.split("\\s*=\\s*");
                updateMap.put(parts[0], Helper.trimQuotes(parts[1]));
            }

            WhereCondition conditions = parseWhereConditions(whereClause);
            int updatedRows = table.update(updateMap, conditions);
            return updatedRows + " row(s) updated in " + tableName;
        }
        return "ERROR: Invalid UPDATE statement";
    }

    public String delete(String query) {
        Matcher matcher = DELETE_PATTERN.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String whereClause = matcher.group(2);

            AbstractTable table = getTable(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }
            WhereCondition conditions = parseWhereConditions(whereClause);
            int deletedRows = table.delete(conditions);
            return deletedRows + " row(s) deleted from " + tableName;
        }
        return "ERROR: Invalid DELETE statement";
    }

    public WhereCondition parseWhereConditions(String whereClause) {
        if (whereClause == null || whereClause.trim().isEmpty()) {
            return null;
        }
        return tokenizeWhereClause(whereClause);
    }

    private WhereCondition tokenizeWhereClause(String whereClause) {
        WhereCondition result = new WhereCondition(null, null, null);
        String[] parts = whereClause.split("\\s+(AND|OR)\\s+");

        // Parse first condition
        String firstCondition = parts[0].trim();
        Condition condition1 = parseCondition(firstCondition);

        result.setCondition1(condition1);

        // If there's a second condition
        if (parts.length > 1) {
            String secondCondition = parts[1].trim();
            Condition condition2 = parseCondition(secondCondition);
            result.setCondition2(condition2);

            // Find the operator (AND/OR)
            Matcher matcher = Pattern.compile("\\s+(AND|OR)\\s+").matcher(whereClause);
            if (matcher.find()) {
                result.setOperator(matcher.group(1));
            }
        }

        return result;
    }

    private Condition parseCondition(String condition) {
        Condition result = null;

        // Match patterns like: column operator value
        // Operators can be =, >, <, >=, <=, <>
        Matcher condMatcher = Pattern.compile("(\\w+)\\s*([=<>]+|<=|>=|<>)\\s*(.+)").matcher(condition);

        if (condMatcher.find()) {
            String column = condMatcher.group(1);
            String operator = condMatcher.group(2);
            String value = Helper.trimQuotes(condMatcher.group(3));
            result = new Condition(column, operator, value);
        }

        return result;
    }
}
