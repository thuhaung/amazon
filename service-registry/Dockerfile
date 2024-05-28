FROM maven:latest as builder
WORKDIR /amazon/api-gateway
COPY ./src /amazon/api-gateway/src
COPY ./pom.xml /amazon/api-gateway/pom.xml
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /amazon/api-gateway
COPY --from=builder /amazon/api-gateway/target/*.jar app.jar
ENTRYPOINT ["java", "-jar","app.jar"]