package com.ilyankov.helpdesk.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ilyankov.helpdesk.dto.HistoryDto;
import com.ilyankov.helpdesk.service.facade.api.HistoryFacade;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class HistoryController {

	private final HistoryFacade historyFacade;

	@GetMapping("/tickets/{ticketId}/history")
	public ResponseEntity<List<HistoryDto>> getAll(@PathVariable Long ticketId) {
		return ResponseEntity.ok(historyFacade.getByTicketId(ticketId));
	}

	@GetMapping("/tickets/{ticketId}/history/{page}")
	public ResponseEntity<List<HistoryDto>> getQuantityHistory(@PathVariable Long ticketId,
			@PathVariable Integer page) {
		return ResponseEntity.ok(historyFacade.getCertainQuantityByTicketId(ticketId, page));
	}
}
