package edu.smu.smusql.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.smu.smusql.Column;
import edu.smu.smusql.Row;

public class ParallelStreamTable extends AbstractTable {
    public ParallelStreamTable(String tableName, List<String> columns) {
        super(tableName, columns);
    }

    @Override
    public List<Row> select(Column[] selectedColumns, List<String> conditions) {
        // Use a thread-safe collection to gather results
        List<Row> results = Collections.synchronizedList(new ArrayList<>());

        this.rows.parallelStream()
                .filter(row -> matchesConditions(row, conditions))
                .forEach(row -> {
                    results.add(row);
                });

        return results;
    }
}
