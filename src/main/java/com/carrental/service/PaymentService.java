package com.carrental.service;

import com.carrental.dto.request.PaymentRequest;
import com.carrental.model.Booking;
import com.carrental.model.Discount;
import com.carrental.model.Payment;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.DiscountRepository;
import com.carrental.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final DiscountRepository discountRepository;
    private final DiscountService discountService;

    @Autowired
    public PaymentService(BookingRepository bookingRepository, PaymentRepository paymentRepository,
                          DiscountRepository discountRepository, DiscountService discountService) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.discountRepository = discountRepository;
        this.discountService = discountService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Payment processPayment(PaymentRequest request) {
        logger.debug("Processing payment request: {}", request);

        if (request == null) {
            logger.error("Payment request is null");
            throw new IllegalArgumentException("Payment request cannot be null");
        }
        if (request.getBookingId() == null || request.getPaymentDate() == null ||
                request.getMethod() == null || request.getStatus() == null) {
            logger.error("Required fields are null: bookingId={}, paymentDate={}, method={}, status={}",
                    request.getBookingId(), request.getPaymentDate(), request.getMethod(), request.getStatus());
            throw new IllegalArgumentException("Required fields (bookingId, paymentDate, method, status) cannot be null");
        }

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + request.getBookingId()));

        // Tính toán finalAmount từ booking
        BigDecimal finalAmount = booking.getTotal_amount();
        logger.debug("Initial amount from booking: {}", finalAmount);

        if (booking.getDiscount_code() != null) {
            logger.debug("Discount code found: {}", booking.getDiscount_code());
            Discount discount = discountRepository.findById(booking.getDiscount_code())
                    .orElseThrow(() -> new IllegalArgumentException("Discount code not found: " + booking.getDiscount_code()));

            if (discount.getExpiryDate().isBefore(LocalDate.now())) {
                logger.warn("Discount code has expired: {}", booking.getDiscount_code());
                throw new IllegalArgumentException("Discount code has expired: " + booking.getDiscount_code());
            }

            BigDecimal discountValue = discountService.calculateDiscount(booking.getDiscount_code(), finalAmount);
            finalAmount = finalAmount.subtract(discountValue);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
            logger.debug("Applied discount, final amount: {}", finalAmount);
        }

        // Tạo payment với finalAmount
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setAmount(finalAmount); // Sử dụng finalAmount từ booking
        payment.setMethod(request.getMethod());
        payment.setStatus(request.getStatus());
        payment.setPaymentDate(request.getPaymentDate());

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment processed successfully for bookingId: {}", request.getBookingId());
        return savedPayment;
    }
}