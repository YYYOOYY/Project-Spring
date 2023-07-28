package com.onyu.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JWTservice {
	
	@Value("${jwt.secret.key}")
	String secretKey;
	
	public String createToken(String loginId) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		
		return JWT.create()
				.withIssuer("...")
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis() + 1000*60*60*24))
				.withClaim("loginId", loginId)
				.sign(algorithm);
	}
	
	public String verifyToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		JWTVerifier verifier = JWT.require(algorithm).build();
		
		DecodedJWT decodedJWT = verifier.verify(token);
		
		log.info(decodedJWT.getIssuer());
		log.info(decodedJWT.getClaim("loginId").asString());
		
		return decodedJWT.getClaim("loginId").asString();
	}
}
