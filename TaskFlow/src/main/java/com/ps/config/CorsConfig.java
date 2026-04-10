package com.ps.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
	
	@Value("${taskflow.cors.allowed.origins}")
	private List<String> allowedOrigin;

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(allowedOrigin);
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(false);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return source;
	}
	
}
