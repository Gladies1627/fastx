package com.fastx.busbooking.apiController;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.fastx.busbooking.dto.BookingDTO;
import com.fastx.busbooking.dto.BusRouteDTO;
import com.fastx.busbooking.dto.CancellationDTO;
import com.fastx.busbooking.dto.CancellationRequestDTO;
import com.fastx.busbooking.dto.PassengerCancellationDTO;
import com.fastx.busbooking.dto.SeatDTO;
import com.fastx.busbooking.entity.*;
import com.fastx.busbooking.repository.*;
import com.fastx.busbooking.security.UserDetailsImpl;
import com.fastx.busbooking.service.*;

@RestController
@RequestMapping("/api/passenger")
public class PassengerRestController {

    @Autowired 
    private BusRouteRepository busRouteRepository;
    @Autowired 
    private BookingRepository bookingRepository;
    @Autowired 
    private SeatRepository seatRepository;
    @Autowired 
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired 
    private SeatService seatService;
    @Autowired 
    private BookingService bookingService;
    @Autowired 
    private CancellationService cancellationService;
    @Autowired 
    private PdfService pdfService;
    @Autowired 
    private EmailService emailService;
    
    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping("/search")
    public List<BusRouteDTO> searchRoutes(@RequestParam String origin, @RequestParam String destination) {
        List<BusRoute> routes = busRouteRepository.findByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination);
        		return routes.stream()
        		        .filter(route -> route.getStatus() == BusRoute.Status.ACTIVE) // 💡 FILTER HERE
        		        .map(route -> new BusRouteDTO(
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
        		            route.getAmenities(),
        		            route.getStatus().toString()
        		        ))
                    .toList();
            }


    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping("/seats/{routeId}")
    public List<SeatDTO> getSeatsByRoute(@PathVariable int routeId) {
        List<Seat> seats = seatRepository.findByRoute_Id(routeId);

        return seats.stream()
            .map(seat -> new SeatDTO(
                seat.getId(),
                seat.getSeatNumber(),
                seat.isBooked(),
                seat.getBookedByGender() // ✅ include gender
            ))
            .collect(Collectors.toList());
    }


    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping("/all-bookings/{passengerId}")
    public ResponseEntity<List<BookingDTO>> getAllBookingsForPassenger(@PathVariable int passengerId) {
        List<BookingDTO> bookings = bookingService.getBookingsByPassengerId(passengerId);

        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(bookings); // 200 OK with bookings
    }




    @PreAuthorize("hasRole('PASSENGER')")
    @PostMapping("/book")
    public ResponseEntity<?> bookSeats(@RequestParam Integer passengerId,
                                       @RequestParam Integer routeId,
                                       @RequestParam List<Integer> seatIds,
                                       @RequestParam String paymentMode) {
        try {
            // 1. Get passenger & route
            User passenger = userRepository.findById(passengerId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Passenger ID"));

            BusRoute route = busRouteRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Route ID"));

            // 2. Get seats
            List<Seat> selectedSeats = seatService.getSeatsByIds(seatIds);

            // 3. Create booking
            Booking booking = new Booking();
            booking.setPassenger(passenger);
            booking.setRoute(route);
            booking.setTravelDate(route.getDepartureTime().toLocalDate());
            booking.setSeats(selectedSeats);
            booking.setStatus(Booking.Status.CONFIRMED);
            booking.setTotalAmount(route.getFarePerSeat() * selectedSeats.size());

            // 4. Save booking
            Booking savedBooking = bookingRepository.save(booking);

            // 5. Update seats
            for (Seat seat : selectedSeats) {
                seat.setBooked(true);
                seat.setBooking(savedBooking);
                seat.setBookedByGender(passenger.getGender());
            }
            seatRepository.saveAll(selectedSeats);

            // 6. Payment
            Payment payment = new Payment();
            payment.setBooking(savedBooking);
            payment.setAmountPaid(savedBooking.getTotalAmount());
            payment.setPaymentMode(paymentMode);
            payment.setPaymentStatus(Payment.Status.SUCCESS);

            savedBooking.setPayment(payment);
            paymentRepository.save(payment);
            bookingRepository.save(savedBooking); // re-save with payment

            // 7. Send email with ticket
            emailService.sendBookingConfirmation(savedBooking);

            // ✅ 8. Convert to DTO and return
            BookingDTO dto = bookingService.convertToDTO(savedBooking);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Booking failed: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping("/ticket/{bookingId}")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Integer bookingId) throws IOException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking ID"));

        ByteArrayInputStream bis = pdfService.generateTicketPdf(booking);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=ticket_" + bookingId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }



    @PreAuthorize("hasRole('PASSENGER')")
    @PutMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<String> cancelBookingDirect(
        @PathVariable Integer bookingId,
        @RequestBody CancellationRequestDTO request,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        cancellationService.cancelBookingAsPassenger(bookingId, request.reason, userDetails.getId());
        return ResponseEntity.ok("Booking cancelled successfully");
    }



    
    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping("/cancellations")
    public ResponseEntity<List<PassengerCancellationDTO>> getMyCancellations(Authentication authentication) {
        // Extract user ID from the authenticated principal
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long passengerId = userDetails.getId();

        List<PassengerCancellationDTO> list = cancellationService.getCancellationsByPassengerId(passengerId);
        return ResponseEntity.ok(list);
    }







}

