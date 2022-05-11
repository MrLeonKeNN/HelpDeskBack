package com.epam.ilyankov.helpdesk.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
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

import com.epam.ilyankov.helpdesk.domain.Attachment;
import com.epam.ilyankov.helpdesk.domain.Category;
import com.epam.ilyankov.helpdesk.domain.ticket.Action;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.ticket.Urgency;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.TicketDto;
import com.epam.ilyankov.helpdesk.repository.api.FeedbackRepository;
import com.epam.ilyankov.helpdesk.service.api.CategoryService;
import com.epam.ilyankov.helpdesk.service.api.UserService;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class TicketConverterTest {

	@Mock
	private CategoryService categoryService;
	@Mock
	private UserService userService;
	@Mock
	private FeedbackRepository feedbackRepository;

	@InjectMocks
	private TicketConverter converter;

	@Test
	void fromDto() {
		User user = User.builder().role(Role.Employee).id(1L).build();
		Category category = Category.builder().id(1L).build();
		TicketDto ticketDto = TicketDto.builder().name("ticket").description("desc").desiredResolutionDate(new Date())
				.state("Draft").category("category").urgency(Urgency.High).build();

		Ticket expected = Ticket.builder().name("ticket").description("desc")
				.createdOn(new Timestamp(System.currentTimeMillis()))
				.desiredResolutionDate(new Timestamp(ticketDto.getDesiredResolutionDate().getTime())).owner(user)
				.state(State.Draft).category(category).urgency(Urgency.High).build();;

		when(userService.getCurrentUser()).thenReturn(User.builder().id(1L).build());
		when(categoryService.getByName("category")).thenReturn(Optional.ofNullable(category));

		Ticket actual = converter.fromDto(ticketDto);

		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getOwner(), actual.getOwner());
		assertEquals(expected.getDesiredResolutionDate(), actual.getDesiredResolutionDate());
		assertEquals(expected.getState(), actual.getState());
		assertEquals(expected.getUrgency(), actual.getUrgency());
		assertEquals(expected.getCategory(), actual.getCategory());

		verify(userService, times(1)).getCurrentUser();
		verify(categoryService, times(1)).getByName("category");
	}

	@Test
	void toDto() {
		User user = User.builder().role(Role.Employee).id(1L).firsName("Alina").lastName("Verni").build();
		Category category = Category.builder().id(1L).name("category").build();
		Attachment attachment = Attachment.builder().name("Zero.txt").id(1L).build();
		Ticket ticket = Ticket.builder().name("ticket").description("desc")
				.createdOn(new Timestamp(System.currentTimeMillis()))
				.desiredResolutionDate(new Timestamp(System.currentTimeMillis())).owner(user).state(State.Draft)
				.category(category).urgency(Urgency.High).attachments(List.of(attachment)).build();
		TicketDto expected = TicketDto.builder().id(ticket.getId()).name("ticket").description("desc")
				.desiredResolutionDate(ticket.getDesiredResolutionDate()).urgency(Urgency.High)
				.state(State.Draft.meaning).createOn(ticket.getCreatedOn().toString()).ownerFirstName("Alina")
				.ownerLastName("Verni").approverFirstName("").approverLastName("").assignerFirstName("")
				.assignerLastName("").category("category")
				.actions(List.of(Action.Submit.meaning, Action.Cancel.meaning)).attachmentName(List.of("Zero.txt"))
				.build();

		when(userService.getCurrentUser()).thenReturn(user);

		TicketDto actual = converter.toDto(ticket);

		assertEquals(expected, actual);
		verify(userService, times(1)).getCurrentUser();

	}

	@Test
	void edit() {
		User user = User.builder().role(Role.Employee).id(1L).firsName("Alina").build();
		Category category = Category.builder().id(1L).name("category").build();
		Attachment attachment = Attachment.builder().name("Zero.txt").id(1L).build();

		Ticket ticket = Ticket.builder().name("ticket").description("desc")
				.createdOn(new Timestamp(System.currentTimeMillis()))
				.desiredResolutionDate(new Timestamp(System.currentTimeMillis())).owner(user).state(State.Draft)
				.category(category).urgency(Urgency.High).attachments(List.of(attachment)).build();
		TicketDto ticketDto = TicketDto.builder().id(ticket.getId()).name("ticket22").description("desc22")
				.desiredResolutionDate(ticket.getDesiredResolutionDate()).urgency(Urgency.Critical)
				.state(State.Draft.meaning).createOn(ticket.getCreatedOn().toString()).ownerFirstName("Alina")
				.approverFirstName("").assignerFirstName("").category("category")
				.actions(List.of(Action.Submit.meaning, Action.Cancel.meaning)).attachmentName(List.of("Zero.txt"))
				.build();
		Ticket expected = Ticket.builder().name("ticket22").description("desc22")
				.createdOn(new Timestamp(System.currentTimeMillis()))
				.desiredResolutionDate(new Timestamp(System.currentTimeMillis())).owner(user).state(State.Draft)
				.category(category).urgency(Urgency.Critical).attachments(List.of(attachment)).build();

		when(categoryService.getByName("category")).thenReturn(Optional.ofNullable(category));
		converter.edit(ticket, ticketDto);

		assertEquals(expected.getName(), ticket.getName());
		assertEquals(expected.getDescription(), ticket.getDescription());
		assertEquals(expected.getOwner(), ticket.getOwner());
		assertTrue(expected.getDesiredResolutionDate().getTime() - ticket.getDesiredResolutionDate().getTime() < 20);
		assertEquals(expected.getState(), ticket.getState());
		assertEquals(expected.getUrgency(), ticket.getUrgency());
		assertEquals(expected.getCategory(), ticket.getCategory());
	}
}