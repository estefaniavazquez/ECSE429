Feature: Manage projects relationship

  Background: The system is running and there is no data stored.
    Given the system is running
    And the system is initialized with no data

#  As a user, I want to assign a category instance to a project instance,
#  so I can classify my projects.

  Scenario Outline: User assigns an existing category to an existing project. (Normal flow)
    Given the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    And the following categories exist:
      | categoryId | categoryTitle | categoryDescription     |
      | 3          | School        | Tasks related to school |
    When the user assigns the category with id "<categoryId>" to the project with id "<projectId>"
    Then the entries in the relationship projects of the category with id "<categoryId>" now contain:
      | id |
      | 2  |
    And the response status code is "<statusCode>"
    Examples:
      | projectId | categoryId | statusCode |
      | 2         | 3          | 201        |

  Scenario Outline: User assigns an existing category, which has already been associated with a project, to a different existing project. (Alternate flow)
    Given the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
      | 3         | ECSE428 Project | false            | true          | Build app in Agile mode               |
    And the following categories exist:
      | categoryId | categoryTitle | categoryDescription     |
      | 3          | School        | Tasks related to school |
    And the entries in the relationship projects of the category with id "<categoryId>" contain:
      | id |
      | 2  |
    When the user assigns the category with id "<categoryId>" to the project with id "<projectId>"
    Then the entries in the relationship projects of the category with id "<categoryId>" now contain:
      | id |
      | 2  |
      | 3  |
    And the response status code is "<statusCode>"
    Examples:
      | projectId | categoryId | statusCode |
      | 3         | 3          | 201        |

  Scenario Outline: User assigns an inexistent category to an existing project. (Error flow)
    Given the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    And the following categories exist:
      | categoryId | categoryTitle | categoryDescription     |
      | 3          | School        | Tasks related to school |
    When the user assigns the category with id "<categoryId>" to the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"
    Examples:
      | projectId | categoryId | errorMessage                                                         | statusCode |
      | 2         | 100        | Could not find parent thing for relationship categories/100/projects | 404        |