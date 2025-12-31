# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.tempo.core.auth.domain.model.UserTest"

# Run a single test method
./gradlew test --tests "com.tempo.core.auth.domain.model.UserTest.testMethodName"

# Run the application
./gradlew bootRun

# Clean build
./gradlew clean build

# Verify module structure (Spring Modulith)
./gradlew test --tests "com.tempo.core.ModulithTest.verifyModulith"

# Generate module documentation
./gradlew test --tests "com.tempo.core.ModulithTest.writeDocumentation"
```

## Architecture Overview

### Technology Stack
- **Java 21** with Virtual Threads enabled
- **Spring Boot 3.5.9** with Spring Modulith for modular architecture
- **PostgreSQL** with Flyway migrations (`src/main/resources/db/migration/`)
- **Redis** for session storage and rate limiting (Bucket4j)
- **MapStruct** for DTO mapping, **Lombok** for boilerplate reduction

### Module Structure (Spring Modulith)

The codebase follows a modular monolith architecture with strict module boundaries enforced by Spring Modulith:

```
com.tempo.core/
├── shared/      # Shared Kernel (OPEN module) - base entities, VOs, exceptions
├── auth/        # Authentication module - depends only on shared
├── product/     # Product catalog (placeholder)
├── order/       # Order management (placeholder)
├── inventory/   # Inventory management (placeholder)
└── notification/# Notifications (placeholder)
```

Module dependencies are declared in `package-info.java` files using `@ApplicationModule` annotations.

### Layer Structure per Module

Each business module follows DDD layers:
```
{module}/
├── application/     # Use cases, DTOs, mappers
│   ├── model/       # Request/Response DTOs
│   └── service/     # Application services
├── domain/          # Core business logic
│   ├── model/       # Entities, Aggregate Roots
│   ├── repository/  # Repository interfaces
│   ├── rule/        # Business rules (BusinessRule implementations)
│   └── event/       # Domain events
├── infrastructure/  # Technical implementations
│   └── persistence/ # JPA repository implementations
├── interfaces/      # Entry points
│   └── rest/        # REST controllers
└── job/             # Scheduled tasks
```

### Entity Hierarchy

Base classes in `shared.domain.entity`:
- `BaseEntity<ID>` - JPA auditing, optimistic locking (@Version)
- `BaseDomainEntity<ID>` - Adds `checkRule(BusinessRule)` for invariant enforcement
- `AggregateRoot<ID>` - Domain event support via `registerEvent(DomainEvent)`
- `AbstractTenantEntity<ID>` - Multi-tenant support with automatic tenant_id enforcement
- `TenantAggregateRoot<ID>` - Multi-tenant aggregate root (most common base for business entities)

### Multi-Tenancy

All tenant-scoped entities extend `AbstractTenantEntity` or `TenantAggregateRoot`:
- `tenant_id` column is mandatory and immutable
- Hibernate filter `tenantFilter` auto-applies tenant isolation on queries
- System tenant UUID: `00000000-0000-0000-0000-000000000000`
- Tenant context managed via `TenantContext` and resolved from authenticated session

### Business Rules Pattern

Domain invariants are implemented as `BusinessRule` classes:
```java
public class UsernameFormatRule implements BusinessRule {
    boolean isBroken();     // Check if rule is violated
    String getMessage();    // Error message
    String getCode();       // Error code for i18n
}
```
Use in entities: `checkRule(new SomeRule(value))` - throws `BusinessException` if broken.

### Authentication Flow

- Reference Token pattern with Redis session storage
- RBAC with Permission -> Role -> User hierarchy
- Session stored in Redis with `AuthSession` model
- Rate limiting on login via Bucket4j

### Value Objects

Located in `shared.domain.vo`:
- `Money` - Currency-aware monetary values (VND, USD)
- `Email` - Validated email address
- `PhoneNumber` - Validated phone number

### Exception Handling

Global exception handler in `shared.infrastructure.error.GlobalExceptionHandler` maps:
- `BusinessException` - 400 Bad Request with error code
- `EntityNotFoundException` - 404 Not Found
- `AuthenticationException` - 401 Unauthorized
- `AccessDeniedException` - 403 Forbidden
