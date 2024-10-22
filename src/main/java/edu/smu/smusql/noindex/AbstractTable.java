package edu.smu.smusql.noindex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import edu.smu.smusql.*;;

public abstract class AbstractTable {
    public Collection<Row> rows; 
    public Column[] cols; 
    public String tableName;
    
    public AbstractTable(String name) {
        this.tableName = name;
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

    public void removeRow(Row row) {
        this.rows.remove(row);
    }
}

