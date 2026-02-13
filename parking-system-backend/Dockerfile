# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the app, skipping tests to speed up deployment
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR from the build stage. 
# We specificially look for the jar that does NOT start with 'original-' 
# to avoid copying the wrong file.
COPY --from=build /app/target/*.jar /app/

# Rename the copied jar to app.jar (handling the wildcard versioning)
RUN sh -c 'find . -name "*.jar" -not -name "original-*.jar" -exec mv {} app.jar \;'

# Create a non-root user for security (Best Practice on Render)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]