package com.fastx.busbooking.apiController;
import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.Cancellation;
import com.fastx.busbooking.entity.Payment;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.entity.User.Role;
import com.fastx.busbooking.repository.BusRouteRepository;
import com.fastx.busbooking.repository.CancellationRepository;
import com.fastx.busbooking.security.UserDetailsImpl;
import com.fastx.busbooking.service.BookingService;
import com.fastx.busbooking.service.BusRouteService;
import com.fastx.busbooking.service.EmailService;
import com.fastx.busbooking.service.SeatService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/operator")
public class OperatorRestController {

    @Autowired private BusRouteRepository busRouteRepository;
    @Autowired private BusRouteService busRouteService;
    @Autowired private SeatService seatService;
    @Autowired private BookingService bookingService;
    @Autowired private EmailService emailService;
    @Autowired private CancellationRepository cancellationRepository;
    
    @PreAuthorize("hasRole('OPERATOR')")
    @GetMapping("/routes")
    public List<BusRoute> getOperatorRoutes() {
        User operator = getAuthenticatedOperator();
        return busRouteService.getRoutesByOperatorId(operator.getId());
    }
    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping("/add-route")
    public ResponseEntity<?> addRoute(@RequestBody BusRoute route) {
        User operator = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        route.setOperator(operator);
        BusRoute savedRoute = busRouteRepository.save(route);
        seatService.generateSeatsForRoute(savedRoute, route.getTotalSeats());

        // ✅ Return proper JSON
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Route added successfully");
        response.put("route", savedRoute);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('OPERATOR')")
    @PutMapping("/update/{routeId}")
    public ResponseEntity<?> updateRoute(@PathVariable Integer routeId, @RequestBody BusRoute updatedRoute) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User operator = userDetails.getUser();

        try {
            BusRoute updated = busRouteService.updateRoute(routeId, updatedRoute, operator);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('OPERATOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Integer id) {
        BusRoute route = busRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        // Step 1: Mark route as CANCELLED
        route.setStatus(BusRoute.Status.CANCELLED);

        if (route.getBookings() != null) {
            for (Booking booking : route.getBookings()) {

                // Skip already cancelled bookings (avoid duplicate refund/email)
                if (booking.getStatus() == Booking.Status.CANCELLED) continue;

                booking.setStatus(Booking.Status.CANCELLED);

                // Step 2: Refund payment if exists
                Payment payment = booking.getPayment();
                double refundAmount = (payment != null) ? payment.getAmountPaid() : 0.0;

                // Step 3: Prevent duplicate Cancellation record
                if (booking.getCancellation() == null) {
                    Cancellation cancellation = new Cancellation();
                    cancellation.setBooking(booking);
                    cancellation.setUser(booking.getPassenger());
                    cancellation.setCancelledAt(LocalDateTime.now());
                    cancellation.setReason("Route Cancelled by Operator");
                    cancellation.setRefundAmount(refundAmount);
                    cancellationRepository.save(cancellation); // assuming you've autowired this
                }

                // Step 4: Email notification
                try {
                    emailService.sendCancellationMail(
                        booking,
                        "Route Cancelled by Operator",
                        refundAmount
                    );
                } catch (MessagingException e) {
                    e.printStackTrace(); // log properly in real app
                }
            }
        }

        busRouteRepository.save(route); // persist status change
        return ResponseEntity.ok("Route marked as cancelled. Bookings cancelled & notified.");
    }


    
    @PreAuthorize("hasRole('OPERATOR')")
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getOperatorBookings() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User operator = userDetails.getUser();

        List<Booking> bookings = bookingService.getBookingsByOperator(operator.getId());
        return ResponseEntity.ok(bookings);
    }
    private User getAuthenticatedOperator() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }
}
