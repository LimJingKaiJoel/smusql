package edu.smu.smusql.noindex;

import java.util.*;
import java.util.stream.IntStream;

import edu.smu.smusql.*;
import edu.smu.smusql.column.AbstractColumn;
import edu.smu.smusql.column.TreeMapColumn;
import edu.smu.smusql.column.HashMapColumn;

import edu.smu.smusql.utils.WhereCondition;

public class TableArrayListParallelStream extends AbstractTable {

    // create a table with the fixed columns and empty arraylist of rows
    public TableArrayListParallelStream(String tableName, String[] colNames) {
        // CREATE TABLE student (id, name, age, gpa, deans_list)
        super(tableName);
        AbstractColumn[] cols = new AbstractColumn[colNames.length];
        columnNoMap = new HashMap<>();

        IntStream.range(0, cols.length).parallel().forEach(i -> {
            // TODO: Change COLUMN IMPL here
            cols[i] = new HashMapColumn(colNames[i]);
            if (colNames[i].equalsIgnoreCase("id") || colNames[i].contains("_id")) {
                cols[i].setType('i'); // represents id
            } // else will be default value of '0' undeclared
            columnNoMap.put(colNames[i], i);
        });

        super.setColumns(cols);
        super.setRows(new ArrayList<Row>());
        System.out.println(Arrays.toString(colNames));
    }

    public void insert(String[] values) {
        // INSERT INTO student VALUES (1, John, 30, 2.4, False)
        if (values.length != super.columns.length)
            throw new IllegalArgumentException("Invalid number of input values entered: Expected "
                    + super.columns.length + " but got " + values.length);
        Row row = new Row(values.length);
        Object[] rowData = new Object[values.length];
        IntStream.range(0, values.length).parallel().forEach(i -> {
            AbstractColumn col = super.columns[i];
            rowData[i] = values[i];
            char type = col.getType();
            if (type == '0' || type == 'n') {
                try {
                    double val = Double.parseDouble(values[i]);
                    rowData[i] = val;
                    col.setType('n'); // numeric
                } catch (NumberFormatException ex) {
                    col.setType('s'); // string
                    rowData[i] = values[i];
                }
            } else {
                col.setType('s');
                rowData[i] = values[i];
            }
        });
        row.setDataRow(rowData);
        for (int i = 0; i < values.length; i++) {
            AbstractColumn col = super.columns[i];
            if (col.getType() == 'n') {
                if (!(col instanceof TreeMapColumn)) {
                    col = new TreeMapColumn(col.getName());
                    col.setType('n');
                    super.columns[i] = col;
                }
                ((TreeMapColumn) col).insertRow((Double) rowData[columnNoMap.get(col.getName())], row);
            } else {
                ((HashMapColumn) col).insertRow((String) rowData[columnNoMap.get(col.getName())], row);
            }

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

        // Convert Set to Array/List for parallel processing
        String[] columnNamesArray = updateMap.keySet().toArray(new String[0]);
        IntStream.range(0, columnNamesArray.length).parallel().forEach(i -> {
            String columnName = columnNamesArray[i];

            if (columnNoMap.get(columnName) != null) {

                if (columns[columnNoMap.get(columnName)].getType() == 'n') { // numeric
                    try {
                        Double newVal = Double.parseDouble(updateMap.get(columnName));
                        columnNoToUpdate.put(columnNoMap.get(columnName), newVal);
                    } catch (NumberFormatException ex) {
                        System.out.println(String.format("%s must be a numeric", columnName));
                    }

                } else { // string or id
                    columnNoToUpdate.put(columnNoMap.get(columnName), updateMap.get(columnName));
                }

            } else {
                System.out.println(columnName + " does not exist in the table.");
            }
        });

        // updating column data and row data
        rows.parallelStream().forEach(row -> {
            Object[] rowData = row.getDataRow();

            columnNoToUpdate.keySet().parallelStream().forEach(colNo -> {
                AbstractColumn col = columns[colNo];

                if (col.getType() == 'n') {
                    TreeMap<Double, List<Row>> colData = ((TreeMapColumn) col).getValues();
                    synchronized (colData) {
                        colData.get(rowData[colNo]).remove(row);
                        List<Row> newRows = new ArrayList<>();
                        if (colData.containsKey(columnNoToUpdate.get(colNo))) {
                            newRows = colData.get(columnNoToUpdate.get(colNo));
                        }
                        newRows.add(row);
                        colData.put((Double) columnNoToUpdate.get(colNo), newRows);
                    }
                } else {
                    HashMap<String, List<Row>> colData = ((HashMapColumn) col).getValues();
                    synchronized (colData) {
                        colData.get(rowData[colNo]).remove(row);
                        List<Row> newRows = new ArrayList<>();
                        if (colData.containsKey(columnNoToUpdate.get(colNo))) {
                            newRows = colData.get(columnNoToUpdate.get(colNo));
                        }
                        newRows.add(row);
                        colData.put((String) columnNoToUpdate.get(colNo), rows);
                    }
                }
                rowData[colNo] = columnNoToUpdate.get(colNo);
            });
            row.setDataRow(rowData);
        });
        return rows.size();
    }

    public int delete(WhereCondition conditions) {
        // where processing
        List<Row> rows = filterRows(conditions);
        
        // First update the columns
        rows.parallelStream().forEach(row -> {
            Object[] rowData = row.getDataRow();
            IntStream.range(0, columns.length).parallel().forEach(i -> {
                if (columns[i].getType() == 'n') {
                    TreeMap<Double, List<Row>> colData = ((TreeMapColumn) columns[i]).getValues();
                    synchronized (colData) {
                        if (colData.get(rowData[i]).size() == 1) {
                            colData.remove(rowData[i]);
                        } else {
                            colData.get(rowData[i]).remove(row);
                        }
                    }
                } else {
                    HashMap<String, List<Row>> colData = ((HashMapColumn) columns[i]).getValues();
                    synchronized (colData) {
                        if (colData.get(rowData[i]).size() == 1) {
                            colData.remove(rowData[i]);
                        } else {
                            colData.get(rowData[i]).remove(row);
                        }
                    }
                }
            });
        });

        // Then remove all rows at once using removeAll
        synchronized(this) {
            super.getRows().removeAll(rows);
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
        StringBuilder result = new StringBuilder();
        List<Row> selectedRows = filterRows(conditions);

        Integer[] colIndex = new Integer[cols.length];
        IntStream.range(0, cols.length).parallel().forEach(i -> {
            synchronized (result) {
                result.append(cols[i].getName() + '\t');
            }
            colIndex[i] = columnNoMap.get(cols[i].getName());
        });
        result.append('\n');

        for (Row row : selectedRows) {
            for (int j = 0; j < colIndex.length; j++) {
                if ((row.getDataRow()[colIndex[j]] instanceof Double)
                        && (((Double) row.getDataRow()[colIndex[j]]) % 1 == 0)) {
                    result.append(String.format("%d\t", ((Double) row.getDataRow()[colIndex[j]]).intValue()));
                } else {
                    result.append(row.getDataRow()[colIndex[j]].toString() + '\t');
                }

            }
            result.append('\n');
        }

        return result.toString();
    }
}
