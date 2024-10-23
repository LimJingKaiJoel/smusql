package edu.smu.smusql.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import edu.smu.smusql.*;;

public abstract class AbstractTable {
    public Collection<Row> rows; 
    public Column[] cols; 
    public String tableName;
    public Map<Column, Index> indexes; // column and index 
    
    public AbstractTable(String name) {
        this.tableName = name;
        this.indexes = new HashMap<>();
    }

    public Collection<Row> getRows() {
        return rows;
    }

    public void setRows(Collection<Row> rows) {
        this.rows = rows;
    }

    public Column[] getCols() {
        return cols;
    }

    public void setCols(Column[] cols) {
        this.cols = cols;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    } 

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public void addIndex(Column c, Index i) {
        indexes.put(c, i);
    }
}

