// package edu.smu.smusql.index;

// import java.util.*;

// import edu.smu.smusql.*;

// public class TableArrayList extends AbstractTable {

//     // create a table with the fixed columns and empty arraylist of rows 
//     public TableArrayList(String tableName, String[] colNames) {
//         // CREATE TABLE student (id, name, age, gpa, deans_list)
//         super(tableName);
//         Column[] cols = new Column[colNames.length];
//         columnNoMap = new HashMap<>();

//         for (int i = 0; i < cols.length; i++) {
//             cols[i] = new Column(colNames[i]);
//             columnNoMap.put(colNames[i], i);
//         }

//         super.setColumns(cols);
//         super.setRows(new ArrayList<Row>());
//     }

//     public void insert(String[] values) {
//         // INSERT INTO student VALUES (1, John, 30, 2.4, False)
//         if (values.length != super.columns.length) throw new IllegalArgumentException("Invalid number of input values entered: Expected "+ super.columns.length + " but got " + values.length);
        
//         // need insert into index also
        
//         Row row = new Row(values.length);
//         Object[] rowData = new Object[values.length];
//         for (int i = 0; i < values.length; i++) {
//             char type = columns[i].getType(); 
//             if (type == 'b') { // boolean
//                 rowData[i] = Boolean.parseBoolean(values[i]);
//             } else if (type == 'i') { // integer
//                 rowData[i] = Integer.parseInt(values[i]);
//             } else if (type == 'd') { // double
//                 rowData[i] = Double.parseDouble(values[i]);
//             } else { // string 
//                 rowData[i] = values[i];
//             }
//         }
//         row.setDataRow(rowData);
//         super.addRow(row);
//     }

//     // NOTE: for complex queries, i cant seem to figure out why it doesnt work, it's not evaluating the final condition correctly :( 
//     // will debug when i have time
//     public List<Row> where(List<String> conditions) {
//         for (String condition : conditions) {
//             System.out.print("Condition: " + condition + " |");
//         } 
//         System.out.println();

//         List<Row> result = new ArrayList<>();
//         for (Row row : super.getRows()) {
//             if (evaluateConditions(conditions, row)) {
//                 result.add(row);
//             }
//         }
//         return result;
//     }
    
//     private boolean evaluateConditions(List<String> conditions, Row row) {
//         // List<String> postfixTokens = infixToPostfix(conditions);
//         List<String> postfixTokens = conditions;
//         Stack<Object> stack = new Stack<>();
//         System.out.println(Arrays.toString(row.dataRow));
    
//         for (String token : postfixTokens) {
//             System.out.println("Processing token: " + token);
//             System.out.println("Stack before: " + stack);
    
//             if (isOperator(token)) {
//                 if (isLogicalOperator(token)) {
//                     if (stack.size() < 2) throw new IllegalStateException("Not enough operands for logical operator.");
                    
//                     boolean right = (boolean) stack.pop();
//                     boolean left = (boolean) stack.pop();
                    
//                     stack.push(token.equalsIgnoreCase("AND") ? left && right : left || right);
//                 } else if (isComparisonOperator(token)) {
//                     if (stack.size() < 2) throw new IllegalStateException("Not enough operands for comparison operator.");
    
//                     Object rightOperand = stack.pop();
//                     Object leftOperand = stack.pop();
    
//                     Object leftValue = getOperandValue(leftOperand, row);
//                     Object rightValue = getOperandValue(rightOperand, row);
    
//                     boolean result = compareValues(leftValue, rightValue, token);
//                     stack.push(result);
//                 }
//             } else {
//                 // Push column name or literal directly onto the stack
//                 Object value = parseLiteral(token);
//                 stack.push(value);
//             }
    
//             System.out.println("Stack after: " + stack);
//         }
    
//         if (stack.size() != 1) {
//             throw new IllegalStateException("Invalid condition expression");
//         }
//         Object element = stack.pop();
//         System.out.println(element.toString());
//         return (boolean) element;
//     }
    
//     private Object parseLiteral(String literal) {
//         // boolean
//         if (literal.equalsIgnoreCase("true")) {
//             return true;
//         }
//         if (literal.equalsIgnoreCase("false")) {
//             return false;
//         }
    
//         // number
//         try {
//             if (literal.contains(".")) {
//                 return Double.parseDouble(literal);
//             } else {
//                 return Integer.parseInt(literal);
//             }
//         } catch (NumberFormatException e) {
//             // string (strings are always enclosed in single quotes)
//             return literal.replace("'", "");
//         }
//     }
    
//     private boolean isOperator(String token) {
//         return isLogicalOperator(token) || isComparisonOperator(token);
//     }
    
//     private boolean isLogicalOperator(String token) {
//         return token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR");
//     }
    
//     private boolean isComparisonOperator(String token) {
//         return token.equals("=") || token.equals("!=") || token.equals(">") || token.equals("<")
//                 || token.equals(">=") || token.equals("<=");
//     }
    
//     private Object getOperandValue(Object operand, Row row) {
//         String operandStr = operand.toString();
    
//         // If the operand is a column name, fetch the value from the row
//         if (columnNoMap.containsKey(operandStr)) {
//             int colIndex = columnNoMap.get(operandStr);
//             return row.getDataRow()[colIndex];
//         } else {
//             // If it's a literal value, return it as-is or parse it
//             return parseLiteral(operandStr);
//         }
//     }
    
//     private boolean compareValues(Object leftValue, Object rightValue, String operator) {
//         if (leftValue == null || rightValue == null) {
//             return false;
//         }
//         if (leftValue instanceof Number && rightValue instanceof Number) {
//             double leftNum = ((Number) leftValue).doubleValue();
//             double rightNum = ((Number) rightValue).doubleValue();
//             switch (operator) {
//                 case "=":
//                     return leftNum == rightNum;
//                 case "!=":
//                     return leftNum != rightNum;
//                 case ">":
//                     return leftNum > rightNum;
//                 case "<":
//                     return leftNum < rightNum;
//                 case ">=":
//                     return leftNum >= rightNum;
//                 case "<=":
//                     return leftNum <= rightNum;
//                 default:
//                     throw new IllegalArgumentException("Unknown operator: " + operator);
//             }
//         } else if (leftValue instanceof Boolean && rightValue instanceof Boolean) {
//             boolean leftBool = (Boolean) leftValue;
//             boolean rightBool = (Boolean) rightValue;
//             switch (operator) {
//                 case "=":
//                     return leftBool == rightBool;
//                 case "!=":
//                     return leftBool != rightBool;
//                 default:
//                     throw new IllegalArgumentException("Operator " + operator + " not supported for Booleans");
//             }
//         } else {
//             String leftStr = leftValue.toString();
//             String rightStr = rightValue.toString();
//             switch (operator) {
//                 case "=":
//                     return leftStr.equals(rightStr);
//                 case "!=":
//                     return !leftStr.equals(rightStr);
//                 case ">":
//                     return leftStr.compareTo(rightStr) > 0;
//                 case "<":
//                     return leftStr.compareTo(rightStr) < 0;
//                 case ">=":
//                     return leftStr.compareTo(rightStr) >= 0;
//                 case "<=":
//                     return leftStr.compareTo(rightStr) <= 0;
//                 default:
//                     throw new IllegalArgumentException("Unknown operator: " + operator);
//             }
//         }
//     }

//     public int update(Map<String, Object> updateMap, List<String> conditions) { // idk the format passed into this method 
//         /*
//          • Example: UPDATE student SET age = 25 WHERE id = 1
//          • Example: UPDATE student SET deans_list = True WHERE gpa > 3.8 OR age = 201
//         */
//         // currently only assumed 1 col at a time lol

//         // where processing 
//         List<Row> rows = where(conditions);

//         // need update the index also

//         Map<Integer, Object> columnNoToUpdate = new HashMap<>(); // column no, new data

//         Set<String> columnNames = updateMap.keySet(); 
//         for (String columnName : columnNames) {
//             if (columnNoMap.get(columnName) != null) {
//                 columnNoToUpdate.put(columnNoMap.get(columnName), updateMap.get(columnName));
//             } else {
//                 System.out.println(columnName + " does not exist in the table.");
//                 return 0;
//             }
//             // char colType = col.getType();
//         }

//         for (Row row : rows) {
//             Object[] rowData = row.getDataRow();
//             for (Integer colNo : columnNoToUpdate.keySet()) {
//                 rowData[colNo] = columnNoToUpdate.get(colNo);
//                 // if (colType == 'b') { // boolean
//                 //     rowData[colNo] = Boolean.parseBoolean(newVal);
//                 // } else if (colType == 'i') { // integer
//                 //     rowData[colNo] = Integer.parseInt(newVal);
//                 // } else if (colType == 'd') { // double
//                 //     rowData[colNo] = Double.parseDouble(newVal);
//                 // } else { // string 
//                 //     rowData[colNo] = newVal;
//                 // }
//             }
//             row.setDataRow(rowData);
//         }
//         return rows.size(); 
//     }

//     public int delete(List<String> conditions) { // idk the format passed into this method 
//         /*
//          • Example: DELETE FROM student WHERE gpa < 2.0
//          • Example: DELETE FROM student WHERE gpa < 2.0 OR name = little_bobby_tables
//          */


//         // where processing 
//         List<Row> rows = where(conditions);

//         // need delete index too

//         // idk if row needs an equal method that checks every value of its data to match??
//         for (Row row : rows) {
//             super.removeRow(row);
//         }
//         return rows.size();
//     }

//     public String select(Column[] cols, List<String> conditions) { // idk the format passed into this method 
//         /*
//         • Example: SELECT * FROM student
//         • Example: SELECT * FROM student WHERE gpa > 3.8
//         • Example: SELECT * FROM student WHERE gpa > 3.8 AND age < 20
//         • Example: SELECT * FROM student WHERE gpa > 3.8 OR age < 20
//          */
//         StringBuilder result = new StringBuilder();
//         List<Row> selectedRows;
//         if (conditions.size() == 0) {
//             selectedRows = new ArrayList<>(this.rows);
//         } else {
//             selectedRows = where(conditions);
//         }
//         Integer[] colIndex = new Integer[cols.length];
//         for (int i = 0; i < cols.length; i++) {
//             result.append(cols[i].getName() + '\t');
//             colIndex[i] = columnNoMap.get(cols[i].getName());
//         }
//         result.append('\n');
//         for (Row row : selectedRows) {
//             for (int j = 0; j < colIndex.length; j++) {
//                 result.append(row.getDataRow()[colIndex[j]].toString() + '\t');
//             }
//             result.append('\n');
//         }
//         return result.toString();
//     }
// }
