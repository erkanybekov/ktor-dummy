# 🚀 Ktor Todo API with PostgreSQL Setup Guide

This is a production-ready REST API built with Ktor, featuring JWT authentication and PostgreSQL database.

## 🏗️ Architecture Overview

- **Framework**: Ktor 3.2.0
- **Database**: PostgreSQL with Exposed ORM
- **Authentication**: JWT with bcrypt password hashing
- **Connection Pooling**: HikariCP
- **Security**: CORS, CSRF protection, input validation

## 📋 Prerequisites

- JDK 17+
- Docker & Docker Compose (for PostgreSQL)
- curl or Postman (for testing)

## 🚀 Quick Start

### 1. Start PostgreSQL Database

```bash
# Start PostgreSQL and PgAdmin using Docker Compose
docker-compose up -d

# Check if database is running
docker-compose ps
```

This will start:
- **PostgreSQL** on port `5432`
- **PgAdmin** on port `5050` (optional database management UI)

### 2. Set Environment Variables (Optional)

Create a `.env` file or set environment variables:

```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/ktor_todo"
export DATABASE_USER="postgres"
export DATABASE_PASSWORD="password"
export JWT_SECRET="your-super-secret-jwt-key-change-in-production"
```

### 3. Start the Ktor Application

```bash
# Build and run the application
./gradlew run
```

The application will:
- ✅ Connect to PostgreSQL
- ✅ Create tables automatically
- ✅ Start on http://localhost:8080

## 🧪 Testing the API

### Health Check
```bash
curl http://localhost:8080/health
```

### User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:8080" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123",
    "name": "John Doe"
  }'
```

### User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:8080" \
  -d '{
    "email": "john@example.com", 
    "password": "SecurePass123"
  }'
```

### Create Todo (with JWT token)
```bash
# Replace YOUR_JWT_TOKEN with the token from login response
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Origin: http://localhost:8080" \
  -d '{
    "title": "Learn Ktor with PostgreSQL",
    "description": "Build production-ready APIs"
  }'
```

### Get User's Todos
```bash
curl -X GET http://localhost:8080/api/todos \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🗄️ Database Management

### Using PgAdmin (Web UI)
1. Go to http://localhost:5050
2. Login with:
   - Email: `admin@example.com`
   - Password: `admin`
3. Add server:
   - Host: `postgres` (Docker container name)
   - Port: `5432`
   - Database: `ktor_todo`
   - Username: `postgres`
   - Password: `password`

### Using psql (Command Line)
```bash
# Connect to PostgreSQL directly
docker exec -it ktor-todo-postgres psql -U postgres -d ktor_todo

# View tables
\dt

# View users
SELECT * FROM users;

# View todos
SELECT * FROM todos;
```

## 🔧 Configuration Options

The application supports configuration via:

### Environment Variables
- `DATABASE_URL` - PostgreSQL connection URL
- `DATABASE_USER` - Database username
- `DATABASE_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT signing
- `AUTO_CREATE_TABLES` - Whether to auto-create tables (default: true)

### application.yaml
Located in `src/main/resources/application.yaml`

## 🏗️ API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/verify` - Verify JWT token

### Todos (Protected - requires JWT)
- `GET /api/todos` - Get user's todos
- `GET /api/todos/{id}` - Get specific todo
- `POST /api/todos` - Create new todo
- `PUT /api/todos/{id}` - Update todo
- `DELETE /api/todos/{id}` - Delete todo

### System
- `GET /` - Welcome message
- `GET /health` - Health check with database status

## 🔒 Security Features

- **JWT Authentication** with configurable expiration
- **Password Hashing** using PBKDF2 with SHA-256
- **CORS Protection** with origin validation
- **CSRF Protection** for state-changing operations
- **Input Validation** for all user inputs
- **SQL Injection Protection** via Exposed ORM
- **Connection Pooling** with HikariCP

## 🚦 Error Handling

The API returns consistent error responses:

```json
{
  "success": false,
  "data": null,
  "message": "Error description",
  "errors": ["Detailed error messages"]
}
```

## 🛠️ Development

### Run Tests
```bash
./gradlew test
```

### Build for Production
```bash
./gradlew build
```

### Stop Services
```bash
# Stop the Ktor application: Ctrl+C

# Stop PostgreSQL
docker-compose down
```

## 🌟 What You've Built

Congratulations! You now have a **production-ready REST API** with:

✅ **JWT Authentication** with secure password hashing  
✅ **PostgreSQL Database** with connection pooling  
✅ **Comprehensive Security** (CORS, CSRF, validation)  
✅ **Modern Architecture** with clean separation of concerns  
✅ **Docker Integration** for easy database setup  
✅ **Health Monitoring** with database connectivity checks  
✅ **Input Validation** and proper error handling  
✅ **Best Practices** following industry standards  

This is **exactly how production APIs are built** in the real world! 🎉 