package com.carrental;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarRentalSystemApplication {
    public static void main(String[] args) {
        // Tải tệp .env
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // Đường dẫn đến thư mục gốc chứa .env
                .ignoreIfMissing() // Bỏ qua nếu không tìm thấy .env
                .load();

        // Đặt các biến môi trường từ .env vào System properties
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        // In ra để kiểm tra
        System.out.println("DB_URL from .env: " + System.getenv("DB_URL"));
        System.out.println("spring.datasource.url: " + System.getProperty("spring.datasource.url"));

        // Chạy ứng dụng Spring Boot
        SpringApplication.run(CarRentalSystemApplication.class, args);
    }
}