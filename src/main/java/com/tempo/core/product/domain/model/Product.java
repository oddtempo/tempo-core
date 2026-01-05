package com.tempo.core.product.domain.model;

import com.tempo.core.product.domain.event.ProductCreatedEvent;
import com.tempo.core.product.domain.rule.MaxThreeOptionsRule;
import com.tempo.core.shared.domain.entity.TenantAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Product extends TenantAggregateRoot<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<ProductOption> options = new ArrayList<>();

    // ============ FACTORY METHOD ============

    public static Product create(UUID tenantId, String title, String description) {
        Product product = new Product();
        product.setTenantId(tenantId);
        product.title = title;
        product.description = description;

        product.registerEvent(new ProductCreatedEvent(null, title));

        return product;
    }

    // ============ BUSINESS METHODS ============

    public void addOption(String name) {
        checkRule(new MaxThreeOptionsRule(this.options.size()));

        ProductOption option = ProductOption.create(this, name, this.options.size());
        this.options.add(option);
    }

    public void updateDetails(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
