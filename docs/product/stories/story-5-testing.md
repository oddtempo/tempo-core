# Story 5: Testing & Validation

> **Epic**: Product & Variant Management  
> **Points**: 3  
> **Status**: ⬜ To Do

---

## Summary

Comprehensive testing cho Product module.

## Scope

- [ ] Unit tests for business rules
- [ ] Unit tests for domain entities
- [ ] Integration tests for API endpoints
- [ ] Integration tests for event emission

## Test Cases

### Unit Tests

| Test Class | Coverage |
|------------|----------|
| `SkuMustBeUniqueRuleTest` | SKU validation logic |
| `MaxThreeOptionsRuleTest` | Option limit enforcement |
| `ProductTest` | Entity factory methods |
| `VariantGenerationServiceTest` | Combination generation |

### Integration Tests

| Test Class | Coverage |
|------------|----------|
| `ProductControllerTest` | All REST endpoints |
| `ProductEventIntegrationTest` | Event emission & receipt |

## Sample Tests

```java
@Test
void createProduct_withTwoOptions_generatesFourVariants() {
    var request = new CreateProductRequest(
        "Áo thun", 
        "Description",
        List.of(
            new OptionRequest("Color", List.of("Red", "Blue")),
            new OptionRequest("Size", List.of("S", "M"))
        )
    );
    
    var response = productWriteService.createProduct(request);
    
    assertThat(response.variantCount()).isEqualTo(4);
}

@Test
void createVariant_withDuplicateSku_throwsBusinessException() {
    assertThatThrownBy(() -> 
        variantService.create(productId, duplicateSkuRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("SKU");
}
```

## Acceptance Criteria

- [ ] All tests pass: `./gradlew test --tests "*Product*"`
- [ ] Coverage > 80% for domain layer
- [ ] API tests cover: happy path + validation errors + auth

## Dependencies

- Story 4: Event Integration
