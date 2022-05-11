package com.epam.ilyankov.helpdesk.converter.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.epam.ilyankov.helpdesk.converter.api.Converter;
import com.epam.ilyankov.helpdesk.domain.Comment;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.CommentDto;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.service.api.UserService;

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

		return Comment.builder()
				.text(commentDto.getText())
				.ticket(ticket).user(user)
				.date(new Timestamp(System.currentTimeMillis())).build();
	}

	public CommentDto toDto(Comment comment) {
		return CommentDto.builder()
				.date(comment.getDate().toString())
				.firstName(comment.getUser().getFirsName())
				.lastName(comment.getUser().getLastName())
				.text(comment.getText())
				.build();
	}
}
