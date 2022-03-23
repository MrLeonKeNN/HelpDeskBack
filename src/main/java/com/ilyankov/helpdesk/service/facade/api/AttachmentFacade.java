package com.ilyankov.helpdesk.service.facade.api;

import com.ilyankov.helpdesk.domain.Attachment;
import com.ilyankov.helpdesk.dto.AttachmentDto;

public interface AttachmentFacade {

    void save(AttachmentDto attachmentDto);

    Attachment get(String name, Long ticketId);

    void remove(String removeName, Long ticketId);
}
