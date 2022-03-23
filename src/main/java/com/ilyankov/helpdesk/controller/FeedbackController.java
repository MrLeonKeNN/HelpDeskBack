package com.ilyankov.helpdesk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilyankov.helpdesk.dto.FeedbackDto;
import com.ilyankov.helpdesk.service.api.FeedbackService;
import com.ilyankov.helpdesk.service.facade.api.FeedbackFacade;

import lombok.AllArgsConstructor;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class FeedbackController {

	private final FeedbackFacade feedbackFacade;
	private final FeedbackService feedbackService;

	@PostMapping("/tickets/feedbacks")
	public ResponseEntity<Void> save(@Valid @RequestBody FeedbackDto feedbackDto) {
		feedbackFacade.save(feedbackDto);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/tickets/{ticketId}/feedbacks")
	public ResponseEntity<FeedbackDto> get(@PathVariable Long ticketId) {
		return ResponseEntity.ok(feedbackService.getByTicketId(ticketId));
	}
}
