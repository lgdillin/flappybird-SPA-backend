package com.sponsky.restfulwebservice.stack.flappybirdfullstack.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.sponsky.restfulwebservice.stack.flappybirdfullstack.security.Jwks;
import com.sponsky.restfulwebservice.stack.flappybirdfullstack.user.UserDetailsJpaService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	private final List<String> allowedOrigins = List.of("http://localhost:3000");
	private final List<String> allowedMethods = List.of("*");
	private final List<String> allowedHeaders = List.of("*", "Authorization", "Content-Type");
	private final String allowedMappings = "/**";
	
	
	private RSAKey rsaKey;
	private final UserDetailsJpaService userDetailsJpaService;
	
	public SecurityConfig(UserDetailsJpaService userDetailsJpaService) {
		this.userDetailsJpaService = userDetailsJpaService;
	}
	
	// returns an authentication manager to use in the controller
	@Bean
	public AuthenticationManager authManager(UserDetailsJpaService userDetailsJpaService) {
		var authProvider = new DaoAuthenticationProvider();
		authProvider.setPasswordEncoder(passwordEncoder());
		authProvider.setUserDetailsService(userDetailsJpaService);
		return new ProviderManager(authProvider);
	}
	

	// Re-configure the default Spring Security filter chain with our own filter chain
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				
				// Cross Origin Request
				.cors(withDefaults())
				
				// Disable cross-site request forgery (Re-apply default configurations)
				.csrf(csrf -> csrf
						
						// Use this method when creating exceptions for regular paths
						.ignoringRequestMatchers(
								"/token",
								"/createuser")
						
						// For H2 DATABASE Implementations ONLY:
						.ignoringRequestMatchers(PathRequest.toH2Console())
						
						// Never disable CSRF protection while leaving session management enabled
						// Doing so will open you up to a CSRF attack
						//.disable()
				)
				
				// By default everything in Spring Security is required to be authenticated
				// Since we are overriding the default configuration,
				// We need to re-apply the default authentication configurations
				.authorizeHttpRequests(auth -> auth
						
						// Deprecated method is antMatchers(), use requestMatchers()
						// Use this method to create exceptions in the auth requirements
						.requestMatchers(
								"/token",
								"/createuser").permitAll()
						
						// For H2 DATABASE Implementations ONLY:
						.requestMatchers(PathRequest.toH2Console()).permitAll()
						
						// May need this?
						//.mvcMatchers("/api/posts/**").permitAll()
						
						// I don't know what this line is for, but it might be useful one day
						.requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
				
						.anyRequest().authenticated()
				)
				
				// This is so Spring Security knows that this is the service to use
				// When it needs to look up a user by username
				.userDetailsService(userDetailsJpaService)
				
				// Allows for interaction with the H2 console
				// If this is missing, the console will load, but all the frames will be dead
				.headers(headers -> headers.frameOptions().sameOrigin())
				
				// Create a state-less session because we are using a REST API
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				
				// Wires in a Bearer Token authentication filter
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				
				// Spring Native login form for testing/debugging security settings
				// (Disable for production/API use)
				//.formLogin(withDefaults())
				
				// Using HTTP basic security to get up and running
				// Or to use with a simple login in order to get back the JWT bearer token
				.httpBasic(withDefaults()) 
				
				// Create the security filter chain
				.build();
	}
	
	// Securely hashes passwords with the BCrypt algorithm for secure storage in a DB
	// doing a nasty little hack to prevent a circular dependency with the user service
	@Bean
	PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		userDetailsJpaService.setPasswordEncoder(passwordEncoder);
		return passwordEncoder;
	}
       
	
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		rsaKey = Jwks.generateRsa();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}
	
	
	//
	@Bean
	JwtDecoder jwtDecoder() throws JOSEException {
		return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
	}
	
	@Bean JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
		return new NimbusJwtEncoder(jwks);
	}
	
	// Manage Cross Origin Requests
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping(allowedMappings)
					.allowedMethods(allowedMethods.get(0))
					.allowedOrigins(allowedOrigins.get(0));
			}
		};
	}
	
	
//	//Set the CORS configuration such that we can communicate with the frontend
//	@Bean
//	UrlBasedCorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(allowedOrigins);
//		configuration.setAllowedMethods(allowedMethods);
//		configuration.setAllowedHeaders(allowedHeaders);
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration(allowedMappings, configuration);
//		return source;
//	}
	
}
	









