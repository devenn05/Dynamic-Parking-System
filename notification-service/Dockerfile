# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 1. Copy the Aiven CA Certificate from your project folder
COPY ca.pem /app/ca.pem

# 2. Import it into the DEFAULT Java Keystore
# This makes Java trust Aiven natively without extra config
RUN keytool -import \
    -trustcacerts \
    -alias aiven-ca \
    -file /app/ca.pem \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit \
    -noprompt

COPY --from=build /app/target/*.jar /app/
RUN sh -c 'find . -name "*.jar" -not -name "original-*.jar" -exec mv {} app.jar \;'

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]