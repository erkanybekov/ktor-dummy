FROM openjdk:17-jre-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r ktor && useradd -r -g ktor ktor

# Create app directory
WORKDIR /app

# Copy JAR file
COPY build/libs/ktor-dummy-all.jar app.jar

# Change ownership to non-root user
RUN chown -R ktor:ktor /app
USER ktor

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
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