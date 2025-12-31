package com.tempo.core.product.domain.model;

import com.tempo.core.product.domain.event.ProductCreatedEvent;
import com.tempo.core.shared.domain.entity.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Product Aggregate Root.
 * <p>
 * This is an example of how to implement an Aggregate Root using the base
 * classes.
 * </p>
 * 
 * <h2>Key Design Patterns:</h2>
 * <ul>
 * <li>Factory method ({@code create()}) instead of public constructor</li>
 * <li>Domain events registered within business methods</li>
 * <li>Invariants enforced in constructor and business methods</li>
 * </ul>
 */
@Entity
@Table(name = "products")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required by JPA
public class Product extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    // ============ FACTORY METHOD (Domain Entry Point) ============

    /**
     * Creates a new Product and registers the ProductCreatedEvent.
     * <p>
     * Use this factory method instead of constructor to ensure
     * all invariants are validated and events are properly registered.
     * </p>
     * 
     * @param name  product name (required)
     * @param price product price (must be positive)
     * @return newly created Product
     * @throws IllegalArgumentException if validation fails
     */
    public static Product create(String name, BigDecimal price) {
        // Validate invariants
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }

        Product product = new Product();
        product.setName(name.trim());
        product.setPrice(price);

        // Register domain event (will be published on save())
        product.registerEvent(new ProductCreatedEvent(
                String.valueOf(product.getId()), // ID may be null before persist
                name,
                price));

        return product;
    }

    // ============ BUSINESS METHODS ============

    /**
     * Updates the product price.
     * Can be extended to register a ProductPriceChangedEvent if needed.
     */
    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        this.price = newPrice;
        // Optionally: registerEvent(new ProductPriceChangedEvent(...))
    }

    /**
     * Deactivates the product (soft delete).
     */
    public void deactivate() {
        this.active = false;
        // Optionally: registerEvent(new ProductDeactivatedEvent(...))
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
