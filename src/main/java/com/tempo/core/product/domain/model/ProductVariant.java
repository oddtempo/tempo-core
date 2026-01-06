package com.tempo.core.product.domain.model;

import com.tempo.core.product.domain.rule.SkuMustBeUniqueRule;
import com.tempo.core.product.domain.rule.VariantMustBelongToProductRule;
import com.tempo.core.shared.domain.entity.Audit;
import org.hibernate.annotations.Filter;
import com.tempo.core.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Filter(name = "tenantFilter")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductVariant extends BaseEntity<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(precision = 19, scale = 2)
    private BigDecimal compareAtPrice;

    private String option1;

    private String option2;

    private String option3;

    @Embedded
    private Audit audit = new Audit();

    public static ProductVariant create(UUID tenantId, Product product, String sku, BigDecimal price) {
        ProductVariant variant = new ProductVariant();
        variant.checkRule(new VariantMustBelongToProductRule(product));
        variant.checkRule(new SkuMustBeUniqueRule(sku));

        variant.id = UUID.randomUUID();
        variant.tenantId = tenantId;
        variant.product = product;
        variant.sku = sku;
        variant.price = price;

        return variant;
    }

    public void setOptionValues(String option1, String option2, String option3) {
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
    }

    public void updatePrice(BigDecimal price, BigDecimal compareAtPrice) {
        this.price = price;
        this.compareAtPrice = compareAtPrice;
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
