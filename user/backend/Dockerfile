FROM maven:latest as builder
WORKDIR /amazon/user/backend
COPY ./src /amazon/user/backend/src
COPY ./pom.xml /amazon/user/backend/pom.xml
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /amazon/user/backend
COPY --from=builder /amazon/user/backend/target/*.jar app.jar
ENTRYPOINT ["java", "-jar","app.jar"]