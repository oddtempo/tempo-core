# Tempo Core

A multi-tenant backend service built with Spring Boot 3.5 and Spring Modulith, following Domain-Driven Design principles.

## Tech Stack

- **Java 21** with Virtual Threads
- **Spring Boot 3.5.9**
- **Spring Modulith 1.4.6** - Modular monolith architecture
- **PostgreSQL** - Primary database
- **Redis** - Session storage & rate limiting
- **Flyway** - Database migrations
- **MapStruct** - Object mapping
- **Bucket4j** - Rate limiting

## Getting Started

### Prerequisites

- Java 21+
- PostgreSQL
- Redis

### Configuration

Set environment variables or use defaults in `application.yml`:

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8080 | Server port |
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | tempo | Database name |
| `DB_USERNAME` | - | Database username |
| `DB_PASSWORD` | - | Database password |
| `REDIS_HOST` | localhost | Redis host |
| `REDIS_PORT` | 6379 | Redis port |
| `REDIS_PASSWORD` | - | Redis password |
| `BOOTSTRAP_ENABLED` | true | Auto-create super admin |
| `SUPER_ADMIN_USERNAME` | super_admin | Super admin username |
| `SUPER_ADMIN_PASSWORD` | 12345678 | Super admin password |

### Running the Application

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Run tests
./gradlew test
```

## Project Structure

```
src/main/java/com/tempo/core/
├── auth/           # Authentication & Authorization
├── product/        # Product catalog
├── order/          # Order management
├── inventory/      # Inventory management
├── notification/   # Notifications
├── shared/         # Shared kernel (base classes, utilities)
└── config/         # Application configuration
```

Each module follows DDD layered architecture:
- `application/` - Use cases, DTOs, services
- `domain/` - Entities, repositories, business rules
- `infrastructure/` - Technical implementations
- `interfaces/` - REST controllers

## Features

- **Multi-tenancy** - Tenant isolation via Hibernate filters
- **RBAC** - Role-based access control with permissions
- **Redis Sessions** - Reference token authentication
- **Rate Limiting** - Login attempt throttling
- **Domain Events** - Spring Modulith event publication

## License

Proprietary
