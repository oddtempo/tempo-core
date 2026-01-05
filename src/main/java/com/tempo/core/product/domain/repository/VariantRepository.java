package com.tempo.core.product.domain.repository;

import com.tempo.core.product.domain.model.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariantRepository {

    Optional<ProductVariant> findById(UUID id);

    Optional<ProductVariant> findBySku(String sku);

    List<ProductVariant> findByProductId(UUID productId);

    boolean existsBySku(String sku);

    ProductVariant save(ProductVariant variant);

    void delete(ProductVariant variant);

    void deleteById(UUID id);
}
