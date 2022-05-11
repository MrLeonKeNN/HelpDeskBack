package com.epam.ilyankov.helpdesk.converter.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.epam.ilyankov.helpdesk.converter.api.Converter;
import com.epam.ilyankov.helpdesk.domain.history.History;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.HistoryDto;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.service.api.UserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HistoryConverter implements Converter<History, HistoryDto> {

	private final UserService userService;
	private final TicketRepository ticketRepository;

	public History fromDto(HistoryDto historyDto) {
		User user = userService.getCurrentUser();
		Ticket ticket = ticketRepository.getById(historyDto.getTicketId())
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		return History.builder()
				.action(historyDto.getAction())
				.description(historyDto.getDescription())
				.ticket(ticket)
				.user(user)
				.date(new Timestamp(System.currentTimeMillis()))
				.build();
	}

	public HistoryDto toDto(History history) {
		return HistoryDto.builder()
				.date(history.getDate().toString())
				.description(history.getDescription())
				.firstName(history.getUser().getFirsName())
				.lastName(history.getUser().getLastName())
				.action(history.getAction())
				.build();
	}
}
