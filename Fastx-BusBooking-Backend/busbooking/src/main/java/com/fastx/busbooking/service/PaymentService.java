package com.fastx.busbooking.service;

import com.fastx.busbooking.entity.Payment;
import com.fastx.busbooking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public void deletePaymentByBookingId(Integer bookingId) {
        paymentRepository.deleteByBooking_Id(bookingId);
    }

    public void deletePaymentsByRouteId(Integer routeId) {
        List<Payment> payments = paymentRepository.findByBooking_Route_Id(routeId);
        paymentRepository.deleteAll(payments);
    }

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public Payment getPaymentByBookingId(Integer bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}
