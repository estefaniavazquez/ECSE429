@todo
Feature: Todo Management API Testing
    As a user of the Rest API Todo List Manager
    I want to be able to create, read, update, and delete todo items
    So that I can manage my tasks effectively

    Background:
        # on localhost:4567
        Given the Rest API Todo List Manager is running
        And my list of tasks is cleared to start fresh

    Scenario Outline: Create a new todo task with optional fields and check for errors
        As a user, I want to create a new todo task with a title and an optional description to save the task to my list.
        # POST request to /todos
        When I send a request to create a task with these details:
            | title            | description                |  doneStatus         |
            | <title>          | <description>              |  <doneStatus>       |
        Then the status code should be <status_code>
        And the saved task should show field "title" with value "<expected_title>"
        And the saved task should show field "description" with value "<expected_description>"
        And the saved task should show field "doneStatus" with value "<expected_doneStatus>"
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Creation Flows (Normal, Alternate, Error)
            # Normal Flow: provide all fields correctly
            | title             | description             | doneStatus | status_code | expected_title   | expected_description      | expected_doneStatus | expected_error_message               |
            | Full testing      | Full testing task.      | false      | 201         | Full testing     | Full testing task.        | false               |                                      |
            # Alternate Flow: omit optional description
            | Quick task        |                         | true       | 201         | Quick task       |                           | true                |                                      |
            # Error Flow: missing title
            |                   | No title provided.      | false      | 400         |                  |                           |                     | title : field is mandatory          |

    Scenario Outline: Update an existing todo's doneStatus and/or description 
        As a user, I want to change the done status of an existing task and update its description so that the task's details are up to date.

        #Setup: create a todo item to be updated
        Given a todo item exists with title "POST Target Task", description "Original Content", and doneStatus "false"
        And its ID is stored as "target_todo_id"

        # POST request to /todos/{id}
        When I send a request to update task "<target_id>" with body:
            | description          | doneStatus       |
            | <description>        | <doneStatus>     |
        Then the status code should be <status_code>
        And the saved task should show field "title" with value "<expected_title>"
        And the saved task should show field "description" with value "<expected_description>"
        And the saved task should show field "doneStatus" with value "<expected_doneStatus>"
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Partial Update Flows (Normal, Alternate, Error)
            # We pass the key ("target_todo_id") to reference the created todo item's ID.
            # Normal Flow: toggle doneStatus from false to true
            | target_id       | doneStatus | description         | status_code | expected_title       | expected_description     | expected_doneStatus | expected_error_message                                    |
            | target_todo_id  | true       |                     | 200         | POST Target Task     | Original Content         | true                |                                                           |
            # Alternate Flow: update both description and doneStatus
            | target_todo_id  | false      | Updated description | 200         | POST Target Task     | Updated description      | false               |                                                           |
            # Error Flow: Attempt to update a non-existent item
            | 999             | true       |                     | 404         |                      |                          |                     | No such todo entity instance with GUID or ID null found   |

    Scenario Outline: Retrieve and filter Todos by done status
        As a user, I want to retrieve a list of todos filtered by their done status to get the items I need to work on.

        #Setup: create multiple todo items with varying done status
        Given a todo item exists with title "Completed Filter Test 1", description "This is done", and doneStatus "true"
        And a todo item exists with title "Completed Filter Test 2", description "This is also done", and doneStatus "true"
        And a todo item exists with title "Pending Filter Test 1", description "This is not done", and doneStatus "false"
        And a todo item exists with title "Pending Filter Test 2", description "This is also not done", and doneStatus "false"

        # Get request to /todos<query_params>
        When I send a request to view tasks filtered by the query "<query_params>" and requested format "<accept_header>"
        Then the status code should be <status_code>
        And the task list format should be "<expected_content_type>"
        And the list should contain <expected_count> tasks with completion status "<filter_status>"
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Retrieval and Filtering Flows (Normal, Alternate, Error)
            # Normal Flow: retrieve todos with doneStatus false in JSON
            | query_params       | accept_header        | status_code | expected_content_type | expected_count | filter_status | expected_error_message   |
            | ?doneStatus=false  | application/json     | 200         | application/json      | 2              | false         |                          |
            # Alternate Flow: retrieve todos with doneStatus false in XML
            | ?doneStatus=false  | application/xml      | 200         | application/xml       | 2              | false         |                          |
            # Error Flow: invalid doneStatus value
            | ?doneStatus=maybe  | application/json     | 400         | application/json      | 0              |               | Invalid doneStatus value |


    Scenario Outline: Delete a todo item
        As a user, I want to delete an existing todo item to get the task permanently removed from my active list.

        #Setup: create a todo item to be deleted. A new ID is needed for each run to test idempotency and 404.
        Given a todo item exists with title "Deletion Target", description "Task to be deleted", and doneStatus "false"
        And its ID is stored as "delete_todo_id"

        # DELETE request to /todos/<target_id>
        When I send a request to delete task "<target_id>" with requested format "<accept_header>"
        Then the status code should be <status_code>
        And the system should tell me if there was an error: "<expected_error_message>"
        And the task with ID "<target_id>" should yield a "<check_status_code>" on a quick check

        Examples: Deletion Flows (Normal, Alternate, Error)
            # Normal Flow: delete existing item
            | target_id          | accept_header      | status_code | check_id               | expected_error_message                          | check_status_code |
            | delete_todo_id     | application/json   | 200         | delete_todo_id         |                                                 | 404               |
            # Alternate Flow: delete another existing item
            | delete_todo_id     | application/xml    | 200         | delete_todo_id         |                                                 | 404               |
            # Error Flow: Attempt to delete a non-existent item
            | 999                | application/json   | 404         | 999                    | Could not find any instances with todos/null    | 404               |

    Scenario Outline: Fully replace a todo item's data
        As a user, I want to replace an existing todo item's entire data to get the task completely refreshed.

        #Setup: create a todo item to be replaced
        Given a todo item exists with title "PUT Target Task", description "Original Content", and doneStatus "false"
        And its ID is stored as "put_todo_id"

        # PUT request to /todos/<target_id>
        When I send a request to fully replace task "<target_id>" with body:
            | title          | description          | doneStatus     |
            | <title>        | <description>        | <doneStatus>   |
        Then the status code should be <status_code>
        And the saved task should show field "title" with value "<expected_title>"
        And the saved task should show field "description" with value "<expected_description>"
        And the saved task should show field "doneStatus" with value "<expected_doneStatus>"
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Full Replacement Flows (Normal, Alternate, Error)
            # Normal Flow: replace all fields correctly (all original data is overwritten)
            | target_id   | title             | description                  | doneStatus     | status_code | expected_title   | expected_description         | expected_doneStatus | expected_error_message               |
            | put_todo_id | Replaced Task     | This task has been replaced. | true           | 200         | Replaced Task    | This task has been replaced. | true                |                                      |

            # Alternate Flow: replace with minimal fields (description omitted)
            | put_todo_id | Minimal Task      |                              | false          | 200         | Minimal Task     |                              | false               |                                      |
            # Error Flow: missing title in replacement
            | put_todo_id |                   | No title in this replacement.| true           | 400         | PUT Target Task  | Original Content             | false               | title : field is mandatory            |