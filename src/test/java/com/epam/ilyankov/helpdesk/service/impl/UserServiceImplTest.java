package com.epam.ilyankov.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.repository.api.UserRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void getByEmail() {
		User actual = new User(1L, "123", "123", Role.Manager, "123@gmail.com", "123");

		when(userRepository.getByEmail("123")).thenReturn(Optional.of(actual));

		User expected = userService.getByEmail("123").get();

		assertEquals(expected, actual);
		verify(userRepository, times(1)).getByEmail("123");
	}

	@Test
	void getEmailsByRole() {
		List<String> actual = List.of("One", "Two", "Three");

		when(userRepository.getEmailsByRole(any())).thenReturn(actual);

		List<String> expected = userService.getEmailsByRole(Role.Manager);

		assertEquals(expected, actual);
		verify(userRepository, times(1)).getEmailsByRole(Role.Manager);
	}
}
