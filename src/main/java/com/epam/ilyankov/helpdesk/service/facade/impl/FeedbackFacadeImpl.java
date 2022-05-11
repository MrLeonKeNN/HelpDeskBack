package com.epam.ilyankov.helpdesk.service.facade.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.FeedbackDto;
import com.epam.ilyankov.helpdesk.enums.Subject;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.service.api.EmailService;
import com.epam.ilyankov.helpdesk.service.api.FeedbackService;
import com.epam.ilyankov.helpdesk.service.api.UserService;
import com.epam.ilyankov.helpdesk.service.facade.api.FeedbackFacade;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FeedbackFacadeImpl implements FeedbackFacade {

	private final TicketRepository ticketRepository;
	private final UserService userService;
	private final FeedbackService feedbackService;
	private final EmailService emailService;

	@Override
	@Transactional
	public void save(FeedbackDto feedbackDto) {
		Ticket ticket = ticketRepository.getById(feedbackDto.getTicketId())
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
		User user = userService.getCurrentUser();

		feedbackService.save(feedbackDto, ticket, user);
		emailService.sendMessage(
				List.of(ticket.getAssignee().getEmail()), emailService.getContext(ticket.getId(),
						ticket.getAssignee().getFirsName(), ticket.getAssignee().getLastName()),
				Subject.FEEDBACK_WAS_PROVIDED.meaning, "TEMPLATE7");
	}

}
