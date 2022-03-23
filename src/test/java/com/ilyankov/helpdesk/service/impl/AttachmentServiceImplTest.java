package com.ilyankov.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ilyankov.helpdesk.converter.api.Converter;
import com.ilyankov.helpdesk.domain.Attachment;
import com.ilyankov.helpdesk.dto.AttachmentDto;
import com.ilyankov.helpdesk.repository.api.AttachmentRepository;
import com.ilyankov.helpdesk.repository.impl.CrudRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class AttachmentServiceImplTest {

	@Mock
	private AttachmentRepository attachmentRepository;
	@Mock
	private CrudRepository<Attachment> crudRepository;
	@Mock
	private Converter<Attachment, AttachmentDto> converter;

	@InjectMocks
	private AttachmentServiceImpl attachmentService;

	@Test
	void save() {
		Attachment expected = Attachment.builder().id(1L).name("123").build();

		when(attachmentRepository.getByTicketIdAndName(1L, "123")).thenReturn(Optional.ofNullable(expected));

		Attachment actual = attachmentService.getByTicketIdAndName("123", 1L);

		assertEquals(expected, actual);
		verify(attachmentRepository, times(1)).getByTicketIdAndName(1L, "123");
	}

	@Test
	void remove() {
		Attachment expected = Attachment.builder().id(1L).name("123").build();

		when(attachmentRepository.getByTicketIdAndName(1L, "123")).thenReturn(Optional.ofNullable(expected));

		attachmentService.remove("123", 1L);

		verify(attachmentRepository, times(1)).getByTicketIdAndName(1L, "123");
		verify(attachmentRepository, times(1)).remove(expected);
	}

	@Test
	void getNamesByTicketId() {
		List<Attachment> attachmentList = List.of();
	}

	@Test
	void validation() {
	}
}
