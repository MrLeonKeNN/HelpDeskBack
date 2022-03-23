package com.ilyankov.helpdesk.converter.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ilyankov.helpdesk.converter.api.Converter;
import com.ilyankov.helpdesk.converter.api.EditConverter;
import com.ilyankov.helpdesk.domain.Attachment;
import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.domain.Feedback;
import com.ilyankov.helpdesk.domain.ticket.Action;
import com.ilyankov.helpdesk.domain.ticket.State;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.Role;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.TicketDto;
import com.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.ilyankov.helpdesk.repository.api.FeedbackRepository;
import com.ilyankov.helpdesk.service.api.CategoryService;
import com.ilyankov.helpdesk.service.api.UserService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TicketConverter implements Converter<Ticket, TicketDto>, EditConverter {

	private static final String GET_FIRST_NAME_CONST = "First";
	private static final String GET_LAST_NAME_CONST = "Last";

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

		return Ticket.builder().name(ticketDto.getName()).description(ticketDto.getDescription())
				.createdOn(new Timestamp(System.currentTimeMillis())).desiredResolutionDate(timestamp).owner(user)
				.state(State.valueOf(ticketDto.getState())).category(category).urgency(ticketDto.getUrgency()).build();
	}

	@Override
	public TicketDto toDto(Ticket ticket) {
		User user = userService.getCurrentUser();
		List<String> attachmentName = ticket.getAttachments().stream().map(Attachment::getName)
				.collect(Collectors.toList());

		return TicketDto.builder().id(ticket.getId()).name(ticket.getName()).description(ticket.getDescription())
				.desiredResolutionDate(ticket.getDesiredResolutionDate()).urgency(ticket.getUrgency())
				.state(ticket.getState().meaning).createOn(ticket.getCreatedOn().toString())
				.ownerFirstName(ticket.getOwner().getFirsName()).ownerLastName(ticket.getOwner().getLastName())
				.approverFirstName(getApproverName(ticket, GET_FIRST_NAME_CONST))
				.approverLastName(getApproverName(ticket, GET_LAST_NAME_CONST))
				.assignerFirstName(getAssignerName(ticket, GET_FIRST_NAME_CONST))
				.assignerLastName(getAssignerName(ticket, GET_LAST_NAME_CONST))
				.category(ticket.getCategory().getName())
				.actions(getActions(ticket, user)).attachmentName(attachmentName).build();
	}

	@Override
	public void edit(Ticket ticket, TicketDto ticketDto) {
		Category category = categoryService.getByName(ticketDto.getCategory())
				.orElseThrow(() -> new EntityNotFoundException("Category not found"));
		Timestamp timestamp = null;
		if (Objects.nonNull(ticketDto.getDesiredResolutionDate())) {
			timestamp = new Timestamp(ticketDto.getDesiredResolutionDate().getTime());
		}

		ticket.setCategory(category);
		ticket.setName(ticketDto.getName());
		ticket.setDescription(ticketDto.getDescription());
		ticket.setUrgency(ticketDto.getUrgency());
		ticket.setDesiredResolutionDate(timestamp);
	}

	private String getApproverName(Ticket ticket, String witchName) {
		if (witchName.equals(GET_FIRST_NAME_CONST)) {
			return ticket.getApprover() == null ? "" : ticket.getApprover().getFirsName();
		}
		if (witchName.equals(GET_LAST_NAME_CONST)) {
			return ticket.getApprover() == null ? "" : ticket.getApprover().getLastName();
		}
		return "";
	}

	private String getAssignerName(Ticket ticket, String witchName) {

		if (witchName.equals(GET_FIRST_NAME_CONST)) {
			return ticket.getAssignee() == null ? "" : ticket.getAssignee().getFirsName();
		}
		if (witchName.equals(GET_LAST_NAME_CONST)) {
			return ticket.getAssignee() == null ? "" : ticket.getAssignee().getLastName();
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
		Optional<Feedback> feedback = Optional.empty();
		if (ticket.getState() == State.Done) {
			feedback = feedbackRepository.getByTicketId(ticket.getId());
		}

		if (ticket.getOwner().equals(user) && ticket.getState() == State.Draft || ticket.getState() == State.Declined) {
			return List.of(Action.Submit.meaning, Action.Cancel.meaning);
		}
		if (ticket.getOwner().equals(user) && ticket.getState() == State.Done) {
			if (feedback.isPresent()) {
				return List.of(Action.VIEW_FEEDBACK.meaning);
			}
			return List.of(Action.LEAVE_FEEDBACK.meaning, Action.VIEW_FEEDBACK.meaning);
		}

		return Collections.emptyList();
	}

	private List<String> getActionsManager(Ticket ticket, User user) {
		Optional<Feedback> feedback = Optional.empty();
		if (ticket.getState() == State.Done) {
			feedback = feedbackRepository.getByTicketId(ticket.getId());
		}

		if (ticket.getOwner().equals(user) && ticket.getState() == State.Draft
				|| ticket.getOwner().equals(user) && ticket.getState() == State.Declined) {
			return List.of(Action.Submit.meaning, Action.Cancel.meaning);
		}
		if (ticket.getState() == State.New && !ticket.getOwner().equals(user)) {
			return List.of(Action.Approve.meaning, Action.Decline.meaning, Action.Cancel.meaning);
		}
		if (ticket.getOwner().equals(user) && ticket.getState() == State.Done) {
			if (feedback.isPresent()) {
				return List.of(Action.VIEW_FEEDBACK.meaning);
			}
			return List.of(Action.LEAVE_FEEDBACK.meaning, Action.VIEW_FEEDBACK.meaning);
		}

		return Collections.emptyList();
	}

	private List<String> getActionsEngineer(Ticket ticket, User user) {
		if (ticket.getState() == State.Approved) {
			return List.of(Action.Assign_to_Me.meaning, Action.Cancel.meaning);
		}
		if (ticket.getState() == State.In_Progress) {
			return List.of(Action.Done.meaning);
		}
		if (ticket.getAssignee().equals(user) && ticket.getState() == State.Done) {
			return List.of(Action.VIEW_FEEDBACK.meaning);
		}

		return Collections.emptyList();
	}
}
