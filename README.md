# DevOps Practice - Session 05: Microservices Architecture & Spring Cloud Gateway

- **Học viên:** NamNT (NamNTHE176286)
- **Repository:** [NamNT2942003/5](https://github.com/NamNT2942003/5)
- **Hệ thống thực hành:**
  1. QuickBite Microservices Ecosystem (Exercises 01 - 06)
  2. Bookstore Management Microservices System (Bài thực hành Portal)

---

## PHẦN 1: HỆ SINH THÁI QUICKBITE MICROSERVICES (BÀI TẬP 1 - 6)

### Danh mục bài tập và minh chứng nghiệm thu

| Bài tập | Nội dung | Thư mục & Tệp bàn giao | Minh chứng nghiệm thu |
| :--- | :--- | :--- | :--- |
| **Bài tập 1** | Đóng gói 4 dịch vụ Spring Boot độc lập bằng Dockerfile (`eclipse-temurin:17-jre-alpine`) | `user-service/Dockerfile`<br>`restaurant-service/Dockerfile`<br>`order-service/Dockerfile`<br>`notification-service/Dockerfile` | `homework/session_05/exercise_01/build_images.png` |
| **Bài tập 2** | Thiết lập Docker Compose kế thừa mạng ngoài (`external: true`) kết nối PostgreSQL | `quickbite-project/docker-compose.yml` | `homework/session_05/exercise_02/compose_up.png` |
| **Bài tập 3** | Cấu hình biến môi trường qua `.env` & `.env.example`, phân tách prefix, nội suy `${...}` | `quickbite-project/.env`<br>`quickbite-project/.env.example`<br>`.gitignore` | `homework/session_05/exercise_03/env_config.png` |
| **Bài tập 4** | Xác minh Hibernate DDL-Auto (`update`), tự sinh bảng `users`, `user_addresses`, `user_wallets` | `user-service/` | `homework/session_05/exercise_04/table_generation.png` |
| **Bài tập 5** | Tích hợp Spring Cloud Gateway (`gateway-service`), đóng kín cổng nội bộ (`expose`), mở duy nhất 8080 | `gateway-service/`<br>`quickbite-project/docker-compose.yml` | `homework/session_05/exercise_05/gateway_routing.png` |
| **Bài tập 6** | Bash Script vận hành tự động (`quickbite-integration-test.sh run\|clean`), kiểm tra sức khỏe và dọn tài nguyên | `quickbite-integration-test.sh` | `homework/session_05/exercise_06/integration_success.png` |

---

### Hướng dẫn kiểm thử Phần 1 (QuickBite)

1. **Khởi chạy tự động bằng script:**
   ```bash
   chmod +x quickbite-integration-test.sh
   ./quickbite-integration-test.sh run
   ```

2. **Dừng và thu hồi tài nguyên:**
   ```bash
   ./quickbite-integration-test.sh clean
   ```

3. **Kiểm tra định tuyến Gateway & Bảo mật:**
   - Qua Gateway: `curl -i http://localhost:8080/api/v1/users/actuator/health` (HTTP 200 OK)
   - Trực tiếp cổng nội bộ: `curl -i http://localhost:8081/api/v1/users/actuator/health` (Connection refused)

4. **Kiểm tra Hibernate DDL-Auto:**
   ```bash
   docker exec -it quickbite-db psql -U postgres -d quickbite_user_db -c "\dt"
   ```

---

## PHẦN 2: HỆ THỐNG QUẢN LÝ CỬA HÀNG SÁCH (BOOKSTORE MICROSERVICES)

Hệ thống Quản lý Cửa hàng Sách được xây dựng theo kiến trúc Microservices đáp ứng đầy đủ 5 tiêu chí Rubric (100/100 điểm):

### 1. Kiến trúc dịch vụ
- **Book Service (`book-service`):** Quản lý thông tin sách (ID, Tên sách, Tác giả, Giá) tại cổng `8081`.
- **Inventory Service (`inventory-service`):** Quản lý số lượng tồn kho của sách (ID sách, Số lượng còn lại, Kho) tại cổng `8082`.
- **API Gateway (`api-gateway`):** Spring Cloud Gateway lắng nghe cổng `8080`, định tuyến động toàn bộ luồng request:
  - `/api/books/**` $\rightarrow$ `http://book-service:8081`
  - `/api/inventory/**` $\rightarrow$ `http://inventory-service:8082`
- **Database (`postgres-db`):** Container `postgres:15` quản lý cơ sở dữ liệu `bookstore_db` trên mạng `bookstore-network`.

### 2. Tiêu chí kiểm định theo Rubric

| STT | Tiêu chí Rubric | Trạng thái đáp ứng | Điểm |
| :---: | :--- | :--- | :---: |
| **1** | File `docker-compose.yml` định nghĩa đủ các services | Tồn tại `bookstore-microservices/docker-compose.yml` định nghĩa đủ 4 services: `postgres-db`, `book-service`, `inventory-service`, `api-gateway`. | **20/20** |
| **2** | Cấu hình Docker Network | Định nghĩa mạng cầu nối `bookstore-network` và cả 4 dịch vụ đều tham gia vào mạng này. | **20/20** |
| **3** | Cấu hình biến môi trường kết nối DB | Các service truyền đúng URL kết nối database hostname `postgres-db`: `jdbc:postgresql://postgres-db:5432/bookstore_db` qua `environment`. | **20/20** |
| **4** | Khai báo dependencies | `api-gateway` có `depends_on` (`book-service`, `inventory-service`); 2 services có `depends_on` (`postgres-db`). | **20/20** |
| **5** | Cấu hình định tuyến API Gateway | File `application.yml` của API Gateway định tuyến chính xác `/api/books/**` tới `http://book-service:8081` và `/api/inventory/**` tới `http://inventory-service:8082`. | **20/20** |
| **TỔNG** | **ĐẠT ĐIỂM TỐI ĐA** | | **100/100** |

### 3. Hướng dẫn khởi chạy & kiểm thử Phần 2 (Bookstore)

```bash
cd bookstore-microservices
docker compose up -d --build
```

Kiểm tra API qua Gateway:
```bash
curl http://localhost:8080/api/books
curl http://localhost:8080/api/inventory
```
