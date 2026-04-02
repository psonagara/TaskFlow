package com.ps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ps.filter.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private SecurityFilter securityFilter;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
		.csrf(csrf -> csrf.disable())
		.cors(cors -> {})
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()
				.anyRequest().authenticated()
				)
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.httpBasic(httpBasic -> httpBasic.disable())
		.formLogin(formLogin -> formLogin.disable())
		.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}
	
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
}
