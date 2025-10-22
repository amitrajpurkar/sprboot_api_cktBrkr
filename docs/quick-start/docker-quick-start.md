# Docker Quick Start Guide

## 🚀 Get Started in 3 Steps

### 1. Start the Application
```bash
docker-compose up -d
```

### 2. Verify It's Running
```bash
curl http://localhost:8080/actuator/health
```

### 3. Access the Application
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

---

## 📋 Common Commands

### Start & Stop
```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Restart services
docker-compose restart

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### View Logs
```bash
# All services
docker-compose logs -f

# Application only
docker-compose logs -f app

# Last 100 lines
docker-compose logs --tail=100 app
```

### Check Status
```bash
# Service status
docker-compose ps

# Resource usage
docker stats

# Health check
curl http://localhost:8080/actuator/health
```

### Rebuild
```bash
# Rebuild and restart
docker-compose up -d --build

# Force rebuild (no cache)
docker-compose build --no-cache
```

---

## 🔧 Configuration

### Change Port
Edit `docker-compose.yml`:
```yaml
services:
  app:
    ports:
      - "9090:8080"  # Change 9090 to your desired port
```

### Increase Memory
Edit `docker-compose.yml`:
```yaml
services:
  app:
    environment:
      - JAVA_OPTS=-Xmx1g -Xms512m
```

### Enable Debug Logging
```bash
docker-compose up -d -e LOGGING_LEVEL_COM_ANR=DEBUG
```

---

## 🐛 Troubleshooting

### Application Won't Start
```bash
# Check logs
docker-compose logs app

# Check if MongoDB is running
docker-compose ps mongodb
```

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Or change port in docker-compose.yml
```

### MongoDB Connection Issues
```bash
# Test MongoDB
docker-compose exec mongodb mongosh --eval "db.runCommand({ ping: 1 })"

# Restart MongoDB
docker-compose restart mongodb
```

### Clean Everything
```bash
# Remove all containers, networks, and volumes
docker-compose down -v

# Remove images
docker rmi sprboot-api-cktbrkr:latest

# Start fresh
docker-compose up -d --build
```

---

## 📊 Monitoring

### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# MongoDB health
docker-compose exec mongodb mongosh --eval "db.adminCommand('ping')"
```

### Metrics
```bash
# Application metrics
curl http://localhost:8080/actuator/metrics

# Container stats
docker stats
```

---

## 🔐 Security

### Change Default Password
Edit `docker-compose.yml`:
```yaml
services:
  app:
    environment:
      - SPRING_SECURITY_USER_PASSWORD=your-secure-password
```

### Don't Expose MongoDB
Edit `docker-compose.yml`:
```yaml
services:
  mongodb:
    ports: []  # Remove this line to not expose MongoDB
```

---

## 📚 More Information

- Full documentation: [DOCKER.md](DOCKER.md)
- Implementation details: [src/main/docs/DOCKER_IMPLEMENTATION.md](src/main/docs/DOCKER_IMPLEMENTATION.md)
- Troubleshooting: See DOCKER.md

---

**Need Help?** Check the full [DOCKER.md](DOCKER.md) guide for detailed instructions.
