
package com.fastx.busbooking.service;

import com.fastx.busbooking.dto.BusRouteDTO;
import com.fastx.busbooking.dto.CancellationDTO;
import com.fastx.busbooking.dto.PassengerCancellationDTO;
import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.Cancellation;
import com.fastx.busbooking.entity.Seat;
import com.fastx.busbooking.repository.BookingRepository;
import com.fastx.busbooking.repository.CancellationRepository;
import com.fastx.busbooking.repository.SeatRepository;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.fastx.busbooking.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

    @Service
    public class CancellationService {

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private CancellationRepository cancellationRepository;

        @Autowired
        private EmailService emailService;

        @Autowired
        private SeatRepository seatRepository;

        public void cancelBooking(Integer bookingId, String reason, boolean cancelledByOperator) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));

            // Step 1: Mark booking as CANCELLED
            booking.setStatus(Booking.Status.CANCELLED);

            // Step 2: Calculate refund
            double refundAmount = cancelledByOperator
                    ? booking.getTotalAmount()        // full refund if route/operator cancels
                    : booking.getTotalAmount() * 0.8; // 80% refund if passenger cancels

            // Step 3: Create Cancellation record
            Cancellation cancellation = new Cancellation();
            cancellation.setBooking(booking);
            cancellation.setReason(reason);
            cancellation.setRefundAmount(refundAmount);
            cancellation.setCancelledAt(LocalDateTime.now());
            cancellation.setUser(booking.getPassenger());

            cancellationRepository.save(cancellation);

            // Step 4: Unbook the seats (make available again)
            for (Seat seat : booking.getSeats()) {
                seat.setBooked(false);                // ✅ make seat available for reuse
                seat.setBookedByGender(null);         // ✅ clear metadata
                // DO NOT set seat.setBooking(null);  // 🚫 keep link so seat still shows in cancelled booking
            }
            seatRepository.saveAll(booking.getSeats());

            // Step 5: Update payment status if payment exists
            if (booking.getPayment() != null) {
                booking.getPayment().setPaymentStatus(Payment.Status.REFUNDED);
            }

            bookingRepository.save(booking); // Save booking with updated status and refund

            // Step 6: Send cancellation email
            try {
                emailService.sendCancellationMail(booking, reason, refundAmount);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        public List<PassengerCancellationDTO> getCancellationsByPassengerId(Long passengerId) {
            List<Cancellation> cancellations = cancellationRepository.findByPassengerId(passengerId);

            return cancellations.stream()
                    .map(this::mapToDTO) // ✅ full DTO mapping
                    .collect(Collectors.toList());
        }

        

        private PassengerCancellationDTO mapToDTO(Cancellation cancellation) {
            Booking booking = cancellation.getBooking();
            BusRoute route = booking.getRoute();

            // Map seat numbers
            List<String> seatNumbers = booking.getSeats().stream()
                    .map(Seat::getSeatNumber)
                    .toList();

            // Map route to BusRouteDTO
            BusRouteDTO routeDTO = new BusRouteDTO();
            routeDTO.setId(route.getId());
            routeDTO.setOrigin(route.getOrigin());
            routeDTO.setDestination(route.getDestination());
            routeDTO.setDepartureTime(route.getDepartureTime().toString());
            routeDTO.setArrivalTime(route.getArrivalTime().toString());
            routeDTO.setBusNumber(route.getBusNumber());

            // Return PassengerCancellationDTO instance
            return new PassengerCancellationDTO(
                    cancellation.getId(),
                    cancellation.getRefundAmount(),
                    cancellation.getCancelledAt().toString(),
                    booking.getId(),
                    booking.getTravelDate().toString(),
                    booking.getTotalAmount(),
                    seatNumbers,
                    routeDTO,
                    cancellation.getReason()
            );
        }



        public void cancelBookingAsPassenger(Integer bookingId, String reason, int passengerId) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (!booking.getPassenger().getId().equals(passengerId)) {
                throw new AccessDeniedException("Not allowed to cancel this booking");
            }

            if (booking.getStatus() == Booking.Status.CANCELLED) {
                throw new IllegalStateException("Booking already cancelled");
            }

     
            booking.setStatus(Booking.Status.CANCELLED);

            double refundAmount = booking.getTotalAmount() * 0.8;

         
            Cancellation cancellation = new Cancellation();
            cancellation.setBooking(booking);
            cancellation.setReason(reason);
            cancellation.setRefundAmount(refundAmount);
            cancellation.setCancelledAt(LocalDateTime.now());
            cancellation.setUser(booking.getPassenger());
            cancellationRepository.save(cancellation);

            
            for (Seat seat : booking.getSeats()) {
                seat.setBooked(false);
                seat.setBookedByGender(null);
                
            }
            seatRepository.saveAll(booking.getSeats());

            if (booking.getPayment() != null) {
                booking.getPayment().setPaymentStatus(Payment.Status.REFUNDED);
            }

            bookingRepository.save(booking);

            try {
                emailService.sendCancellationMail(booking, reason, refundAmount);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }





    public void deleteCancellationByBookingId(Integer bookingId) {
        Cancellation cancellation = cancellationRepository.findByBooking_Id(bookingId);
        if (cancellation != null) {
            cancellationRepository.delete(cancellation);
        }
    }
    
    
}
