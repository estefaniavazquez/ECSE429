# 0. Check server
curl -i http://localhost:4567/todos
# → HTTP/1.1 200 OK
# → {"todos":[{"id":"207","title":"baseline","doneStatus":"false","description":"desc"}]}

# 1. Create baseline todo
curl -s -H "Content-Type: application/json" -d '{"title":"baseline","description":"desc"}' http://localhost:4567/todos
# → {"id":"214","title":"baseline","doneStatus":"false","description":"desc"}

# 2. POST JSON
curl -i -H "Content-Type: application/json" -d '{"title":"json test","description":"desc"}' http://localhost:4567/todos
# → HTTP/1.1 201 Created

# 3. POST XML
curl -i -H "Content-Type: application/xml" -d '<todo><title>xml test</title><description>desc</description></todo>' http://localhost:4567/todos
# → HTTP/1.1 201 Created

# 4. HEAD /todos
curl -I http://localhost:4567/todos
# → HTTP/1.1 200 OK

# 5. OPTIONS /todos
curl -i -X OPTIONS http://localhost:4567/todos
# → Allow: OPTIONS, GET, HEAD, POST

# 6. PUT /todos (not allowed)
curl -i -X PUT http://localhost:4567/todos
# → HTTP/1.1 405 Method Not Allowed

# 7. DELETE /todos (not allowed)
curl -i -X DELETE http://localhost:4567/todos
# → HTTP/1.1 405 Method Not Allowed

# 8. Malformed JSON
curl -i -H "Content-Type: application/json" -d '{"title":' http://localhost:4567/todos
# → HTTP/1.1 400 Bad Request

# 9. Malformed XML
curl -i -H "Content-Type: application/xml" -d '<todo><title>' http://localhost:4567/todos
# → HTTP/1.1 400 Bad Request

# 10. GET baseline
curl -i http://localhost:4567/todos/200
# → HTTP/1.1 200 OK

# 11. PUT valid
curl -i -X PUT -H "Content-Type: application/json" -d '{"title":"updated"}' http://localhost:4567/todos/203
# → HTTP/1.1 200 OK

# 12. PUT malformed
curl -i -X PUT -H "Content-Type: application/json" -d '{"title"' http://localhost:4567/todos/203
# → HTTP/1.1 400 Bad Request

# 13. HEAD /todos/:id
curl -I http://localhost:4567/todos/200
# → HTTP/1.1 200 OK

# 14. OPTIONS /todos/:id
curl -i -X OPTIONS http://localhost:4567/todos/200
# → Allow: OPTIONS, GET, HEAD, POST, PUT, DELETE

# 15. DELETE /todos/:id
curl -i -X DELETE http://localhost:4567/todos/210
# → HTTP/1.1 200 OK

# 16. GET invalid id
curl -i http://localhost:4567/todos/invalid-id-123
# → HTTP/1.1 404 Not Found

# 17. Double DELETE
curl -i -X DELETE http://localhost:4567/todos/213
curl -i -X DELETE http://localhost:4567/todos/213
# → HTTP/1.1 404 Not Found

# 18. GET categories
curl -i http://localhost:4567/todos/200/categories
# → {"categories":[]}

# 19. OPTIONS categories
curl -i -X OPTIONS http://localhost:4567/todos/200/categories
# → Allow: OPTIONS, GET, HEAD, POST

# 20. HEAD categories
curl -I http://localhost:4567/todos/200/categories
# → HTTP/1.1 200 OK

# 21. GET tasksof
curl -i http://localhost:4567/todos/200/tasksof
# → {"projects":[]}

# 22. OPTIONS tasksof
curl -i -X OPTIONS http://localhost:4567/todos/200/tasksof
# → HTTP/1.1 200 OK

# 23. HEAD tasksof
curl -I http://localhost:4567/todos/200/tasksof
# → HTTP/1.1 200 OK

# 24. Cleanup
for id in $(curl -s http://localhost:4567/todos | grep -o '"id":"[^"]*' | cut -d'"' -f4); do
  if [ "$id" != "200" ]; then
    curl -s -X DELETE http://localhost:4567/todos/$id > /dev/null
  fi
done
# → cleanup done
