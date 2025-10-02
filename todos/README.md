# Todo API Test Suite (JUnit)

This is a simple JUnit test suite for the Thingifier Todo API. It runs against the server on http://localhost:4567 and uses plain Java with HttpURLConnection. No Maven or Gradle needed.

---

## Test Coverage

The tests check that the server is running, and clean up after each test to keep results independent. They cover basic JSON and XML requests.

For /todos, the suite tests GET in both JSON and XML, POST for valid and malformed payloads, HEAD, OPTIONS, and verifies that PUT and DELETE return the proper error codes since they’re not supported.

For /todos/:id, it tests GET with valid and invalid IDs, PUT with both valid and malformed data, HEAD, OPTIONS, and DELETE including double deletion. PATCH is skipped because Java’s built-in client doesn’t support it.

The relationship endpoints /todos/:id/categories and /todos/:id/tasksof are tested for GET, HEAD, and OPTIONS.

---

## Requirements

- Java 8 or newer
- JUnit 4 jars in a lib folder
- Thingifier Todo API server running locally

You can download the server from the Thingifier GitHub releases page and start it like this:

```bash
java -jar runTodoManagerRestAPI-1.5.5.jar
```

Make sure it’s reachable at http://localhost:4567.

---

## How to Run

Go to the tests folder and compile the test file:

```bash
cd ECSE429/todos/tests
javac -cp ".:../lib/junit-4.13.2.jar:../lib/hamcrest-core-1.3.jar" TodoApiTests_Extended.java
```

Then run the tests:

```bash
java -cp ".:../lib/junit-4.13.2.jar:../lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore TodoApiTests_Extended
```

If everything works you should see something like:

```
JUnit version 4.13.2
........................
Time: 0.12

OK (24 tests)
```

---

## File Layout

```
ECSE429/
├── todos/
│   ├── tests/
│   │   ├── TodoApiTests_Extended.java
│   │   └── README.md
│   └── lib/
│       ├── junit-4.13.2.jar
│       └── hamcrest-core-1.3.jar
└── runTodoManagerRestAPI-1.5.5.jar
```

---

Author: Najib Najib  
ECSE-429 — Fall 2025
