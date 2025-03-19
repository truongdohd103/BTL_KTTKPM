package com.carrental.service;

import com.carrental.dto.request.BookingRequest;
import com.carrental.dto.response.BookingResponse;
import com.carrental.dto.response.PickupCarResponse;
import com.carrental.dto.request.ReturnCarRequest;
import com.carrental.dto.response.ReturnCarResponse;
import com.carrental.dto.common.CarDto;
import com.carrental.dto.common.CustomerDto;
import com.carrental.dto.common.EmployeeDto;
import com.carrental.dto.common.VehicleCategoryDto;
import com.carrental.model.Booking;
import com.carrental.model.Car;
import com.carrental.model.Customer;
import com.carrental.model.Discount;
import com.carrental.model.Employee;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.CustomerRepository;
import com.carrental.repository.DiscountRepository;
import com.carrental.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final DiscountRepository discountRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, CarRepository carRepository,
                          CustomerRepository customerRepository, EmployeeRepository employeeRepository,
                          DiscountRepository discountRepository) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.discountRepository = discountRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Booking bookCar(BookingRequest request) {
        logger.debug("Processing booking request: {}", request);

        try {
            // Kiểm tra request
            if (request == null) {
                logger.error("Booking request is null");
                throw new IllegalArgumentException("Booking request cannot be null");
            }
            if (request.getCarId() == null || request.getCustomerId() == null || request.getEmployeeId() == null) {
                logger.error("Required fields (carId, customerId, employeeId) cannot be null: carId={}, customerId={}, employeeId={}",
                        request.getCarId(), request.getCustomerId(), request.getEmployeeId());
                throw new IllegalArgumentException("Required fields (carId, customerId, employeeId) cannot be null");
            }
            if (request.getStartDate() == null || request.getEndDate() == null) {
                logger.error("Start date or end date is null: startDate={}, endDate={}", request.getStartDate(), request.getEndDate());
                throw new IllegalArgumentException("Start date and end date must not be null");
            }
            if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
                logger.error("Status is null or empty");
                throw new IllegalArgumentException("Status must not be null or empty");
            }
            if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Total amount is invalid: totalAmount={}", request.getTotalAmount());
                throw new IllegalArgumentException("Total amount must be greater than 0");
            }

            // Kiểm tra thời gian thuê hợp lý (tối thiểu 1 ngày, tối đa 30 ngày)
            long daysBetween = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
            if (daysBetween < 1) {
                logger.error("Rental period must be at least 1 day: startDate={}, endDate={}", request.getStartDate(), request.getEndDate());
                throw new IllegalArgumentException("Rental period must be at least 1 day");
            }
            if (daysBetween > 30) {
                logger.error("Rental period cannot exceed 30 days: startDate={}, endDate={}", request.getStartDate(), request.getEndDate());
                throw new IllegalArgumentException("Rental period cannot exceed 30 days");
            }

            // Tìm xe
            logger.debug("Finding car with ID: {}", request.getCarId());
            Car car = carRepository.findById(request.getCarId())
                    .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + request.getCarId()));
            logger.debug("Found car: {}", car);

            // Kiểm tra trạng thái xe
            if (!"Available".equals(car.getStatus())) {
                logger.error("Car is not available: carId={}, status={}", car.getId(), car.getStatus());
                throw new IllegalArgumentException("Car is not available for booking, current status: " + car.getStatus());
            }

            // Kiểm tra thời gian khả dụng của xe
            LocalDate startDateAvailable = car.getStartDateAvailable();
            LocalDate endDateAvailable = car.getEndDateAvailable();
            if (startDateAvailable != null && endDateAvailable != null) {
                if (request.getStartDate().isBefore(startDateAvailable) || request.getEndDate().isAfter(endDateAvailable)) {
                    logger.error("Car is not available for the requested dates: carId={}, requestedStart={}, requestedEnd={}, availableStart={}, availableEnd={}",
                            car.getId(), request.getStartDate(), request.getEndDate(), startDateAvailable, endDateAvailable);
                    throw new IllegalArgumentException("Car is not available for the requested dates");
                }
            }

            // Tìm khách hàng
            logger.debug("Finding customer with ID: {}", request.getCustomerId());
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + request.getCustomerId()));
            logger.debug("Found customer: {}", customer);

            // Tìm nhân viên
            logger.debug("Finding employee with ID: {}", request.getEmployeeId());
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + request.getEmployeeId()));
            logger.debug("Found employee: {}", employee);

            // Kiểm tra ngày
            LocalDate today = LocalDate.now();
            logger.debug("Checking dates: startDate={}, endDate={}, today={}", request.getStartDate(), request.getEndDate(), today);
            if (request.getStartDate().isBefore(today)) {
                logger.error("Start date is in the past: startDate={}, today={}", request.getStartDate(), today);
                throw new IllegalArgumentException("Start date cannot be in the past");
            }
            if (request.getEndDate().isBefore(request.getStartDate())) {
                logger.error("End date is before start date: startDate={}, endDate={}", request.getStartDate(), request.getEndDate());
                throw new IllegalArgumentException("End date cannot be before start date");
            }

            // Kiểm tra discount code nếu có
            BigDecimal totalAmount = request.getTotalAmount();
            if (request.getDiscountCode() != null && !request.getDiscountCode().trim().isEmpty()) {
                logger.debug("Checking discount code: {}", request.getDiscountCode());
                Optional<Discount> discountOpt = discountRepository.findById(request.getDiscountCode());
                if (!discountOpt.isPresent()) {
                    logger.warn("Discount code not found: {}", request.getDiscountCode());
                    throw new IllegalArgumentException("Discount code not found: " + request.getDiscountCode());
                }
                Discount discount = discountOpt.get();
                if (discount.getExpiryDate() != null && discount.getExpiryDate().isBefore(LocalDate.now())) {
                    logger.warn("Discount code has expired: {}", request.getDiscountCode());
                    throw new IllegalArgumentException("Discount code has expired: " + request.getDiscountCode());
                }
                // Áp dụng giảm giá (giả định giảm giá trực tiếp từ value)
                BigDecimal discountValue = discount.getValue() != null ? discount.getValue() : BigDecimal.ZERO;
                totalAmount = totalAmount.subtract(discountValue);
                if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    totalAmount = BigDecimal.ZERO;
                }
                logger.debug("Applied discount, new total amount: {}", totalAmount);
            }

            // Tạo và lưu booking
            logger.debug("Creating booking...");
            Booking booking = new Booking();
            booking.setCar(car);
            booking.setCustomer(customer);
            booking.setEmployee(employee);
            booking.setStart_date(request.getStartDate());
            booking.setEnd_date(request.getEndDate());
            booking.setStatus(request.getStatus().toLowerCase());
            booking.setTotal_amount(totalAmount);
            booking.setDiscount_code(request.getDiscountCode());

            logger.debug("Saving booking: {}", booking);
            Booking savedBooking = bookingRepository.save(booking);
            logger.debug("Saved booking: {}", savedBooking);

            // Cập nhật trạng thái xe thành Rented
            car.setStatus("Rented");
            carRepository.save(car);
            logger.debug("Updated car status to Rented: carId={}", car.getId());

            return savedBooking;
        } catch (Exception e) {
            logger.error("Error in bookCar: {}", e.getMessage(), e);
            throw e; // Đảm bảo transaction rollback
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public PickupCarResponse pickupCar(Long bookingId) {
        logger.debug("Processing pickup for bookingId: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if ("confirmed".equals(booking.getStatus())) {
            booking.setStatus("picked_up");
            Booking updatedBooking = bookingRepository.save(booking);
            logger.info("Car picked up successfully for bookingId: {}", bookingId);
            return mapToDto(updatedBooking); // Trả về DTO
        } else {
            logger.error("Cannot pick up car, invalid status: bookingId={}, status={}", bookingId, booking.getStatus());
            throw new IllegalStateException("Booking must be in confirmed status to pick up, current status: " + booking.getStatus());
        }
    }

    private PickupCarResponse mapToDto(Booking booking) {
        PickupCarResponse dto = new PickupCarResponse();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStart_date());
        dto.setEndDate(booking.getEnd_date());
        dto.setStatus(booking.getStatus());
        dto.setTotalAmount(booking.getTotal_amount());
        dto.setDiscountCode(booking.getDiscount_code());
        dto.setReturnDate(booking.getReturn_date() != null ? booking.getReturn_date() : null);
        dto.setCarCondition(booking.getCar_condition() != null ? booking.getCar_condition() : null);
        dto.setAdditionalFees(booking.getAdditional_fees() != null ? booking.getAdditional_fees() : BigDecimal.ZERO);

        // Ánh xạ Car
        if (booking.getCar() != null) {
            CarDto carDto = new CarDto();
            carDto.setId(booking.getCar().getId());
            carDto.setLicensePlate(booking.getCar().getLicensePlate());
            if (booking.getCar().getCategory() != null) {
                VehicleCategoryDto categoryDto = new VehicleCategoryDto();
                categoryDto.setId(booking.getCar().getCategory().getId());
                categoryDto.setName(booking.getCar().getCategory().getName());
                carDto.setCategory(categoryDto);
            }
            dto.setCar(carDto);
        }

        // Ánh xạ Customer
        if (booking.getCustomer() != null) {
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(booking.getCustomer().getId());
            customerDto.setName(booking.getCustomer().getName());
            dto.setCustomer(customerDto);
        }

        // Ánh xạ Employee
        if (booking.getEmployee() != null) {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(booking.getEmployee().getId());
            employeeDto.setName(booking.getEmployee().getName());
            dto.setEmployee(employeeDto);
        }

        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnCarResponse returnCar(Long bookingId, ReturnCarRequest request) {
        logger.debug("Processing return for bookingId: {}, request={}", bookingId, request);

        // Kiểm tra dữ liệu đầu vào từ ReturnCarRequest
        if (request == null || request.getReturnDate() == null || request.getCarCondition() == null) {
            logger.error("Invalid return request: missing required fields");
            throw new IllegalArgumentException("Return date and car condition are required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if ("picked_up".equals(booking.getStatus())) {
            booking.setStatus("returned");
            booking.setReturn_date(request.getReturnDate());
            booking.setCar_condition(request.getCarCondition());
            booking.setAdditional_fees(request.getAdditionalFees() != null ? request.getAdditionalFees() : BigDecimal.ZERO);
            Booking updatedBooking = bookingRepository.save(booking);
            logger.info("Car returned successfully for bookingId: {}", bookingId);

            // Cập nhật trạng thái xe thành Available
            Car car = booking.getCar();
            if (car != null) {
                car.setStatus("Available");
                carRepository.save(car);
            }

            return mapToReturnCarResponse(updatedBooking);
        } else {
            logger.error("Cannot return car, invalid status: bookingId={}, status={}", bookingId, booking.getStatus());
            throw new IllegalStateException("Booking must be in picked_up status to return, current status: " + booking.getStatus());
        }
    }

    private ReturnCarResponse mapToReturnCarResponse(Booking booking) {
        ReturnCarResponse dto = new ReturnCarResponse();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStart_date());
        dto.setEndDate(booking.getEnd_date());
        dto.setReturnDate(booking.getReturn_date());
        dto.setStatus(booking.getStatus());
        dto.setTotalAmount(booking.getTotal_amount());
        dto.setDiscountCode(booking.getDiscount_code());
        dto.setCarCondition(booking.getCar_condition());
        dto.setAdditionalFees(booking.getAdditional_fees());

        // Ánh xạ Car
        if (booking.getCar() != null) {
            CarDto carDto = new CarDto();
            carDto.setId(booking.getCar().getId());
            carDto.setLicensePlate(booking.getCar().getLicensePlate());
            if (booking.getCar().getCategory() != null) {
                VehicleCategoryDto categoryDto = new VehicleCategoryDto();
                categoryDto.setId(booking.getCar().getCategory().getId());
                categoryDto.setName(booking.getCar().getCategory().getName());
                carDto.setCategory(categoryDto);
            }
            dto.setCar(carDto);
        }

        // Ánh xạ Customer
        if (booking.getCustomer() != null) {
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(booking.getCustomer().getId());
            customerDto.setName(booking.getCustomer().getName());
            dto.setCustomer(customerDto);
        }

        // Ánh xạ Employee
        if (booking.getEmployee() != null) {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(booking.getEmployee().getId());
            employeeDto.setName(booking.getEmployee().getName());
            dto.setEmployee(employeeDto);
        }

        return dto;
    }

    public Booking getBookingById(Long bookingId) {
        logger.debug("Fetching booking with ID: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
    }

    @Transactional(rollbackFor = Exception.class)
    public Booking cancelBooking(Long bookingId) {
        logger.debug("Processing cancellation for bookingId: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if ("pending".equals(booking.getStatus()) || "confirmed".equals(booking.getStatus())) {
            booking.setStatus("cancelled");
            Booking updatedBooking = bookingRepository.save(booking);

            // Cập nhật trạng thái xe thành Available nếu đã booked
            Car car = booking.getCar();
            if (car != null && "Rented".equals(car.getStatus())) {
                car.setStatus("Available");
                carRepository.save(car);
            }
            logger.info("Booking cancelled successfully for bookingId: {}", bookingId);
            return updatedBooking;
        } else {
            logger.error("Cannot cancel booking, invalid status: bookingId={}, status={}", bookingId, booking.getStatus());
            throw new IllegalStateException("Booking can only be cancelled if it is pending or confirmed, current status: " + booking.getStatus());
        }
    }

    public List<BookingResponse> getPickedUpBookings(String status) {
        logger.debug("Fetching picked up bookings with status filter: {}", status);
        List<Booking> bookings;
        if (status == null || status.trim().isEmpty()) {
            bookings = bookingRepository.findByStatus("picked_up");
        } else {
            try {
                bookings = bookingRepository.findByStatus(status.toLowerCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status filter, returning empty list: status={}", status, e);
                return new ArrayList<>();
            }
        }
        return bookings.stream().map(this::mapToBookingResponse).collect(Collectors.toList());
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setStartDate(booking.getStart_date());
        response.setEndDate(booking.getEnd_date());
        response.setStatus(booking.getStatus());
        response.setTotalAmount(booking.getTotal_amount());
        response.setDiscountCode(booking.getDiscount_code());

        // Ánh xạ Car
        if (booking.getCar() != null) {
            CarDto carDto = new CarDto();
            carDto.setId(booking.getCar().getId());
            carDto.setLicensePlate(booking.getCar().getLicensePlate());
            if (booking.getCar().getCategory() != null) {
                VehicleCategoryDto categoryDto = new VehicleCategoryDto();
                categoryDto.setId(booking.getCar().getCategory().getId());
                categoryDto.setName(booking.getCar().getCategory().getName());
                carDto.setCategory(categoryDto);
            }
            response.setCar(carDto);
        }

        // Ánh xạ Customer
        if (booking.getCustomer() != null) {
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(booking.getCustomer().getId());
            customerDto.setName(booking.getCustomer().getName());
            response.setCustomer(customerDto);
        }

        // Ánh xạ Employee
        if (booking.getEmployee() != null) {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(booking.getEmployee().getId());
            employeeDto.setName(booking.getEmployee().getName());
            response.setEmployee(employeeDto);
        }

        return response;
    }
}