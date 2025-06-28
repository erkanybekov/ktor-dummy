# Build stage
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts ./
COPY src/ src/

# Build the application
RUN ./gradlew build --no-daemon

# Runtime stage - using eclipse-temurin instead of openjdk
FROM eclipse-temurin:17-jre

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r ktor && useradd -r -g ktor ktor

# Create app directory
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/build/libs/*-all.jar app.jar

# Change ownership to non-root user
RUN chown -R ktor:ktor /app
USER ktor

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Environment variables with defaults
ENV DATABASE_URL="jdbc:postgresql://postgres:5432/ktor_todo"
ENV DATABASE_USER="postgres"
ENV DATABASE_PASSWORD="password"
ENV JWT_SECRET="change-me-in-production"
ENV PORT="8080"
ENV HOST="0.0.0.0"

# JVM optimizations for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Start application
CMD java $JAVA_OPTS -jar app.jar 