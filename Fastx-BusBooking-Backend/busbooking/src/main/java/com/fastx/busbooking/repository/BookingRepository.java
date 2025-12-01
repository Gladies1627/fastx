package com.fastx.busbooking.repository;

import com.fastx.busbooking.entity.Booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByPassenger_Id(Integer userId);
    void deleteByPassenger_Id(Integer id);
    List<Booking> findByRoute_Id(Integer routeId);
    @EntityGraph(attributePaths = {"seats", "passenger", "route"})  
    @Query("SELECT DISTINCT b FROM Booking b " +
    	       "JOIN FETCH b.route " +
    	       "JOIN FETCH b.passenger " +
    	       "LEFT JOIN FETCH b.seats")
    	List<Booking> findAllWithDetails();
    void deleteByRoute_Id(Integer routeId);
    List<Booking> findAllByRoute_Operator_Id(Integer operatorId);
    List<Booking> findByPassenger_Id(int passengerId);
    List<Booking> findByPassengerId(Integer id);





}
