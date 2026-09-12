# ==========================================
# Stage 1: Build Stage
# ==========================================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and configuration files first for dependency caching
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/

# Ensure gradlew has execute permissions
RUN chmod +x gradlew

# Download dependencies / warm up Gradle cache
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src/ src/

# Build the executable Spring Boot fat JAR (skipping tests during image packaging)
RUN ./gradlew bootJar --no-daemon -x test

# ==========================================
# Stage 2: Runtime Stage
# ==========================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create a non-root system user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/app.jar app.jar

# Expose default application port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
