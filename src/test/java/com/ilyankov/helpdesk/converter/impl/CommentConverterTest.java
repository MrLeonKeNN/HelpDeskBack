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

import com.ilyankov.helpdesk.domain.Comment;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.CommentDto;
import com.ilyankov.helpdesk.repository.api.TicketRepository;
import com.ilyankov.helpdesk.service.api.UserService;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class CommentConverterTest {

	@Mock
	private UserService userService;
	@Mock
	private TicketRepository ticketRepository;

	@InjectMocks
	private CommentConverter converter;

	@Test
	void fromDto() {
		User user = User.builder().id(1L).build();
		Ticket ticket = Ticket.builder().id(1L).build();
		CommentDto commentDto = CommentDto.builder().ticketId(1L).text("Hello world").build();
		Comment expected = Comment.builder().text(commentDto.getText()).ticket(ticket).user(user)
				.date(new Timestamp(System.currentTimeMillis())).build();

		when(userService.getCurrentUser()).thenReturn(user);
		when(ticketRepository.getById(1L)).thenReturn(Optional.ofNullable(ticket));

		Comment actual = converter.fromDto(commentDto);

		assertEquals(expected.getText(), actual.getText());
		assertTrue(expected.getDate().getTime() - actual.getDate().getTime() < 30);
		assertEquals(expected.getTicket(), actual.getTicket());
		assertEquals(expected.getUser(), actual.getUser());

		verify(userService, times(1)).getCurrentUser();
		verify(ticketRepository, times(1)).getById(1L);
	}

	@Test
	void toDto() {
		User user = User.builder().id(1L).firsName("Lena").build();
		Ticket ticket = Ticket.builder().id(1L).build();
		Comment comment = Comment.builder().text("123").ticket(ticket).user(user)
				.date(new Timestamp(System.currentTimeMillis())).build();
		CommentDto expected = CommentDto.builder().date(comment.getDate().toString()).firstName("Lena").text("123")
				.build();

		CommentDto actual = converter.toDto(comment);

		assertEquals(expected, actual);
	}
}
