Feature: Manage tasks/tasksof relationships

  Background: The system is running and there is no data stored.
    Given the Rest API Todo List Manager is running
    And the system is initialized with no data



  # As a user, I want to associate a todo instance with a project instance,
  # so I can include tasks to follow to complete that project.

  Scenario Outline: User associates an existing todo with an existing project. (Normal flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    When the user associates the todo with id "<todoId>" with the project with id "<projectId>"
    Then the entries in the relationship tasksof of the todo with id "<todoId>" now contain:
      | id |
      | 2  |
    And the response status code is "<statusCode>"
    Examples:
      | todoId | projectId | statusCode |
      | 3      | 2         | 201        |

  Scenario Outline: User assigns an existing todo, which has already been associated with a project, to a different existing project. (Alternate flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
      | 3         | ECSE428 Project | false            | true          | Build app in Agile mode               |
    And the entries in the relationship tasksof of the todo with id "<todoId>" contain:
      | id |
      | 2  |
    When the user associates the todo with id "<todoId>" with the project with id "<projectId>"
    Then the entries in the relationship tasksof of the todo with id "<todoId>" now contain:
      | id |
      | 2  |
      | 3  |
    And the response status code is "<statusCode>"
    Examples:
      | todoId | projectId | statusCode |
      | 3      | 3         | 201        |

  Scenario Outline: User associates an existing todo with an inexistent project. (Error flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    When the user associates the todo with id "<todoId>" with the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"
    Examples:
      | todoId | projectId | errorMessage                               | statusCode |
      | 3      | 100       | Could not find thing matching value for id | 404        |



#  As a user, I want to remove a todo instance from its associated project instance,
#  so I can exclude a task that is no longer required in that project.

  Scenario Outline: User removes an existing association between an existing todo and an existing project. (Normal flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
      | 3         | ECSE428 Project | false            | true          | Build app in Agile mode               |
    And the entries in the relationship tasksof of the todo with id "<todoId>" contain:
      | id |
      | 2  |
      | 3  |
    When the user removes the association between the todo with id "<todoId>" and the project with id "<projectId>"
    Then the entries in the relationship tasksof of the todo with id "<todoId>" now contain:
      | id |
      | 2  |
    And the response status code is "<statusCode>"
    Examples:
      | todoId | projectId | statusCode |
      | 3      | 3         | 200        |

  Scenario Outline: User deletes an existing todo that is associated with an existing project. (Alternate flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
      | 4      | Write tests       | false          | Write unit tests for the project        |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    And the entries in the relationship tasksof of the todo with id "<todoId>" contain:
      | id |
      | 2  |
    And the entries in the relationship tasks of the project with id "<projectId>" contain:
      | id |
      | 3  |
      | 4  |
    When the user deletes the todo with id "<todoId>"
    Then no instance of a todo with id "<todoId>" exists
    And the entries in the relationship tasks of the project with id "<projectId>" now contain:
      | id |
      | 3  |
    And the response status code is "<statusCode>"
    Examples:
      | todoId | projectId | statusCode |
      | 4      | 2         | 200        |

  Scenario Outline: User removes an existing association between an inexistent todo and an existing project. (Error flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    When the user removes the association between the todo with id "<todoId>" and the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"
    Examples:
      | todoId | projectId | errorMessage                                          | statusCode |
      | 100    | 2         | Could not find any instances with todos/100/tasksof/2 | 404        |



#  As a user, I want to get all the todo instances related to a project instance,
#  so I can keep track of the tasks that need to be done to complete that project.

  Scenario Outline: User gets the existing todos associated with an existing project. (Normal flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
      | 4      | Write tests       | false          | Write unit tests for the project        |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    And the entries in the relationship tasks of the project with id "<projectId>" contain:
      | id |
      | 3  |
      | 4  |
    When the user gets the todos associated with the project with id "<projectId>"
    Then the returned todos associated with the project with id "<projectId>" contain:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         | tasksofId |
      | 3      | Create repository | false          | Create the project repository on GitHub | 2         |
      | 4      | Write tests       | false          | Write unit tests for the project        | 2         |
    And the response status code is "<statusCode>"
    Examples:
      | projectId | statusCode |
      | 2         | 200        |

  Scenario Outline: User gets the existing todos associated with an existing project that has no todos associated. (Alternate flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | "est API for a project management app |
    When the user gets the todos associated with the project with id "<projectId>"
    Then the returned todos associated with the project with id "<projectId>" is empty
    And the response status code is "<statusCode>"
    Examples:
      | projectId | statusCode |
      | 2         | 200        |

  # BUG: The below scenario should return 404 with an error message but currently returns 200 with empty list of todos
  Scenario Outline: User gets the existing todos associated with an inexistent project. (Error flow)
    Given the following todos exist:
      | todoId | todoTitle         | todoDoneStatus | todoDescription                         |
      | 3      | Create repository | false          | Create the project repository on GitHub |
    And the following projects exist:
      | projectId | projectTitle    | projectCompleted | projectActive | projectDescription                    |
      | 2         | ECSE429 Project | false            | true          | Test API for a project management app |
    When the user gets the todos associated with the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"
    Examples:
      | projectId | errorMessage                                         | statusCode |
      | 100       | Could not find any instances with projects/100/tasks | 404        |