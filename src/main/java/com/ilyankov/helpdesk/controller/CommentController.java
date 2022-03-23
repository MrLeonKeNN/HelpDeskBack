package com.ilyankov.helpdesk.controller;

import java.util.List;

import com.ilyankov.helpdesk.service.facade.api.CommentFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilyankov.helpdesk.dto.CommentDto;
import com.ilyankov.helpdesk.service.api.CommentService;

import lombok.AllArgsConstructor;

import javax.validation.Valid;

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
	public ResponseEntity<List<CommentDto>> getQuantityComment(@PathVariable Long ticketId,
			@PathVariable Integer page) {
		return ResponseEntity.ok(commentFacade.getCertainQuantityByTicketId(ticketId, page));
	}
}
