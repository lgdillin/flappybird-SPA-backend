package com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.User;

public interface UserRepository extends JpaRepository<User, Integer> { 
	// List<User> findByUsername(String username); 
	Optional<User> findByUsername(String username);
}
