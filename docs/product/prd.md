# Product & Variant Management (Shopify-style) - PRD

## 1. Tổng quan sản phẩm (Product Overview)
* **Tên dự án:** Tempo-Core
* **Module:** `product`
* **Mục tiêu:** Cung cấp giải pháp quản lý sản phẩm linh hoạt, cho phép cấu hình đa biến thể (Variants) dựa trên các thuộc tính (Options) tùy chỉnh, tương tự như mô hình của Shopify. Đảm bảo sự phân tách hoàn toàn giữa dữ liệu sản phẩm và dữ liệu kho (Inventory).

## 2. Đối tượng người dùng (User Personas)
* **Admin/Merchant:** Người chịu trách nhiệm tạo danh mục sản phẩm, thiết lập các tùy chọn màu sắc, kích thước và quản lý giá bán của từng biến thể.

## 3. Danh sách tính năng (Functional Requirements)

### 3.1. Quản lý Sản phẩm Gốc (Product Core)
* **Thông tin cơ bản:** Tên sản phẩm (Title), Mô tả (Description)
* **Quản lý Options:** * Hỗ trợ tối đa **3 Options** cho mỗi sản phẩm (ví dụ: Màu sắc, Kích thước, Chất liệu).
    * Mỗi Option có một danh sách các giá trị (ví dụ: Màu sắc: [Đỏ, Xanh, Vàng]).

### 3.2. Quản lý Biến thể (Product Variant)
* **Tổ hợp biến thể:** Hệ thống hỗ trợ sinh ra các biến thể dựa trên tổ hợp của các Options.
* **Thuộc tính biến thể:** * **SKU (Bắt buộc):** Mã định danh duy nhất toàn hệ thống.
    * **Giá (Price):** Giá bán lẻ của biến thể đó.
    * **Giá so sánh (Compare at Price):** Dùng để hiển thị giá gốc khi có giảm giá.
    * **Tham chiếu Option:** Lưu trữ giá trị cụ thể (ví dụ: Option1 = "Đỏ", Option2 = "L").
* **Ảnh:** Mỗi biến thể có thể có một ảnh đại diện riêng (Optional).

### 3.3. Cơ chế Đồng bộ Kho (Inventory Sync via Event)
* **Domain Event:** Khi một Variant được tạo mới thành công, module `product` sẽ phát đi sự kiện `ProductVariantCreatedEvent`.
* **Payload:** Bao gồm `variantId` và `sku`.
* **Mục đích:** Để module `inventory` lắng nghe và tự động tạo bản ghi tồn kho tương ứng.

## 4. Quy tắc nghiệp vụ (Business Rules)
1.  **Duy nhất (Uniqueness):** `SKU` của Variant là duy nhất trên toàn bộ hệ thống (Global Unique).
2.  **Ràng buộc Option:** Một biến thể không thể tồn tại nếu không thuộc về một Sản phẩm gốc.
3.  **Toàn vẹn dữ liệu:** Khi xóa một Sản phẩm, tất cả các Biến thể và Options liên quan phải bị xóa theo (Cascade Delete).
4.  **Tính đóng gói (Encapsulation):** Module `product` không được phép truy cập trực tiếp vào Database của module `inventory`. Mọi trao đổi thông tin phải thông qua Events hoặc Public Interfaces.

## 5. Tiêu chí nghiệm thu (Acceptance Criteria)
* [ ] Có thể tạo một sản phẩm "Áo thun" với 2 Options: Color (Red, Blue) và Size (S, M).
* [ ] Hệ thống phải sinh ra đủ 4 biến thể với các mã SKU tương ứng.
* [ ] Khi lưu thành công, module `product` phải log ra được sự kiện phát đi thành công.
* [ ] Kiểm tra ràng buộc: Nếu tạo một biến thể mới trùng mã SKU đã tồn tại, hệ thống phải trả về lỗi `400 Bad Request`.