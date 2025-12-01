package com.fastx.busbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String busName;

    @Column(unique = true)
    private String busNumber;

    @Enumerated(EnumType.STRING)
    private BusType busType;

    private String origin;
    private String destination;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private double farePerSeat;
    private int totalSeats;

    private String amenities;

    public enum BusType {
        SEATER_AC,
        SEATER_NON_AC,
        SLEEPER_AC,
        SLEEPER_NON_AC
    }
    public enum Status {
        ACTIVE, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    @JsonIgnore  // ✅ Prevent back-reference loop
    private User operator;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ✅ Prevent loop
    private List<Seat> seats;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ✅ Prevent loop
    private List<Booking> bookings;
}
