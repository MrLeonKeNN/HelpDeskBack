package com.ilyankov.helpdesk.config.security;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.service.api.UserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final UserService userService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		return userService.getByEmail(authentication.getName())
				.filter(u -> u.getPassword().equals(authentication.getCredentials().toString()))
				.map(this::getUsernamePasswordAuthenticationToken).orElse(null);
	}

	private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(User u) {
		List<SimpleGrantedAuthority> grantedAuthorities = List
				.of(new SimpleGrantedAuthority(String.format("ROLE_%s", u.getRole())));

		return new UsernamePasswordAuthenticationToken(u.getEmail(), u.getPassword(), grantedAuthorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
