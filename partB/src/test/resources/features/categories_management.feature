@category
Feature: Category Management API Testing
    As a user of the Rest API Todo List Manager
    I want to create, read, update, replace, and delete categories
    So that I can organize my tasks effectively

    Background:
        Given the Rest API Todo List Manager is running
        And my list of categories is cleared to start fresh

    @category_create
    Scenario Outline: Create a new category with optional fields and check for errors
        As a user, I want to create a category with a title and an optional description to save the category.

        # POST /categories
        When I send a request to create a category with these details:
            | title       | description   |
            | <title>     | <description> |
        Then the category creation status should be <status_code>
        And the saved category should show field "title" with value "<expected_title>"
        And the saved category should show field "description" with value "<expected_description>"
        And the category error should contain "<expected_error_message>"

        Examples: Creation Flows (Normal, Alternate, Error)
            # Normal
            | title         | description           | status_code | expected_title | expected_description     | expected_error_message |
            | School Work   | Midterm preparation   | 201         | School Work    | Midterm preparation      |                        |
            # Alternate: omit description
            | Fitness       |                       | 201         | Fitness        |                          |                        |
            # Error: missing title
            |               | No title provided     | 400         |                |                          | title : field is mandatory |

    @category_update
    Scenario Outline: Update an existing category using POST /categories/{id}
        As a user, I want to change an existing category’s title and/or description.

        # Setup
        Given a category exists with title "POST Target Category" and description "Original Content"
        And its category ID is stored as "category_id"

        # POST /categories/<target_id>
        When I send a request to update category "<target_id>" with body:
            | title         | description          |
            | <title>       | <description>        |
        Then the category update status should be <status_code>
        And the updated category should show field "title" with value "<expected_title>"
        And the updated category should show field "description" with value "<expected_description>"
        And the category error should contain "<expected_error_message>"

        Examples: Partial Update Flows (Normal, Alternate, Error)
            # Normal: change title only
            | target_id  | title                  | description       | status_code | expected_title           | expected_description | expected_error_message |
            | category_id| Renamed Category       |                   | 200         | Renamed Category         | Original Content     |                        |
            # Alternate: change both
            | category_id| Study                  | Updated desc      | 200         | Study                    | Updated desc         |                        |
            # Error: non-existent
            | 999        | Whatever               |                   | 404         |                          |                      | categoryId : 999 was not found |

    @category_list
    Scenario Outline: Retrieve all categories and verify response format and count
        As a user, I want to retrieve the list of categories in a chosen format.

        # Setup: create 2 categories
        Given a category exists with title "List C1" and description "A"
        And a category exists with title "List C2" and description "B"

        # GET /categories
        When I send a request to view categories with requested format "<accept_header>"
        Then the status code should be <status_code>
        And the category list format should be "<expected_content_type>"
        And the list should contain <expected_count> categories
        And the category error should contain "<expected_error_message>"

        Examples: Retrieval Flows
            | accept_header     | status_code | expected_content_type | expected_count | expected_error_message |
            | application/json  | 200         | application/json      | 2              |                        |
            | application/xml   | 200         | application/xml       | 2              |                        |

    @category_delete
    Scenario Outline: Delete a category
        As a user, I want to delete an existing category.

        # Setup
        Given a category exists with title "Deletion Target" and description "To be removed"
        And its category ID is stored as "delete_cat_id"

        # DELETE /categories/<target_id>
        When I send a request to delete category "<target_id>" with requested format "<accept_header>"
        Then the category deletion status should be <status_code>
        And the category error should contain "<expected_error_message>"
        And the category with ID "<check_id>" should yield a "<check_status_code>" on a quick lookup

        Examples: Deletion Flows (Normal, Alternate, Error)
            | target_id     | accept_header     | status_code | expected_error_message | check_id       | check_status_code |
            | delete_cat_id | application/json  | 200         |                        | delete_cat_id  | 404               |
            | delete_cat_id | application/xml   | 200         |                        | delete_cat_id  | 404               |
            | 999           | application/json  | 404         | categoryId : 999 was not found | 999           | 404               |

    @category_put
    Scenario Outline: Fully replace a category using PUT /categories/{id}
        As a user, I want to replace a category’s entire data.

        # Setup
        Given a category exists with title "PUT Target Category" and description "Original Content"
        And its category ID is stored as "put_cat_id"

        # PUT /categories/<target_id>
        When I send a request to fully replace category "<target_id>" with body:
            | title        | description               |
            | <title>      | <description>             |
        Then the category replacement status should be <status_code>
        And the updated category should show field "title" with value "<expected_title>"
        And the updated category should show field "description" with value "<expected_description>"
        And the category error should contain "<expected_error_message>"

        Examples: Full Replacement Flows (Normal, Alternate, Error)
            | target_id  | title           | description                  | status_code | expected_title   | expected_description         | expected_error_message |
            | put_cat_id | Replaced Cat    | This category is replaced    | 200         | Replaced Cat     | This category is replaced    |                        |
            | put_cat_id | Minimal Cat     |                              | 200         | Minimal Cat      |                              |                        |
            | put_cat_id |                  | No title here                | 400         |                  |                              | title : field is mandatory |
