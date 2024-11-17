# Getting Started

## Running the CLI Tool

Assuming you are in the ROOT of this directory.

A makefile has been created with all the common commands needed to work with the Java code. To run each of the row based implementation, column based implementation, and dynamic column types implementation, you must first cd into the respective folders, then run make exec.

- For Row-Based, change the table type under line 50 of Engine.java, in the createTable method.
- For Col-Based, change column type under line 26 of Table.java, in the Table constructor. You may also change the type of HashMap used in CustomHashMapColumn, and the respective parameters (load factor, resizing factor, initial capacity, type of probing).
- For Dynamic Column Types, the data structures used are decided at runtime based on the datatype, so there is nothing to change here.

**NOTE: The makefile below only works on UNIX systems**

```bash
# cleans, builds and runs the project
make exec

# just compile/build
make build

# just run using previously compiled classes
make run
```

## Commands

### CREATE command

```bash
CREATE TABLE users (id, name, age, city)
```

### INSERT command

```bash
INSERT INTO users VALUES (1, 'John', 20, 'New York')
INSERT INTO users VALUES (2, 'Jane', 21, 'Los Angeles')
INSERT INTO users VALUES (3, 'Jim', 22, 'Chicago')
```

### SELECT command

```bash
SELECT * FROM users

SELECT id, city FROM users WHERE id = 1
```

### UPDATE command

```bash
UPDATE users SET name = Jack WHERE id = 1
```

### DELETE command

```bash
DELETE FROM users WHERE id = 1
```

# Experiment Results

## Changing Implementations

The areas to change implementations are marked with `// TODO:`. The steps on how to change the implementations are detailed above in this README.