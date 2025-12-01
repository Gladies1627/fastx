package com.fastx.busbooking.repository;



import com.fastx.busbooking.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByNameAndPassword(String name, String password);

	Optional<User> findByName(String name);

	Optional<User> findByEmail(String email);


}


