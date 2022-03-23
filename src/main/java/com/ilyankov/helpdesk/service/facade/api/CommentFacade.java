package com.ilyankov.helpdesk.service.facade.api;

import com.ilyankov.helpdesk.dto.CommentDto;

import java.util.List;

public interface CommentFacade {

	List<CommentDto> getCertainQuantityByTicketId(Long ticketId, Integer page);

	List<CommentDto> getByTicketId(Long ticketID);
}
