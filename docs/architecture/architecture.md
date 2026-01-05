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

## Quick Reference

### Critical Files

| Category | File Path |
| -------- | --------- |
| **Main Entry** | `src/main/java/com/tempo/core/TempoApplication.java` |
| **Configuration** | `src/main/resources/application.yml` |
| **Auth Module** | `src/main/java/com/tempo/core/auth/` |
| **Shared Kernel** | `src/main/java/com/tempo/core/shared/` |
| **DB Migrations** | `src/main/resources/db/migration/` |

### Build Commands

```bash
./gradlew build        # Build project
./gradlew bootRun      # Run application
./gradlew test         # Run all tests
```
