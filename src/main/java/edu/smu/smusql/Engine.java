package edu.smu.smusql;

import java.util.*;
import java.util.regex.*;
import edu.smu.smusql.table.AbstractTable;
import edu.smu.smusql.utils.Helper;
import edu.smu.smusql.table.DefaultTable;
// import edu.smu.smusql.table.ParallelStreamTable;

import java.util.Stack;

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

    protected AbstractTable createTable(String name, List<String> columns) {
        // CHANGE THIS TABLE AS NEEDED
        return new DefaultTable(name, columns);
    }

    public String create(String query) {
        Matcher matcher = CREATE_PATTERN.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String[] columns = Helper.trimQuotes(matcher.group(2).split("\\s*,\\s*"));
            database.put(tableName, createTable(tableName, Arrays.asList(columns)));
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
            Column[] selectedColumns = columns.equals("*") ? table.getColumns()
                    : Arrays.stream(columns.split("\\s*,\\s*"))
                            .map(Column::new)
                            .toArray(Column[]::new);

            List<String> conditions = parseWhereConditions(whereClause);
            List<Row> result = table.select(selectedColumns, conditions);

            return formatResult(result, selectedColumns);
        }
        return "ERROR: Invalid SELECT statement";
    }

    public String update(String query) {
        Matcher matcher = UPDATE_PATTERN.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String updates = matcher.group(2);
            String conditions = matcher.group(3);

            AbstractTable table = getTable(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }
            Map<String, Object> updateMap = new HashMap<>();
            for (String update : updates.split("\\s*,\\s*")) {
                String[] parts = update.split("\\s*=\\s*");
                updateMap.put(parts[0], Helper.trimQuotes(parts[1]));
            }

            List<String> conditionsList = parseWhereConditions(conditions);
            int updatedRows = table.update(updateMap, conditionsList);
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
            List<String> conditions = parseWhereConditions(whereClause);
            int deletedRows = table.delete(conditions);
            return deletedRows + " row(s) deleted from " + tableName;
        }
        return "ERROR: Invalid DELETE statement";
    }

    private List<String> parseWhereConditions(String whereClause) {
        if (whereClause == null || whereClause.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return convertToPostfix(tokenizeWhereClause(whereClause));
    }

    private List<String> tokenizeWhereClause(String whereClause) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (char c : whereClause.toCharArray()) {
            if ((c == '\'' || c == '"') && !inQuotes) {
                inQuotes = true;
                // currentToken.append(c);
            } else if ((c == '\'' || c == '"') && inQuotes) {
                inQuotes = false;
                // currentToken.append(c);
            } else if (!inQuotes && (c == '(' || c == ')' || c == ' ')) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                if (c != ' ') {
                    tokens.add(String.valueOf(c));
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private List<String> convertToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isOperand(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop();
                }
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }

    private boolean isOperand(String token) {
        return !isOperator(token) && !token.equals("(") && !token.equals(")");
    }

    private boolean isOperator(String token) {
        return token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR") ||
                token.equals("=") || token.equals(">") || token.equals("<") ||
                token.equals(">=") || token.equals("<=") || token.equals("!=");
    }

    private int precedence(String operator) {
        if (operator.equalsIgnoreCase("AND"))
            return 2;
        if (operator.equalsIgnoreCase("OR"))
            return 1;
        if (operator.equals("=") || operator.equals(">") || operator.equals("<") ||
                operator.equals(">=") || operator.equals("<=") || operator.equals("!="))
            return 3;
        return 0;
    }

    private String formatResult(List<Row> result, Column[] selectedColumns) {
        StringBuilder resultString = new StringBuilder();

        for (Column column : selectedColumns) {
            resultString.append(column.getName()).append("\t");
        }
        resultString.append("\n");

        for (Row row : result) {
            for (Object value : row.getDataRow()) {
                resultString.append(value).append("\t");
            }
            resultString.append("\n");
        }
        return resultString.toString();
    }
}
