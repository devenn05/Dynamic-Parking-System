# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the JAR
COPY --from=build /app/target/*.jar app.jar

# Important: Render mounts secrets at /etc/secrets
# No need to COPY certificates into the image.

# Use a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Use the port you need (e.g., 8081)
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]