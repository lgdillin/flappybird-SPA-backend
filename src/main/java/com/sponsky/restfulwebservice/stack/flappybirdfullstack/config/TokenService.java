package com.sponsky.restfulwebservice.stack.flappybirdfullstack.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;


//3 parts to a JWT: header, payload, signature
@Service
public class TokenService {

	private final JwtEncoder encoder;
	
	public TokenService(JwtEncoder encoder) {
		this.encoder = encoder;
	}
	
	// This method will take in an authentication (authenticated user)
	// 
	public String generateToken(Authentication authentication) {
		
		// Create a TimeStamp
		Instant now = Instant.now();
		
		// streams over the authorities, maps them to a granted authority, and generates
		// A string based on those granted authorities
		String scope = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				//.filter(authority -> !authority.startsWith("ROLE"))
				.collect(Collectors.joining(" "));
		
		
		JwtClaimsSet claims = JwtClaimsSet.builder()
				
				// This application self-signs the token, as opposed to large
				// Distributed applications with their own authentication apps/servers,
				// For example, google may have one authentication server for
				// gmail, youtube, drive, etc. Ours is simplified to this app
				.issuer("self") // Who issues the token
				.issuedAt(now) // When was it issued
				.expiresAt(now.plus(1, ChronoUnit.HOURS)) // When does it expire
				.subject(authentication.getName()) // The principal (username)
				.claim("scope", scope) // The scope of mapped authorities granted earlier
				.build();
		
		// using the JWT Encoder parameters from our claims, we get our token value string
		return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}
}
