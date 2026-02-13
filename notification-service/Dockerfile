# ===============================
# Stage 1: Build the Application
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Create the Final Runtime Image
# ===============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# --- AIVEN CERTIFICATE IMPORT ---
# 1. Copy the Aiven CA certificate from our project into the container.
COPY aiven-ca.pem /tmp/aiven-ca.pem

# 2. Use Java's 'keytool' to import our certificate into the main truststore.
#    The default password for the Java cacerts file is 'changeit'.
RUN keytool -import -alias aiven-ca -keystore /etc/ssl/certs/java/cacerts -file /tmp/aiven-ca.pem -storepass changeit -noprompt

# --- END OF CERTIFICATE IMPORT ---

# Copy the built JAR from the first stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]