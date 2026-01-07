package com.tempo.core.product.domain.model;

import com.tempo.core.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;
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
    @BatchSize(size = 100)
    private Set<OptionValue> values = new HashSet<>();

    static ProductOption create(Product product, String name, int position) {
        ProductOption option = new ProductOption();
        option.product = product;
        option.name = name;
        option.position = position;
        return option;
    }

    public void addValue(String value) {
        var optionValue = OptionValue.create(this, value, this.values.size());
        this.values.add(optionValue);
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
