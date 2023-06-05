package com.sponsky.restfulwebservice.stack.flappybirdfullstack.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
	
	private final TokenService tokenService;
	private final AuthenticationManager authenticationManager;
	
	
	public AuthController(TokenService tokenService, AuthenticationManager authenticationManager) {
		this.tokenService = tokenService;
		this.authenticationManager = authenticationManager;
	}
	
	@PostMapping("/token")
	public String token(@RequestBody LoginRequest userLogin) {
//		LOG.debug("Token requested for user: '{}'", authentication.getName());
//		String token = tokenService.generateToken(authentication);
//		LOG.debug("Token Granted {}", token);
		
		
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						userLogin.username(), 
						userLogin.password()));
		
		var token = tokenService.generateToken(authentication);
		
		// System.out.println(token);
		return token;
	}
}
