Feature: Create projects relationship

  Background: The system is running and there is no data stored.
    Given the system is running
    And there is no data stored

#  As a user, I want to assign a category instance to a project instance,
#  so I can classify my projects.

  Scenario: User assigns an existing category to an existing project. (Normal flow)
    Given the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    And the existing categories:
      | categoryId | categoryTitle | categoryDescription       |
      | 1          | "School"      | "Tasks related to school" |
    When the user assigns the category with id "<categoryId>" to the project with id "<projectId>"
    Then an instance of a relationship projects exists in the category with id "<categoryId>" that points to the project with id "<projectId>"
    And the entries in the relationship projects of the category with id "<categoryId> contain:
      | id |
      | 1  |
    And the response status code is "<statusCode>"

      | projectId | categoryId | statusCode |
      | 1         | 1          | 201        |

  Scenario: User assigns an existing category to an existing project that already has a different category assigned. (Alternate flow)
    Given the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    And the entries in the relationship projects of the category with id "<categoryId> contain:
      | id |
      | 1  |
    And the existing categories:
      | categoryId | categoryTitle | categoryDescription       |
      | 1          | "School"      | "Tasks related to school" |
      | 2          | "Work"        | "Tasks related to work"   |
    When the user assigns the category with id "<categoryId>" to the project with id "<projectId>"
    Then an instance of a relationship projects exists in the category with id "<categoryId>" that points to the project with id "<projectId>"
    And the entries in the relationship projects of the category with id "<categoryId> contain:
      | id |
      | 1  |
      | 2  |
    And the response status code is "<statusCode>"

      | projectId | categoryId | statusCode |
      | 1         | 2          | 201        |

  Scenario: User assigns an inexistent category to an existing project. (Error flow)
    Given the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    And the existing categories:
      | categoryId | categoryTitle | categoryDescription       |
      | 1          | "School"      | "Tasks related to school" |
    When the user assigns the category with id "<categoryId>" to the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"

      | projectId | categoryId | errorMessage                                 | statusCode |
      | 1         | 2          | "Could not find thing matching value for id" | 404        |