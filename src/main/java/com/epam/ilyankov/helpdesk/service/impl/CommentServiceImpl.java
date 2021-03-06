package com.epam.ilyankov.helpdesk.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.converter.impl.CommentConverter;
import com.epam.ilyankov.helpdesk.domain.Comment;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.CommentDto;
import com.epam.ilyankov.helpdesk.enums.Quantity;
import com.epam.ilyankov.helpdesk.exceptions.NoAccessException;
import com.epam.ilyankov.helpdesk.repository.api.CommentRepository;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;
import com.epam.ilyankov.helpdesk.service.api.CommentService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentConverter converter;
	private final CrudRepository<Comment> crudRepository;
	private final CommentRepository commentRepository;

	@Override
	@Transactional
	public void save(CommentDto commentDto) {
		crudRepository.save(converter.fromDto(commentDto));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommentDto> getByTicketId(Ticket ticket, User user) {
		if (!forbidden(user, ticket)) {
			throw new NoAccessException("No access");
		}

		return converter.toDto(commentRepository.getByTicketId(ticket.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommentDto> getCertainQuantityByTicketId(Ticket ticket, Integer page, User user) {
		int firstResult = page * 5 - 5;

		if (!forbidden(user, ticket)) {
			throw new NoAccessException("No access");
		}

		return converter
				.toDto(commentRepository.getByTicketIdPagination(ticket.getId(), firstResult, Quantity.TEN.label));
	}

	private boolean forbidden(User user, Ticket ticket) {
		if (user.getRole() == Role.Employee) {
			return ticket.getOwner().equals(user);
		}
		if (user.getRole() == Role.Manager) {
			return ticket.getOwner().equals(user) || ticket.getState() == State.New
					|| Objects.nonNull(ticket.getApprover()) && ticket.getApprover().equals(user);
		}
		if (user.getRole() == Role.Engineer) {
			return ticket.getState() == State.Approved
					|| Objects.nonNull(ticket.getAssignee()) && ticket.getAssignee().equals(user);
		}
		return false;
	}
}
