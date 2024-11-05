package edu.smu.smusql.noindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.smu.smusql.Row;
import edu.smu.smusql.column.AbstractColumn;
import edu.smu.smusql.exceptions.ColumnNotFoundException;
import edu.smu.smusql.utils.Condition;
import edu.smu.smusql.utils.WhereCondition;

public abstract class AbstractTable {
    protected Collection<Row> rows; 
    protected AbstractColumn[] columns; 
    protected String tableName;
    protected Map<String, Integer> columnNoMap; 
    
    public AbstractTable(String name) {
        this.tableName = name;
    }

    public Collection<Row> getRows() {
        return rows;
    }

    public void setRows(Collection<Row> rows) {
        this.rows = rows;
    }

    public AbstractColumn[] getColumns() {
        return columns;
    }

    public void setColumns(AbstractColumn[] cols) {
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

    public boolean removeRow(Row row) {
        for (Row r : rows) {
            if (r.equals(row)) {
                return this.rows.remove(r);
            } 
        }
        return false;
    }

    public abstract void insert(String[] values);

    public abstract String select(AbstractColumn[] cols, WhereCondition conditions); 
    
    public abstract int update(Map<String, String> updateMap, WhereCondition conditions);

    public abstract int delete(WhereCondition conditions);

    protected List<Row> filterRows(WhereCondition conditions) {
        // If no conditions, return all rows
        if (conditions == null) {
            return new ArrayList<>(this.rows);
        }

        Condition condition1 = conditions.getCondition1();
        Condition condition2 = conditions.getCondition2();
        String operator = conditions.getOperator();

        // If there is no second condition, handle single condition case
        if (condition2 == null) {
            return this.columns[columnNoMap.get(condition1.getColumnName())]
                    .getRows(condition1.getOperator(), condition1.getValue());
        }

        // If both conditions are on the same column, handle as range query
        if (condition1.getColumnName().equals(condition2.getColumnName())) {
            AbstractColumn column = this.columns[columnNoMap.get(condition1.getColumnName())];
            
            if (operator.equals("AND")) {
                // For AND, get intersection of both conditions directly from the column
                return column.getRowsRange(condition1.getOperator(), condition1.getValue(), 
                                    condition2.getOperator(), condition2.getValue());
            } else {
                // For OR, get union of both conditions
                List<Row> rows1 = column.getRows(condition1.getOperator(), condition1.getValue());
                List<Row> rows2 = column.getRows(condition2.getOperator(), condition2.getValue());
                rows1.addAll(rows2);
                return rows1;
            }
        }

        // Handle conditions on different columns
        List<Row> filteredRows = this.columns[columnNoMap.get(condition1.getColumnName())]
                .getRows(condition1.getOperator(), condition1.getValue());

        if (operator.equals("OR")) {
            filteredRows.addAll(this.columns[columnNoMap.get(condition2.getColumnName())]
                    .getRows(condition2.getOperator(), condition2.getValue()));
        } else {
            // AND operator
            filteredRows.retainAll(this.columns[columnNoMap.get(condition2.getColumnName())]
                    .getRows(condition2.getOperator(), condition2.getValue()));
        }

        return filteredRows;
    }
}

