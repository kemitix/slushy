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
FROM debian:stable

# install wget and python required to download and install calibre, plus a JRE
RUN apt-get update && \
    apt-get install -y \
        wget=1.20.1-1.1 \
        python3=3.7.3-1 \
        openjdk-11-jre \
    && rm -rf /var/lib/apt/lists/*

# download and install calibre
RUN wget -nv -O- \
    https://download.calibre-ebook.com/linux-installer.sh | \
    sh /dev/stdin version=5.19.0

WORKDIR /app

COPY target/quarkus-app/ /app/

EXPOSE 8080
CMD ["java", "-jar", "/app/quarkus-run.jar"]
