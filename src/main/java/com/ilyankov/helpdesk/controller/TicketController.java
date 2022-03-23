package com.ilyankov.helpdesk.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilyankov.helpdesk.dto.ExtendedTicketDto;
import com.ilyankov.helpdesk.dto.TicketDto;
import com.ilyankov.helpdesk.enums.Column;
import com.ilyankov.helpdesk.enums.Scope;
import com.ilyankov.helpdesk.enums.Sort;
import com.ilyankov.helpdesk.service.facade.api.TicketFacade;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tickets")
@AllArgsConstructor
public class TicketController {

	private final TicketFacade ticketFacade;

	@GetMapping("/all/{page}")
	public ResponseEntity<ExtendedTicketDto> getAll(@PathVariable Integer page) {
		return ResponseEntity.ok(ticketFacade.getAllByRole(page));
	}

	@GetMapping("/my/{page}")
	public ResponseEntity<ExtendedTicketDto> getMy(@PathVariable Integer page) {
		return ResponseEntity.ok(ticketFacade.getMyByRole(page));
	}

	@GetMapping("/sort/{column}/{methodSort}/{scopeSort}/{page}")
	public ResponseEntity<ExtendedTicketDto> getSort(@PathVariable Column column, @PathVariable Sort methodSort,
			@PathVariable Scope scopeSort, @PathVariable Integer page) {
		return ResponseEntity.ok(ticketFacade.sort(column, methodSort, scopeSort, page));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TicketDto> getById(@PathVariable Long id) {
		return ResponseEntity.ok(ticketFacade.get(id));
	}

	@PostMapping
	@PreAuthorize("hasRole('Employee') or hasRole('Manager')")
	public ResponseEntity<Long> save(@Valid @RequestBody TicketDto ticketDto) {
		return ResponseEntity.ok(ticketFacade.save(ticketDto));
	}

	@PutMapping
	@PreAuthorize("hasRole('Employee') or hasRole('Manager')")
	public ResponseEntity<Void> edit(@Valid @RequestBody TicketDto ticketDto) {
		ticketFacade.edit(ticketDto);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/approve")
	@PreAuthorize("hasRole('Manager')")
	public ResponseEntity<Void> approve(@PathVariable Long id) {
		ticketFacade.approve(id);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/decline")
	@PreAuthorize("hasRole('Manager')")
	public ResponseEntity<Void> decline(@PathVariable Long id) {
		ticketFacade.decline(id);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/submit")
	@PreAuthorize("hasRole('Manager') or hasRole('Employee')")
	public ResponseEntity<Void> submit(@PathVariable Long id) {
		ticketFacade.submit(id);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/cancel")
	public ResponseEntity<Void> cancel(@PathVariable Long id) {
		ticketFacade.cancel(id);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/assign")
	@PreAuthorize("hasRole('Engineer')")
	public ResponseEntity<Void> assign(@PathVariable Long id) {
		ticketFacade.assign(id);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/done")
	@PreAuthorize("hasRole('Engineer')")
	public ResponseEntity<Void> done(@PathVariable Long id) {
		ticketFacade.done(id);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/search/{page}/{scope}/{word}")
	public ResponseEntity<ExtendedTicketDto> search(@PathVariable Integer page, @PathVariable Scope scope,
			@PathVariable String word) {
		return ResponseEntity.ok(ticketFacade.search(page, scope, word));
	}
}
