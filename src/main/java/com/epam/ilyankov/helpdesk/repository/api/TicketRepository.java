package com.epam.ilyankov.helpdesk.repository.api;

import java.util.List;
import java.util.Optional;

import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;

public interface TicketRepository {

	List<Ticket> getAllByEmployee(User owner, Integer firstResult, Integer maxResult);

	List<Ticket> getAllByManager(User manager, Integer firstResult, Integer maxResult);

	List<Ticket> getAllByEngineer(User engineer, Integer firstResult, Integer maxResult);

	Optional<Ticket> getById(Long id);

	List<Ticket> getMyByManager(User manager, Integer firstResult, Integer maxResult);

	List<Ticket> getMyByEngineer(User engineer, Integer firstResult, Integer maxResult);

	Long getCountAllByEmployee(User owner);

	Long getCountAllByManager(User manager);

	Long getCountAllByEngineer(User engineer);

	Long getCountMyByManager(User manager);

	Long getCountMyByEngineer(User engineer);
}
