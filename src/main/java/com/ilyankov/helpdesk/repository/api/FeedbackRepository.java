package com.ilyankov.helpdesk.repository.api;

import com.ilyankov.helpdesk.domain.Feedback;

import java.util.Optional;

public interface FeedbackRepository {

    Optional<Feedback> getByTicketId(Long ticketId);
}
