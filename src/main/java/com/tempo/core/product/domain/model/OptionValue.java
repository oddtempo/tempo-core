package com.tempo.core.product.domain.model;

import com.tempo.core.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "option_values", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "option_id", "value" })
})
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionValue extends BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private ProductOption option;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private int position;

    // ============ FACTORY METHOD ============

    static OptionValue create(ProductOption option, String value, int position) {
        OptionValue optionValue = new OptionValue();
        optionValue.option = option;
        optionValue.value = value;
        optionValue.position = position;
        return optionValue;
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
