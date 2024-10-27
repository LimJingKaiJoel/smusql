package edu.smu.smusql.index;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.smu.smusql.Column;
import edu.smu.smusql.Row;

public abstract class AbstractTable {
    protected Collection<Row> rows; 
    protected Column[] columns; 
    protected String tableName;
    protected Map<String, Integer> columnNoMap; 
    public Map<Column, Index> indexes; // column and index 
    
    public AbstractTable(String name) {
        this.tableName = name;
    }

    public Collection<Row> getRows() {
        return rows;
    }

    public void setRows(Collection<Row> rows) {
        this.rows = rows;
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] cols) {
        this.columns = cols;
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

    public void addIndex(Column c, Index i) {
        indexes.put(c, i);
    }

    public abstract void insert(String[] values);

    public abstract String select(Column[] cols, List<String> conditions); 
    
    public abstract int update(Map<String, Object> updateMap, List<String> conditions);

    public abstract int delete(List<String> conditions);
}

