####
# This Dockerfile is used in order to build a container that runs the Quarkus application in JVM mode
#
# Before building the docker image run:
#
# mvn package
#
# Then, build the image with:
#
# docker build -f src/main/docker/Dockerfile -t kemitix/slushy .
#
# Then run the container using:
#
# docker run -i --rm -p 9999:8080 kemitix/slushy
#
# If you want to include the debug port into your docker image
# you will have to expose the debug port (default 5005) like this :  EXPOSE 8080 5050
#
# Then run the container using :
#
# docker run -i --rm -p 9999:8080 -p 5005:5005 -e JAVA_ENABLE_DEBUG="true" kemitix/slushy
#
###
FROM adoptopenjdk:latest

WORKDIR /app

COPY target/lib /app/lib/
COPY target/slushy-runner.jar /app/

CMD ["java", "-jar", "/app/slushy-runner.jar"]
