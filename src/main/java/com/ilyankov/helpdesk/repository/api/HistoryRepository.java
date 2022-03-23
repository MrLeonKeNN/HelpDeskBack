package com.ilyankov.helpdesk.repository.api;

import com.ilyankov.helpdesk.domain.history.History;

import java.util.List;

public interface HistoryRepository {

    List<History> getByTicketId(Long id);

    List<History> getByTicketIdQuantity(Long ticketId, Integer firstResult, Integer maxResult);
}
