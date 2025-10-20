FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk
COPY --from=build /target/kusur-1.0-SNAPSHOT.jar Kusur.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","Kusur.jar"]

