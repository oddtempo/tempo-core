# Tech Stack & Infrastructure

## Technical Summary

| Category | Technology | Version | Notes |
| -------- | ---------- | ------- | ----- |
| Runtime | Java | 21 | Virtual Threads enabled |
| Framework | Spring Boot | 3.5.9 | Latest stable |
| Modularity | Spring Modulith | 1.4.6 | Modular monolith architecture |
| Database | PostgreSQL | - | Primary persistence |
| Cache/Session | Redis | - | Session storage & rate limiting |
| Migrations | Flyway | - | Database version control |
| Mapping | MapStruct | 1.6.3 | DTO ↔ Entity mapping |
| Rate Limiting | Bucket4j | 8.14.0 | Login throttling |
| Build | Gradle | - | Kotlin DSL |

---

## Multi-Tenancy

- All tenant-scoped entities extend `TenantAggregateRoot`
- `tenant_id` column is **mandatory and immutable**
- Hibernate filter `tenantFilter` auto-applies tenant isolation
- **System Tenant UUID**: `00000000-0000-0000-0000-000000000000`

### Key Files
- `shared/infrastructure/tenant/TenantContext.java` - ThreadLocal tenant storage
- `shared/infrastructure/tenant/TenantFilter.java` - HTTP filter for tenant resolution

---

## Authentication & Authorization

### Reference Token Pattern
- Login → Store session in Redis → Return UUID token
- Request → Lookup session from Redis → Validate permissions

### RBAC Hierarchy
```
User → Role → Permission
```

| Entity | Table | Description |
| ------ | ----- | ----------- |
| `User` | `users` | User credentials and profile |
| `Role` | `roles` | Named role with permissions |
| `Permission` | `permissions` | Granular permission codes |
| `Tenant` | `tenants` | Tenant/organization |

---

## Exception Handling (RFC 7807)

| Exception | HTTP Status | Description |
| --------- | ----------- | ----------- |
| `BusinessException` | 400 | Business rule violations |
| `EntityNotFoundException` | 404 | Entity not found |
| `AuthenticationException` | 401 | Auth failures |
| `AccessDeniedException` | 403 | Permission denied |

---

## Database Migrations

Flyway migrations in `db/migration/`:

| Migration | Description |
| --------- | ----------- |
| `V1__init_modulith.sql` | Spring Modulith event_publication |
| `V2__auth_rbac.sql` | Auth tables: users, roles, permissions |
| `V3__add_audit_columns.sql` | JPA auditing columns |
| `V4__refactor_store_to_tenant.sql` | Rename store → tenant |

---

## Development Setup

### Prerequisites
- Java 21+, PostgreSQL, Redis

### Configuration

| Variable | Default | Description |
| -------- | ------- | ----------- |
| `SERVER_PORT` | 8080 | Server port |
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_NAME` | tempo | Database name |
| `REDIS_HOST` | localhost | Redis host |
| `BOOTSTRAP_ENABLED` | true | Auto-create super admin |

### Commands

```bash
./gradlew build        # Full build
./gradlew bootRun      # Run application
./gradlew test         # Run tests

# Verify module structure
./gradlew test --tests "com.tempo.core.ModulithTest.verifyModulith"
```
