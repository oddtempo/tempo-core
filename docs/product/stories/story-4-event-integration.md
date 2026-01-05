# Story 4: Event Integration

> **Epic**: Product & Variant Management  
> **Points**: 3  
> **Status**: ⬜ To Do

---

## Summary

Tích hợp với inventory module qua Spring Modulith events.

## Scope

- [ ] Define `ProductVariantCreatedEvent` record
- [ ] Emit event in `ProductWriteService` after variant creation
- [ ] Create listener stub in `inventory` module
- [ ] Verify event persistence in `event_publication` table

## Event Definition

```java
// domain/event/ProductVariantCreatedEvent.java
@TypeAlias("product.variant-created")
public record ProductVariantCreatedEvent(
    UUID variantId,
    String sku,
    UUID productId,
    UUID tenantId
) implements DomainEvent {}
```

## Listener (Inventory Module)

```java
// inventory/application/listener/ProductEventListener.java
@Component
public class ProductEventListener {
    
    @ApplicationModuleListener(id = "inventory.on-variant-created")
    public void onVariantCreated(ProductVariantCreatedEvent event) {
        log.info("Received variant created: {}", event.sku());
        // TODO: Create inventory item
    }
}
```

## Acceptance Criteria

- [ ] Event logged successfully on variant creation
- [ ] Event payload contains all 4 fields
- [ ] Event appears in `event_publication` table
- [ ] Listener receives event (log verification)

## Dependencies

- Story 3: Variant Management
