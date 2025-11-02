@projects
Feature: Project Management API Testing
    As a user of the Rest API Project Manager
    I want to be able to create, read, update, and delete project items
    So that I can manage my high-level goals

    Background:
        # on localhost:4567
        Given the Rest API Todo List Manager is running
        And my list of tasks, projects and categories is cleared to start fresh

    @project_create
    Scenario Outline: Create a new project with optional fields and check for errors
        As a user, I want to create a new project, so I can track its status and description.
        # POST request to /projects
        When I send a request to create a project with these details:
            | title   | description   | active   |
            | <title> | <description> | <active> |
        Then the response status code is "<status_code>"
        And the project details should show field "title" with value "<expected_title>"
        And the project details should show field "description" with value "<expected_description>"
        And the project details should show field "active" with value "<expected_active>"
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Creation Flows (Normal, Alternate, Error)
            # Normal Flow: provide all fields correctly
            | title          | description           | active | status_code | expected_title | expected_description  | expected_active | expected_error_message |
            | Finish ECSE429 | Complete Part B and C | true   | 201         | Finish ECSE429 | Complete Part B and C | true            |                        |
            # Alternate Flow: omit fields (description, active)
            | New Project    |                       |        | 201         | New Project    |                       | false           |                        |
            # Error Flow: Invalid input
            |                |                       | maybe  | 400         |                |                       |                 |                        |

    @project_update_post
    Scenario Outline: Update an existing project's fields using POST
        As a user, I want to update the fields of existing projects, so I can keep track of updates done in projects.
        # POST request to /projects/{id}
        Given a project exists with title "POST Target Project", description "Original Content", and active "false"
        And its ID is stored as "project_id"

        When I send a request to update project "<target_id>" with body:
            | title   | description   | active   |
            | <title> | <description> | <active> |
        Then the response status code is "<status_code>"
        And the updated project should show field "title" with value "<expected_title>"
        And the updated project should show field "description" with value "<expected_description>"
        And the updated project should show field "active" with value "<expected_active>"
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Update Flows (Normal, Alternate, Error)
            # Normal Flow: update all fields
            | target_id | title                 | description         | active | status_code | expected_title        | expected_description | expected_active | expected_error_message                                    |
            | 1         | Updated Project Title | Updated Description | true   | 200         | Updated Project Title | Updated Description  | true            |                                                           |
            # Alternate Flow: update only active status
            | 1         |                       |                     | true   | 200         | POST Target Project   | Original Content     | true            |                                                           |
            # Error Flow: invalid ID (not found)
            | 999       |                       | New Description     | true   | 404         |                       |                      |                 | No such project entity instance with GUID or ID 999 found |

    @project_get_all
    Scenario Outline: Retrieve and filter all projects
        As a user, I want to view all projects that exist as well as filter them, so I can view all my current projects and filter them via query parameters.
        # GET request to /projects
        Given a project exists with title "Active Project", description "A", and active "true"
        And a project exists with title "Inactive Project", description "B", and active "false"

        When I send a request to view projects filtered by the query "<query_params>"
        Then the response status code is "<status_code>"
        And the list should contain <expected_count> projects
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Retrieval All and Filtering Flows (Normal, Alternate, Error)
            # Normal Flow: retrieve all projects (no filter)
            | query_params | status_code | expected_count | expected_error_message  |
            |              | 200         | 2              |                         |
            # Alternate Flow: filter for active projects
            | ?active=true | 200         | 1              |                         |
            # Error Flow: invalid filter value
            | ?active=foo  | 400         | 0              | Invalid query parameter |

    @project_delete
    Scenario Outline: Delete a project by ID
        As a user, I want to delete an existing project, so I can remove it once it is completed.
        # DELETE request to /projects/{id}
        Given a project exists with title "Deletion Target"
        And its ID is stored as "delete_project_id"
        And a project exists with title "Completed Project"
        And the project is marked as completed
        And its ID is stored as "completed_project_id"

        When I send a request to delete project "<target_id>"
        Then the response status code is "<status_code>"
        And the system should tell me if there was an error: "<expected_error_message>"
        And the project with ID "<target_id>" should yield a "<check_status_code>" on a quick check

        Examples: Deletion Flows (Normal, Error)
            # Normal Flow: delete existing item that has not been completed yet
            | target_id | status_code | expected_error_message                         | check_status_code |
            | 1         | 200         |                                                | 404               |
            # Alternate Flow: Attempt to delete a project that has been completed already
            | 2         | 200         |                                                | 404               |
            # Error Flow: Attempt to delete a project that never existed
            | 999       | 404         | Could not find any instances with projects/999 | 404               |


    @project_get_one
    Scenario Outline: Retrieve a single project by ID
        As a user, I want to view the details of only one project, so I can quickly query the project that I am working on currently.
        # GET request to /projects/{id}
        Given a project exists with title "Get Me", description "Project details here", and active "false"
        And its ID is stored as "get_project_id"
        And a project exists with title "Updated Project", description "Original description", and active "false"
        And the project field "active" is updated to "true"
        And its ID is stored as "updated_project_id"

        When I send a request to view project "<target_id>"
        Then the response status code is "<status_code>"
        And the project should show field "title" with value "<expected_title>"
        And the project should show field "description" with value "<expected_description>"
        And the project should show field "active" with value "<expected_active>"
        And the system should tell me if there was an error: "<expected_error_message>"

        Examples: Retrieval Flows (Normal, Alternate, Error)
            # Normal Flow: retrieve existing project that has not been updated
            | target_id | status_code | expected_title  | expected_description | expected_active | expected_error_message                       |
            | 1         | 200         | Get Me          | Project details here | false           |                                              |
            # Alternate Flow: retrieve existing project that has been updated after creation
            | 2         | 200         | Updated Project | Original description | true            |                                              |
            # Error Flow: retrieve non-existent project
            | 999       | 404         |                 |                      |                 | Could not find an instance with projects/999 |
