package edu.smu.smusql;

import java.util.ArrayList;
import java.util.List;

public class Row {
    public Object[] dataRow;

    public Row(int colSize) {
        dataRow = new Object[colSize];
    }

    public Object[] getDataRow() {
        return dataRow;
    }

    public void setDataRow(Object[] dataRow) {
        this.dataRow = dataRow;
    }

}
