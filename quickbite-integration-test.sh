#!/bin/bash
set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

COMPOSE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/quickbite-project" && pwd)"

case "$1" in
    clean)
        echo "=== [CLEAN] Dừng và thu hồi tài nguyên Docker Compose ==="
        docker compose -f "$COMPOSE_DIR/docker-compose.yml" down
        ;;
    run)
        echo "=== [RUN] Bắt đầu quy trình tự động hóa kiểm thử tích hợp ==="
        
        # 1. Khởi chạy cụm
        echo "--> Khởi chạy cụm container (docker compose up -d --build)..."
        docker compose -f "$COMPOSE_DIR/docker-compose.yml" up -d --build

        # 2. Vòng lặp kiểm tra PostgreSQL pg_isready mỗi 2s (tối đa 5 lần = 10s)
        echo "--> Kiểm tra trạng thái PostgreSQL (quickbite-db)..."
        pg_ready=false
        for i in {1..5}; do
            if docker exec quickbite-db pg_isready -U postgres >/dev/null 2>&1; then
                pg_ready=true
                echo "PostgreSQL đã sẵn sàng kết nối (lần thử $i)."
                break
            fi
            echo "Đang chờ PostgreSQL... ($i/5)"
            sleep 2
        done

        if [ "$pg_ready" = false ]; then
            echo -e "${RED}[LỖI] Quá thời gian chờ PostgreSQL (10s)! Đang dọn dẹp hệ thống...${NC}"
            docker compose -f "$COMPOSE_DIR/docker-compose.yml" down
            exit 1
        fi

        # 3. Chờ 5s cho Spring Boot khởi động
        echo "--> Chờ 5 giây để Spring Boot hoàn tất khởi động..."
        sleep 5

        # 4. Kiểm tra qua Spring Cloud Gateway
        echo "--> Gửi request GET tới http://localhost:8080/api/v1/users/actuator/health..."
        status_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/users/actuator/health || echo "000")

        if [ "$status_code" = "200" ]; then
            echo -e "${GREEN}CỔNG GATEWAY HOẠT ĐỘNG ỔN ĐỊNH. HỆ THỐNG LIÊN THÔNG THÀNH CÔNG!${NC}"
            exit 0
        else
            echo -e "${RED}[LỖI] Gateway trả về mã HTTP: $status_code (Kỳ vọng: 200). Trích xuất 30 dòng log cuối:${NC}"
            docker compose -f "$COMPOSE_DIR/docker-compose.yml" logs --tail=30
            echo "--> Tự động thu hồi hệ thống..."
            docker compose -f "$COMPOSE_DIR/docker-compose.yml" down
            exit 1
        fi
        ;;
    *)
        echo "Cách sử dụng: $0 [run | clean]"
        exit 1
        ;;
esac
