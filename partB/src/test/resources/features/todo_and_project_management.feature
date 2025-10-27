Feature: Manage tasks/taskof relationships

  Background: The system is running and there is no data stored.
    Given the system is running
    And there is no data stored



  # As a user, I want to associate a todo instance with a project instance,
  # so I can include tasks to follow to complete that project.

  Scenario: User associates an existing todo with an existing project. (Normal flow)
    Given the existing todos:
        | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
        | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing projects:
        | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
        | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    When the user associates the todo with id "<todoId>" with the project with id "<projectId>"
    Then an instance of a relationship taskof exists in the todo with id "<todoId>" that points to the project with id "<projectId>"
    And the entries in the relationship taskof contain:
      | id |
      | 1  |
    And the response status code is "<statusCode>"

      | todoId | projectId | statusCode |
      | 1      | 1         | 201        |

  Scenario: User assigns an existing todo, which has already been associated with a project, with a different existing project.  (Alternate flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the entries in the relationship taskof contain:
      | id |
      | 1  |
    And the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
      | 2         | "ECSE428 Project" | false            | true          | ""Build app in Agile mode"              |
    When the user associates the todo with id "<todoId>" with the project with id "<projectId>"
    Then an instance of a relationship taskof exists in the todo with id "<todoId>" that points to the project with id "<projectId>"
    And the entries in the relationship taskof contain:
      | id |
      | 1  |
      | 2  |
    And the response status code is "<statusCode>"

      | todoId | projectId | statusCode |
      | 1      | 1         | 201        |

  Scenario: User associates an existing todo with an inexistent project. (Error flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    When the user associates the todo with id "<todoId>" with the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"

      | todoId | projectId | errorMessage                                 | statusCode |
      | 1      | 100       | "Could not find thing matching value for id" | 404        |



#  As a user, I want to remove a todo instance from its associated project instance,
#  so I can exclude a task that is no longer required in that project.

  Scenario: User removes an existing association between an existing todo and an existing project. (Normal flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the entries in the relationship taskof of the todo with id "<todoId>" contain:
      | id |
      | 1  |
      | 2  |
    And the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
      | 2         | "ECSE428 Project" | false            | true          | ""Build app in Agile mode"              |
    When the user removes the association between the todo with id "<todoId>" and the project with id "<projectId>"
    Then no instance of a relationship taskof exists in the todo with id "<todoId>" that points to the project with id "<projectId>"
    And the entries in the relationship taskof of the todo with id "<todoId>" contain:
      | id |
      | 2  |
    And the response status code is "<statusCode>"

      | todoId | projectId | statusCode |
      | 1      | 1         | 200        |

  Scenario: User deletes an existing todo that is associated with an existing project. (Alternate flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
      | 2      | "Write tests"       | false          | "Write unit tests for the project"        |
    And the entries in the relationship taskof of the todo with id "<todoId>" contain:
      | id |
      | 1  |
    And the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    And the entries in the relationship tasks of the project with id "<projectIs>" contain:
      | id |
      | 1  |
      | 2  |
    When the user deletes the todo with id "<todoId>"
    Then no instance of a relationship taskof exists in the todo with id "<todoId>" that points to the project with id "<projectId>"
    And no instance of todo with id "<todoId>" exists
    And the entries in the relationship tasks of the project with id "<projectIs>" contain:
      | id |
      | 2  |
    And the response status code is "<statusCode>"

      | todoId | projectId | statusCode |
      | 1      | 1         | 200        |

  Scenario: User removes an existing association between an inexistent todo and an existing project. (Error flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    When the user removes the association between the todo with id "<todoId>" and the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"

      | todoId | projectId | errorMessage                                         | statusCode |
      | 100    | 1         | "Could not find an instance with todos/100/taskof/1" | 404        |



#  As a user, I want to get all the todo instances related to a project instance,
#  so I can keep track of the tasks that need to be done to complete that project.

  Scenario: User gets the existing todos associated with an existing project. (Normal flow)
    Given the existing todos:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
      | 2      | "Write tests"       | false          | "Write unit tests for the project"        |
    And the existing projects:
      | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
      | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    And the entries in the relationship tasks of the project with id "<projectId>" contain:
      | id |
      | 1  |
      | 2  |
    When the user gets the todos associated with the project with id "<projectId>"
    Then the returned todos associated with the project with id "<projectId> contain:
      | todoId | todoTitle           | todoDoneStatus | todoDescription                           | taskofId |
      | 1      | "Create repository" | false          | "Create the project repository on GitHub" | 1        |
      | 2      | "Write tests"       | false          | "Write unit tests for the project"        | 1        |
    And the response status code is "<statusCode>"

      | projectId | statusCode |
      | 1         | 200        |

  Scenario: User gets the existing todos associated with an existing project that has no todos associated. (Alternate flow)
    Given the existing todos:
        | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
        | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing projects:
        | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
        | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    When the user gets the todos associated with the project with id "<projectId>"
    Then the returned todos associated with the project with id "<projectId> is empty
    And the response status code is "<statusCode>"

      | projectId | statusCode |
      | 1         | 200        |

  Scenario: User gets the existing todos associated with an inexistent project. (Error flow)
    Given the existing todos:
        | todoId | todoTitle           | todoDoneStatus | todoDescription                           |
        | 1      | "Create repository" | false          | "Create the project repository on GitHub" |
    And the existing projects:
        | projectId | projectTitle      | projectCompleted | projectActive | projectDescription                      |
        | 1         | "ECSE429 Project" | false            | true          | "Test API for a project management app" |
    When the user gets the todos associated with the project with id "<projectId>"
    Then an error message "<errorMessage>" is returned
    And the response status code is "<statusCode>"

      | projectId | errorMessage                                         | statusCode |
      | 100       | "Could not find an instance with projects/100/tasks" | 404        |