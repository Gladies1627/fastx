package com.fastx.busbooking.apiTests;

import com.fastx.busbooking.apiController.AdminRestController;
import com.fastx.busbooking.entity.*;
import com.fastx.busbooking.repository.*;
import com.fastx.busbooking.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AdminRestControllerTest {

    @InjectMocks
    private AdminRestController adminController;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BusRouteRepository busRouteRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CancellationRepository cancellationRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private EmailService emailService;
    @Mock
    private BusRouteService busRouteService;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        List<User> mockUsers = List.of(new User());
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = adminController.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBookings() {
        List<Booking> mockBookings = List.of(new Booking());
        when(bookingRepository.findAllWithDetails()).thenReturn(mockBookings);

        List<Booking> result = adminController.getAllBookings();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(bookingRepository, times(1)).findAllWithDetails();
    }

    @Test
    void testGetAllCancellations() {
        List<Cancellation> mockCancellations = List.of(new Cancellation());
        when(cancellationRepository.findAll()).thenReturn(mockCancellations);

        List<Cancellation> result = adminController.getAllCancellations();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(cancellationRepository, times(1)).findAll();
    }

    @Test
    void testGetRoute_found() {
        BusRoute mockRoute = new BusRoute();
        mockRoute.setId(1);
        when(busRouteRepository.existsById(1)).thenReturn(true);
        when(busRouteRepository.findById(1)).thenReturn(Optional.of(mockRoute));

        ResponseEntity<BusRoute> result = adminController.getRoute(1);

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isEqualTo(mockRoute);
    }

    @Test
    void testGetRoute_notFound() {
        when(busRouteRepository.existsById(100)).thenReturn(false);

        ResponseEntity<BusRoute> result = adminController.getRoute(100);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void testDeleteUser_success() {
        doNothing().when(userService).deleteUserById(1);

        ResponseEntity<?> result = adminController.deleteUser(1);

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        verify(userService, times(1)).deleteUserById(1);
    }

    @Test
    void testDeleteUser_failure() {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUserById(999);

        ResponseEntity<?> result = adminController.deleteUser(999);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isEqualTo("User not found");
    }

    @Test
    void testUpdateRoute_success() {
        BusRoute input = new BusRoute();
        input.setId(1);
        when(busRouteService.updateRoute(any())).thenReturn(input);

        ResponseEntity<?> result = adminController.updateRoute(input);

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isEqualTo(input);
    }

    @Test
    void testUpdateRoute_invalid() {
        BusRoute invalid = new BusRoute();
        doThrow(new IllegalArgumentException("Invalid")).when(busRouteService).updateRoute(any());

        ResponseEntity<?> result = adminController.updateRoute(invalid);

        assertThat(result.getStatusCode().is4xxClientError()).isTrue();
        assertThat(result.getBody()).isEqualTo("Invalid");
    }

    @Test
    void testDeleteRoute_marksRouteAsCancelled() {
        // Setup
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setTotalAmount(100.0);
        booking.setPassenger(new User());
        booking.setSeats(List.of());

        BusRoute route = new BusRoute();
        route.setId(1);
        route.setStatus(BusRoute.Status.ACTIVE);
        route.setBookings(List.of(booking));

        when(busRouteRepository.findById(1)).thenReturn(Optional.of(route));
        when(busRouteRepository.save(any())).thenReturn(route);

        // Execute
        ResponseEntity<String> response = adminController.deleteRoute(1);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(route.getStatus()).isEqualTo(BusRoute.Status.CANCELLED);
        assertThat(booking.getStatus()).isEqualTo(Booking.Status.CANCELLED);
        assertThat(booking.getCancellation()).isNotNull();
        verify(busRouteRepository, times(1)).save(route);
    }
}
