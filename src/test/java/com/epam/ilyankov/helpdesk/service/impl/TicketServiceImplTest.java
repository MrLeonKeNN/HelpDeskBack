package com.epam.ilyankov.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.ilyankov.helpdesk.converter.impl.TicketConverter;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.ticket.Urgency;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.TicketDto;
import com.epam.ilyankov.helpdesk.enums.Column;
import com.epam.ilyankov.helpdesk.enums.Scope;
import com.epam.ilyankov.helpdesk.enums.Sort;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.exceptions.NoAccessException;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class TicketServiceImplTest {

	@Mock
	private TicketConverter converter;
	@Mock
	private CrudRepository<Ticket> crudRepository;
	@Mock
	private TicketRepository ticketRepository;

	@InjectMocks
	private TicketServiceImpl ticketService;

	@Test
	void save() {
		TicketDto ticketDto = TicketDto.builder().id(1L).build();
		Ticket ticket = Ticket.builder().id(1L).build();

		when(converter.fromDto(ticketDto)).thenReturn(ticket);

		ticketService.save(ticketDto);

		verify(converter, times(1)).fromDto(ticketDto);
	}

	@Test
	void getByRoleEmployee() {
		User user = User.builder().id(1L).email("123").password("123").role(Role.Employee).build();
		List<Ticket> tickets = List.of(Ticket.builder().id(1L).name("test").build());
		List<TicketDto> expected = List.of(TicketDto.builder().id(1L).name("test").build());

		when(ticketRepository.getAllByEmployee(user, 0, 10)).thenReturn(tickets);
		when(converter.toDto(tickets)).thenReturn(expected);

		List<TicketDto> actual = ticketService.getAllByRole(user, 1);

		assertEquals(expected, actual);
	}

	@Test
	void getByRoleManager() {
		User user = User.builder().id(1L).email("123").password("123").role(Role.Manager).build();
		List<Ticket> tickets = List.of(Ticket.builder().id(1L).name("test").build());
		List<TicketDto> expected = List.of(TicketDto.builder().id(1L).name("test").build());

		when(ticketRepository.getAllByManager(user, 0, 10)).thenReturn(tickets);
		when(converter.toDto(tickets)).thenReturn(expected);

		List<TicketDto> actual = ticketService.getAllByRole(user, 1);

		assertEquals(expected, actual);
	}

	@Test
	void getByRoleEngineer() {
		User user = User.builder().id(1L).email("123").password("123").role(Role.Engineer).build();
		List<Ticket> tickets = List.of(Ticket.builder().id(1L).name("test").build());
		List<TicketDto> expected = List.of(TicketDto.builder().id(1L).name("test").build());

		when(ticketRepository.getAllByEngineer(user, 0, 10)).thenReturn(tickets);
		when(converter.toDto(tickets)).thenReturn(expected);

		List<TicketDto> actual = ticketService.getAllByRole(user, 1);

		assertEquals(expected, actual);
	}

	@Test
	void getByIdDto() {
		User user = User.builder().id(1L).email("123").password("123").role(Role.Employee).build();
		Optional<Ticket> ticket = Optional.ofNullable(Ticket.builder().id(1L).name("123").owner(user).build());
		TicketDto expected = TicketDto.builder().id(1L).name("123").build();

		when(ticketRepository.getById(1L)).thenReturn(ticket);
		when(converter.toDto(ticket.get())).thenReturn(expected);

		TicketDto actual = ticketService.getByIdDto(1L, user);

		assertEquals(expected, actual);
		verify(ticketRepository, times(1)).getById(1L);
		verify(converter, times(1)).toDto(ticket.get());
	}

	@Test
	void getByIdDtoNull() {
		Optional<Ticket> expected = Optional.empty();
		User user = User.builder().id(1L).email("123").password("123").role(null).build();
		when(ticketRepository.getById(1L)).thenReturn(expected);

		assertThrows(EntityNotFoundException.class, () -> {
			ticketService.getByIdDto(1L, user);
		});
	}

	@Test
	void getById() {
		Ticket expected = Ticket.builder().id(1L).name("123").build();

		when(ticketRepository.getById(1L)).thenReturn(Optional.ofNullable(expected));

		Ticket actual = ticketService.getById(1L);

		assertEquals(expected, actual);
		verify(ticketRepository, times(1)).getById(1L);
	}

	@Test
	void edit() {
		TicketDto ticketDto = TicketDto.builder().id(1L).name("123").build();
		User user = User.builder().id(1L).email("123").build();
		Optional<Ticket> expected = Optional
				.ofNullable(Ticket.builder().id(1L).owner(user).state(State.Draft).name("123").build());

		when(ticketRepository.getById(1L)).thenReturn(expected);

		Ticket actual = ticketService.edit(user, ticketDto);

		assertEquals(expected.get(), actual);
		verify(crudRepository, times(1)).update(expected.get());
	}

	@Test
	void approve() {
		User approver = User.builder().id(2L).role(Role.Manager).email("222").build();
		User owner = User.builder().id(1L).email("123").build();
		Ticket ticket = Ticket.builder().id(1L).state(State.New).owner(owner).build();

		ticketService.approve(ticket, approver);

		verify(crudRepository, times(1))
				.update(Ticket.builder().id(1L).state(State.Approved).owner(owner).approver(approver).build());
	}

	@Test
	void approveNoAccessException() {
		User owner = User.builder().id(2L).role(Role.Manager).build();
		Ticket ticket = Ticket.builder().state(State.Declined).owner(owner).build();

		assertThrows(NoAccessException.class, () -> {
			ticketService.approve(ticket, owner);
		});
	}

	@Test
	void decline() {
		User approver = User.builder().id(2L).role(Role.Manager).email("222").build();
		User owner = User.builder().id(1L).email("123").build();
		Ticket ticket = Ticket.builder().id(1L).state(State.New).owner(owner).build();

		ticketService.decline(ticket, approver);

		verify(crudRepository, times(1))
				.update(Ticket.builder().id(1L).state(State.Declined).owner(owner).approver(approver).build());
	}

	@Test
	void declineNoAccessException() {
		User owner = User.builder().id(2L).role(Role.Manager).build();
		Ticket ticket = Ticket.builder().state(State.Declined).owner(owner).build();

		assertThrows(NoAccessException.class, () -> {
			ticketService.decline(ticket, owner);
		});
	}

	@Test
	void submit() {
		User owner = User.builder().id(1L).email("123").build();
		Ticket ticket = Ticket.builder().id(1L).state(State.Draft).owner(owner).build();

		ticketService.submit(ticket, owner);

		verify(crudRepository, times(1))
				.update(Ticket.builder().id(1L).state(State.New).owner(owner).approver(owner).build());
	}

	@Test
	void submitNoAccessException() {
		User owner = User.builder().id(2L).role(Role.Manager).build();
		Ticket ticket = Ticket.builder().state(State.Done).owner(owner).build();

		assertThrows(NoAccessException.class, () -> {
			ticketService.submit(ticket, owner);
		});
	}

	@Test
	void cancel() {
		User approver = User.builder().id(2L).role(Role.Manager).email("222").build();
		User owner = User.builder().id(1L).email("123").role(Role.Employee).build();
		Ticket ticket = Ticket.builder().id(1L).state(State.New).owner(owner).build();

		ticketService.cancel(ticket, approver);

		verify(crudRepository, times(1))
				.update(Ticket.builder().id(1L).state(State.Canceled).owner(owner).approver(owner).build());
	}

	@Test
	void cancelNoAccessException() {
		User owner = User.builder().id(2L).role(Role.Engineer).build();
		Ticket ticket = Ticket.builder().state(State.Canceled).owner(owner).build();

		assertThrows(NoAccessException.class, () -> {
			ticketService.cancel(ticket, owner);
		});
	}

	@Test
	void assign() {
		User approver = User.builder().id(2L).role(Role.Manager).email("222").build();
		User assign = User.builder().id(1L).email("123").role(Role.Employee).build();
		Ticket ticket = Ticket.builder().id(1L).state(State.Approved).approver(approver).build();

		ticketService.assign(ticket, assign);

		verify(crudRepository, times(1))
				.update(Ticket.builder().id(1L).state(State.In_Progress).approver(approver).assignee(assign).build());
	}

	@Test
	void assignNoAccessException() {
		User owner = User.builder().id(2L).role(Role.Engineer).build();
		Ticket ticket = Ticket.builder().state(State.Canceled).owner(owner).build();

		assertThrows(NoAccessException.class, () -> {
			ticketService.assign(ticket, owner);
		});
	}

	@Test
	void done() {
		User approver = User.builder().id(2L).role(Role.Manager).email("222").build();
		User assign = User.builder().id(1L).email("123").role(Role.Employee).build();
		Ticket ticket = Ticket.builder().id(1L).state(State.In_Progress).approver(approver).build();

		ticketService.done(ticket, assign);

		verify(crudRepository, times(1))
				.update(Ticket.builder().id(1L).state(State.Done).approver(approver).assignee(assign).build());
	}

	@Test
	void doneNoAccessException() {
		User owner = User.builder().id(2L).role(Role.Engineer).build();
		Ticket ticket = Ticket.builder().state(State.Canceled).owner(owner).build();

		assertThrows(NoAccessException.class, () -> {
			ticketService.done(ticket, owner);
		});
	}

	@Test
	void sortIdAsc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> expected = List.of(
				TicketDto.builder().id(1L).name("Hello").desiredResolutionDate(new Date(System.currentTimeMillis()))
						.urgency(Urgency.High).state(State.In_Progress.meaning).build(),
				TicketDto.builder().id(2L).name("Zello")
						.desiredResolutionDate(new Date(System.currentTimeMillis() + 100500)).urgency(Urgency.Low)
						.state(State.Draft.meaning).build(),
				TicketDto.builder().id(3L).name("Gello")
						.desiredResolutionDate(new Date(System.currentTimeMillis() + 2000)).urgency(Urgency.Critical)
						.state(State.Canceled.meaning).build(),
				TicketDto.builder().id(4L).name("Dello")
						.desiredResolutionDate(new Date(System.currentTimeMillis() + 3000)).urgency(Urgency.Average)
						.state(State.Approved.meaning).build());

		when(converter.toDto(anyList())).thenReturn(expected);

		List<TicketDto> actual = ticketService.sort(Column.ID, Sort.ASC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortIdDesc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(TicketDto.builder().id(1L).build(), TicketDto.builder().id(2L).build(),
				TicketDto.builder().id(3L).build(), TicketDto.builder().id(4L).build());

		List<TicketDto> expected = List.of(TicketDto.builder().id(4L).build(), TicketDto.builder().id(3L).build(),
				TicketDto.builder().id(2L).build(), TicketDto.builder().id(1L).build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.ID, Sort.DESC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortNameAsc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(TicketDto.builder().name("Zero").build(),
				TicketDto.builder().name("Alisa").build(), TicketDto.builder().name("Dunno").build(),
				TicketDto.builder().name("Mamba").build());

		List<TicketDto> expected = List.of(TicketDto.builder().name("Alisa").build(),
				TicketDto.builder().name("Dunno").build(), TicketDto.builder().name("Mamba").build(),
				TicketDto.builder().name("Zero").build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.NAME, Sort.ASC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortNameDesc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(TicketDto.builder().name("Zero").build(),
				TicketDto.builder().name("Alisa").build(), TicketDto.builder().name("Dunno").build(),
				TicketDto.builder().name("Mamba").build());

		List<TicketDto> expected = List.of(TicketDto.builder().name("Zero").build(),
				TicketDto.builder().name("Mamba").build(), TicketDto.builder().name("Dunno").build(),
				TicketDto.builder().name("Alisa").build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.NAME, Sort.DESC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortDateAsc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis())).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 100500)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 2000)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 3000)).build());
		List<TicketDto> expected = List.of(
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis())).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 2000)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 3000)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 100500)).build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.DATE, Sort.ASC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortDateDesc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis())).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 100500)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 2000)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 3000)).build());
		List<TicketDto> expected = List.of(
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 100500)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 3000)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis() + 2000)).build(),
				TicketDto.builder().desiredResolutionDate(new Date(System.currentTimeMillis())).build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.DATE, Sort.DESC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortUrgencyAsc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(TicketDto.builder().urgency(Urgency.High).build(),
				TicketDto.builder().urgency(Urgency.Low).build(), TicketDto.builder().urgency(Urgency.Critical).build(),
				TicketDto.builder().urgency(Urgency.Average).build());
		List<TicketDto> expected = List.of(TicketDto.builder().urgency(Urgency.Critical).build(),
				TicketDto.builder().urgency(Urgency.High).build(), TicketDto.builder().urgency(Urgency.Average).build(),
				TicketDto.builder().urgency(Urgency.Low).build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.URGENCY, Sort.ASC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortUrgencyDesc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(TicketDto.builder().urgency(Urgency.High).build(),
				TicketDto.builder().urgency(Urgency.Low).build(), TicketDto.builder().urgency(Urgency.Critical).build(),
				TicketDto.builder().urgency(Urgency.Average).build());
		List<TicketDto> expected = List.of(TicketDto.builder().urgency(Urgency.Low).build(),
				TicketDto.builder().urgency(Urgency.Average).build(), TicketDto.builder().urgency(Urgency.High).build(),
				TicketDto.builder().urgency(Urgency.Critical).build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.URGENCY, Sort.DESC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortStatusAsc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(TicketDto.builder().state(State.Canceled.meaning).build(),
				TicketDto.builder().state(State.Declined.meaning).build(),
				TicketDto.builder().state(State.Draft.meaning).build(),
				TicketDto.builder().state(State.In_Progress.meaning).build(),
				TicketDto.builder().state(State.Approved.meaning).build());
		List<TicketDto> expected = List.of(TicketDto.builder().state(State.Approved.meaning).build(),
				TicketDto.builder().state(State.Canceled.meaning).build(),
				TicketDto.builder().state(State.Declined.meaning).build(),
				TicketDto.builder().state(State.Draft.meaning).build(),
				TicketDto.builder().state(State.In_Progress.meaning).build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.STATUS, Sort.ASC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}

	@Test
	void sortStatusDesc() {
		User user = User.builder().role(Role.Employee).build();
		List<TicketDto> ticketDtoList = List.of(TicketDto.builder().state(State.Canceled.meaning).build(),
				TicketDto.builder().state(State.Declined.meaning).build(),
				TicketDto.builder().state(State.Draft.meaning).build(),
				TicketDto.builder().state(State.In_Progress.meaning).build(),
				TicketDto.builder().state(State.Approved.meaning).build());
		List<TicketDto> expected = List.of(TicketDto.builder().state(State.In_Progress.meaning).build(),
				TicketDto.builder().state(State.Draft.meaning).build(),
				TicketDto.builder().state(State.Declined.meaning).build(),
				TicketDto.builder().state(State.Canceled.meaning).build(),
				TicketDto.builder().state(State.Approved.meaning).build());

		when(converter.toDto(anyList())).thenReturn(ticketDtoList);

		List<TicketDto> actual = ticketService.sort(Column.STATUS, Sort.DESC, user, Scope.ALL, 1);

		assertEquals(expected, actual);
	}
}
