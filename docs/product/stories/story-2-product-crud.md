# Story 2: Product CRUD Operations

> **Epic**: Product & Variant Management  
> **Points**: 5  
> **Status**: ⬜ To Do

---

## Summary

REST API cho Product management cơ bản.

## Scope

- [ ] Create `ProductController` with endpoints:
  - `POST /api/products` - Create product with options
  - `GET /api/products` - List products (tenant-scoped, paginated)
  - `GET /api/products/{id}` - Get product detail
  - `PUT /api/products/{id}` - Update product
  - `DELETE /api/products/{id}` - Delete product (cascade)
- [ ] Create `ProductWriteService`
- [ ] Create `ProductReadService`
- [ ] Create DTOs:
  - `CreateProductRequest`
  - `UpdateProductRequest`
  - `ProductResponse`
  - `ProductDetailResponse`
- [ ] Create `ProductMapper` (MapStruct)

## Acceptance Criteria

- [ ] All endpoints return proper HTTP status codes
- [ ] Multi-tenant isolation: users only see their tenant's products
- [ ] Cascade delete removes all variants and options
- [ ] Validation errors return RFC 7807 ProblemDetail

## API Examples

```bash
# Create product
POST /api/products
{
  "title": "Áo thun",
  "description": "Áo thun cotton",
  "options": [
    { "name": "Color", "values": ["Red", "Blue"] },
    { "name": "Size", "values": ["S", "M"] }
  ]
}

# Response: 201 Created
{
  "id": "uuid",
  "title": "Áo thun",
  "optionCount": 2,
  "variantCount": 4
}
```

## Dependencies

- Story 1: Domain Model & Database

## Rollback

N/A - API layer only, no data changes.
