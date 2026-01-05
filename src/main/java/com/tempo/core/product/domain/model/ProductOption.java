package com.tempo.core.product.domain.model;

import com.tempo.core.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_options", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "product_id", "name" })
})
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int position;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<OptionValue> values = new ArrayList<>();

    // ============ FACTORY METHOD ============

    static ProductOption create(Product product, String name, int position) {
        ProductOption option = new ProductOption();
        option.product = product;
        option.name = name;
        option.position = position;
        return option;
    }

    // ============ BUSINESS METHODS ============

    public void addValue(String value) {
        OptionValue optionValue = OptionValue.create(this, value, this.values.size());
        this.values.add(optionValue);
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
