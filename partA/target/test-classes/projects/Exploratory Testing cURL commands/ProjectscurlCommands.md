curl -i http://localhost:4567/projects

curl -s -H "Content-Type: application/json" -d '{"title":"baseline", "completed":false,"active":false,"description":"desc"}' http://localhost:4567/projects

curl -i -H "Content-Type: application/json" -d '{"title":"json test","description":"desc"}' http://localhost:4567/projects

curl -i -H "Content-Type: application/xml" -d '<todo><title>xml test</title><description>desc</description></todo>' http://localhost:4567/projects

curl -I http://localhost:4567/projects

curl -i -X OPTIONS http://localhost:4567/projects

curl -i -X PUT http://localhost:4567/projects

curl -i -X DELETE http://localhost:4567/projects

curl -i -H "Content-Type: application/json" -d '{"title":' http://localhost:4567/projects

curl -i -H "Content-Type: application/xml" -d '<todo><title>' http://localhost:4567/projects

curl -i http://localhost:4567/projects/3

curl -i -X PUT -H "Content-Type: application/json" -d '{"title":"updated"}' http://localhost:4567/projects/3

curl -i -X PUT -H "Content-Type: application/json" -d '{"title"' http://localhost:4567/projects/3

curl -i -X POST -H "Content-Type: application/json" -d '{"title":"updatedPOST"}' http://localhost:4567/projects/3

curl -i -X POST -H "Content-Type: application/json" -d '{"title"' http://localhost:4567/projects/3

curl -I http://localhost:4567/projects/3

curl -i -X OPTIONS http://localhost:4567/projects/3

curl -i -X DELETE http://localhost:4567/projects/1

curl -i http://localhost:4567/projects/invalid-id-123

curl -i -X DELETE http://localhost:4567/projects/3
curl -i -X DELETE http://localhost:4567/projects/3

curl -i http://localhost:4567/projects/4/categories

curl -i -X OPTIONS http://localhost:4567/projects/4/categories

curl -I http://localhost:4567/projects/4/categories

curl -i http://localhost:4567/projects/4/tasks

curl -i -X OPTIONS http://localhost:4567/projects/4/tasks

curl -I http://localhost:4567/projects/4/tasks

