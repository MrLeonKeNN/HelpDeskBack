package com.ilyankov.helpdesk.repository.api;

import java.util.List;
import java.util.Optional;

import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;

public interface TicketRepository {

	List<Ticket> getAllByEmployee(User owner, Integer firstResult, Integer maxResult);

	List<Ticket> getAllByEmployee(User owner);

	List<Ticket> getAllByManager(User manager, Integer firstResult, Integer maxResult);

	List<Ticket> getAllByManager(User manager);

	List<Ticket> getAllByEngineer(User engineer, Integer firstResult, Integer maxResult);

	List<Ticket> getAllByEngineer(User engineer);

	Optional<Ticket> getById(Long id);

	List<Ticket> getMyTicketByManager(User manager, Integer firstResult, Integer maxResult);

	List<Ticket> getMyTicketByManager(User manager);

	List<Ticket> getMyTicketByEngineer(User engineer, Integer firstResult, Integer maxResult);

	List<Ticket> getMyTicketByEngineer(User engineer);

	Long getCountAllByEmployee(User owner);

	Long getCountAllByManager(User manager);

	Long getCountAllByEngineer(User engineer);

	Long getCountMyTicketByManager(User manager);

	Long getCountMyTicketByEngineer(User engineer);
}
