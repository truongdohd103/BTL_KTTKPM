package com.carrental.controller;

import com.carrental.dto.request.BookingRequest;
import com.carrental.dto.request.ReturnCarRequest;
import com.carrental.dto.response.BookingResponse;
import com.carrental.dto.response.ErrorResponse;
import com.carrental.dto.response.PickupCarResponse;
import com.carrental.dto.response.ReturnCarResponse;
import com.carrental.model.Booking;
import com.carrental.service.BookingService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    public BookingController(BookingService bookingService, ModelMapper modelMapper) {
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
    }

    /**
     * Tạo một booking mới
     */
    @PostMapping
    public ResponseEntity<?> bookCar(@Valid @RequestBody BookingRequest request, BindingResult result) {
        logger.debug("Received booking request: {}", request);
        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            Booking booking = bookingService.bookCar(request);
            logger.info("Booking created successfully: bookingId={}", booking.getId());
            return ResponseEntity.ok(booking); // Có thể ánh xạ sang BookingResponse nếu cần
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Xử lý việc nhận xe (pickup)
     */
    @PostMapping("/{bookingId}/pickup")
    public ResponseEntity<PickupCarResponse> pickupCar(@PathVariable Long bookingId) {
        logger.debug("Received pickup request for bookingId: {}", bookingId);
        try {
            PickupCarResponse response = bookingService.pickupCar(bookingId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error picking up car: bookingId={}, error={}", bookingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Xử lý việc trả xe (return)
     */
    @PostMapping("/{bookingId}/return")
    public ResponseEntity<ReturnCarResponse> returnCar(@PathVariable Long bookingId, @RequestBody ReturnCarRequest request) {
        logger.debug("Received return request for bookingId: {}, request={}", bookingId, request);
        try {
            ReturnCarResponse response = bookingService.returnCar(bookingId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error returning car: bookingId={}, error={}", bookingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Lấy thông tin trạng thái booking
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingStatus(@PathVariable Long bookingId) {
        logger.debug("Received request to get booking status for bookingId: {}", bookingId);
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching booking: bookingId={}, error={}", bookingId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Hủy booking
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        logger.debug("Received cancel request for bookingId: {}", id);
        try {
            Booking booking = bookingService.cancelBooking(id);
            if (booking == null) {
                logger.warn("Booking not found for cancellation: bookingId={}", id);
                return ResponseEntity.notFound().build();
            }
            BookingResponse response = modelMapper.map(booking, BookingResponse.class); // Ánh xạ tự động
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error cancelling booking: bookingId={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Lấy danh sách booking đã nhận (picked up)
     */
    @GetMapping("/picked-up")
    public ResponseEntity<List<BookingResponse>> getPickedUpBookings(@RequestParam(required = false) String status) {
        logger.debug("Received request to get picked up bookings with status: {}", status);
        try {
            List<BookingResponse> pickedUpBookings = bookingService.getPickedUpBookings(status);
            return ResponseEntity.ok(pickedUpBookings != null ? pickedUpBookings : new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error fetching picked up bookings: status={}, error={}", status, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }
}