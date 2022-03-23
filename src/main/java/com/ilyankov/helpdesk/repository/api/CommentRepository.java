package com.ilyankov.helpdesk.repository.api;

import com.ilyankov.helpdesk.domain.Comment;

import java.util.List;

public interface CommentRepository {

    List<Comment> getByTicketId(Long id);

    List<Comment> getByTicketIdQuantity(Long ticketId, Integer firstResult, Integer masResult);
}
