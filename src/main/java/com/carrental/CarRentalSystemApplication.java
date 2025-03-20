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

        // Đặt các biến môi trường cần thiết từ .env vào System properties
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("STRIPE_API_KEY", dotenv.get("STRIPE_API_KEY"));
        System.setProperty("TWILIO_ACCOUNT_SID", dotenv.get("TWILIO_ACCOUNT_SID"));
        System.setProperty("TWILIO_AUTH_TOKEN", dotenv.get("TWILIO_AUTH_TOKEN"));
        System.setProperty("TWILIO_PHONE_NUMBER", dotenv.get("TWILIO_PHONE_NUMBER"));

        // In ra để kiểm tra các giá trị từ System properties
        System.out.println("DB_URL from System properties: " + System.getProperty("DB_URL"));
        System.out.println("DB_USERNAME from System properties: " + System.getProperty("DB_USERNAME"));
        System.out.println("DB_PASSWORD from System properties: " + System.getProperty("DB_PASSWORD"));

        // Chạy ứng dụng Spring Boot
        SpringApplication.run(CarRentalSystemApplication.class, args);
    }
}