package com.fastx.busbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime paymentDate = LocalDateTime.now();

    private Double amountPaid;

    @Column(name = "payment_mode")
    private String paymentMode;


    @Enumerated(EnumType.STRING)
    private Status paymentStatus = Status.SUCCESS;

    @OneToOne
    @JoinColumn(name = "booking_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Booking booking;


    public enum Status {
        SUCCESS, FAILED, REFUNDED
    }
}
