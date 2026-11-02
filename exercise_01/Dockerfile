# Base image JRE 17 Alpine gọn nhẹ tối ưu
FROM eclipse-temurin:17-jre-alpine

# Thư mục làm việc mặc định trong container
WORKDIR /app

# Sao chép file JAR đã biên dịch vào container dưới tên app.jar
COPY build/libs/*.jar app.jar

# Khai báo lệnh khởi chạy bằng Exec Form
ENTRYPOINT ["java", "-jar", "app.jar"]
