# Todo API Test Suite (JUnit 4 + Maven)

This is a simple JUnit 4 test suite for the Thingifier Todo API. It runs against the server on http://localhost:4567 and uses plain Java. Tests are executed using Maven.

---

## Test Coverage

The tests check that the server is running and clean up after each test to keep results independent. They cover basic JSON and XML requests.

For `/todos`, the suite tests:  
- `GET` in both JSON and XML  
- `POST` for valid and malformed payloads  
- `HEAD` and `OPTIONS`  
- `PUT` and `DELETE` to confirm correct error codes since they’re not supported

For `/todos/:id`, the suite tests:  
- `GET` with valid and invalid IDs  
- `PUT` with valid and malformed data  
- `HEAD`, `OPTIONS`, and `DELETE` including double deletion  
- `PATCH` is skipped because Java’s built-in client does not support it

For `/todos/:id/categories` and `/todos/:id/tasksof`, the suite tests `GET`, `HEAD`, and `OPTIONS`.

---

## Requirements

- Java 8 or newer  
- Maven installed  
- JUnit 4 dependency included in `pom.xml`  
- Thingifier Todo API server running locally

You can download the server from the Thingifier GitHub releases page and start it with:

```bash
java -jar runTodoManagerRestAPI-1.5.5.jar
```

Make sure it’s reachable at http://localhost:4567.

---

## How to Run

From the project root folder:

```bash
mvn test
```

Maven will compile and run all JUnit 4 tests in the `tests` directory.

If everything works you should see something like:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running TodoApiTests_Extended
Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
```

---

## Example pom.xml Snippet

Make sure your `pom.xml` includes JUnit 4 as a test dependency:

```xml
<dependencies>
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
  </dependency>
</dependencies>

<build>
  <testSourceDirectory>todos/tests</testSourceDirectory>
</build>
```

---

## File Layout

```
ECSE429/
├── todos/
│   ├── tests/
│   │   ├── TodoApiTests_Extended.java
│   │   └── README.md
│   ├── pom.xml
│   └── lib/
│       ├── junit-4.13.2.jar
│       └── hamcrest-core-1.3.jar
└── runTodoManagerRestAPI-1.5.5.jar
```

---

Author: Najib Najib  
ECSE-429 — Fall 2025
