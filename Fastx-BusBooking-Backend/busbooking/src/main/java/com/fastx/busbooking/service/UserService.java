package com.fastx.busbooking.service;

import com.fastx.busbooking.entity.Booking;
import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.Cancellation;
import com.fastx.busbooking.entity.Payment;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.entity.User.Role;
import com.fastx.busbooking.repository.BookingRepository;
import com.fastx.busbooking.repository.UserRepository;
import com.fastx.busbooking.security.UserDetailsImpl;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired 
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public Optional<User> getUserByName(String name) {
        return userRepository.findByName(name);
    }


    public User saveUser(User user) {
    	 Optional<User> existing = userRepository.findByEmail(user.getEmail());

    	    if (existing.isPresent()) {
    	    	throw new IllegalArgumentException("❌ This email is already registered with a deleted account. Please contact support to restore access.");
    	    }

    	    user.setPassword(passwordEncoder.encode(user.getPassword()));
    	    user.setStatus(User.Status.VALID); // just to be sure
    	    return userRepository.save(user);
    	}

    public Optional<User> findByCredentials(String name, String rawPassword) {
        Optional<User> userOptional = userRepository.findByName(name);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user); 
            }
        }

        return Optional.empty(); 
    }
    @Transactional
    public void deleteUserById(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot delete Admin users.");
        }

        // Mark user as DELETED
        user.setStatus(User.Status.DELETED);

        // Cancel bookings if passenger
        if (user.getRole() == User.Role.PASSENGER && user.getBookings() != null) {
            for (Booking booking : user.getBookings()) {
                if (booking.getStatus() != Booking.Status.CANCELLED) {
                    booking.setStatus(Booking.Status.CANCELLED);
                    booking.setCancellation(createCancellation(booking, user, "Cancelled due to user deletion"));
                }
            }
        }

        // Cancel routes if operator
        if (user.getRole() == User.Role.OPERATOR && user.getRoutes() != null) {
            for (BusRoute route : user.getRoutes()) {
                if (route.getStatus() != BusRoute.Status.CANCELLED) {
                    route.setStatus(BusRoute.Status.CANCELLED);
                    for (Booking booking : route.getBookings()) {
                        if (booking.getStatus() != Booking.Status.CANCELLED) {
                            booking.setStatus(Booking.Status.CANCELLED);
                            booking.setCancellation(createCancellation(booking, booking.getPassenger(), "Cancelled due to route deletion"));
                        }
                    }
                }
            }
        }

        userRepository.save(user);
    }
    private Cancellation createCancellation(Booking booking, User user, String reason) {
        Cancellation c = new Cancellation();
        c.setBooking(booking);
        c.setUser(user);
        c.setCancelledAt(LocalDateTime.now());
        c.setReason(reason);
        c.setRefundAmount(booking.getTotalAmount());
        return c;
    }



    
}
