package com.epam.ilyankov.helpdesk.repository.api;

import com.epam.ilyankov.helpdesk.domain.history.History;

import java.util.List;

public interface HistoryRepository {

    List<History> getByTicketId(Long id);

    List<History> getByTicketIdPagination(Long ticketId, Integer firstResult, Integer maxResult);
}
