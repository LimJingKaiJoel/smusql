package edu.smu.smusql.exceptions;

public class ColumnNotFoundException extends Exception {
    public ColumnNotFoundException() {
        super("Column not found");
    }
}
