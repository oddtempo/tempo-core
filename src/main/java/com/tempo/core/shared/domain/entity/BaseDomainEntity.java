package com.tempo.core.shared.domain.entity;

import com.tempo.core.shared.domain.exception.BusinessException;
import com.tempo.core.shared.domain.rule.BusinessRule;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

/**
 * Base class for all domain entities.
 * <p>
 * Provides the {@code checkRule} helper to enforce domain invariants.
 * All domain entities (Root or child) should extend this class.
 * </p>
 * 
 * @param <ID> the type of the entity identifier
 */
@MappedSuperclass
public abstract class BaseDomainEntity<ID extends Serializable> extends BaseEntity<ID> {

    /**
     * Checks a business rule and throws a BusinessException if it's broken.
     * 
     * @param rule the business rule to check
     * @throws BusinessException if the rule is broken
     */
    protected void checkRule(BusinessRule rule) {
        if (rule.isBroken()) {
            throw new BusinessException(rule.getCode(), rule.getMessage());
        }
    }
}
