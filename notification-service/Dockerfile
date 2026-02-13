# ===============================
# Stage 1: Build the Application & Modify Truststore
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# --- AIVEN CERTIFICATE IMPORT (DONE IN THE BUILD STAGE) ---
# 1. Copy the Aiven CA certificate into the container.
COPY aiven-ca.pem /tmp/aiven-ca.pem

# 2. Use Java's 'keytool' to import our certificate into the JDK's main truststore.
#    $JAVA_HOME is a standard environment variable in this official image.
#    This command modifies the 'cacerts' file within this build stage.
RUN keytool -import -alias aiven-ca -keystore $JAVA_HOME/lib/security/cacerts -file /tmp/aiven-ca.pem -storepass changeit -noprompt
# --- END OF CERTIFICATE IMPORT ---

# Build the application using Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests


# ===============================
# Stage 2: Create the Final Runtime Image
# ===============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# --- COPY THE MODIFIED TRUSTSTORE ---
# Copy the 'cacerts' file (which now includes our Aiven cert) from the build stage
# into the final runtime image, overwriting the default one.
COPY --from=build $JAVA_HOME/lib/security/cacerts $JAVA_HOME/lib/security/cacerts
# --- END COPY ---

# Copy the application JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]