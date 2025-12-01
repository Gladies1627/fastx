package com.fastx.busbooking.apiTests;



import com.fastx.busbooking.apiController.AuthController;
import com.fastx.busbooking.dto.LoginResponse;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.security.JwtUtil;
import com.fastx.busbooking.security.UserDetailsServiceImpl;
import com.fastx.busbooking.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        User loginRequest = new User();
        loginRequest.setName("admin");
        loginRequest.setPassword("adminpass");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mock-token");

        User user = new User();
        user.setId(1);
        user.setRole(User.Role.ADMIN);
        user.setStatus(User.Status.VALID);
        user.setName("admin");

        when(userService.getUserByName("admin")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof LoginResponse);

        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals("mock-token", loginResponse.getToken());
        assertEquals("ADMIN", loginResponse.getRole());
        assertEquals(1, loginResponse.getId());
    }

    @Test
    void testLoginFailure_UserNotFound() {
        User loginRequest = new User();
        loginRequest.setName("ghost");
        loginRequest.setPassword("nope");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("ghost");
        when(userDetailsService.loadUserByUsername("ghost")).thenReturn(userDetails);

        when(jwtUtil.generateToken(any())).thenReturn("ghost-token");
        when(userService.getUserByName("ghost")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testLoginInvalidCredentials() {
        User loginRequest = new User();
        loginRequest.setName("invalid");
        loginRequest.setPassword("wrongpass");

        doThrow(new BadCredentialsException("Invalid")).when(authenticationManager).authenticate(any());

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void testRegisterSuccess() {
        User input = new User();
        input.setName("newuser");

        User savedUser = new User();
        savedUser.setName("newuser");
        savedUser.setId(2);

        when(userService.saveUser(input)).thenReturn(savedUser);

        ResponseEntity<?> response = authController.register(input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(savedUser, response.getBody());
    }

    @Test
    void testRegisterDuplicateOrInvalidUser() {
        User input = new User();
        input.setName("duplicate");

        when(userService.saveUser(input)).thenThrow(new IllegalArgumentException("User already exists"));

        ResponseEntity<?> response = authController.register(input);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody());
    }
}

