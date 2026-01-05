package com.tempo.core.product.domain.model;

import com.tempo.core.product.domain.event.ProductVariantCreatedEvent;
import com.tempo.core.product.domain.rule.SkuMustBeUniqueRule;
import com.tempo.core.product.domain.rule.VariantMustBelongToProductRule;
import com.tempo.core.shared.domain.entity.TenantAggregateRoot;
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
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductVariant extends TenantAggregateRoot<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "compare_at_price", precision = 19, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(name = "option1_value")
    private String option1Value;

    @Column(name = "option2_value")
    private String option2Value;

    @Column(name = "option3_value")
    private String option3Value;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // ============ FACTORY METHOD ============

    public static ProductVariant create(UUID tenantId, Product product, String sku, BigDecimal price) {
        ProductVariant variant = new ProductVariant();
        variant.checkRule(new VariantMustBelongToProductRule(product));
        variant.checkRule(new SkuMustBeUniqueRule(sku));

        variant.setTenantId(tenantId);
        variant.product = product;
        variant.sku = sku;
        variant.price = price;

        variant.registerEvent(new ProductVariantCreatedEvent(variant.getId(), sku));

        return variant;
    }

    // ============ BUSINESS METHODS ============

    public void setOptionValues(String option1, String option2, String option3) {
        this.option1Value = option1;
        this.option2Value = option2;
        this.option3Value = option3;
    }

    public void updatePrice(BigDecimal price, BigDecimal compareAtPrice) {
        this.price = price;
        this.compareAtPrice = compareAtPrice;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
