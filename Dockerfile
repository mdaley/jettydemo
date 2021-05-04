FROM openjdk:16-jdk-alpine3.13

ARG JAR_FILE
ARG SERVER_PORT

RUN apk update

RUN mkdir -p /app

ADD ${JAR_FILE} /app/app.jar

ENTRYPOINT java -jar /app/app.jar
