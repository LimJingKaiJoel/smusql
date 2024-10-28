package edu.smu.smusql.noindex;

import java.util.ArrayList;
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

    public void removeRow(Row row) {
        this.rows.remove(row);
    }

    public abstract void insert(String[] values);

    public abstract String select(AbstractColumn[] cols, WhereCondition conditions); 
    
    public abstract int update(Map<String, Object> updateMap, WhereCondition conditions);

    public abstract int delete(WhereCondition conditions);

    protected List<Row> filterRows(WhereCondition conditions) {
        // If no conditions, return all rows
        if (conditions == null) {
            return new ArrayList<>(this.rows);
        }

        Condition condition1 = conditions.getCondition1();
        Condition condition2 = conditions.getCondition2();
        String operator = conditions.getOperator();

        // Get rows that match the first condition
        List<Row> filteredRows = this.columns[columnNoMap.get(condition1.getColumnName())].getRows(condition1.getOperator(), condition1.getValue().toString());

        // If there is no second condition, return the rows that match the first condition
        if (condition2 == null) {
            return filteredRows;
        }

        // If operator is OR, return the rows that match either condition
        if (operator.equals("OR")) {
            filteredRows.addAll(this.columns[columnNoMap.get(condition2.getColumnName())].getRows(condition2.getOperator(), condition2.getValue().toString()));
        } else {
            // If operator is AND, return the rows that match both conditions
            // Check if the rows in filteredRows pass the second condition
            filteredRows.retainAll(this.columns[columnNoMap.get(condition2.getColumnName())].getRows(condition2.getOperator(), condition2.getValue().toString()));
        }

        return filteredRows;
    }
}

