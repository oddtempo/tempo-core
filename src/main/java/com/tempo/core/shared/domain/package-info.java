/**
 * Shared domain package for Tempo Core.
 * <p>
 * Contains global Hibernate filter definition for multi-tenancy.
 * </p>
 */
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class), defaultCondition = "tenant_id = :tenantId")
package com.tempo.core.shared.domain;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.UUID;
