# Build Stage
FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir -p /alten-sources
COPY pom.xml /alten-sources
COPY src /alten-sources/src
RUN mvn -f /alten-sources/pom.xml clean package

FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=build /alten-sources/target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]