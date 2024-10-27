package edu.smu.smusql.indexing;

import java.util.List;
import edu.smu.smusql.Row;

public interface Index {
    void addRow(Row row);
    void removeRow(Row row);
    List<Row> search(String operator, Object value);
    String getColumnName();
}