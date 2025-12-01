package com.fastx.busbooking.apiTests;


import com.fastx.busbooking.apiController.PassengerRestController;
import com.fastx.busbooking.dto.*;
import com.fastx.busbooking.entity.*;
import com.fastx.busbooking.repository.*;
import com.fastx.busbooking.security.UserDetailsImpl;
import com.fastx.busbooking.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PassengerRestControllerTest {

    @InjectMocks
    private PassengerRestController controller;

    @Mock private BusRouteRepository busRouteRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private UserRepository userRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private SeatService seatService;
    @Mock private BookingService bookingService;
    @Mock private CancellationService cancellationService;
    @Mock private PdfService pdfService;
    @Mock private EmailService emailService;

    private User passenger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passenger = new User();
        passenger.setId(10);
        passenger.setName("Gladi");
        passenger.setGender("Female");
        passenger.setRole(User.Role.PASSENGER);

        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken(new UserDetailsImpl(passenger), null)
        );
    }

    @Test
    void testSearchRoutes_ReturnsOnlyActiveRoutes() {
        BusRoute active = new BusRoute();
        active.setStatus(BusRoute.Status.ACTIVE);
        active.setId(1);
        active.setBusName("Express");
        active.setBusNumber("FX123");
        active.setBusType(BusRoute.BusType.SEATER_AC);
        active.setOrigin("A");
        active.setDestination("B");
        active.setDepartureTime(LocalDateTime.now());
        active.setArrivalTime(LocalDateTime.now().plusHours(5));
        active.setFarePerSeat(300.0);
        active.setTotalSeats(40);
        active.setAmenities("WiFi");

        BusRoute cancelled = new BusRoute();
        cancelled.setStatus(BusRoute.Status.CANCELLED);

        when(busRouteRepository.findByOriginIgnoreCaseAndDestinationIgnoreCase("A", "B"))
            .thenReturn(List.of(active, cancelled));

        List<BusRouteDTO> result = controller.searchRoutes("A", "B");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBusName()).isEqualTo("Express");
    }

    @Test
    void testGetSeatsByRoute_ReturnsSeatDTOs() {
        Seat seat = new Seat();
        seat.setId(1);
        seat.setSeatNumber("A1");
        seat.setBooked(false);
        seat.setBookedByGender("Male");

        when(seatRepository.findByRoute_Id(5)).thenReturn(List.of(seat));

        List<SeatDTO> result = controller.getSeatsByRoute(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSeatNumber()).isEqualTo("A1");
    }

    @Test
    void testGetAllBookingsForPassenger_Returns200() {
        BookingDTO booking = new BookingDTO();
        booking.setId(101);

        when(bookingService.getBookingsByPassengerId(10)).thenReturn(List.of(booking));

        ResponseEntity<List<BookingDTO>> response = controller.getAllBookingsForPassenger(10);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void testBookSeats_SuccessfulBooking() {
        BusRoute route = new BusRoute();
        route.setId(1);
        route.setDepartureTime(LocalDateTime.now());
        route.setFarePerSeat(300.0);

        Seat seat = new Seat();
        seat.setId(1);

        Booking booking = new Booking();
        booking.setId(123);
        booking.setTotalAmount(600.0);
        booking.setSeats(List.of(seat));
        booking.setRoute(route);

        Payment payment = new Payment();
        payment.setAmountPaid(600.0);
        payment.setPaymentMode("CARD");

        BookingDTO dto = new BookingDTO();
        dto.setId(123);

        when(userRepository.findById(10)).thenReturn(Optional.of(passenger));
        when(busRouteRepository.findById(1)).thenReturn(Optional.of(route));
        when(seatService.getSeatsByIds(List.of(1))).thenReturn(List.of(seat));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(paymentRepository.save(any())).thenReturn(payment);
        when(bookingService.convertToDTO(any())).thenReturn(dto);

        ResponseEntity<?> response = controller.bookSeats(10, 1, List.of(1), "CARD");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((BookingDTO) response.getBody()).getId()).isEqualTo(123);
    }

    @Test
    void testDownloadTicket_ReturnsPdf() throws Exception {
        Booking booking = new Booking();
        booking.setId(101);

        when(bookingRepository.findById(101)).thenReturn(Optional.of(booking));
        when(pdfService.generateTicketPdf(booking)).thenReturn(new ByteArrayInputStream("PDF Content".getBytes()));

        ResponseEntity<byte[]> response = controller.downloadTicket(101);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().get("Content-Disposition").get(0)).contains("ticket_101.pdf");
    }

    @Test
    void testCancelBookingDirect_ReturnsSuccessMessage() {
        CancellationRequestDTO request = new CancellationRequestDTO();
        request.reason = "Change of plans";

        ResponseEntity<String> response = controller.cancelBookingDirect(1, request, new UserDetailsImpl(passenger));

        verify(cancellationService).cancelBookingAsPassenger(1, "Change of plans", 10);
        assertThat(response.getBody()).isEqualTo("Booking cancelled successfully");
    }

    @Test
    void testGetMyCancellations_ReturnsList() {
        PassengerCancellationDTO dto = new PassengerCancellationDTO();
        dto.setBookingId(1);

        when(cancellationService.getCancellationsByPassengerId(10L)).thenReturn(List.of(dto));

        ResponseEntity<List<PassengerCancellationDTO>> response = controller.getMyCancellations(
            new TestingAuthenticationToken(new UserDetailsImpl(passenger), null));

        assertThat(response.getBody()).hasSize(1);
    }
}

