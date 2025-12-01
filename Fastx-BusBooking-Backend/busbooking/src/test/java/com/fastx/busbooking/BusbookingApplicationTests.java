package com.fastx.busbooking;

import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@SpringBootTest
public class BusbookingApplicationTests {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setName("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password123");
        testUser.setContactNumber("9876543210");
        testUser.setGender("Other");
        testUser.setAddress("Test Address");
        testUser.setRole(User.Role.PASSENGER);

        userRepository.save(testUser);
    }

    @Test
    void testUserRegistration() {
        User user = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(user);
        assertEquals("testuser", user.getName());
    }

    @Test
    void testUserLoginWithValidCredentials() {
        boolean matched = userRepository.findAll().stream()
                .filter(u -> u.getName().equals("testuser"))
                .anyMatch(u -> u.getPassword().equals("password123"));

        assertTrue(matched, "Login should succeed with valid credentials");
    }
    @Test
    void testFindByName() {
        Optional<User> found = userRepository.findByName("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("testuser@example.com");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getName());
    }
    @Test
    void testGetAllUsers() {
        var users = userRepository.findAll();
        assertFalse(users.isEmpty());
    }
   
    @Test
    void testGettersAndSetters() {
        User user = new User();

        user.setId(2);
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("pass456");
        user.setContactNumber("1234567890");
        user.setGender("Male");
        user.setAddress("Hosur");
        user.setRole(User.Role.OPERATOR);

        assertEquals(2, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("pass456", user.getPassword());
        assertEquals("1234567890", user.getContactNumber());
        assertEquals("Male", user.getGender());
        assertEquals("Hosur", user.getAddress());
        assertEquals(User.Role.OPERATOR, user.getRole());
    }


    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

}
