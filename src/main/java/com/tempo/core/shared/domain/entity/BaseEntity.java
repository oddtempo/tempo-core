package com.tempo.core.shared.domain.entity;

import com.tempo.core.shared.domain.exception.BusinessException;
import com.tempo.core.shared.domain.rule.BusinessRule;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> {

    public abstract ID getId();

    @Version
    private Long version;


    protected void checkRule(BusinessRule rule) {
        if (rule.isBroken()) {
            throw new BusinessException(rule.getCode(), rule.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
