# Library Management System

REST API service untuk sistem manajemen perpustakaan dengan multi-layered security, role-based access control, MFA authentication, dan audit logging.

## 🚀 Tech Stack

- **Backend:** Spring Boot 3.x
- **Database:** PostgreSQL + Redis (caching)
- **Security:** Spring Security + JWT
- **Migration:** Flyway
- **Documentation:** Swagger/OpenAPI
- **Email:** MailHog (development)
- **Containerization:** Docker & Docker Compose

## ✨ Fitur Utama

### Security Features
- 🔐 JWT Authentication & Authorization
- 🛡️ Multi-Factor Authentication (MFA) via OTP
- 👥 Role-Based Access Control (RBAC)
- 🚦 Rate Limiting
- 📝 Audit Logging
- 🔒 Account Lock (setelah failed login attempts)

### Core Features
- 📚 CRUD Operations untuk Books, Users, dll
- 🔍 Bubble Sort Algorithm API (public endpoint)
- 📧 Email Notifications
- ⚡ Redis Caching
- 📊 Performance Analysis Tools

## 📋 Prerequisites

### Option 1: Docker (Recommended)
- Docker
- Docker Compose

### Option 2: Manual Setup
- Java 17+
- PostgreSQL 12+
- Redis
- Maven
- MailHog (untuk testing email)

## 🛠️ Setup & Installation

### 🐳 Option 1: Using Docker Compose (Recommended)

**1. Clone Repository**
```bash
git clone https://github.com/azizAb/library-management.git
cd library-management
```

**2. Start All Services**
```bash
docker-compose up -d
```

**3. Check Logs**
```bash
docker-compose logs -f backend
```

**4. Stop Services**
```bash
docker-compose down
```

**Services akan berjalan di:**
- 🌐 **Backend API:** http://localhost:8080
- 📖 **Swagger UI:** http://localhost:8080/swagger-ui.html
- 📊 **Redis Commander:** http://localhost:8081
- 📧 **MailHog UI:** http://localhost:8025
- 🗄️ **PostgreSQL:** localhost:5432
- 🔴 **Redis:** localhost:6379

---

## 🔑 Default Credentials

Check database migrations di `src/main/resources/db/migration/` untuk default users.

## 🏗️ Project Structure

```
com.aziz.library/
├── application/          # DTOs, Use Cases
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── usecase/
├── domain/              # Entities, Services
│   ├── model/
│   └── service/
├── infrastructure/      # Config, Security, Repository
│   ├── config/
│   ├── repository/
│   └── security/
└── presentation/        # Controllers
    └── controller/
```

Menggunakan **Clean Architecture** dengan pemisahan layer yang jelas.

## 🔧 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8080 |
| `SPRING_DATASOURCE_URL` | PostgreSQL URL | jdbc:postgresql://postgres:5432/library_db |
| `SPRING_DATASOURCE_USERNAME` | Database username | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Database password | postgres |
| `SPRING_DATA_REDIS_HOST` | Redis host | redis (docker) / localhost |
| `SPRING_DATA_REDIS_PORT` | Redis port | 6379 |
| `SPRING_MAIL_HOST` | SMTP host | mailhog (docker) / localhost |
| `SPRING_MAIL_PORT` | SMTP port | 1025 |
| `JWT_SECRET` | JWT secret key | - |
| `JWT_EXPIRATION` | JWT expiration (ms) | 86400000 (24h) |

## 🐳 Docker Commands

```bash
# Build and start all services
docker-compose up -d

# Rebuild backend service
docker-compose up -d --build backend

# View logs
docker-compose logs -f backend

# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes all data)
docker-compose down -v

# Access backend container
docker-compose exec backend sh
```

## 🔍 Troubleshooting

### Backend tidak bisa connect ke PostgreSQL
```bash
# Check PostgreSQL status
docker-compose ps postgres
docker-compose logs postgres
```

### Redis connection error
```bash
# Check Redis status
docker-compose logs redis
```

### Port sudah digunakan
```bash
# Check port usage
lsof -i :8080
lsof -i :5432
lsof -i :6379
```
