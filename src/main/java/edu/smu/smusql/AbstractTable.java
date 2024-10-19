package edu.smu.smusql;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractTable {
    public Collection<Row> rows; 
    public Column[] cols; 
    public String tableName;
    public Map<String, Index> indexes; 
    
    public AbstractTable(String name) {
        this.tableName = name;
        
    }

    // public AbstractTable(Collection<Row> rows, Collection<Column> cols, String tableName) {
    //     this.rows = rows;
    //     this.cols = cols;
    //     this.tableName = tableName;
    // }

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
    
    // public void addCol(Column col) {
    //     this.cols.add(col);
    // }
}
