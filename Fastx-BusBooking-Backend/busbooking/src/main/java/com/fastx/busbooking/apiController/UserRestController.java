package com.fastx.busbooking.apiController;

import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.repository.UserRepository;
import com.fastx.busbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired 
    private UserRepository userRepository;
    @Autowired 
    private UserService userService;


    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

  

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/encode-passwords")
    public String encodePasswords() {
        List<User> users = userRepository.findAll();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        for (User user : users) {
            if (!user.getPassword().startsWith("$2a")) {
                user.setPassword(encoder.encode(user.getPassword()));
            }
        }

        userRepository.saveAll(users);
        return "Passwords encoded successfully!";
    }
}
