# Story 6: Documentation & Rollback

> **Epic**: Product & Variant Management  
> **Points**: 2  
> **Status**: ⬜ To Do

---

## Summary

Hoàn thiện tài liệu và định nghĩa rollback strategy.

## Scope

- [ ] Update main `docs/architecture/architecture.md` - add product module reference
- [ ] Document rollback procedure
- [ ] Add API examples to architecture doc
- [ ] Review and finalize `docs/product/architecture.md`

## Documentation Updates

### Main Architecture Index

Add to `docs/architecture/architecture.md`:

```markdown
| **Product Module** | `src/main/java/com/tempo/core/product/` |
```

### Product Architecture

Verify `docs/product/architecture.md` contains:
- [x] Entity relationship diagram
- [x] REST API endpoints
- [x] Database migration script
- [x] Event definitions
- [ ] Rollback procedure (add this)

## Rollback Procedure

### Database Rollback

```sql
-- V9_1__rollback_product_module.sql (emergency use only)
DROP TABLE IF EXISTS product_variants CASCADE;
DROP TABLE IF EXISTS option_values CASCADE;  
DROP TABLE IF EXISTS product_options CASCADE;
DROP TABLE IF EXISTS products CASCADE;

DELETE FROM flyway_schema_history WHERE version = '9';
```

### Application Rollback

1. Revert to previous deployment
2. Run rollback migration if needed
3. Verify existing functionality unaffected

## Acceptance Criteria

- [ ] Documentation complete and consistent
- [ ] Rollback procedure documented
- [ ] Architecture doc reviewed by team

## Dependencies

- Story 5: Testing & Validation
