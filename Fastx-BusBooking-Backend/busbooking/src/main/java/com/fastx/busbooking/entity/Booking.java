package com.fastx.busbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime bookingDate = LocalDateTime.now();

    private LocalDate travelDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.CONFIRMED;

    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User passenger;

    @ManyToOne
    @JsonIgnoreProperties("bookings")
    private BusRoute route;


    @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("booking") 
    private List<Seat> seats;
    
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cancellation cancellation;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private Payment payment;

    public enum Status {
        CONFIRMED, CANCELLED
    }
    public User getUser() {
        return passenger;
    }
    public void setUser(User user) {
        this.passenger = user;
    }


}
