package com.epam.ilyankov.helpdesk.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.epam.ilyankov.helpdesk.dto.CommentDto;
import com.epam.ilyankov.helpdesk.service.api.CommentService;
import com.epam.ilyankov.helpdesk.service.facade.api.CommentFacade;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CommentController {

	private final CommentService commentService;
	private final CommentFacade commentFacade;

	@PostMapping("/tickets/comments")
	public ResponseEntity<Void> save(@Valid @RequestBody CommentDto commentDto) {
		commentService.save(commentDto);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/tickets/{ticketId}/comments")
	public ResponseEntity<List<CommentDto>> getAll(@PathVariable Long ticketId) {
		return ResponseEntity.ok(commentFacade.getByTicketId(ticketId));
	}

	@GetMapping("/tickets/{ticketId}/comments/{page}")
	public ResponseEntity<List<CommentDto>> getPaginationResult(@PathVariable Long ticketId,
			@PathVariable Integer page) {
		return ResponseEntity.ok(commentFacade.getCertainQuantityByTicketId(ticketId, page));
	}
}
