Feature: Todo Management API Testing
    As a user of the Rest API Todo List Manager
    I want to be able to create, read, update, and delete todo items
    So that I can manage my tasks effectively

    Background:
        Given the Rest API Todo List Manager is running on localhost:4567
        And the system is initialized with an empty todo list

    Scenario Outline: Create a new todo task with optional fields
        As a user, I want to create a new todo task with a title and an optional description to save the task.
        When I send a POST request to "/todos" with body:
            | title            | description                |  doneStatus         |
            | <title>          | <description>              |  <doneStatus>       |
        Then the response status code should be <status_code>
        And the response body should contain the value "<expected_title>" for the "title" field
        And the response body should contain the value "<expected_description>" for the "description" field
        And the response body should contain the value "<expected_doneStatus>" for the "doneStatus" field
        And the response body should confirm the error message "<expected_error_message>" when applicable

        Examples: Creation Flows (Normal, Alternate, Error)
            # Normal Flow: provide all fields correctly
            | title             | description             | doneStatus | status_code | expected_title   | expected_description      | expected_doneStatus | expected_error_message               |
            | Full testing      | Full testing task.      | false      | 201         | Full testing     | Full testing task.        | false               |                                      |
            # Alternate Flow: omit optional description
            | Quick task        |                         | true       | 201         | Quick task       |                           | true                |                                      |
            # Error Flow: missing title
            |                   | No title provided.      | false      | 400         |                  |                           |                     | "Title is required."                 |

    Scenario Outline: Toggle status or update fields using POST
        As a user, i want to toggle the doneStatus of an existing todo between true and false to reflect its completion status. (or update more fields)

        #Setup: create a todo item to be updated
        Given a todo item exists with title "POST Target Task", description "Original Content", and doneStatus "false"
        And its ID is stored as "todo_id"

        When I send a POST request to "/todos/<target_id>" with body:
            | description          | doneStatus       |
            | <description>        | <doneStatus>     |
        Then the response status code should be <status_code>
        And the response body should contain the value "<expected_title>" for the "title" field
        And the response body should contain the value "<expected_description>" for the "description" field
        And the response body should contain the value "<expected_doneStatus>" for the "doneStatus" field
        And the response body should confirm the error message "<expected_error_message>" when applicable

        Examples: Partial Update Flows (Normal, Alternate, Error)
            # Normal Flow: toggle doneStatus from false to true
            | target_id | doneStatus | description         | status_code | final_id | expected_title       | expected_description     | expected_doneStatus | expected_error_message |
            | todo_id   | true       |                     | 200         | todo_id  | POST Target Task     | Original Content         | true                |                        |
            # Alternate Flow: update both description and doneStatus
            | todo_id   | false      | Updated description | 200         | todo_id  | POST Target Task     | Updated description      | false               |                        |
            # Error Flow: Attempt to update a non-existent item
            | 999       | true       |                     | 404         | 999      |                      |                          |                     | "Todo item not found." |

    Scenario Outline: Retrieve and filter Todos by doneStatus
        As a user, I want to retrieve a list of todos filtered by their doneStatus to get the items I need to work on.

        #Setup: create multiple todo items with varying doneStatus
        Given a todo item exists with title "Completed Filter Test 1", description "This is done", and doneStatus "true"
        And a todo item exists with title "Completed Filter Test 2", description "This is also done", and doneStatus "true"
        And a todo item exists with title "Pending Filter Test 1", description "This is not done", and doneStatus "false"
        And a todo item exists with title "Pending Filter Test 2", description "This is also not done", and doneStatus "false"

        When I send a GET request to "/todos<query_params>" with Accept header "<accept_header>"
        Then the response status code should be <status_code>
        And the response Content-Type should be "<expected_content_type>"
        And the response body should contain <expected_count> todo items with doneStatus "<filter_status>"
        And the response body should confirm the error message "<expected_error_message>" when applicable

        Examples: Retrieval and Filtering Flows (Normal, Alternate, Error)
            # Normal Flow: retrieve todos with doneStatus false in JSON
            | query_params       | accept_header        | status_code | expected_content_type | expected_count | filter_status | expected_error_message |
            | ?doneStatus=false  | application/json     | 200         | application/json      | 2              | false         |                        |
            # Alternate Flow: retrieve todos with doneStatus false in XML
            | ?doneStatus=false  | application/xml      | 200         | application/xml       | 2              | false         |                        |
            # Error Flow: invalid doneStatus value
            | ?doneStatus=maybe  | application/json     | 400         | application/json      | 0              |               | "Invalid doneStatus value." |


    Scenario Outline: Delete an existing todo item
        As a user, I want to delete an existing todo item to get the task permanently removed from the system's active list.

        #Setup: create a todo item to be deleted. A new ID is needed for each run to test idempotency and 404.
        Given a todo item exists with title "Deletion Target"
        And its ID is stored as "delete_todo_id"

        When I send a DELETE request to "/todos/<target_id>" with Accept header "<accept_header>"
        Then the response status code should be <status_code>
        And the response body should confirm the error message "<expected_error_message>" when applicable
        And the todo item with ID "<target_id>" should yield a "<check_status_code>" on a HEAD request

        Examples: Deletion Flows (Normal, Alternate, Error)
            # Normal Flow: delete existing item
            | target_id          | accept_header      | status_code | check_id               | expected_error_message | check_status_code |
            | delete_todo_id     | application/json   | 204         | delete_todo_id         |                        | 404               |
            # Alternate Flow: delete another existing item
            | delete_todo_id     | application/xml    | 204         | delete_todo_id         |                        | 404               |
            # Error Flow: Attempt to delete a non-existent item
            | 999                | application/json   | 404         | 999                    | "Todo item not found." | 404               |

    Scenario Outline: Replace an existing todo's entire data using PUT
        As a user, I want to replace an existing todo item's entire data to get the task completely refreshed.

        #Setup: create a todo item to be replaced
        Given a todo item exists with title "PUT Target Task", description "Original Content", and doneStatus "false"
        And its ID is stored as "put_todo_id"

        When I send a PUT request to "/todos/<target_id>" with body:
            | title          | description          | doneStatus     |
            | <title>        | <description>        | <doneStatus>   |
        Then the response status code should be <status_code>

        And the todo item with ID "<target_id>" should contain the value "<expected_title>" for the "title" field
        And the todo item with ID "<target_id>" should contain the value "<expected_description>" for the "description" field
        And the todo item with ID "<target_id>" should contain the value "<expected_doneStatus>" for the "doneStatus" field
        And the response body should confirm the error message "<expected_error_message>" when applicable

        Examples: Full Replacement Flows (Normal, Alternate, Error)
            # Normal Flow: replace all fields correctly (all original data is overwritten)
            | target_id   | title             | description                  | doneStatus     | status_code | expected_title   | expected_description         | expected_doneStatus | expected_error_message               |
            | put_todo_id | Replaced Task     | This task has been replaced. | true           | 200         | Replaced Task    | This task has been replaced. | true                |                                      |

            # Alternate Flow: replace with minimal fields (description omitted)
            | put_todo_id | Minimal Task      |                              | false          | 200         | Minimal Task     |                              | false               |                                      |
            # Error Flow: missing title in replacement
            | put_todo_id |                   | No title in this replacement.| true           | 400         | PUT Target Task  | Original Content             | false               | "Title is required."                 |