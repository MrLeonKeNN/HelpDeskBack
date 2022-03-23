package com.ilyankov.helpdesk.converter.api;

import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.dto.TicketDto;

public interface EditConverter {

    void edit(Ticket ticket, TicketDto ticketDto);
}
