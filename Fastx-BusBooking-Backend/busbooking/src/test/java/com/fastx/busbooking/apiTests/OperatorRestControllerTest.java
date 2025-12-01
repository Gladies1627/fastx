package com.fastx.busbooking.apiTests;



import com.fastx.busbooking.apiController.OperatorRestController;
import com.fastx.busbooking.entity.*;
import com.fastx.busbooking.repository.BusRouteRepository;
import com.fastx.busbooking.repository.CancellationRepository;
import com.fastx.busbooking.security.UserDetailsImpl;
import com.fastx.busbooking.service.*;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OperatorRestControllerTest {

    @InjectMocks
    private OperatorRestController controller;

    @Mock private BusRouteRepository busRouteRepository;
    @Mock private BusRouteService busRouteService;
    @Mock private SeatService seatService;
    @Mock private BookingService bookingService;
    @Mock private EmailService emailService;
    @Mock private CancellationRepository cancellationRepository;

    private User operator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        operator = new User();
        operator.setId(1);
        operator.setRole(User.Role.OPERATOR);
        operator.setName("OperatorUser");

        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken(new UserDetailsImpl(operator), null)
        );
    }

    @Test
    void testGetOperatorRoutes_ReturnsRouteList() {
        BusRoute route1 = new BusRoute();
        route1.setId(101);
        when(busRouteService.getRoutesByOperatorId(1)).thenReturn(List.of(route1));

        List<BusRoute> result = controller.getOperatorRoutes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(101);
    }

    @Test
    void testAddRoute_Success() {
        BusRoute inputRoute = new BusRoute();
        inputRoute.setTotalSeats(10);

        BusRoute savedRoute = new BusRoute();
        savedRoute.setId(202);

        when(busRouteRepository.save(any(BusRoute.class))).thenReturn(savedRoute);

        ResponseEntity<?> response = controller.addRoute(inputRoute);

        verify(seatService).generateSeatsForRoute(savedRoute, 10);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(((Map<?, ?>) response.getBody()).get("message")).isEqualTo("Route added successfully");
    }

    @Test
    void testUpdateRoute_Success() {
        BusRoute updatedRoute = new BusRoute();
        updatedRoute.setBusName("Updated Bus");

        when(busRouteService.updateRoute(eq(10), any(BusRoute.class), any(User.class)))
                .thenReturn(updatedRoute);

        ResponseEntity<?> response = controller.updateRoute(10, updatedRoute);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedRoute);
    }

    @Test
    void testUpdateRoute_Unauthorized() {
        when(busRouteService.updateRoute(eq(10), any(BusRoute.class), any(User.class)))
                .thenThrow(new SecurityException("Forbidden"));

        ResponseEntity<?> response = controller.updateRoute(10, new BusRoute());
        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    void testDeleteRoute_CancelsBookings() throws MessagingException {
        BusRoute route = new BusRoute();
        route.setId(303);
        route.setStatus(BusRoute.Status.ACTIVE);

        Booking booking = new Booking();
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setPassenger(operator);  // just for null safety
        booking.setTotalAmount(500.0);
        Payment payment = new Payment();
        payment.setAmountPaid(500.0);
        booking.setPayment(payment);


        route.setBookings(List.of(booking));

        when(busRouteRepository.findById(303)).thenReturn(Optional.of(route));

        ResponseEntity<String> response = controller.deleteRoute(303);

        verify(busRouteRepository).save(route);
        verify(cancellationRepository).save(any(Cancellation.class));
        verify(emailService).sendCancellationMail(eq(booking), anyString(), eq(500.0));

        assertThat(response.getBody()).contains("Route marked as cancelled");
    }

    @Test
    void testGetOperatorBookings_ReturnsList() {
        Booking booking = new Booking();
        booking.setId(404);
        when(bookingService.getBookingsByOperator(1)).thenReturn(List.of(booking));

        ResponseEntity<List<Booking>> response = controller.getOperatorBookings();

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(404);
    }
}
