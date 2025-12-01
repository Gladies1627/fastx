package com.fastx.busbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastx.busbooking.entity.BusRoute;
import com.fastx.busbooking.entity.User;

@Repository
public interface BusRouteRepository extends JpaRepository<BusRoute, Integer> {
	List<BusRoute> findByOriginAndDestination(String origin, String destination);
	List<BusRoute> findByOperator(User operator);
	 List<BusRoute> findByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
	 List<BusRoute> findByOperatorId(Integer operatorId);


	



}
