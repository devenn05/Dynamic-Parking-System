# --- STAGE 1: Build the application ---
# We use a standard Maven image, so you don't need 'mvnw' or '.mvn' in your repo
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /workspace/app

# Copy the pom.xml and download dependencies
# This is cached by Docker as long as your pom.xml doesn't change
COPY pom.xml .
RUN medical_search_wait=0 mvn dependency:go-offline

# Copy the actual source code
COPY src src

# Build the jar
RUN mvn package -DskipTests

# --- STAGE 2: Run the application ---
FROM eclipse-temurin:17-jre-jammy

# Define where the Jar is
ARG JAR_FILE=/workspace/app/target/*.jar

# Copy the built Jar from the build stage
COPY --from=build ${JAR_FILE} app.jar

# Render dynamic port binding
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]