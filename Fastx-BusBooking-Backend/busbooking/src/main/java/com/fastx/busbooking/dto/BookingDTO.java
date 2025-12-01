package com.fastx.busbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private int id;
    private String status;
    private LocalDate travelDate;
    private double totalAmount;
    private List<SeatDTO> seats;
    private BusRouteDTO route;
    
}
