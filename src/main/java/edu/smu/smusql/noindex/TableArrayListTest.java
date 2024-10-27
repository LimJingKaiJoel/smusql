package edu.smu.smusql.noindex;
import java.util.*;
import java.util.regex.*;
import edu.smu.smusql.*;

public class TableArrayListTest {
    public static void main(String[] args) {
        // Create table with columns
        String[] columns = {"id", "name", "age", "gpa", "deans_list"};
        TableArrayList table = new TableArrayList(columns, "student");

        // Insert rows into the table
        table.insert(new String[]{"1", "Alice", "20", "3.9", "true"});
        table.insert(new String[]{"2", "Bob", "21", "3.5", "true"});
        table.insert(new String[]{"3", "Charlie", "22", "2.8", "false"});
        table.insert(new String[]{"4", "Diana", "23", "3.2", "false"});

        // Create an instance of Engine to parse where conditions
        Engine engine = new Engine();

        // Test 1: Select students with gpa > 3.0 AND deans_list = true
        String whereClause1 = "gpa > 3.0 AND deans_list = true";
        List<String> conditions1 = engine.parseWhereConditions(whereClause1);
        Column[] selectedColumns1 = table.getColumns();
        List<Row> result1 = table.select(selectedColumns1, conditions1);
        System.out.println("Test 1 Results:");
        for (Row row : result1) {
            System.out.println(Arrays.toString(row.getDataRow()));
        }

        // Test 2: Select students with age >= 22 OR name = 'Bob'
        String whereClause2 = "age >= 22 OR name = 'Bob'";
        List<String> conditions2 = engine.parseWhereConditions(whereClause2);
        Column[] selectedColumns2 = table.getColumns();
        List<Row> result2 = table.select(selectedColumns2, conditions2);
        System.out.println("\nTest 2 Results:");
        for (Row row : result2) {
            System.out.println(Arrays.toString(row.getDataRow()));
        }

        // Test 3: Select students where name = 'Charlie' AND gpa < 3.0
        String whereClause3 = "name = 'Charlie' AND gpa < 3.0";
        List<String> conditions3 = engine.parseWhereConditions(whereClause3);
        Column[] selectedColumns3 = table.getColumns();
        List<Row> result3 = table.select(selectedColumns3, conditions3);
        System.out.println("\nTest 3 Results:");
        for (Row row : result3) {
            System.out.println(Arrays.toString(row.getDataRow()));
        }
    }
}