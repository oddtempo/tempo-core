# Story 3: Variant Management

> **Epic**: Product & Variant Management  
> **Points**: 5  
> **Status**: ⬜ To Do

---

## Summary

Quản lý biến thể sản phẩm với auto-generation và validation.

## Scope

- [ ] Implement `VariantGenerationService` - auto-generate từ option combinations
- [ ] API endpoints:
  - `POST /api/products/{id}/variants` - Add single variant
  - `PUT /api/products/{id}/variants/{variantId}` - Update variant
  - `DELETE /api/products/{id}/variants/{variantId}` - Delete variant
- [ ] Create DTOs:
  - `CreateVariantRequest`
  - `UpdateVariantRequest` 
  - `VariantResponse`
- [ ] SKU validation (global unique across all tenants)
- [ ] Price validation (must be positive)

## Acceptance Criteria

- [ ] Creating "Áo thun" with Color(Red,Blue) + Size(S,M) generates 4 variants
- [ ] Duplicate SKU returns `400 Bad Request` with message
- [ ] Variant must belong to existing product - orphan creation fails
- [ ] Deleting product cascades to all variants

## Business Rules

| Rule | Implementation |
|------|----------------|
| SKU unique | `SkuMustBeUniqueRule` + DB unique constraint |
| Max 3 options | `MaxThreeOptionsRule` |
| Price > 0 | `PriceMustBePositiveRule` |

## Generated SKU Pattern

```
{product_id_short}-{option1}-{option2}-{option3}
Example: ABC12-RED-S → for Red + Size S
```

## Dependencies

- Story 2: Product CRUD Operations
