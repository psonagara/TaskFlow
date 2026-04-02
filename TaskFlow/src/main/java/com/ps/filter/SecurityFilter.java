package com.ps.filter;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ps.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	private static final Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		LOG.debug("Entering SecurityFilter.doFilterInternal");
		
		String authHeader = request.getHeader("Authorization");
		
		String token = null;
		String username = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtUtil.getUsername(token);
		}

		LOG.debug("Token: {}", token);
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			
			if (jwtUtil.validateToken(token, username)) {
				SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, List.of(authority));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				LOG.debug("Authentication set for user: {}", username);
			} else {
				LOG.warn("Invalid token detected: {}", token);
			}
		}
		
		filterChain.doFilter(request, response);
	}

}
