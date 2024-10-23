package edu.smu.smusql;

import java.util.*;
import java.util.regex.*;
import edu.smu.smusql.table.DefaultTable;
import edu.smu.smusql.utils.Helper;

public class Engine {
    private Map<String, DefaultTable> database = new HashMap<>();

    public String executeSQL(String query) {
        if (query.matches("(?i)^CREATE\\s+TABLE\\s+.*")) {
            return create(query);
        } else if (query.matches("(?i)^INSERT\\s+INTO\\s+.*")) {
            return insert(query);
        } else if (query.matches("(?i)^SELECT\\s+.*")) {
            return select(query);
        } else if (query.matches("(?i)^UPDATE\\s+.*")) {
            return update(query);
        } else if (query.matches("(?i)^DELETE\\s+FROM\\s+.*")) {
            return delete(query);
        } else {
            return "ERROR: Unknown command";
        }
    }

    public String create(String query) {
        Pattern pattern = Pattern.compile("(?i)CREATE\\s+TABLE\\s+(\\w+)\\s*\\((.+)\\)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String[] columns = matcher.group(2).split("\\s*,\\s*");
            columns = Helper.trimQuotes(columns);
            database.put(tableName, new DefaultTable(Arrays.asList(columns)));
            return "Table " + tableName + " created with columns: " + String.join(", ", columns);
        }
        return "ERROR: Invalid CREATE statement";
    }

    public String insert(String query) {
        Pattern pattern = Pattern.compile("(?i)INSERT\\s+INTO\\s+(\\w+)\\s+VALUES\\s*\\((.+)\\)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String[] values = matcher.group(2).split("\\s*,\\s*");
            values = Helper.trimQuotes(values);
            DefaultTable table = database.get(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }
            table.insert(Arrays.asList(values));
            return "1 row inserted into " + tableName;
        }
        return "ERROR: Invalid INSERT statement";
    }

    public String select(String query) {
        Pattern pattern = Pattern.compile("(?i)SELECT\\s+(.+?)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String columns = matcher.group(1);
            String tableName = matcher.group(2);
            String conditions = matcher.group(3);

            DefaultTable table = database.get(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }

            List<String> selectedColumns = columns.equals("*") ? table.getColumns()
                    : Arrays.asList(columns.split("\\s*,\\s*"));

            return table.select(selectedColumns, conditions);
        }
        return "ERROR: Invalid SELECT statement";
    }

    public String update(String query) {
        Pattern pattern = Pattern.compile("(?i)UPDATE\\s+(\\w+)\\s+SET\\s+(.+?)(?:\\s+WHERE\\s+(.+))?$");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String updates = matcher.group(2);
            String conditions = matcher.group(3);

            DefaultTable table = database.get(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }

            Map<String, String> updateMap = new HashMap<>();
            for (String update : updates.split("\\s*,\\s*")) {
                String[] parts = update.split("\\s*=\\s*");
                updateMap.put(parts[0], Helper.trimQuotes(parts[1]));
            }

            List<String> conditionsList = conditions != null ? Arrays.asList(conditions.split("\\s+"))
                    : new ArrayList<>();

            int updatedRows = table.update(updateMap, conditionsList);
            return updatedRows + " row(s) updated in " + tableName;
        }
        return "ERROR: Invalid UPDATE statement";
    }

    public String delete(String query) {
        Pattern pattern = Pattern.compile("(?i)DELETE\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String conditions = matcher.group(2);

            DefaultTable table = database.get(tableName);
            if (table == null) {
                return "ERROR: Table " + tableName + " does not exist";
            }

            List<String> conditionsList = conditions != null ? Arrays.asList(conditions.split("\\s+"))
                    : new ArrayList<>();

            int deletedRows = table.delete(conditionsList);
            return deletedRows + " row(s) deleted from " + tableName;
        }
        return "ERROR: Invalid DELETE statement";
    }
}
