# Getting Started

## Running the CLI Tool

Assuming you are in the ROOT of this directory.

A makefile has been created with all the common commands needed to work with the Java code.

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

## DEFAULT IMPL

```bash
Finished processing 1000000 queries.
Time elapsed: 2582.001823686 seconds

Finished processing 300000 queries.
Total time elapsed: 131.251024004 seconds
Total number of queries: 300000
Queries per second: 2285.696452858587
Average query time: 0.43750 milliseconds
Query type 0 took 0.01564 milliseconds on average, for 49620 queries
Query type 1 took 0.00020 milliseconds on average, for 49807 queries
Query type 2 took 0.45848 milliseconds on average, for 50126 queries
Query type 3 took 0.45393 milliseconds on average, for 50019 queries
Query type 4 took 1.16365 milliseconds on average, for 50282 queries
Query type 5 took 0.51879 milliseconds on average, for 50146 queries
Time elapsed: 131.277091955 seconds
```

