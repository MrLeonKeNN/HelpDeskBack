package com.ilyankov.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ilyankov.helpdesk.converter.impl.FeedbackConverter;
import com.ilyankov.helpdesk.domain.Feedback;
import com.ilyankov.helpdesk.domain.ticket.State;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.FeedbackDto;
import com.ilyankov.helpdesk.exceptions.NoAccessException;
import com.ilyankov.helpdesk.repository.api.FeedbackRepository;
import com.ilyankov.helpdesk.repository.impl.CrudRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class FeedbackServiceImplTest {

	@Mock
	private FeedbackRepository feedbackRepository;
	@Mock
	private CrudRepository<Feedback> crudRepository;
	@Mock
	private FeedbackConverter converter;

	@InjectMocks
	private FeedbackServiceImpl feedbackService;

	@Test
	void save() {
		User owner = User.builder().id(2L).build();
		Ticket ticket = Ticket.builder().state(State.Done).owner(owner).build();

		Feedback feedback = Feedback.builder().id(2L).text("123").build();

		when(converter.fromDto(any(FeedbackDto.class))).thenReturn(feedback);

		feedbackService.save(new FeedbackDto(), ticket, owner);
		verify(crudRepository, times(1)).save(feedback);
	}

	@Test
	void saveNoAccessException() {
		User owner = User.builder().id(2L).build();
		Ticket ticket = Ticket.builder().state(State.Canceled).owner(owner).build();

		assertThrows(NoAccessException.class, () -> feedbackService.save(new FeedbackDto(), ticket, owner));
	}

	@Test
	void getByTicketId() {
		Feedback feedback = Feedback.builder().id(2L).text("123").build();
		FeedbackDto expected = FeedbackDto.builder().ticketId(2L).description("123").build();

		when(feedbackRepository.getByTicketId(any())).thenReturn(Optional.ofNullable(feedback));
		when(converter.toDto(any(Feedback.class))).thenReturn(expected);

		FeedbackDto actual = feedbackService.getByTicketId(1L);

		assertEquals(expected, actual);
		verify(feedbackRepository, times(1)).getByTicketId(1L);
		verify(converter, times(1)).toDto(any(Feedback.class));
	}
}
