package com.carrental.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class NotificationService {
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendBookingConfirmation(Long bookingId) {
        Message.creator(
                new com.twilio.type.PhoneNumber("+recipient_phone_number"), // Thay bằng số thực tế
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                "Your booking ID " + bookingId + " has been confirmed!"
        ).create();
    }

    public void notifyEmployee(Long bookingId, String message) {
        Message.creator(
                new com.twilio.type.PhoneNumber("+employee_phone_number"), // Thay bằng số thực tế
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                message
        ).create();
    }

    public void sendPaymentConfirmation(Long bookingId) {
        Message.creator(
                new com.twilio.type.PhoneNumber("+recipient_phone_number"), // Thay bằng số thực tế
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                "Payment for booking ID " + bookingId + " has been completed!"
        ).create();
    }

    public void sendCancellationNotification(Long bookingId) {
        Message.creator(
                new com.twilio.type.PhoneNumber("+recipient_phone_number"), // Thay bằng số thực tế
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                "Booking ID " + bookingId + " has been cancelled."
        ).create();
    }
}