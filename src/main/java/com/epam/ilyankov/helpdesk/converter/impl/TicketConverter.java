package com.epam.ilyankov.helpdesk.converter.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.epam.ilyankov.helpdesk.converter.api.Converter;
import com.epam.ilyankov.helpdesk.domain.Attachment;
import com.epam.ilyankov.helpdesk.domain.Category;
import com.epam.ilyankov.helpdesk.domain.Feedback;
import com.epam.ilyankov.helpdesk.domain.ticket.Action;
import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.Role;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.TicketDto;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.FeedbackRepository;
import com.epam.ilyankov.helpdesk.service.api.CategoryService;
import com.epam.ilyankov.helpdesk.service.api.UserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TicketConverter implements Converter<Ticket, TicketDto> {

	private static final String GET_FIRST_NAME_CONST = "First";
	private static final String GET_LAST_NAME_CONST = "Last";
	private static final String ASSIGNEE = "Assignee";
	private static final String APPROVER = "Approver";

	private final CategoryService categoryService;
	private final UserService userService;
	private final FeedbackRepository feedbackRepository;

	@Override
	public Ticket fromDto(TicketDto ticketDto) {
		User user = userService.getCurrentUser();
		Category category = categoryService.getByName(ticketDto.getCategory())
				.orElseThrow(() -> new EntityNotFoundException("Category not found"));
		Timestamp timestamp = null;
		if (Objects.nonNull(ticketDto.getDesiredResolutionDate())) {
			timestamp = new Timestamp(ticketDto.getDesiredResolutionDate().getTime());
		}

		return Ticket.builder()
				.name(ticketDto.getName())
				.description(ticketDto.getDescription())
				.createdOn(new Timestamp(System.currentTimeMillis()))
				.desiredResolutionDate(timestamp).owner(user)
				.state(State.valueOf(ticketDto.getState()))
				.category(category)
				.urgency(ticketDto.getUrgency())
				.build();
	}

	@Override
	public TicketDto toDto(Ticket ticket) {
		User user = userService.getCurrentUser();
		List<String> attachmentName = ticket.getAttachments().stream().map(Attachment::getName)
				.collect(Collectors.toList());

		return TicketDto.builder()
				.id(ticket.getId())
				.name(ticket.getName())
				.description(ticket.getDescription())
				.desiredResolutionDate(ticket.getDesiredResolutionDate())
				.urgency(ticket.getUrgency())
				.state(ticket.getState().meaning)
				.createOn(ticket.getCreatedOn().toString())
				.ownerFirstName(ticket.getOwner().getFirsName())
				.ownerLastName(ticket.getOwner().getLastName())
				.approverFirstName(getName(ticket, GET_FIRST_NAME_CONST, APPROVER))
				.approverLastName(getName(ticket, GET_LAST_NAME_CONST, APPROVER))
				.assignerFirstName(getName(ticket, GET_FIRST_NAME_CONST, ASSIGNEE))
				.assignerLastName(getName(ticket, GET_LAST_NAME_CONST, ASSIGNEE))
				.category(ticket.getCategory().getName())
				.actions(getActions(ticket, user))
				.attachmentName(attachmentName)
				.build();
	}

	public void edit(Ticket ticket, TicketDto ticketDto) {
		Category category = categoryService.getByName(ticketDto.getCategory())
				.orElseThrow(() -> new EntityNotFoundException("Category not found"));
		Timestamp timestamp = ticket.getDesiredResolutionDate() != null
				? new Timestamp(ticket.getDesiredResolutionDate().getTime())
				: null;

		ticket.setCategory(category);
		ticket.setName(ticketDto.getName());
		ticket.setDescription(ticketDto.getDescription());
		ticket.setUrgency(ticketDto.getUrgency());
		ticket.setDesiredResolutionDate(timestamp);
	}

	private String getName(Ticket ticket, String witchName, String userRole) {
		if (witchName.equals(GET_FIRST_NAME_CONST)) {
			if (userRole.equals(ASSIGNEE)) {
				return ticket.getAssignee() == null ? "" : ticket.getAssignee().getFirsName();
			}
			if (userRole.equals(APPROVER)) {
				return ticket.getApprover() == null ? "" : ticket.getApprover().getFirsName();
			}
		}
		if (witchName.equals(GET_LAST_NAME_CONST)) {
			if (userRole.equals(ASSIGNEE)) {
				return ticket.getAssignee() == null ? "" : ticket.getAssignee().getLastName();
			}
			if (userRole.equals(APPROVER)) {
				return ticket.getApprover() == null ? "" : ticket.getApprover().getLastName();
			}
		}
		return "";
	}

	private List<String> getActions(Ticket ticket, User user) {
		Map<Role, Supplier<List<String>>> strategyMap = Map.of(Role.Employee, () -> getActionsEmployee(ticket, user),
				Role.Manager, () -> getActionsManager(ticket, user), Role.Engineer,
				() -> getActionsEngineer(ticket, user));

		return Optional.ofNullable(strategyMap.get(user.getRole())).orElse(Collections::emptyList).get();
	}

	private List<String> getActionsEmployee(Ticket ticket, User user) {
		State ticketState = ticket.getState();
		User ticketOwner = ticket.getOwner();
		Optional<Feedback> feedback = Optional.empty();

		if (ticketState == State.Done) {
			feedback = feedbackRepository.getByTicketId(ticket.getId());
		}

		if (ticketOwner.equals(user) && ticketState == State.Draft || ticketState == State.Declined) {
			return List.of(Action.Submit.meaning, Action.Cancel.meaning);
		}
		if (ticketOwner.equals(user) && ticketState == State.Done) {
			if (feedback.isPresent()) {
				return List.of(Action.VIEW_FEEDBACK.meaning);
			}
			return List.of(Action.LEAVE_FEEDBACK.meaning, Action.VIEW_FEEDBACK.meaning);
		}

		return Collections.emptyList();
	}

	private List<String> getActionsManager(Ticket ticket, User user) {
		State ticketState = ticket.getState();
		User ticketOwner = ticket.getOwner();
		Optional<Feedback> feedback = Optional.empty();

		if (ticketState == State.Done) {
			feedback = feedbackRepository.getByTicketId(ticket.getId());
		}

		if (ticketOwner.equals(user) && ticketState == State.Draft
				|| ticketOwner.equals(user) && ticketState == State.Declined) {
			return List.of(Action.Submit.meaning, Action.Cancel.meaning);
		}
		if (ticketState == State.New && !ticketOwner.equals(user)) {
			return List.of(Action.Approve.meaning, Action.Decline.meaning, Action.Cancel.meaning);
		}
		if (ticketOwner.equals(user) && ticketState == State.Done) {
			if (feedback.isPresent()) {
				return List.of(Action.VIEW_FEEDBACK.meaning);
			}
			return List.of(Action.LEAVE_FEEDBACK.meaning, Action.VIEW_FEEDBACK.meaning);
		}

		return Collections.emptyList();
	}

	private List<String> getActionsEngineer(Ticket ticket, User user) {
		State ticketState = ticket.getState();

		if (ticketState == State.Approved) {
			return List.of(Action.Assign_to_Me.meaning, Action.Cancel.meaning);
		}
		if (ticketState == State.In_Progress) {
			return List.of(Action.Done.meaning);
		}
		if (ticket.getAssignee().equals(user) && ticketState == State.Done) {
			return List.of(Action.VIEW_FEEDBACK.meaning);
		}

		return Collections.emptyList();
	}
}
