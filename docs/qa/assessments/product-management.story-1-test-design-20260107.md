# Test Design: Story 1 - Domain Model & Database

**Date:** 2026-01-07  
**Designer:** Quinn (Test Architect)  
**Story:** [story-1-domain-model.md](file:///f:/oddy/tempo-core/docs/product/stories/story-1-domain-model.md)

---

## Test Strategy Overview

| Metric | Count |
|--------|-------|
| **Total test scenarios** | 21 |
| **Unit tests** | 14 (67%) |
| **Integration tests** | 6 (28%) |
| **E2E tests** | 1 (5%) |
| **Priority distribution** | P0: 7, P1: 9, P2: 5 |

> [!IMPORTANT]
> Current test coverage is **minimal** - only 1 test file exists (`VariantMustBelongToProductRuleTest.java`). This design addresses the gap.

---

## Test Scenarios by Acceptance Criteria

### AC1: Entities follow existing patterns in `auth` module

| ID | Level | Priority | Test | Justification |
|----|-------|----------|------|---------------|
| 1.1-UNIT-001 | Unit | P1 | `Product.create()` generates UUID and registers `ProductCreatedEvent` | Factory method logic, pure domain |
| 1.1-UNIT-002 | Unit | P1 | `ProductOption.create()` sets parent and position correctly | Entity creation logic |
| 1.1-UNIT-003 | Unit | P1 | `ProductVariant.create()` registers `ProductVariantCreatedEvent` | Event registration verification |
| 1.1-UNIT-004 | Unit | P2 | `Product.getVariants()` returns unmodifiable list | Encapsulation protection |
| 1.1-INT-001 | Integration | P0 | Entities persist/retrieve correctly with JPA relationships | Critical data integrity |
| 1.1-INT-002 | Integration | P0 | `@Filter(tenantFilter)` isolates data by tenant | Security-critical multi-tenancy |

---

### AC2: Migration runs without errors on `./gradlew flywayMigrate`

| ID | Level | Priority | Test | Justification |
|----|-------|----------|------|---------------|
| 1.2-INT-001 | Integration | P0 | `V9__product_module.sql` applies without errors | Database schema integrity |
| 1.2-INT-002 | Integration | P1 | Rollback script drops tables correctly | Recovery capability |
| 1.2-E2E-001 | E2E | P1 | Full `flywayMigrate` succeeds in CI environment | End-to-end deployment validation |

---

### AC3: Max 3 options per product enforced at domain level

| ID | Level | Priority | Test | Justification |
|----|-------|----------|------|---------------|
| 1.3-UNIT-001 | Unit | P0 | `MaxThreeOptionsRule.isBroken()` returns false for 0-2 options | Core business rule validation |
| 1.3-UNIT-002 | Unit | P0 | `MaxThreeOptionsRule.isBroken()` returns true for 3+ options | Boundary condition |
| 1.3-UNIT-003 | Unit | P0 | `Product.addOption()` throws exception on 4th option | Rule enforcement in aggregate |
| 1.3-UNIT-004 | Unit | P1 | `MaxThreeOptionsRule.getMessage()` returns correct message | Error message accuracy |

---

### AC4: SKU uniqueness constraint at database level

| ID | Level | Priority | Test | Justification |
|----|-------|----------|------|---------------|
| 1.4-UNIT-001 | Unit | P0 | `SkuMustBeUniqueRule.isBroken()` fails for null/blank SKU | Input validation |
| 1.4-UNIT-002 | Unit | P1 | `SkuMustBeUniqueRule.isBroken()` fails for SKU > 100 chars | Boundary validation |
| 1.4-UNIT-003 | Unit | P1 | `SkuMustBeUniqueRule.isBroken()` passes for valid SKU | Happy path |
| 1.4-INT-001 | Integration | P0 | DB constraint prevents duplicate SKU within tenant | Data integrity |

---

## Additional Domain Logic Tests

| ID | Level | Priority | Test | Justification |
|----|-------|----------|------|---------------|
| 1.X-UNIT-001 | Unit | P1 | `Product.generateVariants()` creates single variant when no options | Edge case handling |
| 1.X-UNIT-002 | Unit | P1 | `Product.generateVariants()` creates Cartesian product (2x2 = 4 variants) | Algorithm correctness |
| 1.X-UNIT-003 | Unit | P2 | `Product.generateSku()` formats correctly with special characters | SKU generation logic |
| 1.X-UNIT-004 | Unit | P2 | `Product.updateDetails()` modifies title and description | Basic mutation logic |

---

## Gate YAML Block

```yaml
test_design:
  scenarios_total: 21
  by_level:
    unit: 14
    integration: 6
    e2e: 1
  by_priority:
    p0: 7
    p1: 9
    p2: 5
  coverage_gaps:
    - "OptionValue entity not directly tested"
    - "VariantMustBelongToProductRule has test but needs review"
```

---

## Recommended Execution Order

1. **P0 Unit tests** - `MaxThreeOptionsRule`, `SkuMustBeUniqueRule`, `Product.addOption()`
2. **P0 Integration tests** - Tenant filter, entity persistence, SKU uniqueness constraint
3. **P1 Unit tests** - Factory methods, `generateVariants()` algorithm
4. **P1 Integration tests** - Migration scripts
5. **P1 E2E test** - Flyway migration in CI
6. **P2 tests** - Edge cases, formatting, mutations

---

## Test File Recommendations

| Test Class | Location | Coverage |
|------------|----------|----------|
| `MaxThreeOptionsRuleTest` | `src/test/java/.../product/domain/rule/` | 1.3-UNIT-001 to 004 |
| `SkuMustBeUniqueRuleTest` | `src/test/java/.../product/domain/rule/` | 1.4-UNIT-001 to 003 |
| `ProductTest` | `src/test/java/.../product/domain/model/` | 1.1-UNIT-*, 1.X-UNIT-* |
| `ProductRepositoryIntegrationTest` | `src/test/java/.../product/infrastructure/` | 1.1-INT-*, 1.4-INT-001 |
| `ProductMigrationTest` | `src/test/java/.../product/` | 1.2-* |

---

## Quality Checklist

- [x] Every AC has test coverage
- [x] Test levels are appropriate (favor unit tests)
- [x] No duplicate coverage across levels
- [x] Priorities align with business risk
- [x] Test IDs follow naming convention
- [x] Scenarios are atomic and independent

---

## Trace Reference

```
Test design matrix: docs/qa/assessments/product-management.story-1-test-design-20260107.md
P0 tests identified: 7
```
