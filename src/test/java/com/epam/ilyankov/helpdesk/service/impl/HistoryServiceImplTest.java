package com.epam.ilyankov.helpdesk.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.ilyankov.helpdesk.converter.impl.HistoryConverter;
import com.epam.ilyankov.helpdesk.domain.history.History;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.HistoryDto;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class HistoryServiceImplTest {

	@Mock
	private HistoryConverter converter;
	@Mock
	private CrudRepository<History> crudRepository;
	@InjectMocks
	private HistoryServiceImpl historyService;

	@Test
	void saveHistoryCreateTest() {
		Ticket ticket = Ticket.builder().description("description").build();
		User user = User.builder().firsName("Alisa").build();
		History history = History.builder().ticket(ticket).user(user).action("Ticket is created")
				.description("Ticket is created").build();
		HistoryDto historyDto = HistoryDto.builder().action("Ticket is created").description("Ticket is created")
				.ticketId(1L).build();

		when(converter.fromDto(historyDto)).thenReturn(history);

		historyService.saveHistoryCreate(1L);

		verify(converter, times(1)).fromDto(historyDto);
		verify(crudRepository, times(1)).save(history);
	}

	@Test
	void saveHistoryEdit() {
		Ticket ticket = Ticket.builder().description("description").build();
		User user = User.builder().firsName("Alisa").build();
		History history = History.builder().ticket(ticket).user(user).action("Ticket is edited")
				.description("Ticket is edited").build();
		HistoryDto historyDto = HistoryDto.builder().action("Ticket is edited").description("Ticket is edited")
				.ticketId(1L).build();

		when(converter.fromDto(historyDto)).thenReturn(history);

		historyService.saveHistoryEdit(1L);

		verify(converter, times(1)).fromDto(historyDto);
		verify(crudRepository, times(1)).save(history);
	}

	@Test
	void saveHistoryStatus() {
		Ticket ticket = Ticket.builder().description("description").build();
		User user = User.builder().firsName("Alisa").build();
		History history = History.builder().ticket(ticket).user(user).action("Ticket Status is changed")
				.description("Ticket Status is changed from Approved to Canceled").build();
		HistoryDto historyDto = HistoryDto.builder().action("Ticket Status is changed")
				.description("Ticket Status is changed from Approved to Canceled").ticketId(1L).build();

		when(converter.fromDto(historyDto)).thenReturn(history);

		historyService.saveHistoryStatus(1L, State.Approved, State.Canceled);

		verify(converter, times(1)).fromDto(historyDto);
		verify(crudRepository, times(1)).save(history);
	}

	@Test
	void saveHistoryFileAttached() {
		Ticket ticket = Ticket.builder().description("description").build();
		User user = User.builder().firsName("Alisa").build();
		History history = History.builder().ticket(ticket).user(user).action("File is attached")
				.description("File is attached: One").build();
		HistoryDto historyDto = HistoryDto.builder().action("File is attached").description("File is attached: One")
				.ticketId(1L).build();

		when(converter.fromDto(historyDto)).thenReturn(history);

		historyService.saveHistoryFileAttached(1L, "One");

		verify(converter, times(1)).fromDto(historyDto);
		verify(crudRepository, times(1)).save(history);
	}

	@Test
	void saveHistoryFileRemove() {
		Ticket ticket = Ticket.builder().description("description").build();
		User user = User.builder().firsName("Alisa").build();
		History history = History.builder().ticket(ticket).user(user).action("File is removed")
				.description("File is removed: Two").build();
		HistoryDto historyDto = HistoryDto.builder().action("File is removed").description("File is removed: Two")
				.ticketId(1L).build();

		when(converter.fromDto(historyDto)).thenReturn(history);

		historyService.saveHistoryFileRemove(1L, "Two");

		verify(converter, times(1)).fromDto(historyDto);
		verify(crudRepository, times(1)).save(history);
	}

	@Test
	void get() {
		// List<History> histories = List.of(History.builder().id(1L).build());
		//
		// when(historyRepository.getByTicketId(1L)).thenReturn(histories);
		// when(historyConvertor.toDto(any())).thenReturn(new HistoryDto());
	}
}
