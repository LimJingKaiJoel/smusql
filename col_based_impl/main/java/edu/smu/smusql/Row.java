package edu.smu.smusql;

import java.util.Arrays;

public class Row {
    public Object[] dataRow;

    public Row(int colSize) {
        this.dataRow = new Object[colSize];
    }

    public Row(int colSize, Object[] dataRow) {
        this.dataRow = new Object[colSize];
        System.arraycopy(dataRow, 0, this.dataRow, 0, colSize);
    }

    public Object[] getDataRow() {
        return dataRow;
    }

    public void setDataRow(Object[] dataRow) {
        this.dataRow = dataRow;
    }

    @Override
    public String toString() {
        return Arrays.toString(dataRow);
    }
}
