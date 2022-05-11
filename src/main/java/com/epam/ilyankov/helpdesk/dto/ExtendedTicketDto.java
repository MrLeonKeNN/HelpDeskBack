package com.epam.ilyankov.helpdesk.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedTicketDto {

	private Long countTicket;
	private Boolean canCreate;
	private List<TicketDto> tickets;
}
