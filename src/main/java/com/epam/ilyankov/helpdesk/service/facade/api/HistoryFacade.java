package com.epam.ilyankov.helpdesk.service.facade.api;

import java.util.List;

import com.epam.ilyankov.helpdesk.dto.HistoryDto;

public interface HistoryFacade {

	List<HistoryDto> getByTicketId(Long ticketId);

	List<HistoryDto> getCertainQuantityByTicketId(Long ticketId, Integer page);
}
