package com.epam.ilyankov.helpdesk.controller;

import java.sql.SQLException;

import javax.activation.MimetypesFileTypeMap;
import javax.validation.Valid;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.ilyankov.helpdesk.domain.Attachment;
import com.epam.ilyankov.helpdesk.dto.AttachmentDto;
import com.epam.ilyankov.helpdesk.service.facade.api.AttachmentFacade;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AttachmentController {

	private final AttachmentFacade attachmentFacade;

	@PostMapping("/tickets/attachments")
	public ResponseEntity<Void> save(@ModelAttribute @Valid AttachmentDto attachmentDto) {
		attachmentFacade.save(attachmentDto);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/tickets/{ticketId}/attachments/{name}")
	public ResponseEntity<Void> remove(@PathVariable String name, @PathVariable Long ticketId) {
		attachmentFacade.remove(name, ticketId);

		return ResponseEntity.ok().build();
	}

	@GetMapping("tickets/{ticketId}/attachments/{name}")
	public ResponseEntity<InputStreamResource> get(@PathVariable String name, @PathVariable Long ticketId)
			throws SQLException {
		Attachment attachment = attachmentFacade.get(name, ticketId);
		String mimetype = new MimetypesFileTypeMap().getContentType(attachment.getName());

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimetype))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						String.format("attachment; filename =\" %s \"", attachment.getName()))
				.body(new InputStreamResource(attachment.getBlob().getBinaryStream()));
	}
}
