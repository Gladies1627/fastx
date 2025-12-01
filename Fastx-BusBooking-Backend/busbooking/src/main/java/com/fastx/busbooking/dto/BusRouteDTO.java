
package com.fastx.busbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusRouteDTO {
    private int id;
    private String busName;
    private String busNumber;
    private String busType;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double farePerSeat;
    private int totalSeats;
    private String amenities;
    private String status;
    

}
