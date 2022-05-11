package com.epam.ilyankov.helpdesk.service.api;

import com.epam.ilyankov.helpdesk.domain.ticket.State;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.HistoryDto;

import java.util.List;

public interface HistoryService {

    void saveHistoryCreate(Long ticketId);

    void saveHistoryEdit(Long ticketId);

    void saveHistoryStatus(Long ticketId, State firstState, State lastState);

    void saveHistoryFileAttached(Long ticketId, String nameAttachment);

    void saveHistoryFileRemove(Long ticketId, String removeName);

    List<HistoryDto> getByTicketId(Ticket ticket, User user);

    List<HistoryDto> getCertainQuantityByTicketId(Ticket ticket, User user, Integer page);
}
