# 🚀 Ktor Todo API - Start, Stop & Deploy Guide

> **Clean Architecture Ktor Backend with PostgreSQL**
> 
> This guide covers everything you need to start, stop, build, and deploy your production-ready Ktor application.

## 📋 Table of Contents

- [Quick Start](#-quick-start)
- [Development Setup](#️-development-setup)
- [Production Build](#-production-build)
- [Deployment Options](#-deployment-options)
- [Shutdown Procedures](#-shutdown-procedures)
- [Troubleshooting](#-troubleshooting)

---

## 🏃 Quick Start

### **Option 1: Development Mode (Fastest)**
```bash
# 1. Start PostgreSQL
colima start
docker-compose up -d postgres

# 2. Start Ktor API
./gradlew run

# 3. Test API
curl http://localhost:8080/health
```

### **Option 2: Full Stack with PgAdmin**
```bash
# 1. Start everything
colima start
docker-compose up -d

# 2. Start Ktor API  
./gradlew run

# 3. Access services
# - API: http://localhost:8080
# - PgAdmin: http://localhost:5050 (admin@admin.com / admin)
```

---

## 🛠️ Development Setup

### **Prerequisites**
- **Java 17+** (`java -version`)
- **Docker** (via Colima: `brew install colima docker docker-compose`)
- **Git** (`git --version`)

### **First Time Setup**
```bash
# 1. Clone & Navigate
git clone <your-repo>
cd ktor-dummy

# 2. Install Docker Engine
colima start

# 3. Start PostgreSQL
docker-compose up -d postgres

# 4. Verify Database
docker exec -t ktor-todo-postgres psql -U postgres -d ktor_todo -c "SELECT version();"

# 5. Build Application
./gradlew build

# 6. Start Development Server
./gradlew run
```

### **Development Workflow**
```bash
# Hot reload development
./gradlew run --continuous

# Run tests
./gradlew test

# Check code style
./gradlew ktlintCheck

# Clean build
./gradlew clean build
```

---

## 📦 Production Build

### **1. Build JAR File**
```bash
# Create production JAR
./gradlew buildFatJar

# JAR location
ls -la build/libs/ktor-dummy-all.jar
```

### **2. Verify JAR**
```bash
# Check JAR contents
jar tf build/libs/ktor-dummy-all.jar | head -20

# Test JAR (requires PostgreSQL running)
java -jar build/libs/ktor-dummy-all.jar
```

### **3. Build with Environment**
```bash
# Production build with optimizations
./gradlew clean buildFatJar -Pprod

# Verify size (should be ~15-25MB)
du -h build/libs/ktor-dummy-all.jar
```

---

## 🌐 Deployment Options

### **Option 1: Docker Deployment**

**Create Dockerfile:**
```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app
COPY build/libs/ktor-dummy-all.jar app.jar

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

CMD ["java", "-jar", "app.jar"]
```

**Build & Run Docker:**
```bash
# Build image
docker build -t ktor-todo-api .

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL="jdbc:postgresql://host.docker.internal:5432/ktor_todo" \
  -e DATABASE_USER="postgres" \
  -e DATABASE_PASSWORD="password" \
  ktor-todo-api
```

### **Option 2: Traditional Server**
```bash
# 1. Copy JAR to server
scp build/libs/ktor-dummy-all.jar user@server:/opt/ktor-todo/

# 2. Create systemd service
sudo nano /etc/systemd/system/ktor-todo.service
```

**Systemd Service File:**
```ini
[Unit]
Description=Ktor Todo API
After=network.target

[Service]
Type=simple
User=ktor
WorkingDirectory=/opt/ktor-todo
ExecStart=/usr/bin/java -jar ktor-dummy-all.jar
Restart=always
RestartSec=10

# Environment variables
Environment=DATABASE_URL=jdbc:postgresql://localhost:5432/ktor_todo
Environment=DATABASE_USER=postgres
Environment=DATABASE_PASSWORD=your_secure_password
Environment=JWT_SECRET=your_super_secret_jwt_key_change_in_production

[Install]
WantedBy=multi-user.target
```

**Start Service:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable ktor-todo
sudo systemctl start ktor-todo
sudo systemctl status ktor-todo
```

### **Option 3: Cloud Platforms**

**Heroku:**
```bash
# Add Procfile
echo "web: java -jar build/libs/ktor-dummy-all.jar" > Procfile

# Deploy
heroku create your-app-name
heroku addons:create heroku-postgresql:mini
git push heroku main
```

**Railway/Render:**
```bash
# Build command
./gradlew buildFatJar

# Start command  
java -jar build/libs/ktor-dummy-all.jar
```

---

## 🛑 Shutdown Procedures

### **Development Shutdown**
```bash
# 1. Stop Ktor API (Ctrl+C or)
pkill -f "gradlew run"

# 2. Stop PostgreSQL
docker-compose down

# 3. Stop Docker engine (optional)
colima stop
```

### **Production Shutdown**
```bash
# Graceful shutdown (systemd)
sudo systemctl stop ktor-todo

# Docker shutdown
docker stop ktor-todo-api

# Emergency kill
pkill -f "ktor-dummy-all.jar"
```

### **Complete Cleanup**
```bash
# Remove all containers & volumes
docker-compose down -v

# Remove Docker VM completely
colima delete

# Clean build files
./gradlew clean
```

---

## 🔧 Environment Configuration

### **Required Environment Variables**
```bash
# Database
export DATABASE_URL="jdbc:postgresql://localhost:5432/ktor_todo"
export DATABASE_USER="postgres"
export DATABASE_PASSWORD="password"

# Security
export JWT_SECRET="your-super-secret-jwt-key-change-in-production"
export JWT_ISSUER="ktor-todo-api"
export JWT_AUDIENCE="ktor-todo-users"

# Server
export PORT="8080"
export HOST="0.0.0.0"
export DEVELOPMENT="false"
```

### **Production Environment File (`.env`)**
```bash
# Create .env file
cat > .env << EOF
DATABASE_URL=jdbc:postgresql://prod-db:5432/ktor_todo
DATABASE_USER=ktor_user
DATABASE_PASSWORD=super_secure_password
JWT_SECRET=ultra_secure_jwt_secret_256_bits_minimum
DEVELOPMENT=false
PORT=8080
EOF

# Load in production
set -a && source .env && set +a
java -jar ktor-dummy-all.jar
```

---

## 🔍 Troubleshooting

### **Common Issues**

**1. "Address already in use" (Port 8080)**
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>

# Or use different port
PORT=8081 ./gradlew run
```

**2. "Database connection failed"**
```bash
# Check PostgreSQL status
docker-compose ps

# Restart PostgreSQL
docker-compose restart postgres

# Check logs
docker-compose logs postgres
```

**3. "JAR file not found"**
```bash
# Rebuild JAR
./gradlew clean buildFatJar

# Check build output
ls -la build/libs/
```

**4. "Out of memory"**
```bash
# Increase JVM memory
java -Xms512m -Xmx1024m -jar ktor-dummy-all.jar
```

### **Health Checks**
```bash
# API Health
curl http://localhost:8080/health

# Database Health  
docker exec ktor-todo-postgres pg_isready -U postgres

# Detailed logs
docker-compose logs -f postgres
```

### **Performance Monitoring**
```bash
# JVM metrics
java -XX:+PrintGCDetails -jar ktor-dummy-all.jar

# Resource usage
docker stats ktor-todo-postgres

# API response times
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8080/health
```

---

## 📊 Quick Reference

### **Essential Commands**
```bash
# 🚀 Start everything
colima start && docker-compose up -d && ./gradlew run

# 🛑 Stop everything  
docker-compose down && colima stop

# 📦 Build production
./gradlew clean buildFatJar

# 🧪 Run tests
./gradlew test

# 🔍 Check health
curl http://localhost:8080/health
```

### **File Locations**
- **JAR File:** `build/libs/ktor-dummy-all.jar`
- **Config:** `src/main/resources/application.yaml`
- **Docker:** `docker-compose.yml`
- **Database:** PostgreSQL container volume

### **Default Ports**
- **API:** http://localhost:8080
- **PostgreSQL:** localhost:5432
- **PgAdmin:** http://localhost:5050

---

## 🎯 Production Checklist

- [ ] Environment variables configured
- [ ] Database credentials secured  
- [ ] JWT secret changed from default
- [ ] SSL/TLS certificates configured
- [ ] Monitoring & logging setup
- [ ] Backup strategy implemented
- [ ] Health checks working
- [ ] Load balancing configured
- [ ] Auto-restart on failure

---

**🎉 Your Clean Architecture Ktor API is production-ready!**

For issues or questions, check the logs first:
```bash
# Application logs
docker-compose logs api

# Database logs  
docker-compose logs postgres
``` 