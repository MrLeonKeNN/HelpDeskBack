package com.epam.ilyankov.helpdesk.converter.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.epam.ilyankov.helpdesk.converter.api.Converter;
import com.epam.ilyankov.helpdesk.domain.Feedback;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.FeedbackDto;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.service.api.UserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FeedbackConverter implements Converter<Feedback, FeedbackDto> {

	private final TicketRepository ticketRepository;
	private final UserService userService;

	@Override
	public FeedbackDto toDto(Feedback feedback) {
		return FeedbackDto.builder()
				.rate(feedback.getRate())
				.description(feedback.getText())
				.build();
	}

	@Override
	public Feedback fromDto(FeedbackDto feedbackDto) {
		User user = userService.getCurrentUser();
		Ticket ticket = ticketRepository.getById(feedbackDto.getTicketId())
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		return Feedback.builder()
				.rate(feedbackDto.getRate())
				.text(feedbackDto.getDescription())
				.date(new Timestamp(System.currentTimeMillis()))
				.ticket(ticket)
				.user(user)
				.build();
	}
}
