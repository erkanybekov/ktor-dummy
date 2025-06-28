# 🚀 Ktor Todo Application - Production Ready Deployment

## 📋 Overview

This is a comprehensive Docker setup for a Ktor-based Todo application with full production infrastructure including:

- **Application Stack**: Ktor API with JWT authentication
- **Database**: PostgreSQL with pgAdmin
- **Caching**: Redis with Redis Commander
- **Reverse Proxy**: Nginx with load balancing
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **File Storage**: MinIO (S3-compatible)
- **Management**: Portainer for Docker management

## 🛠️ Quick Start

### 1. Start All Services
```bash
./scripts/manage-services.sh start-all
```

### 2. Start Only Core Services
```bash
./scripts/manage-services.sh start-core
```

### 3. Check Status
```bash
./scripts/manage-services.sh status
```

## 🌐 Service URLs

| Service | URL | Default Credentials |
|---------|-----|-------------------|
| **Main App** | http://localhost:8080 | - |
| **Nginx Proxy** | http://localhost:80 | - |
| **pgAdmin** | http://localhost:5050 | admin@example.com / admin |
| **Redis Commander** | http://localhost:8081 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin / admin123 |
| **Kibana** | http://localhost:5601 | - |
| **MinIO Console** | http://localhost:9001 | admin / password123456789 |
| **Portainer** | https://localhost:9443 | - |

## 📊 Service Categories

### Core Services
- `postgres` - Main database
- `redis` - Caching layer
- `app` - Ktor application
- `nginx` - Reverse proxy

### Monitoring Stack
- `prometheus` - Metrics collection
- `grafana` - Metrics visualization

### Logging Stack
- `elasticsearch` - Log storage
- `kibana` - Log visualization
- `logstash` - Log processing

### Admin Tools
- `pgadmin` - Database management
- `redis-commander` - Redis management
- `portainer` - Docker management

### Storage
- `minio` - File storage (S3-compatible)

## 🔧 Management Commands

```bash
# Start specific service groups
./scripts/manage-services.sh start-core       # Core services only
./scripts/manage-services.sh start-monitoring # Monitoring stack
./scripts/manage-services.sh start-logging   # Logging stack
./scripts/manage-services.sh start-admin     # Admin interfaces

# General operations
./scripts/manage-services.sh restart-all     # Restart everything
./scripts/manage-services.sh stop-all        # Stop everything
./scripts/manage-services.sh logs           # View all logs
./scripts/manage-services.sh logs-app       # View app logs only
./scripts/manage-services.sh clean          # Clean up (WARNING: Removes data!)
```

## 🔐 Security Configuration

### Production Checklist

1. **Change Default Passwords** in `.env`:
   ```bash
   POSTGRES_PASSWORD=your-secure-password
   JWT_SECRET=your-super-secret-jwt-key-at-least-32-chars
   GRAFANA_ADMIN_PASSWORD=your-secure-password
   MINIO_ROOT_PASSWORD=your-secure-password
   ```

2. **Enable HTTPS** in nginx configuration:
   - Uncomment SSL configuration in `nginx/nginx.conf`
   - Add SSL certificates to `nginx/ssl/`

3. **Configure Firewall**:
   - Only expose port 80/443 to public
   - Keep admin interfaces (5050, 3000, 5601, etc.) internal

## 🏗️ Architecture

```
Internet → Nginx (80/443) → Ktor App (8080)
                                ↓
                          PostgreSQL (5432)
                                ↓
                            Redis (6379)
                                ↓
          Monitoring: Prometheus ← Grafana
                                ↓
           Logging: App → Logstash → Elasticsearch → Kibana
                                ↓
              File Storage: MinIO (9000)
```

## 📈 Monitoring Setup

### Grafana Dashboards
1. Access Grafana at http://localhost:3000
2. Login with admin/admin123
3. Prometheus datasource is pre-configured
4. Import dashboards for:
   - Application metrics
   - PostgreSQL metrics
   - Redis metrics
   - System metrics

### Prometheus Targets
- Application: `app:8080/metrics` (if implemented)
- Database: `postgres:5432`
- Cache: `redis:6379`

## 📝 Logging Setup

### ELK Stack Configuration
- **Elasticsearch**: Stores logs with daily indices
- **Logstash**: Processes and enriches logs
- **Kibana**: Provides log search and visualization

### Log Sources
- Application logs (JSON format recommended)
- Nginx access/error logs
- System logs (if node-exporter added)

## 💾 Data Persistence

All data is persisted using Docker volumes:
- `postgres_data` - Database data
- `redis_data` - Cache data
- `elasticsearch_data` - Log data
- `grafana_data` - Dashboard configurations
- `prometheus_data` - Metrics data
- `minio_data` - File storage

## 🔄 Backup Strategy

### Database Backup
```bash
# Create backup
docker exec ktor-todo-postgres pg_dump -U postgres ktor_todo > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore backup
docker exec -i ktor-todo-postgres psql -U postgres -d ktor_todo < backup_file.sql
```

### Full Volume Backup
```bash
# Stop services
./scripts/manage-services.sh stop-all

# Backup volumes
docker run --rm -v ktor-dummy_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .

# Restart services
./scripts/manage-services.sh start-all
```

## 🐛 Troubleshooting

### Common Issues

1. **Port Conflicts**: Check if ports are already in use
   ```bash
   netstat -tulpn | grep :8080
   ```

2. **Memory Issues**: ELK stack needs sufficient RAM (4GB+ recommended)
   ```bash
   # Reduce Elasticsearch memory
   ES_JAVA_OPTS="-Xms256m -Xmx256m"
   ```

3. **Permission Issues**: Ensure proper ownership
   ```bash
   sudo chown -R $USER:$USER .
   ```

### Health Checks
```bash
# Check service health
docker-compose ps

# Check application health
curl http://localhost:8080/health

# Check logs
./scripts/manage-services.sh logs-app
```

## 🌟 Performance Optimization

### Production Tuning

1. **PostgreSQL**: Tune `postgresql.conf` for your workload
2. **Redis**: Configure memory policies and persistence
3. **Nginx**: Enable caching and compression
4. **JVM**: Tune heap size for Ktor application

### Scaling Options

1. **Horizontal**: Add more app instances behind nginx
2. **Database**: Configure read replicas
3. **Cache**: Redis cluster setup
4. **Load Balancing**: Multiple nginx instances

## 📚 Additional Resources

- [Ktor Documentation](https://ktor.io/)
- [Docker Compose Reference](https://docs.docker.com/compose/)
- [PostgreSQL Tuning](https://wiki.postgresql.org/wiki/Tuning_Your_PostgreSQL_Server)
- [Nginx Configuration](https://nginx.org/en/docs/)
- [Prometheus Monitoring](https://prometheus.io/docs/)
- [ELK Stack Guide](https://www.elastic.co/guide/) 