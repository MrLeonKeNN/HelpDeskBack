package com.ilyankov.helpdesk.service.api;

import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.dto.CommentDto;

import java.util.List;

public interface CommentService {

    void save(CommentDto commentDto);

    List<CommentDto> getByTicketId(Ticket ticket, User user);

    List<CommentDto> getCertainQuantityByTicketId(Ticket ticket, Integer page, User user);
}
