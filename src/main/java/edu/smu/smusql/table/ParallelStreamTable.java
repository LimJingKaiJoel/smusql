package edu.smu.smusql.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.smu.smusql.Column;

public class ParallelStreamTable extends AbstractTable {
    public ParallelStreamTable(String tableName, List<String> columns) {
        super(tableName, columns);
    }

    @Override
    public String select(Column[] selectedColumns, List<String> conditions) {
        StringBuilder result = new StringBuilder();

        // Add header
        for (Column column : selectedColumns) {
            result.append(column.getName()).append("\t");
        }
        result.append("\n");

        // Use a thread-safe collection to gather results
        List<String> results = Collections.synchronizedList(new ArrayList<>());

        this.rows.parallelStream()
            .filter(row -> matchesConditions(row, conditions))
            .forEach(row -> {
                StringBuilder rowResult = new StringBuilder();
                for (Column column : selectedColumns) {
                    int index = Arrays.asList(columns).indexOf(column);
                    String value = (index != -1 && index < row.getDataRow().length) ? (String) row.getDataRow()[index] : "NULL";
                    rowResult.append(value).append("\t");
                }
                results.add(rowResult.toString());
            });

        // Append all row results to the main result
        results.forEach(result::append);

        return result.toString();
    }
}
