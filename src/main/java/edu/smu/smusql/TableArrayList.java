package edu.smu.smusql;

import java.util.ArrayList;
import java.util.Collection;

public class TableArrayList extends AbstractTable {

    // create a table with the fixed columns and empty arraylist of rows 
    public TableArrayList(String[] colNames, String tableName) {
        super(tableName);
        Column[] cols = new Column[colNames.length];

        for (int i = 0; i < cols.length; i++) {
            cols[i] = new Column(colNames[i], i);
        }

        super.setCols(cols);
        super.setRows(new ArrayList<Row>());

    }

    public void insert(String[] values) {
        

    }
}
