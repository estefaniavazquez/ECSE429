Feature: Manage todos/categories relationships

  Background: The system is running and there is no data stored.
    Given the Rest API Todo List Manager is running
    And the system is initialized with no data



  #  As a user, I want to assign a category instance to a todo instance,
  #  so I can organize my tasks.

  Scenario Outline: User assigns an existing category to an existing todo. (Normal flow)
    Given the following todos exist:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 3      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the following categories exist:
      | categoryId | categoryTitle | categoryDescription       |
      | 3          | "School"      | "Tasks related to school" |
    When the user assigns the category with id "<categoryId>" to the todo with id "<todoId>"
    Then the entries in the relationship todos of the category with id "<categoryId>" now contain:
      | id |
      | 3  |
    And the response status code is "<statusCode>"
    Examples:
      | todoId | categoryId | statusCode |
      | 3      | 3          | 201        |

  Scenario Outline: User assigns an existing todo to an existing category. (Alternate flow)
    Given the following todos exist:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 3      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the following categories exist:
      | categoryId | categoryTitle | categoryDescription       |
      | 3          | "School"      | "Tasks related to school" |
    When the user assigns the todo with id "<todoId>" to the category with id "<categoryId>"
    Then the entries in the relationship categories of the todo with id "<todoId>" now contain:
      | id |
      | 3  |
    And the entries in the relationship todos of the category with id "<categoryId>" now contain:
      | id |
      | 3  |
    And the response status code is "<statusCode>"
    Examples:
      | todoId | categoryId | statusCode |
      | 3      | 3          | 201        |

  Scenario Outline: User assigns an existing category to an inexistent todo. (Error flow)
    Given the following todos exist:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 3      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the following categories exist:
      | categoryId | categoryTitle | categoryDescription       |
      | 3          | "School"      | "Tasks related to school" |
    When the user assigns the category with id "<categoryId>" to the todo with id "<todoId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"
    Examples:
      | todoId | categoryId | errorMessage                                 | statusCode |
      | 100    | 3          | "Could not find thing matching value for id" | 404        |