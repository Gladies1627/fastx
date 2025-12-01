package com.fastx.busbooking.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor

public class AdminBookingDTO {
    private int id;
    private String travelDate;
    private double totalAmount;
    private String status;

    private String passengerName;
    private String passengerEmail;

    private List<String> seatNumbers;

    private RouteDTO route;  
    
    public AdminBookingDTO(int id, String travelDate, double totalAmount, String status,
            String passengerName, String passengerEmail,
            List<String> seatNumbers, RouteDTO route) {
this.id = id;
this.travelDate = travelDate;
this.totalAmount = totalAmount;
this.status = status;
this.passengerName = passengerName;
this.passengerEmail = passengerEmail;
this.seatNumbers = seatNumbers;
this.route = route;
}
}
