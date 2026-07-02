# eMarket Backend - Spring Boot REST API

Đây là mã nguồn Backend (Máy chủ) của hệ thống Thương mại Điện tử eMarket. Hệ thống cung cấp các RESTful API để xử lý logic mua bán, quản lý đơn hàng đa chi nhánh và kiểm soát tồn kho theo thời gian thực.

## Công nghệ sử dụng
* **Framework:** Java Spring Boot 3.x
* **Ngôn ngữ:** Java (JDK 21)
* **Cơ sở dữ liệu:** MySQL (v8.0+)
* **ORM:** Spring Data JPA / Hibernate
* **Kiểm thử tự động:** JUnit 5 & Mockito

---

## Hướng dẫn Cài đặt & Khởi chạy

### Bước 1: Chuẩn bị Cơ sở dữ liệu (MySQL)
1. Mở MySQL Workbench.
2. Tạo database mới bằng lệnh SQL sau:
   ```sql
   CREATE DATABASE emarket_n8;

### Bước 2: Cấu hình mã nguồn
Mở dự án bằng IntelliJ IDEA (khuyên dùng) hoặc Eclipse.

Điều hướng đến file cấu hình tại đường dẫn: src/main/resources/application.properties.

Cập nhật thông tin kết nối CSDL cho khớp với máy cá nhân của Giảng viên:

```Properties
spring.datasource.url=jdbc:mysql://localhost:3306/emarket_n8
spring.datasource.username=root
spring.datasource.password=123456   # Vui lòng đổi thành mật khẩu MySQL thực tế
```
### Bước 3: Khởi chạy Server
Chờ Maven tải hoàn tất các thư viện (Dependencies) trong file pom.xml.

Chạy class EmarketApplication.java để khởi động Server.

Nếu console thông báo thành công, Backend đã sẵn sàng nhận request tại địa chỉ: http://localhost:8080