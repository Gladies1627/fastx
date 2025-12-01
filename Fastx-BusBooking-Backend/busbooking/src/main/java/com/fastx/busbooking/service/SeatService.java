

package com.fastx.busbooking.service;

import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.Seat;
import com.fastx.busbooking.entity.User;
import com.fastx.busbooking.repository.BusRouteRepository;
import com.fastx.busbooking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private BusRouteRepository busRouteRepository;
    

    public void generateSeatsForRoute(BusRoute route, int totalSeats) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("S" + i);
            seat.setRoute(route); // ✅ Use the BusRoute object directly
            seat.setBooked(false);
            seat.setBookedByGender(null); 
            seats.add(seat);
        }
        seatRepository.saveAll(seats);
    }


    public List<Seat> getSeatsByRoute(Integer routeId) {
        return seatRepository.findByRoute_Id(routeId);
    }

    public long getAvailableSeatsCount(Integer routeId) {
        return seatRepository.countByRoute_IdAndIsBookedFalse(routeId);
    }
    

    public List<Seat> getSeatsByIds(List<Integer> seatIds) {
        return seatRepository.findAllById(seatIds);
    }


    // Book selected seats
    public void bookSeats(Integer routeId, List<Integer> seatIds, User passenger) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        for (Seat seat : seats) {
            if (seat.isBooked()) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is already booked!");
            }

            seat.setBooked(true);
            seat.setBookedByGender(passenger.getGender());
            seatRepository.save(seat);
        }
    }
}
