package com.ilyankov.helpdesk.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ilyankov.helpdesk.converter.impl.FeedbackConverter;
import com.ilyankov.helpdesk.domain.Feedback;
import com.ilyankov.helpdesk.domain.ticket.State;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.FeedbackDto;
import com.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.ilyankov.helpdesk.exceptions.FeedbackExistException;
import com.ilyankov.helpdesk.exceptions.NoAccessException;
import com.ilyankov.helpdesk.repository.api.FeedbackRepository;
import com.ilyankov.helpdesk.repository.impl.CrudRepository;
import com.ilyankov.helpdesk.service.api.FeedbackService;

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
			throw new NoAccessException();
		}
		if (feedback.isPresent()) {
			throw new FeedbackExistException();
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
