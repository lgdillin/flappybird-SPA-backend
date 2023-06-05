package com.sponsky.restfulwebservice.stack.flappybirdfullstack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.User;
import com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.respository.UserRepository;

@SpringBootApplication
public class FlappybirdFullstackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlappybirdFullstackApplication.class, args);
	}
	
	// Insert some data
	@Bean
	CommandLineRunner commandLineRunner(UserRepository users, PasswordEncoder encoder) {
		return args -> {
			users.save(new User("sponsky", encoder.encode("password"), "ADMIN"));
			//users.save(new User("sponsky", "password", "ADMIN"));
			users.save(new User("user2", encoder.encode("password2"), "USER"));
		};
	}

}
