/**
 * Module quản lý tồn kho (Inventory).
 * <p>
 * Quản lý số lượng tồn kho (Stock) theo SKU.
 * Lắng nghe sự kiện từ Product để khởi tạo kho.
 * Cung cấp Interface công khai để check tồn kho.
 * </p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Inventory Management",
        allowedDependencies = {"product", "shared"}
)
package com.tempo.core.inventory;