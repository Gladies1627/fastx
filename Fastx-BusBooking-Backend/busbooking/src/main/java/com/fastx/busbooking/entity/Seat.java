package com.fastx.busbooking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String seatNumber;

	private boolean isBooked;
	
	private String bookedByGender; 


    @PrePersist
    public void prePersist() {
        this.isBooked = false;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    @JsonBackReference
    private BusRoute route;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = true)
    @JsonBackReference 
    private Booking booking; 
   
  

    
    

    
    
}
