FROM openjdk:21-slim

COPY gradlew gradlew

RUN ./gradlew bootJar

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
