# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /hackathon-APP
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /hackathon-APP

# Create a non-root user for security
RUN addgroup -S spring && adduser -S himangshu -G spring


COPY --from=build --chown=himangshu:spring /hackathon-APP/target/*.jar app.jar


USER himangshu:spring

# Standard Spring Boot port
EXPOSE 8086

ENTRYPOINT ["sh","-c","ping google.com && java -jar app.jar --spring.profiles.active=test"]
