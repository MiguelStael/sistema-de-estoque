# =============================================================
# Stage 1: Build
# =============================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Cache Maven dependencies separately from source code.
# This layer is only invalidated when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# =============================================================
# Stage 2: Runtime
# =============================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Install curl for the HEALTHCHECK (minimal overhead on Alpine)
RUN apk add --no-cache curl

# Create a non-root user and group for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Ensure the non-root user owns the app files
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
