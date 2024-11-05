# Makefile for SMU SQL

# Define the source and output directories
SRC_DIR := ./src/main/java
OUT_DIR := ./out

# Find all Java files recursively (silently)
JAVA_FILES := $(shell find $(SRC_DIR) -name "*.java" 2>/dev/null)

build:
	@echo "Building project..."
	@mkdir -p $(OUT_DIR)
	@javac -d $(OUT_DIR) -cp $(SRC_DIR) $(JAVA_FILES)

run:
	@echo "Running project..."
	@java -cp $(OUT_DIR) edu.smu.smusql.Main

clean:
	@echo "Cleaning up..."
	@rm -rf $(OUT_DIR)

exec: clean build run
