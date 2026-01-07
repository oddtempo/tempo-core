package com.tempo.core.product.infrastructure.persistence;

import com.tempo.core.product.domain.model.Product;
import com.tempo.core.product.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {

    @Override
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithOptions(UUID id);

    @Override
    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Product> findByTitleContaining(String title);
}
