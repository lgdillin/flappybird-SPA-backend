package com.sponsky.restfulwebservice.stack.flappybirdfullstack.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.respository.UserRepository;

@Service
public class UserDetailsJpaService implements UserDetailsService {
	
	private final UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	public UserDetailsJpaService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository
				.findByUsername(username)
				.map(UserSecurity::new)
				.orElseThrow(() -> 
					new UsernameNotFoundException("Username not found " + username));
	}
	
	// Creates a new user in the database
	public User createNewUser(User user) {
		user.setUsername(user.getUsername());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles("USER");
		user.setScore(0);
		return userRepository.save(user);
	}
	
	public User setUserProfilePicture(String username, byte[] data) {
		User user = userRepository.findByUsername(username).get();
		user.setImageData(data);
		return userRepository.save(user);
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
