package com.epam.ilyankov.helpdesk.repository.api;

import com.epam.ilyankov.helpdesk.domain.Feedback;

import java.util.Optional;

public interface FeedbackRepository {

    Optional<Feedback> getByTicketId(Long ticketId);
}
