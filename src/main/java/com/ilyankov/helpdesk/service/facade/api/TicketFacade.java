package com.ilyankov.helpdesk.service.facade.api;

import com.ilyankov.helpdesk.dto.ExtendedTicketDto;
import com.ilyankov.helpdesk.dto.TicketDto;
import com.ilyankov.helpdesk.enums.Column;
import com.ilyankov.helpdesk.enums.Scope;
import com.ilyankov.helpdesk.enums.Sort;

public interface TicketFacade {

	Long save(TicketDto ticketDto);

	ExtendedTicketDto getAllByRole(Integer page);

	ExtendedTicketDto getMyByRole(Integer page);

	ExtendedTicketDto sort(Column collum, Sort sort, Scope scope, Integer page);

	ExtendedTicketDto search(Integer page, Scope scope, String word);

	void edit(TicketDto ticketDto);

	TicketDto get(Long id);

	void approve(Long id);

	void decline(Long id);

	void submit(Long id);

	void cancel(Long id);

	void assign(Long id);

	void done(Long id);
}
