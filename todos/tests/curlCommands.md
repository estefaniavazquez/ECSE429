curl -i http://localhost:4567/todos

curl -i -H "Content-Type: application/json" -d '{"title":"baseline","description":"desc"}' http://localhost:4567/todos

curl -i -H "Content-Type: application/json" -d '{"title":"json test","description":"desc"}' http://localhost:4567/todos

curl -i -H "Content-Type: application/xml" -d '<todo><title>xml test</title><description>desc</description></todo>' http://localhost:4567/todos

curl -I http://localhost:4567/todos

curl -i -X OPTIONS http://localhost:4567/todos

curl -i -X PUT -H "Content-Type: application/json" -d '{}' http://localhost:4567/todos

curl -i -X DELETE http://localhost:4567/todos

curl -i -H "Content-Type: application/json" -d '{"title":' http://localhost:4567/todos

curl -i -H "Content-Type: application/xml" -d '<todo><title>' http://localhost:4567/todos

curl -i http://localhost:4567/todos/{baseline_id}

curl -i -X PUT -H "Content-Type: application/json" -d '{"title":"updated"}' http://localhost:4567/todos/{some_id}

curl -i -X PUT -H "Content-Type: application/json" -d '{"title"' http://localhost:4567/todos/{bad_id}

curl -I http://localhost:4567/todos/{baseline_id}

curl -i -X OPTIONS http://localhost:4567/todos/{baseline_id}

curl -i -X DELETE http://localhost:4567/todos/{delete_id}

curl -i http://localhost:4567/todos/invalid-id-123

curl -i -X DELETE http://localhost:4567/todos/{double_id}
curl -i -X DELETE http://localhost:4567/todos/{double_id}

curl -i http://localhost:4567/todos/{baseline_id}/categories

curl -i -X OPTIONS http://localhost:4567/todos/{baseline_id}/categories

curl -I http://localhost:4567/todos/{baseline_id}/categories

curl -i http://localhost:4567/todos/{baseline_id}/tasksof

curl -i -X OPTIONS http://localhost:4567/todos/{baseline_id}/tasksof

curl -I http://localhost:4567/todos/{baseline_id}/tasksof
