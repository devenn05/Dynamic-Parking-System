# --- STAGE 1: Build the application ---
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace/app

# Copy Maven wrapper and dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY src src
RUN ./mvnw package -DskipTests

# --- STAGE 2: Run the application ---
FROM eclipse-temurin:17-jre-jammy

# Define where the Jar is
ARG JAR_FILE=/workspace/app/target/*.jar

# Copy the built Jar
COPY --from=build ${JAR_FILE} app.jar

# Render will pass the PORT env var, we expose 8080 for documentation
EXPOSE 8080

# Run command
ENTRYPOINT ["java", "-jar", "app.jar"]