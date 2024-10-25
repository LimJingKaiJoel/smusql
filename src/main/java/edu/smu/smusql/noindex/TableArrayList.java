package edu.smu.smusql.noindex;

import java.util.*;

import edu.smu.smusql.*;
import edu.smu.smusql.exceptions.ColumnNotFoundException;

public class TableArrayList extends AbstractTable {

    // create a table with the fixed columns and empty arraylist of rows 
    public TableArrayList(String[] colNames, String tableName) {
        // CREATE TABLE student (id, name, age, gpa, deans_list)
        super(tableName);
        Column[] cols = new Column[colNames.length];

        for (int i = 0; i < cols.length; i++) {
            cols[i] = new Column(colNames[i], i);
        }

        super.setColumns(cols);
        super.setRows(new ArrayList<Row>());

    }

    public void insert(String[] values) {
        // INSERT INTO student VALUES (1, John, 30, 2.4, False)
        if (values.length != super.columns.length) throw new IllegalArgumentException("Invalid number of input values entered: Expected "+ super.columns.length + " but got " + values.length);
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
    }

    public List<Row> where(List<String> conditions) {
        // idk yet
        return new ArrayList<>();
    }

    public int update(Map<String, Object> updateMap, List<String> conditions) { // idk the format passed into this method 
        /*
         • Example: UPDATE student SET age = 25 WHERE id = 1
         • Example: UPDATE student SET deans_list = True WHERE gpa > 3.8 OR age = 201
        */
        // currently only assumed 1 col at a time lol

        // where processing 
        List<Row> rows = where(conditions);

        Map<Integer, Object> columnNoToUpdate = new HashMap<>();

        Set<String> columnNames = updateMap.keySet(); 
        for (String columnName : columnNames) {
            Column col;
            try {
                col = findColumn(columnName);
            } catch (ColumnNotFoundException ex) {
                System.out.println(ex.getMessage());
                return 0;
            }
            columnNoToUpdate.put(col.getNumber(), updateMap.get(columnName));
            // char colType = col.getType();
        }

        for (Row row : rows) {
            Object[] rowData = row.getDataRow();
            for (Integer colNo : columnNoToUpdate.keySet()) {
                rowData[colNo] = columnNoToUpdate.get(colNo);
                // if (colType == 'b') { // boolean
                //     rowData[colNo] = Boolean.parseBoolean(newVal);
                // } else if (colType == 'i') { // integer
                //     rowData[colNo] = Integer.parseInt(newVal);
                // } else if (colType == 'd') { // double
                //     rowData[colNo] = Double.parseDouble(newVal);
                // } else { // string 
                //     rowData[colNo] = newVal;
                // }
            }
            row.setDataRow(rowData);
        }
        return rows.size(); 
    }

    public int delete(List<String> conditions) { // idk the format passed into this method 
        /*
         • Example: DELETE FROM student WHERE gpa < 2.0
         • Example: DELETE FROM student WHERE gpa < 2.0 OR name = little_bobby_tables
         */


        // where processing 
        List<Row> rows = where(conditions);

        // idk if row needs an equal method that checks every value of its data to match??
        for (Row row : rows) {
            super.removeRow(row);
        }
        return rows.size();
    }

    public List<Row> select(Column[] cols, List<String> conditions) { // idk the format passed into this method 
        /*
        • Example: SELECT * FROM student
        • Example: SELECT * FROM student WHERE gpa > 3.8
        • Example: SELECT * FROM student WHERE gpa > 3.8 AND age < 20
        • Example: SELECT * FROM student WHERE gpa > 3.8 OR age < 20
         */
        return where(conditions);
    }
}
