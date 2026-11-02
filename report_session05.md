# BÁO CÁO THỰC HÀNH TỔNG HỢP (SESSION 05)
## Chủ đề: Kiến trúc Microservices & Spring Cloud Gateway

- **Học viên:** NamNT (NamNTHE176286)
- **Repository:** [NamNT2942003/5](https://github.com/NamNT2942003/5)
- **Hệ sinh thái:** QuickBite Ecosystem & Bookstore Microservices

---

## I. MỤC TIÊU BÀI THỰC HÀNH

Sau khi hoàn thành bài thực hành Session 05, học viên đạt được các năng lực:
1. Đóng gói độc lập các microservice Spring Boot bằng Dockerfile tối ưu trên base image `eclipse-temurin:17-jre-alpine`.
2. Thiết kế hạ tầng phân tán kế thừa mạng ngoài (`external: true`) kết nối database dùng chung.
3. Quản lý cấu hình tập trung qua biến môi trường tiền tố (`.env` / `.env.example`), áp dụng cú pháp nội suy `${...}`.
4. Tự động khởi tạo và đồng bộ schema cơ sở dữ liệu qua cơ chế Hibernate DDL-Auto (`update`).
5. Xây dựng mô hình bảo mật hạ tầng microservices: đóng toàn bộ cổng backend ngoài host, định tuyến tập trung qua Spring Cloud Gateway (port `8080`).
6. Tự động hóa vòng đời vận hành hệ thống, kiểm thử liên thông (Integration Smoke Test) và dọn dẹp tài nguyên qua Bash Script.
7. Triển khai kiến trúc Bookstore Microservices đáp ứng 100/100 điểm theo tiêu chuẩn Rubric.

---

## II. DANH MỤC NGHIỆM THU CHI TIẾT

| Bài tập | Nội dung | Tệp bàn giao | Minh chứng | Kết quả |
| :--- | :--- | :--- | :--- | :---: |
| **Bài tập 1** | Đóng gói 4 dịch vụ Spring Boot với Dockerfile | `user-service/Dockerfile`<br>`restaurant-service/Dockerfile`<br>`order-service/Dockerfile`<br>`notification-service/Dockerfile` | `build_images.png` | **ĐẠT** |
| **Bài tập 2** | Thiết lập Docker Compose kế thừa mạng ngoài `quickbite-net` | `quickbite-project/docker-compose.yml` | `compose_up.png` | **ĐẠT** |
| **Bài tập 3** | Cấu hình biến môi trường qua `.env` & `.env.example` | `quickbite-project/.env.example`<br>`.gitignore` | `env_config.png` | **ĐẠT** |
| **Bài tập 4** | Xác minh Hibernate DDL-Auto (`update`) trong PostgreSQL | `user-service/` | `table_generation.png` | **ĐẠT** |
| **Bài tập 5** | Tích hợp Spring Cloud Gateway & đóng cổng nội bộ | `gateway-service/`<br>`docker-compose.yml` | `gateway_routing.png` | **ĐẠT** |
| **Bài tập 6** | Kịch bản tự động hóa `quickbite-integration-test.sh` | `quickbite-integration-test.sh` | `integration_success.png` | **ĐẠT** |
| **Thực hành Portal** | Bookstore Microservices System theo Rubric | `bookstore-microservices/` | `100/100 Rubric` | **ĐẠT** |

---

## III. KẾT LUẬN

Hệ thống đã được kiểm thử toàn diện, hoạt động ổn định và sẵn sàng nộp bài trên Portal.
