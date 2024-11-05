package edu.smu.smusql.noindex;

import java.util.*;

import edu.smu.smusql.*;
import edu.smu.smusql.column.AbstractColumn;
import edu.smu.smusql.column.HashMapNumericColumn;
import edu.smu.smusql.column.HashMapColumn;

import edu.smu.smusql.utils.WhereCondition;

public class TableArrayListHash extends AbstractTable {

    // create a table with the fixed columns and empty arraylist of rows
    public TableArrayListHash(String tableName, String[] colNames) {
        // CREATE TABLE student (id, name, age, gpa, deans_list)
        super(tableName);
        AbstractColumn[] cols = new AbstractColumn[colNames.length];
        columnNoMap = new HashMap<>();

        for (int i = 0; i < cols.length; i++) {
            // TODO: Change COLUMN IMPL here
            cols[i] = new HashMapColumn(colNames[i]);
            if (colNames[i].equalsIgnoreCase("id") || colNames[i].contains("_id")) {
                cols[i].setType('i'); // represents id 
            } // else will be default value of '0' undeclared
            columnNoMap.put(colNames[i], i);
        }

        super.setColumns(cols);
        super.setRows(new ArrayList<Row>());
        System.out.println(Arrays.toString(colNames));
    }

    public void insert(String[] values) {
        // INSERT INTO student VALUES (1, John, 30, 2.4, False)
        if (values.length != super.columns.length)
            throw new IllegalArgumentException("Invalid number of input values entered: Expected "
                    + super.columns.length + " but got " + values.length);
        Row row = new Row(values.length);
        Object[] rowData = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            AbstractColumn col = super.columns[i];
            rowData[i] = values[i]; 
            char type = col.getType(); 
            if (type == '0' || type == 'n') {
                try {
                    double val = Double.parseDouble(values[i]); 
                    rowData[i] = val; 
                    col.setType('n'); // numeric 
                } catch (NumberFormatException ex) {
                    col.setType('s'); // string 
                    rowData[i] = values[i]; 
                }
            } else {
                col.setType('s');
                rowData[i] = values[i]; 
            }
        }
        row.setDataRow(rowData);
        for (int i = 0; i < values.length; i++) {
            AbstractColumn col = super.columns[i];
            if (col.getType() == 'n') {
                if (!(col instanceof HashMapNumericColumn)) {
                    col = new HashMapNumericColumn(col.getName());
                    col.setType('n');
                    super.columns[i] = col;
                }
                ((HashMapNumericColumn) col).insertRow((Double) rowData[columnNoMap.get(col.getName())], row);
            } else {
                ((HashMapColumn) col).insertRow((String) rowData[columnNoMap.get(col.getName())], row);
            }
            
        }
        super.addRow(row);
    }

    public int update(Map<String, String> updateMap, WhereCondition conditions) {
        /*
         * • Example: UPDATE student SET age = 25 WHERE id = 1
         * • Example: UPDATE student SET deans_list = True WHERE gpa > 3.8 OR age = 201
         */
        // currently only assumed 1 col at a time lol

        // where processing
        List<Row> rows = filterRows(conditions);

        Map<Integer, Object> columnNoToUpdate = new HashMap<>(); // column no, new data

        Set<String> columnNames = updateMap.keySet();
        for (String columnName : columnNames) {

            if (columnNoMap.get(columnName) != null) {

                if (columns[columnNoMap.get(columnName)].getType() == 'n') { // numeric
                    try {
                        Double newVal = Double.parseDouble(updateMap.get(columnName));
                        columnNoToUpdate.put(columnNoMap.get(columnName), newVal);
                    } catch (NumberFormatException ex) {
                        System.out.println(String.format("%s must be a numeric", columnName));
                    }

                } else { // string or id
                    columnNoToUpdate.put(columnNoMap.get(columnName), updateMap.get(columnName));
                }
                
            } else {
                System.out.println(columnName + " does not exist in the table.");
                return 0;
            }
        }

        // updating column data and row data
        for (Row row : rows) {
            Object[] rowData = row.getDataRow();

            for (Integer colNo : columnNoToUpdate.keySet()) {
                AbstractColumn col = columns[colNo]; 

                if (col.getType() == 'n') { // update numeric column
                    HashMap<Double, List<Row>> colData = ((HashMapNumericColumn) col).getValues();
                    colData.get(rowData[colNo]).remove(row); 
                    
// try {
//                         colData.get(rowData[colNo]).remove(row); 
// } catch (NullPointerException ex) {
//     System.out.println(conditions.toString());
//     System.out.println();
//     System.out.println(colData.ceilingKey((Double) rowData[colNo]));
//     System.out.println(colData.floorKey((Double) rowData[colNo]));
//     System.out.println(colData.get((Double) rowData[colNo]));
//     System.out.println(rowData[colNo]);
//     System.out.println(col.getName());
//     System.out.println(col.getType());
//     System.out.println("update error");
//     for (String s : updateMap.keySet()) {
//         System.out.println(s + " " + updateMap.get(s));
//     }
//     System.exit(0);
// }

                    List<Row> newRows = new ArrayList<>();
                    if (colData.containsKey(columnNoToUpdate.get(colNo))) {
                        newRows = colData.get(columnNoToUpdate.get(colNo));
                    }
                    newRows.add(row);
                    colData.put((Double) columnNoToUpdate.get(colNo), newRows);

                } else { // update string column
                    HashMap<String, List<Row>> colData = ((HashMapColumn) col).getValues();
                    System.out.println(rowData[colNo]);
                    System.out.println(colData.get(rowData[colNo]));
                    colData.get(rowData[colNo]).remove(row); 
                    List<Row> newRows = new ArrayList<>();
                    if (colData.containsKey(columnNoToUpdate.get(colNo))) {
                        newRows = colData.get(columnNoToUpdate.get(colNo));
                    }
                    newRows.add(row);
                    colData.put((String) columnNoToUpdate.get(colNo), rows);
                }


                rowData[colNo] = columnNoToUpdate.get(colNo);
            }
            row.setDataRow(rowData);
        }
        return rows.size();
    }

    public int delete(WhereCondition conditions) {
        /*
         * • Example: DELETE FROM student WHERE gpa < 2.0
         * • Example: DELETE FROM student WHERE gpa < 2.0 OR name = little_bobby_tables
         */

        // where processing
        List<Row> rows = filterRows(conditions);

        for (Row row : rows) {
            Object[] rowData = row.getDataRow();
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].getType() == 'n') {
                    HashMap<Double, List<Row>> colData = ((HashMapNumericColumn) columns[i]).getValues();

// try {
//     colData.get((Double) rowData[i]).size();
//     // System.out.println("updated successfully");
// } catch (NullPointerException ex) {
//     System.out.println(conditions.toString());
//     System.out.println();
//     // for (Double d : colData.keySet()) System.out.println(d);
//     System.out.println();
//     System.out.println(colData.ceilingKey((Double) rowData[i]));
//     System.out.println(colData.floorKey((Double) rowData[i]));
//     System.out.println(colData.get((Double) rowData[i]));
//     System.out.println(rowData[i]);
//     System.out.println(columns[i].getName());
//     System.out.println(columns[i].getType());
//     System.out.println("delete error");
//     System.exit(0);
// }

                    if (colData.get(rowData[i]).size() == 1) {
                        colData.remove(rowData[i]);
                    } else {
                        colData.get(rowData[i]).remove(row); 
                    }
                    // colData.get(rowData[i]).remove(row); 
                } else {
                    HashMap<String, List<Row>> colData = ((HashMapColumn) columns[i]).getValues();
                    if (colData.get(rowData[i]).size() == 1) {
                        colData.remove(rowData[i]);
                    } else {
                        colData.get(rowData[i]).remove(row); 
                    }
                }
            }
            super.removeRow(row);
        }
        
        return rows.size();
    }

    public String select(AbstractColumn[] cols, WhereCondition conditions) {
        /*
         * • Example: SELECT * FROM student
         * • Example: SELECT * FROM student WHERE gpa > 3.8
         * • Example: SELECT * FROM student WHERE gpa > 3.8 AND age < 20
         * • Example: SELECT * FROM student WHERE gpa > 3.8 OR age < 20
         */
        StringBuilder result = new StringBuilder();
        List<Row> selectedRows = filterRows(conditions);

        Integer[] colIndex = new Integer[cols.length];
        for (int i = 0; i < cols.length; i++) {
            result.append(cols[i].getName() + '\t');
            colIndex[i] = columnNoMap.get(cols[i].getName());
        }
        result.append('\n');

        for (Row row : selectedRows) {
            for (int j = 0; j < colIndex.length; j++) {
                if ((row.getDataRow()[colIndex[j]] instanceof Double) && (((Double) row.getDataRow()[colIndex[j]]) % 1 == 0)) {
                    result.append(String.format("%d\t", ((Double) row.getDataRow()[colIndex[j]]).intValue()));
                } else {
                    result.append(row.getDataRow()[colIndex[j]].toString() + '\t');
                }
                
            }
            result.append('\n');
        }

        return result.toString();
    }
}
