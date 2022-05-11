package com.epam.ilyankov.helpdesk.service.api;

import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.domain.user.User;
import com.epam.ilyankov.helpdesk.dto.CommentDto;

import java.util.List;

public interface CommentService {

    void save(CommentDto commentDto);

    List<CommentDto> getByTicketId(Ticket ticket, User user);

    List<CommentDto> getCertainQuantityByTicketId(Ticket ticket, Integer page, User user);
}
