package com.epam.ilyankov.helpdesk.service.facade.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.CommentDto;
import com.epam.ilyankov.helpdesk.dto.ExtendedTicketDto;
import com.epam.ilyankov.helpdesk.dto.TicketDto;
import com.epam.ilyankov.helpdesk.enums.Column;
import com.epam.ilyankov.helpdesk.enums.Scope;
import com.epam.ilyankov.helpdesk.enums.Sort;
import com.epam.ilyankov.helpdesk.enums.Subject;
import com.epam.ilyankov.helpdesk.service.api.CommentService;
import com.epam.ilyankov.helpdesk.service.api.EmailService;
import com.epam.ilyankov.helpdesk.service.api.HistoryService;
import com.epam.ilyankov.helpdesk.service.api.TicketService;
import com.epam.ilyankov.helpdesk.service.api.UserService;
import com.epam.ilyankov.helpdesk.service.facade.api.TicketFacade;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TicketFacadeImpl implements TicketFacade {

	private final TicketService ticketService;
	private final HistoryService historyService;
	private final UserService userService;
	private final EmailService emailService;
	private final CommentService commentService;

	@Override
	public Long save(TicketDto ticketDto) {
		Ticket ticket = ticketService.save(ticketDto);

		historyService.saveHistoryCreate(ticket.getId());
		if (Objects.nonNull(ticketDto.getComment()) && !ticketDto.getComment().isEmpty()) {
			commentService.save(CommentDto.builder().text(ticketDto.getComment()).ticketId(ticket.getId()).build());
		}

		return ticket.getId();
	}

	@Override
	@Transactional
	public ExtendedTicketDto getAllByRole(Integer page) {
		User user = userService.getCurrentUser();
		return ExtendedTicketDto.builder().countTicket(ticketService.getCountAllByRole(user))
				.canCreate(ticketService.getCanCreate(user)).tickets(ticketService.getAllByRole(user, page)).build();
	}

	@Override
	@Transactional
	public void edit(TicketDto ticketDto) {
		ticketService.edit(userService.getCurrentUser(), ticketDto);
		historyService.saveHistoryEdit(ticketDto.getId());
	}

	@Override
	@Transactional
	public TicketDto get(Long id) {
		return ticketService.getByIdDto(id, userService.getCurrentUser());
	}

	@Override
	@Transactional
	public void approve(Long id) {
		User user = userService.getCurrentUser();
		Ticket ticket = getTicket(id);
		List<String> userEmails = userService.getEmailsByRole(Role.Engineer);
		State firstState = ticket.getState();

		userEmails.add(ticket.getOwner().getEmail());

		ticketService.approve(ticket, user);
		historyService.saveHistoryStatus(ticket.getId(), firstState, ticket.getState());
		emailService.sendMessage(userEmails, emailService.getContext(ticket.getId()),
				Subject.TICKET_WAS_APPROVED.meaning, "Template2");
	}

	@Override
	@Transactional
	public void decline(Long id) {
		User user = userService.getCurrentUser();
		Ticket ticket = getTicket(id);

		State firstState = ticket.getState();

		ticketService.decline(ticket, user);
		historyService.saveHistoryStatus(ticket.getId(), firstState, ticket.getState());
		emailService.sendMessage(
				List.of(ticket.getOwner().getEmail()), emailService.getContext(ticket.getId(),
						ticket.getOwner().getFirsName(), ticket.getOwner().getLastName()),
				Subject.TICKET_WAS_DECLINED.meaning, "Template3");
	}

	@Override
	@Transactional
	public void submit(Long id) {
		User user = userService.getCurrentUser();
		Ticket ticket = getTicket(id);
		State firstState = ticket.getState();

		ticketService.submit(ticket, user);
		historyService.saveHistoryStatus(ticket.getId(), firstState, ticket.getState());
		emailService.sendMessage(userService.getEmailsByRole(Role.Manager), emailService.getContext(ticket.getId()),
				Subject.NEW_TICKET_FOR_APPROVAL.meaning, "TEMPLATE1");
	}

	@Override
	@Transactional
	public void cancel(Long id) {
		User user = userService.getCurrentUser();
		Ticket ticket = getTicket(id);
		State firstState = ticket.getState();

		ticketService.cancel(ticket, user);
		historyService.saveHistoryStatus(ticket.getId(), firstState, ticket.getState());
		if (firstState == State.New) {
			emailService.sendMessage(
					List.of(ticket.getOwner().getEmail()), emailService.getContext(ticket.getId(),
							ticket.getOwner().getFirsName(), ticket.getOwner().getLastName()),
					Subject.TICKET_WAS_CANCELLED.meaning, "Template4");
		}
		if (firstState == State.Approved) {
			emailService.sendMessage(List.of(ticket.getOwner().getEmail(), ticket.getApprover().getEmail()),
					emailService.getContext(ticket.getId()), Subject.TICKET_WAS_CANCELLED.meaning, "TEMPLATE5");
		}
	}

	@Override
	@Transactional
	public void assign(Long id) {
		User user = userService.getCurrentUser();
		Ticket ticket = getTicket(id);
		State firstState = ticket.getState();

		ticketService.assign(ticket, user);
		historyService.saveHistoryStatus(ticket.getId(), firstState, ticket.getState());
	}

	@Override
	@Transactional
	public void done(Long id) {
		User user = userService.getCurrentUser();
		Ticket ticket = getTicket(id);
		State firstState = ticket.getState();

		ticketService.done(ticket, user);
		historyService.saveHistoryStatus(ticket.getId(), firstState, ticket.getState());
		emailService.sendMessage(List.of(ticket.getOwner().getEmail()),
				emailService.getContext(ticket.getId(), ticket.getOwner().getEmail(), ticket.getOwner().getLastName()),
				Subject.TICKET_WAS_DONE.meaning, "TEMPLATE6");
	}

	@Override
	public ExtendedTicketDto getMyByRole(Integer page) {
		User user = userService.getCurrentUser();

		return ExtendedTicketDto.builder().countTicket(ticketService.getCountMyByRole(user))
				.canCreate(ticketService.getCanCreate(user)).tickets(ticketService.getByRoleMyTicket(user, page))
				.build();
	}

	@Override
	public ExtendedTicketDto sort(Column collum, Sort sort, Scope scope, Integer page) {
		User user = userService.getCurrentUser();

		return ExtendedTicketDto.builder().tickets(ticketService.sort(collum, sort, user, scope, page)).build();
	}

	@Override
	public ExtendedTicketDto search(Integer page, Scope scope, String word) {
		User user = userService.getCurrentUser();

		return ticketService.search(word,page,scope,user);
	}

	private Ticket getTicket(Long id) {
		return ticketService.getById(id);
	}
}
