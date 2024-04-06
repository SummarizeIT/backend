# Build stage
FROM maven:3.8-openjdk-17 AS build

WORKDIR /home/app

COPY pom.xml .
RUN mvn validate

COPY . .
RUN mvn package -DskipTests=true

# Package stage
FROM openjdk:21-ea-17-slim-buster
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]