package com.epam.ilyankov.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.ilyankov.helpdesk.converter.impl.CommentConverter;
import com.epam.ilyankov.helpdesk.domain.Comment;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.CommentDto;
import com.epam.ilyankov.helpdesk.repository.api.CommentRepository;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class CommentServiceImplTest {

	@Mock
	private CommentConverter converter;
	@Mock
	private CrudRepository<Comment> crudRepository;
	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentServiceImpl commentService;

	@Test
	void save() {
		Comment comment = Comment.builder().text("123").build();

		when(converter.fromDto(any(CommentDto.class))).thenReturn(comment);

		commentService.save(new CommentDto());

		verify(converter, times(1)).fromDto(any(CommentDto.class));
		verify(crudRepository, times(1)).save(comment);
	}

	@Test
	void getByTicketId() {
		User user = User.builder().id(1L).email("123").password("123").role(Role.Employee).build();
		Ticket ticket = Ticket.builder().id(1L).owner(user).build();
		List<CommentDto> expected = List.of(CommentDto.builder().ticketId(1L).build(),
				CommentDto.builder().ticketId(1L).build(), CommentDto.builder().ticketId(1L).build());

		when(converter.toDto(anyList())).thenReturn(expected);

		List<CommentDto> actual = commentService.getByTicketId(ticket, user);

		assertEquals(expected, actual);

		verify(converter, times(1)).toDto(anyList());
		verify(commentRepository, times(1)).getByTicketId(any());
	}

	@Test
	void getCertainQuantity() {
		User user = User.builder().id(1L).email("123").password("123").role(Role.Employee).build();
		Ticket ticket = Ticket.builder().id(1L).owner(user).build();
		List<CommentDto> expected = List.of(CommentDto.builder().ticketId(1L).build(),
				CommentDto.builder().ticketId(1L).build(), CommentDto.builder().ticketId(1L).build());

		when(converter.toDto(anyList())).thenReturn(expected);

		List<CommentDto> actual = commentService.getCertainQuantityByTicketId(ticket, 5,user);

		assertEquals(expected, actual);

		verify(converter, times(1)).toDto(anyList());
	}
}
