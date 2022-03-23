package com.ilyankov.helpdesk.config.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
@PropertySource("classpath:application.properties")
public class JwtProvider {

	@Value("${secret}")
	private String secretWord;

	public String generationAccessToken(String name) {
		return JWT.create().withSubject(name).withExpiresAt(new Date(System.currentTimeMillis() + 432_000_000L))
				.sign(Algorithm.HMAC256(secretWord.getBytes(StandardCharsets.UTF_8)));
	}

	public String validatorToken(String token) {
		return JWT.require(Algorithm.HMAC256(secretWord.getBytes(StandardCharsets.UTF_8))).build().verify(token)
				.getSubject();
	}
}
