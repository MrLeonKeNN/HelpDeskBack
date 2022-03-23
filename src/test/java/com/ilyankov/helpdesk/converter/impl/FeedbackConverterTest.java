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

import com.ilyankov.helpdesk.domain.Feedback;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.FeedbackDto;
import com.ilyankov.helpdesk.repository.api.TicketRepository;
import com.ilyankov.helpdesk.service.api.UserService;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class FeedbackConverterTest {

	@Mock
	private TicketRepository ticketRepository;
	@Mock
	private UserService userService;

	@InjectMocks
	private FeedbackConverter converter;

	@Test
	void toDto() {
		Feedback feedback = Feedback.builder().rate(4).text("Hello world").build();
		FeedbackDto expected = FeedbackDto.builder().rate(4).description("Hello world").build();

		FeedbackDto actual = converter.toDto(feedback);

		assertEquals(expected, actual);
	}

	@Test
	void fromDto() {
		User user = User.builder().id(1L).build();
		Ticket ticket = Ticket.builder().id(1L).build();
		FeedbackDto feedbackDto = FeedbackDto.builder().rate(4).ticketId(1L).description("Hello").build();
		Feedback expected = Feedback.builder().rate(4).text("Hello").date(new Timestamp(System.currentTimeMillis()))
				.ticket(ticket).user(user).build();

		when(userService.getCurrentUser()).thenReturn(user);
		when(ticketRepository.getById(1L)).thenReturn(Optional.ofNullable(ticket));

		Feedback actual = converter.fromDto(feedbackDto);

		assertEquals(expected.getRate(), actual.getRate());
		assertEquals(expected.getText(), actual.getText());
		assertEquals(expected.getTicket(), actual.getTicket());
		assertEquals(expected.getUser(), actual.getUser());
		assertTrue(actual.getDate().getTime() - expected.getDate().getTime() < 30);

		verify(ticketRepository, times(1)).getById(1L);
		verify(userService, times(1)).getCurrentUser();
	}
}
