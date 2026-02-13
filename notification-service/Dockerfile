# ===============================
# Stage 1: Build the Application
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies
# This layer is cached if pom.xml doesn't change, making re-builds faster
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application (Skipping tests to speed up deployment)
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Run the Application
# ===============================
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Create a non-root user for security (Good practice for Render)
RUN addgroup -S render && adduser -S render -G render
USER render

# Copy the built JAR file from the build stage
# Note: We use a wildcard *.jar to match whatever version you set in pom.xml
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]