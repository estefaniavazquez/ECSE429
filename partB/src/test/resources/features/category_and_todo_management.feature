Feature: Manage todos/categories relationships

  Background: The system is running and there is no data stored.
    Given the system is running
    And there is no data stored



  #  As a user, I want to assign a category instance to a todo instance,
  #  so I can organize my tasks.

  Scenario: User assigns an existing category to an existing todo. (Normal flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing categories:
      | categoryId | categoryTitle | categoryDescription       |
      | 1          | "School"      | "Tasks related to school" |
    When the user assigns the category with id "<categoryId>" to the todo with id "<todoId>"
    Then an instance of a relationship todos exists in the category with id "<categoryId>" that points to the todo with id "<todoId>"
    And the entries in the relationship todos contain:
      | id |
      | 1  |
    And the response status code is "<statusCode>"

      | todoId | categoryId | statusCode |
      | 1      | 1          | 201        |

  Scenario: User assigns an existing todo to an existing category. (Alternate flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing categories:
      | categoryId | categoryTitle | categoryDescription       |
      | 1          | "School"      | "Tasks related to school" |
    When the user assigns the todo with id "<todoId>" to the category with id "<categoryId>"
    Then an instance of a relationship categories exists in the todo with id "<todoId>" that points to the category with id "<categoryId>"
    And the entries in the relationship categories of the todo with id "<todoId>" contain:
      | id |
      | 1  |
    And an instance of a relationship todos exists in the category with id "<categoryId>" that points to the todo with id "<todoId>"
    And the entries in the relationship todos of the category with id "<categoryId>" contain:
      | id |
      | 1  |
    And the response status code is "<statusCode>"

      | todoId | categoryId | statusCode |
      | 1      | 1          | 201        |

  Scenario: User assigns an existing category to an inexistent todo. (Error flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing categories:
      | categoryId | categoryTitle | categoryDescription       |
      | 1          | "School"      | "Tasks related to school" |
    When the user assigns the category with id "<categoryId>" to the todo with id "<todoId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"

      | todoId | categoryId | errorMessage                                 | statusCode |
      | 100    | 1          | "Could not find thing matching value for id" | 404        |