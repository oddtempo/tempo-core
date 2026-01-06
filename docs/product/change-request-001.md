# Change Request #001 - Product Atomic Creation

> **Date**: 2026-01-06  
> **Requested By**: Business Analyst (Mary)  
> **Assigned To**: Architect (Winston)  
> **Status**: ✅ COMPLETED

---

## Summary

PRD đã được cập nhật với business rules mới về luồng tạo Product. Architecture cần được cập nhật tương ứng.

---

## PRD Changes (Đã hoàn thành)

| Rule | Mô tả |
|------|-------|
| **#1 Bắt buộc có Options** | Product không thể tồn tại mà không có ít nhất 1 Option |
| **#2 Bắt buộc có Variants** | Product không thể tồn tại mà không có ít nhất 1 Variant |
| **#3 Tạo Atomic** | Options + Variants phải được tạo đồng thời với Product trong 1 transaction |

---

## Required Architecture Updates

### 1. REST API Endpoints

**Cần xóa/sửa:**
- ❌ `POST /api/products/{id}/variants` → Xóa (không cho phép tạo variant riêng)

**Giữ nguyên:**
- ✅ `POST /api/products` → Request body phải bao gồm Options + Variants

### 2. Module Structure

**Cần review:**
- `CreateVariantRequest.java` → Có thể không cần file riêng, nhúng vào `CreateProductRequest`

### 3. Business Rules Table

**Cần thêm:**
| Rule Class | Mô tả |
|------------|-------|
| `ProductMustHaveOptionsRule` | Product phải có ít nhất 1 Option |
| `ProductMustHaveVariantsRule` | Product phải có ít nhất 1 Variant |

### 4. High Level Diagram

Cần cập nhật diagram để thể hiện **Atomic Creation Flow**:
- Request → Validate Options → Validate Variants → Persist All → Emit Events

---

## Acceptance Criteria for Architecture Update

- [ ] Xóa endpoint `POST /api/products/{id}/variants` khỏi API table
- [ ] Thêm 2 business rules mới vào Business Rules table
- [ ] Cập nhật `CreateProductRequest` structure trong Module Structure
- [ ] Cập nhật diagram nếu cần

---

## References

- [PRD Updated](./prd.md)
- [Current Architecture](./architecture.md)
