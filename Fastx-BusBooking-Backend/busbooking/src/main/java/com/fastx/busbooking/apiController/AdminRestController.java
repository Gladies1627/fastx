package com.fastx.busbooking.apiController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastx.busbooking.repository.UserRepository;
import com.fastx.busbooking.security.UserDetailsImpl;
import com.fastx.busbooking.dto.AdminBookingDTO;
import com.fastx.busbooking.dto.CancellationDTO;
import com.fastx.busbooking.dto.RouteDTO;
import com.fastx.busbooking.dto.UserDTO;
import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.Cancellation;
import com.fastx.busbooking.entity.Payment;
import com.fastx.busbooking.entity.Seat;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.repository.BookingRepository;
import com.fastx.busbooking.repository.BusRouteRepository;

import com.fastx.busbooking.repository.SeatRepository;
import com.fastx.busbooking.service.BookingService;
import com.fastx.busbooking.service.BusRouteService;
import com.fastx.busbooking.service.EmailService;
import com.fastx.busbooking.service.UserService;
import com.fastx.busbooking.repository.CancellationRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    @Autowired 
    private UserRepository userRepository;
    @Autowired 
    private BusRouteRepository busRouteRepository;
    @Autowired 
    private BookingRepository bookingRepository;
    @Autowired 
    private CancellationRepository cancellationRepository;
    @Autowired 
    private BookingService bookingService;
    @Autowired 
    private EmailService emailService;
    @Autowired 
    private SeatRepository seatRepository;
    @Autowired
    private BusRouteService busRouteService;
    @Autowired
    private UserService userService;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> response = new HashMap<>();

        List<UserDTO> users = userRepository.findAll().stream()
        	    .map(u -> new UserDTO(
        	        u.getId(), 
        	        u.getName(), 
        	        u.getEmail(), 
        	        u.getContactNumber(), 
        	        u.getGender(), 
        	        u.getRole().name(),
        	        u.getStatus().name()  // <-- Add this line
        	    ))
        	    .toList();


        List<RouteDTO> routes = busRouteRepository.findAll().stream()
            .map(r -> new RouteDTO(
                r.getId(), r.getBusName(), r.getBusType().name(), r.getOrigin(), r.getDestination(),
                r.getDepartureTime().toString(), r.getArrivalTime().toString(), r.getFarePerSeat(),r.getStatus().name()
            ))
            .toList();

        List<CancellationDTO> cancellations = cancellationRepository.findAll().stream()
        	    .map(c -> {
        	        Booking booking = c.getBooking();
        	        User passenger = booking.getPassenger();
        	        List<String> cancelledSeats = booking.getSeats() != null
        	            ? booking.getSeats().stream().map(Seat::getSeatNumber).toList()
        	            : List.of();

        	        return new CancellationDTO(
        	            c.getId(),
        	            c.getRefundAmount(),
        	            c.getCancelledAt().toString(),
        	            booking.getId(),
        	            booking.getTravelDate().toString(),
        	            passenger != null ? passenger.getName() : "N/A",
        	            cancelledSeats,
        	            c.getReason()
        	        );
        	    })
        	    .toList();


        
        List<AdminBookingDTO> bookings = bookingRepository.findAllWithDetails().stream()
        	    .map(b -> {
        	        List<String> seats = b.getSeats() != null
        	            ? b.getSeats().stream().map(Seat::getSeatNumber).toList()
        	            : List.of();

        	        User passenger = b.getPassenger();
        	        BusRoute route = b.getRoute();

        	        return new AdminBookingDTO(
        	            b.getId(),
        	            b.getTravelDate() != null ? b.getTravelDate().toString() : "N/A",
        	            b.getTotalAmount() != null ? b.getTotalAmount() : 0.0,
        	            b.getStatus() != null ? b.getStatus().name() : "UNKNOWN",
        	            passenger != null ? passenger.getName() : "N/A",
        	            passenger != null ? passenger.getEmail() : "N/A",
        	            seats,
        	            route != null
        	                ? new RouteDTO(
        	                    route.getId(),
        	                    route.getBusName(),
        	                    route.getBusType().name(),
        	                    route.getOrigin(),
        	                    route.getDestination(),
        	                    route.getDepartureTime().toString(),
        	                    route.getArrivalTime().toString(),
        	                    route.getFarePerSeat(),
        	                    route.getStatus().name()
        	                )
        	                : null
        	        );
        	    })
        	    .toList();



        response.put("users", users);
        response.put("busRoutes", routes);
        response.put("cancellations", cancellations);
        response.put("bookings", bookings);
        return response;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllWithDetails();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cancellations")
    public List<Cancellation> getAllCancellations() {
        return cancellationRepository.findAll();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/route/{id}")
    public ResponseEntity<BusRoute> getRoute(@PathVariable Integer id) {
    	if (!busRouteRepository.existsById(id)) {
    	    return ResponseEntity.notFound().build();
    	}

        return ResponseEntity.of(busRouteRepository.findById(id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/route")
    public ResponseEntity<?> updateRoute(@RequestBody BusRoute route) {
        try {
            BusRoute updated = busRouteService.updateRoute(route);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/route/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Integer id) {
        BusRoute route = busRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setStatus(BusRoute.Status.CANCELLED);

        if (route.getBookings() != null) {
            for (Booking booking : route.getBookings()) {
                if (booking.getStatus() == Booking.Status.CANCELLED) continue; 
                booking.setStatus(Booking.Status.CANCELLED);

              
                Payment payment = booking.getPayment();
                double refundAmount = (payment != null) ? payment.getAmountPaid() : 0.0;

                
                if (booking.getCancellation() == null) {
                    Cancellation cancellation = new Cancellation();
                    cancellation.setBooking(booking);
                    cancellation.setUser(booking.getPassenger());
                    cancellation.setReason("Route cancelled by Admin");
                    cancellation.setRefundAmount(booking.getTotalAmount());
                    cancellation.setCancelledAt(LocalDateTime.now());

                    booking.setCancellation(cancellation);
                }
                try {
                    emailService.sendCancellationMail(
                        booking,
                        "Route Cancelled by Admin",
                        booking.getTotalAmount()
                    );
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

    
        busRouteRepository.save(route);

        return ResponseEntity.ok("Route marked as cancelled, bookings refunded.");
    }


}


