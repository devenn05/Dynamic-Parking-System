# --- STAGE 1: Build the application ---
# Use a JDK image to build our application
FROM eclipse-temurin:17-jdk-jammy AS build

# Set the working directory
WORKDIR /workspace/app

# Copy the Maven wrapper and pom.xml to leverage Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Package the application, skipping tests
RUN ./mvnw package -DskipTests

# --- STAGE 2: Create the final, smaller runtime image ---
# Use a JRE image which is smaller than the JDK
FROM eclipse-temurin:17-jre-jammy

# Set the argument for the JAR file
ARG JAR_FILE=/workspace/app/target/*.jar

# Copy the built JAR from the 'build' stage
COPY --from=build ${JAR_FILE} app.jar

# Expose the port the application will run on.
# Render will set the PORT environment variable dynamically.
# Spring Boot automatically listens on the port defined by this variable.
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]