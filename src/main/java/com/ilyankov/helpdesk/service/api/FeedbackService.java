package com.ilyankov.helpdesk.service.api;

import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.FeedbackDto;

public interface FeedbackService {

    void save(FeedbackDto feedbackDto, Ticket ticket, User user);

    FeedbackDto getByTicketId(Long ticketId);
}
