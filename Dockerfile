FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.0-jdk-slim
COPY --from=build /target/Kusur-00.1-SNAPSHOT.jar Kusur.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","kusur.jar"]

