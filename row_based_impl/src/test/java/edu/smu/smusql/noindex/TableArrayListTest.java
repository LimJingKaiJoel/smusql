package edu.smu.smusql.noindex;

import edu.smu.smusql.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TableArrayListTest {

    private TableArrayList table;

    @BeforeEach
    void setUp() {
        String[] columns = {"id", "name", "age", "gpa", "deans_list"};
        table = new TableArrayList("student", columns);

        // Insert sample data into the table
        table.insert(new String[]{"1", "Alice", "20", "3.9", "true"});
        table.insert(new String[]{"2", "Bob", "21", "3.5", "true"});
        table.insert(new String[]{"3", "Charlie", "22", "2.8", "false"});
        table.insert(new String[]{"4", "Diana", "23", "3.2", "false"});
    }

    @Test
    void testWhereWithGpaAndDeansList() {
        List<String> conditions = Arrays.asList("gpa", ">", "3.0", "AND", "deans_list", "=", "true");
        List<Row> result = table.where(conditions);

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getDataRow()[1]);
        assertEquals("Bob", result.get(1).getDataRow()[1]);
    }

    @Test
    void testWhereWithAgeOrName() {
        List<String> conditions = Arrays.asList("age", ">=", "22", "OR", "name", "=", "'Bob'");
        List<Row> result = table.where(conditions);

        assertEquals(3, result.size());
    }

    @Test
    void testWhereWithNameAndGpa() {
        List<String> conditions = Arrays.asList("name", "=", "'Charlie'", "AND", "gpa", "<", "3.0");
        List<Row> result = table.where(conditions);

        assertEquals(1, result.size());
        assertEquals("Charlie", result.get(0).getDataRow()[1]);
    }

    // @Test
    // void testWhereWithNestedConditions() {
    //     // WHERE (age >= 22 OR name = 'Bob') AND (gpa < 3.5 OR deans_list = true)
    //     List<String> conditions = Arrays.asList(
    //         "age", ">=", "22", "OR", "name", "=", "'Bob'",
    //         "gpa", "<", "3.5", "OR", "deans_list", "=", "true", 
    //         "AND"
    //     );
    //     List<Row> result = table.where(conditions);

    //     assertEquals(3, result.size());
    //     assertTrue(result.stream().anyMatch(row -> row.getDataRow()[1].equals("Bob")));
    // }

    @Test
    void testWhereWithOnlyLogicalOperator() {
        // Edge case: No comparison operators, only logical operators
        List<String> conditions = Arrays.asList("deans_list", "=", "true", "OR", "deans_list", "=", "false");
        List<Row> result = table.where(conditions);

        assertEquals(4, result.size()); // All rows should be selected
    }

    @Test
    void testWhereWithEmptyCondition() {
        // Edge case: Empty condition list
        List<String> conditions = Collections.emptyList();
        Exception exception = assertThrows(IllegalStateException.class, () -> table.where(conditions));
        assertEquals("Invalid condition expression", exception.getMessage());
    }

    @Test
    void testWhereWithInvalidCondition() {
        // Edge case: Incorrect syntax that will cause an exception
        List<String> conditions = Arrays.asList("name", "=");
        Exception exception = assertThrows(IllegalStateException.class, () -> table.where(conditions));
        assertEquals("Not enough operands for comparison operator.", exception.getMessage());
    }

    // @Test
    // void testWhereWithMultipleAndOr() {
    //     // WHERE (gpa >= 3.2 AND deans_list = false) OR (age < 21 AND name = 'Alice')
    //     List<String> conditions = Arrays.asList(
    //         "gpa", ">=", "3.2", "AND", "deans_list", "=", "false",
    //         "age", "<", "21", "AND", "name", "=", "'Alice'",
    //         "OR"
    //     );
    //     List<Row> result = table.where(conditions);

    //     assertEquals(2, result.size());
    //     assertEquals("Alice", result.get(0).getDataRow()[1]);
    //     assertEquals("Diana", result.get(1).getDataRow()[1]);
    // }

    @Test
    void testWhereWithAllFalseConditions() {
        // WHERE gpa < 2.0 AND name = 'Joel'
        List<String> conditions = Arrays.asList("gpa", "<", "2.0", "AND", "name", "=", "'Joel'");
        List<Row> result = table.where(conditions);

        assertEquals(0, result.size()); // No rows should match
    }

    @Test
    void testWhereWithAllTrueCondition() {
        // WHERE id >= 1
        List<String> conditions = Arrays.asList("id", ">=", "1");
        List<Row> result = table.where(conditions);

        assertEquals(4, result.size()); // All rows should match
    }
}