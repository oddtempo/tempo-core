package com.tempo.core.product.domain.repository;

import com.tempo.core.product.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Optional<Product> findById(UUID id);

    Optional<Product> findByIdWithOptions(UUID id);

    List<Product> findAll();

    List<Product> findByTitleContaining(String title);

    boolean existsById(UUID id);

    Product save(Product product);

    void delete(Product product);

    void deleteById(UUID id);
}
