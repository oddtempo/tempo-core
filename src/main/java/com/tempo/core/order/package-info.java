@org.springframework.modulith.ApplicationModule(
        displayName = "Order Processing",
        allowedDependencies = {"product", "inventory", "shared"}
)
package com.tempo.core.order;