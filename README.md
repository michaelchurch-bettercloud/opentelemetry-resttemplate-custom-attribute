This repository attempts to add a custom telemetry attribute to an external api call. 
It hits a Postman echo api to test this. 

This project requires java 16 to build/run
You can use `./gradlew bootJar` to build a jar at `build/libs/resttemplate-span-attributes-0.0.1-SNAPSHOT.jar`

This service exposes an endpoint at `GET http://localhost:8080` to test the functionality.

