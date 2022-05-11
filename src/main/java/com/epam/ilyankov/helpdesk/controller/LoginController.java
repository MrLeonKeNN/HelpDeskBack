package com.epam.ilyankov.helpdesk.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.ilyankov.helpdesk.config.security.JwtProvider;
import com.epam.ilyankov.helpdesk.dto.UserDto;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {

	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;

	@PostMapping
	public ResponseEntity<Void> authentication(@Valid @RequestBody UserDto userDto) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
		String jwtToken = jwtProvider.createAccessToken(userDto.getEmail());

		ResponseCookie responseCookie = ResponseCookie.from("JWT", jwtToken).httpOnly(true).secure(true)
				.maxAge(259200000 * 2L).path("/").build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();
	}

	@GetMapping("/success")
	public ResponseEntity<Void> getAuthorization() {
		return ResponseEntity.ok().build();
	}
}
