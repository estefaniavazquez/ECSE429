# Todo API Test Suite (JUnit 4 + Maven)

This project is a JUnit 4 test suite for the **Thingifier Todo API**.  
It runs against the local server at http://localhost:4567 and uses plain Java.  
All tests are executed using **Maven**.

---

## Test Coverage

The tests make sure the server starts correctly and clean up data after each run.  
They cover basic JSON and XML requests for the main API endpoints.

For `/todos`, the suite tests:  
- `GET` in JSON and XML  
- `POST` for valid and malformed payloads  
- `HEAD` and `OPTIONS`  
- `PUT`, `DELETE`, and `PATCH` to confirm correct error codes (these are not supported)

For `/todos/:id`, the suite tests:  
- `GET` with valid and invalid IDs  
- `PUT` with valid and malformed data  
- `HEAD`, `OPTIONS`, and `DELETE` including double deletion  
- `PATCH` requests return `405 Method Not Allowed`

For `/todos/:id/categories` and `/todos/:id/tasksof`, the suite tests:  
- `GET` requests for data  
- `HEAD` to check headers  
- `OPTIONS` to verify supported methods

---

## Requirements

- Java 8 or newer  
- Maven installed  
- JUnit 4 added to `pom.xml`  
- Thingifier Todo API JAR file available locally

⚡ The server is started **automatically** by the test suite before the tests run, so there is **no need to launch it manually**.

---

## How to Run

From the project root folder:

Navigate to todos folder:

`cd todos`

Then run this command

`mvn clean test`

and it should run, and at the end show this as an output

T E S T S
Running general.AllApiTests
Tests run: 56, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

---

## Author

Najib Najib  
ECSE-429 — Fall 2025
