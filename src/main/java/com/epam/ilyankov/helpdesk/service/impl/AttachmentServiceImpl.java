package com.epam.ilyankov.helpdesk.service.impl;

import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.ilyankov.helpdesk.converter.impl.AttachmentConverter;
import com.epam.ilyankov.helpdesk.domain.Attachment;
import com.epam.ilyankov.helpdesk.dto.AttachmentDto;
import com.epam.ilyankov.helpdesk.exceptions.AttachmentValidException;
import com.epam.ilyankov.helpdesk.exceptions.EntityNotFoundException;
import com.epam.ilyankov.helpdesk.repository.api.AttachmentRepository;
import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;
import com.epam.ilyankov.helpdesk.service.api.AttachmentService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

	private final AttachmentRepository attachmentRepository;
	private final CrudRepository<Attachment> crudRepository;
	private final AttachmentConverter converter;

	@Override
	@Transactional
	public void save(AttachmentDto attachmentDto) {
		crudRepository.save(converter.fromDto(attachmentDto));
	}

	@Override
	@Transactional(readOnly = true)
	public Attachment getByTicketIdAndName(String name, Long ticketId) {
		return attachmentRepository.getByTicketIdAndName(ticketId, name)
				.orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
	}

	@Override
	@Transactional
	public void remove(String name, Long ticketId) {
		Attachment attachment = attachmentRepository.getByTicketIdAndName(ticketId, name)
				.orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
		attachmentRepository.remove(attachment);
	}

	@Override
	public void verify(AttachmentDto attachmentDto) {
		attachmentDto.getFile().forEach(
				multipartFile -> validateExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename())));
	}

	private void validateExtension(String extension) {
		final String[] pol = {"pdf", "png", "doc", "docx", "jpg", "jpeg"};
		long a = Arrays.stream(pol).filter(s -> s.equals(extension)).count();
		if (a != 1) {
			throw new AttachmentValidException("Wrong attachment format");
		}
	}
}
