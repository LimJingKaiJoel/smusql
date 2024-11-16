package edu.smu.smusql;

import java.util.*;

// @author ziyuanliu@smu.edu.sg

public class Main {
    /*
     * Main method for accessing the command line interface of the database engine.
     * MODIFICATION OF THIS FILE IS NOT RECOMMENDED!
     */
    static Engine dbEngine = new Engine();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("smuSQL Starter Code version 0.5");
        System.out.println("Have fun, and good luck!");

        // SAMPLE QUERIES FOR TESTING

        // dbEngine.executeSQL("CREATE TABLE users (id, name, age, city)");
        // dbEngine.executeSQL("INSERT INTO users VALUES (1, 'Alice', 25, 'New York')");
        // dbEngine.executeSQL("INSERT INTO users VALUES (2, 'Bob', 30, 'Los
        // Angeles')");

        // System.out.println("You should see both rows");
        // System.out.println(dbEngine.executeSQL("SELECT * FROM users"));

        // System.out.println("You should see both Alice and Bob");
        // System.out.println(dbEngine.executeSQL("SELECT name FROM users"));

        // System.out.println("You should see 'Alice', 25");
        // System.out.println(dbEngine.executeSQL("SELECT name, age FROM users WHERE id
        // = 1"));

        // System.out.println("You should see 1, 'Alice', 25, 'New York'");
        // System.out.println(dbEngine.executeSQL("SELECT * FROM users WHERE id = 1"));

        // System.out.println("You should see both rows");
        // System.out.println(dbEngine.executeSQL("SELECT * FROM users WHERE id = 1 OR
        // name = 'Bob"));

        // dbEngine.executeSQL("UPDATE users SET age = 31 WHERE city = 'Los Angeles' OR
        // id = 1");

        // System.out.println("You should see both rows but age is 31");
        // System.out.println(dbEngine.executeSQL("SELECT * FROM users"));

        // dbEngine.executeSQL("UPDATE users SET age = 55 WHERE id = 1 AND city = 'New
        // York'");

        // System.out.println("You should see both rows but Alice age is 55");
        // System.out.println(dbEngine.executeSQL("SELECT * FROM users"));

        // System.out.println("You should see only Bob");
        // System.out.println(dbEngine.executeSQL("SELECT * FROM users WHERE id > 1"));

        // dbEngine.executeSQL(
        // "DELETE FROM users WHERE id = 1 AND city = 'New York'");
        // System.out.println("You should see only Bob");
        // System.out.println(dbEngine.executeSQL("SELECT * FROM users"));

        while (true) {
            System.out.print("smusql> ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit")) {
                break;
            } else if (query.equalsIgnoreCase("evaluate")) {
                autoEvaluate();
                break;
            }

            System.out.println(dbEngine.executeSQL(query));
        }
        scanner.close();
    }

    /*
     * Below is the code for auto-evaluating your work.
     * DO NOT CHANGE ANYTHING BELOW THIS LINE!
     */
    public static void autoEvaluate() {

        long startTime = System.nanoTime();

        // Set the number of queries to execute
        int numberOfQueries = 100000;

        // Create tables
        dbEngine.executeSQL("CREATE TABLE users (id, name, age, city)");
        dbEngine.executeSQL("CREATE TABLE products (id, name, price, category)");
        dbEngine.executeSQL("CREATE TABLE orders (id, user_id, product_id, quantity)");

        // Random data generator
        Random random = new Random();

        // Prepopulate the tables in preparation for evaluation
        prepopulateTables(random);

        long queryStartTime;

        Map<Integer, ArrayList<Double>> queryTimes = new HashMap<>();

        // Loop to simulate millions of queries
        for (int i = 0; i < numberOfQueries; i++) {
            queryStartTime = System.nanoTime();
            int queryType = random.nextInt(6); // Randomly choose the type of query to execute

            switch (queryType) {
                case 0: // INSERT query
                    insertRandomData(random);
                    break;
                case 1: // SELECT query (simple)
                    selectRandomData(random);
                    break;
                case 2: // UPDATE query
                    updateRandomData(random);
                    break;
                case 3: // DELETE query
                    deleteRandomData(random);
                    break;
                case 4: // Complex SELECT query with WHERE, AND, OR, >, <, LIKE
                    complexSelectQuery(random);
                    break;
                case 5: // Complex UPDATE query with WHERE
                    complexUpdateQuery(random);
                    break;
            }

            long queryElapsedTime = System.nanoTime() - queryStartTime;
            double queryElapsedTimeInSecond = (double) queryElapsedTime / 1_000_000_000;
            queryTimes.computeIfAbsent(queryType, k -> new ArrayList<>()).add(queryElapsedTimeInSecond);

            // Print progress every 100,000 queries
            if (i % 10000 == 0) {
                double timeSoFar = (System.nanoTime() - startTime) / 1_000_000_000;
                System.out
                        .println("Processed " + i + " queries in " + String.format("%.2f", timeSoFar) + " seconds...");
            }
        }

        long stopTime = System.nanoTime();
        long elapsedTime = stopTime - startTime;
        double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;

        System.out.println("Finished processing " + numberOfQueries + " queries.");

        // METRICS
        System.out.println("Total time elapsed: " + elapsedTimeInSecond + " seconds");
        System.out.println("Total number of queries: " + numberOfQueries);
        System.out.println("Queries per second: " + numberOfQueries / elapsedTimeInSecond);
        System.out.println("Average query time: " + String.format("%.5f", elapsedTimeInSecond / numberOfQueries * 1000)
                + " milliseconds");
        queryTimes.forEach((key, value) -> {
            System.out.println("Query type " + key + " took "
                    + String.format("%.5f", value.stream().mapToDouble(Double::doubleValue).average().orElse(0) * 1000)
                    + " milliseconds on average, for " + value.size() + " queries");
        });

    }

    private static void prepopulateTables(Random random) {
        System.out.println("Prepopulating users");
        // Insert initial users
        for (int i = 0; i < 50; i++) {
            String name = "User" + i;
            int age = 20 + (i % 41); // Ages between 20 and 60
            String city = getRandomCity(random);
            String insertCommand = String.format("INSERT INTO users VALUES (%d, '%s', %d, '%s')", i, name, age, city);
            dbEngine.executeSQL(insertCommand);
        }
        System.out.println("Prepopulating products");
        // Insert initial products
        for (int i = 0; i < 50; i++) {
            String productName = "Product" + i;
            double price = 10 + (i % 990); // Prices between $10 and $1000
            String category = getRandomCategory(random);
            String insertCommand = String.format("INSERT INTO products VALUES (%d, '%s', %.2f, '%s')", i, productName,
                    price, category);
            dbEngine.executeSQL(insertCommand);
        }
        System.out.println("Prepopulating orders");
        // Insert initial orders
        for (int i = 0; i < 50; i++) {
            int user_id = random.nextInt(9999);
            int product_id = random.nextInt(9999);
            int quantity = random.nextInt(1, 100);
            String category = getRandomCategory(random);
            String insertCommand = String.format("INSERT INTO orders VALUES (%d, %d, %d, %d)", i, user_id, product_id,
                    quantity);
            dbEngine.executeSQL(insertCommand);
        }
    }

    // Helper method to insert random data into users, products, or orders table
    private static void insertRandomData(Random random) {
        int tableChoice = random.nextInt(3);
        switch (tableChoice) {
            case 0: // Insert into users table
                int id = random.nextInt(10000) + 10000;
                String name = "User" + id;
                int age = random.nextInt(60) + 20;
                String city = getRandomCity(random);
                String insertUserQuery = "INSERT INTO users VALUES (" + id + ", '" + name + "', " + age + ", '" + city
                        + "')";
                dbEngine.executeSQL(insertUserQuery);
                break;
            case 1: // Insert into products table
                int productId = random.nextInt(1000) + 10000;
                String productName = "Product" + productId;
                double price = 50 + (random.nextDouble() * 1000);
                String category = getRandomCategory(random);
                String insertProductQuery = "INSERT INTO products VALUES (" + productId + ", '" + productName + "', "
                        + price + ", '" + category + "')";
                dbEngine.executeSQL(insertProductQuery);
                break;
            case 2: // Insert into orders table
                int orderId = random.nextInt(10000) + 1;
                int userId = random.nextInt(10000) + 1;
                int productIdRef = random.nextInt(1000) + 1;
                int quantity = random.nextInt(10) + 1;
                String insertOrderQuery = "INSERT INTO orders VALUES (" + orderId + ", " + userId + ", " + productIdRef
                        + ", " + quantity + ")";
                dbEngine.executeSQL(insertOrderQuery);
                break;
        }
    }

    // Helper method to randomly select data from tables
    private static void selectRandomData(Random random) {
        int tableChoice = random.nextInt(3);
        String selectQuery;
        switch (tableChoice) {
            case 0:
                selectQuery = "SELECT * FROM users";
                break;
            case 1:
                selectQuery = "SELECT * FROM products";
                break;
            case 2:
                selectQuery = "SELECT * FROM orders";
                break;
            default:
                selectQuery = "SELECT * FROM users";
        }
    }

    // Helper method to update random data in the tables
    private static void updateRandomData(Random random) {
        int tableChoice = random.nextInt(3);
        switch (tableChoice) {
            case 0: // Update users table
                int id = random.nextInt(10000) + 1;
                int newAge = random.nextInt(60) + 20;
                String updateUserQuery = "UPDATE users SET age = " + newAge + " WHERE id = " + id;
                dbEngine.executeSQL(updateUserQuery);
                break;
            case 1: // Update products table
                int productId = random.nextInt(1000) + 1;
                double newPrice = 50 + (random.nextDouble() * 1000);
                String updateProductQuery = "UPDATE products SET price = " + newPrice + " WHERE id = " + productId;
                dbEngine.executeSQL(updateProductQuery);
                break;
            case 2: // Update orders table
                int orderId = random.nextInt(10000) + 1;
                int newQuantity = random.nextInt(10) + 1;
                String updateOrderQuery = "UPDATE orders SET quantity = " + newQuantity + " WHERE id = " + orderId;
                dbEngine.executeSQL(updateOrderQuery);
                break;
        }
    }

    // Helper method to delete random data from tables
    private static void deleteRandomData(Random random) {
        int tableChoice = random.nextInt(3);
        switch (tableChoice) {
            case 0: // Delete from users table
                int userId = random.nextInt(10000) + 1;
                String deleteUserQuery = "DELETE FROM users WHERE id = " + userId;
                dbEngine.executeSQL(deleteUserQuery);
                break;
            case 1: // Delete from products table
                int productId = random.nextInt(1000) + 1;
                String deleteProductQuery = "DELETE FROM products WHERE id = " + productId;
                dbEngine.executeSQL(deleteProductQuery);
                break;
            case 2: // Delete from orders table
                int orderId = random.nextInt(10000) + 1;
                String deleteOrderQuery = "DELETE FROM orders WHERE id = " + orderId;
                dbEngine.executeSQL(deleteOrderQuery);
                break;
        }
    }

    // Helper method to execute a complex SELECT query with WHERE, AND, OR, >, <,
    // LIKE
    private static void complexSelectQuery(Random random) {
        int tableChoice = random.nextInt(2); // Complex queries only on users and products for now
        String complexSelectQuery;
        switch (tableChoice) {
            case 0: // Complex SELECT on users
                int minAge = random.nextInt(20) + 20;
                int maxAge = minAge + random.nextInt(30);
                String city = getRandomCity(random);
                complexSelectQuery = "SELECT * FROM users WHERE age > " + minAge + " AND age < " + maxAge;
                break;
            case 1: // Complex SELECT on products
                double minPrice = 50 + (random.nextDouble() * 200);
                double maxPrice = minPrice + random.nextDouble() * 500;
                complexSelectQuery = "SELECT * FROM products WHERE price > " + minPrice + " AND price < " + maxPrice;
                break;
            case 2: // Complex SELECT on products
                double minPrice2 = 50 + (random.nextDouble() * 200);
                String category = getRandomCategory(random);
                complexSelectQuery = "SELECT * FROM products WHERE price > " + minPrice2 + " AND category = "
                        + category;
                break;
            default:
                complexSelectQuery = "SELECT * FROM users";
        }
        dbEngine.executeSQL(complexSelectQuery);
    }

    // Helper method to execute a complex UPDATE query with WHERE
    private static void complexUpdateQuery(Random random) {
        int tableChoice = random.nextInt(2); // Complex updates only on users and products for now
        switch (tableChoice) {
            case 0: // Complex UPDATE on users
                int newAge = random.nextInt(60) + 20;
                String city = getRandomCity(random);
                String updateUserQuery = "UPDATE users SET age = " + newAge + " WHERE city = '" + city + "'";
                dbEngine.executeSQL(updateUserQuery);
                break;
            case 1: // Complex UPDATE on products
                double newPrice = 50 + (random.nextDouble() * 1000);
                String category = getRandomCategory(random);
                String updateProductQuery = "UPDATE products SET price = " + newPrice + " WHERE category = '" + category
                        + "'";
                dbEngine.executeSQL(updateProductQuery);
                break;
        }
    }

    // Helper method to return a random city
    private static String getRandomCity(Random random) {
        String[] cities = { "New York", "Los Angeles", "Chicago", "Boston", "Miami", "Seattle", "Austin", "Dallas",
                "Atlanta", "Denver" };
        return cities[random.nextInt(cities.length)];
    }

    // Helper method to return a random category for products
    private static String getRandomCategory(Random random) {
        String[] categories = { "Electronics", "Appliances", "Clothing", "Furniture", "Toys", "Sports", "Books",
                "Beauty", "Garden" };
        return categories[random.nextInt(categories.length)];
    }
}