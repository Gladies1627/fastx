package com.fastx.busbooking.service;

import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.Booking.Status;
import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.Cancellation;
import com.fastx.busbooking.entity.Seat;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.repository.BookingRepository;
import com.fastx.busbooking.repository.BusRouteRepository;
import com.fastx.busbooking.repository.SeatRepository;
import com.fastx.busbooking.service.EmailService;
import com.fastx.busbooking.repository.CancellationRepository;
import com.fastx.busbooking.repository.PaymentRepository;
import com.fastx.busbooking.entity.Payment;


import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusRouteService {

    @Autowired
    private BusRouteRepository busRouteRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
	private SeatRepository seatRepository;
    @Autowired 
    private CancellationRepository cancellationRepository;
    @Autowired 
    private CancellationService cancellationService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired 
    private EmailService emailService;
    @Transactional
	private void generateSeatsForRoute(BusRoute route) {
	    List<Seat> seats = new ArrayList<>();

	    for (int row = 1; row <= 10; row++) {
	        for (char col = 'A'; col <= 'C'; col++) {
	            String seatNumber = row + "" + col;
	            Seat seat = new Seat();
	            seat.setSeatNumber(seatNumber);
	            seat.setBooked(false);
	            seat.setRoute(route);
	            seats.add(seat);
	        }
	    }

	    seatRepository.saveAll(seats);
	}
    public BusRoute addRoute(BusRoute route) {
        BusRoute savedRoute = busRouteRepository.save(route);
        generateSeatsForRoute(savedRoute);
        return savedRoute;
    }


    public List<BusRoute> getAllRoutes() {
        return busRouteRepository.findAll();
    }

    public BusRoute saveRoute(BusRoute route) {
        return busRouteRepository.save(route);
    }
    
    public BusRoute updateRoute(Integer routeId, BusRoute updatedRoute, User operator) {
        BusRoute existingRoute = busRouteRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));

        

        if (!existingRoute.getOperator().getId().equals(operator.getId())) {
            throw new SecurityException("Unauthorized to update this route.");
        }

        existingRoute.setBusName(updatedRoute.getBusName());
        existingRoute.setBusNumber(updatedRoute.getBusNumber());
        existingRoute.setBusType(updatedRoute.getBusType());
        existingRoute.setOrigin(updatedRoute.getOrigin());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setDepartureTime(updatedRoute.getDepartureTime());
        existingRoute.setArrivalTime(updatedRoute.getArrivalTime());
        existingRoute.setFarePerSeat(updatedRoute.getFarePerSeat());
        existingRoute.setTotalSeats(updatedRoute.getTotalSeats());
        existingRoute.setAmenities(updatedRoute.getAmenities());
        

        return busRouteRepository.save(existingRoute);
    }

    public BusRoute updateRoute(BusRoute updatedRoute) {
        Integer routeId = updatedRoute.getId();
        if (routeId == null) {
            throw new IllegalArgumentException("Route ID is required for update.");
        }

        BusRoute existing = busRouteRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));

        existing.setBusName(updatedRoute.getBusName());
        existing.setBusNumber(updatedRoute.getBusNumber());
        existing.setBusType(updatedRoute.getBusType());
        existing.setOrigin(updatedRoute.getOrigin());
        existing.setDestination(updatedRoute.getDestination());
        existing.setDepartureTime(updatedRoute.getDepartureTime());
        existing.setArrivalTime(updatedRoute.getArrivalTime());
        existing.setFarePerSeat(updatedRoute.getFarePerSeat());
        existing.setTotalSeats(updatedRoute.getTotalSeats());
        existing.setAmenities(updatedRoute.getAmenities());

        return busRouteRepository.save(existing);
    }

    @Transactional
    public String deleteRouteAndHandleBookings(int routeId) throws MessagingException {
        BusRoute route = busRouteRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid route ID"));

        List<Booking> bookings = bookingRepository.findByRoute_Id(routeId);

        for (Booking booking : bookings) {
            // 1. Cancel booking (marks as CANCELLED, calculates refund, saves Cancellation, sends refund mail)
            cancellationService.cancelBooking(booking.getId(), "Route deleted by Admin", true);

            // 2. Nullify the route link to avoid Hibernate error
            booking.setRoute(null);
            bookingRepository.save(booking);
        }

        // 3. Finally delete the route
        busRouteRepository.delete(route);

        // 4. Send follow-up route cancellation email
        for (Booking booking : bookings) {
            emailService.sendCancellationNotice(booking);  // optional, if you want to send a follow-up
        }

        return bookings.size() + " bookings cancelled. Passengers notified with full refunds.";
    }


    



    public void deleteRoute(Integer id) {
        busRouteRepository.deleteById(id);
    }

    public BusRoute getRouteById(Integer id) {
        return busRouteRepository.findById(id).orElse(null);
    }
    public List<BusRoute> getRoutesByOperator(User operator) {
        return busRouteRepository.findByOperator(operator);
    }
    public List<BusRoute> getRoutesByOperatorId(Integer operatorId) {
        return busRouteRepository.findByOperatorId(operatorId);
    }



    
}
