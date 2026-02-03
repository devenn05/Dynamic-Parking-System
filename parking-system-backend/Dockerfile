# Stage 1: Build the application using Maven with Eclipse Temurin JDK 17
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the entire backend project into the container
COPY . .

# Run the Maven build command to produce the JAR file
RUN mvn clean package -DskipTests

# Stage 2: Create the final, lightweight image using Eclipse Temurin JRE 17
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy ONLY the built JAR file from the 'builder' stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "/app/app.jar"]