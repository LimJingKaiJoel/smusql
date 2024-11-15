package edu.smu.smusql;

public class Column {
    private String name;
    private char type; 


    public Column(String name) {
        this.name = name;
        this.type = 's';
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    } 
}
