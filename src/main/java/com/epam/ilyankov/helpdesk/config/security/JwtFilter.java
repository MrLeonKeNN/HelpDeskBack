package com.epam.ilyankov.helpdesk.config.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.service.api.UserService;

public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final UserService userService;

	public JwtFilter(JwtProvider jwtProvider, UserService userService) {
		this.jwtProvider = jwtProvider;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (Objects.isNull(request.getCookies())) {
			filterChain.doFilter(request, response);
			return;
		}

		List<Cookie> cookieList = Arrays.asList(request.getCookies());
		Cookie token = cookieList.stream().filter(cookie -> cookie.getName().equals("JWT")).findFirst().orElse(null);

		if (Objects.isNull(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		String email;
		try {
			email = jwtProvider.verificationToken(token.getValue());
		} catch (SignatureVerificationException | TokenExpiredException | AlgorithmMismatchException e) {
			filterChain.doFilter(request, response);
			return;
		}
		User user = userService.getByEmail(email).orElse(null);

		if (Objects.nonNull(user)) {
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					user.getEmail(), user.getPassword());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}

		filterChain.doFilter(request, response);
	}
}
