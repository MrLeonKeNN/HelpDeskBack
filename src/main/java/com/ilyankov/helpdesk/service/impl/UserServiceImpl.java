package com.ilyankov.helpdesk.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ilyankov.helpdesk.domain.user.Role;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.ilyankov.helpdesk.repository.api.UserRepository;
import com.ilyankov.helpdesk.service.api.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public Optional<User> getByEmail(String email) {
		return userRepository.getByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getEmailsByRole(Role role) {
		return userRepository.getEmailsByRole(role);
	}

	@Override
	@Transactional(readOnly = true)
	public User getCurrentUser() {
		return getByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
	}
}
