# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build and verify, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built jar
COPY --from=build /app/target/*.jar /app/
# Rename for consistency
RUN sh -c 'find . -name "*.jar" -not -name "original-*.jar" -exec mv {} app.jar \;'

# Security: Non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]