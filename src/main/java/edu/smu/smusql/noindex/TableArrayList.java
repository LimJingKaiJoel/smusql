package edu.smu.smusql.noindex;

import java.util.*;

import edu.smu.smusql.*;
import edu.smu.smusql.column.AbstractColumn;
import edu.smu.smusql.column.TreeMapColumn;
import edu.smu.smusql.column.HashMapColumn;
import edu.smu.smusql.column.CustomHashMapColumn;

import edu.smu.smusql.utils.WhereCondition;

public class TableArrayList extends AbstractTable {

    // create a table with the fixed columns and empty arraylist of rows
    public TableArrayList(String tableName, String[] colNames) {
        // CREATE TABLE student (id, name, age, gpa, deans_list)
        super(tableName);
        AbstractColumn[] cols = new AbstractColumn[colNames.length];
        columnNoMap = new HashMap<>();

        for (int i = 0; i < cols.length; i++) {
            // TODO: Change COLUMN IMPL here
            cols[i] = new CustomHashMapColumn(colNames[i]);
            columnNoMap.put(colNames[i], i);
        }

        super.setColumns(cols);
        super.setRows(new ArrayList<Row>());
    }

    public void insert(String[] values) {
        // INSERT INTO student VALUES (1, John, 30, 2.4, False)
        if (values.length != super.columns.length)
            throw new IllegalArgumentException("Invalid number of input values entered: Expected "
                    + super.columns.length + " but got " + values.length);
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
        for (AbstractColumn col : super.columns) {
            col.insertRow(values[columnNoMap.get(col.getName())], row);
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
                columnNoToUpdate.put(columnNoMap.get(columnName), updateMap.get(columnName));
            } else {
                System.out.println(columnName + " does not exist in the table.");
                return 0;
            }
            // char colType = col.getType();
        }

        for (Row row : rows) {
            Object[] rowData = row.getDataRow();
            for (Integer colNo : columnNoToUpdate.keySet()) {
                rowData[colNo] = columnNoToUpdate.get(colNo);
                // if (colType == 'b') { // boolean
                // rowData[colNo] = Boolean.parseBoolean(newVal);
                // } else if (colType == 'i') { // integer
                // rowData[colNo] = Integer.parseInt(newVal);
                // } else if (colType == 'd') { // double
                // rowData[colNo] = Double.parseDouble(newVal);
                // } else { // string
                // rowData[colNo] = newVal;
                // }
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

        // idk if row needs an equal method that checks every value of its data to
        // match??
        for (Row row : rows) {
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
        // List<Row> result = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<Row> selectedRows = filterRows(conditions);

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
}
