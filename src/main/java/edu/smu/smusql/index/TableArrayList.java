package edu.smu.smusql.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.smu.smusql.*;

public class TableArrayList extends AbstractTable {

    // create a table with the fixed columns and empty arraylist of rows 
    public TableArrayList(String[] colNames, String tableName) {
        // CREATE TABLE student (id, name, age, gpa, deans_list)
        super(tableName);
        Column[] cols = new Column[colNames.length];

        for (int i = 0; i < cols.length; i++) {
            cols[i] = new Column(colNames[i], i);
        }

        super.setCols(cols);
        super.setRows(new ArrayList<Row>());

    }

    public void insert(String[] values) {
        // INSERT INTO student VALUES (1, John, 30, 2.4, False)
        if (values.length != super.cols.length) throw new IllegalArgumentException("Invalid number of input values entered: Expected "+ super.cols.length + " but got " + values.length);
        Row row = new Row(values.length);
        Object[] rowData = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            char type = cols[i].getType(); 
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

    public void update() { // idk the format passed into this method 
        /*
         • Example: UPDATE student SET age = 25 WHERE id = 1
         • Example: UPDATE student SET deans_list = True WHERE gpa > 3.8 OR age = 201
         */

    }

    public void delete() { // idk the format passed into this method 
        /*
         • Example: DELETE FROM student WHERE gpa < 2.0
         • Example: DELETE FROM student WHERE gpa < 2.0 OR name = little_bobby_tables
         */

    }

    public void select(String condition) { // idk the format passed into this method 
        /*
        • Example: SELECT * FROM student
        • Example: SELECT * FROM student WHERE gpa > 3.8
        • Example: SELECT * FROM student WHERE gpa > 3.8 AND age < 20
        • Example: SELECT * FROM student WHERE gpa > 3.8 OR age < 20
         */
    }

    public void index(Column col) { // index the column and add to map of indexes 
        Map<Object, ArrayList<Row>> mapping = new HashMap<>(); // idk wat map to use first 
        int colNo = col.getNumber();
        for (Row row : super.rows) {
            Object val = row.getDataRow()[colNo]; 
            if (!mapping.containsKey(val)) {
                mapping.put(val, new ArrayList<Row>());
            }
            mapping.get(val).add(row);
        }
        Index index = new Index(mapping);
        super.addIndex(col, index);
    }
}
