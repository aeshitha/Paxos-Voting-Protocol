# Define directories and classpath
SRC = src
BIN = bin
CLASSPATH = $(BIN)

# Source files
SOURCES = $(wildcard $(SRC)/paxos/**/*.java) $(wildcard $(SRC)/tests/*.java)

# Default target
all: clean build

# Build the project
build:
	javac -d $(BIN) -sourcepath $(SRC) $(SOURCES)
	@echo "Build completed."

# Clean the project
clean:
	rm -rf $(BIN)
	@echo "Cleaned build directory."

# Run specific test cases
test-simultaneous:
	@$(MAKE) clean
	@$(MAKE) build
	java -cp $(CLASSPATH) tests.PaxosTest testSimultaneousProposals

test-immediate:
	@$(MAKE) clean
	@$(MAKE) build
	java -cp $(CLASSPATH) tests.PaxosTest testImmediateResponses

test-varying:
	@$(MAKE) clean
	@$(MAKE) build
	java -cp $(CLASSPATH) tests.PaxosTest testVaryingProfiles

# Run the main program
run-main:
	@$(MAKE) clean
	@$(MAKE) build
	java -cp $(CLASSPATH) paxos.main.Main
