package com.tempo.core.product.domain.model;

import com.tempo.core.product.domain.event.ProductVariantCreatedEvent;
import com.tempo.core.product.domain.rule.MaxThreeOptionsRule;
import com.tempo.core.shared.domain.entity.AggregateRoot;
import org.hibernate.annotations.Filter;
import com.tempo.core.shared.domain.entity.Audit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Filter(name = "tenantFilter")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Product extends AggregateRoot<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @Embedded
    private Audit audit = new Audit();

    public static Product create(UUID tenantId, String title, String description) {
        Product product = new Product();
        product.id = UUID.randomUUID();
        product.tenantId = tenantId;
        product.title = title;
        product.description = description;
        return product;
    }

    public void addOption(String name) {
        checkRule(new MaxThreeOptionsRule(this.options.size()));
        ProductOption option = ProductOption.create(this, name, this.options.size());
        this.options.add(option);
    }

    public void addVariant(String sku, BigDecimal price) {
        ProductVariant variant = ProductVariant.create(this.tenantId, this, sku, price);
        this.variants.add(variant);
        this.registerEvent(new ProductVariantCreatedEvent(variant.getId(), sku));
    }

    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(this.variants);
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
