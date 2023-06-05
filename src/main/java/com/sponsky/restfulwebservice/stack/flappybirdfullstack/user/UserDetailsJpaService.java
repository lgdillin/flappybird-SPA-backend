package com.sponsky.restfulwebservice.stack.flappybirdfullstack.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.respository.UserRepository;

@Service
public class UserDetailsJpaService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	public UserDetailsJpaService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository
				.findByUsername(username)
				.map(UserSecurity::new)
				.orElseThrow(() -> 
					new UsernameNotFoundException("Username not found " + username));
	}
	
	/// Wrapper classes for the userRepository
	public User save(User user) {
		return userRepository.save(user);
	}
	
	public Optional<User> findById(int id) {
		return userRepository.findById(id);
	}
	
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public List<User> findAll() {
		return userRepository.findAll();
	}

}
