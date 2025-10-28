Feature: Categories management
  As a user
  I want to create categories
  So I can organize tasks

  Background:
    Given the Category API is reachable
    And my list of categories is cleared to start fresh

  # Normal flow: create with title + description
  Scenario: Create a category with title and description
    When I send a request to create a category with these details:
      | title       | description          |
      | School Work | Midterm preparation  |
    Then the category creation status should be 201
    And the saved category should show field "title" with value "School Work"
    And the saved category should show field "description" with value "Midterm preparation"

  # Alternate flow: description omitted
  Scenario: Create a category with only title
    When I send a request to create a category with these details:
      | title    | description |
      | Fitness  |             |
    Then the category creation status should be 201
    And the saved category should show field "title" with value "Fitness"
    And the saved category should show field "description" with value ""

  # Error flow: missing title
  Scenario: Fail to create a category without title
    When I send a request to create a category with these details:
      | title | description      |
      |       | no title present |
    Then the category creation status should be 400
    And the category error should contain "title : field is mandatory"
