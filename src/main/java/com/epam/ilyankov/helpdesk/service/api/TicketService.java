package com.epam.ilyankov.helpdesk.service.api;

import java.util.List;

import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.ExtendedTicketDto;
import com.epam.ilyankov.helpdesk.dto.TicketDto;
import com.epam.ilyankov.helpdesk.enums.Column;
import com.epam.ilyankov.helpdesk.enums.Scope;
import com.epam.ilyankov.helpdesk.enums.Sort;

public interface TicketService {

	Ticket save(TicketDto ticketDto);

	List<TicketDto> getAllByRole(User user, Integer page);

	List<TicketDto> getAllByRole(User user);

	Ticket edit(User user, TicketDto ticketDto);

	TicketDto getByIdDto(Long id, User user);

	Ticket getById(Long id);

	void approve(Ticket ticket, User user);

	void decline(Ticket ticket, User user);

	void submit(Ticket ticket, User user);

	void cancel(Ticket ticket, User user);

	void assign(Ticket ticket, User user);

	void done(Ticket ticket, User user);

	List<TicketDto> sort(Column column, Sort sort, User user, Scope scope, Integer page);

	List<TicketDto> getByRoleMyTicket(User user, Integer page);

	List<TicketDto> getByRoleMyTicket(User user);

	Long getCountAllByRole(User user);

	Long getCountMyByRole(User user);

	ExtendedTicketDto search(String word, Integer page, Scope scope, User user);

	Boolean getCanCreate(User user);
}
