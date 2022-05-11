package com.epam.ilyankov.helpdesk.service.facade.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.domain.Attachment;
import com.epam.ilyankov.helpdesk.domain.ticket.Ticket;
import com.epam.ilyankov.helpdesk.dto.AttachmentDto;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.TicketRepository;
import com.epam.ilyankov.helpdesk.service.api.AttachmentService;
import com.epam.ilyankov.helpdesk.service.api.HistoryService;
import com.epam.ilyankov.helpdesk.service.facade.api.AttachmentFacade;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AttachmentFacadeImpl implements AttachmentFacade {

	private final TicketRepository ticketRepository;
	private final AttachmentService attachmentService;
	private final HistoryService historyService;

	@Override
	@Transactional
	public void save(AttachmentDto attachmentDto) {
		Ticket ticket = ticketRepository.getById(attachmentDto.getTicketId())
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

		attachmentService.verify(attachmentDto);

		List<AttachmentDto> dtoList = attachmentDto.getFile().stream()
				.map(multipartFile -> AttachmentDto.builder().localFile(multipartFile).ticketId(ticket.getId()).build())
				.collect(Collectors.toList());

		dtoList.forEach(attachmentDtoStream -> {
			attachmentService.save(attachmentDtoStream);
			historyService.saveHistoryFileAttached(attachmentDto.getTicketId(),
					attachmentDtoStream.getLocalFile().getOriginalFilename());
		});
	}

	@Override
	public Attachment get(String name, Long ticketId) {
		return attachmentService.getByTicketIdAndName(name, ticketId);
	}

	@Override
	public void remove(String removeName, Long ticketId) {
		attachmentService.remove(removeName, ticketId);
		historyService.saveHistoryFileRemove(ticketId, removeName);
	}
}
