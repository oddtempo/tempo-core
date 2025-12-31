package com.tempo.core.auth.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should aggregate permissions from all assigned roles")
    void aggregatePermissions() {
        // Arrange
        UUID storeId = UUID.randomUUID();
        User user = User.create(storeId, "testuser", "password", "test@example.com");

        Role role1 = Role.create(storeId, "ROLE_1", "Role 1");
        Permission p1 = Permission.create("res1:action1", "Desc 1");
        Permission p2 = Permission.create("res1:action2", "Desc 2");
        role1.addPermission(p1);
        role1.addPermission(p2);

        Role role2 = Role.create(storeId, "ROLE_2", "Role 2");
        Permission p3 = Permission.create("res2:action1", "Desc 3");
        role2.addPermission(p3);

        // Act
        user.assignRole(role1);
        user.assignRole(role2);

        // Assert
        Set<String> allPermissions = user.getAllPermissions();
        assertEquals(3, allPermissions.size());
        assertTrue(allPermissions.contains("res1:action1"));
        assertTrue(allPermissions.contains("res1:action2"));
        assertTrue(allPermissions.contains("res2:action1"));
    }

    @Test
    @DisplayName("Should throw exception when assigning role from different store")
    void invalidStoreRole() {
        UUID store1 = UUID.randomUUID();
        UUID store2 = UUID.randomUUID();
        User user = User.create(store1, "user", "pass", "email");
        Role roleFromOtherStore = Role.create(store2, "OTHER", "Other");

        assertThrows(IllegalArgumentException.class, () -> user.assignRole(roleFromOtherStore));
    }

    @Test
    @DisplayName("Should sync roles correctly")
    void syncRoles() {
        UUID storeId = UUID.randomUUID();
        User user = User.create(storeId, "user", "pass", "email");

        Role r1 = Role.create(storeId, "R1", "D1");
        Role r2 = Role.create(storeId, "R2", "D2");
        Role r3 = Role.create(storeId, "R3", "D3");

        user.assignRole(r1);
        user.assignRole(r2);

        user.syncRoles(Set.of(r2, r3));

        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(r2));
        assertTrue(user.getRoles().contains(r3));
        assertFalse(user.getRoles().contains(r1));
    }
}
