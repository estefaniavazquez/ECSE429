## Overview

A Simple todo manager with categories and projects.

Will accept json by default.

Content-Type: application/json

Set Content-Type header to application/xml if you want to send in XML.

Content-Type: application/xml

You can control the returned data format by setting the Accept header

You can request XML response by setting the Accept header.

i.e. for XML use

Accept: application/xml

You can request JSON response by setting the Accept header.

i.e. for JSON use

Accept: application/json

Some requests can be filtered by adding query params of fieldname=value. Where only matching items will be returned.

e.g. /items?size=2&status=true

All data lives in memory and is not persisted so the application is cleared everytime you start it. It does have some test data in here when you start

## Model

### todo

Fields:
Fieldname Type Validation
id ID
Mandatory?: false
Example: "4"
title STRING
: can not be empty
Mandatory?: true
Example: "laboris nisi ut alia"
doneStatus BOOLEAN
Mandatory?: false
Example: "true"
description STRING
Mandatory?: false
Example: "do eiusmod tempor ia"

Example JSON Output from API calls

{
"id": "4",
"title": "laboris nisi ut alia",
"doneStatus": "true",
"description": "do eiusmod tempor ia"
}

Example XML Output from API calls

<todo>
    <doneStatus>true</doneStatus>
    <description>do eiusmod tempor ia</description>
    <id>4</id>
    <title>laboris nisi ut alia</title>
</todo>

Example JSON Input to API calls

{
"title": "laboris nisi ut alia",
"doneStatus": "true",
"description": "do eiusmod tempor ia"
}

Example XML Input to API calls

<todo>
    <doneStatus>true</doneStatus>
    <description>do eiusmod tempor ia</description>
    <id>null</id>
    <title>laboris nisi ut alia</title>
</todo>

### project

Fields:
Fieldname Type Validation
id ID
Mandatory?: false
Example: "31"
title STRING
Mandatory?: false
Example: "quis nostrud exercia"
completed BOOLEAN
Mandatory?: false
Example: "true"
active BOOLEAN
Mandatory?: false
Example: "true"
description STRING
Mandatory?: false
Example: "t non proident, sunt"

Example JSON Output from API calls

{
"id": "31",
"title": "quis nostrud exercia",
"completed": "true",
"active": "true",
"description": "t non proident, sunt"
}

Example XML Output from API calls

<project>
<active>true</active>
<description>t non proident, sunt</description>
<id>31</id>
<completed>true</completed>
<title>quis nostrud exercia</title>
</project>

Example JSON Input to API calls

{
"title": "quis nostrud exercia",
"completed": "true",
"active": "true",
"description": "t non proident, sunt"
}

Example XML Input to API calls

<project>
<active>true</active>
<description>t non proident, sunt</description>
<id>null</id>
<completed>true</completed>
<title>quis nostrud exercia</title>
</project>

### category

Fields:
Fieldname Type Validation
id ID
Mandatory?: false
Example: "4"
title STRING
: can not be empty
Mandatory?: true
Example: "nt mollit anim id es"
description STRING
Mandatory?: false
Example: "ut labore et dolorea"

Example JSON Output from API calls

{
"id": "4",
"title": "nt mollit anim id es",
"description": "ut labore et dolorea"
}

Example XML Output from API calls

<category>
    <description>ut labore et dolorea</description>
    <id>4</id>
    <title>nt mollit anim id es</title>
</category>

Example JSON Input to API calls

{
"title": "nt mollit anim id es",
"description": "ut labore et dolorea"
}

Example XML Input to API calls

<category>
    <description>ut labore et dolorea</description>
    <id>null</id>
    <title>nt mollit anim id es</title>
</category>

## Relationships

projects : category => project
categories : todo => category
todos : category => todo
tasks/tasksof : project =(tasks)=> todo / todo =(tasksof)=> project

## API

The API takes body with objects using the field definitions and examples shown in the model.
End Points
/todos

e.g. http://localhost:4567/todos

This endpoint can be filtered with fields as URL Query Parameters.

e.g. http://localhost:4567/todos?title=lit%20esse%20cillum%20dolo

    GET /todos
        return all the instances of todo

    HEAD /todos
        headers for all the instances of todo

    POST /todos
        we should be able to create todo without a ID using the field values in the body of the message

/todos/:id

e.g. http://localhost:4567/todos/:id

    GET /todos/:id
        return a specific instances of todo using a id

    HEAD /todos/:id
        headers for a specific instances of todo using a id

    POST /todos/:id
        amend a specific instances of todo using a id with a body containing the fields to amend

    PUT /todos/:id
        amend a specific instances of todo using a id with a body containing the fields to amend

    DELETE /todos/:id
        delete a specific instances of todo using a id

/todos/:id/tasksof

e.g. http://localhost:4567/todos/:id/tasksof

    GET /todos/:id/tasksof
        return all the project items related to todo, with given id, by the relationship named tasksof

    HEAD /todos/:id/tasksof
        headers for the project items related to todo, with given id, by the relationship named tasksof

    POST /todos/:id/tasksof
        create an instance of a relationship named tasksof between todo instance :id and the project instance represented by the id in the body of the message

/todos/:id/tasksof/:id

e.g. http://localhost:4567/todos/:id/tasksof/:id

    DELETE /todos/:id/tasksof/:id
        delete the instance of the relationship named tasksof between todo and project using the :id

/todos/:id/categories

e.g. http://localhost:4567/todos/:id/categories

    GET /todos/:id/categories
        return all the category items related to todo, with given id, by the relationship named categories

    HEAD /todos/:id/categories
        headers for the category items related to todo, with given id, by the relationship named categories

    POST /todos/:id/categories
        create an instance of a relationship named categories between todo instance :id and the category instance represented by the id in the body of the message

/todos/:id/categories/:id

e.g. http://localhost:4567/todos/:id/categories/:id

    DELETE /todos/:id/categories/:id
        delete the instance of the relationship named categories between todo and category using the :id

/projects

e.g. http://localhost:4567/projects

This endpoint can be filtered with fields as URL Query Parameters.

e.g. http://localhost:4567/projects?completed=false&active=true&title=ris%20nisi%20ut%20aliquipa

    GET /projects
        return all the instances of project

    HEAD /projects
        headers for all the instances of project

    POST /projects
        we should be able to create project without a ID using the field values in the body of the message

/projects/:id

e.g. http://localhost:4567/projects/:id

    GET /projects/:id
        return a specific instances of project using a id

    HEAD /projects/:id
        headers for a specific instances of project using a id

    POST /projects/:id
        amend a specific instances of project using a id with a body containing the fields to amend

    PUT /projects/:id
        amend a specific instances of project using a id with a body containing the fields to amend

    DELETE /projects/:id
        delete a specific instances of project using a id

/projects/:id/categories

e.g. http://localhost:4567/projects/:id/categories

    GET /projects/:id/categories
        return all the category items related to project, with given id, by the relationship named categories

    HEAD /projects/:id/categories
        headers for the category items related to project, with given id, by the relationship named categories

    POST /projects/:id/categories
        create an instance of a relationship named categories between project instance :id and the category instance represented by the id in the body of the message

/projects/:id/categories/:id

e.g. http://localhost:4567/projects/:id/categories/:id

    DELETE /projects/:id/categories/:id
        delete the instance of the relationship named categories between project and category using the :id

/projects/:id/tasks

e.g. http://localhost:4567/projects/:id/tasks

    GET /projects/:id/tasks
        return all the todo items related to project, with given id, by the relationship named tasks

    HEAD /projects/:id/tasks
        headers for the todo items related to project, with given id, by the relationship named tasks

    POST /projects/:id/tasks
        create an instance of a relationship named tasks between project instance :id and the todo instance represented by the id in the body of the message

/projects/:id/tasks/:id

e.g. http://localhost:4567/projects/:id/tasks/:id

    DELETE /projects/:id/tasks/:id
        delete the instance of the relationship named tasks between project and todo using the :id

/categories

e.g. http://localhost:4567/categories

This endpoint can be filtered with fields as URL Query Parameters.

e.g. http://localhost:4567/categories?description=olore%20magna%20aliqua.a

    GET /categories
        return all the instances of category

    HEAD /categories
        headers for all the instances of category

    POST /categories
        we should be able to create category without a ID using the field values in the body of the message

/categories/:id

e.g. http://localhost:4567/categories/:id

    GET /categories/:id
        return a specific instances of category using a id

    HEAD /categories/:id
        headers for a specific instances of category using a id

    POST /categories/:id
        amend a specific instances of category using a id with a body containing the fields to amend

    PUT /categories/:id
        amend a specific instances of category using a id with a body containing the fields to amend

    DELETE /categories/:id
        delete a specific instances of category using a id

/categories/:id/projects

e.g. http://localhost:4567/categories/:id/projects

    GET /categories/:id/projects
        return all the project items related to category, with given id, by the relationship named projects

    HEAD /categories/:id/projects
        headers for the project items related to category, with given id, by the relationship named projects

    POST /categories/:id/projects
        create an instance of a relationship named projects between category instance :id and the project instance represented by the id in the body of the message

/categories/:id/projects/:id

e.g. http://localhost:4567/categories/:id/projects/:id

    DELETE /categories/:id/projects/:id
        delete the instance of the relationship named projects between category and project using the :id

/categories/:id/todos

e.g. http://localhost:4567/categories/:id/todos

    GET /categories/:id/todos
        return all the todo items related to category, with given id, by the relationship named todos

    HEAD /categories/:id/todos
        headers for the todo items related to category, with given id, by the relationship named todos

    POST /categories/:id/todos
        create an instance of a relationship named todos between category instance :id and the todo instance represented by the id in the body of the message

/categories/:id/todos/:id

e.g. http://localhost:4567/categories/:id/todos/:id

    DELETE /categories/:id/todos/:id
        delete the instance of the relationship named todos between category and todo using the :id

/docs

e.g. http://localhost:4567/docs

    GET /http://localhost:4567/docs
        Show this documentation as HTML.

/shutdown

e.g. http://localhost:4567/shutdown

    GET /shutdown
        Shutdown the API server
