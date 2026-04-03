package com.ps.util;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "$jiloilio#lssjcilsacnl%lkjkdscxnewciosznmsdfnewioxcbnkl&*sdfjlcsioej154412645";
	
	private Key getKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}
	
	public String generateToke(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3))
				.signWith(getKey(), SignatureAlgorithm.HS512)
				.compact();
	}
	
	public String getUsername(String token) {
		return getClaims(token).getSubject();
	}
	
	public boolean validateToken(String token, String username) {
		Claims claims = getClaims(token);
		return claims.getSubject().equals(username) && claims.getExpiration().after(new Date());
	}
	
	private Claims getClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}
