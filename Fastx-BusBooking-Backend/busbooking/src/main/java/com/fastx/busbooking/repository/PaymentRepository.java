package com.fastx.busbooking.repository;

import com.fastx.busbooking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByBookingId(Integer bookingId);
    List<Payment> findByBooking_Route_Id(Integer routeId);
    void deleteByBooking_Id(Integer bookingId);
    Optional<Payment> findByBooking_Id(Integer bookingId);

}
