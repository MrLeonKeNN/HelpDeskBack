package com.ilyankov.helpdesk.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ilyankov.helpdesk.domain.history.History;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.HistoryDto;
import com.ilyankov.helpdesk.repository.api.TicketRepository;
import com.ilyankov.helpdesk.service.api.UserService;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class HistoryConverterTest {

	@Mock
	private UserService userService;
	@Mock
	private TicketRepository ticketRepository;

	@InjectMocks
	private HistoryConverter convertor;

	@Test
	void fromDto() {
		User user = User.builder().id(1L).build();
		Ticket ticket = Ticket.builder().id(1L).build();
		HistoryDto historyDto = HistoryDto.builder().action("Action").description("Description").ticketId(1L).build();
		History expected = History.builder().action("Action").description("Description").ticket(ticket).user(user)
				.date(new Timestamp(System.currentTimeMillis())).build();

		when(userService.getCurrentUser()).thenReturn(user);
		when(ticketRepository.getById(1L)).thenReturn(Optional.ofNullable(ticket));

		History actual = convertor.fromDto(historyDto);

		assertEquals(expected.getAction(), actual.getAction());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getTicket(), actual.getTicket());
		assertEquals(expected.getUser(), actual.getUser());
		assertTrue(actual.getDate().getTime() - expected.getDate().getTime() < 30);

		verify(ticketRepository, times(1)).getById(1L);
		verify(userService, times(1)).getCurrentUser();
	}

	@Test
	void toDto() {
		User user = User.builder().id(1L).firsName("Alisa").build();
		History history = History.builder().action("Action").description("Description").user(user)
				.date(new Timestamp(System.currentTimeMillis())).build();
		HistoryDto expected = HistoryDto.builder().date(history.getDate().toString()).action("Action")
				.description("Description").firstName("Alisa").build();

		HistoryDto actual = convertor.toDto(history);

		assertEquals(expected, actual);
	}
}
