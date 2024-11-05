package edu.smu.smusql;

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

    public boolean equals(Object obj) {
        if (obj instanceof Row) {
            Object[] rowData = ((Row) obj).getDataRow(); 
            if (rowData.length != dataRow.length) return false; 
            for (int i = 0; i < rowData.length; i++) {
                if (!rowData[i].toString().equals(dataRow[i].toString())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
