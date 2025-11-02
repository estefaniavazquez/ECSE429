@categories
Feature: Category Management API Testing
  As a user of the REST API Category Manager
  I want to be able to create, read, update, and delete categories
  So that I can classify and organize my todos and projects effectively

  Background:
    Given the system is running
    And my list of categories is cleared to start fresh

  @category_create
  Scenario Outline: Create a new category
    As a user, I want to create a new category with a title and optional description.
    # POST /categories
    When I send a request to create a category with these details:
      | title      | description       |
      | <title>    | <description>     |
    Then the category creation status should be <status_code>
    And the saved category should show field "title" with value "<expected_title>"
    And the saved category should show field "description" with value "<expected_description>"
    And the system should tell me if there was an error: "<expected_error_message>"

    Examples: Creation Flows (Normal, Alternate, Error)
      | title         | description         | status_code | expected_title  | expected_description | expected_error_message              |
      | Work          | Office tasks        | 201         | Work            | Office tasks         |                                    |
      | Personal      |                    | 201         | Personal        |                      |                                    |
      |               | Missing title test  | 400         |                 |                      | title : field is mandatory         |

  @category_update
  Scenario Outline: Update an existing category
    As a user, I want to modify the title or description of an existing category.
    # POST /categories/:id
    Given a category exists with title "Original Title" and description "Original Description"
    And its ID is stored as "category_id"
    When I send a request to update category "<target_id>" with body:
      | title        | description       |
      | <title>      | <description>     |
    Then the update status should be <status_code>
    And the updated category should show field "title" with value "<expected_title>"
    And the updated category should show field "description" with value "<expected_description>"
    And the system should tell me if there was an error: "<expected_error_message>"

    Examples: Update Flows (Normal, Alternate, Error)
      | target_id   | title           | description         | status_code | expected_title     | expected_description     | expected_error_message           |
      | category_id | Updated Title   |                     | 200         | Updated Title      | Original Description      |                                 |
      | category_id | Updated Title 2 | Updated Description | 200         | Updated Title 2    | Updated Description       |                                 |
      | 999         | New Title       | Any Desc            | 404         |                    |                          | Category not found.             |
      | abc         | Invalid ID test | Test                | 400         |                    |                          | Invalid ID format.              |

  @category_list
  Scenario Outline: Retrieve all categories
    As a user, I want to view a list of all categories that exist.
    # GET /categories
    When I send a request to view all categories in format "<accept_header>"
    Then the response status should be <status_code>
    And the response format should be "<expected_content_type>"
    And the list should contain at least <expected_min_count> categories
    And the system should tell me if there was an error: "<expected_error_message>"

    Examples: Retrieval Flows (Normal, Alternate, Error)
      | accept_header      | status_code | expected_content_type | expected_min_count | expected_error_message              |
      | application/json   | 200         | application/json      | 1                  |                                    |
      | application/xml    | 200         | application/xml       | 1                  |                                    |
      | text/plain         | 406         |                       | 0                  | Unsupported Accept header provided. |

  @category_view_one
  Scenario Outline: Retrieve a specific category by ID
    As a user, I want to view the details of a specific category.
    # GET /categories/:id
    Given a category exists with title "Single View Test" and description "Category for testing view"
    And its ID is stored as "view_category_id"
    When I send a request to view category "<target_id>"
    Then the response status should be <status_code>
    And the category details should contain field "title" with value "<expected_title>"
    And the system should tell me if there was an error: "<expected_error_message>"

    Examples: View-One Flows (Normal, Alternate, Error)
      | target_id       | status_code | expected_title       | expected_error_message            |
      | view_category_id| 200         | Single View Test     |                                    |
      | 999             | 404         |                      | Category not found.               |
      | invalid         | 400         |                      | Invalid ID format.                |

  @category_delete
  Scenario Outline: Delete a category
    As a user, I want to delete an existing category and verify its removal.
    # DELETE /categories/:id
    Given a category exists with title "Delete Target" and description "Temporary Category"
    And its ID is stored as "delete_category_id"
    When I send a request to delete category "<target_id>"
    Then the deletion status should be <status_code>
    And the system should tell me if there was an error: "<expected_error_message>"
    And when I verify deletion by requesting GET /categories/<target_id>, the response should be <check_status_code>

    Examples: Deletion Flows (Normal, Alternate, Error)
      | target_id         | status_code | expected_error_message | check_status_code |
      | delete_category_id| 204         |                        | 404               |
      | 999               | 404         | Category not found.     | 404               |
      | abc               | 400         | Invalid ID format.      | 400               |
