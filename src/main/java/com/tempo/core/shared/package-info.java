/**
 * Module dùng chung (Shared Kernel).
 * <p>
 * Chứa các thành phần nền tảng như BaseEntity, Utils, Global Exception,
 * và các Value Object dùng chung (Money, Address).
 * Module này KHÔNG ĐƯỢC phụ thuộc vào bất kỳ module nghiệp vụ nào khác.
 * </p>
 * 
 * <p>
 * Exposes domain and infrastructure packages for use by other modules.
 * </p>
 */
@org.springframework.modulith.ApplicationModule(type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.tempo.core.shared;