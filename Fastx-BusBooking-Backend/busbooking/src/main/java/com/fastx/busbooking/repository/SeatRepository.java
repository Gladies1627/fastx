package com.fastx.busbooking.repository;

import com.fastx.busbooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByRoute_Id(Integer routeId);
    long countByRoute_IdAndIsBookedFalse(Integer routeId);
    
}
