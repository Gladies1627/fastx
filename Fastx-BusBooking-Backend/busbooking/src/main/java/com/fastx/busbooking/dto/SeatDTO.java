package com.fastx.busbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatDTO {
    private int id;
    private String seatNumber;
    private boolean booked;
    private String bookedByGender;
}
