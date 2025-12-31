/**
 * Module quản lý đơn hàng
 * <p>
 * Xử lý quy trình đặt hàng (Checkout).
 * Module này đóng vai trò Orchestrator:
 * 1. Gọi Product để lấy thông tin/giá.
 * 2. Gọi Inventory để giữ hàng (Reserve stock).
 * 3. Phát ra sự kiện OrderPlacedEvent.
 * </p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Order Processing",
        allowedDependencies = {"product", "inventory", "shared"}
)
package com.tempo.core.order;