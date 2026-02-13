# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the Aiven CA Certificate
COPY ca.pem /app/ca.pem

# Create a dedicated Truststore for the app (Password: securepass)
RUN keytool -import \
    -alias aiven \
    -file /app/ca.pem \
    -keystore /app/truststore.jks \
    -storepass securepass \
    -noprompt

# Copy App Jar
COPY --from=build /app/target/*.jar /app/
RUN sh -c 'find . -name "*.jar" -not -name "original-*.jar" -exec mv {} app.jar \;'

# User Permissions
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN chown appuser:appgroup /app/truststore.jks
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]