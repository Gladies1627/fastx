package com.fastx.busbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteDTO {
    private int id;
    private String busName;
    private String busType;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double farePerSeat;
    private String status;

}

