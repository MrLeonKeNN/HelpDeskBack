package com.ilyankov.helpdesk.converter.impl;

import java.io.IOException;
import java.sql.Blob;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import com.ilyankov.helpdesk.converter.api.Converter;
import com.ilyankov.helpdesk.domain.Attachment;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.dto.AttachmentDto;
import com.ilyankov.helpdesk.exceptions.AttachmentValidException;
import com.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.ilyankov.helpdesk.repository.api.TicketRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AttachmentConvertor implements Converter<Attachment, AttachmentDto> {

	private final SessionFactory sessionFactory;
	private final TicketRepository ticketRepository;

	@Override
	public Attachment fromDto(AttachmentDto attachmentDto) {
		Ticket ticket = ticketRepository.getById(attachmentDto.getTicketId())
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		try {
			Blob blob = sessionFactory.getCurrentSession().getLobHelper()
					.createBlob(attachmentDto.getLocalFile().getInputStream(), 5000000);

			return Attachment.builder().blob(blob).ticket(ticket)
					.name(attachmentDto.getLocalFile().getOriginalFilename()).build();
		} catch (IOException e) {
			throw new AttachmentValidException();
		}
	}

	@Override
	public AttachmentDto toDto(Attachment attachment) {
		return new AttachmentDto();
	}
}
