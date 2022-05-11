package com.epam.ilyankov.helpdesk.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.converter.impl.FeedbackConverter;
import com.epam.ilyankov.helpdesk.domain.Feedback;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.FeedbackDto;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.exceptions.FeedbackExistException;
import com.epam.ilyankov.helpdesk.exceptions.NoAccessException;
import com.epam.ilyankov.helpdesk.repository.api.FeedbackRepository;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;
import com.epam.ilyankov.helpdesk.service.api.FeedbackService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

	private final FeedbackRepository feedbackRepository;
	private final CrudRepository<Feedback> crudRepository;
	private final FeedbackConverter converter;

	@Override
	@Transactional
	public void save(FeedbackDto feedbackDto, Ticket ticket, User user) {
		Optional<Feedback> feedback = feedbackRepository.getByTicketId(ticket.getId());

		if (ticket.getState() != State.Done || !ticket.getOwner().equals(user)) {
			throw new NoAccessException("No access");
		}
		if (feedback.isPresent()) {
			throw new FeedbackExistException("Feedback already exists");
		}
		crudRepository.save(converter.fromDto(feedbackDto));
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackDto getByTicketId(Long ticketId) {
		Feedback feedback = feedbackRepository.getByTicketId(ticketId)
				.orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

		return converter.toDto(feedback);
	}
}
