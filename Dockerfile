# Multi-stage build for generating frontend and building backend

# Stage 1: Build frontend generator
FROM node:20-alpine AS frontend-generator-builder
WORKDIR /app/frontend-generator
COPY frontend-generator/package*.json ./
RUN npm ci --only=production
COPY frontend-generator/ ./
RUN npm run build

# Stage 2: Generate frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app

# Copy generator
COPY --from=frontend-generator-builder /app/frontend-generator /app/frontend-generator

# Install generator dependencies and generate frontend
WORKDIR /app/frontend-generator
RUN npm run generate -- --url http://localhost:8080/api-docs --output /app/generated-frontend --name admin-dashboard

# Build frontend
WORKDIR /app/generated-frontend
RUN npm ci --only=production
RUN npm run build

# Stage 3: Build backend with frontend
FROM openjdk:17-jdk-alpine AS backend-builder
WORKDIR /app

# Copy backend source
COPY backend-sample/ ./

# Copy generated frontend build
COPY --from=frontend-builder /app/generated-frontend/dist ./src/main/resources/static/

# Build backend
RUN ./gradlew build -x test --no-daemon

# Stage 4: Production image
FROM openjdk:17-jre-alpine AS runtime

# Add application user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy jar file
COPY --from=backend-builder /app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]