# ==========================
# 1. Build Stage
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================
# 2. Run Stage
# ==========================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render sets PORT env var)
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
