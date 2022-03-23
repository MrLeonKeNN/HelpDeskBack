package com.ilyankov.helpdesk.service.facade.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.HistoryDto;
import com.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.ilyankov.helpdesk.repository.api.TicketRepository;
import com.ilyankov.helpdesk.service.api.HistoryService;
import com.ilyankov.helpdesk.service.api.UserService;
import com.ilyankov.helpdesk.service.facade.api.HistoryFacade;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class HistoryFacadeImpl implements HistoryFacade {

	private final UserService userService;
	private final TicketRepository ticketRepository;
	private final HistoryService historyService;

	@Override
	@Transactional
	public List<HistoryDto> getByTicketId(Long ticketId) {
		User user = userService.getCurrentUser();
		Ticket ticket = ticketRepository.getById(ticketId)
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		return historyService.getByTicketId(ticket, user);
	}

	@Override
	@Transactional
	public List<HistoryDto> getCertainQuantityByTicketId(Long ticketId, Integer page) {
		User user = userService.getCurrentUser();
		Ticket ticket = ticketRepository.getById(ticketId)
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		return historyService.getCertainQuantityByTicketId(ticket, user, page);
	}
}
