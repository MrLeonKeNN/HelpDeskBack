package com.epam.ilyankov.helpdesk.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.converter.impl.HistoryConverter;
import com.epam.ilyankov.helpdesk.domain.history.History;
import com.epam.ilyankov.helpdesk.domain.history.HistoryLog;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.HistoryDto;
import com.epam.ilyankov.helpdesk.enums.Quantity;
import com.epam.ilyankov.helpdesk.exceptions.NoAccessException;
import com.epam.ilyankov.helpdesk.repository.api.HistoryRepository;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;
import com.epam.ilyankov.helpdesk.service.api.HistoryService;

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
				.action(HistoryLog.TICKET_IS_CREATED.meaning).description(HistoryLog.TICKET_IS_CREATED.meaning).build()));
	}

	@Override
	@Transactional
	public void saveHistoryEdit(Long ticketId) {
		crudRepository.save(converter.fromDto(HistoryDto.builder().ticketId(ticketId)
				.action(HistoryLog.TICKET_IS_EDITED.meaning).description(HistoryLog.TICKET_IS_EDITED.meaning).build()));
	}

	@Override
	@Transactional
	public void saveHistoryStatus(Long ticketId, State firstState, State lastState) {
		crudRepository.save(
				converter.fromDto(HistoryDto.builder().ticketId(ticketId).action(HistoryLog.TICKET_STATUS_IS_CHANGED.meaning)
						.description(String.format(HistoryLog.TICKET_STATUS_IS_CHANGED_FROM_X_TO_Y.meaning,
								firstState.meaning, lastState.meaning))
						.build()));
	}

	@Override
	@Transactional
	public void saveHistoryFileAttached(Long ticketId, String nameAttachment) {
		crudRepository
				.save(converter.fromDto(HistoryDto.builder().ticketId(ticketId).action(HistoryLog.FILE_IS_ATTACHED.meaning)
						.description(String.format(HistoryLog.FILE_ID_ATTACHED_NAME.meaning, nameAttachment)).build()));
	}

	@Override
	@Transactional
	public void saveHistoryFileRemove(Long ticketId, String removeName) {
		crudRepository
				.save(converter.fromDto(HistoryDto.builder().ticketId(ticketId).action(HistoryLog.FILE_IS_REMOVED.meaning)
						.description(String.format(HistoryLog.FILE_ID_REMOVED_NAME.meaning, removeName)).build()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<HistoryDto> getByTicketId(Ticket ticket, User user) {
		if (!forbidden(user, ticket)) {
			throw new NoAccessException("No access");
		}
		return converter.toDto(historyRepository.getByTicketId(ticket.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<HistoryDto> getCertainQuantityByTicketId(Ticket ticket, User user, Integer page) {

		if (!forbidden(user, ticket)) {
			throw new NoAccessException("No access");
		}

		int firstResult = page * 5 - 5;

		return converter
				.toDto(historyRepository.getByTicketIdPagination(ticket.getId(), firstResult, Quantity.TEN.label));
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
