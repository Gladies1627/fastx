package com.fastx.busbooking.repository;

import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.Cancellation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CancellationRepository extends JpaRepository<Cancellation, Integer> {
	Cancellation findByBooking_Id(Integer bookingId);

	List<Cancellation> findByUserId(Integer userId);
	 void deleteByBooking_Id(Integer bookingId);
	 
	 void deleteByBooking(Booking booking);
	 void deleteAllByBookingIn(List<Booking> bookings);
	 List<Cancellation> findAllByUser_Id(int passengerId);
	 @Query("SELECT c FROM Cancellation c WHERE c.booking.passenger.id = :passengerId")
	 List<Cancellation> findByPassengerId(@Param("passengerId") Long passengerId);
	 


}
