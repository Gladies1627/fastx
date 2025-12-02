package com.fastx.busbooking.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String email;
    private String password;
    private String contactNumber;
    private String gender;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        PASSENGER, OPERATOR, ADMIN 
    }
    
    @OneToMany(mappedBy = "operator") 
    private List<BusRoute> routes;
    
    @OneToMany(mappedBy = "passenger", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;

    public enum Status {
        VALID, DELETED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.VALID;



}
