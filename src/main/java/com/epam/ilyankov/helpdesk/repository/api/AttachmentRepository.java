package com.epam.ilyankov.helpdesk.repository.api;

import com.epam.ilyankov.helpdesk.domain.Attachment;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository {

    Optional<Attachment> getByTicketIdAndName(Long id, String name);

    List<Attachment> getNamesByTicketId(Long id);

    void remove(Attachment attachment);
}
