package com.fastx.busbooking.apiController;

import com.fastx.busbooking.dto.LoginResponse;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.repository.UserRepository;
import com.fastx.busbooking.security.JwtUtil;
import com.fastx.busbooking.security.UserDetailsServiceImpl;
import com.fastx.busbooking.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserService userService;
    @Autowired private UserDetailsServiceImpl userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getName(),
                    loginRequest.getPassword()
                )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getName());
            String token = jwtUtil.generateToken(userDetails);

            // Find the user details like role and id
            Optional<User> userOpt = userService.getUserByName(loginRequest.getName());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = userOpt.get();

            // Return a proper JSON response
            return ResponseEntity.ok(
            	    new LoginResponse(token, user.getRole().name(), user.getId(), user.getStatus().name(), user.getName())
            	);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }


 
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User saved = userService.saveUser(user);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    }

