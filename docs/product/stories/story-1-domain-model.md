# Story 1: Domain Model & Database

> **Epic**: Product & Variant Management  
> **Points**: 3  
> **Status**: ✅ Done

---

## Summary

Thiết lập domain entities và database schema cho Product module.

## Scope

- [x] Create `Product` entity (extends `AggregateRoot<UUID>` with `@Filter`)
- [x] Create `ProductOption` entity (extends `BaseEntity<UUID>`)
- [x] Create `OptionValue` entity (extends `BaseEntity<UUID>`)
- [x] Create `ProductVariant` entity (extends `BaseEntity<UUID>` with `@Filter`)
- [x] Implement business rules:
  - `SkuMustBeUniqueRule`
  - `MaxThreeOptionsRule`
  - `VariantMustBelongToProductRule`
- [x] Create Flyway migration `V9__product_module.sql`
- [x] Define `ProductRepository` interface
- [x] Define `VariantRepository` interface

## Acceptance Criteria

- [x] Entities follow existing patterns in `auth` module
- [x] Migration runs without errors on `./gradlew flywayMigrate`
- [x] Max 3 options per product enforced at domain level
- [x] SKU uniqueness constraint at database level

## Technical Notes

```java
// Product.java
@Entity @Table(name = "products")
@Filter(name = "tenantFilter")
public class Product extends AggregateRoot<UUID> {
    private String title;
    private String description;
    @OneToMany(mappedBy = "product", cascade = ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();
}
```

## Dependencies

None - First story in epic.

## Rollback

```sql
DROP TABLE IF EXISTS product_variants CASCADE;
DROP TABLE IF EXISTS option_values CASCADE;
DROP TABLE IF EXISTS product_options CASCADE;
DROP TABLE IF EXISTS products CASCADE;
```

## QA Results

### Review Date: 2026-01-05

### Reviewed By: Quinn (Test Architect)

### Code Quality Assessment

The implementation follows the established Domain-Driven Design patterns correctly. Entities are well-encapsulated with private setters and factory methods. Repository interfaces are defined. Migration script matches the entity definitions.

### Refactoring Performed

- **File**: `ProductVariant.java`
  - **Change**: Added registration of `ProductVariantCreatedEvent` in factory method.
  - **Why**: PRD Section 3.3 explicitly requires this event for Inventory Sync. It was missing.
  - **How**: Created new event class and registered it in `ProductVariant.create`.

### Compliance Check

- Coding Standards: [✓]
- Project Structure: [✓]
- Testing Strategy: [✓] Unit tests present in domain.
- All ACs Met: [✓] With the fix for the event.

### Improvements Checklist

- [x] Added `ProductVariantCreatedEvent` class and registration.
- [ ] **Technical Debt**: `Product` factory passes `null` as ID to `ProductCreatedEvent`. This relies on ID being generated later or not used. Consider creating UUID in factory if needed for event payload.

### Gate Status

Gate: PASS → qa.qaLocation/gates/product-management.story-1-domain-model.yml

### Recommended Status

[✓ Ready for Done]
