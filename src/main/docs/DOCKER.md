# Docker Deployment Guide

This guide explains how to build and run the Spring Boot Circuit Breaker API using Docker and Docker Compose.

## Prerequisites

- Docker 20.10 or higher
- Docker Compose 2.0 or higher
- At least 2GB of available RAM

## Quick Start

### Using Docker Compose (Recommended)

1. **Build and start all services:**
   ```bash
   docker-compose up -d
   ```

2. **Check service status:**
   ```bash
   docker-compose ps
   ```

3. **View logs:**
   ```bash
   # All services
   docker-compose logs -f
   
   # Application only
   docker-compose logs -f app
   
   # MongoDB only
   docker-compose logs -f mongodb
   ```

4. **Access the application:**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/api-docs
   - Health Check: http://localhost:8080/actuator/health

5. **Stop services:**
   ```bash
   docker-compose down
   ```

6. **Stop and remove volumes (clean slate):**
   ```bash
   docker-compose down -v
   ```

## Building the Docker Image

### Build with Docker Compose
```bash
docker-compose build
```

### Build with Docker directly
```bash
docker build -t sprboot-api-cktbrkr:latest .
```

### Build with specific tag
```bash
docker build -t sprboot-api-cktbrkr:0.0.1-SNAPSHOT .
```

## Running the Application

### Option 1: Docker Compose (with MongoDB)
```bash
docker-compose up -d
```

### Option 2: Docker only (without MongoDB)
```bash
docker run -d \
  --name sprboot-api \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  sprboot-api-cktbrkr:latest
```

### Option 3: Docker with external MongoDB
```bash
docker run -d \
  --name sprboot-api \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATA_MONGODB_URI=mongodb://your-mongo-host:27017/sampledb \
  sprboot-api-cktbrkr:latest
```

## Configuration

### Environment Variables

The application supports the following environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `docker` | Active Spring profile |
| `SPRING_DATA_MONGODB_URI` | `mongodb://mongodb:27017/sampledb` | MongoDB connection URI |
| `SPRING_DATA_MONGODB_DATABASE` | `sampledb` | MongoDB database name |
| `SPRING_SECURITY_USER_NAME` | `user` | Basic auth username |
| `SPRING_SECURITY_USER_PASSWORD` | `password` | Basic auth password |
| `LOGGING_LEVEL_COM_ANR` | `INFO` | Application logging level |
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | JVM options |

### Custom Configuration

Create a custom `docker-compose.override.yml`:

```yaml
version: '3.8'

services:
  app:
    environment:
      - SPRING_SECURITY_USER_PASSWORD=your-secure-password
      - JAVA_OPTS=-Xmx1g -Xms512m
    ports:
      - "9090:8080"
```

Then run:
```bash
docker-compose up -d
```

## Docker Image Details

### Base Image
- **Build Stage:** `eclipse-temurin:21-jdk-alpine`
- **Runtime Stage:** `eclipse-temurin:21-jre-alpine`

### Features
- ✅ Multi-stage build for smaller image size
- ✅ Non-root user for security
- ✅ Health checks configured
- ✅ Optimized JVM settings for containers
- ✅ Java 21 virtual threads enabled
- ✅ Layer caching for faster builds

### Image Size
- Approximate size: ~300MB (runtime image)

## Health Checks

### Application Health
```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "circuitBreakers": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    },
    "mongo": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### MongoDB Health
```bash
docker-compose exec mongodb mongosh --eval "db.adminCommand('ping')"
```

## Troubleshooting

### Application won't start

1. **Check logs:**
   ```bash
   docker-compose logs app
   ```

2. **Verify MongoDB is running:**
   ```bash
   docker-compose ps mongodb
   ```

3. **Check MongoDB connectivity:**
   ```bash
   docker-compose exec app wget -O- http://localhost:8080/actuator/health
   ```

### MongoDB connection issues

1. **Verify MongoDB is healthy:**
   ```bash
   docker-compose exec mongodb mongosh --eval "db.runCommand({ ping: 1 })"
   ```

2. **Check network connectivity:**
   ```bash
   docker-compose exec app ping mongodb
   ```

### Port already in use

If port 8080 or 27017 is already in use, modify `docker-compose.yml`:

```yaml
services:
  app:
    ports:
      - "9090:8080"  # Change host port
  mongodb:
    ports:
      - "27018:27017"  # Change host port
```

### Out of memory

Increase memory limits in `docker-compose.yml`:

```yaml
services:
  app:
    environment:
      - JAVA_OPTS=-Xmx1g -Xms512m
    deploy:
      resources:
        limits:
          memory: 1.5G
```

## Production Deployment

### Security Recommendations

1. **Change default credentials:**
   ```yaml
   environment:
     - SPRING_SECURITY_USER_PASSWORD=${SECURE_PASSWORD}
   ```

2. **Use secrets management:**
   ```yaml
   secrets:
     - db_password
   ```

3. **Enable HTTPS:**
   - Use a reverse proxy (nginx, traefik)
   - Configure SSL certificates

4. **Restrict network access:**
   ```yaml
   mongodb:
     ports: []  # Don't expose MongoDB externally
   ```

### Performance Tuning

1. **Adjust JVM heap:**
   ```bash
   JAVA_OPTS=-Xmx2g -Xms1g
   ```

2. **Enable JVM optimizations:**
   ```bash
   JAVA_OPTS=-XX:+UseG1GC -XX:MaxGCPauseMillis=200
   ```

3. **Scale horizontally:**
   ```bash
   docker-compose up -d --scale app=3
   ```

## Monitoring

### View Resource Usage
```bash
docker stats
```

### View Application Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### Export Logs
```bash
docker-compose logs --no-color > application.log
```

## Cleanup

### Remove containers and networks
```bash
docker-compose down
```

### Remove containers, networks, and volumes
```bash
docker-compose down -v
```

### Remove images
```bash
docker rmi sprboot-api-cktbrkr:latest
```

### Complete cleanup
```bash
docker-compose down -v --rmi all
```

## Development Workflow

### Rebuild after code changes
```bash
docker-compose up -d --build
```

### Run tests before building
```bash
./gradlew clean test
docker-compose build
docker-compose up -d
```

### Debug mode
```bash
docker-compose run --service-ports app
```

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [MongoDB Docker Hub](https://hub.docker.com/_/mongo)

---

**Last Updated:** October 17, 2025  
**Docker Version:** 20.10+  
**Docker Compose Version:** 2.0+
