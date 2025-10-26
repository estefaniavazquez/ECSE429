# 0. Check server
curl -i http://localhost:4567/projects
# → HTTP/1.1 200 OK
# → {"projects":[{"id":"1","title":"Office Work","completed":"false","active":"false","description":"","tasks":[{"id":"1"},{"id":"2"}]}]}


# 1. Create baseline project
curl -s -H "Content-Type: application/json" -d '{"title":"baseline", "completed":false,"active":false,"description":"desc"}' http://localhost:4567/projects
# → {"id":"3","title":"baseline","completed":"false","active":"false","description":"desc"}

# 2. POST JSON
curl -i -H "Content-Type: application/json" -d '{"title":"json test","description":"desc"}' http://localhost:4567/projects
# → HTTP/1.1 201 Created

# 3. POST XML
curl -i -H "Content-Type: application/xml" -d '<todo><title>xml test</title><description>desc</description></todo>' http://localhost:4567/projects
# → HTTP/1.1 201 Created

# 4. HEAD /projects
curl -I http://localhost:4567/projects
# → HTTP/1.1 200 OK
# → Date: Sat, 04 Oct 2025 23:18:32 GMT
# → Content-Type: application/json
# → Transfer-Encoding: chunked
# → Server: Jetty(9.4.z-SNAPSHOT)

# 5. OPTIONS /projects
curl -i -X OPTIONS http://localhost:4567/projects
# → Allow: OPTIONS, GET, HEAD, POST

# 6. PUT /projects (not allowed)
curl -i -X PUT http://localhost:4567/projects
# → HTTP/1.1 405 Method Not Allowed

# 7. DELETE /projects (not allowed)
curl -i -X DELETE http://localhost:4567/projects
# → HTTP/1.1 405 Method Not Allowed

# 8. Malformed JSON
curl -i -H "Content-Type: application/json" -d '{"title":' http://localhost:4567/projects
# → HTTP/1.1 400 Bad Request
# → {"errorMessages":["java.io.EOFException: End of input at line 1 column 10 path $."]}

# 9. Malformed XML
curl -i -H "Content-Type: application/xml" -d '<todo><title>' http://localhost:4567/projects
# → HTTP/1.1 400 Bad Request
# → {"errorMessages":["Unclosed tag title at 14 [character 15 line 1]"]}

# 10. GET baseline
curl -i http://localhost:4567/projects/3
# → HTTP/1.1 200 OK
# → {"projects":[{"id":"3","title":"baseline","completed":"false","active":"false","description":"desc"}]}

# 11. PUT valid
curl -i -X PUT -H "Content-Type: application/json" -d '{"title":"updated"}' http://localhost:4567/projects/3
# → HTTP/1.1 200 OK
# → {"id":"3","title":"updated","completed":"false","active":"false","description":""}

# 12. PUT malformed
curl -i -X PUT -H "Content-Type: application/json" -d '{"title"' http://localhost:4567/projects/3
# → HTTP/1.1 400 Bad Request
# → {"errorMessages":["java.io.EOFException: End of input at line 1 column 9 path $."]}

# 13. POST valid
curl -i -X POST -H "Content-Type: application/json" -d '{"title":"updatedPOST"}' http://localhost:4567/projects/3
# → HTTP/1.1 200 OK
# → {"id":"3","title":"updatedPOST","completed":"false","active":"false","description":""}

# 14. POST malformed
curl -i -X POST -H "Content-Type: application/json" -d '{"title"' http://localhost:4567/projects/3
# → HTTP/1.1 400 Bad Request
# → {"errorMessages":["java.io.EOFException: End of input at line 1 column 9 path $."]}

# 15. HEAD /projects/:id
curl -I http://localhost:4567/projects/3
# → HTTP/1.1 200 OK

# 16. OPTIONS /projects/:id
curl -i -X OPTIONS http://localhost:4567/projects/3
# → Allow: OPTIONS, GET, HEAD, POST, PUT, DELETE

# 17. DELETE /projects/:id
curl -i -X DELETE http://localhost:4567/projects/1
# → HTTP/1.1 200 OK

# 18. GET invalid id
curl -i http://localhost:4567/projects/invalid-id-123
# → HTTP/1.1 404 Not Found
# → {"errorMessages":["Could not find an instance with projects/invalid-id-123"]}

# 19. Double DELETE
curl -i -X DELETE http://localhost:4567/projects/3
curl -i -X DELETE http://localhost:4567/projects/3
# → HTTP/1.1 404 Not Found
# → {"errorMessages":["Could not find any instances with projects/3"]}

# 20. GET categories
curl -i http://localhost:4567/projects/4/categories
# → {"categories":[]}

# 21. OPTIONS categories
curl -i -X OPTIONS http://localhost:4567/projects/4/categories
# → Allow: OPTIONS, GET, HEAD, POST

# 22. HEAD categories
curl -I http://localhost:4567/projects/4/categories
# → HTTP/1.1 200 OK

# 23. GET tasks
curl -i http://localhost:4567/projects/4/tasks
# → {"projects":[]}

# 24. OPTIONS tasks
curl -i -X OPTIONS http://localhost:4567/projects/4/tasks
# → HTTP/1.1 200 OK

# 25. HEAD tasks
curl -I http://localhost:4567/projects/4/tasks
# → HTTP/1.1 200 OK

# 26. Cleanup
for id in $(curl -s http://localhost:4567/projects | grep -o '"id":"[^"]*' | cut -d'"' -f4); do
  if [ "$id" != "200" ]; then
    curl -s -X DELETE http://localhost:4567/projects/$id > /dev/null
  fi