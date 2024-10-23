# Getting Started

## Running the CLI Tool

Assuming you are in the source folder of the Java files

```bash
javac -d . *.java && java edu.smu.smusql.Main
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
