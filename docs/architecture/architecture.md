# Tempo Core - Architecture Overview

> **Last Updated**: 2026-01-05

## Tổng quan

Tempo Core là một **multi-tenant backend service** được xây dựng theo kiến trúc **Modular Monolith** sử dụng Spring Boot và Spring Modulith, tuân thủ các nguyên tắc Domain-Driven Design (DDD).

## Tài liệu kiến trúc

| File | Nội dung |
| ---- | -------- |
| [tech-stack.md](tech-stack.md) | Tech stack, infrastructure, auth, development |
| [source-tree.md](source-tree.md) | Module organization, Spring Modulith, layer structure |
| [coding-standards.md](coding-standards.md) | Naming conventions, CQRS, DTOs, entity patterns |

## Key Architectural Decisions

### 1. Domain Model
*   **Entity Hierarchy**: Flat & Explicit.
    *   `BaseEntity`: `id`, `version` (optimistic locking), `equals/hashCode`.
    *   `AggregateRoot`: Extends `BaseEntity`, handles `@DomainEvents`.
    *   *No deep inheritance chains.*
*   **Composition over Inheritance**:
    *   **Audit**: Uses `@Embedded Audit` (created/updated at/by) instead of inheritance.
    *   **Tenant**: Uses `@Filter` annotation instead of inheritance.

### 2. Multi-tenancy
*   **Strategy**: Discriminator Column (`tenant_id`).
*   **Implementation**: Standard **Hibernate `@Filter`**.
    *   **Concept**: Explicitly annotating entities with `@Filter(name = "tenantFilter")`.
    *   **Global Def**: Defined in `package-info.java` with `defaultCondition`.
    *   **Runtime**: `HibernateTenantFilter` enables the filter per request.
*   **Why**: avoids "magic" meta-annotations, standard JPA/Hibernate feature, easy to debug SQL.

### 3. Modular Structure
*   **Spring Modulith**: Enforces module boundaries.
*   **Shared Kernel**: Minimal shared code (`com.tempo.core.shared`), strictly for cross-cutting concerns (Security, Base Entities).

## Quick Reference

### Critical Files
| Category | File Path |
| -------- | --------- |
| **Main Entry** | `src/main/java/com/tempo/core/TempoApplication.java` |
| **Configuration** | `src/main/resources/application.yml` |
| **Global Filter** | `src/main/java/com/tempo/core/shared/domain/package-info.java` |
| **Security Utils** | `src/main/java/com/tempo/core/shared/infrastructure/security/SecurityUtils.java` |

### Build Commands
```bash
./gradlew build        # Build project
./gradlew bootRun      # Run application
./gradlew test         # Run all tests
```
