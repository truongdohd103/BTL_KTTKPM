package com.carrental.service;

import com.carrental.model.Discount;
import com.carrental.repository.DiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class DiscountService {

    private static final Logger logger = LoggerFactory.getLogger(DiscountService.class);

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * Tính toán giá trị giảm giá dựa trên mã giảm giá và tổng số tiền.
     *
     * @param discountCode Mã giảm giá
     * @param totalAmount Tổng số tiền trước khi giảm giá
     * @return Giá trị giảm giá (BigDecimal)
     */
    public BigDecimal calculateDiscount(String discountCode, BigDecimal totalAmount) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            logger.debug("No discount code provided, returning zero discount");
            return BigDecimal.ZERO;
        }

        try {
            // Tìm mã giảm giá trong database
            logger.debug("Fetching discount with code: {}", discountCode);
            Optional<Discount> discountOpt = discountRepository.findById(discountCode);
            if (!discountOpt.isPresent()) {
                logger.warn("Discount code not found in database: {}", discountCode);
                // Fallback: Sử dụng logic hard-code nếu không tìm thấy trong database
                return applyHardcodedDiscount(discountCode, totalAmount);
            }

            Discount discount = discountOpt.get();

            // Kiểm tra ngày hết hạn
            if (discount.getExpiryDate() != null && discount.getExpiryDate().isBefore(LocalDate.now())) {
                logger.warn("Discount code has expired: code={}, expiryDate={}", discountCode, discount.getExpiryDate());
                throw new IllegalArgumentException("Discount code has expired: " + discountCode);
            }

            // Tính giá trị giảm giá
            BigDecimal discountValue = discount.getValue() != null ? discount.getValue() : BigDecimal.ZERO;
            logger.debug("Applying discount: code={}, value={}, totalAmount={}", discountCode, discountValue, totalAmount);

            // Giả sử discount.getValue() là số tiền cố định (ví dụ: 10.00)
            // Nếu bạn muốn discount.getValue() là phần trăm (%), bạn có thể thay đổi logic ở đây
            return discountValue;
        } catch (Exception e) {
            logger.error("Error calculating discount for code {}: {}", discountCode, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Logic giảm giá hard-code (dùng làm fallback nếu không tìm thấy trong database).
     *
     * @param discountCode Mã giảm giá
     * @param totalAmount Tổng số tiền
     * @return Giá trị giảm giá
     */
    private BigDecimal applyHardcodedDiscount(String discountCode, BigDecimal totalAmount) {
        if ("DISCOUNT10".equals(discountCode)) {
            BigDecimal discount = totalAmount.multiply(new BigDecimal("0.10")); // Giảm 10%
            logger.debug("Applied hardcoded discount: code={}, discount={}", discountCode, discount);
            return discount;
        }
        logger.debug("No applicable hardcoded discount for code: {}", discountCode);
        return BigDecimal.ZERO;
    }

    /**
     * Lấy thông tin mã giảm giá và kiểm tra tính hợp lệ.
     *
     * @param discountCode Mã giảm giá
     * @return Discount entity
     */
    public Discount getDiscountByCode(String discountCode) {
        logger.debug("Fetching discount with code: {}", discountCode);
        Optional<Discount> discountOpt = discountRepository.findById(discountCode);
        if (!discountOpt.isPresent()) {
            logger.warn("Discount code not found: {}", discountCode);
            throw new IllegalArgumentException("Discount code not found: " + discountCode);
        }
        Discount discount = discountOpt.get();
        if (discount.getExpiryDate() != null && discount.getExpiryDate().isBefore(LocalDate.now())) {
            logger.warn("Discount code has expired: {}", discountCode);
            throw new IllegalArgumentException("Discount code has expired: " + discountCode);
        }
        return discount;
    }
}