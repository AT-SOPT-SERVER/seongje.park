package org.sopt.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final SecretKey secretKey;
	private final long expiration;

	public JwtUtil(@Value("${jwt.secret}") String secret,
		           @Value("${jwt.expiration}") long expiration) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		this.expiration = expiration;
	}

	// jwt 토큰 생성

	public String generateToken(Long userId, String email){
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(userId.toString())
			.claim("email", email)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(secretKey)
			.compact();
	}

	// jwt 토큰에서 사용자 id 가져오기
	public Long getUserIdFromToken(String token){
		Claims claims = parseToken(token);
		return Long.parseLong(claims.getSubject());
	}

	private Claims parseToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	// jwt 토큰에서 email 가져오기.
	public String getEmailFromToken(String token){
		Claims claims = parseToken(token);
		return claims.get("email", String.class);
	}

	// jwt 토큰의 유효성 검증

	public boolean validateToken(String token){
		try{
			parseToken(token);
			return true;
		} catch (JwtException | IllegalArgumentException e){
			return false;
		}
	}

	// 토큰 만료되었는지 확인하기
	public boolean isTokenExpired(String token){
		try {
			Claims claims = parseToken(token);
			return claims.getExpiration().before(new Date());
		} catch (JwtException e){
			return true;
		}
	}
}