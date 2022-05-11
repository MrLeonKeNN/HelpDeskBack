package com.epam.ilyankov.helpdesk.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.converter.impl.TicketConverter;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.ExtendedTicketDto;
import com.epam.ilyankov.helpdesk.dto.TicketDto;
import com.epam.ilyankov.helpdesk.enums.Column;
import com.epam.ilyankov.helpdesk.enums.Quantity;
import com.epam.ilyankov.helpdesk.enums.Scope;
import com.epam.ilyankov.helpdesk.enums.Sort;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.exceptions.NoAccessException;
import com.epam.ilyankov.helpdesk.pagination.PaginationHelper;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;
import com.epam.ilyankov.helpdesk.service.api.TicketService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketConverter converter;
	private final CrudRepository<Ticket> crudRepository;
	private final TicketRepository ticketRepository;

	@Override
	@Transactional()
	public Ticket save(TicketDto ticketDto) {
		Ticket ticket = converter.fromDto(ticketDto);

		crudRepository.save(ticket);

		return ticket;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketDto> getAllByRole(User user, Integer page) {
		Integer firstResult = PaginationHelper.getFirstResult(page, Quantity.TEN.label);
		Map<Role, Supplier<List<TicketDto>>> strategyMap = Map.of(Role.Employee,
				() -> converter.toDto(ticketRepository.getAllByEmployee(user, firstResult, Quantity.TEN.label)),
				Role.Manager,
				() -> converter.toDto(ticketRepository.getAllByManager(user, firstResult, Quantity.TEN.label)),
				Role.Engineer,
				() -> converter.toDto(ticketRepository.getAllByEngineer(user, firstResult, Quantity.TEN.label)));

		return Optional.ofNullable(strategyMap.get(user.getRole())).orElse(Collections::emptyList).get();
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketDto> getAllByRole(User user) {
		Map<Role, Supplier<List<TicketDto>>> strategyMap = Map.of(Role.Employee,
				() -> converter.toDto(ticketRepository.getAllByEmployee(user,null,null)), Role.Manager,
				() -> converter.toDto(ticketRepository.getAllByManager(user,null,null)), Role.Engineer,
				() -> converter.toDto(ticketRepository.getAllByEngineer(user,null,null)));

		return Optional.ofNullable(strategyMap.get(user.getRole())).orElse(Collections::emptyList).get();
	}

	@Override
	@Transactional(readOnly = true)
	public TicketDto getByIdDto(Long id, User user) {
		Ticket ticket = ticketRepository.getById(id).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
		if (!forbidden(user, ticket)) {
			throw new NoAccessException("No access");
		}

		return converter.toDto(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	public Ticket getById(Long id) {
		return ticketRepository.getById(id).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
	}

	@Override
	@Transactional
	public Ticket edit(User user, TicketDto ticketDto) {
		Ticket ticket = ticketRepository.getById(ticketDto.getId())
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		if (!ticket.getOwner().equals(user) || ticket.getState() != State.Draft) {
			throw new NoAccessException("No access");
		}

		converter.edit(ticket, ticketDto);
		crudRepository.update(ticket);

		return ticket;
	}

	@Override
	@Transactional
	public void approve(Ticket ticket, User user) {

		if (ticket.getState() != State.New || ticket.getOwner().equals(user)) {
			throw new NoAccessException("No access");
		}

		ticket.setApprover(user);
		ticket.setState(State.Approved);

		crudRepository.update(ticket);
	}

	@Override
	@Transactional
	public void decline(Ticket ticket, User user) {

		if (ticket.getState() != State.New || ticket.getOwner().equals(user)) {
			throw new NoAccessException("No access");
		}

		ticket.setState(State.Declined);
		ticket.setApprover(user);

		crudRepository.update(ticket);
	}

	@Override
	@Transactional
	public void submit(Ticket ticket, User user) {

		if (!ticket.getOwner().equals(user)
				|| ticket.getState() != State.Draft && ticket.getState() != State.Declined) {
			throw new NoAccessException("No access");
		}

		ticket.setState(State.New);

		crudRepository.update(ticket);
	}

	@Override
	@Transactional
	public void cancel(Ticket ticket, User user) {

		if (!validationCancel(ticket, user)) {
			throw new NoAccessException("No access");
		}

		ticket.setState(State.Canceled);

		if (user.getRole() == Role.Manager) {
			ticket.setApprover(user);
		}

		if (user.getRole() == Role.Engineer) {
			ticket.setAssignee(user);
		}

		crudRepository.update(ticket);
	}

	@Override
	@Transactional
	public void assign(Ticket ticket, User user) {

		if (ticket.getState() != State.Approved) {
			throw new NoAccessException("No access");
		}

		ticket.setAssignee(user);
		ticket.setState(State.In_Progress);

		crudRepository.update(ticket);
	}

	@Override
	@Transactional
	public void done(Ticket ticket, User user) {

		if (ticket.getState() != State.In_Progress) {
			throw new NoAccessException("No access");
		}

		ticket.setState(State.Done);

		crudRepository.update(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketDto> sort(Column column, Sort sort, User user, Scope scope, Integer page) {
		List<TicketDto> ticketDtoList;

		if (scope == Scope.MY) {
			ticketDtoList = getByRoleMyTicket(user);
		} else {
			ticketDtoList = getAllByRole(user);
		}

		Map<Column, Supplier<List<TicketDto>>> strategyMap = Map.of(Column.ID, () -> sortByID(ticketDtoList, sort),
				Column.NAME, () -> sortByName(ticketDtoList, sort), Column.DATE, () -> sortByDate(ticketDtoList, sort),
				Column.URGENCY, () -> sortByUrgency(ticketDtoList, sort), Column.STATUS,
				() -> sortByStatus(ticketDtoList, sort));

		return getLimitTicket(page, Optional.ofNullable(strategyMap.get(column)).orElse(Collections::emptyList).get());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketDto> getByRoleMyTicket(User user, Integer page) {
		Integer firstResult = PaginationHelper.getFirstResult(page, Quantity.TEN.label);
		Map<Role, Supplier<List<TicketDto>>> strategyMap = Map.of(Role.Employee,
				() -> converter.toDto(ticketRepository.getAllByEmployee(user, firstResult, Quantity.TEN.label)),
				Role.Manager,
				() -> converter.toDto(ticketRepository.getMyByManager(user, firstResult, Quantity.TEN.label)),
				Role.Engineer,
				() -> converter.toDto(ticketRepository.getMyByEngineer(user, firstResult, Quantity.TEN.label)));

		return Optional.ofNullable(strategyMap.get(user.getRole())).orElse(Collections::emptyList).get();
	}

	@Override
	@Transactional(readOnly = true)
	public List<TicketDto> getByRoleMyTicket(User user) {
		Map<Role, Supplier<List<TicketDto>>> strategyMap = Map.of(Role.Employee,
				() -> converter.toDto(ticketRepository.getAllByEmployee(user,null,null)), Role.Manager,
				() -> converter.toDto(ticketRepository.getMyByManager(user,null,null)), Role.Engineer,
				() -> converter.toDto(ticketRepository.getMyByEngineer(user,null,null)));

		return Optional.ofNullable(strategyMap.get(user.getRole())).orElse(Collections::emptyList).get();
	}

	@Override
	@Transactional(readOnly = true)
	public Long getCountAllByRole(User user) {
		Map<Role, Supplier<Long>> strategyMap = Map.of(Role.Employee,
				() -> ticketRepository.getCountAllByEmployee(user), Role.Manager,
				() -> ticketRepository.getCountAllByManager(user), Role.Engineer,
				() -> ticketRepository.getCountAllByEngineer(user));

		return Optional.ofNullable(strategyMap.get(user.getRole())).orElse(() -> 0L).get();
	}

	@Override
	@Transactional(readOnly = true)
	public Long getCountMyByRole(User user) {
		Map<Role, Supplier<Long>> strategyMap = Map.of(Role.Employee,
				() -> ticketRepository.getCountAllByEmployee(user), Role.Manager,
				() -> ticketRepository.getCountMyByManager(user), Role.Engineer,
				() -> ticketRepository.getCountMyByEngineer(user));

		return Optional.ofNullable(strategyMap.get(user.getRole())).orElse(() -> 0L).get();
	}

	@Override
	@Transactional(readOnly = true)
	public ExtendedTicketDto search(String word, Integer page, Scope scope, User user) {
		List<TicketDto> list;

		if (scope == Scope.MY) {
			list = getByRoleMyTicket(user);
		} else {
			list = getAllByRole(user);
		}
		String worldLowerCase = word.toLowerCase(Locale.ROOT);

		List<TicketDto> sortedList = list.stream()
				.filter(ticketDto -> ticketDto.getId().toString().toLowerCase(Locale.ROOT).contains(worldLowerCase)
						|| ticketDto.getName().toLowerCase(Locale.ROOT).contains(worldLowerCase)
						|| ticketDto.getUrgency().toString().toLowerCase(Locale.ROOT).contains(worldLowerCase)
						|| ticketDto.getState().toLowerCase(Locale.ROOT).contains(worldLowerCase)
						|| ticketDto.getDesiredResolutionDate() != null && ticketDto.getDesiredResolutionDate()
								.toString().toLowerCase(Locale.ROOT).contains(worldLowerCase))
				.collect(Collectors.toList());

		return ExtendedTicketDto.builder().tickets(getLimitTicket(page, sortedList))
				.countTicket((long) sortedList.size()).build();
	}

	@Override
	public Boolean getCanCreate(User user) {
		return user.getRole() == Role.Employee || user.getRole() == Role.Manager;
	}

	private boolean validationCancel(Ticket ticket, User user) {
		boolean decliner = ticket.getOwner().equals(user) && ticket.getState() == State.Declined;
		if (user.getRole() == Role.Employee && ticket.getState() == State.Draft || decliner) {
			return true;
		}

		if (user.getRole() == Role.Manager && ticket.getState() == State.Draft || decliner
				|| !ticket.getOwner().equals(user) && ticket.getState() == State.New) {
			return true;
		}

		return user.getRole() == Role.Engineer && ticket.getState() == State.Approved;
	}

	private List<TicketDto> sortByID(List<TicketDto> ticketDtoList, Sort sort) {
		return sortComparator(ticketDtoList, sort, Comparator.comparing(TicketDto::getId));
	}

	private List<TicketDto> sortByName(List<TicketDto> ticketDtoList, Sort sort) {
		return sortComparator(ticketDtoList, sort, Comparator.comparing(TicketDto::getName));
	}

	private List<TicketDto> sortByUrgency(List<TicketDto> ticketDtoList, Sort sort) {
		return sortComparator(ticketDtoList, sort, Comparator.comparing(TicketDto::getUrgency));
	}

	private List<TicketDto> sortByStatus(List<TicketDto> ticketDtoList, Sort sort) {
		return sortComparator(ticketDtoList, sort, Comparator.comparing(TicketDto::getState));
	}

	private List<TicketDto> sortByDate(List<TicketDto> ticketDtoList, Sort sort) {

		Comparator<TicketDto> comparator = (o1, o2) -> {
			if (o1.getDesiredResolutionDate() == null) {
				if (o2.getDesiredResolutionDate() == null) {
					return 0;
				}
				return -1;
			}
			if (o2.getDesiredResolutionDate() == null) {
				return 1;
			}

			return Long.compare(o1.getDesiredResolutionDate().getTime(), o2.getDesiredResolutionDate().getTime());
		};

		return sortComparator(ticketDtoList, sort, comparator);
	}

	private List<TicketDto> sortComparator(List<TicketDto> ticketDtoList, Sort sort, Comparator<TicketDto> comparing) {
		Map<Sort, Supplier<List<TicketDto>>> strategyMap = Map.of(Sort.ASC,
				() -> ticketDtoList.stream().sorted(comparing).collect(Collectors.toList()), Sort.DESC,
				() -> ticketDtoList.stream().sorted(comparing.reversed()).collect(Collectors.toList()));

		return Optional.ofNullable(strategyMap.get(sort)).orElse(() -> ticketDtoList).get();
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

	List<TicketDto> getLimitTicket(Integer page, List<TicketDto> sortedList) {
		int firstResult = PaginationHelper.getFirstResult(page, Quantity.TEN.label);
		int limit = 0;
		List<TicketDto> sortedLimitList = new LinkedList<>();

		if (!sortedList.isEmpty()) {
			for (int i = 0; i < sortedList.size(); i++) {
				if (i >= firstResult && limit != Quantity.TEN.label) {
					sortedLimitList.add(sortedList.get(i));
					++limit;
				}
			}
		}
		return sortedLimitList;
	}

}
