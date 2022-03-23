package com.ilyankov.helpdesk.service.api;

import com.ilyankov.helpdesk.domain.Attachment;
import com.ilyankov.helpdesk.dto.AttachmentDto;

public interface AttachmentService {

	void save(AttachmentDto attachmentDto);

	Attachment getByTicketIdAndName(String name, Long ticketId);

	void remove(String name, Long ticketId);

	void verify(AttachmentDto attachmentDto);
}
