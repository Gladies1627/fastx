package com.fastx.busbooking.service;

import com.fastx.busbooking.dto.BookingDTO;
import com.fastx.busbooking.dto.BusRouteDTO;
import com.fastx.busbooking.dto.SeatDTO;
import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.Payment;
import com.fastx.busbooking.entity.Seat;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.repository.BookingRepository;
import com.fastx.busbooking.repository.BusRouteRepository;
import com.fastx.busbooking.repository.SeatRepository;
import com.fastx.busbooking.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

	@Autowired
    private BookingRepository bookingRepository;
	
	@Autowired
    private BusRouteRepository busRouteRepository;
	
	@Autowired
	private SeatRepository seatRepository;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository; 
    
    public List<Booking> getBookingsByRouteId(Integer routeId) 
    {
        return bookingRepository.findByRoute_Id(routeId);
    }

    @Autowired
    private CancellationService cancellationService;

    @Transactional
    public void deleteBookingsByRouteId(int routeId) {
        List<Booking> bookings = bookingRepository.findByRoute_Id(routeId);

        for (Booking booking : bookings) {
            // Set route to null to avoid constraint issue if needed
            booking.setRoute(null);

            // Also detach related entities like seat and payment if necessary
            if (booking.getSeats() != null) {
                for (Seat seat : booking.getSeats()) {
                    seat.setBooking(null);
                }
            }

            if (booking.getPayment() != null) {
                booking.getPayment().setBooking(null);
            }
        }

        bookingRepository.deleteAll(bookings);
    }

   

    @Transactional
    public Booking createBooking(Integer passengerId, Integer routeId, List<Integer> seatIds, String paymentMode) {
        User passenger = userRepository.findById(passengerId).orElseThrow(() -> new RuntimeException("User not found"));
        BusRoute route = busRouteRepository.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));

        List<Seat> seats = seatRepository.findAllById(seatIds);
        double totalAmount = seats.size() * route.getFarePerSeat();

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setRoute(route);
        booking.setTravelDate(route.getDepartureTime().toLocalDate());
        booking.setTotalAmount(totalAmount);
        booking.setSeats(seats);
        booking.setStatus(Booking.Status.CONFIRMED);

        Booking saved = bookingRepository.save(booking);

        for (Seat seat : seats) {
            seat.setBooked(true);
            seat.setBooking(saved);
            seat.setBookedByGender(passenger.getGender());
        }
        seatRepository.saveAll(seats);

        // Add payment
        Payment payment = new Payment();
        payment.setBooking(saved);
        payment.setAmountPaid(totalAmount);
        payment.setPaymentMode(paymentMode);
        payment.setPaymentStatus(Payment.Status.SUCCESS);

        saved.setPayment(payment);
        paymentService.savePayment(payment);
        bookingRepository.save(saved);

        return saved;
    }
    public List<Booking> getBookingsByOperator(Integer operatorId) {
        return bookingRepository.findAllByRoute_Operator_Id(operatorId);
    }
   
    public List<BookingDTO> getBookingsByPassengerId(int passengerId) {
        List<Booking> bookings = bookingRepository.findByPassenger_Id(passengerId);

        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public BookingDTO convertToDTO(Booking booking) {
        // Convert Seats to SeatDTOs
        List<SeatDTO> seatDTOs = booking.getSeats().stream()
                .map(seat -> new SeatDTO(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.isBooked(),
                        seat.getBookedByGender()
                ))
                .toList();

        // Convert Route to BusRouteDTO
        BusRoute route = booking.getRoute();
        BusRouteDTO routeDTO = new BusRouteDTO(
                route.getId(),
                route.getBusName(),
                route.getBusNumber(),
                route.getBusType().toString(),
                route.getOrigin(),
                route.getDestination(),
                route.getDepartureTime().toString(),
                route.getArrivalTime().toString(),
                route.getFarePerSeat(),
                route.getTotalSeats(),
                route.getAmenities(), null
        );

        return new BookingDTO(
                booking.getId(),
                booking.getStatus().toString(),
                booking.getTravelDate(),
                booking.getTotalAmount(),
                seatDTOs,
                routeDTO
        );
    }








}


