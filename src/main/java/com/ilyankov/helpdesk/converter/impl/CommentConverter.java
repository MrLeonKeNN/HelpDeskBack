package com.ilyankov.helpdesk.converter.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.ilyankov.helpdesk.converter.api.Converter;
import com.ilyankov.helpdesk.domain.Comment;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.CommentDto;
import com.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.ilyankov.helpdesk.repository.api.TicketRepository;
import com.ilyankov.helpdesk.service.api.UserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CommentConverter implements Converter<Comment, CommentDto> {

	private final UserService userService;
	private final TicketRepository ticketRepository;

	@Override
	public Comment fromDto(CommentDto commentDto) {
		User user = userService.getCurrentUser();
		Ticket ticket = ticketRepository.getById(commentDto.getTicketId())
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		return Comment.builder().text(commentDto.getText()).ticket(ticket).user(user)
				.date(new Timestamp(System.currentTimeMillis())).build();
	}

	public CommentDto toDto(Comment comment) {
		return CommentDto.builder().date(comment.getDate().toString()).firstName(comment.getUser().getFirsName())
				.lastName(comment.getUser().getLastName()).text(comment.getText()).build();
	}
}
