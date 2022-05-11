package com.epam.ilyankov.helpdesk.repository.api;

import com.epam.ilyankov.helpdesk.domain.Comment;

import java.util.List;

public interface CommentRepository {

    List<Comment> getByTicketId(Long id);

    List<Comment> getByTicketIdPagination(Long ticketId, Integer firstResult, Integer masResult);
}
