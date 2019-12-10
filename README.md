# distributed-db

Distributed database with multiversion concurrency control, deadlock detection, replication and failure recovery.

**Features:**

- Multiversion concurrency control,
- Deadlock detection,
- Replication,
- Failure recovery

---

## Running the code:

### Using reprozip file 'adb-project.rpz':

`reprounzip directory setup adb-project.rpz distributed-db`
`reprounzip directory run distributed-db/`

### Using maven:

1. Navigate to the directory `distributed-db`

1. Create the jar file with maven command:	`mvn package`. This creates a jar executable file. 
To run on a particular input file, run the command: `java -cp target/maven-unit-tests.jar adb.project.Main {pathToTestFiles}`

1. To test all test cases using maven, run the command: `mvn clean test`

1. To test a particular test with a TestClass using maven, run the command: `mvn -Dtest=TestClassName test`

**More Sample tests can be found in `src/test` directory**

## Main classes:

TransactionManager - Transaction manager
Transaction - Class for holding transaction info. Does not act on them.
DataManager - Data manager, middle man between TM and site. Basically Site manager.
Site - just holds info about site and updates it.