package com.epam.ilyankov.helpdesk.service.api;

import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.FeedbackDto;

public interface FeedbackService {

    void save(FeedbackDto feedbackDto, Ticket ticket, User user);

    FeedbackDto getByTicketId(Long ticketId);
}
