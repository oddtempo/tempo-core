/**
 * Module thông báo (Notification).
 * <p>
 * Chịu trách nhiệm gửi Email/SMS/Push Notification.
 * Hoạt động hoàn toàn dựa trên Event Listener từ các module khác.
 * </p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Notifications",
        allowedDependencies = {"order", "product", "shared"}
)
package com.tempo.core.notification;