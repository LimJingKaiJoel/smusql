package edu.smu.smusql;

public class Column {
    private String name;
    private int number; 
    private char type; 

    public Column(String name, int id) {
        this.name = name;
        this.number = id; 
        this.type = 's';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int id) {
        this.number = id;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    } 

    
}
