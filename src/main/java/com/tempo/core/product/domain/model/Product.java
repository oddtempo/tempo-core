package com.tempo.core.product.domain.model;

import com.tempo.core.product.domain.event.ProductCreatedEvent;
import com.tempo.core.product.domain.event.ProductVariantCreatedEvent;
import com.tempo.core.product.domain.rule.MaxThreeOptionsRule;
import com.tempo.core.shared.domain.entity.AggregateRoot;
import org.hibernate.annotations.BatchSize;
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

    private UUID tenantId;

    private String title;

    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @BatchSize(size = 100)
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<ProductVariant> variants = new ArrayList<>();

    @Embedded
    private Audit audit = new Audit();

    public static Product create(UUID tenantId, String title, String description) {
        Product product = new Product();
        product.id = UUID.randomUUID();
        product.tenantId = tenantId;
        product.title = title;
        product.description = description;
        product.registerEvent(new ProductCreatedEvent(product.id, title));
        return product;
    }

    public void addOption(String name, List<String> values) {
        checkRule(new MaxThreeOptionsRule(this.options.size()));
        ProductOption option = ProductOption.create(this, name, this.options.size());
        if (values != null) {
            values.forEach(option::addValue);
        }
        this.options.add(option);
    }

    public void addVariant(String sku, BigDecimal price) {
        ProductVariant variant = ProductVariant.create(this.tenantId, this, sku, price);
        this.variants.add(variant);
        this.registerEvent(new ProductVariantCreatedEvent(variant.getId(), sku));
    }

    /**
     * Generates variants from Cartesian product of option values.
     * Example: Color[Red,Blue] x Size[S,M] = 4 variants
     */
    public void generateVariants(BigDecimal defaultPrice) {
        if (this.options.isEmpty()) {
            // No options → create single default variant
            String sku = generateSku(null, null, null);
            addVariant(sku, defaultPrice);
            return;
        }

        List<List<String>> optionValueLists = this.options.stream()
                .map(opt -> opt.getValues().stream()
                        .map(OptionValue::getValue)
                        .toList())
                .toList();

        List<List<String>> combinations = cartesianProduct(optionValueLists);

        for (List<String> combo : combinations) {
            String opt1 = !combo.isEmpty() ? combo.get(0) : null;
            String opt2 = combo.size() > 1 ? combo.get(1) : null;
            String opt3 = combo.size() > 2 ? combo.get(2) : null;

            String sku = generateSku(opt1, opt2, opt3);
            ProductVariant variant = ProductVariant.create(this.tenantId, this, sku, defaultPrice);
            variant.setOptionValues(opt1, opt2, opt3);
            this.variants.add(variant);
            this.registerEvent(new ProductVariantCreatedEvent(variant.getId(), sku));
        }
    }

    private String generateSku(String opt1, String opt2, String opt3) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.title.toUpperCase().replaceAll("\\s+", "-"), 0, Math.min(10, this.title.length()));
        if (opt1 != null)
            sb.append("-").append(opt1.toUpperCase().replaceAll("\\s+", ""));
        if (opt2 != null)
            sb.append("-").append(opt2.toUpperCase().replaceAll("\\s+", ""));
        if (opt3 != null)
            sb.append("-").append(opt3.toUpperCase().replaceAll("\\s+", ""));
        return sb.toString();
    }

    private List<List<String>> cartesianProduct(List<List<String>> lists) {
        List<List<String>> result = new ArrayList<>();
        if (lists.isEmpty()) {
            result.add(new ArrayList<>());
            return result;
        }
        cartesianProductHelper(lists, 0, new ArrayList<>(), result);
        return result;
    }

    private void cartesianProductHelper(List<List<String>> lists, int depth, List<String> current,
            List<List<String>> result) {
        if (depth == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (String item : lists.get(depth)) {
            current.add(item);
            cartesianProductHelper(lists, depth + 1, current, result);
            current.removeLast();
        }
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
