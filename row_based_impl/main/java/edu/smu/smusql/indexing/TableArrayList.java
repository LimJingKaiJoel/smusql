package edu.smu.smusql.indexing;

import java.util.*;

import edu.smu.smusql.*;

public class TableArrayList extends AbstractTable {
    private Map<String, List<Index>> indexes;

    // create a table with the fixed columns and empty arraylist of rows 
    public TableArrayList(String tableName, String[] colNames) {
        super(tableName);
        Column[] cols = new Column[colNames.length];
        columnNoMap = new HashMap<>();

        for (int i = 0; i < cols.length; i++) {
            cols[i] = new Column(colNames[i]);
            columnNoMap.put(colNames[i], i);
        }

        super.setColumns(cols);
        super.setRows(new ArrayList<Row>());
        indexes = new HashMap<>();
    }

    public void createIndex(String columnName, String indexType) {
        if (!columnNoMap.containsKey(columnName)) {
            throw new IllegalArgumentException("Column " + columnName + " does not exist.");
        }
        int colIndex = columnNoMap.get(columnName);
        Index index;
        if (indexType.equalsIgnoreCase("equality")) {
            index = new EqualityIndex(columnName, colIndex);
        } else if (indexType.equalsIgnoreCase("range")) {
            index = new RangeIndex(columnName, colIndex);
        } else {
            throw new IllegalArgumentException("Unknown index type: " + indexType);
        }

        // Build the index
        for (Row row : this.rows) {
            index.addRow(row);
        }

        indexes.computeIfAbsent(columnName, k -> new ArrayList<>()).add(index);
    }

    public void insert(String[] values) {
        // INSERT INTO student VALUES (1, John, 30, 2.4, False)
        if (values.length != super.columns.length) throw new IllegalArgumentException("Invalid number of input values entered: Expected " + super.columns.length + " but got " + values.length);
        Row row = new Row(values.length);
        Object[] rowData = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            char type = columns[i].getType();
            if (type == 'b') { // boolean
                rowData[i] = Boolean.parseBoolean(values[i]);
            } else if (type == 'i') { // integer
                rowData[i] = Integer.parseInt(values[i]);
            } else if (type == 'd') { // double
                rowData[i] = Double.parseDouble(values[i]);
            } else { // string 
                rowData[i] = values[i];
            }
        }
        row.setDataRow(rowData);
        super.addRow(row);

        // Update indexes
        for (List<Index> indexList : indexes.values()) {
            for (Index index : indexList) {
                index.addRow(row);
            }
        }
    }

    public int update(Map<String, Object> updateMap, List<String> conditions) {
        List<Row> rows = where(conditions);

        Map<Integer, Object> columnNoToUpdate = new HashMap<>();

        Set<String> columnNames = updateMap.keySet();
        for (String columnName : columnNames) {
            if (columnNoMap.get(columnName) != null) {
                columnNoToUpdate.put(columnNoMap.get(columnName), updateMap.get(columnName));
            } else {
                System.out.println(columnName + " does not exist in the table.");
                return 0;
            }
        }

        for (Row row : rows) {
            Object[] rowData = row.getDataRow();
            Map<String, Object> oldValues = new HashMap<>();
            // Store old values for indexed columns
            for (String idxColumnName : indexes.keySet()) {
                if (columnNames.contains(idxColumnName)) {
                    int colIndex = columnNoMap.get(idxColumnName);
                    oldValues.put(idxColumnName, rowData[colIndex]);
                }
            }
            // Update row data
            for (Integer colNo : columnNoToUpdate.keySet()) {
                rowData[colNo] = columnNoToUpdate.get(colNo);
            }
            row.setDataRow(rowData);
            // Update indexes
            for (String idxColumnName : oldValues.keySet()) {
                List<Index> indexList = indexes.get(idxColumnName);
                int colIndex = columnNoMap.get(idxColumnName);
                Object oldValue = oldValues.get(idxColumnName);
                Object newValue = rowData[colIndex];
                for (Index index : indexList) {
                    index.removeRow(row); // Remove using old value
                    index.addRow(row);    // Add using new value
                }
            }
        }
        return rows.size();
    }

    public int delete(List<String> conditions) {
        List<Row> rows = where(conditions);

        for (Row row : rows) {
            super.removeRow(row);
            // Remove from indexes
            for (List<Index> indexList : indexes.values()) {
                for (Index index : indexList) {
                    index.removeRow(row);
                }
            }
        }
        return rows.size();
    }


    public String select(Column[] cols, List<String> conditions) { // idk the format passed into this method 
        /*
        • Example: SELECT * FROM student
        • Example: SELECT * FROM student WHERE gpa > 3.8
        • Example: SELECT * FROM student WHERE gpa > 3.8 AND age < 20
        • Example: SELECT * FROM student WHERE gpa > 3.8 OR age < 20
         */
        // List<Row> result = new ArrayList<>(); 
        StringBuilder result = new StringBuilder();
        List<Row> selectedRows;
        if (conditions.size() == 0) {
            selectedRows = new ArrayList<>(this.rows);
        } else {
            selectedRows = where(conditions);
        }
        // String[] headers = new String[cols.length];
        Integer[] colIndex = new Integer[cols.length];
        for (int i = 0; i < cols.length; i++) {
            result.append(cols[i].getName() + '\t');
            // headers[i] = cols[i].getName();
            colIndex[i] = columnNoMap.get(cols[i].getName());
        }
        result.append('\n');
        // Row header = new Row(cols.length, headers); 
        // result.add(header);
        for (Row row : selectedRows) {
            // Object[] newRowData = new Object[cols.length];
            for (int j = 0; j < colIndex.length; j++) {
                result.append(row.getDataRow()[colIndex[j]].toString() + '\t');
            }
            // Row r = new Row(cols.length, newRowData);
            // result.add(r);
            result.append('\n');
        }
        return result.toString();
    }

    public List<Row> where(List<String> conditions) {
        Set<Row> candidateRows = null;
        Map<String, Object[]> indexedConditions = extractIndexedConditions(conditions);
        for (Map.Entry<String, Object[]> entry : indexedConditions.entrySet()) {
            String columnName = entry.getKey();
            Object[] condition = entry.getValue();
            String operator = (String) condition[0];
            Object value = condition[1];

            List<Row> indexedRows = null;
            if (indexes.containsKey(columnName)) {
                for (Index index : indexes.get(columnName)) {
                    List<Row> result = index.search(operator, value);
                    if (result != null) {
                        indexedRows = result;
                        break; // Use the first index that supports the operator
                    }
                }
            }

            if (indexedRows != null) {
                if (candidateRows == null) {
                    candidateRows = new HashSet<>(indexedRows);
                } else {
                    candidateRows.retainAll(indexedRows);
                }
            }
        }
        List<Row> result = new ArrayList<>();
        Collection<Row> rowsToCheck;
        if (candidateRows == null) {
            // No indexed columns in conditions, scan all rows
            rowsToCheck = super.getRows();
        } else {
            rowsToCheck = candidateRows;
        }
        for (Row row : rowsToCheck) {
            if (evaluateConditions(conditions, row)) {
                result.add(row);
            }
        }
        return result;
    }

    private Map<String, Object[]> extractIndexedConditions(List<String> postfixTokens) {
        Stack<Object> stack = new Stack<>();
        Map<String, Object[]> indexedConditions = new HashMap<>();

        for (String token : postfixTokens) {
            if (isOperator(token)) {
                if (isComparisonOperator(token)) {
                    Object rightOperand = stack.pop();
                    Object leftOperand = stack.pop();
                    String columnName = null;
                    Object value = null;
                    if (leftOperand instanceof String && columnNoMap.containsKey((String) leftOperand)) {
                        columnName = (String) leftOperand;
                        value = parseLiteral(rightOperand.toString());
                    } else if (rightOperand instanceof String && columnNoMap.containsKey((String) rightOperand)) {
                        columnName = (String) rightOperand;
                        value = parseLiteral(leftOperand.toString());
                        token = reverseOperator(token); // Reverse operator since operands are swapped
                    }
                    if (columnName != null && value != null) {
                        indexedConditions.put(columnName, new Object[]{token, value});
                    }
                    // Push a placeholder
                    stack.push(Boolean.TRUE);
                } else if (isLogicalOperator(token)) {
                    // Pop two operands
                    stack.pop();
                    stack.pop();
                    // Push a placeholder
                    stack.push(Boolean.TRUE);
                }
            } else {
                stack.push(token);
            }
        }
        return indexedConditions;
    }

    private String reverseOperator(String operator) {
        switch (operator) {
            case ">":
                return "<";
            case "<":
                return ">";
            case ">=":
                return "<=";
            case "<=":
                return ">=";
            default:
                return operator; // '=', '!=', etc.
        }
    }

    private boolean evaluateConditions(List<String> conditions, Row row) {
        Stack<Object> stack = new Stack<>();

        for (String token : conditions) {
            if (isOperator(token)) {
                if (isLogicalOperator(token)) {
                    if (stack.size() < 2) throw new IllegalStateException("Not enough operands for logical operator.");

                    boolean right = (boolean) stack.pop();
                    boolean left = (boolean) stack.pop();

                    stack.push(token.equalsIgnoreCase("AND") ? left && right : left || right);
                } else if (isComparisonOperator(token)) {
                    if (stack.size() < 2) throw new IllegalStateException("Not enough operands for comparison operator.");

                    Object rightOperand = stack.pop();
                    Object leftOperand = stack.pop();

                    Object leftValue = getOperandValue(leftOperand, row);
                    Object rightValue = getOperandValue(rightOperand, row);

                    boolean result = compareValues(leftValue, rightValue, token);
                    stack.push(result);
                }
            } else {
                // Push column name or literal directly onto the stack
                Object value = parseLiteral(token);
                stack.push(value);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("Invalid condition expression");
        }
        return (boolean) stack.pop();
    }


    private Object parseLiteral(String literal) {
        // boolean
        if (literal.equalsIgnoreCase("true")) {
            return true;
        }
        if (literal.equalsIgnoreCase("false")) {
            return false;
        }
    
        // number
        try {
            if (literal.contains(".")) {
                return Double.parseDouble(literal);
            } else {
                return Integer.parseInt(literal);
            }
        } catch (NumberFormatException e) {
            // string (strings are always enclosed in single quotes)
            return literal.replace("'", "");
        }
    }
    
    private boolean isOperator(String token) {
        return isLogicalOperator(token) || isComparisonOperator(token);
    }
    
    private boolean isLogicalOperator(String token) {
        return token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR");
    }
    
    private boolean isComparisonOperator(String token) {
        return token.equals("=") || token.equals("!=") || token.equals(">") || token.equals("<")
                || token.equals(">=") || token.equals("<=");
    }
    
    private Object getOperandValue(Object operand, Row row) {
        String operandStr = operand.toString();
    
        // If the operand is a column name, fetch the value from the row
        if (columnNoMap.containsKey(operandStr)) {
            int colIndex = columnNoMap.get(operandStr);
            return row.getDataRow()[colIndex];
        } else {
            // If it's a literal value, return it as-is or parse it
            return parseLiteral(operandStr);
        }
    }
    
    private boolean compareValues(Object leftValue, Object rightValue, String operator) {
        if (leftValue == null || rightValue == null) {
            return false;
        }
        if (leftValue instanceof Number && rightValue instanceof Number) {
            double leftNum = ((Number) leftValue).doubleValue();
            double rightNum = ((Number) rightValue).doubleValue();
            switch (operator) {
                case "=":
                    return leftNum == rightNum;
                case "!=":
                    return leftNum != rightNum;
                case ">":
                    return leftNum > rightNum;
                case "<":
                    return leftNum < rightNum;
                case ">=":
                    return leftNum >= rightNum;
                case "<=":
                    return leftNum <= rightNum;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        } else if (leftValue instanceof Boolean && rightValue instanceof Boolean) {
            boolean leftBool = (Boolean) leftValue;
            boolean rightBool = (Boolean) rightValue;
            switch (operator) {
                case "=":
                    return leftBool == rightBool;
                case "!=":
                    return leftBool != rightBool;
                default:
                    throw new IllegalArgumentException("Operator " + operator + " not supported for Booleans");
            }
        } else {
            String leftStr = leftValue.toString();
            String rightStr = rightValue.toString();
            switch (operator) {
                case "=":
                    return leftStr.equals(rightStr);
                case "!=":
                    return !leftStr.equals(rightStr);
                case ">":
                    return leftStr.compareTo(rightStr) > 0;
                case "<":
                    return leftStr.compareTo(rightStr) < 0;
                case ">=":
                    return leftStr.compareTo(rightStr) >= 0;
                case "<=":
                    return leftStr.compareTo(rightStr) <= 0;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        }
    }

    private List<Row> linearSearch(Collection <Row> rows, List<String> conditions) {
        List<Row> result = new ArrayList<>();
        for (Row row : rows) {
            if (evaluateConditions(conditions, row)) {
                result.add(row);
            }
        }
        return result;
    }

}
