1# CLAUDE.md

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
- **Spring Boot 3.5.9** with Spring Modulith 1.4.6 for modular architecture
- **PostgreSQL** with Flyway migrations (`src/main/resources/db/migration/`)
- **Redis** for session storage and rate limiting (Bucket4j 8.14.0)
- **MapStruct 1.6.3** for DTO mapping, **Lombok** for boilerplate reduction
- **Jackson** with snake_case naming and UTC timezone

### Module Structure (Spring Modulith)

The codebase follows a modular monolith architecture with strict module boundaries enforced by Spring Modulith:

```
com.tempo.core/
├── shared/       # Shared Kernel (OPEN module) - base entities, VOs, exceptions
├── auth/         # Authentication & Authorization - depends only on shared
├── product/      # Product catalog with variants - depends only on shared
├── order/        # Order management (placeholder) - depends on product, inventory, shared
├── inventory/    # Inventory management (placeholder) - depends on product, shared
└── notification/ # Notifications (placeholder) - depends on order, product, shared
```

Module dependencies are declared in `package-info.java` files using `@ApplicationModule` annotations.

### Layer Structure per Module

Each business module follows DDD layers:
```
{module}/
├── application/     # Use cases, DTOs, mappers
│   ├── model/       # Request/Response DTOs
│   └── service/     # Application services (separate Read/Write services)
├── domain/          # Core business logic
│   ├── model/       # Entities, Aggregate Roots
│   ├── repository/  # Repository interfaces
│   ├── rule/        # Business rules (BusinessRule implementations)
│   └── event/       # Domain events (Java Records)
├── infrastructure/  # Technical implementations
│   └── persistence/ # JPA repository implementations
├── interfaces/      # Entry points
│   └── rest/        # REST controllers
└── job/             # Scheduled tasks
```

### Entity Hierarchy

Base classes in `shared.domain.entity`:

```
BaseEntity<ID>
├── Abstract base with checkRule(BusinessRule)
├── Proper equals()/hashCode() based on ID
└── No JPA annotations (MappedSuperclass)

AggregateRoot<ID> extends BaseEntity
├── @Version for optimistic locking
├── @Transient List<DomainEvent> domainEvents
├── registerEvent(DomainEvent) method
└── @DomainEvents/@AfterDomainEventPublication support

Audit (Embeddable)
├── @CreatedDate createdAt (immutable)
└── @LastModifiedDate updatedAt
```

### Multi-Tenancy

All tenant-scoped entities have `tenantId` field:
- `tenant_id` column is mandatory and immutable
- Hibernate filter `tenantFilter` auto-applies tenant isolation on queries
- System tenant UUID: `00000000-0000-0000-0000-000000000000` (in `TenantConstants`)
- Tenant context managed via `TenantContext` (ThreadLocal) and `TenantIdArgumentResolver`
- Filter enabled per-request via `TenantFilterAspect`

### Business Rules Pattern

Domain invariants are implemented as `BusinessRule` classes:
```java
public class UsernameFormatRule implements BusinessRule {
    boolean isBroken();     // Check if rule is violated
    String getMessage();    // Error message
    String getCode();       // Error code for i18n (defaults to class name)
}
```
Use in entities: `checkRule(new SomeRule(value))` - throws `BusinessException` if broken.

### Domain Events Pattern

Domain events must be Java Records implementing `DomainEvent`:
```java
public record ProductCreatedEvent(String aggregateId, String title) implements DomainEvent {}
```
- `aggregateId()` - required, identifies the aggregate
- `occurredOn()` - defaults to `Instant.now()`
- Registered via `registerEvent()` on `AggregateRoot`
- Published automatically by Spring Data after transaction commit

### Value Objects

Located in `shared.domain.vo` (all are Java Records):
- `Money` - Currency-aware monetary values with `VND`, `USD` constants; methods: `add()`, `subtract()`, `multiply()`
- `Email` - Validated and normalized (lowercase, trimmed) email address
- `PhoneNumber` - Vietnamese phone validation with `toLocalFormat()`, `toInternationalFormat()`

### Permissions

Located in `shared.domain.Permissions`:
```java
// Auth permissions
Permissions.Auth.USER_READ      // "hasAuthority('user:read')"
Permissions.Auth.USER_WRITE
Permissions.Auth.USER_MANAGE
Permissions.Auth.ROLE_MANAGE
Permissions.Auth.IS_AUTHENTICATED

// Product permissions
Permissions.Product.PRODUCT_READ
Permissions.Product.PRODUCT_CREATE
Permissions.Product.PRODUCT_UPDATE
Permissions.Product.PRODUCT_DELETE
```

### Authentication Flow

- Reference Token pattern with Redis session storage
- RBAC with Permission -> Role -> User hierarchy
- Session stored in Redis with `AuthSession` model (TTL: 24 hours)
- Rate limiting on login via Bucket4j

### Exception Handling

Global exception handler in `shared.infrastructure.error.GlobalExceptionHandler` maps to RFC 7807 ProblemDetail:
- `BusinessException` - 422 Unprocessable Entity with error code
- `EntityNotFoundException` - 404 Not Found
- `AuthenticationException` - 401 Unauthorized
- `AccessDeniedException` - 403 Forbidden
- `OptimisticLockingFailureException` - 409 Conflict
- Validation errors - 400 Bad Request

## Module Details

### Auth Module

**Domain Models:**
- `User` - AggregateRoot with username, email, roles; factory method `User.create()`
- `Role` - AggregateRoot with code, name, permissions
- `Permission` - AggregateRoot with code, name
- `Tenant` - AggregateRoot with code, name, active status

**Business Rules:**
- `UsernameFormatRule`, `UsernameMustNotBeEmptyRule`
- `PasswordMustNotBeEmptyRule`, `PasswordStrengthRule`
- `TenantCodeFormatRule`, `TenantCodeMustNotBeEmptyRule`, `TenantNameMustNotBeEmptyRule`
- `RoleNameMustNotBeEmptyRule`, `PermissionCodeFormatRule`

**REST Endpoints:**
```
POST /api/auth/login              # Public
POST /api/auth/logout             # Public
GET  /api/auth/roles              # ROLE_MANAGE
GET  /api/auth/users              # USER_MANAGE
GET  /api/auth/users/{id}/roles   # USER_MANAGE
POST /api/auth/users              # USER_MANAGE
PUT  /api/auth/users/{id}/roles   # USER_MANAGE
POST /api/auth/roles              # ROLE_MANAGE
PUT  /api/auth/roles/{id}         # ROLE_MANAGE
```

### Product Module

**Domain Models:**
- `Product` - AggregateRoot with title, description, options, variants
  - Factory: `Product.create(tenantId, title, description)`
  - Methods: `addOption()`, `addVariant()`, `generateVariants()`
- `ProductVariant` - BaseEntity with sku, price, option1/2/3
- `ProductOption` - Entity with name and values
- `OptionValue` - Entity for option values

**Business Rules:**
- `MaxThreeOptionsRule` - Max 3 options per product
- `SkuMustBeUniqueRule` - SKU uniqueness
- `VariantMustBelongToProductRule`

**Domain Events:**
- `ProductCreatedEvent` - Fired on product creation
- `ProductVariantCreatedEvent` - Fired on variant creation

**REST Endpoints:**
```
POST   /api/products              # PRODUCT_CREATE (returns 201)
GET    /api/products              # PRODUCT_READ
GET    /api/products/{id}         # PRODUCT_READ
PUT    /api/products/{id}         # PRODUCT_UPDATE
DELETE /api/products/{id}         # PRODUCT_DELETE (returns 204)
```

## Database Migrations

Located in `src/main/resources/db/migration/`:
- V1: Initial Modulith setup
- V2: Auth RBAC tables
- V3: Audit columns (created_at, updated_at, created_by, updated_by)
- V4: Refactor store -> tenant
- V5: Customer permissions
- V6-7: Event publication tracking
- V8: Seed system tenant
- V9: Product module tables (products, product_variants, product_options, option_values)
- V10: Fix product variant option columns

## Testing

### Test Structure
```
src/test/java/com/tempo/core/
├── ModulithTest.java              # Module verification & docs
├── auth/
│   ├── domain/model/UserTest.java
│   ├── domain/rule/*Test.java
│   ├── application/service/*Test.java
│   └── interfaces/rest/*IntegrationTest.java
├── product/
│   ├── domain/rule/*Test.java
│   └── interfaces/rest/*IntegrationTest.java
└── shared/
    ├── domain/vo/*Test.java
    └── infrastructure/*Test.java
```

### Running Tests
```bash
# All tests
./gradlew test

# Single module
./gradlew test --tests "com.tempo.core.product.*"

# Integration tests only
./gradlew test --tests "*IntegrationTest"
```

## Documentation

Located in `/docs/`:
- `architecture/` - Architecture decisions, tech stack, source tree, coding standards
- `product/` - Product requirements, architecture, user stories
- `qa/` - QA gates and test assessments
