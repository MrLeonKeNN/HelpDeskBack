package com.ilyankov.helpdesk.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ilyankov.helpdesk.converter.impl.HistoryConverter;
import com.ilyankov.helpdesk.domain.history.History;
import com.ilyankov.helpdesk.domain.history.Story;
import com.ilyankov.helpdesk.domain.ticket.State;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.Role;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.HistoryDto;
import com.ilyankov.helpdesk.enums.Quantity;
import com.ilyankov.helpdesk.exceptions.NoAccessException;
import com.ilyankov.helpdesk.repository.api.HistoryRepository;
import com.ilyankov.helpdesk.repository.impl.CrudRepository;
import com.ilyankov.helpdesk.service.api.HistoryService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HistoryServiceImpl implements HistoryService {

	private final HistoryConverter converter;
	private final CrudRepository<History> crudRepository;
	private final HistoryRepository historyRepository;

	@Override
	@Transactional
	public void saveHistoryCreate(Long ticketId) {
		crudRepository.save(converter.fromDto(HistoryDto.builder().ticketId(ticketId)
				.action(Story.TICKET_IS_CREATED.meaning).description(Story.TICKET_IS_CREATED.meaning).build()));
	}

	@Override
	@Transactional
	public void saveHistoryEdit(Long ticketId) {
		crudRepository.save(converter.fromDto(HistoryDto.builder().ticketId(ticketId)
				.action(Story.TICKET_IS_EDITED.meaning).description(Story.TICKET_IS_EDITED.meaning).build()));
	}

	@Override
	@Transactional
	public void saveHistoryStatus(Long ticketId, State firstState, State lastState) {
		crudRepository.save(
				converter.fromDto(HistoryDto.builder().ticketId(ticketId).action(Story.TICKET_STATUS_IS_CHANGED.meaning)
						.description(String.format(Story.TICKET_STATUS_IS_CHANGED_FROM_X_TO_Y.meaning,
								firstState.meaning, lastState.meaning))
						.build()));
	}

	@Override
	@Transactional
	public void saveHistoryFileAttached(Long ticketId, String nameAttachment) {
		crudRepository
				.save(converter.fromDto(HistoryDto.builder().ticketId(ticketId).action(Story.FILE_IS_ATTACHED.meaning)
						.description(String.format(Story.FILE_ID_ATTACHED_NAME.meaning, nameAttachment)).build()));
	}

	@Override
	@Transactional
	public void saveHistoryFileRemove(Long ticketId, String removeName) {
		crudRepository
				.save(converter.fromDto(HistoryDto.builder().ticketId(ticketId).action(Story.FILE_IS_REMOVED.meaning)
						.description(String.format(Story.FILE_ID_REMOVED_NAME.meaning, removeName)).build()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<HistoryDto> getByTicketId(Ticket ticket, User user) {
		if (!forbidden(user, ticket)) {
			throw new NoAccessException();
		}
		return converter.toDto(historyRepository.getByTicketId(ticket.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<HistoryDto> getCertainQuantityByTicketId(Ticket ticket, User user, Integer page) {

		if (!forbidden(user, ticket)) {
			throw new NoAccessException();
		}

		int firstResult = page * 5 - 5;

		return converter
				.toDto(historyRepository.getByTicketIdQuantity(ticket.getId(), firstResult, Quantity.TEN.label));
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
