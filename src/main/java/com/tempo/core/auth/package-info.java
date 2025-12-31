/**
 * Authentication and Authorization module.
 * <p>
 * Provides Redis-based Reference Token authentication with:
 * <ul>
 * <li>Instant session revocation</li>
 * <li>RBAC (Role-Based Access Control)</li>
 * <li>Multi-tenant support via store_id</li>
 * </ul>
 * </p>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Authentication", allowedDependencies = { "shared" })
package com.tempo.core.auth;
