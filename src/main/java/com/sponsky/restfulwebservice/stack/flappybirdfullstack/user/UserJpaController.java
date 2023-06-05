package com.sponsky.restfulwebservice.stack.flappybirdfullstack.user;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.respository.UserRepository;

@RestController
public class UserJpaController {
	
	// private UserRepository userRepository;
	private final UserDetailsJpaService userDetailsJpaService;
	
	public UserJpaController(UserDetailsJpaService userDetailsJpaService) {
		super();
		this.userDetailsJpaService = userDetailsJpaService;
	}
	
	@GetMapping("/testauth")
	public String testAuth(Authentication authentication) {
		return "Hello, " + authentication.getName(); 
	}
	
	// @PreAuthorize("hasRole('ADMIN')")
	// Create a new user
	@PostMapping(path = "/createuser")
	public User createUser(@RequestBody User user) {
		user.setUsername(user.getUsername());
		user.setPassword("placeholder");
		user.setRoles("USER");
		return userDetailsJpaService.save(user);
	}

	
	// Retrieve the details of a users public profile
	@GetMapping("/users/{id}/profile")
	public User retrieveProfile(@PathVariable int id) {
		return userDetailsJpaService.findById(id).get();
	}
	
	// Add a User Score
	@PutMapping("/users/{username}/game/{score}")
	public void submitScore(@PathVariable String username, @PathVariable int score) {
		User user = userDetailsJpaService.findByUsername(username).get();
		Integer userScore = user.getScore();
		Integer newScore = score > userScore ? score : userScore; // this op returns a boolean i guess
		
		user.setDate(LocalDate.now());
		user.setScore(newScore);
		userDetailsJpaService.save(user);
	}
	 
	@PutMapping(path="/{username}/profile")
	public User testMethod(@PathVariable String username, @RequestBody String imageData) {
		User user = userDetailsJpaService.findByUsername(username).get();
		
		byte[] base64EncodedData = Base64.getEncoder().encode(imageData.getBytes());
		byte[] decodedImageData = Base64.getDecoder().decode(new String(base64EncodedData).getBytes());
		user.setImageData(decodedImageData);
		return userDetailsJpaService.save(user);
	}
	
	@GetMapping(path="/{username}/profile/image")
	public byte[] retrieveProfilePicture(@PathVariable String username) {
		User user = userDetailsJpaService.findByUsername(username).get();
		return user.getImageData();
	}
	
	@GetMapping(path="/leaderboard")
	public List<User> retrieveLeaderboard() {
				
		List<User> users = userDetailsJpaService.findAll();
		users.removeIf(user -> user.getScore().equals(0));
		
		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(User u1, User u2) {return u1.getScore().compareTo(u2.getScore());}
		});
		
		Collections.reverse(users);
		for(User u : users) {
			System.out.println(u.toString());
		}
		return users;
	}
	
}
