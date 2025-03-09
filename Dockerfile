FROM openjdk:21-slim

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]