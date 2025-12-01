package com.fastx.busbooking.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerCancellationDTO {
    private Integer id;
    private Double refundAmount;
    private String cancelledAt;
    private Integer bookingId;
    private String travelDate;
    private Double originalAmount;
    private List<String> seatNumbers;
    private BusRouteDTO route;
    private String reason;
}
