package com.epam.ilyankov.helpdesk.service.facade.impl;

import java.util.List;

import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.CommentDto;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.service.api.CommentService;
import com.epam.ilyankov.helpdesk.service.api.UserService;
import com.epam.ilyankov.helpdesk.service.facade.api.CommentFacade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Component
public class CommentFacadeImpl implements CommentFacade {

	private final UserService userService;
	private final TicketRepository ticketRepository;
	private final CommentService commentService;

	@Override
	@Transactional
	public List<CommentDto> getCertainQuantityByTicketId(Long ticketId, Integer page) {
		User user = userService.getCurrentUser();
		Ticket ticket = ticketRepository.getById(ticketId)
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		return commentService.getCertainQuantityByTicketId(ticket, page, user);
	}

	@Override
	@Transactional
	public List<CommentDto> getByTicketId(Long ticketId) {
		User user = userService.getCurrentUser();
		Ticket ticket = ticketRepository.getById(ticketId)
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		return commentService.getByTicketId(ticket, user);
	}
}
