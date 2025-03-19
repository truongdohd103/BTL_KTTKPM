package com.carrental.service;

import com.carrental.model.Booking;
import com.carrental.model.Invoice;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InvoiceService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice generateInvoice(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Invoice invoice = new Invoice();
        invoice.setBookingId(bookingId);
        invoice.setTotalAmount(booking.getTotal_amount());
        invoice.setIssuedDate(LocalDate.now());

        return invoiceRepository.save(invoice);
    }
}